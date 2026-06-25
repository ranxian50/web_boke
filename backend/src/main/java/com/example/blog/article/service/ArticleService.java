package com.example.blog.article.service;

import com.example.blog.article.dto.ArticleCreateRequest;
import com.example.blog.article.dto.ArticleUpdateRequest;
import com.example.blog.article.dto.ArticleVO;
import com.example.blog.article.entity.Article;
import com.example.blog.article.repository.ArticleRepository;
import com.example.blog.auth.repository.UserRepository;
import com.example.blog.common.PageResult;
import com.example.blog.common.constants.ArticleStatus;
import com.example.blog.tag.entity.ArticleTag;
import com.example.blog.tag.entity.Tag;
import com.example.blog.tag.repository.ArticleTagRepository;
import com.example.blog.tag.repository.TagRepository;
import com.example.blog.ai.service.AiTagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章业务服务
 * 包含 CRUD、浏览计数、随机推荐、AI 标签
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    private final AiTagService aiTagService;
    private final UserRepository userRepository;

    /**
     * 分页查询已公开的文章（默认只返回公开文章）
     * 如果传入了 currentUserId，则额外返回该用户的私人文章
     * @param page          页码（从1开始）
     * @param size          每页条数
     * @param tagName       按标签筛选（可选）
     * @param keyword       关键词搜索（可选）
     * @param currentUserId 当前登录用户ID（可选），传入则额外包含该用户的私人文章
     */
    public PageResult<ArticleVO> getPublishedArticles(int page, int size, String tagName, String keyword, Long currentUserId) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> articlePage;

        if (keyword != null && !keyword.isBlank()) {
            articlePage = articleRepository.searchByTitle(ArticleStatus.PUBLISHED, keyword, pageRequest);
        } else {
            articlePage = articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageRequest);
        }

        // 在内存中过滤：保留公开文章 + 当前用户的私人文章
        List<ArticleVO> vos = articlePage.getContent().stream()
                .map(this::toVO)
                .filter(vo -> tagName == null || tagName.isBlank()
                        || (vo.getTags() != null && vo.getTags().contains(tagName)))
                .filter(vo -> Boolean.TRUE.equals(vo.getIsPublic())
                        || (currentUserId != null && currentUserId.equals(vo.getUserId())))
                .collect(Collectors.toList());

        return new PageResult<>(vos, articlePage.getTotalElements(), page, size);
    }

    /**
     * 获取某个用户的所有文章（公开 + 私人）
     */
    public PageResult<ArticleVO> getArticlesByUser(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> articlePage = articleRepository.findByUserId(userId, pageRequest);
        List<ArticleVO> vos = articlePage.getContent().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(vos, articlePage.getTotalElements(), page, size);
    }

    /** 获取文章详情（同时 +1 浏览次数） */
    @Transactional
    public ArticleVO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        return toVO(article);
    }

    /** 随机推荐文章 */
    public List<ArticleVO> getRandomArticles(int n) {
        return articleRepository.findRandomArticles(n)
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 创建文章（含 AI 自动标签）
     */
    @Transactional
    public ArticleVO createArticle(ArticleCreateRequest request, Long userId) {
        // 提取摘要（取前80字）
        String summary = request.getSummary();
        if (summary == null || summary.isBlank()) {
            summary = request.getContent().length() > 80
                    ? request.getContent().substring(0, 80) + "..."
                    : request.getContent();
        }

        // 处理定时发布时间
        LocalDateTime scheduledTime = null;
        if (request.getScheduledPublishTime() != null && !request.getScheduledPublishTime().isBlank()) {
            scheduledTime = LocalDateTime.parse(request.getScheduledPublishTime());
        }

        // 处理自动转私人
        Integer autoPrivateDays = request.getAutoPrivateDays();
        LocalDateTime autoPrivateTime = null;
        if (autoPrivateDays != null && autoPrivateDays > 0) {
            autoPrivateTime = LocalDateTime.now().plusDays(autoPrivateDays);
        }

        // 创建文章实体
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setContentType(request.getContentType());
        article.setSummary(summary);
        article.setCoverImage(request.getCoverImage());
        article.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        article.setScheduledPublishTime(scheduledTime);
        article.setAutoPrivateDays(autoPrivateDays);
        article.setAutoPrivateTime(autoPrivateTime);

        // 如果有定时发布时间且未到，设置状态为 DRAFT
        if (scheduledTime != null && scheduledTime.isAfter(LocalDateTime.now())) {
            article.setStatus(ArticleStatus.DRAFT);
        } else {
            article.setStatus(request.getStatus() != null ? request.getStatus() : ArticleStatus.PUBLISHED);
        }

        Article saved = articleRepository.save(article);

        // 调用 AI 自动打标签
        List<String> tagNames = aiTagService.generateTags(summary);

        // 保存标签关联
        for (String tagName : tagNames) {
            // 查找或创建标签
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            // 建立关联
            ArticleTag at = new ArticleTag();
            at.setArticleId(saved.getId());
            at.setTagId(tag.getId());
            articleTagRepository.save(at);
        }

        return toVO(saved);
    }

    /** 更新文章 */
    @Transactional
    public ArticleVO updateArticle(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        if (request.getTitle() != null) article.setTitle(request.getTitle());
        if (request.getContent() != null) article.setContent(request.getContent());
        if (request.getSummary() != null) article.setSummary(request.getSummary());
        if (request.getCoverImage() != null) article.setCoverImage(request.getCoverImage());
        if (request.getStatus() != null) article.setStatus(request.getStatus());
        if (request.getIsPublic() != null) article.setIsPublic(request.getIsPublic());

        if (request.getScheduledPublishTime() != null && !request.getScheduledPublishTime().isBlank()) {
            LocalDateTime scheduledTime = LocalDateTime.parse(request.getScheduledPublishTime());
            article.setScheduledPublishTime(scheduledTime);
            if (scheduledTime.isAfter(LocalDateTime.now())) {
                article.setStatus(ArticleStatus.DRAFT);
            }
        }

        if (request.getAutoPrivateDays() != null) {
            article.setAutoPrivateDays(request.getAutoPrivateDays());
            if (request.getAutoPrivateDays() > 0) {
                article.setAutoPrivateTime(LocalDateTime.now().plusDays(request.getAutoPrivateDays()));
            } else {
                article.setAutoPrivateTime(null);
            }
        }

        return toVO(articleRepository.save(article));
    }

    /** 删除文章（仅作者或管理员可删除） */
    @Transactional
    public void deleteArticle(Long id, Long currentUserId, String currentUserRole) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("文章不存在"));
        // 权限校验：仅作者或 ADMIN 可删除
        if (!article.getUserId().equals(currentUserId) && !"ADMIN".equals(currentUserRole)) {
            throw new org.springframework.security.access.AccessDeniedException("无权限删除此文章");
        }
        articleTagRepository.deleteByArticleId(id);
        articleRepository.deleteById(id);
    }

    /** 管理员查看所有公开文章 */
    public PageResult<ArticleVO> getPublicArticlesForAdmin(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Article> articlePage = articleRepository.findPublicArticles(pageRequest);
        List<ArticleVO> vos = articlePage.getContent().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(vos, articlePage.getTotalElements(), page, size);
    }

    /** 将 Article 实体转换为 ArticleVO */
    private ArticleVO toVO(Article article) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setUserId(article.getUserId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContentType(article.getContentType());
        vo.setSourceFile(article.getSourceFile());
        vo.setCoverImage(article.getCoverImage());
        vo.setViewCount(article.getViewCount());
        vo.setStatus(article.getStatus());
        vo.setIsPublic(article.getIsPublic());
        vo.setScheduledPublishTime(article.getScheduledPublishTime());
        vo.setAutoPrivateDays(article.getAutoPrivateDays());
        vo.setAutoPrivateTime(article.getAutoPrivateTime());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());

        // 获取作者昵称
        if (article.getUserId() != null) {
            userRepository.findById(article.getUserId())
                    .ifPresent(user -> vo.setAuthorName(user.getNickname()));
        }

        // 获取标签
        List<String> tags = articleTagRepository.findByArticleId(article.getId())
                .stream().map(at -> tagRepository.findById(at.getTagId())
                        .map(Tag::getName).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        vo.setTags(tags);
        return vo;
    }
}
