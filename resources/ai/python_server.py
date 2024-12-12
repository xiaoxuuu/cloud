import os
import logging
from fastapi import FastAPI, HTTPException, Request
from pydantic import BaseModel
from transformers import AutoModel, AutoModelForCausalLM, AutoTokenizer
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch

# 设置 Hugging Face 镜像
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

# 初始化 FastAPI 应用
app = FastAPI()

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 设备选择
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

def load_models():
    """加载并初始化模型"""
    logger.info("Loading chat model...")
    tokenizer = AutoTokenizer.from_pretrained("Qwen/Qwen-1_8B-Chat", trust_remote_code=True)
    chat_model = AutoModelForCausalLM.from_pretrained("Qwen/Qwen-1_8B-Chat", trust_remote_code=True).eval()
    chat_model.to(device)

    logger.info("Loading embedding model...")
    embedding_model = AutoModel.from_pretrained("jinaai/jina-embeddings-v3", trust_remote_code=True)
    embedding_model.to(device)

    return tokenizer, chat_model, embedding_model

# 加载模型
tokenizer, chat_model, embedding_model = load_models()

class ChatRequest(BaseModel):
    messages: list

class EmbeddingsRequest(BaseModel):
    texts: list
    truncate_dim: int = 512

class SplitTextRequest(BaseModel):
    text: str
    chunk_size: int = 100
    chunk_overlap: int = 0

class CompletionRequest(BaseModel):
    prompt: str
    max_tokens: int = 512
    temperature: float = 1.0
    top_p: float = 1.0
    n: int = 1
    stop: list = None
    stream: bool = False

@app.post("/v1/completions")
async def completions(request: CompletionRequest):
    """处理 OpenAI 兼容的 completions 请求"""
    try:
        prompt = request.prompt
        max_tokens = request.max_tokens
        temperature = request.temperature
        top_p = request.top_p
        n = request.n
        stop = request.stop
        stream = request.stream

        # 如果 stream 为 True，需要支持流式响应（此处暂不支持流式）
        if stream:
            raise HTTPException(status_code=501, detail="Streaming is not supported yet.")

        # 生成响应
        inputs = tokenizer(prompt, return_tensors="pt").to(device)
        outputs = chat_model.generate(
            **inputs,
            max_length=max_tokens,
            temperature=temperature,
            top_p=top_p,
            num_return_sequences=n,
            do_sample=True,
            eos_token_id=tokenizer.eos_token_id,
            pad_token_id=tokenizer.pad_token_id,
            early_stopping=True
        )

        # 解码生成的文本
        completions = [tokenizer.decode(output, skip_special_tokens=True) for output in outputs]

        # 返回与 OpenAI 一致的响应格式
        return {
            "id": "cmpl-1234567890",  # 模拟 OpenAI 的 ID
            "object": "text_completion",
            "created": int(torch.tensor(0).item()),  # 模拟时间戳
            "model": "Qwen/Qwen-1_8B-Chat",
            "choices": [
                {
                    "text": completion,
                    "index": i,
                    "logprobs": None,
                    "finish_reason": "length" if len(completion) >= max_tokens else "stop"
                }
                for i, completion in enumerate(completions)
            ],
            "usage": {
                "prompt_tokens": len(tokenizer.encode(prompt)),
                "completion_tokens": sum(len(tokenizer.encode(completion)) for completion in completions),
                "total_tokens": len(tokenizer.encode(prompt)) + sum(len(tokenizer.encode(completion)) for completion in completions)
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
