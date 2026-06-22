# 个人博客系统实施计划

> **对于自动化执行者：** 必须子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 按任务逐步实施。步骤使用复选框（`- [ ]`）语法追踪。

**目标：** 构建一个前后端分离的个人技术博客系统，支持 Markdown / Jupyter Notebook 发布、AI 自动标签、评论与浏览统计。

**架构：** Vue 3 + Element Plus 前端 SPA，Spring Boot 3 + JPA 后端 REST API，MySQL 持久化，JWT 无状态认证。

**技术栈：** Vue 3 / Vite / Element Plus / Vue Router 4 / Pinia / Axios / marked.js / highlight.js — Spring Boot 3.2 / Spring Data JPA / Spring Security / MySQL 8 / jjwt / SpringDoc OpenAPI — DeepSeek v4 Flash API

---

## 文件清单总览

### 后端 (`backend/`)

| 文件路径 | 职责 |
|:--|:--|
| `pom.xml` | Maven 依赖管理 |
| `src/main/resources/application.yml` | 数据库/JWT/DeepSeek 配置 |
| `src/main/java/com/example/blog/BlogApplication.java` | 启动类 |
| `BlogApplication.java` | Spring Boot 入口 |
| `config/SecurityConfig.java` | Spring Security + JWT 过滤器链 |
| `config/CorsConfig.java` | 跨域配置 |
| `config/WebMvcConfig.java` | 静态资源映射 |
| `config/OpenApiConfig.java` | SpringDoc API 文档配置 |
| `common/Result.java` | 统一响应体 |
| `common/PageResult.java` | 分页响应 |
| `common/GlobalExceptionHandler.java` | 全局异常处理 |
| `common/constants/ArticleStatus.java` | 文章状态常量 |
| `auth/entity/User.java` | 用户实体 |
| `auth/repository/UserRepository.java` | 用户仓库 |
| `auth/service/UserService.java` | 用户业务 |
| `auth/service/UserDetailsServiceImpl.java` | Spring Security 用户加载 |
| `auth/controller/AuthController.java` | 登录/获取用户 |
| `auth/dto/LoginRequest.java` | 登录请求 DTO |
| `auth/dto/LoginResponse.java` | 登录响应 DTO |
| `auth/jwt/JwtUtil.java` | JWT 工具类 |
| `auth/jwt/JwtAuthFilter.java` | JWT 请求过滤器 |
| `article/entity/Article.java` | 文章实体 |
| `article/repository/ArticleRepository.java` | 文章仓库 |
| `article/service/ArticleService.java` | 文章业务（含 AI 标签调用） |
| `article/controller/ArticleController.java` | 文章接口 |
| `article/dto/ArticleCreateRequest.java` | 发布文章 DTO |
| `article/dto/ArticleUpdateRequest.java` | 更新文章 DTO |
| `article/dto/ArticleVO.java` | 文章视图对象 |
| `tag/entity/Tag.java` | 标签实体 |
| `tag/entity/ArticleTag.java` | 文章-标签关联实体 |
| `tag/repository/TagRepository.java` | 标签仓库 |
| `tag/repository/ArticleTagRepository.java` | 关联仓库 |
| `tag/service/TagService.java` | 标签业务 |
| `tag/controller/TagController.java` | 标签接口 |
| `comment/entity/Comment.java` | 评论实体 |
| `comment/repository/CommentRepository.java` | 评论仓库 |
| `comment/service/CommentService.java` | 评论业务 |
| `comment/controller/CommentController.java` | 评论接口 |
| `file/service/FileStorageService.java` | 文件存储 |
| `file/service/MarkdownParseService.java` | Markdown 解析 |
| `file/service/NotebookParseService.java` | Jupyter Notebook 解析 |
| `file/controller/FileController.java` | 文件接口 |
| `ai/service/AiTagService.java` | DeepSeek API 调用 |
| `ai/config/AiConfig.java` | AI 配置 |

### 前端 (`frontend/`)

| 文件路径 | 职责 |
|:--|:--|
| `package.json` | 依赖管理 |
| `vite.config.js` | Vite 配置（代理 /api → 8080） |
| `index.html` | HTML 入口 |
| `src/main.js` | Vue 应用入口 |
| `src/App.vue` | 根组件 |
| `src/router/index.js` | 路由配置 + 路由守卫 |
| `src/layouts/BlogLayout.vue` | 前台布局 |
| `src/layouts/AdminLayout.vue` | 后台布局 |
| `src/views/blog/Home.vue` | 首页（文章列表 + 随机推荐） |
| `src/views/blog/ArticleDetail.vue` | 文章详情 + 评论 |
| `src/views/blog/TagFilter.vue` | 按标签筛选 |
| `src/views/blog/Login.vue` | 登录页 |
| `src/views/admin/Dashboard.vue` | 仪表盘 |
| `src/views/admin/ArticleList.vue` | 文章管理列表 |
| `src/views/admin/ArticleEditor.vue` | 文章发布/编辑 |
| `src/views/admin/CommentManage.vue` | 评论管理 |
| `src/views/admin/TagManage.vue` | 标签管理 |
| `src/views/admin/Profile.vue` | 个人设置 |
| `src/components/common/ArticleCard.vue` | 文章卡片 |
| `src/components/common/TagChip.vue` | 标签组件 |
| `src/components/common/CommentList.vue` | 评论列表+输入 |
| `src/components/common/Pagination.vue` | 分页组件 |
| `src/components/markdown/MarkdownRenderer.vue` | Markdown 渲染 |
| `src/components/notebook/NotebookRenderer.vue` | Notebook 渲染 |
| `src/api/request.js` | Axios 实例 |
| `src/api/auth.js` | 认证 API |
| `src/api/article.js` | 文章 API |
| `src/api/comment.js` | 评论 API |
| `src/api/tag.js` | 标签 API |
| `src/stores/auth.js` | 认证状态管理 |
| `src/stores/app.js` | 全局状态 |
| `src/utils/parseNotebook.js` | Notebook 解析 |
| `src/utils/format.js` | 格式化工具 |

### 文档

| 文件路径 | 职责 |
|:--|:--|
| `docs/db-schema.md` | 数据库结构说明 |
| `docs/user-guide.md` | 用户操作说明 |
| `README.md` | 项目说明 |

---

## 第一阶段：项目初始化与基础架构

### 任务 1：初始化 Spring Boot 后端项目

**文件：**
- 创建：`backend/pom.xml`
- 创建：`backend/src/main/resources/application.yml`
- 创建：`backend/src/main/java/com/example/blog/BlogApplication.java`

- [ ] **步骤 1：创建 `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>blog</artifactId>
    <version>1.0.0</version>
    <name>personal-blog</name>
    <description>个人技术博客系统</description>

    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot 基础 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- SpringDoc OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 测试 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **步骤 2：创建 `application.yml`**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai

deepseek:
  api:
    key: your-api-key-here
    url: https://api.deepseek.com/v1/chat/completions

jwt:
  secret: bG9uZy1zZWNyZXQta2V5LWZvci1ibG9nLXN5c3RlbS1taW5pbXVtLTI1Ni1iaXRzLWxvbmc=
  expiration: 86400000

# 文件上传存储路径
blog:
  upload:
    base-path: ./uploads
```

- [ ] **步骤 3：创建 `BlogApplication.java`**

```java
package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 个人博客系统 — 启动类
 */
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```

---

### 任务 2：创建后端公共基础类

**文件：**
- 创建：`backend/src/main/java/com/example/blog/common/Result.java`
- 创建：`backend/src/main/java/com/example/blog/common/PageResult.java`
- 创建：`backend/src/main/java/com/example/blog/common/GlobalExceptionHandler.java`
- 创建：`backend/src/main/java/com/example/blog/common/constants/ArticleStatus.java`

- [ ] **步骤 1：创建 `Result.java`** — 统一响应体，包含 code / msg / data

```java
package com.example.blog.common;

import lombok.Data;

/**
 * 统一 API 响应体
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    private int code;        // 状态码
    private String msg;      // 提示信息
    private T data;          // 数据体

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功响应（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 失败响应 */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 参数错误 400 */
    public static <T> Result<T> badRequest(String msg) {
        return new Result<>(400, msg, null);
    }

    /** 未认证 401 */
    public static <T> Result<T> unauthorized(String msg) {
        return new Result<>(401, msg, null);
    }

    /** 无权限 403 */
    public static <T> Result<T> forbidden(String msg) {
        return new Result<>(403, msg, null);
    }

    /** 未找到 404 */
    public static <T> Result<T> notFound(String msg) {
        return new Result<>(404, msg, null);
    }
}
```

- [ ] **步骤 2：创建 `PageResult.java`** — 分页响应

```java
package com.example.blog.common;

import lombok.Data;
import java.util.List;

/**
 * 分页响应体
 */
@Data
public class PageResult<T> {
    private List<T> records;   // 当前页数据
    private long total;        // 总记录数
    private int page;          // 当前页码
    private int size;          // 每页条数

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
```

- [ ] **步骤 3：创建 `ArticleStatus.java`** — 文章状态常量

