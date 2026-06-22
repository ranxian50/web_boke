# 个人博客系统设计文档

> 项目名称：技术博客管理系统
> 创建日期：2026-06-22
> 作者：管理员
> 状态：设计定稿

---

## 1. 项目概述

### 1.1 项目目标

开发一个功能完整的个人技术博客系统，支持 Markdown 和 Jupyter Notebook 文档的发布与展示，集成 AI 自动标签功能，实现前后端分离架构。

### 1.2 核心功能

| 功能模块 | 说明 |
|:--|:--|
| **用户认证** | JWT 无状态认证，仅管理员可登录后台 |
| **文章管理** | 发布/编辑/删除文章，支持 Markdown 和 .ipynb 上传 |
| **内容渲染** | Markdown 渲染为 HTML，Jupyter Notebook 解析为结构化展示 |
| **AI 标签** | 发布时调用 DeepSeek v4 Flash API 自动打标签 |
| **标签筛选** | 访客可按标签分类查看文章 |
| **评论系统** | 访客填写昵称+邮箱可评论，支持嵌套回复 |
| **浏览统计** | 每篇文章自动计数浏览次数 |
| **随机推荐** | 首页随机展示 N 篇文章推荐给访客 |

### 1.3 用户角色

| 角色 | 权限 |
|:--|:--|
| **管理员（你）** | 全部权限：发布/编辑/删除文章、管理评论、管理标签、查看统计 |
| **访客** | 查看文章列表、文章详情、按标签筛选、发表评论 |

---

## 2. 技术栈

### 2.1 前端

| 技术 | 版本 | 用途 |
|:--|:--|:--|
| Vue | 3.x | 前端框架（组合式 API） |
| Vite | 5.x | 构建工具 |
| Element Plus | 最新 | UI 组件库 |
| Vue Router | 4.x | 前端路由 |
| Pinia | 最新 | 状态管理 |
| Axios | 最新 | HTTP 请求封装 |
| marked.js | 最新 | Markdown 渲染 |
| highlight.js | 最新 | 代码语法高亮 |

### 2.2 后端

| 技术 | 版本 | 用途 |
|:--|:--|:--|
| Java | 17 | 运行语言 |
| Spring Boot | 3.2.x | 后端框架 |
| Spring Data JPA | 3.2.x | ORM / 数据持久化 |
| Spring Security | 3.2.x | 认证与授权 |
| MySQL | 8.x | 数据库 |
| jjwt | 0.12.x | JWT 生成与验证 |
| SpringDoc OpenAPI | 2.x | 接口文档自动生成 |

### 2.3 外部服务

| 服务 | 用途 |
|:--|:--|
| DeepSeek v4 Flash API | AI 文章标签分类 |

---

## 3. 系统架构

```
┌───────────────────────────────────────────────────────────┐
│                      浏览器                                │
│  ┌─────────────────────────────────────────────────────┐  │
│  │            Vue 3 + Element Plus SPA                  │  │
│  │  ┌──────────────────┐  ┌──────────────────────┐    │  │
│  │  │   博客前台        │  │    管理后台            │    │  │
│  │  │   / /article/... │  │    /admin/...         │    │  │
│  │  └──────────────────┘  └──────────────────────┘    │  │
│  └─────────────────────────────────────────────────────┘  │
│                          ↓                                  │
│              Axios HTTP + JWT (Authorization: Bearer)       │
└───────────────────────────────────────────────────────────┘
                          ↓
┌───────────────────────────────────────────────────────────┐
│                Spring Boot REST API                        │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  security/   CorsConfig → JwtAuthFilter → Controller│  │
│  └─────────────────────────────────────────────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌────────┐ ┌──────────────┐  │
│  │ Auth     │ │ Article  │ │ Tag    │ │ Comment      │  │
│  │ Controller│ │Controller│ │Control │ │ Controller   │  │
│  └──────────┘ └──────────┘ └────────┘ └──────────────┘  │
│  ┌──────────┐ ┌──────────────────────────────────────┐  │
│  │ File     │ │ DeepSeek AI Tag Service              │  │
│  │Controller│ │ → HTTP call → parse tags → save     │  │
│  └──────────┘ └──────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────┐  ┌─────────────────────────────────┐
│     MySQL 8.x       │  │  本地文件存储                     │
│  (root/root)        │  │  uploads/markdown/               │
│  5 张表             │  │  uploads/notebook/               │
└─────────────────────┘  └─────────────────────────────────┘
```

