# 数据库结构说明

> 数据库：MySQL 8.x  
> 数据库名：`blog_db`  
> 字符集：`utf8mb4` / `utf8mb4_unicode_ci`  
> JPA ddl-auto: `update`（自动建表）

---

## 表关系概览

```
tb_user  ──1:N──>  tb_article  ──N:M──>  tb_tag
                        │                  ↑
                        │               tb_article_tag（关联表）
                        │
                        └──1:N──>  tb_comment
```

---

## 1. tb_user — 用户表

存储管理员和普通用户信息（当前仅管理员角色生效）。

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

**初始化数据：** 应用启动时自动创建默认管理员（admin / admin123）

---

## 2. tb_article — 文章表

核心业务表，存储所有博客文章。

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 文章 ID |
| user_id | BIGINT | FK → tb_user.id | 发布者 ID |
| title | VARCHAR(200) | NOT NULL | 文章标题 |
| summary | VARCHAR(500) | NULLABLE | 摘要（自动取前80字） |
| content | LONGTEXT | NOT NULL | 文章全文 |
| content_type | VARCHAR(20) | NOT NULL | MARKDOWN / NOTEBOOK |
| source_file | VARCHAR(255) | NULLABLE | 上传原始文件路径 |
| cover_image | VARCHAR(255) | NULLABLE | 封面图 |
| view_count | INT | DEFAULT 0 | 浏览次数 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PUBLISHED' | DRAFT / PUBLISHED |
| create_time | DATETIME | NOT NULL | 发布时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

**索引：** `(create_time DESC)`, `(status)`, `(user_id)`

---

## 3. tb_tag — 标签表

由 AI 自动生成或管理员手动创建的标签。

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 标签 ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 标签名 |
| category | VARCHAR(50) | NULLABLE | 分类 |
| create_time | DATETIME | NOT NULL | 创建时间 |

**标签分类：** LANGUAGE / FRAMEWORK / TOOL / DATABASE / OTHER

---

## 4. tb_article_tag — 文章-标签关联表

实现文章与标签的多对多关系。

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 关联 ID |
| article_id | BIGINT | FK → tb_article.id | 文章 ID |
| tag_id | BIGINT | FK → tb_tag.id | 标签 ID |

**唯一约束：** `(article_id, tag_id)`

---

## 5. tb_comment — 评论表

访客对文章的评论，支持嵌套回复。

| 字段 | 类型 | 约束 | 说明 |
|:--|:--|:--|:--|
| id | BIGINT | PK, AUTO_INCREMENT | 评论 ID |
| article_id | BIGINT | FK → tb_article.id | 所属文章 |
| parent_id | BIGINT | FK → tb_comment.id, NULLABLE | 父评论（嵌套回复） |
| nickname | VARCHAR(50) | NOT NULL | 评论者昵称 |
| email | VARCHAR(100) | NULLABLE | 评论者邮箱 |
| content | TEXT | NOT NULL | 评论内容 |
| create_time | DATETIME | NOT NULL | 评论时间 |

**索引：** `(article_id)`
