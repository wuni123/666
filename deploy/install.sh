#!/usr/bin/env bash
# ============================================================
# 乡镇农产品信息发布平台 - 服务器一键部署脚本
# 适用：Ubuntu 22.04 / Alibaba Cloud Linux，root 用户
# 用法：sudo bash install.sh <MySQL root 密码>
# 示例：sudo bash install.sh '你的MySQL密码'
# 注意：密码不要包含单引号
# ============================================================
set -euo pipefail

DB_PASS="${1:-}"
if [ -z "$DB_PASS" ]; then
  echo "用法: sudo bash install.sh <MySQL root 密码>"
  exit 1
fi

PKG_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR=/opt/agri

echo "==> [1/6] 安装系统依赖 (JDK 17 / MySQL / Redis / Nginx) ..."
export DEBIAN_FRONTEND=noninteractive
apt-get update -y
apt-get install -y openjdk-17-jdk-headless mysql-server redis-server nginx unzip curl

echo "==> [2/6] 启动 MySQL / Redis 并设置 root 密码 ..."
systemctl enable --now mysql
systemctl enable --now redis-server

mysql <<SQL
CREATE DATABASE IF NOT EXISTS agri_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '${DB_PASS}';
FLUSH PRIVILEGES;
SQL

echo "==> [3/6] 导入数据库初始化脚本（建表 + 江西演示数据）..."
mysql -u root -p"${DB_PASS}" --default-character-set=utf8mb4 < "${PKG_DIR}/init.sql"

echo "==> [4/6] 部署应用文件到 ${APP_DIR} ..."
mkdir -p "${APP_DIR}/dist" "${APP_DIR}/upload" "${APP_DIR}/logs"
cp "${PKG_DIR}/app.jar" "${APP_DIR}/app.jar"
cp "${PKG_DIR}/application-prod.yml" "${APP_DIR}/application-prod.yml"
cp -r "${PKG_DIR}/dist/." "${APP_DIR}/dist/"

# 环境变量文件（数据库密码 + 随机 JWT 密钥，权限 600）
cat > "${APP_DIR}/.env" <<EOF
AGRI_DB_PASSWORD=${DB_PASS}
AGRI_JWT_SECRET=$(cat /proc/sys/kernel/random/uuid | tr -d '-' | head -c 48)
EOF
chmod 600 "${APP_DIR}/.env"

echo "==> [5/6] 注册并启动后端 systemd 服务 ..."
cp "${PKG_DIR}/agri.service" /etc/systemd/system/agri.service
systemctl daemon-reload
systemctl enable --now agri

echo "==> [6/6] 配置 Nginx 反代并启动 ..."
cp "${PKG_DIR}/nginx-agri.conf" /etc/nginx/sites-available/agri
ln -sf /etc/nginx/sites-available/agri /etc/nginx/sites-enabled/agri
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl enable nginx && systemctl restart nginx

PUBLIC_IP=$(curl -s --max-time 5 https://api.ipify.org || echo "your-server-ip")
echo ""
echo "============================================================"
echo "  部署完成！"
echo "  访问地址:   http://${PUBLIC_IP}/"
echo "  管理员登录:  admin / admin123（游客注册为普通用户，登录回前台）"
echo "  后端日志:   journalctl -u agri -f"
echo "  服务状态:   systemctl status agri"
echo "============================================================"
echo "  提示：记得在云控制台「安全组/防火墙」放行 80 端口"
