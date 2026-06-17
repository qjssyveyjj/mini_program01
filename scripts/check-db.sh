#!/usr/bin/env bash
# 数据库连通性诊断（在云服务器项目根目录执行）
# 用法：bash scripts/check-db.sh

set -euo pipefail

if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
else
  echo "[WARN] 未找到 .env，使用默认变量"
fi

DB_HOST="${DB_HOST:-172.22.67.85}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-health_manager}"
DB_USERNAME="${DB_USERNAME:-root}"

echo "========== 配置检查 =========="
echo "DB_HOST=$DB_HOST"
echo "DB_PORT=$DB_PORT"
echo "DB_NAME=$DB_NAME"
echo "DB_USERNAME=$DB_USERNAME"
if [ -n "${DB_PASSWORD:-}" ]; then
  echo "DB_PASSWORD=****** (已设置, 长度=${#DB_PASSWORD})"
else
  echo "DB_PASSWORD=(未设置!)"
fi

echo ""
echo "========== 宿主机网络测试 =========="
if command -v nc >/dev/null 2>&1; then
  nc -zv "$DB_HOST" "$DB_PORT" && echo "[PASS] 宿主机可连通 $DB_HOST:$DB_PORT" || echo "[FAIL] 宿主机无法连通 $DB_HOST:$DB_PORT"
else
  timeout 3 bash -c "cat < /dev/null > /dev/tcp/$DB_HOST/$DB_PORT" \
    && echo "[PASS] 宿主机可连通 $DB_HOST:$DB_PORT" \
    || echo "[FAIL] 宿主机无法连通 $DB_HOST:$DB_PORT"
fi

echo ""
echo "========== Docker 容器内网络测试 =========="
if docker ps --format '{{.Names}}' | grep -q '^health-server$'; then
  docker exec health-server sh -c "nc -zv $DB_HOST $DB_PORT" \
    && echo "[PASS] 容器内可连通数据库" \
    || echo "[FAIL] 容器内无法连通数据库（检查白名单/安全组）"
else
  echo "[SKIP] health-server 容器未运行"
fi

echo ""
echo "========== 容器环境变量 =========="
if docker ps --format '{{.Names}}' | grep -q '^health-server$'; then
  docker exec health-server sh -c 'echo SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL; echo DB_HOST=$DB_HOST; echo DB_USERNAME=$DB_USERNAME'
else
  echo "[SKIP] health-server 容器未运行"
fi

echo ""
echo "========== MySQL 登录测试（宿主机需安装 mysql 客户端）=========="
if command -v mysql >/dev/null 2>&1 && [ -n "${DB_PASSWORD:-}" ]; then
  mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USERNAME" -p"$DB_PASSWORD" -e "SELECT 1;" "$DB_NAME" \
    && echo "[PASS] MySQL 登录成功" \
    || echo "[FAIL] MySQL 登录失败（地址/账号/密码/白名单）"
else
  echo "[SKIP] 未安装 mysql 客户端或未设置 DB_PASSWORD"
fi

echo ""
echo "========== 建议 =========="
echo "1. 在项目根目录创建 .env，密码含 # 必须加引号，例如：DB_PASSWORD=\"QQ13579aa##\""
echo "2. 云数据库白名单需放行当前 ECS 内网 IP"
echo "3. 确认已创建数据库：CREATE DATABASE health_manager;"
echo "4. 修复后执行：docker compose down && docker compose up -d --build"
