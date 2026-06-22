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
    private Boolean isPublic = true;
    private String scheduledPublishTime;  // ISO 格式字符串，如 "2026-07-01T10:00:00"
    private Integer autoPrivateDays;
}
