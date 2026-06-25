package com.example.blog.article.controller;

import com.example.blog.article.dto.ArticleCreateRequest;
import com.example.blog.article.dto.ArticleUpdateRequest;
import com.example.blog.article.dto.ArticleVO;
import com.example.blog.article.service.ArticleService;
import com.example.blog.auth.entity.User;
import com.example.blog.auth.repository.UserRepository;
import com.example.blog.common.Result;
import com.example.blog.common.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章接口控制器
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserRepository userRepository;

    /** 获取文章列表（分页 + 标签筛选 + 关键词搜索） */
    @GetMapping
    public Result<PageResult<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            Authentication auth) {
        Long currentUserId = getUserIdFromAuth(auth);
        return Result.success(articleService.getPublishedArticles(page, size, tag, keyword, currentUserId));
    }

    /** 获取文章详情 */
    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    /** 随机推荐文章 */
    @GetMapping("/random")
    public Result<List<ArticleVO>> random(@RequestParam(defaultValue = "3") int n) {
        return Result.success(articleService.getRandomArticles(n));
    }

    /** 获取当前用户的所有文章 */
    @GetMapping("/my")
    public Result<PageResult<ArticleVO>> myArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        return Result.success(articleService.getArticlesByUser(userId, page, size));
    }

    /** 管理员获取所有公开文章 */
    @GetMapping("/admin/public")
    public Result<PageResult<ArticleVO>> adminPublic(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(articleService.getPublicArticlesForAdmin(page, size));
    }

    /** 发布文章（需管理员登录） */
    @PostMapping
    public Result<ArticleVO> create(@Valid @RequestBody ArticleCreateRequest request,
                                     Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        if (userId == null) {
            userId = 1L; // 降级处理
        }
        return Result.success(articleService.createArticle(request, userId));
    }

    /** 编辑文章 */
    @PutMapping("/{id}")
    public Result<ArticleVO> update(@PathVariable Long id,
                                     @Valid @RequestBody ArticleUpdateRequest request) {
        return Result.success(articleService.updateArticle(id, request));
    }

    /** 删除文章（仅作者或管理员可删除） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        String role = auth.getAuthorities().stream()
                .findFirst().map(g -> g.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        articleService.deleteArticle(id, userId, role);
        return Result.success();
    }

    /**
     * 从 Authentication 对象中提取用户ID
     */
    private Long getUserIdFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username)
                    .map(User::getId)
                    .orElse(null);
        }
        return null;
    }
}