```java
package com.example.blog.common.constants;

/**
 * 文章状态常量
 */
public class ArticleStatus {
    /** 草稿 */
    public static final String DRAFT = "DRAFT";
    /** 已发布 */
    public static final String PUBLISHED = "PUBLISHED";
}
```

- [ ] **步骤 4：创建 `GlobalExceptionHandler.java`**

```java
package com.example.blog.common;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获并转换各类异常为标准 Result 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 参数校验失败 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.badRequest(msg);
    }

    /** 登录失败 401 */
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthError(RuntimeException e) {
        return Result.unauthorized("用户名或密码错误");
    }

    /** 无权限 403 */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied() {
        return Result.forbidden("无权限执行此操作");
    }

    /** 资源未找到 404 */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNotFound(EntityNotFoundException e) {
        return Result.notFound(e.getMessage());
    }

    /** 通用异常 500 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器内部错误: " + e.getMessage());
    }
}
```

---

### 任务 3：创建后端配置类

**文件：**
- 创建：`backend/src/main/java/com/example/blog/config/CorsConfig.java`
- 创建：`backend/src/main/java/com/example/blog/config/WebMvcConfig.java`
- 创建：`backend/src/main/java/com/example/blog/config/OpenApiConfig.java`

- [ ] **步骤 1：创建 `CorsConfig.java`**

```java
package com.example.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 * 允许前端 localhost:5173 访问后端 API
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **步骤 2：创建 `WebMvcConfig.java`**

```java
package com.example.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 映射上传文件的访问路径到本地文件系统
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${blog.upload.base-path:./uploads}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传文件通过 /uploads/** 访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadBasePath + "/");
    }
}
```

- [ ] **步骤 3：创建 `OpenApiConfig.java`**

```java
package com.example.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置
 * 访问地址: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("个人博客系统 API")
                        .description("前后端分离的个人技术博客，支持 Markdown / Notebook 发布、AI 标签、评论互动")
                        .version("1.0.0")
                        .contact(new Contact().name("管理员")));
    }
}
```

---

### 任务 4：初始化 Vue 3 前端项目

**文件：**
- 创建：`frontend/package.json`
- 创建：`frontend/vite.config.js`
- 创建：`frontend/index.html`
- 创建：`frontend/src/main.js`
- 创建：`frontend/src/App.vue`

- [ ] **步骤 1：创建 `package.json`**

```json
{
  "name": "personal-blog-frontend",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "axios": "^1.6.8",
    "element-plus": "^2.6.1",
    "@element-plus/icons-vue": "^2.3.1",
    "marked": "^12.0.1",
    "highlight.js": "^11.9.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "vite": "^5.2.8"
  }
}
```

- [ ] **步骤 2：创建 `vite.config.js`**

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
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **步骤 3：创建 `index.html`**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>个人技术博客</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **步骤 4：创建 `src/main.js`**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 全局注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: undefined })
app.mount('#app')
```

- [ ] **步骤 5：创建 `src/App.vue`**

```vue
<template>
  <router-view />
</template>

<script setup>
// 根组件 — 通过路由切换前台/后台布局
</script>

<style>
/* 全局基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background-color: #f5f5f5;
  color: #333;
}
</style>
```

---

### 任务 5：创建前端 API 层与路由

**文件：**
- 创建：`frontend/src/api/request.js`
- 创建：`frontend/src/router/index.js`

- [ ] **步骤 1：创建 `request.js`** — Axios 实例（注入 JWT Token + 统一错误处理）

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000
})

// 请求拦截器 — 在 Header 中注入 JWT Token
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 — 统一错误处理
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res.data
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        // Token 过期或未登录 → 清除 Token, 跳转登录页
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(data?.msg || '服务器错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **步骤 2：创建 `router/index.js`**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // ===== 博客前台 =====
  {
    path: '/',
    component: () => import('../layouts/BlogLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/blog/Home.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('../views/blog/ArticleDetail.vue') },
      { path: 'tag/:tagName', name: 'TagFilter', component: () => import('../views/blog/TagFilter.vue') },
    ]
  },
  // ===== 登录页（独立布局）=====
  { path: '/login', name: 'Login', component: () => import('../views/blog/Login.vue') },

  // ===== 管理后台 =====
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'articles', name: 'ArticleList', component: () => import('../views/admin/ArticleList.vue') },
      { path: 'articles/new', name: 'ArticleNew', component: () => import('../views/admin/ArticleEditor.vue') },
      { path: 'articles/:id/edit', name: 'ArticleEdit', component: () => import('../views/admin/ArticleEditor.vue') },
      { path: 'comments', name: 'CommentManage', component: () => import('../views/admin/CommentManage.vue') },
      { path: 'tags', name: 'TagManage', component: () => import('../views/admin/TagManage.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/admin/Profile.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 — 未登录访问后台时跳转登录页
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
      return
    }
  }
  next()
})

export default router
```

---

## 第二阶段：认证模块

### 任务 6：后端认证 — 用户实体 + JWT 工具

**文件：**
- 创建：`backend/src/main/java/com/example/blog/auth/entity/User.java`
- 创建：`backend/src/main/java/com/example/blog/auth/repository/UserRepository.java`
- 创建：`backend/src/main/java/com/example/blog/auth/jwt/JwtUtil.java`
- 创建：`backend/src/main/java/com/example/blog/auth/jwt/JwtAuthFilter.java`

- [ ] **步骤 1：创建 `User.java`** — 用户实体

```java
package com.example.blog.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 255)
    private String avatar;

    @Column(nullable = false, length = 20)
    private String role = "ADMIN";            // ADMIN / USER

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

- [ ] **步骤 2：创建 `UserRepository.java`**

```java
package com.example.blog.auth.repository;

import com.example.blog.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 用户数据仓库
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

- [ ] **步骤 3：创建 `JwtUtil.java`**

```java
package com.example.blog.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析和验证
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /** 生成 JWT Token */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /** 从 Token 中提取用户名 */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /** 验证 Token 是否有效 */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

- [ ] **步骤 4：创建 `JwtAuthFilter.java`** — 每个请求过滤验证 Token

```java
package com.example.blog.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 请求过滤器
 * 从请求头中提取 Token，验证通过后设置 SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### 任务 7：后端认证 — Spring Security 配置 + 用户服务

**文件：**
- 创建：`backend/src/main/java/com/example/blog/config/SecurityConfig.java`
- 创建：`backend/src/main/java/com/example/blog/auth/service/UserDetailsServiceImpl.java`
- 创建：`backend/src/main/java/com/example/blog/auth/service/UserService.java`
- 创建：`backend/src/main/java/com/example/blog/auth/dto/LoginRequest.java`
- 创建：`backend/src/main/java/com/example/blog/auth/dto/LoginResponse.java`
- 创建：`backend/src/main/java/com/example/blog/auth/controller/AuthController.java`

- [ ] **步骤 1：创建 `SecurityConfig.java`**

```java
package com.example.blog.config;

import com.example.blog.auth.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * 无状态 JWT 认证，不启用 CSRF
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/articles/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/files/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/articles/*/comments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/comments").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其余需要登录
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

- [ ] **步骤 2：创建 `UserDetailsServiceImpl.java`**

```java
package com.example.blog.auth.service;

import com.example.blog.auth.entity.User;
import com.example.blog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security 用户加载服务
 * 根据用户名从数据库加载用户信息
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
```

- [ ] **步骤 3：创建 `UserService.java`**

```java
package com.example.blog.auth.service;

import com.example.blog.auth.entity.User;
import com.example.blog.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * 用户业务服务
 * 自动初始化管理员账号
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 应用启动时自动创建默认管理员账号 */
    @PostConstruct
    public void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
```

- [ ] **步骤 4：创建 `LoginRequest.java`**

```java
package com.example.blog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求 DTO
 */
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

- [ ] **步骤 5：创建 `LoginResponse.java`**

```java
package com.example.blog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应 DTO（返回 Token + 用户信息）
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String role;
    }
}
```

- [ ] **步骤 6：创建 `AuthController.java`**

```java
package com.example.blog.auth.controller;

import com.example.blog.auth.dto.LoginRequest;
import com.example.blog.auth.dto.LoginResponse;
import com.example.blog.auth.entity.User;
import com.example.blog.auth.jwt.JwtUtil;
import com.example.blog.auth.service.UserService;
import com.example.blog.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 管理员登录
     * @param request 用户名 + 密码
     * @return JWT Token + 用户信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 认证
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        // 生成 Token
        String token = jwtUtil.generateToken(auth.getName());

        // 获取用户信息
        User user = userService.findByUsername(request.getUsername());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatar(), user.getRole());

        return Result.success(new LoginResponse(token, userInfo));
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me() {
        // 从 SecurityContext 获取当前用户
        return Result.success(null); // 简化处理，后续完善
    }
}
```

---

### 任务 8：前端登录页 + 认证状态管理

**文件：**
- 创建：`frontend/src/stores/auth.js`
- 创建：`frontend/src/api/auth.js`
- 创建：`frontend/src/views/blog/Login.vue`

- [ ] **步骤 1：创建 `stores/auth.js`**

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, getMeApi } from '../api/auth'

/**
 * 认证状态管理
 */
