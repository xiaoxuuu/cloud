# hugging face 镜像
import os
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

from flask import Flask, request, jsonify
from transformers import AutoModel, AutoModelForCausalLM, AutoTokenizer
import re
from typing import List
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter
import torch

app = Flask(__name__)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

print("Loading chat model...")
tokenizer = AutoTokenizer.from_pretrained("Qwen/Qwen-1_8B-Chat", trust_remote_code=True)
# model = AutoModelForCausalLM.from_pretrained("Qwen/Qwen-1_8B-Chat", device_map="auto", trust_remote_code=True, bf16=True).eval()
chat_model = AutoModelForCausalLM.from_pretrained("Qwen/Qwen-1_8B-Chat", trust_remote_code=True).eval()
# 将模型移动到 GPU 上
chat_model.to(device)


print("Loading embedding model...")
embedding_model = AutoModel.from_pretrained("jinaai/jina-embeddings-v3", trust_remote_code=True)
# 将模型移动到 GPU 上
embedding_model.to(device)


@app.route('/chat', methods=['POST'])
def chat():
    data = request.json
    messages = data.get('messages', [])

    # 将 messages 转换为模型所需的输入格式
    prompt = ""
    for message in messages:
        role = message.get('role', '')
        content = message.get('content', '')
        prompt += f"{role}: {content}\n"

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


@app.route('/embeddings', methods=['POST'])
def get_embeddings():
    # 获取 JSON 请求中的文本数据
    data = request.get_json()
    texts = data['texts']
    truncate_dim = data['truncate_dim']
    # 为每个文本生成嵌入向量
    embeddings = embedding_model.encode(texts, task="text-matching", truncate_dim=truncate_dim)
    demo = []
    for i, text in enumerate(texts):
        demo.append({"index": i, "text": texts[i], "embedding": embeddings[i].tolist()})
    # 返回嵌入向量
    return jsonify(demo)


@app.route('/split', methods=['POST'])
def split_text_api():
    # 获取请求中的JSON数据
    data = request.get_json()
    
    # 检查是否包含'text'键
    if 'text' not in data:
        return jsonify({'error': 'Missing text parameter'}), 400
    
    # 获取文本
    text = data['text']
    chunk_size = data.get('chunk_size', 100)  # 默认值100
    chunk_overlap = data.get('chunk_overlap', 0)  # 默认值0
    
    # 调用分割函数
    text_splitter = ChineseRecursiveTextSplitter(
        # 指定在分割后的文本中是否保留分隔符，默认为 True
        keep_separator=True,
        # 指定分隔符列表中的元素是否为正则表达式，默认为 True
        is_separator_regex=True,
        # 每个块的最大大小，大小由 length_function 决定。
        chunk_size=chunk_size,
        # 块之间的目标重叠。重叠的块有助于减轻在块之间划分上下文时信息的丢失。
        chunk_overlap=chunk_overlap
        # length_function：确定块大小的函数，默认为 len，即按字符数计算。
    )
    split_results = text_splitter.split_text(text)
    
    # 返回结果
    return jsonify(split_results)


if __name__ == '__main__':
    print("Loading server...")
    app.run(port=50004, host='0.0.0.0')
    print("Done")