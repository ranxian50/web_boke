package com.example.blog.comment.repository;

import com.example.blog.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 评论数据仓库
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /** 按文章获取所有评论（按时间升序） */
    List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);

    /** 删除某文章的所有评论 */
    void deleteByArticleId(Long articleId);

    /** 按文章ID分页查询评论（按时间降序） */
    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    /** 分页查询所有评论（按时间降序） */
    Page<Comment> findAllByOrderByCreateTimeDesc(Pageable pageable);
}
