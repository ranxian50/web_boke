package com.example.blog.report.service;

import com.example.blog.article.entity.Article;
import com.example.blog.article.repository.ArticleRepository;
import com.example.blog.common.PageResult;
import com.example.blog.report.entity.Report;
import com.example.blog.report.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 举报业务服务
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ArticleRepository articleRepository;

    /** 提交举报 */
    public Report createReport(Long articleId, String nickname, String email, String reason, String description) {
        Report report = new Report();
        report.setArticleId(articleId);
        report.setReporterNickname(nickname);
        report.setReporterEmail(email);
        report.setReason(reason);
        report.setDescription(description);
        report.setStatus("PENDING");
        return reportRepository.save(report);
    }

    /** 分页获取举报（含文章标题） */
    public PageResult<Map<String, Object>> getReports(int page, int size, String status) {
        PageRequest pr = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Report> result;
        if (status != null && !status.isBlank()) {
            result = reportRepository.findByStatus(status, pr);
        } else {
            result = reportRepository.findAll(pr);
        }

        List<Map<String, Object>> records = result.getContent().stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("articleId", r.getArticleId());
            // 获取文章标题
            String articleTitle = articleRepository.findById(r.getArticleId())
                    .map(Article::getTitle).orElse("未知文章");
            m.put("articleTitle", articleTitle);
            m.put("reporterNickname", r.getReporterNickname());
            m.put("reporterEmail", r.getReporterEmail());
            m.put("reason", r.getReason());
            m.put("description", r.getDescription());
            m.put("status", r.getStatus());
            m.put("handleNote", r.getHandleNote());
            m.put("createTime", r.getCreateTime());
            m.put("handleTime", r.getHandleTime());
            return m;
        }).collect(Collectors.toList());

        return new PageResult<>(records, result.getTotalElements(), page, size);
    }

    /** 处理举报 */
    @Transactional
    public void handleReport(Long reportId, String action, Long adminId, String note) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("举报不存在"));
        report.setStatus("RESOLVED".equals(action) ? "RESOLVED" : "DISMISSED");
        report.setHandledBy(adminId);
        report.setHandleNote(note);
        report.setHandleTime(LocalDateTime.now());
        reportRepository.save(report);
    }

    /** 获取待处理举报数 */
    public long getPendingCount() {
        return reportRepository.countByStatus("PENDING");
    }
}
