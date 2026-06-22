package com.example.blog.report.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章举报实体
 */
@Data
@Entity
@Table(name = "tb_report", indexes = {
    @Index(name = "idx_report_article", columnList = "article_id"),
    @Index(name = "idx_report_status", columnList = "status")
})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(nullable = false, length = 50)
    private String reporterNickname;

    @Column(length = 100)
    private String reporterEmail;

    @Column(nullable = false, length = 200)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";  // PENDING / RESOLVED / DISMISSED

    @Column(name = "handled_by")
    private Long handledBy;

    @Column(name = "handle_note", length = 500)
    private String handleNote;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    @PrePersist
    protected void onCreate() { createTime = LocalDateTime.now(); }
}
