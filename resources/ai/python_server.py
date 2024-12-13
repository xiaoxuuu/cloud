import os
import logging
from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from transformers import AutoModel, AutoModelForCausalLM, AutoTokenizer, TextIteratorStreamer
from threading import Thread
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch
import time

# 设置 Hugging Face 镜像
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

# 初始化 FastAPI 应用
app = FastAPI()

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 设备选择
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
chat_model_name = "Qwen/Qwen-1_8B-Chat"

def load_models():
    """加载并初始化模型"""
    logger.info("Loading chat model...")
    tokenizer = AutoTokenizer.from_pretrained(chat_model_name, trust_remote_code=True)
    # 设置 chat_template
    tokenizer.chat_template = "{% for message in messages %}{{'<|im_start|>' + message['role'] + '\n' + message['content'] + '<|im_end|>' + '\n'}}{% endfor %}{% if add_generation_prompt %}{{ '<|im_start|>assistant\n' }}{% endif %}"
    chat_model = AutoModelForCausalLM.from_pretrained(chat_model_name, trust_remote_code=True).eval()
    chat_model.to(device)

    logger.info("Loading embedding model...")
    embedding_model = AutoModel.from_pretrained("jinaai/jina-embeddings-v3", trust_remote_code=True)
    embedding_model.to(device)

    return tokenizer, chat_model, embedding_model

# 加载模型
tokenizer, chat_model, embedding_model = load_models()

class ChatRequest(BaseModel):
    messages: list
    stream: bool = False

class EmbeddingsRequest(BaseModel):
    texts: list
    truncate_dim: int = 768

class SplitTextRequest(BaseModel):
    text: str
    chunk_size: int = 100
    chunk_overlap: int = 0

@app.post("/v1/completions")
async def completions(request: ChatRequest):
    # 请求参数支持 messages 参数，接收提问
    # 请求参数支持 stream 参数，控制是否以流式返回数据
    # 返回参数按照 openai api 格式
    try:
        messages = request.messages
        stream = request.stream
        
        # 使用 apply_chat_template 将消息转换为模型可以理解的格式 ([3](https://qwen.readthedocs.io/zh-cn/latest/inference/chat.html))
        text = tokenizer.apply_chat_template(
            messages,
            tokenize=False,
            add_generation_prompt=True,
        )
        model_inputs = tokenizer([text], return_tensors="pt").to(device)

        # 使用 generate 方法进行问答 ([3](https://qwen.readthedocs.io/zh-cn/latest/inference/chat.html))
        generated_ids = chat_model.generate(
            **model_inputs,
            max_new_tokens=512,
        )
        generated_ids = [
            output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
        ]

        response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

        if stream:
            # 流式返回
            async def generate_stream():
                streamer = TextIteratorStreamer(tokenizer, skip_prompt=True, skip_special_tokens=True)
                generation_kwargs = dict(
                    input_ids=model_inputs.input_ids,
                    max_new_tokens=512,
                    streamer=streamer
                )
                thread = Thread(target=chat_model.generate, kwargs=generation_kwargs)
                thread.start()
                
                start_time = int(time.time())
                first_token = True
                for output in streamer:
                    yield f"data: {{\"id\": \"cmpl-\", \"object\": \"chat.completion.chunk\", \"created\": {start_time}, \"model\": \"{chat_model_name}\", \"choices\": [{{\"index\": 0, \"delta\": {{\"role\": \"assistant\", \"content\": \"\" if first_token else output}}, \"finish_reason\": None}}]}}\n\n"
                    first_token = False
                
                # 结束标记
                yield f"data: {{\"id\": \"cmpl-\", \"object\": \"chat.completion.chunk\", \"created\": {start_time}, \"model\": \"{chat_model_name}\", \"choices\": [{{\"index\": 0, \"delta\": {{}}, \"finish_reason\": \"stop\", \"usage\": {{\"prompt_tokens\": {len(model_inputs.input_ids[0])}, \"completion_tokens\": 0, \"total_tokens\": 0}}}}]}}\n\n"
                yield "data: [DONE]\n\n"

            return StreamingResponse(generate_stream(), media_type="application/json")
        else:
            return {
                "choices": [{
                    "index": 0,
                    "message": {
                        "role": "assistant",
                        "content": response,
                    },
                    "finish_reason": "stop"
                }],
                "usage": {
                    "prompt_tokens": len(model_inputs.input_ids[0]),
                    "completion_tokens": len(generated_ids[0]),
                    "total_tokens": len(model_inputs.input_ids[0]) + len(generated_ids[0])
                }
            }
    except Exception as e:
        logger.error(f"Error in completions endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/embeddings")
async def get_embeddings(request: EmbeddingsRequest):
    """处理文本嵌入请求"""
    try:
        texts = request.texts
        truncate_dim = request.truncate_dim

        # 为每个文本生成嵌入向量
        embeddings = embedding_model.encode(texts, task="text-matching", truncate_dim=truncate_dim)

        # 返回嵌入向量
        return [{"index": i, "text": text, "embedding": embedding.tolist()}
                for i, (text, embedding) in enumerate(zip(texts, embeddings))]
    except Exception as e:
        logger.error(f"Error in embeddings endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/split")
async def split_text_api(request: SplitTextRequest):
    """处理文本分割请求"""
    try:
        text = request.text
        chunk_size = request.chunk_size
        chunk_overlap = request.chunk_overlap

        # 调用分割函数
        text_splitter = ChineseRecursiveTextSplitter(
            keep_separator=True,
            is_separator_regex=True,
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap
        )
        split_results = text_splitter.split_text(text)

        # 返回结果
        return split_results
    except Exception as e:
        logger.error(f"Error in split endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == '__main__':
    import uvicorn
    logger.info("Starting server...")
    uvicorn.run(app, host="0.0.0.0", port=50004)
    logger.info("Server started.")