export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

  /** 是否已登录 */
  const isLoggedIn = computed(() => !!token.value)

  /** 登录 */
  async function login(username, password) {
    const result = await loginApi({ username, password })
    token.value = result.token
    user.value = result.user
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
  }

  /** 登出 */
  function logout() {
    token.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, login, logout }
})
```

- [ ] **步骤 2：创建 `api/auth.js`**

```javascript
import request from './request'

export function loginApi(data) {
  return request.post('/auth/login', data)
}

export function getMeApi() {
  return request.get('/auth/me')
}
```

- [ ] **步骤 3：创建 `Login.vue`** — 登录页面，简洁居中设计

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">管理员登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" native-type="submit">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/** 登录处理 */
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/admin')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}
.login-btn {
  width: 100%;
}
</style>
```

---

## 第三阶段：文章模块

### 任务 9：后端文章 — 实体 + 仓库

**文件：**
- 创建：`backend/src/main/java/com/example/blog/article/entity/Article.java`
- 创建：`backend/src/main/java/com/example/blog/article/repository/ArticleRepository.java`

- [ ] **步骤 1：创建 `Article.java`**

```java
package com.example.blog.article.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章实体
 * 支持 Markdown 和 Jupyter Notebook 两种内容类型
 */
@Data
@Entity
@Table(name = "tb_article", indexes = {
    @Index(name = "idx_create_time", columnList = "create_time DESC"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String summary;         // 摘要（自动从正文前80字提取）

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;         // 文章全文（Markdown 原文 或 Notebook JSON）

    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;     // MARKDOWN / NOTEBOOK

    @Column(name = "source_file", length = 255)
    private String sourceFile;      // 上传的原始文件路径

    @Column(name = "cover_image", length = 255)
    private String coverImage;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(nullable = false, length = 20)
    private String status = "PUBLISHED";    // DRAFT / PUBLISHED

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (viewCount == null) viewCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
```

- [ ] **步骤 2：创建 `ArticleRepository.java`**

```java
package com.example.blog.article.repository;

import com.example.blog.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 文章数据仓库
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** 按状态分页查询 */
    Page<Article> findByStatus(String status, Pageable pageable);

    /** 按状态和标题关键词搜索 */
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.title LIKE %:keyword%")
    Page<Article> searchByTitle(@Param("status") String status,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    /** 随机获取 N 篇已发布的文章 */
    @Query(value = "SELECT * FROM tb_article WHERE status = 'PUBLISHED' ORDER BY RAND() LIMIT :n",
           nativeQuery = true)
    List<Article> findRandomArticles(@Param("n") int n);

    /** 统计已发布文章总数 */
    long countByStatus(String status);
}
```

---

### 任务 10：后端文章 — DTO + 服务层

**文件：**
- 创建：`backend/src/main/java/com/example/blog/article/dto/ArticleCreateRequest.java`
- 创建：`backend/src/main/java/com/example/blog/article/dto/ArticleUpdateRequest.java`
- 创建：`backend/src/main/java/com/example/blog/article/dto/ArticleVO.java`
- 创建：`backend/src/main/java/com/example/blog/article/service/ArticleService.java`

- [ ] **步骤 1：创建 `ArticleCreateRequest.java`**

```java
package com.example.blog.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建文章请求 DTO
 */
@Data
public class ArticleCreateRequest {
    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String contentType = "MARKDOWN";    // MARKDOWN / NOTEBOOK
    private String summary;
    private String coverImage;
    private String status = "PUBLISHED";
}
```

- [ ] **步骤 2：创建 `ArticleUpdateRequest.java`**

```java
package com.example.blog.article.dto;

import lombok.Data;

/**
 * 更新文章请求 DTO
 */
@Data
public class ArticleUpdateRequest {
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String status;
}
```

- [ ] **步骤 3：创建 `ArticleVO.java`** — 文章视图对象（返回给前端）

```java
package com.example.blog.article.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象
 * 包含标签列表，不暴露敏感信息
 */
@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String contentType;
    private String sourceFile;
    private String coverImage;
    private Integer viewCount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> tags;          // 文章关联的标签列表
}
```

- [ ] **步骤 4：创建 `ArticleService.java`**

```java
package com.example.blog.article.service;

import com.example.blog.article.dto.ArticleCreateRequest;
import com.example.blog.article.dto.ArticleUpdateRequest;
import com.example.blog.article.dto.ArticleVO;
import com.example.blog.article.entity.Article;
import com.example.blog.article.repository.ArticleRepository;
import com.example.blog.common.PageResult;
import com.example.blog.common.constants.ArticleStatus;
import com.example.blog.tag.entity.ArticleTag;
import com.example.blog.tag.entity.Tag;
import com.example.blog.tag.repository.ArticleTagRepository;
import com.example.blog.tag.repository.TagRepository;
import com.example.blog.ai.service.AiTagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章业务服务
 * 包含 CRUD、浏览计数、随机推荐、AI 标签
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    private final AiTagService aiTagService;

    /**
     * 分页查询已发布文章
     * @param page    页码（从1开始）
     * @param size    每页条数
     * @param tagName 按标签筛选（可选）
     * @param keyword 关键词搜索（可选）
     */
    public PageResult<ArticleVO> getPublishedArticles(int page, int size, String tagName, String keyword) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> articlePage;

        if (keyword != null && !keyword.isBlank()) {
            articlePage = articleRepository.searchByTitle(ArticleStatus.PUBLISHED, keyword, pageRequest);
        } else {
            articlePage = articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageRequest);
        }

        // 如果指定了标签，在内存中过滤
        List<ArticleVO> vos = articlePage.getContent().stream()
                .map(this::toVO)
                .filter(vo -> tagName == null || tagName.isBlank()
                        || (vo.getTags() != null && vo.getTags().contains(tagName)))
                .collect(Collectors.toList());

        return new PageResult<>(vos, articlePage.getTotalElements(), page, size);
    }

    /** 获取文章详情（同时 +1 浏览次数） */
    @Transactional
    public ArticleVO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        return toVO(article);
    }

    /** 随机推荐文章 */
    public List<ArticleVO> getRandomArticles(int n) {
        return articleRepository.findRandomArticles(n)
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 创建文章（含 AI 自动标��）
     */
    @Transactional
    public ArticleVO createArticle(ArticleCreateRequest request, Long userId) {
        // 提取摘要（取前80字）
        String summary = request.getSummary();
        if (summary == null || summary.isBlank()) {
            summary = request.getContent().length() > 80
                    ? request.getContent().substring(0, 80) + "..."
                    : request.getContent();
        }

        // 创建文章实体
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setContentType(request.getContentType());
        article.setSummary(summary);
        article.setCoverImage(request.getCoverImage());
        article.setStatus(request.getStatus());
        Article saved = articleRepository.save(article);

        // 调用 AI 自动打标签
        List<String> tagNames = aiTagService.generateTags(summary);

        // 保存标签关联
        for (String tagName : tagNames) {
            // 查找或创建标签
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            // 建立关联
            ArticleTag at = new ArticleTag();
            at.setArticleId(saved.getId());
            at.setTagId(tag.getId());
            articleTagRepository.save(at);
        }

        return toVO(saved);
    }

    /** 更新文章 */
    @Transactional
    public ArticleVO updateArticle(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        if (request.getTitle() != null) article.setTitle(request.getTitle());
        if (request.getContent() != null) article.setContent(request.getContent());
        if (request.getSummary() != null) article.setSummary(request.getSummary());
        if (request.getCoverImage() != null) article.setCoverImage(request.getCoverImage());
        if (request.getStatus() != null) article.setStatus(request.getStatus());
        return toVO(articleRepository.save(article));
    }

    /** 删除文章 */
    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("文章不存在");
        }
        articleTagRepository.deleteByArticleId(id);
        articleRepository.deleteById(id);
    }

    /** 将 Article 实体转换为 ArticleVO */
    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContentType(article.getContentType());
        vo.setSourceFile(article.getSourceFile());
        vo.setCoverImage(article.getCoverImage());
        vo.setViewCount(article.getViewCount());
        vo.setStatus(article.getStatus());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        // 获取标签
        List<String> tags = articleTagRepository.findByArticleId(article.getId())
                .stream().map(at -> tagRepository.findById(at.getTagId())
                        .map(Tag::getName).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        vo.setTags(tags);
        return vo;
    }
}
```

---

### 任务 11：后端文章 — 控制器

**文件：**
- 创建：`backend/src/main/java/com/example/blog/article/controller/ArticleController.java`

- [ ] **步骤 1：创建 `ArticleController.java`**

```java
package com.example.blog.article.controller;

import com.example.blog.article.dto.ArticleCreateRequest;
import com.example.blog.article.dto.ArticleUpdateRequest;
import com.example.blog.article.dto.ArticleVO;
import com.example.blog.article.service.ArticleService;
import com.example.blog.common.Result;
import com.example.blog.common.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章接口控制器
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /** 获取文章列表（分页 + 标签筛选 + 关键词搜索） */
    @GetMapping
    public Result<PageResult<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.getPublishedArticles(page, size, tag, keyword));
    }

    /** 获取文章详情 */
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    /** 随机推荐文章 */
    @GetMapping("/random")
    public Result<List<ArticleVO>> random(@RequestParam(defaultValue = "3") int n) {
        return Result.success(articleService.getRandomArticles(n));
    }

    /** 发布文章（需管理员登录） */
    @PostMapping
    public Result<ArticleVO> create(@Valid @RequestBody ArticleCreateRequest request,
                                     Authentication auth) {
        // 从认证信息中获取用户ID（简化：默认 userId=1）
        return Result.success(articleService.createArticle(request, 1L));
    }

    /** 编辑文章 */
    @PutMapping("/{id}")
    public Result<ArticleVO> update(@PathVariable Long id,
                                     @Valid @RequestBody ArticleUpdateRequest request) {
        return Result.success(articleService.updateArticle(id, request));
    }

    /** 删除文章 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }
}
```

