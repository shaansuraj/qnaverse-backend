package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.BlockLog;

public interface BlockLogRepository extends JpaRepository<BlockLog, Long> {
    List<BlockLog> findByBlockerUsername(String username);  // Method to find block logs by the username of the blocker
    List<BlockLog> findByBlockedUsername(String username);  // Method to find block logs by the username of the blocked user
}
