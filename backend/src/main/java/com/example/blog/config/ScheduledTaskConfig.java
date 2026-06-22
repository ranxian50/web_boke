package com.example.blog.config;

import com.example.blog.article.entity.Article;
import com.example.blog.article.repository.ArticleRepository;
import com.example.blog.common.constants.ArticleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务配置
 * 处理文章定时发布和自动转私人
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTaskConfig {

    private final ArticleRepository articleRepository;

    /** 每分钟检查一次：定时发布 */
    @Scheduled(fixedRate = 60000)
    public void processScheduledPublish() {
        LocalDateTime now = LocalDateTime.now();
        List<Article> toPublish = articleRepository
                .findByStatusAndScheduledPublishTimeBefore(ArticleStatus.DRAFT, now);
        for (Article article : toPublish) {
            article.setStatus(ArticleStatus.PUBLISHED);
            articleRepository.save(article);
            log.info("定时发布文章: ID={}, 标题={}", article.getId(), article.getTitle());
        }
    }

    /** 每分钟检查一次：到期自动转私人 */
    @Scheduled(fixedRate = 60000)
    public void processAutoPrivate() {
        LocalDateTime now = LocalDateTime.now();
        List<Article> toPrivate = articleRepository
                .findByIsPublicTrueAndAutoPrivateTimeBefore(now);
        for (Article article : toPrivate) {
            article.setIsPublic(false);
            articleRepository.save(article);
            log.info("文章已自动转为私人: ID={}, 标题={}", article.getId(), article.getTitle());
        }
    }
}
