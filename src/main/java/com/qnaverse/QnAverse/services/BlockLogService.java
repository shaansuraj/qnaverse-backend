package com.qnaverse.QnAverse.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qnaverse.QnAverse.dto.BlockLogDTO;
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

    /**
     * Fetch logs for a user (either as blocker or blocked)
     */
    public List<BlockLog> getBlockLogsByUser(String username) {
        List<BlockLog> blockLogs = blockLogRepository.findByBlockerUsername(username);
        blockLogs.addAll(blockLogRepository.findByBlockedUsername(username));
        return blockLogs;
    }

    /**
     * Save a new block or unblock action to the logs
     */
    public void logBlockAction(User blocker, User blocked, String action) {
        BlockLog blockLog = new BlockLog(blocker, blocked, action);
        blockLogRepository.save(blockLog);
    }

    /**
     * Convert a BlockLog entity to a BlockLogDTO
     */
    public BlockLogDTO convertToDTO(BlockLog log) {
        return new BlockLogDTO(
            log.getId(),
            log.getBlocker().getUsername(),
            log.getBlocked().getUsername(),
            log.getAction(),
            log.getCreatedAt()
        );
    }

    /**
     * Get block logs for a user as a list of BlockLogDTOs
     */
    public List<BlockLogDTO> getBlockLogDTOsByUser(String username) {
        List<BlockLog> logs = getBlockLogsByUser(username);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