---

## 4. 数据库设计

### 4.1 ER 概览

共 5 张表：`tb_user`、`tb_article`、`tb_tag`、`tb_article_tag`、`tb_comment`

### 4.2 表结构

#### tb_user（用户表）

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 用户 ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(255) | NOT NULL | BCrypt 加密密码 |
| nickname | VARCHAR(50) | NOT NULL | 昵称 |
| avatar | VARCHAR(255) | NULLABLE | 头像 URL |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'ADMIN' | 角色：ADMIN / USER |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

#### tb_article（文章表）

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 文章 ID |
| user_id | BIGINT | FK → tb_user.id | 发布者 ID |
| title | VARCHAR(200) | NOT NULL | 文章标题 |
| summary | VARCHAR(500) | NULLABLE | 摘要（前 80 字） |
| content | LONGTEXT | NOT NULL | 文章全文 |
| content_type | VARCHAR(20) | NOT NULL | MARKDOWN / NOTEBOOK |
| source_file | VARCHAR(255) | NULLABLE | 原始文件路径 |
| cover_image | VARCHAR(255) | NULLABLE | 封面图 |
| view_count | INT | DEFAULT 0 | 浏览次数 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PUBLISHED' | DRAFT / PUBLISHED |
| create_time | DATETIME | NOT NULL | 发布时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

#### tb_tag（标签表）

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 标签 ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 标签名 |
| category | VARCHAR(50) | NULLABLE | 分类：LANGUAGE / FRAMEWORK / TOOL / DATABASE / OTHER |
| create_time | DATETIME | NOT NULL | 创建时间 |

#### tb_article_tag（文章-标签关联表）

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 关联 ID |
| article_id | BIGINT | FK → tb_article.id | 文章 ID |
| tag_id | BIGINT | FK → tb_tag.id | 标签 ID |

#### tb_comment（评论表）

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 评论 ID |
| article_id | BIGINT | FK → tb_article.id | 所属文章 |
| parent_id | BIGINT | FK → tb_comment.id, NULLABLE | 父评论（嵌套回复） |
| nickname | VARCHAR(50) | NOT NULL | 评论者昵称 |
| email | VARCHAR(100) | NULLABLE | 评论者邮箱 |
| content | TEXT | NOT NULL | 评论内容 |
| create_time | DATETIME | NOT NULL | 评论时间 |

### 4.3 索引设计

| 表 | 索引字段 | 类型 | 说明 |
|:--|:--|:--|:--|
| tb_article | create_time | DESC | 文章列表排序 |
| tb_article | status | 普通 | 筛选已发布文章 |
| tb_article | user_id | FK | 按作者查询 |
| tb_comment | article_id | FK | 按文章查评论 |
| tb_article_tag | (article_id, tag_id) | UNIQUE | 避免重复关联 |

---

## 5. 前端设计

### 5.1 路由结构

```
路由路径                     页面组件           访问权限
─────────────────────────────────────────────────────────
/                           Home.vue           公开
/article/:id                ArticleDetail.vue  公开
/tag/:tagName               TagFilter.vue      公开
/login                      Login.vue          公开
/admin                      Dashboard.vue      需登录
/admin/articles             ArticleList.vue    需登录
/admin/articles/new         ArticleEditor.vue  需登录
/admin/articles/:id/edit    ArticleEditor.vue  需登录
/admin/comments             CommentManage.vue  需登录
/admin/tags                 TagManage.vue      需登录
/admin/profile              Profile.vue        需登录
```

