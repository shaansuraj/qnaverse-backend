package com.qnaverse.QnAverse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qnaverse.QnAverse.models.BlockLog;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.BlockLogRepository;

@Service
@Transactional
public class BlockLogService {

    private final BlockLogRepository blockLogRepository;

    @Autowired
    public BlockLogService(BlockLogRepository blockLogRepository) {
        this.blockLogRepository = blockLogRepository;
    }

    public List<BlockLog> getBlockLogsByUser(String username) {
        // Fetch logs for a user, either by blocker or blocked
        List<BlockLog> blockLogs = blockLogRepository.findByBlockerUsername(username);
        blockLogs.addAll(blockLogRepository.findByBlockedUsername(username));
        return blockLogs;
    }

    public void logBlockAction(User blocker, User blocked, String action) {
        BlockLog blockLog = new BlockLog(blocker, blocked, action);
        blockLogRepository.save(blockLog);
    }
}
