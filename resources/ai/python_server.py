import os
import logging
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import AutoModel
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch

# 设置 Hugging Face 镜像（可选）
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)
logger.info("System loading...")

# 初始化 FastAPI 应用
app = FastAPI()
logger.info("FastAPI loaded.")

# 设备选择
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
logger.info(f"Device selected: {device}")

# 模型名称
embedding_model = AutoModel.from_pretrained("/home/app/transformers_modules/jinaai/jina-embeddings-v3", local_files_only=True, trust_remote_code=True).to(device)
logger.info("Embedding Model loaded.")


class SplitTextRequest(BaseModel):
    text: str
    chunk_size: int = 100
    chunk_overlap: int = 0

class EmbeddingsRequest(BaseModel):
    input: list


@app.post("/embeddings")
async def get_embeddings(request: EmbeddingsRequest):
    """处理文本嵌入请求"""
    try:
        texts = request.input

        # 为每个文本生成嵌入向量
        embeddings = embedding_model.encode(texts, task="text-matching", 1024)

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
    uvicorn.run(app, host="0.0.0.0", port=50005)
    logger.info("Server shutdown.")
