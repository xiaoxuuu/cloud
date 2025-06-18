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

# TODO 白名单 IP，此名单中的IP无需推送消息，但是需要打印日志
WHITE_IP_LIST=(
  "root"
)

# Bark 推送 API Endpoint
BARK_KEYS=(
  ""
)

# 获取登录信息
LOGIN_TIME=$(date +"%Y-%m-%d %H:%M:%S")
LOGIN_USER=${PAM_USER:-$(logname)} # 尝试使用 PAM_USER，否则使用 logname
LOGIN_USER_WHOAMI=$(whoami)
LOGIN_IP=$(echo $PAM_RHOST | awk '{print $1}')
LOGIN_TYPE=$(echo $PAM_TYPE)
LOGIN_SERVICE=$(echo $PAM_SERVICE)
# TODO 查询 DNS 获取自身 IP：dig +short myip.opendns.com @resolver1.opendns.com
OWN_IP=""
# TODO 使用百度 IP 查询 LOGIN_IP 的地址：https://opendata.baidu.com/api.php?query=${LOGIN_IP}&co=&resource_id=6006&oe=utf8
# 响应：{"status":"0","t":"","set_cache_time":"","data":[{"ExtendedLocation":"","OriginQuery":"222.222.22.2","SchemaVer":"","appinfo":"","disp_type":0,"fetchkey":"222.222.22.2","location":"河北省石家庄市 电信","origip":"222.222.22.2","origipquery":"222.222.22.2","resourceid":"6006","role_id":0,"schemaID":"","shareImage":1,"showLikeShare":1,"showlamp":"1","strategyData":{},"titlecont":"IP地址查询","tplt":"ip"}]}
# 需要考虑接口调用失败降级的情况

# 确定是否为重点通知用户
IS_IMPORTANT="active"
for user in "${IMPORTANT_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    IS_IMPORTANT="critical"
    break
  fi
  if [ "$LOGIN_USER_WHOAMI" == "$user" ]; then
    IS_IMPORTANT="critical"
    break
  fi
done

# 构建消息标题和内容
TITLE="${LOGIN_USER}/${LOGIN_USER_WHOAMI} 登录 ${OWN_IP}"
BODY="logname：${LOGIN_USER}\nwhoami：${LOGIN_USER_WHOAMI}\n登陆IP：${LOGIN_IP}\n登陆时间：${LOGIN_TIME}\n登陆服务：${LOGIN_SERVICE}\n类型：${LOGIN_TYPE}"

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
echo "[$(date +"%Y-%m-%d %H:%M:%S")] Title: ${TITLE} Body: ${BODY}." >> /var/log/login_notifications.log

# 检查用户是否在排除列表中
for user in "${EXCLUDE_USERS[@]}"; do
  if [ "$LOGIN_USER" == "$user" ]; then
    exit 0  # 如果在排除列表中，则退出脚本，不发送通知
  fi
done

# 发送 Bark 推送 (循环)
for BARK_KEY in "${BARK_KEYS[@]}"; do
  timeout 5 curl -s -X POST \
    "https://api.day.app/${BARK_KEY}" \
    -H "Content-Type: application/json" \
    -d "$PAYLOAD" > /dev/null 2>&1
done