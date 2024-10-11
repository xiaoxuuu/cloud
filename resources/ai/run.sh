#!/bin/bash

sh stop.sh

# 找到 Conda 的 activate 脚本的路径
ACTIVATE_SCRIPT="$HOME/anaconda3/bin/activate"

# 激活 Conda 环境
$ACTIVATE_SCRIPT ai

# 检查 Conda 是否成功激活
if [ $? -ne 0 ]; then
    echo "ai 环境切换失败，请手动切换：conda activate ai"
    sh stop.sh
    exit 1
fi

# 在后台运行 Python 脚本
python3 python_server.py >> logs.txt 2>&1 &

tail -f logs.txt