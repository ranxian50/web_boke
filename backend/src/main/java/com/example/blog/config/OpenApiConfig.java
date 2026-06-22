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
