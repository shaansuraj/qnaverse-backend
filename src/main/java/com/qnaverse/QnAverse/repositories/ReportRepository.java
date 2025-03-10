package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByContentType(String contentType);

    List<Report> findByContentTypeAndContentId(String contentType, Long contentId);


    // For counting how many times something was reported
    long countByContentIdAndContentType(Long contentId, String contentType);
}
