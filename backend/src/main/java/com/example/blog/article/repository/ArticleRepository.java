package com.example.blog.article.repository;

import com.example.blog.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据仓库
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** 按状态分页查询 */
    Page<Article> findByStatus(String status, Pageable pageable);

    /** 按状态和标题关键词搜索 */
    @Query("SELECT a FROM Article a WHERE a.status = :status AND a.title LIKE %:keyword%")
    Page<Article> searchByTitle(@Param("status") String status,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    /** 随机获取 N 篇已发布的文章 */
    @Query(value = "SELECT * FROM tb_article WHERE status = 'PUBLISHED' ORDER BY RAND() LIMIT :n",
           nativeQuery = true)
    List<Article> findRandomArticles(@Param("n") int n);

    /** 统计已发布文章总数 */
    long countByStatus(String status);

    /** 按用户ID分页查询 */
    Page<Article> findByUserId(Long userId, Pageable pageable);

    /** 定时发布：状态为草稿且定时时间已到 */
    List<Article> findByStatusAndScheduledPublishTimeBefore(String status, LocalDateTime time);

    /** 自动转私人：已公开且到期时间已过 */
    List<Article> findByIsPublicTrueAndAutoPrivateTimeBefore(LocalDateTime time);

    /** 查询公开文章（管理员用） */
    @Query("SELECT a FROM Article a WHERE a.isPublic = true AND a.status = 'PUBLISHED'")
    Page<Article> findPublicArticles(Pageable pageable);
}