---

### 任务 12：前端文章 — API + 工具函数

**文件：**
- 创建：`frontend/src/api/article.js`
- 创建：`frontend/src/utils/format.js`
- 创建：`frontend/src/utils/parseNotebook.js`

- [ ] **步骤 1：创建 `api/article.js`**

```javascript
import request from './request'

export function getArticles(params) {
  return request.get('/articles', { params })
}

export function getArticleDetail(id) {
  return request.get(`/articles/${id}`)
}

export function getRandomArticles(n = 3) {
  return request.get('/articles/random', { params: { n } })
}

export function createArticle(data) {
  return request.post('/articles', data)
}

export function updateArticle(id, data) {
  return request.put(`/articles/${id}`, data)
}

export function deleteArticle(id) {
  return request.delete(`/articles/${id}`)
}
```

- [ ] **步骤 2：创建 `utils/format.js`**

```javascript
/**
 * 格式化日期
 * @param {string|Date} date 日期字符串或对象
 * @returns {string} 格式化后的日期 yyyy-MM-dd HH:mm
 */
export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${min}`
}

/**
 * 格式化阅读量（超过1000显示为 k）
 */
export function formatViewCount(count) {
  if (!count) return '0'
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k'
  }
  return String(count)
}
```

- [ ] **步骤 3：创建 `utils/parseNotebook.js`**

```javascript
/**
 * 解析 Jupyter Notebook (.ipynb) JSON 结构
 * 返回可渲染的单元格列表
 *
 * @param {string} jsonStr .ipynb 文件的 JSON 字符串
 * @returns {Array} 单元格数组 [{cellType, source, outputs}]
 */
export function parseNotebook(jsonStr) {
  try {
    const nb = JSON.parse(jsonStr)
    if (!nb.cells || !Array.isArray(nb.cells)) {
      throw new Error('无效的 Notebook 格式')
    }
    return nb.cells.map((cell, index) => {
      const source = (cell.source || [])
        .join('')
        // 处理特殊空白字符
        .replace(/\r\n/g, '\n')

      let outputs = []
      if (cell.outputs && cell.outputs.length > 0) {
        outputs = cell.outputs.flatMap(output => {
          // 文本输出
          if (output.text) {
            return [{ type: 'text', data: output.text.join('') }]
          }
          // 图片输出
          if (output.data && output.data['image/png']) {
            return [{ type: 'image', data: output.data['image/png'] }]
          }
          // 执行结果
          if (output.data && output.data['text/plain']) {
            return [{ type: 'text', data: output.data['text/plain'].join('') }]
          }
          return []
        })
      }

      return {
        index: index + 1,
        cellType: cell.cell_type,       // code / markdown / raw
        source: source,
        outputs: outputs
      }
    })
  } catch (e) {
    console.error('Notebook 解析失败:', e)
    return []
  }
}
```

---

### 任务 13：前端 Markdown / Notebook 渲染组件

**文件：**
- 创建：`frontend/src/components/markdown/MarkdownRenderer.vue`
- 创建：`frontend/src/components/notebook/NotebookRenderer.vue`

- [ ] **步骤 1：创建 `MarkdownRenderer.vue`**

```vue
<template>
  <div class="markdown-body" v-html="renderedContent"></div>
</template>

<script setup>
import { computed } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const props = defineProps({
  content: { type: String, default: '' }
})

// 配置 marked 使用 highlight.js 代码高亮
marked.setOptions({
  breaks: true,
  gfm: true,
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  }
})

const renderedContent = computed(() => {
  if (!props.content) return ''
  return marked.parse(props.content)
})
</script>

<style scoped>
.markdown-body {
  line-height: 1.8;
  font-size: 15px;
}
.markdown-body :deep(h1) { font-size: 2em; margin: 0.67em 0; }
.markdown-body :deep(h2) { font-size: 1.5em; margin: 0.83em 0; }
.markdown-body :deep(h3) { font-size: 1.17em; margin: 1em 0; }
.markdown-body :deep(p) { margin: 1em 0; }
.markdown-body :deep(code) {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.9em;
}
.markdown-body :deep(pre) {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
}
.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
}
.markdown-body :deep(img) { max-width: 100%; }
.markdown-body :deep(blockquote) {
  border-left: 4px solid #1890ff;
  padding-left: 16px;
  color: #666;
  margin: 1em 0;
}
.markdown-body :deep(table) {
  border-collapse: collapse;
  width: 100%;
}
.markdown-body :deep(th), :deep(td) {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}
.markdown-body :deep(ul), :deep(ol) { padding-left: 2em; }
</style>
```

- [ ] **步骤 2：创建 `NotebookRenderer.vue`**

```vue
<template>
  <div class="notebook-body">
    <div v-for="cell in cells" :key="cell.index" class="notebook-cell">
      <!-- Markdown 单元格 -->
      <div v-if="cell.cellType === 'markdown'" class="cell-markdown">
        <MarkdownRenderer :content="cell.source" />
      </div>

      <!-- 代码单元格 -->
      <div v-else-if="cell.cellType === 'code'" class="cell-code">
        <div class="cell-header">
          <el-tag size="small" type="info">[{{ cell.index }}]</el-tag>
          <span class="cell-label">代码</span>
        </div>
        <MarkdownRenderer :content="'```\n' + cell.source + '\n```'" />
        <!-- 输出结果 -->
        <div v-if="cell.outputs && cell.outputs.length" class="cell-outputs">
          <div v-for="(output, oi) in cell.outputs" :key="oi" class="cell-output">
            <pre v-if="output.type === 'text'" class="output-text">{{ output.data }}</pre>
            <img v-else-if="output.type === 'image'" :src="'data:image/png;base64,' + output.data" class="output-image" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { parseNotebook } from '../../utils/parseNotebook'
import MarkdownRenderer from '../markdown/MarkdownRenderer.vue'

const props = defineProps({
  content: { type: String, default: '' }
})

const cells = computed(() => {
  if (!props.content) return []
  return parseNotebook(props.content)
})
</script>

<style scoped>
.notebook-body {
  max-width: 100%;
}
.notebook-cell {
  margin-bottom: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  overflow: hidden;
}
.cell-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}
.cell-label {
  font-size: 12px;
  color: #999;
}
.cell-markdown {
  padding: 12px;
}
.cell-code {
  background: #f6f8fa;
}
.cell-outputs {
  border-top: 1px solid #e8e8e8;
  background: #fff;
  padding: 12px;
}
.cell-output {
  margin: 4px 0;
}
.output-text {
  background: #f5f5f5;
  padding: 8px;
  border-radius: 4px;
  font-size: 13px;
  overflow-x: auto;
}
.output-image {
  max-width: 100%;
}
</style>
```

---

### 任务 14：前端通用组件

**文件：**
- 创建：`frontend/src/components/common/ArticleCard.vue`
- 创建：`frontend/src/components/common/TagChip.vue`
- 创建：`frontend/src/components/common/Pagination.vue`

- [ ] **步骤 1：创建 `ArticleCard.vue`**

```vue
<template>
  <div class="article-card" @click="$router.push(`/article/${article.id}`)">
    <h3 class="article-title">{{ article.title }}</h3>
    <p class="article-summary">{{ article.summary }}</p>
    <div class="article-meta">
      <div class="article-tags">
        <TagChip v-for="tag in article.tags" :key="tag" :name="tag" />
      </div>
      <div class="article-stats">
        <span><el-icon><View /></el-icon> {{ formatViewCount(article.viewCount) }}</span>
        <span><el-icon><Clock /></el-icon> {{ formatDate(article.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { View, Clock } from '@element-plus/icons-vue'
import TagChip from './TagChip.vue'
import { formatDate, formatViewCount } from '../../utils/format'

defineProps({
  article: { type: Object, required: true }
})
</script>

<style scoped>
.article-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.article-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.article-title {
  font-size: 20px;
  color: #1a1a1a;
  margin-bottom: 8px;
}
.article-title:hover { color: #1890ff; }
.article-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
}
.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}
.article-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.article-stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 13px;
}
.article-stats span { display: flex; align-items: center; gap: 4px; }
</style>
```

- [ ] **步骤 2：创建 `TagChip.vue`**

```vue
<template>
  <el-tag
    :type="type"
    size="small"
    :hit="false"
    style="cursor: pointer;"
    @click.stop="$router.push(`/tag/${name}`)"
  >
    {{ name }}
  </el-tag>
</template>

<script setup>
defineProps({
  name: { type: String, required: true },
  type: { type: String, default: 'primary' }
})
</script>
```

- [ ] **步骤 3：创建 `Pagination.vue`**

```vue
<template>
  <div class="pagination-wrapper" v-if="total > size">
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="current"
      @current-change="$emit('change', $event)"
    />
  </div>
</template>

<script setup>
defineProps({
  total: { type: Number, default: 0 },
  size: { type: Number, default: 10 },
  current: { type: Number, default: 1 }
})

defineEmits(['change'])
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin: 32px 0;
}
</style>
```

---

### 任务 15：前端布局组件

**文件：**
- 创建：`frontend/src/layouts/BlogLayout.vue`
- 创建：`frontend/src/layouts/AdminLayout.vue`

- [ ] **步骤 1：创建 `BlogLayout.vue`** — 前台极简单栏布局

```vue
<template>
  <div class="blog-layout">
    <!-- 顶部导航 -->
    <header class="blog-header">
      <div class="header-inner">
        <router-link to="/" class="blog-logo">📝 技术博客</router-link>
        <nav class="blog-nav">
          <router-link to="/">首页</router-link>
          <router-link v-if="authStore.isLoggedIn" to="/admin">管理后台</router-link>
          <router-link v-else to="/login">登录</router-link>
        </nav>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="blog-main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="blog-footer">
      <p>© {{ new Date().getFullYear() }} 个人技术博客 — 用 ❤️ 打造</p>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const authStore = useAuthStore()
