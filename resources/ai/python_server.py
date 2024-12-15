import os
import logging
from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from transformers import AutoModel, AutoModelForCausalLM, AutoTokenizer, TextIteratorStreamer
from threading import Thread
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch

# 设置 Hugging Face 镜像（可选）
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)
logger.info("System load...")

# 初始化 FastAPI 应用
app = FastAPI()
logger.info("FastAPI loaded.")

# 设备选择
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
logger.info("Device selected.")

# 模型名称
chat_model_path = "/home/app/transformers_modules/Qwen/Qwen2.5-32B-Instruct-AWQ"
embedding_model_path = "/home/app/transformers_modules/jinaai/jina-embeddings-v3"

def load_models():
    """加载并初始化模型"""
    logger.info("Loading chat model...")
    tokenizer = AutoTokenizer.from_pretrained(chat_model_path, local_files_only=True, trust_remote_code=True)
    # 设置 chat_template
    tokenizer.chat_template = "{% for message in messages %}{{'<|im_start|>' + message['role'] + '\n' + message['content'] + '<|im_end|>' + '\n'}}{% endfor %}{% if add_generation_prompt %}{{ '<|im_start|>assistant\n' }}{% endif %}"
    chat_model = AutoModelForCausalLM.from_pretrained(chat_model_path, local_files_only=True, trust_remote_code=True).to(device).eval()

    logger.info("Loading embedding model...")
    embedding_model = AutoModel.from_pretrained(embedding_model_path, local_files_only=True, trust_remote_code=True).to(device)

    return tokenizer, chat_model, embedding_model

logger.info("Model loaded.")
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
    """处理对话请求"""
    try:
        messages = request.messages
        stream = request.stream

        # 使用 apply_chat_template 将消息转换为模型可以理解的格式
        text = tokenizer.apply_chat_template(
            messages,
            tokenize=False,
            add_generation_prompt=True,
        )
        model_inputs = tokenizer([text], return_tensors="pt").to(device)

        if stream:
            # 流式返回
            async def generate_stream():
                streamer = TextIteratorStreamer(tokenizer, skip_prompt=True, skip_special_tokens=True)
                generation_kwargs = dict(
                    input_ids=model_inputs.input_ids,
                    max_new_tokens=512,
                    streamer=streamer,
                    pad_token_id=tokenizer.pad_token_id,
                    eos_token_id=tokenizer.eos_token_id,
                )
                thread = Thread(target=chat_model.generate, kwargs=generation_kwargs)
                thread.start()

                for output in streamer:
                    yield f"data: {output}\n\n"

                # 结束标记
                yield "data: [DONE]\n\n"

            return StreamingResponse(generate_stream(), media_type="text/event-stream")
        else:
            # 非流式返回
            generated_ids = chat_model.generate(
                **model_inputs,
                max_new_tokens=512,
                pad_token_id=tokenizer.pad_token_id,
                eos_token_id=tokenizer.eos_token_id,
            )
            generated_ids = [
                output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
            ]

            response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

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
                    "prompt_tokens": model_inputs.input_ids.shape[1],
                    "completion_tokens": generated_ids[0].shape[0],
                    "total_tokens": model_inputs.input_ids.shape[1] + generated_ids[0].shape[0]
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

        return split_results
    except Exception as e:
        logger.error(f"Error in split endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == '__main__':
    import uvicorn
    logger.info("Starting server...")
    uvicorn.run(app, host="0.0.0.0", port=50004)
    logger.info("Server shutdown.")
