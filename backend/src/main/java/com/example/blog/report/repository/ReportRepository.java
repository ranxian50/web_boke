package com.example.blog.report.repository;

import com.example.blog.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatus(String status, Pageable pageable);
    List<Report> findByArticleId(Long articleId);
    long countByStatus(String status);
}