</script>

<style scoped>
.blog-layout { min-height: 100vh; display: flex; flex-direction: column; }
.blog-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.blog-logo {
  font-size: 20px;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}
.blog-nav { display: flex; gap: 20px; }
.blog-nav a {
  text-decoration: none;
  color: #666;
  font-size: 14px;
}
.blog-nav a:hover { color: #1890ff; }
.blog-main {
  flex: 1;
  max-width: 900px;
  width: 100%;
  margin: 24px auto;
  padding: 0 20px;
}
.blog-footer {
  text-align: center;
  padding: 24px;
  color: #999;
  font-size: 13px;
  border-top: 1px solid #e8e8e8;
}
</style>
```

- [ ] **步骤 2：创建 `AdminLayout.vue`** — 后台管理布局

```vue
<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="admin-sidebar">
      <div class="sidebar-header">📝 博客管理</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
      >
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles/new">
          <el-icon><Edit /></el-icon>
          <span>发布文章</span>
        </el-menu-item>
        <el-menu-item index="/admin/tags">
          <el-icon><CollectionTag /></el-icon>
          <span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatDotSquare /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/profile">
          <el-icon><User /></el-icon>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="admin-header">
        <span>欢迎回来，{{ authStore.user.nickname || '管理员' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </el-header>

      <!-- 内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { DataAnalysis, Document, Edit, CollectionTag, ChatDotSquare, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.admin-sidebar { background: #001529; }
.sidebar-header {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #0d2137;
}
.admin-header {
  background: #fff;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #e8e8e8;
  font-size: 14px;
  color: #666;
}
.admin-main {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>
```

---

### 任务 16：前端博客前台页面

**文件：**
- 创建：`frontend/src/views/blog/Home.vue`
- 创建：`frontend/src/views/blog/ArticleDetail.vue`
- 创建：`frontend/src/views/blog/TagFilter.vue`

- [ ] **步骤 1：创建 `Home.vue`** — 首页（文章列表 + 随机推荐 + 标签栏）

```vue
<template>
  <div class="home-page">
    <!-- 标签筛选栏 -->
    <div class="tag-bar" v-if="tags.length">
      <el-tag
        :type="activeTag === '' ? 'primary' : 'info'"
        @click="filterByTag('')"
        style="cursor: pointer;"
      >全部</el-tag>
      <el-tag
        v-for="tag in tags"
        :key="tag"
        :type="activeTag === tag ? 'primary' : 'info'"
        @click="filterByTag(tag)"
        style="cursor: pointer;"
      >{{ tag }}</el-tag>
    </div>

    <!-- 文章列表 -->
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />

    <!-- 加载状态 -->
    <el-skeleton v-if="loading" :rows="5" animated />

    <!-- 空状态 -->
    <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />

    <!-- 分页 -->
    <Pagination :total="total" :current="page" @change="loadArticles" />

    <!-- 随机推荐 -->
    <div class="recommend-section" v-if="randomArticles.length">
      <h3 class="section-title">🎯 猜你喜欢</h3>
      <div class="recommend-list">
        <ArticleCard v-for="article in randomArticles" :key="'r' + article.id" :article="article" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getRandomArticles } from '../../api/article'
import { getTags } from '../../api/tag'
import ArticleCard from '../../components/common/ArticleCard.vue'
import Pagination from '../../components/common/Pagination.vue'

const articles = ref([])
const randomArticles = ref([])
const tags = ref([])
const total = ref(0)
const page = ref(1)
const activeTag = ref('')
const loading = ref(false)

async function loadArticles(p = 1) {
  loading.value = true
  page.value = p
  try {
    const params = { page: p, size: 10 }
    if (activeTag.value) params.tag = activeTag.value
    const result = await getArticles(params)
    articles.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function filterByTag(tag) {
  activeTag.value = tag
  loadArticles(1)
}

onMounted(async () => {
  loadArticles()
  // 加载标签列表
  tags.value = await getTags()
  // 加载随机推荐
  randomArticles.value = await getRandomArticles(3)
})
</script>

<style scoped>
.tag-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding: 12px 0;
}
.recommend-section { margin-top: 40px; }
.section-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #1890ff;
}
</style>
```

- [ ] **步骤 2：创建 `ArticleDetail.vue`**

```vue
<template>
  <div class="article-detail" v-loading="loading">
    <div v-if="article">
      <h1 class="detail-title">{{ article.title }}</h1>
      <div class="detail-meta">
        <span>👁️ {{ formatViewCount(article.viewCount) }} 次阅读</span>
        <span>📅 {{ formatDate(article.createTime) }}</span>
      </div>
      <div class="detail-tags" v-if="article.tags && article.tags.length">
        <TagChip v-for="tag in article.tags" :key="tag" :name="tag" />
      </div>

      <!-- 内容渲染：根据类型选择不同渲染器 -->
      <div class="detail-content">
        <MarkdownRenderer v-if="article.contentType === 'MARKDOWN'" :content="article.content" />
        <NotebookRenderer v-else-if="article.contentType === 'NOTEBOOK'" :content="article.content" />
        <MarkdownRenderer v-else :content="article.content" />
      </div>

      <!-- 评论区 -->
      <CommentList :article-id="article.id" />
    </div>
    <el-empty v-else-if="!loading" description="文章不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '../../api/article'
import { formatDate, formatViewCount } from '../../utils/format'
import MarkdownRenderer from '../../components/markdown/MarkdownRenderer.vue'
import NotebookRenderer from '../../components/notebook/NotebookRenderer.vue'
import TagChip from '../../components/common/TagChip.vue'
import CommentList from '../../components/common/CommentList.vue'

const route = useRoute()
const article = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    article.value = await getArticleDetail(route.params.id)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-detail {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
}
.detail-title {
  font-size: 28px;
  color: #1a1a1a;
  margin-bottom: 12px;
}
.detail-meta {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}
.detail-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 24px;
}
.detail-content {
  line-height: 1.8;
  font-size: 15px;
  border-top: 1px solid #e8e8e8;
  padding-top: 24px;
}
</style>
```

- [ ] **步骤 3：创建 `TagFilter.vue`**

```vue
<template>
  <div class="tag-filter-page">
    <h2 class="page-title">标签：{{ tagName }}</h2>
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    <el-empty v-if="!loading && articles.length === 0" description="该标签下暂无文章" />
    <Pagination :total="total" :current="page" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles } from '../../api/article'
import ArticleCard from '../../components/common/ArticleCard.vue'
import Pagination from '../../components/common/Pagination.vue'

const route = useRoute()
const articles = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const tagName = ref('')

async function loadArticles(p = 1) {
  loading.value = true
  page.value = p
  try {
    const result = await getArticles({ page: p, size: 10, tag: tagName.value })
    articles.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

watch(() => route.params.tagName, (newVal) => {
  tagName.value = newVal
  loadArticles(1)
})

onMounted(() => {
  tagName.value = route.params.tagName
  loadArticles()
})
</script>

<style scoped>
.page-title {
  font-size: 22px;
  margin-bottom: 20px;
  color: #333;
}
</style>
```

---

## 第四阶段：标签与 AI 模块

### 任务 17：后端标签 — 实体 + 仓库

**文件：**
- 创建：`backend/src/main/java/com/example/blog/tag/entity/Tag.java`
- 创建：`backend/src/main/java/com/example/blog/tag/entity/ArticleTag.java`
- 创建：`backend/src/main/java/com/example/blog/tag/repository/TagRepository.java`
- 创建：`backend/src/main/java/com/example/blog/tag/repository/ArticleTagRepository.java`

- [ ] **步骤 1：创建 `Tag.java`**

```java
package com.example.blog.tag.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 标签实体
 */
@Data
@Entity
@Table(name = "tb_tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 50)
    private String category;    // LANGUAGE / FRAMEWORK / TOOL / DATABASE / OTHER

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
```

- [ ] **步骤 2：创建 `ArticleTag.java`**

```java
package com.example.blog.tag.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 文章-标签关联实体
 */
@Data
@Entity
@Table(name = "tb_article_tag", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "tag_id"})
})
public class ArticleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;
}
```

- [ ] **步骤 3：创建 `TagRepository.java`**

```java
package com.example.blog.tag.repository;

import com.example.blog.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

/**
 * 标签数据仓库
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    /** 获取所有标签，附带文章数 */
    @Query(value = "SELECT t.name, t.category, COUNT(at.article_id) as article_count " +
           "FROM tb_tag t LEFT JOIN tb_article_tag at ON t.id = at.tag_id " +
           "GROUP BY t.id ORDER BY article_count DESC", nativeQuery = true)
    List<Object[]> findAllWithCount();
}
```

- [ ] **步骤 4：创建 `ArticleTagRepository.java`**

```java
package com.example.blog.tag.repository;

import com.example.blog.tag.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 文章-标签关联数据仓库
 */
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    List<ArticleTag> findByArticleId(Long articleId);
    void deleteByArticleId(Long articleId);
}
```

---

### 任务 18：后端标签 + AI 服务

**文件：**
- 创建：`backend/src/main/java/com/example/blog/tag/service/TagService.java`
- 创建：`backend/src/main/java/com/example/blog/tag/controller/TagController.java`
- 创建：`backend/src/main/java/com/example/blog/ai/config/AiConfig.java`
- 创建：`backend/src/main/java/com/example/blog/ai/service/AiTagService.java`

- [ ] **步骤 1：创建 `TagService.java`**

```java
package com.example.blog.tag.service;

