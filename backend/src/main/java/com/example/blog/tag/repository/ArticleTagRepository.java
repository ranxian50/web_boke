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
