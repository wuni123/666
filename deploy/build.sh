#!/usr/bin/env bash
# ============================================================
# 本地打包脚本：生成 deploy/package/ 部署包
# 依赖：JDK 17 + Maven + Node.js 18+（Git Bash / WSL / Linux 均可）
# 用法：bash deploy/build.sh
# ============================================================
set -euo pipefail

# 本机 JAVA_HOME 若未配置，取消注释下面两行（Windows Git Bash 示例）
# export JAVA_HOME="C:\Program Files\Microsoft\jdk-17.0.12.7-hotspot"
# export PATH="$JAVA_HOME/bin:$PATH"

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEPLOY="$ROOT/deploy"
PKG="$DEPLOY/package"

echo "==> 清理旧部署包"
rm -rf "$PKG"
mkdir -p "$PKG"

echo "==> 打包后端 jar"
cd "$ROOT/backend"
mvn -q clean package -DskipTests
cp target/agri-platform-0.0.1-SNAPSHOT.jar "$PKG/app.jar"

echo "==> 构建前端 dist"
cd "$ROOT/frontend"
npm run build
cp -r dist "$PKG/dist"

echo "==> 复制部署配置与脚本"
cp "$DEPLOY/application-prod.yml" "$PKG/"
cp "$DEPLOY/agri.service"          "$PKG/"
cp "$DEPLOY/nginx-agri.conf"       "$PKG/"
cp "$DEPLOY/init.sql"              "$PKG/"
cp "$DEPLOY/install.sh"            "$PKG/"

echo ""
echo "============================================================"
echo "  部署包已生成: $PKG"
echo "  下一步：把 package 目录整体上传到服务器（如 /root/agri-package），"
echo "  然后执行: sudo bash /root/agri-package/install.sh <MySQL密码>"
echo "============================================================"
