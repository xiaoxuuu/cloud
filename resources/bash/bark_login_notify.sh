#!/bin/bash

# 授权：chmod +x /usr/local/bin/bark_login_notify.sh
# 配置文件：/etc/pam.d/ 下的 su login sshd
# session     optional    pam_exec.so /usr/local/bin/bark_login_notify.sh

# 排除用户列表
EXCLUDE_USERS=(

)

# 重点通知用户列表
IMPORTANT_USERS=(
  "root"
)

# 白名单 IP，此名单中的 IP 登录无需推送消息，但是需要打印日志
WHITE_IP_LIST=(
  "127.0.0.1"
)

# Bark 推送 API Endpoint
BARK_KEYS=(
  "" # 请填写你的 Bark Key
)

# 日志文件
LOG_FILE="/var/log/login_notifications.log"

# 函数：日志输出
log_message() {
  local level="$1"             # 日志级别 (INFO, ERROR, WARN)
  local message="$2"           # 日志消息
  local replace_newline="$3" # 是否替换换行符 (true/false)

  local timestamp=$(date +"%Y-%m-%d %H:%M:%S")

  if [ "$replace_newline" == "true" ]; then
    message=$(echo "$message" | tr '\n' ' ')
  fi

  echo "[$timestamp] $level: $message" >> "${LOG_FILE}"
}


# 函数：发送 Bark 推送
send_bark_notification() {
  local bark_key="$1"
  local payload="$2"

  timeout 5 curl -s -X POST \
    "https://api.day.app/${bark_key}" \
    -H "Content-Type: application/json" \
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

  if [[ -z "$OWN_IP" ]]; then
    OWN_IP="Unknown"
    log_message "WARNING" "Failed to determine own IP address."
  fi
  echo "$OWN_IP"
}


# 函数：查询 IP 地址归属地 (使用百度 IP API,带错误处理, 不使用 jq)
get_ip_location() {
  local ip="$1"
  local location=""
  local result=$(curl -s "https://opendata.baidu.com/api.php?query=${ip}&co=&resource_id=6006&oe=utf8" 2>/dev/null)

  if [[ -n "$result" ]]; then
    # 使用 grep 和 sed 解析 JSON (更脆弱，但避免了 jq 依赖)
    if echo "$result" | grep -q '"status":"0"'; then
      location=$(echo "$result" | grep -oP '"location":\s*"\K[^"]+' 2>/dev/null)
      # 如果匹配失败，location 为空, 需要提供默认值
      if [[ -z "$location" ]] ; then
        location="Unknown"
        log_message "WARNING" "Failed to parse location from API response using grep/sed."
      fi
    else
      log_message "WARNING"  "Failed to query IP location for ${ip} (百度 API error)."
    fi
  else
    log_message "WARNING"  "Failed to query IP location for ${ip} (curl error)."
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

# 获取用户登陆类型：我希望可以识别登陆、退出、su 切换用户
LOGIN_TYPE_NAME=""
case "$PAM_TYPE" in
  open_session)
    LOGIN_TYPE_NAME="登录"
    ;;
  close_session)
    LOGIN_TYPE_NAME="退出"
    ;;
  account) # 通常用于su
    if [[ "$PAM_SERVICE" == "su" ]]; then
      LOGIN_TYPE_NAME="su切换用户"
       SU_USER=$(sudo grep "^${PAM_USER}:" /etc/passwd | cut -d: -f1) # 获取 su 切换的目标用户
       if [ -n "$SU_USER" ]; then
            LOGIN_USER="su ${SU_USER}"
       fi
    else
      LOGIN_TYPE_NAME="账户验证"
    fi
    ;;
  *)
    LOGIN_TYPE_NAME="未知类型"
    ;;
esac

# 查询 DNS 获取自身 IP
OWN_IP=$(get_own_ip)
LOGIN_LOCATION=$(get_ip_location "${LOGIN_IP}")



# 确定是否为重点通知用户
IS_IMPORTANT="active"
for user in "${IMPORTANT_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    IS_IMPORTANT="critical"
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


# 构建消息标题和内容
TITLE="${LOGIN_USER} ${LOGIN_TYPE_NAME} ${OWN_IP}"
BODY="登陆IP：${LOGIN_IP}\n登陆地点：${LOGIN_LOCATION}\n登陆时间：${LOGIN_TIME}\n登陆用户：${LOGIN_USER}\n\n登陆服务：${LOGIN_SERVICE}\n类型：${LOGIN_TYPE}"

# 构建 JSON Payload
PAYLOAD=$(cat <<EOF
{
  "title": "${TITLE}",
  "body": "${BODY}",
  "isArchive": 1,
  "sound": "glass",
  "group": "服务器登录日志",
  "volume": 10,
  "level": "${IS_IMPORTANT}"
}
EOF
)

# 记录日志
log_message "INFO" "Title: ${TITLE} Body: ${BODY}."

# 白名单的 IP 登录只记录日志，不发送通知
if $IS_WHITELISTED; then
  log_message "INFO" "Whitelisted IP ${LOGIN_IP}, skipping notification."
  exit 0
fi


# 检查用户是否在排除列表中
for user in "${EXCLUDE_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    log_message "INFO" "User ${LOGIN_USER} is excluded, skipping notification."
    exit 0  # 如果在排除列表中，则退出脚本，不发送通知
  fi
done

# 发送 Bark 推送 (循环)
if [ -n "${BARK_KEYS[0]}" ]; then  # 确保 BARK_KEYS 不为空
  for BARK_KEY in "${BARK_KEYS[@]}"; do
    send_bark_notification "${BARK_KEY}" "${PAYLOAD}"
  done
else
  log_message "WARNING" "BARK_KEYS is empty, no notification will be sent."
fi

exit 0
