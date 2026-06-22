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
