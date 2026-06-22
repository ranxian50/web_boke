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
