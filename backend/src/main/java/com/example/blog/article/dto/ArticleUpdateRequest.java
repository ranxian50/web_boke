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
    private Boolean isPublic;
    private String scheduledPublishTime;  // ISO 格式字符串
    private Integer autoPrivateDays;
}
