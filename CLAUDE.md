# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

前后端分离的个人技术博客系统。支持 Markdown / Jupyter Notebook 发布、AI 自动标签、评论互动与文章举报。

## 技术栈

- **前端**: Vue 3 (Composition API) + Vite 5 + Vue Router 4 + Pinia + Element Plus + Axios + marked.js + highlight.js
- **后端**: Spring Boot 3.2 + Spring Data JPA + Spring Security + MySQL 8 + jjwt 0.12 + SpringDoc OpenAPI
- **AI**: DeepSeek v4 Flash API（文章自动标签）
- **构建**: Maven (后端) / npm (前端)
- **Java 版本**: 21

## 项目结构

```
backend/                          # Spring Boot 后端
├── pom.xml
└── src/main/java/com/example/blog/
    ├── BlogApplication.java      # 启动类
    ├── config/                   # 安全、跨域、OpenAPI、MVC、定时任务
    ├── common/                   # 统一响应体(Result/PageResult)、全局异常处理、常量
    ├── auth/                     # 认证模块：JWT、Security、用户(CRUD)
    ├── article/                  # 文章模块：实体、仓库、服务、DTO、控制器
    ├── tag/                      # 标签模块：Tag、ArticleTag 多对多关联
    ├── comment/                  # 评论模块：支持嵌套回复
    ├── file/                     # 文件上传：Markdown/Notebook 文件存储
    ├── ai/                       # DeepSeek API 集成（自动标签生成）
    ├── captcha/                  # 图片验证码（自实现，无第三方依赖）
    └── report/                   # 文章举报模块

frontend/                         # Vue 3 前端 SPA
├── package.json
├── vite.config.js                # 代理 /api → localhost:8080
└── src/
    ├── main.js                   # 入口，注册 Element Plus + Pinia + Router
    ├── App.vue                   # 根组件
    ├── router/index.js           # 路由配置 + 守卫
    ├── layouts/                  # BlogLayout（前台）、AdminLayout（后台）
    ├── views/blog/               # 前台页面：首页、文章详情、标签筛选、登录、注册
    ├── views/admin/              # 后台页面：仪表盘、文章管理、发布、标签、评论、举报、设置
    ├── components/common/        # 通用：文章卡片、标签、评论列表、分页、验证码
    ├── components/markdown/      # Markdown 渲染（marked + highlight.js）
    ├── components/notebook/      # Jupyter Notebook 渲染
    ├── stores/                   # Pinia 状态：auth、app
    ├── api/                      # Axios 封装 + API 函数（request、auth、article、comment、tag、file）
    └── utils/                    # format（日期/阅读量）、parseNotebook（.ipynb 解析）
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `tb_user` | 用户，启动时自动创建 admin/admin123 |
| `tb_article` | 文章，支持定时发布/自动转私人 |
| `tb_tag` | 标签 |
| `tb_article_tag` | 文章-标签关联 |
| `tb_comment` | 评论，通过 parent_id 支持嵌套回复 |
| `tb_report` | 文章举报 |

## 常用命令

### 后端
```bash
cd backend
mvn compile           # 编译
mvn spring-boot:run   # 启动（需 MySQL，默认 root/root）
```

### 前端
```bash
cd frontend
npm install            # 安装依赖
npm run dev            # 开发模式（localhost:5173，自动代理 /api → 8080）
npm run build          # 生产构建
```

## 关键配置

- **数据库**: `backend/src/main/resources/application.yml` — 默认连接 `localhost:3306/blog_db`，用户名/密码 `root/root`
- **JWT**: 密钥 `jwt.secret`，过期时间 24h
- **DeepSeek**: 需在 `application.yml` 中配置 `deepseek.api.key`
- **默认管理员**: 应用启动时自动创建，账号 `admin` / 密码 `admin123`

## API 路由约定

- **公开**: `GET /api/v1/articles/**`、`GET /api/v1/tags/**`、`POST /api/v1/auth/login`、`POST /api/v1/auth/register`、验证码、举报提交
- **需登录**: 文章创建/编辑/删除、评论管理、举报管理、个人设置
- **管理员**: `/api/v1/admin/**` 需要 `ROLE_ADMIN` 权限
- **前缀**: 前端开发时 `/api/v1/...` 由 Vite 代理到后端 8080 端口

## 设计要点

- **文章类型**: contentType 字段区分 `MARKDOWN` 和 `NOTEBOOK`（.ipynb JSON），前端使用不同渲染器
- **AI 标签**: 创建文章时自动调用 DeepSeek API 分析摘要生成标签，失败不影响发布
- **验证码**: 自实现图片验证码（AWT），存储答案到内存 ConcurrentHashMap，5分钟过期
- **定时发布**: ScheduledTaskConfig 每分钟检查待发布的定时文章
- **评论嵌套**: 后端递归构建评论树，前端根据 depth 缩进展示
- **统一响应**: 所有 API 返回 `Result<T>` 格式：`{ code, msg, data }`
