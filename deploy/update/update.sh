#!/usr/bin/env bash
# 生产环境增量更新脚本：更新 jar + 前端 + 预置管理员
# 用法：/bin/bash update.sh '<MySQL root 密码>'
set -euo pipefail

DB_PASS="${1:-}"
if [ -z "$DB_PASS" ]; then
  echo "用法: /bin/bash update.sh '<MySQL root 密码>'"
  exit 1
fi

# 无论从哪里调用，都切到本脚本所在目录执行
cd "$(dirname "$0")"

echo "==> 更新后端 jar"
mv app.jar /opt/agri/app.jar

echo "==> 更新前端 dist"
rm -rf /opt/agri/dist
mv dist /opt/agri/dist

echo "==> 预置管理员账号（admin/admin123）"
mysql -u root -p"${DB_PASS}" agri_platform < seed_admin.sql

echo "==> 重启后端与 Nginx"
systemctl restart agri
systemctl restart nginx

echo "==> 更新完成"
