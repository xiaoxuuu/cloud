#!/bin/bash

sh stop.sh

# 找到 Conda 的 activate 脚本的路径
ACTIVATE_SCRIPT="$HOME/anaconda3/bin/activate"

# 激活 Conda 环境
$ACTIVATE_SCRIPT ai

# 在后台运行 Python 脚本
python3 python_server.py >> logs.txt 2>&1 &

tail -f logs.txt