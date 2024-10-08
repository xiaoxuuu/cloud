#!/bin/bash

# 找到 Conda 的 activate 脚本的路径
ACTIVATE_SCRIPT="$HOME/anaconda3/bin/activate"

# 激活 Conda 环境
source $ACTIVATE_SCRIPT ai

# 在后台运行 Python 脚本
python run.py &