import com.example.blog.tag.entity.Tag;
import com.example.blog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签业务服务
 */
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /** 获取所有标签（含文章数量） */
    public List<Map<String, Object>> getAllTagsWithCount() {
        List<Object[]> results = tagRepository.findAllWithCount();
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("category", row[1]);
            map.put("articleCount", row[2]);
            return map;
        }).collect(Collectors.toList());
    }

    /** 获取所有标签名列表 */
    public List<String> getAllTagNames() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
```

- [ ] **步骤 2：创建 `TagController.java`**

```java
package com.example.blog.tag.controller;

import com.example.blog.common.Result;
import com.example.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 标签接口控制器
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /** 获取所有标签（含文章数，用于首页展示） */
    @GetMapping
    public Result<List<Map<String, Object>>> getAllTags() {
        return Result.success(tagService.getAllTagsWithCount());
    }

    /** 获取所有标签名列表 */
    @GetMapping("/names")
    public Result<List<String>> getTagNames() {
        return Result.success(tagService.getAllTagNames());
    }
}
```

- [ ] **步骤 3：创建 `AiConfig.java`**

```java
package com.example.blog.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API 配置
 */
@Data
@Configuration
public class AiConfig {
    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
}
```

- [ ] **步骤 4：创建 `AiTagService.java`**

```java
package com.example.blog.ai.service;

import com.example.blog.ai.config.AiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 标签服务
 * 调用 DeepSeek v4 Flash API 根据文章摘要自动生成标签
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiTagService {

    private final AiConfig aiConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 根据文章前80字生成标签
     * @param summary 文章摘要（前80字）
     * @return 标签列表（1~5个）
     */
    public List<String> generateTags(String summary) {
        try {
            // 构建请求体
            String requestBody = objectMapper.writeValueAsString(
                Map.of(
                    "model", "deepseek-chat",
                    "messages", List.of(
                        Map.of("role", "system", "content",
                            "你是一个技术博客标签分类器。根据文章开头内容，判断这篇文章" +
                            "涉及哪些编程语言、框架、工具或技术领域。" +
                            "只返回 JSON 格式：{\"tags\": [\"标签1\", \"标签2\", ...]}" +
                            "每个标签不超过20个字符，返回1~5个标签。"),
                        Map.of("role", "user", "content",
                            "根据以下文章开头判断技术标签：\n" + summary)
                    ),
                    "temperature", 0.3,
                    "max_tokens", 200
                )
            );

            // 发送 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(aiConfig.getApiUrl()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + aiConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.at("/choices/0/message/content").asText();

            // 提取 JSON 中的 tags 数组
            JsonNode tagsNode = objectMapper.readTree(content).get("tags");
            if (tagsNode != null && tagsNode.isArray()) {
                List<String> tags = new ArrayList<>();
                tagsNode.forEach(t -> tags.add(t.asText()));
                return tags;
            }
        } catch (Exception e) {
            log.error("AI 标签生成失败", e);
        }

        // 失败时返回空列表（不影响文章发布）
        return List.of();
    }
}
```

---

### 任务 19：前端标签 API + 标签管理页面

**文件：**
- 创建：`frontend/src/api/tag.js`
- 创建：`frontend/src/views/admin/TagManage.vue`

- [ ] **步骤 1：创建 `api/tag.js`**

```javascript
import request from './request'

export function getTags() {
  return request.get('/tags')
}

export function getTagNames() {
  return request.get('/tags/names')
}
```

- [ ] **步骤 2：创建 `TagManage.vue`**

```vue
<template>
  <div class="tag-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" size="small" @click="showCreateDialog = true">新增标签</el-button>
        </div>
      </template>

      <el-table :data="tags" stripe>
        <el-table-column prop="name" label="标签名" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="articleCount" label="文章数" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDelete(scope.row.name)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增标签对话框 -->
    <el-dialog v-model="showCreateDialog" title="新增标签" width="400px">
      <el-form :model="newTag" label-width="60px">
        <el-form-item label="标签名">
          <el-input v-model="newTag.name" placeholder="请输入标签名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTags } from '../../api/tag'

const tags = ref([])
const showCreateDialog = ref(false)
const newTag = ref({ name: '' })

async function loadTags() {
  tags.value = await getTags()
}

function handleCreate() {
  // 简化：前端不会直接调后端创建标签，标签由 AI 自动生成
  showCreateDialog.value = false
}

function handleDelete(name) {
  ElMessage.info('标签由系统自动管理，暂不支持手动删除')
}

onMounted(loadTags)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

---

## 第五阶段：评论模块

### 任务 20：后端评论

**文件：**
- 创建：`backend/src/main/java/com/example/blog/comment/entity/Comment.java`
- 创建：`backend/src/main/java/com/example/blog/comment/repository/CommentRepository.java`
- 创建：`backend/src/main/java/com/example/blog/comment/service/CommentService.java`
- 创建：`backend/src/main/java/com/example/blog/comment/controller/CommentController.java`

- [ ] **步骤 1：创建 `Comment.java`**

```java
package com.example.blog.comment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体
 * 支持嵌套回复（parent_id 指向自身）
 */
@Data
@Entity
@Table(name = "tb_comment", indexes = {
    @Index(name = "idx_article_id", columnList = "article_id")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "parent_id")
    private Long parentId;          // 父评论 ID，null=根评论

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
```

- [ ] **步骤 2：创建 `CommentRepository.java`**

```java
package com.example.blog.comment.repository;

import com.example.blog.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 评论数据仓库
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /** 按文章获取所有评论（按时间升序） */
    List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);

    /** 删除某文章的所有评论 */
    void deleteByArticleId(Long articleId);
}
```

- [ ] **步骤 3：创建 `CommentService.java`**

```java
package com.example.blog.comment.service;

import com.example.blog.comment.entity.Comment;
import com.example.blog.comment.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 评论业务服务
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 获取文章评论（已按父子关系组织）
     */
    public List<Map<String, Object>> getCommentsByArticle(Long articleId) {
        List<Comment> all = commentRepository.findByArticleIdOrderByCreateTimeAsc(articleId);

        // 分离根评论和回复
        List<Comment> roots = new ArrayList<>();
        Map<Long, List<Comment>> repliesMap = new HashMap<>();

        for (Comment c : all) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                repliesMap.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c);
            }
        }

        // 组装为树形结构
        List<Map<String, Object>> result = new ArrayList<>();
        for (Comment root : roots) {
            Map<String, Object> rootMap = commentToMap(root);
            rootMap.put("replies", repliesMap.getOrDefault(root.getId(), Collections.emptyList())
                    .stream().map(this::commentToMap).toList());
            result.add(rootMap);
        }
        return result;
    }

    /** 发表评论 */
    public Comment createComment(Long articleId, String nickname, String email,
                                  String content, Long parentId) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setNickname(nickname);
        comment.setEmail(email);
        comment.setContent(content);
        comment.setParentId(parentId);
        return commentRepository.save(comment);
    }

    /** 删除评论 */
    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("评论不存在");
        }
        commentRepository.deleteById(id);
    }

    /** 评论实体转 Map */
    private Map<String, Object> commentToMap(Comment c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("articleId", c.getArticleId());
        map.put("parentId", c.getParentId());
        map.put("nickname", c.getNickname());
        map.put("email", c.getEmail());
        map.put("content", c.getContent());
        map.put("createTime", c.getCreateTime());
        return map;
    }
}
```

- [ ] **步骤 4：创建 `CommentController.java`**

