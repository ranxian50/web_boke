# 📝 个人技术博客系统

基于 **Vue 3 + Element Plus**（前端）和 **Spring Boot 3 + JPA**（后端）的前后端分离个人博客系统。

## 功能特性

| 功能 | 说明 |
|:--|:--|
| ✅ Markdown / Jupyter Notebook 发布 | 支持直接编写和上传文件两种方式 |
| ✅ AI 自动标签 | 发布时调用 DeepSeek v4 Flash API 自动生成标签 |
| ✅ 标签筛选 | 访客可按标签分类查看文章 |
| ✅ 嵌套评论 | 访客填写昵称+邮箱即可评论，支持回复 |
| ✅ 浏览统计 | 每篇文章自动计数浏览次数 |
| ✅ 随机推荐 | 首页随机推荐 3 篇文章 |
| ✅ JWT 认证 | 管理员可安全登录管理后台 |
| ✅ 管理后台 | 文章/评论/标签统一管理 |

## 技术栈

### 前端
- Vue 3（组合式 API）+ Vite 5 构建
- Element Plus UI 组件库
- Vue Router 4 + Pinia 状态管理
- Axios HTTP 客户端（JWT 拦截注入）
- marked.js + highlight.js Markdown 渲染

### 后端
- Spring Boot 3.2 + Spring Data JPA
- Spring Security + JWT（jjwt 0.12）
- MySQL 8 数据库
- SpringDoc OpenAPI 文档
- DeepSeek v4 Flash API 集成

## 快速开始

### 前置条件
- **Java 17+** 和 Maven（或使用 `mvnw` 自动下载）
- **Node.js 18+**
- **MySQL 8+**

### 1. 创建数据库
```bash
mysql -u root -p
CREATE DATABASE blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置 DeepSeek API
编辑 `backend/src/main/resources/application.yml`，将 `deepseek.api.key` 改为你的 DeepSeek API Key。

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```
> 后端运行在 http://localhost:8080，自动建表并创建默认管理员账号

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```
> 前端运行在 http://localhost:5173

### 5. 访问系统
- **博客首页**：http://localhost:5173
- **管理后台**：http://localhost:5173/admin
- **API 文档**：http://localhost:8080/swagger-ui.html

## 默认管理员账户
> **用户名**：`admin`  
> **密码**：`admin123`

⚠️ 首次登录后请及时修改密码。

## 项目结构
```
boke/
├── frontend/               # Vue 3 前端项目
│   ├── src/
│   │   ├── views/blog/     # 博客前台页面
│   │   ├── views/admin/    # 管理后台页面
│   │   ├── components/     # 可复用组件
│   │   ├── api/            # API 封装
│   │   ├── stores/         # Pinia 状态管理
│   │   └── utils/          # 工具函数
│   └── package.json
├── backend/                # Spring Boot 后端项目
│   ├── src/main/java/
│   │   └── com/example/blog/
│   │       ├── config/     # 全局配置
│   │       ├── common/     # 公共工具
│   │       ├── auth/       # 认证模块
│   │       ├── article/    # 文章模块
│   │       ├── tag/        # 标签模块
│   │       ├── comment/    # 评论模块
│   │       ├── file/       # 文件上传
│   │       └── ai/         # AI 标签服务
│   └── pom.xml
├── uploads/                # 上传文件存储
├── docs/                   # 项目文档
└── README.md
```

## API 接口一览

| 方法 | 路径 | 认证 | 说明 |
|:--|:--|:--|:--|
| POST | /api/v1/auth/login | ❌ | 管理员登录 |
| GET | /api/v1/articles | ❌ | 文章列表（分页+标签+搜索） |
| GET | /api/v1/articles/{id} | ❌ | 文章详情 |
| GET | /api/v1/articles/random | ❌ | 随机推荐 |
| POST | /api/v1/articles | ✅ | 发布文章 |
| PUT | /api/v1/articles/{id} | ✅ | 编辑文章 |
| DELETE | /api/v1/articles/{id} | ✅ | 删除文章 |
| GET | /api/v1/tags | ❌ | 标签列表 |
| GET/POST | /api/v1/articles/{id}/comments | ❌ | 评论 |
| DELETE | /api/v1/comments/{id} | ✅ | 删除评论 |
| POST | /api/v1/files/upload | ✅ | 上传文件 |

## License
MIT
