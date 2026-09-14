# 阿里云部署手册（轻量应用服务器）

本目录包含整套部署脚本。目标环境：**Ubuntu 22.04 / Alibaba Cloud Linux，2 核 2G 起步**。

```
deploy/
├── build.sh               # 本地打包脚本（生成 package/）
├── install.sh             # 服务器一键安装+部署脚本
├── application-prod.yml   # 后端生产配置（密码走环境变量）
├── agri.service           # systemd 守护后端
├── nginx-agri.conf        # Nginx：托管前端 + 反代 /api /upload
└── init.sql               # 数据库初始化（建表 + 江西演示数据）
```

## 一、领取服务器

1. 阿里云官网 → 实名认证 → **免费试用中心** 领 1 个月轻量应用服务器（2 核 2G 足够），或 **云翼计划**（学生认证）99 元/年。
2. 系统镜像选 **Ubuntu 22.04**。
3. 记下**公网 IP** 和 **root 密码**（或后续用密钥登录）。

> ⚠️ 免费试用机器到期会被释放，数据不保留。本平台演示数据在 `init.sql` 里，换新机器重跑一次 `install.sh` 即可，不依赖旧机器。

## 二、本地打包

在你自己的电脑上（需要 JDK 17 + Maven + Node.js 18）：

```bash
cd 考试
bash deploy/build.sh
```

如果本机 JAVA_HOME 有问题，先打开 `deploy/build.sh`，取消注释文件开头的两行 `export JAVA_HOME=...` 再跑。

打包完成后生成 `deploy/package/` 目录，里面就是部署包（jar + 前端 + 配置文件 + 脚本）。

## 三、上传到服务器

把整个 `package` 目录传到服务器任意位置（建议 `/root/agri-package`）。本地终端执行：

```bash
# 先在本地把 package 打成 tar.gz
cd deploy
tar -czf agri-package.tar.gz package

# 上传（把 IP 和密码换成你的）
scp agri-package.tar.gz root@你的公网IP:/root/

# 登录服务器
ssh root@你的公网IP

# 解压
cd /root
tar -xzf agri-package.tar.gz
```

## 四、一键部署

在服务器上执行（把 `<密码>` 换成你要给 MySQL 设置的 root 密码，密码别含单引号）：

```bash
sudo bash /root/agri-package/install.sh '你的MySQL密码'
```

脚本会自动完成：装 JDK 17 / MySQL 8 / Redis / Nginx → 初始化数据库并导入演示数据 → 部署后端 systemd 服务 → 配置 Nginx。执行完会打印访问地址。

## 五、放行端口

云控制台 → 轻量应用服务器 → **防火墙**（或安全组）→ 放行 **80** 端口（还有 SSH 22）。

## 六、验证

1. 浏览器打开 `http://公网IP/` → 应看到产品列表和江西演示数据。
2. 首页 → 登录 → 使用预置管理员 `admin` / `admin123` 登录进入后台，数据看板/地图应有数据。
   > 游客注册的账号默认为普通用户，登录后回到前台首页，不能进后台。
3. 常用命令：
   - 后端日志：`journalctl -u agri -f`
   - 后端状态：`systemctl status agri`
   - 重启后端：`sudo systemctl restart agri`
   - 数据库备份：`mysqldump -u root -p agri_platform > backup.sql`

## 七、常见问题

| 现象 | 处理 |
| --- | --- |
| 打不开页面 | 检查防火墙是否放行 80；`curl -I http://127.0.0.1/` 在服务器上自测 |
| 接口 502 | `systemctl status agri` 看后端是否起来；`journalctl -u agri -e` 看报错 |
| 地图打不开 | 确认高德 Key 已随前端构建打进 dist（`deploy/build.sh` 是在本地构建，key 在 `.env.local` 中）；新版 JS API 用 securityJsCode 免域名白名单 |
| 上传图片后 404 | 检查 `/opt/agri/upload` 目录权限，确保可写 |

## 八、后续更新代码

改完后端：本地 `mvn package` → 覆盖服务器 `/opt/agri/app.jar` → `sudo systemctl restart agri`。
改完前端：本地 `npm run build` → 覆盖服务器 `/opt/agri/dist`（先删旧的再拷贝）。