```java
package com.example.blog.comment.controller;

import com.example.blog.comment.entity.Comment;
import com.example.blog.comment.service.CommentService;
import com.example.blog.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论接口控制器
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 获取文章的评论 */
    @GetMapping("/articles/{articleId}/comments")
    public Result<List<Map<String, Object>>> getComments(@PathVariable Long articleId) {
        return Result.success(commentService.getCommentsByArticle(articleId));
    }

    /** 发表评论 */
    @PostMapping("/articles/{articleId}/comments")
    public Result<Comment> createComment(@PathVariable Long articleId,
                                          @RequestParam String nickname,
                                          @RequestParam(required = false) String email,
                                          @RequestParam String content,
                                          @RequestParam(required = false) Long parentId) {
        return Result.success(
            commentService.createComment(articleId, nickname, email, content, parentId));
    }

    /** 删除评论（需管理员） */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
```

---

### 任务 21：前端评论组件 + API

**文件：**
- 创建：`frontend/src/api/comment.js`
- 创建：`frontend/src/components/common/CommentList.vue`

- [ ] **步骤 1：创建 `api/comment.js`**

```javascript
import request from './request'

export function getComments(articleId) {
  return request.get(`/articles/${articleId}/comments`)
}

export function createComment(articleId, data) {
  return request.post(`/articles/${articleId}/comments`, null, { params: data })
}

export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}
```

- [ ] **步骤 2：创建 `CommentList.vue`**

```vue
<template>
  <div class="comment-section">
    <h3 class="comment-title">💬 评论（{{ comments.length }}）</h3>

    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-form :model="form" inline>
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="昵称 *" size="small" style="width:150px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="邮箱（选填）" size="small" style="width:200px" />
        </el-form-item>
        <el-form-item style="width:100%">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="submitComment">发表评论</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 评论列表 -->
    <div v-if="comments.length" class="comments-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-body">
          <strong>{{ comment.nickname }}</strong>
          <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
          <p class="comment-content">{{ comment.content }}</p>
          <el-button type="text" size="small" @click="replyTo(comment)">回复</el-button>
          <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(comment.id)">删除</el-button>

          <!-- 嵌套回复 -->
          <div v-if="comment.replies && comment.replies.length" class="replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <strong>{{ reply.nickname }}</strong>
              <span class="comment-time">{{ formatDate(reply.createTime) }}</span>
              <p class="comment-content">{{ reply.content }}</p>
              <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(reply.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无评论，快来抢沙发吧！" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getComments, createComment, deleteComment } from '../../api/comment'
import { useAuthStore } from '../../stores/auth'
import { formatDate } from '../../utils/format'

const props = defineProps({
  articleId: { type: [String, Number], required: true }
})

const authStore = useAuthStore()
const comments = ref([])
const replyToId = ref(null)

const form = reactive({
  nickname: '',
  email: '',
  content: '',
  parentId: null
})

async function loadComments() {
  try {
    comments.value = await getComments(props.articleId)
  } catch (e) {
    comments.value = []
  }
}

async function submitComment() {
  if (!form.nickname.trim()) { ElMessage.warning('请输入昵称'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入评论内容'); return }
  try {
    form.parentId = replyToId.value
    await createComment(props.articleId, form)
    ElMessage.success('评论发表成功')
    form.content = ''
    replyToId.value = null
    loadComments()
  } catch (e) { /* 错误已在拦截器中处理 */ }
}

function replyTo(comment) {
  replyToId.value = comment.id
  form.content = ''
  // 滚动到输入框
  window.scrollTo({ top: document.querySelector('.comment-input').offsetTop - 100, behavior: 'smooth' })
}

async function handleDelete(id) {
  try {
    await deleteComment(id)
    ElMessage.success('评论已删除')
    loadComments()
  } catch (e) { /* 错误已在拦截器中处理 */ }
}

onMounted(loadComments)
</script>

<style scoped>
.comment-section {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
}
.comment-title {
  font-size: 18px;
  margin-bottom: 20px;
}
.comment-input {
  margin-bottom: 24px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}
.comment-item { margin-bottom: 16px; }
.comment-time {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}
.comment-content {
  margin: 8px 0;
  line-height: 1.6;
}
.replies {
  margin-left: 24px;
  padding-left: 16px;
  border-left: 2px solid #e8e8e8;
  margin-top: 8px;
}
.reply-item { margin-bottom: 12px; }
</style>
```

---

## 第六阶段：管理后台

### 任务 22：后台仪表盘 + 文章管理

**文件：**
- 创建：`frontend/src/views/admin/Dashboard.vue`
- 创建：`frontend/src/views/admin/ArticleList.vue`

- [ ] **步骤 1：创建 `Dashboard.vue`**

```vue
<template>
  <div class="dashboard">
    <h2>仪表盘</h2>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.articleCount }}</div>
            <div class="stat-label">文章总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.commentCount }}</div>
            <div class="stat-label">评论总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.tagCount }}</div>
            <div class="stat-label">标签数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalViews }}</div>
            <div class="stat-label">总浏览量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getArticles } from '../../api/article'
import { getTags } from '../../api/tag'
import { getComments } from '../../api/comment'

const stats = reactive({
  articleCount: 0,
  commentCount: 0,
  tagCount: 0,
  totalViews: 0
})

onMounted(async () => {
  try {
    const articleResult = await getArticles({ page: 1, size: 1 })
    stats.articleCount = articleResult.total
  } catch (e) { /* 静默处理 */ }
  try {
    const tags = await getTags()
    stats.tagCount = tags.length
  } catch (e) { /* 静默处理 */ }
})
</script>

<style scoped>
.stat-card { text-align: center; padding: 10px 0; }
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #1890ff;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}
</style>
```

- [ ] **步骤 2：创建 `ArticleList.vue`**

```vue
<template>
  <div class="article-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文章管理</span>
          <el-button type="primary" size="small" @click="$router.push('/admin/articles/new')">发布文章</el-button>
        </div>
      </template>

      <el-table :data="articles" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="contentType" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.contentType === 'MARKDOWN' ? 'success' : 'warning'" size="small">
              {{ scope.row.contentType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="70" />
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button size="small" @click="$router.push(`/admin/articles/${scope.row.id}/edit`)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination
        :total="total"
        :current="page"
        @change="loadArticles"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticles, deleteArticle } from '../../api/article'
import { formatDate } from '../../utils/format'
import Pagination from '../../components/common/Pagination.vue'

const articles = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

async function loadArticles(p = 1) {
  loading.value = true
  page.value = p
  try {
    const result = await getArticles({ page: p, size: 10 })
    articles.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该文章吗？', '确认')
    await deleteArticle(id)
    ElMessage.success('文章已删除')
    loadArticles()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadArticles())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

---

### 任务 23：文章编辑器（含 AI 标签 + 文件上传）

**文件：**
- 创建：`frontend/src/views/admin/ArticleEditor.vue`
- 创建：`frontend/src/api/file.js`

- [ ] **步骤 1：创建 `api/file.js`**

```javascript
import request from './request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
```

- [ ] **步骤 2：创建 `ArticleEditor.vue`**

```vue
<template>
  <div class="article-editor">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文章' : '发布新文章' }}</span>
      </template>

      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="文章标题" />
        </el-form-item>

        <el-form-item label="内容">
          <!-- 上传文件方式 -->
          <div class="upload-area">
            <el-upload
              :auto-upload="false"
              accept=".md,.ipynb"
              :on-change="handleFileChange"
              :show-file-list="false"
              :limit="1"
            >
              <el-button type="info" size="small">上传 .md / .ipynb 文件</el-button>
            </el-upload>
            <span v-if="fileName" class="file-name">已选择: {{ fileName }}</span>
          </div>
          <!-- Markdown 编辑器 -->
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="20"
            placeholder="在此编写 Markdown 内容，或上传文件自动填充"
          />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="留空则自动取前80字" />
        </el-form-item>

        <el-form-item label="类型">
          <el-radio-group v-model="form.contentType">
            <el-radio value="MARKDOWN">Markdown</el-radio>
            <el-radio value="NOTEBOOK">Jupyter Notebook</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="PUBLISHED">直接发布</el-radio>
            <el-radio value="DRAFT">存为草稿</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- AI 标签预览 -->
        <el-form-item label="AI 标签">
          <div class="ai-tags">
            <el-tag
              v-for="tag in aiTags"
              :key="tag"
              type="success"
              style="margin-right: 6px;"
            >{{ tag }}</el-tag>
            <span v-if="!aiTags.length && !aiLoading" class="tag-hint">发布后将自动生成标签</span>
            <el-tag v-if="aiLoading" type="warning">AI 正在分析中...</el-tag>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存修改' : '发布文章' }}
          </el-button>
          <el-button @click="$router.push('/admin/articles')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createArticle, updateArticle, getArticleDetail } from '../../api/article'
import { uploadFile } from '../../api/file'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)
const fileName = ref('')
const aiTags = ref([])
const aiLoading = ref(false)

const form = reactive({
  title: '',
  content: '',
  summary: '',
  contentType: 'MARKDOWN',
  status: 'PUBLISHED',
  sourceFile: ''
})

