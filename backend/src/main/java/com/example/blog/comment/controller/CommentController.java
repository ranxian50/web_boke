package com.example.blog.comment.controller;

import com.example.blog.comment.entity.Comment;
import com.example.blog.comment.service.CommentService;
import com.example.blog.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论接口控制器
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 获取文章的评论 */
    @GetMapping("/articles/{articleId}/comments")
    public Result<List<Map<String, Object>>> getComments(@PathVariable Long articleId) {
        return Result.success(commentService.getCommentsByArticle(articleId));
    }

    /** 发表评论 */
    @PostMapping("/articles/{articleId}/comments")
    public Result<Comment> createComment(@PathVariable Long articleId,
                                          @RequestParam String nickname,
                                          @RequestParam(required = false) String email,
                                          @RequestParam String content,
                                          @RequestParam(required = false) Long parentId) {
        return Result.success(
            commentService.createComment(articleId, nickname, email, content, parentId));
    }

    /** 删除评论（需管理员） */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    /** 获取评论数 */
    @GetMapping("/articles/{articleId}/comments/count")
    public Result<Long> getCommentCount(@PathVariable Long articleId) {
        return Result.success((long) commentService.getCommentsByArticle(articleId).size());
    }
}