### 5.2 组件树

```
src/
├── App.vue                          # 根组件
├── router/index.js                  # 路由配置 + 路由守卫
├── layouts/
│   ├── BlogLayout.vue               # 前台布局（导航 + 内容 + 底部）
│   └── AdminLayout.vue              # 后台布局（侧边栏 + 顶栏 + 内容）
├── views/
│   ├── blog/
│   │   ├── Home.vue                 # 文章列表 + 随机推荐
│   │   ├── ArticleDetail.vue        # 文章详情 + 评论区
│   │   ├── TagFilter.vue            # 按标签筛选
│   │   └── Login.vue                # 登录页
│   └── admin/
│       ├── Dashboard.vue            # 仪表盘统计
│       ├── ArticleList.vue          # 文章管理列表
│       ├── ArticleEditor.vue        # 发布/编辑（含 AI 标签）
│       ├── CommentManage.vue        # 评论管理
│       ├── TagManage.vue            # 标签管理
│       └── Profile.vue              # 个人设置
├── components/
│   ├── common/
│   │   ├── ArticleCard.vue          # 文章卡片组件
│   │   ├── TagChip.vue              # 标签展示
│   │   ├── CommentList.vue          # 评论列表 + 输入框
│   │   └── Pagination.vue           # 分页
│   ├── markdown/
│   │   └── MarkdownRenderer.vue     # Markdown 渲染
│   └── notebook/
│       └── NotebookRenderer.vue     # Jupyter Notebook 渲染
├── api/
│   ├── request.js                   # Axios 实例 (拦截器 + JWT 注入)
│   ├── auth.js                      # 认证 API
│   ├── article.js                   # 文章 API
│   ├── comment.js                   # 评论 API
│   └── tag.js                       # 标签 API
├── stores/
│   ├── auth.js                      # Pinia 认证状态
│   └── app.js                       # 全局状态
└── utils/
    ├── parseNotebook.js             # .ipynb JSON 解析器
    └── format.js                    # 格式化工具
```

### 5.3 前台页面结构（极简单栏式）

```
┌─────────────────────────────────────────────┐
│  [Logo] 博客名称           [登录]            │  ← 顶栏导航
├─────────────────────────────────────────────┤
│                                             │
│  标签筛选栏: [全部] [Java] [Vue] [Python]   │  ← 标签行
│                                             │
├─────────────────────────────────────────────┤
│                                             │
│  ┌───────────────────────────────────────┐  │
│  │  文章卡片 1                           │  │
│  │  📝 标题：Spring Boot 入门指南        │  │
│  │  📋 摘要：本文介绍...                │  │
│  │  🏷️ [Java] [Spring Boot] [后端]      │  │
│  │  👁️ 128次阅读  📅 2026-06-20        │  │
│  └───────────────────────────────────────┘  │
│                                             │
│  ┌───────────────────────────────────────┐  │
│  │  文章卡片 2                           │  │
│  │  ...                                  │  │
│  └───────────────────────────────────────┘  │
│                                             │
│  ─── 猜你喜欢 ────────────────────────────  │  ← 随机推荐区
│  [卡片A] [卡片B] [卡片C]                    │
│                                             │
│  [上一页] 1 2 3 ... [下一页]               │  ← 分页
│                                             │
├─────────────────────────────────────────────┤
│  © 2026 个人技术博客  [GitHub] [关于]       │  ← 底部
└─────────────────────────────────────────────┘
```

---

## 6. 后端设计

### 6.1 包结构

