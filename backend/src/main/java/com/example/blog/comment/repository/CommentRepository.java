package com.example.blog.comment.repository;

import com.example.blog.comment.entity.Comment;
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
}
