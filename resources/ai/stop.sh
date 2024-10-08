#!/bin/bash

# 查找 run_model.py 的进程 ID
PID=$(pgrep -f run_model.py)

# 检查是否找到了PID
if [ -z "$PID" ]; then
    echo "没有找到运行中的 run_model.py 进程。"
else
    echo "找到 run_model.py 进程，PID为：$PID"

    # 发送 SIGTERM 信号来优雅地停止进程
    kill $PID

    # 检查进程是否被成功停止
    if [ $? -eq 0 ]; then
        echo "run_model.py 进程已被停止。"
    else
        echo "停止 run_model.py 进程失败。"
    fi
fi