```
com.example.blog
├── BlogApplication.java
├── config/
│   ├── SecurityConfig.java         # Spring Security 配置
│   ├── CorsConfig.java            # 跨域配置
│   ├── WebMvcConfig.java          # 静态资源映射
│   └── OpenApiConfig.java         # API 文档
├── common/
│   ├── Result.java                # 统一响应体
│   ├── PageResult.java            # 分页响应
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── constants/
│       └── ArticleStatus.java     # 文章状态常量
├── auth/
│   ├── entity/User.java
│   ├── repository/UserRepository.java
│   ├── service/UserService.java / UserDetailsServiceImpl.java
│   ├── controller/AuthController.java
│   ├── dto/LoginRequest.java / LoginResponse.java
│   └── jwt/JwtUtil.java / JwtAuthFilter.java
├── article/
│   ├── entity/Article.java
│   ├── repository/ArticleRepository.java
│   ├── service/ArticleService.java
│   ├── controller/ArticleController.java
│   └── dto/ArticleCreateRequest.java / ArticleUpdateRequest.java / ArticleVO.java
├── tag/
│   ├── entity/Tag.java / ArticleTag.java
│   ├── repository/TagRepository.java / ArticleTagRepository.java
│   ├── service/TagService.java
│   └── controller/TagController.java
├── comment/
│   ├── entity/Comment.java
│   ├── repository/CommentRepository.java
│   ├── service/CommentService.java
│   └── controller/CommentController.java
├── file/
│   ├── service/FileStorageService.java
│   ├── service/MarkdownParseService.java
│   ├── service/NotebookParseService.java
│   └── controller/FileController.java
└── ai/
    ├── service/AiTagService.java
    └── config/AiConfig.java
```

### 6.2 API 接口清单

| 方法 | 路径 | 认证 | 说明 |
|:--|:--|:--|:--|
| POST | /api/v1/auth/login | 否 | 管理员登录 |
| GET | /api/v1/auth/me | 是 | 获取当前用户 |
| GET | /api/v1/articles | 否 | 文章列表（分页+标签+搜索） |
| GET | /api/v1/articles/{id} | 否 | 文章详情 |
| GET | /api/v1/articles/random | 否 | 随机推荐 |
| POST | /api/v1/articles | 是 | 发布文章 |
| PUT | /api/v1/articles/{id} | 是 | 编辑文章 |
| DELETE | /api/v1/articles/{id} | 是 | 删除文章 |
| GET | /api/v1/tags | 否 | 标签列表（含文章数） |
| GET | /api/v1/articles/{id}/comments | 否 | 获取评论 |
| POST | /api/v1/articles/{id}/comments | 否 | 发表评论 |
| DELETE | /api/v1/comments/{id} | 是 | 删除评论 |
| POST | /api/v1/files/upload | 是 | 上传文件 |
| GET | /api/v1/files/{filename} | 否 | 获取文件 |

---

## 7. AI 自动标签流程

### 7.1 流程描述

1. 管理员在发布文章页面填写/上传内容，点击发布
2. 后端 ArticleService 接收文章内容
3. 从正文中提取前 80 个字符作为摘要
4. 调用 AiTagService，向 DeepSeek v4 Flash API 发送请求
5. DeepSeek 返回 JSON 格式标签列表
6. 解析标签，在 tb_tag 表中查找或创建标签
7. 建立 tb_article_tag 关联
8. 保存文章并返回

### 7.2 DeepSeek API 调用配置

```java
// AiConfig.java
@Configuration
public class AiConfig {
    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
}
```

### 7.3 Prompt 设计

```
系统提示:
你是一个技术博客标签分类器。根据文章开头内容，判断这篇文章
涉及哪些编程语言、框架、工具或技术领域。
只返回 JSON 格式：{"tags": ["标签1", "标签2", ...]}
每个标签不超过 20 个字符，返回 1~5 个标签。
标签示例：Java, Python, Vue, Spring Boot, MySQL, Docker, 机器学习, 前端

用户输入:
{文章前80字}
```

---

## 8. 错误处理与响应规范

### 8.1 统一响应体

