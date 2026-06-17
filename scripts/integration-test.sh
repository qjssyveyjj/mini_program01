#!/usr/bin/env bash
# 前后端联调测试脚本（在部署服务器上执行）
# 用法：bash scripts/integration-test.sh [BASE_URL]
# 示例：bash scripts/integration-test.sh http://8.166.140.82

set -euo pipefail

BASE_URL="${1:-http://127.0.0.1}"
API_DIRECT="${API_DIRECT:-http://127.0.0.1:8080}"

pass=0
fail=0

check() {
  local name="$1"
  local cmd="$2"
  echo ""
  echo "==> $name"
  if eval "$cmd"; then
    echo "[PASS] $name"
    pass=$((pass + 1))
  else
    echo "[FAIL] $name"
    fail=$((fail + 1))
  fi
}

echo "========== 容器状态 =========="
docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}' 2>/dev/null || echo "docker 不可用"

echo ""
echo "========== 后端直连 (8080) =========="
check "后端 /api/stats/users" "curl -sf '$API_DIRECT/api/stats/users' | head -c 200"
check "后端 /api/users" "curl -sf '$API_DIRECT/api/users' | head -c 200"
check "后端 /api/health/all" "curl -sf '$API_DIRECT/api/health/all' | head -c 200"

echo ""
echo "========== 经 Nginx 反代 ($BASE_URL) =========="
check "前端首页" "curl -sf -o /dev/null -w '%{http_code}' '$BASE_URL/' | grep -q '^200$'"
check "API /api/stats/users" "curl -sf '$BASE_URL/api/stats/users' | head -c 200"
check "API /api/users" "curl -sf '$BASE_URL/api/users' | head -c 200"

echo ""
echo "========== 后端日志（最近 20 行）=========="
docker logs health-server --tail 20 2>/dev/null || echo "无法读取 health-server 日志"

echo ""
echo "========== 结果 =========="
echo "通过: $pass  失败: $fail"
if [ "$fail" -gt 0 ]; then
  echo ""
  echo "若后端直连失败，请检查："
  echo "  1. docker logs health-server"
  echo "  2. 根目录 .env 中 DB_HOST/DB_PASSWORD 是否正确（含 # 的密码需加引号）"
  echo "  3. MySQL 白名单是否放行当前服务器 IP"
  exit 1
fi

echo "联调测试全部通过。"
