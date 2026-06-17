#!/usr/bin/env bash
# 数据库连通性诊断
# 用法：bash scripts/check-db.sh

set -euo pipefail

if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
else
  echo "[ERROR] 未找到 .env，请先执行：cp .env.example .env"
  exit 1
fi

URL="${SPRING_DATASOURCE_URL:-}"
USER="${SPRING_DATASOURCE_USERNAME:-root}"
PASS="${SPRING_DATASOURCE_PASSWORD:-}"

echo "========== 配置检查 =========="
echo "SPRING_DATASOURCE_URL=$URL"
echo "SPRING_DATASOURCE_USERNAME=$USER"
if [ -n "$PASS" ]; then
  echo "SPRING_DATASOURCE_PASSWORD=****** (已设置, 长度=${#PASS})"
else
  echo "SPRING_DATASOURCE_PASSWORD=(未设置!)"
fi

# 从 JDBC URL 解析 host/port
HOST=$(echo "$URL" | sed -n 's#jdbc:mysql://\([^:/]*\).*#\1#p')
PORT=$(echo "$URL" | sed -n 's#jdbc:mysql://[^:]*:\([0-9]*\).*#\1#p')
PORT="${PORT:-3306}"

echo ""
echo "========== 网络测试 ($HOST:$PORT) =========="
if command -v nc >/dev/null 2>&1; then
  nc -zv "$HOST" "$PORT" && echo "[PASS] 可连通" || echo "[FAIL] 无法连通"
else
  timeout 3 bash -c "cat < /dev/null > /dev/tcp/$HOST/$PORT" \
    && echo "[PASS] 可连通" || echo "[FAIL] 无法连通"
fi

echo ""
echo "========== 容器环境变量 =========="
if docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^health-server$'; then
  docker exec health-server sh -c 'echo $SPRING_DATASOURCE_URL'
else
  echo "[SKIP] health-server 未运行"
fi

echo ""
echo "修复后执行：docker compose down && docker compose up -d --build"