```json
// 成功
{ "code": 200, "msg": "success", "data": { ... } }

// 失败
{ "code": 400, "msg": "参数错误", "data": null }

// 未认证
{ "code": 401, "msg": "未登录或 Token 已过期", "data": null }

// 无权限
{ "code": 403, "msg": "无权限访问", "data": null }

// 未找到
{ "code": 404, "msg": "资源不存在", "data": null }

// 服务器错误
{ "code": 500, "msg": "服务器内部错误", "data": null }
```

### 8.2 分页响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [ ... ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 8.3 全局异常处理

通过 `@ControllerAdvice` 统一处理：
- `MethodArgumentNotValidException` → 400 参数校验失败
- `AccessDeniedException` → 403 无权限
- `UsernameNotFoundException` / `BadCredentialsException` → 401 登录失败
- `EntityNotFoundException` → 404 资源不存在
- `Exception` → 500 未知错误

---

## 9. 项目目录结构

```
boke/
├── frontend/                    # Vue 3 前端项目
│   ├── src/
│   ├── public/
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── backend/                     # Spring Boot 后端项目
│   ├── src/main/java/com/example/blog/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-dev.yml
│   └── pom.xml
├── uploads/                     # 上传文件存储
│   ├── markdown/
│   └── notebook/
├── docs/                        # 文档
│   ├── spec.md                  # 本设计文档
│   ├── db-schema.md             # 数据库说明
│   └── user-guide.md            # 用户操作说明
└── README.md                    # 项目说明
```

---

## 10. 开发规范

### 10.1 代码规范

- 后端严格遵循 Controller → Service → Repository → Entity 分层
- 前端组件采用组合式 API（Composition API）+ `<script setup>`
- 所有中文注释：类注释、方法注释、关键逻辑注释
- 接口文档使用 SpringDoc 注解自动生成

### 10.2 命名规范

- 数据库表：`tb_` 前缀，小写下划线
- Java 类：PascalCase（ArticleService）
- Java 方法：camelCase（createArticle）
- 前端组件：PascalCase（ArticleCard.vue）
- API 路径：小写 kebab-case（/api/v1/article-comments）

### 10.3 提交规范

- feat: 新功能
- fix: 修复
- docs: 文档
- refactor: 重构
- style: 样式
- chore: 工具/配置

---

## 11. 实施路线图

### 第一阶段：项目初始化与基础架构（预计 1 天）
- 初始化 Spring Boot 项目（pom.xml + application.yml）
- 初始化 Vue 3 项目（vite + element-plus + router + pinia）
- 配置跨域、Spring Security、JWT
- 设计数据库表并自动建表

### 第二阶段：认证模块（预计 0.5 天）
- 后端：登录接口、JWT 生成与校验
- 前端：登录页、路由守卫、Token 管理

### 第三阶段：文章模块（预计 1.5 天）
- 后端：文章 CRUD、分页、浏览计数、随机推荐
- 前端：文章列表、文章详情（Markdown/Notebook 渲染）、发布编辑页

### 第四阶段：标签与 AI 模块（预计 1 天）
- 后端：标签 CRUD、DeepSeek API 集成
- 前端：标签筛选、AI 标签展示

### 第五阶段：评论模块（预计 0.5 天）
- 后端：评论 CRUD、嵌套回复
- 前端：评论展示、评论输入框

### 第六阶段：管理后台（预计 1 天）
- 仪表盘统计
- 文章管理列表
- 评论管理
- 标签管理

### 第七阶段：完善与文档（预计 0.5 天）
- 代码注释补充
- README + 功能说明文档
- 数据库结构说明
- 截图与最终提交

---

## 12. 附录

### 12.1 application.yml 配置

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update  # 自动建表
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

deepseek:
  api:
    key: your-api-key-here
    url: https://api.deepseek.com/v1/chat/completions

jwt:
  secret: your-secret-key-at-least-256-bits-long
  expiration: 86400000  # 24小时
```

### 12.2 vite.config.js 配置

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```
