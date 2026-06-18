#!/usr/bin/env bash
# 数据库连通性诊断
# 用法：bash scripts/check-db.sh

set -euo pipefail

# 安全读取 .env（避免 JDBC URL 中 & 被 bash 当成后台命令）
load_env() {
  local file="$1"
  while IFS= read -r line || [ -n "$line" ]; do
    line="${line#"${line%%[![:space:]]*}"}"
    [[ -z "$line" || "$line" =~ ^# ]] && continue
    local key="${line%%=*}"
    local val="${line#*=}"
    key="${key%"${key##*[![:space:]]}"}"
    # 去掉首尾双引号
    if [[ "$val" =~ ^\".*\"$ ]]; then
      val="${val:1:${#val}-2}"
    fi
    export "$key=$val"
  done < "$file"
}

if [ -f .env ]; then
  load_env .env
else
  echo "[ERROR] 未找到 .env，请先执行：cp .env.example .env"
  exit 1
fi

URL="${SPRING_DATASOURCE_URL:-}"
USER="${SPRING_DATASOURCE_USERNAME:-root}"
PASS="${SPRING_DATASOURCE_PASSWORD:-}"

echo "========== 配置检查 =========="
if [ -z "$URL" ]; then
  echo "SPRING_DATASOURCE_URL=(空!) 请检查 .env 中 URL 是否加了双引号"
else
  echo "SPRING_DATASOURCE_URL=$URL"
fi
echo "SPRING_DATASOURCE_USERNAME=$USER"
if [ -n "$PASS" ]; then
  echo "SPRING_DATASOURCE_PASSWORD=****** (已设置, 长度=${#PASS})"
else
  echo "SPRING_DATASOURCE_PASSWORD=(未设置!)"
fi

HOST=$(echo "$URL" | sed -n 's#jdbc:mysql://\([^:/]*\).*#\1#p')
PORT=$(echo "$URL" | sed -n 's#jdbc:mysql://[^:]*:\([0-9]*\).*#\1#p')
PORT="${PORT:-3306}"

echo ""
echo "========== 网络测试 ($HOST:$PORT) =========="
if [ -z "$HOST" ]; then
  echo "[SKIP] URL 为空，无法测试"
elif command -v nc >/dev/null 2>&1; then
  nc -zv "$HOST" "$PORT" && echo "[PASS] 可连通" || echo "[FAIL] 无法连通（检查白名单/安全组）"
else
  timeout 3 bash -c "cat < /dev/null > /dev/tcp/$HOST/$PORT" \
    && echo "[PASS] 可连通" || echo "[FAIL] 无法连通（检查白名单/安全组）"
fi

echo ""
echo "========== 容器环境变量 =========="
if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^health-server$'; then
  docker exec health-server sh -c 'echo $SPRING_DATASOURCE_URL'
else
  echo "[SKIP] health-server 未运行"
fi

echo ""
echo "========== 常见错误 =========="
echo "若 docker logs 出现：Host '172.18.0.x' is not allowed to connect"
echo "  → MySQL 未授权 Docker 容器 IP，需在数据库侧放行（见下方说明）"
echo ""
echo "修复后执行：docker compose down && docker compose up -d --build"
