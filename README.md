# 乡镇农产品信息发布平台

一个帮助乡镇农户发布农产品（脐橙、茶叶、大米等）信息的 Web 平台。

- **前台**：游客浏览农产品列表、按分类搜索。
- **后台**：管理员（村委会）管理农产品信息、发布通知。
- **特色功能**：农产品地图展示（高德地图 API）、数据统计看板（ECharts）。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 前端 | Vue 3 + Element Plus + Vite + Pinia + Vue Router + ECharts |
| 后端 | Spring Boot 3.x + MyBatis-Plus |
| 数据库 | MySQL 8 + Redis |
| 鉴权 | JWT（jjwt 0.12）+ BCrypt |

## 目录结构

```
考试/
├── backend/                 # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/rural/agri/
│       │   ├── AgriApplication.java
│       │   ├── common/      # 统一返回、全局异常
│       │   ├── config/      # MyBatis-Plus / Redis / CORS 配置
│       │   ├── controller/  # 接口层
│       │   ├── dto/         # 入参对象
│       │   ├── entity/      # 实体
│       │   ├── interceptor/ # JWT 拦截器
│       │   ├── mapper/      # MyBatis-Plus Mapper
│       │   ├── service/     # 业务层
│       │   ├── utils/       # JWT 工具
│       │   └── vo/          # 出参对象
│       └── resources/application.yml
├── frontend/                # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/             # axios 封装与接口
│       ├── layouts/         # 后台布局
│       ├── router/          # 路由
│       ├── store/           # Pinia 状态
│       └── views/           # 页面
├── sql/schema.sql           # 建表脚本
└── README.md
```

## 快速启动

### 1. 数据库

```bash
mysql -u root -p < sql/schema.sql
```

数据库账号密码**不写在代码里**。本地开发有两种配置方式，任选其一：

**方式一（推荐）**：在 `backend/src/main/resources/application-local.yml` 中填写。该文件已被 `.gitignore` 忽略，不会提交；`application.yml` 里的 `spring.profiles.default=local` 会自动加载它。

```yaml
spring:
  datasource:
    username: root
    password: 你的数据库密码
```

**方式二**：通过环境变量注入，无需任何文件。

```bash
# Windows PowerShell
$env:AGRI_DB_PASSWORD="你的数据库密码"
# Linux / macOS
export AGRI_DB_PASSWORD="你的数据库密码"
```

生产环境走 `application-prod.yml`，由 systemd 的 `EnvironmentFile=/opt/agri/.env` 提供 `AGRI_DB_PASSWORD` 和 `AGRI_JWT_SECRET`，详见 [deploy/README.md](deploy/README.md)。

可选：导入江西（赣州脐橙产区等）演示数据，让首页和地图有内容展示：

```bash
mysql -u root -p < sql/demo_data.sql
```

### 2. 后端（需 JDK 17 + Maven）

```bash
cd backend
mvn spring-boot:run
```

服务启动在 `http://localhost:8080`，健康检查接口 `GET /api/health`。

### 3. 前端（需 Node.js 18+）

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173`。开发环境已配置 `/api` 代理到后端 8080 端口。

### 4. 首次登录

数据库已预置管理员账号：`admin` / `admin123`，在登录页用它进入后台管理。

前台注册的账号默认为普通用户（`USER`），登录后回到前台首页，无法访问后台。

## 功能模块进度

| 模块 | 状态 |
| --- | --- |
| 用户登录/注册（JWT） | ✅ 已实现 |
| 农产品列表（分页 + 搜索） | ✅ 已实现 |
| 发布/编辑/删除农产品（管理员） | ✅ 已实现 |
| 农产品地图展示（高德 API） | ✅ 已实现 |
| 后台数据看板（ECharts） | ✅ 已实现 |
| 通知模块（发布/展示） | ✅ 已实现 |
| Redis 缓存（分类/地图热点数据） | ✅ 已实现 |
| 前台游客浏览页 | ✅ 已实现 |

## 约定

- 统一返回结构 `{ code, message, data }`，`code=200` 表示成功。
- 需要登录的接口统一放在 `/api/admin/**` 前缀下，由 JWT 拦截器鉴权。
- 请求头携带 `Authorization: Bearer <token>`。
- 所有表使用逻辑删除字段 `deleted`（0 正常 / 1 删除）。
