package com.example.blog.report.controller;

import com.example.blog.common.PageResult;
import com.example.blog.common.Result;
import com.example.blog.report.entity.Report;
import com.example.blog.report.service.ReportService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 举报接口控制器
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** 提交举报（公开接口） */
    @PostMapping
    public Result<Report> createReport(@RequestBody ReportRequest request) {
        return Result.success(reportService.createReport(
                request.getArticleId(), request.getNickname(),
                request.getEmail(), request.getReason(), request.getDescription()));
    }

    /** 获取举报列表（管理员） */
    @GetMapping
    public Result<PageResult<Report>> listReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return Result.success(reportService.getReports(page, size, status));
    }

    /** 处理举报（管理员） */
    @PutMapping("/{id}/handle")
    public Result<Void> handleReport(@PathVariable Long id,
                                      @RequestBody Map<String, String> body,
                                      Authentication auth) {
        Long adminId = 1L;
        reportService.handleReport(id, body.getOrDefault("action", "RESOLVED"), adminId, body.get("note"));
        return Result.success();
    }

    /** 获取待处理举报数 */
    @GetMapping("/pending-count")
    public Result<Long> getPendingCount() {
        return Result.success(reportService.getPendingCount());
    }

    @Data
    public static class ReportRequest {
        private Long articleId;
        private String nickname;
        private String email;
        private String reason;
        private String description;
    }
}