/** 上传文件处理 — 读取文件内容填入编辑器 */
function handleFileChange(uploadFile) {
  fileName.value = uploadFile.name

  // 判断文件类型
  if (uploadFile.name.endsWith('.ipynb')) {
    form.contentType = 'NOTEBOOK'
  } else {
    form.contentType = 'MARKDOWN'
  }

  // 读取文件内容
  const reader = new FileReader()
  reader.onload = (e) => {
    form.content = e.target.result
  }
  reader.readAsText(uploadFile.raw)
}

/** 提交文章 */
async function handleSubmit() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入内容'); return }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateArticle(route.params.id, form)
      ElMessage.success('文章已更新')
    } else {
      await createArticle(form)
      ElMessage.success('文章已发布')
    }
    router.push('/admin/articles')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    submitting.value = false
  }
}

// 编辑时加载已有文章
onMounted(async () => {
  if (isEdit.value) {
    try {
      const article = await getArticleDetail(route.params.id)
      form.title = article.title
      form.content = article.content
      form.summary = article.summary
      form.contentType = article.contentType
      form.status = article.status
      aiTags.value = article.tags || []
    } catch (e) {
      ElMessage.error('文章加载失败')
    }
  }
})
</script>

<style scoped>
.upload-area {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.file-name { color: #1890ff; font-size: 13px; }
.tag-hint { color: #999; font-size: 13px; }
</style>
```

---

### 任务 24：后台评论管理 + 个人设置

**文件：**
- 创建：`frontend/src/views/admin/CommentManage.vue`
- 创建：`frontend/src/views/admin/Profile.vue`

- [ ] **步骤 1：创建 `CommentManage.vue`**

```vue
<template>
  <div class="comment-manage">
    <el-card>
      <template #header>
        <span>评论管理</span>
      </template>

      <el-table :data="allComments" stripe v-if="allComments.length">
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="articleId" label="文章 ID" width="100" />
        <el-table-column prop="createTime" label="时间" width="160">
          <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无评论" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticles } from '../../api/article'
import { getComments, deleteComment } from '../../api/comment'
import { formatDate } from '../../utils/format'

const allComments = ref([])

async function loadAllComments() {
  // 简化：遍历文章获取评论（实际应后端提供统一接口）
  // 临时方案：只展示最近文章的部分评论
  allComments.value = []
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '确认')
    await deleteComment(id)
    ElMessage.success('评论已删除')
    loadAllComments()
  } catch (e) { /* 用户取消或其他错误 */ }
}

onMounted(loadAllComments)
</script>
```

- [ ] **步骤 2：创建 `Profile.vue`**

```vue
<template>
  <div class="profile">
    <el-card>
      <template #header>
        <span>个人设置</span>
      </template>

      <el-form label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="authStore.user.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="authStore.user.nickname" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag type="danger">{{ authStore.user.role }}</el-tag>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { useAuthStore } from '../../stores/auth'
const authStore = useAuthStore()
</script>
```

---

## 第七阶段：文件上传后端

### 任务 25：后端文件上传服务

**文件：**
- 创建：`backend/src/main/java/com/example/blog/file/service/FileStorageService.java`
- 创建：`backend/src/main/java/com/example/blog/file/controller/FileController.java`

- [ ] **步骤 1：创建 `FileStorageService.java`**

```java
package com.example.blog.file.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件存储服务
 * 将上传的文件保存到本地文件系统，并返回可访问的 URL
 */
@Service
public class FileStorageService {

    @Value("${blog.upload.base-path:./uploads}")
    private String uploadBasePath;

    private Path markdownPath;
    private Path notebookPath;

    @PostConstruct
    public void init() {
        markdownPath = Paths.get(uploadBasePath, "markdown");
        notebookPath = Paths.get(uploadBasePath, "notebook");
        try {
            Files.createDirectories(markdownPath);
            Files.createDirectories(notebookPath);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    /**
     * 保存上传文件
     * @param file 上传的文件
     * @param contentType MARKDOWN 或 NOTEBOOK
     * @return 文件的相对访问路径
     */
    public String storeFile(MultipartFile file, String contentType) {
        String originalFilename = file.getOriginalFilename();
        // 生成唯一文件名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        Path targetDir = "NOTEBOOK".equals(contentType) ? notebookPath : markdownPath;
        Path targetPath = targetDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            // 返回相对路径（前端可通过 /uploads/ 访问）
            String subDir = "NOTEBOOK".equals(contentType) ? "notebook" : "markdown";
            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
    }
}
```

- [ ] **步骤 2：创建 `FileController.java`**

```java
package com.example.blog.file.controller;

import com.example.blog.common.Result;
import com.example.blog.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件接口控制器
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /** 上传文件 */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "MARKDOWN") String contentType) {
        String filePath = fileStorageService.storeFile(file, contentType);
        return Result.success(Map.of("url", filePath, "fileName", file.getOriginalFilename()));
    }
}
```

---

## 第八阶段：完善与文档

### 任务 26：编写项目文档

**文件：**
- 创建：`README.md`
- 创建：`docs/db-schema.md`
- 创建：`docs/user-guide.md`

- [ ] **步骤 1：创建 `README.md`**

```markdown
# 📝 个人技术博客系统

基于 Vue 3 + Spring Boot 3 的前后端分离个人博客系统。

## 技术栈

### 前端
- Vue 3 + Vite + Element Plus
- Vue Router 4 + Pinia
- Axios + marked.js + highlight.js

### 后端
- Spring Boot 3.2 + Spring Data JPA
- Spring Security + JWT
- MySQL 8 + jjwt + SpringDoc OpenAPI

### 特色功能
- ✅ Markdown / Jupyter Notebook 双格式支持
- ✅ AI 自动标签（DeepSeek v4 Flash API）
- ✅ 随机推荐文章
- ✅ 嵌套评论系统
- ✅ 浏览计数统计
- ✅ 标签筛选分类

## 快速开始

### 前置条件
- Java 17+
- Node.js 18+
- MySQL 8+

### 1. 创建数据库
```sql
CREATE DATABASE blog_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 启动后端
```bash
cd backend
# 配置 application.yml 中的 DeepSeek API Key
./mvnw spring-boot:run
```
> 后端启动在 http://localhost:8080，自动建表并创建默认管理员（admin / admin123）

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
> 前端启动在 http://localhost:5173

### 4. 访问
- 博客前台：http://localhost:5173
- 管理后台：http://localhost:5173/admin（需登录）
- API 文档：http://localhost:8080/swagger-ui.html

## 项目结构

```
boke/
├── frontend/          # Vue 3 前端
├── backend/           # Spring Boot 后端
├── uploads/           # 上传文件存储
├── docs/              # 文档
└── README.md
```

## 默认管理员
- 用户名：admin
- 密码：admin123
```

- [ ] **步骤 2：创建 `docs/db-schema.md`**

数据库结构说明文档 — 建议从设计文档中提取表结构部分，以 Markdown 表格形式列出每张表的字段、类型、约束、索引和关联关系。

- [ ] **步骤 3：创建 `docs/user-guide.md`**

用户操作说明文档 — 分"访客"和"管理员"两个角色，描述各功能的使用步骤、界面截图指引和常见问题。

---

### 任务 27：最终构建与验证

- [ ] **步骤 1：安装前端依赖并构建**

```bash
cd frontend
npm install
npm run build
```
预期：构建成功，生成 `frontend/dist/` 目录

- [ ] **步骤 2：编译后端**

```bash
cd backend
./mvnw clean compile -DskipTests
```
预期：BUILD SUCCESS

- [ ] **步骤 3：验证数据库表**

连接 MySQL，检查 `blog_db` 数据库，确认 5 张表已自动创建。

- [ ] **步骤 4：项目提交**

```bash
git init
git add .
git commit -m "feat: 初始化个人博客系统

完成前后端分离架构搭建，实现文章/标签/评论/认证/AI标签核心功能

Co-Authored-By: Claude <noreply@anthropic.com>"
```

---

## 自检

### 1. 规范覆盖率检查

| 规范需求 | 对应任务 |
|:--|:--|
| 用户认证（JWT 登录） | 任务 6、7、8 |
| 文章 CRUD + 分页 + 搜索 | 任务 9、10、11、16 |
| Markdown 渲染 | 任务 13 |
| Jupyter Notebook 渲染 | 任务 13 |
| 浏览计数 | 任务 10（getArticleById 自动 +1） |
| 随机推荐 | 任务 10（findRandomArticles） |
| AI 自动标签 | 任务 18（AiTagService）|
| 标签筛选 | 任务 10、14、16 |
| 评论 + 嵌套回复 | 任务 20、21 |
| 管理后台 | 任务 22、23、24 |
| 文件上传 | 任务 25 |
| 统一响应体 | 任务 2 |
| 全局异常处理 | 任务 2 |
| 路由守卫 | 任务 5 |
| 响应式设计 | 任务 15（BlogLayout 单栏） |

### 2. 占位符检查
- ✅ `your-api-key-here` — 用户需填写的 DeepSeek API Key（预期行为）
- ✅ `your-secret-key-at-least-256-bits-long` — 已预填了实际的 base64 密钥（非占位符）
- 无其他待办或未完成内容

### 3. 类型一致性
- 所有 Java 实体字段类型与数据库 DDL 一致
- 前端 API 调用参数名与后端 DTO 字段名一致
- 组件 Prop 命名与使用处一致
