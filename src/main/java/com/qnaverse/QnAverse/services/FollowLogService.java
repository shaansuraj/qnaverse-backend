package com.qnaverse.QnAverse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qnaverse.QnAverse.models.FollowLog;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.FollowLogRepository;

@Service
@Transactional
public class FollowLogService {

    private final FollowLogRepository followLogRepository;

    @Autowired
    public FollowLogService(FollowLogRepository followLogRepository) {
        this.followLogRepository = followLogRepository;
    }

    public List<FollowLog> getFollowLogsByUser(String username) {
        List<FollowLog> followLogs = followLogRepository.findByFollowerUsername(username);
        followLogs.addAll(followLogRepository.findByFollowingUsername(username));
        return followLogs;
    }

    public void logFollowAction(User follower, User following, String action) {
        FollowLog followLog = new FollowLog(follower, following, action);
        followLogRepository.save(followLog);
    }
}
