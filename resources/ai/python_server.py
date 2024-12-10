import os
import logging
from flask import Flask, request, jsonify
from transformers import AutoModel, AutoModelForCausalLM, AutoTokenizer
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch

# 设置 Hugging Face 镜像
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

# 初始化 Flask 应用
app = Flask(__name__)

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


@app.route('/chat', methods=['POST'])
def chat():
    """处理聊天请求"""
    try:
        data = request.json
        messages = data.get('messages', [])

        # 将 messages 转换为模型所需的输入格式
        prompt = "\n".join([f"{msg.get('role', '')}: {msg.get('content', '')}" for msg in messages])

        # 生成响应
        inputs = tokenizer(prompt, return_tensors="pt").to(device)
        outputs = chat_model.generate(**inputs, max_length=512)
        response_text = tokenizer.decode(outputs[0], skip_special_tokens=True)

        # 返回与 OpenAI 一致的响应格式
        return jsonify({
            "choices": [{
                "message": {
                    "role": "assistant",
                    "content": response_text
                }
            }]
        })
    except Exception as e:
        logger.error(f"Error in chat endpoint: {e}")
        return jsonify({"error": str(e)}), 500



if __name__ == '__main__':
    logger.info("Starting server...")
    app.run(port=50004, host='0.0.0.0')
    logger.info("Server started.")