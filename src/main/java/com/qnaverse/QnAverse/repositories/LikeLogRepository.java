package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.LikeLog;

public interface LikeLogRepository extends JpaRepository<LikeLog, Long> {
    List<LikeLog> findByUserUsername(String username);  // Custom query for fetching LikeLogs by username
}
