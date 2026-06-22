package com.example.blog.report.service;

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

/**
 * 举报业务服务
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

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

    /** 分页获取举报 */
    public PageResult<Report> getReports(int page, int size, String status) {
        PageRequest pr = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Report> result;
        if (status != null && !status.isBlank()) {
            result = reportRepository.findByStatus(status, pr);
        } else {
            result = reportRepository.findAll(pr);
        }
        return new PageResult<>(result.getContent(), result.getTotalElements(), page, size);
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
