#!/bin/bash

# TODO 我发现 Bark 的推送通知是可以使用 device_keys 参数实现一次发送给多个设备，这样我就无需再 url 中传递 device_key
# TODO 我已经去掉了 URL 中的 device_key，增加了 json 数组 device_keys
# TODO 请调整代码，解包 send_bark_notification 方法，调用一次发送即可，不要使用 for 循环调用

# 授权: chmod +x /usr/local/bin/bark_login_notify.sh
# 配置文件: /etc/pam.d/ 下的 su login sshd other
# session     optional    pam_exec.so /usr/local/bin/bark_login_notify.sh

# 排除用户列表
EXCLUDE_USERS=(

)

# 重点通知用户列表
IMPORTANT_USERS=(
  "root"
)

# 白名单 IP，此名单中的 IP 登录静默推送消息，优先于 重点通知用户列表
WHITE_IP_LIST=(
  "127.0.0.1"
)

# Bark 推送 API Endpoint
BARK_KEYS=(
  "" # 请填写你的 Bark Key
)

# 日志文件
LOG_FILE="/var/log/login_notifications.log"
# 初始化一个 4 位随机数，打印在日志时间后面，用于识别这是本次登陆输出的日志，区分上下两次登陆日志
RANDOM_ID=$((1000 + RANDOM % 9000))

# 函数：日志输出
log_message() {
  local level="$1"             # 日志级别 (INFO, ERROR, WARN)
  local message="$2"           # 日志消息
  local replace_newline="$3"   # 是否替换换行符 (true/false)

  local timestamp=$(date +"%Y-%m-%d %H:%M:%S")
  local formatted_message="$message"

  if [ "$replace_newline" == "true" ]; then
    formatted_message=$(echo -e "$message" | tr '\n' ' ')
  fi

  echo "[$timestamp][$RANDOM_ID] $level: $formatted_message" >> "${LOG_FILE}"
}

# 函数：发送 Bark 推送
send_bark_notification() {
  local bark_key="$1"
  local payload="$2"

  timeout 5 curl -s -X POST \
    "https://api.day.app/push" \
    -H "Content-Type: application/json; charset=utf-8" \
    -d "$payload" > /dev/null 2>&1

  if [ $? -ne 0 ]; then
    log_message "ERROR"  "Failed to send Bark notification with key: ${bark_key}"
  fi
}


# 函数：获取公网 IP (使用 opendns 和 icanhazip，增加容错)
get_own_ip() {
  # 先尝试 opendns
  OWN_IP=$(dig +short myip.opendns.com @resolver1.opendns.com 2>/dev/null)
  if [[ -z "$OWN_IP" ]]; then
    # 如果 opendns 获取失败，尝试 icanhazip.com
    OWN_IP=$(curl -s icanhazip.com 2>/dev/null)
  fi

  if [[ -z "$OWN_IP" || "$OWN_IP" == "Unknown" ]]; then
    OWN_IP="Unknown"
    log_message "WARN " "Failed to determine own IP address."
    OWN_IP="" # 清空变量，阻止后续 IP 查询
  fi
  echo "$OWN_IP"
}


# 函数：查询 IP 地址归属地 (使用百度 IP API,带错误处理, 不使用 jq)
get_ip_location() {
  local ip="$1"
  local location=""

  if [[ -z "$ip" ]]; then
    log_message "WARN " "No IP address to query location for."
    location="Unknown"
    echo "$location"
    return
  fi


  local result=$(curl -s "https://opendata.baidu.com/api.php?query=${ip}&co=&resource_id=6006&oe=utf8" 2>/dev/null)

  if [[ -n "$result" ]]; then
    # 使用 grep 和 sed 解析 JSON (更脆弱，但避免了 jq 依赖)
    if echo "$result" | grep -q '"status":"0"'; then
      location=$(echo "$result" | grep -oP '"location":\s*"\K[^"]+' 2>/dev/null)
      # 如果匹配失败，location 为空, 需要提供默认值
      if [[ -z "$location" ]] ; then
        location="Unknown"
        log_message "WARN " "Failed to parse location from API response using grep/sed."
      fi
    else
      log_message "WARN "  "Failed to query IP location for ${ip} (BaiDu API error)."
    fi
  else
    log_message "WARN "  "Failed to query IP location for ${ip} (curl error)."
  fi

  if [[ -z "$location" ]]; then
    location="Unknown" # 接口调用失败，降级为 Unknown
  fi
  echo "$location"
}



