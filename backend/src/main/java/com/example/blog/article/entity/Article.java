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

    /** 是否公开：true=公开(默认), false=私人 */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /** 定时发布时间（为null表示立即发布） */
    @Column(name = "scheduled_publish_time")
    private LocalDateTime scheduledPublishTime;

    /** 发布后多少天自动转为私人 */
    @Column(name = "auto_private_days")
    private Integer autoPrivateDays;

    /** 自动转私人的具体时间 */
    @Column(name = "auto_private_time")
    private LocalDateTime autoPrivateTime;

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
