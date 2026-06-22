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
    private Long userId;
    private String title;
    private String summary;
    private String contentType;
    private String sourceFile;
    private String coverImage;
    private Integer viewCount;
    private String status;
    private Boolean isPublic;
    private LocalDateTime scheduledPublishTime;
    private Integer autoPrivateDays;
    private LocalDateTime autoPrivateTime;
    private String authorName;  // 作者昵称
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> tags;          // 文章关联的标签列表
}