# 获取登录信息
LOGIN_TIME=$(date +"%Y-%m-%d %H:%M:%S")
LOGIN_USER=${PAM_USER:-$(logname)} # 尝试使用 PAM_USER，否则使用 logname
LOGIN_IP=$(echo $PAM_RHOST | awk '{print $1}')
LOGIN_SERVICE=$(echo $PAM_SERVICE)
LOGIN_TYPE=$(echo $PAM_TYPE)

# 翻译 LOGIN_TYPE
case "$LOGIN_TYPE" in
  "open_session")
    LOGIN_TYPE_NAME="login"
    ;;
  "close_session")
    LOGIN_TYPE_NAME="logout"
    ;;
  *)
    LOGIN_TYPE_NAME="$LOGIN_TYPE" # 未知类型，保持原样
    ;;
esac

# 查询 DNS 获取自身 IP
OWN_IP=$(get_own_ip)

# 如果没有获取到 IP，则跳过 IP 地址查询
if [[ -n "$OWN_IP" ]]; then
  LOGIN_LOCATION=$(get_ip_location "${LOGIN_IP}")
else
  LOGIN_LOCATION="Unknown"
fi

PUSH_LEVEL="active"

# 确定是否为重点通知用户，优先于白名单
for user in "${IMPORTANT_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    PUSH_LEVEL="critical"
    break
  fi
done

# 白名单检查
IS_WHITELISTED=false
for ip in "${WHITE_IP_LIST[@]}"; do
  if [ "$LOGIN_IP" == "$ip" ]; then
    IS_WHITELISTED=true
    break
  fi
done

# 如果是白名单IP，则设置 PUSH_LEVEL 为 passive
if $IS_WHITELISTED; then
  PUSH_LEVEL="passive"
fi


# 构建消息标题和内容
# 使用 printf 构建 BODY，确保换行符被正确解释
BODY="IP: ${LOGIN_IP}\nLocation: ${LOGIN_LOCATION}\nTime: ${LOGIN_TIME}\nUser: ${LOGIN_USER}\nService: ${LOGIN_SERVICE}\nType: ${LOGIN_TYPE}"
TITLE="${OWN_IP} [${LOGIN_USER}] ${LOGIN_SERVICE} ${LOGIN_TYPE_NAME}"

# 构建 JSON Payload
PAYLOAD=$(cat <<EOF
{
  "title": "${TITLE}",
  "subtitle": "Loc: ${LOGIN_LOCATION}",
  "body": "${BODY}",
  "isArchive": 1,
  "sound": "glass",
  "group": "Server Login Push",
  "volume": 10,
  "level": "${PUSH_LEVEL}",
  "device_keys": [${DEVICE_KEYS}]
}
EOF
)

# 记录日志
log_message "INFO " "${TITLE}, ${BODY}" "true" # 日志打印 TITLE+BODY，替换换行符为空格


# 检查用户是否在排除列表中
for user in "${EXCLUDE_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    log_message "INFO " "User ${LOGIN_USER} is excluded, skipping notification."
    exit 0  # 如果在排除列表中，则退出脚本，不发送通知
  fi
done

# 发送 Bark 推送 (循环)
if [ -n "${BARK_KEYS[0]}" ]; then  # 确保 BARK_KEYS 不为空
  for BARK_KEY in "${BARK_KEYS[@]}"; do
    send_bark_notification "${BARK_KEY}" "${PAYLOAD}"
  done
else
  log_message "WARN " "BARK_KEYS is empty, no notification will be sent."
fi

exit 0