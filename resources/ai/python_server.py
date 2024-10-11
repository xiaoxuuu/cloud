# hugging face 镜像
import os
os.environ['HF_ENDPOINT'] = 'https://hf-mirror.com'

from flask import Flask, request, jsonify
from transformers import AutoModel
import re
from typing import List
from chinese_recursive_text_splitter import ChineseRecursiveTextSplitter

app = Flask(__name__)

print("Loading model...")
model = AutoModel.from_pretrained("jinaai/jina-embeddings-v3", trust_remote_code=True)


@app.route('/embeddings', methods=['POST'])
def get_embeddings():
    # 获取 JSON 请求中的文本数据
    data = request.get_json()
    texts = data['texts']
    # 为每个文本生成嵌入向量
    embeddings = model.encode(texts, task="text-matching", truncate_dim=32)
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
    app.run(port=55555, host='0.0.0.0')
    print("Done")