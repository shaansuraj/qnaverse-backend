package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.FollowLog;

public interface FollowLogRepository extends JpaRepository<FollowLog, Long> {
    List<FollowLog> findByFollowerUsername(String username);  
    List<FollowLog> findByFollowingUsername(String username);  
}
