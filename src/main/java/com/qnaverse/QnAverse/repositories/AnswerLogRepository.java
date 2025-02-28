package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.AnswerLog;

public interface AnswerLogRepository extends JpaRepository<AnswerLog, Long> {
    List<AnswerLog> findByUserUsername(String username);  // Method to find answer logs by the username of the user
}
