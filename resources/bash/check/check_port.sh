#!/bin/bash

# 授权: chmod +x /usr/local/bin/check_port.sh
# 授权: chmod 644 /usr/local/bin/check_port.conf

# crontab -e
# */5 * * * * /usr/local/bin/check_port.sh 192.168.1.222 192.168.1.111 49000 "clickhouse"
# */5 * * * * /usr/local/bin/check_port.sh 192.168.1.222 192.168.1.222 49000 "clickhouse"

# 脚本接收三个参数：
#   $1: 本机 IP 地址 (ownIp)
#   $2: 检测 IP 地址 (check_ip)
#   $3: 检测端口号 (check_port)
#   $4: 检测服务名 (check_service)

# 加载配置文件
# 获取脚本自身的绝对路径
SCRIPT_PATH="$(realpath $0)"
SCRIPT_DIR="$(dirname "$SCRIPT_PATH")"
# 构建配置文件的完整路径
CONFIG_FILE="$SCRIPT_DIR/check_port.conf"
# 加载
if [ -f "$CONFIG_FILE" ]; then
  source "$CONFIG_FILE"
else
  echo "Error: check_port.conf not found in $SCRIPT_DIR. Exiting." >&2
  exit 1
fi

# Bark 推送 API Keys (将字符串转换为数组)
if [ -n "$BARK_KEYS" ]; then
  IFS=',' read -r -a BARK_KEYS_ARRAY <<< "$BARK_KEYS"
else
  BARK_KEYS_ARRAY=()
fi

# 函数：日志输出
log_message() {
  local level="$1"             # 日志级别 (INFO, ERROR, WARN)
  local message="$2"           # 日志消息
  local replace_newline="$3"   # 是否替换换行符 (true/false)

  if [ "$level" == "EMPTY" ]; then
    echo "" >> "${LOG_FILE}"  # 输出空行
    return                    # 退出函数
  fi

  local timestamp=$(date +"%Y-%m-%d %H:%M:%S")
  local formatted_message="$message"

  if [ "$replace_newline" == "true" ]; then
    formatted_message=$(echo -e "$message" | tr '\n' ' ')
  fi

  echo "[$timestamp] $level: $formatted_message" >> "${LOG_FILE}"
}

# 函数：Bark 推送
send_bark_notification() {
  local payload="$1"

  timeout 5 curl -s -X POST \
    "https://api.day.app/push" \
    -H "Content-Type: application/json; charset=utf-8" \
    -d "$payload" > /dev/null 2>&1

  if [ $? -ne 0 ]; then
    log_message "ERROR"  "Failed to send Bark notification"
  fi
}

# 检查参数数量
if [ $# -ne 4 ]; then
  echo "用法: $0 <本机 IP 地址> <检测 IP 地址> <检测端口号> <检测服务名>"
  log_message "WARN " "参数错误"
  exit 1
fi

ownIp="$1"
check_ip="$2"
check_port="$3"
check_service="$4"

# 构建 device_keys 的 JSON 数组
DEVICE_KEYS=$(
  IFS=','
  echo "["
  for key in "${BARK_KEYS_ARRAY[@]}"; do
    echo "\"$key\","
  done | sed '$s/,$//'  # remove trailing comma
  echo "]"
)

TITLE="${check_service} 服务离线"
BODY="服务位置: ${check_ip}:${check_port}\n$(date '+%Y-%m-%d %H:%M:%S')\nnc -z 超过 2s 未响应"

# json 字符串
PAYLOAD=$(cat <<EOF
{
  "title": "${TITLE}",
  "subtitle": "检测机器：${ownIp}",
  "body": "${BODY}",
  "isArchive": 1,
  "sound": "glass",
  "group": "Server Login Push",
  "volume": 10,
  "level": "critical",
  "device_keys": $DEVICE_KEYS
}
EOF
)

# 尝试连接指定IP地址和端口
timeout 2 nc -z "$check_ip" "$check_port"

# 判断连接是否成功
if [ $? -ne 0 ]; then
  # 连接失败，发送通知
  log_message "ERROR" "${TITLE}, ${BODY}" "true"
  log_message "EMPTY"
  # 发送 Bark 推送 (单次调用)
  if [ -n "${BARK_KEYS_ARRAY[0]}" ]; then  # 确保 BARK_KEYS 不为空
    send_bark_notification "${PAYLOAD}"
  else
    log_message "WARN " "BARK_KEYS is empty, no notification will be sent."
  fi
fi

log_message "INFO " "${check_service} ${check_ip}:${check_port} 服务在线"
exit 0
