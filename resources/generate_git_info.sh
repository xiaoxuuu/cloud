#!/bin/bash

# 获取项目根目录
PROJECT_ROOT="$1"

# 定义 yaml 文件路径
APPLICATION_YAML="$PROJECT_ROOT/src/main/resources/git-info.yaml"

# 获取当前 Git 分支名
GIT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# 获取最近构建时间 (使用 ISO 8601 格式)
BUILD_TIME=$(date +"%Y-%m-%d %H:%M:%S")

# 获取最近 5 次提交记录
RECENT_COMMITS=$(git log -n 5 --pretty=format:"%h %ad %an %s" --date=format:"%Y-%m-%d %H:%M:%S")

# 格式化为 YAML 格式
YAML_CONTENT="
build:
  time: \"${BUILD_TIME:-unknown}\"
git:
  branch: ${GIT_BRANCH:-unknown}
  commits:
"

# 添加提交记录到 YAML 内容
if [[ -z "$RECENT_COMMITS" ]]; then
  YAML_CONTENT+="    - \"No commits info\"\n"  # 如果没提交 也要有值 不然会报错
else
  while IFS= read -r line; do
    YAML_CONTENT+="    - \"${line}\"\n" #每个提交都用引号括起来，避免YAML解析问题
  done <<< "$RECENT_COMMITS"
fi

# 将 YAML 内容输出到文件
echo "$YAML_CONTENT" > "$APPLICATION_YAML"

#echo "Git info appended to $APPLICATION_YAML"
#echo "$YAML_CONTENT"