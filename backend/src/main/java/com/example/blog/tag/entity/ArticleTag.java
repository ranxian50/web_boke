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
