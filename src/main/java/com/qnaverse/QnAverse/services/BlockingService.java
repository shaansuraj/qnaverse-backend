package com.qnaverse.QnAverse.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.BlockedUser;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.BlockedUserRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class BlockingService {

    private final BlockedUserRepository blockedRepo;
    private final UserRepository userRepo;

    public BlockingService(BlockedUserRepository blockedRepo, UserRepository userRepo) {
        this.blockedRepo = blockedRepo;
        this.userRepo = userRepo;
    }

    /**
     * Block a user
     */
    public ResponseEntity<?> blockUser(String blockerUsername, String blockedUsername) {
        Optional<User> blockOpt = userRepo.findByUsername(blockerUsername);
        Optional<User> blockedOpt = userRepo.findByUsername(blockedUsername);

        if (blockOpt.isEmpty() || blockedOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid blocker or blocked user");
        }

        User block = blockOpt.get();
        User blocked = blockedOpt.get();
        if (block.getId().equals(blocked.getId())) {
            return ResponseEntity.badRequest().body("You cannot block yourself");
        }

        Optional<BlockedUser> existing = blockedRepo.findByBlockerAndBlocked(block, blocked);
        if (existing.isPresent()) {
            return ResponseEntity.ok("Already blocked this user");
        }

        BlockedUser bu = new BlockedUser(block, blocked);
        blockedRepo.save(bu);
        return ResponseEntity.ok("User blocked successfully");
    }

    /**
     * Unblock a user
     */
    public ResponseEntity<?> unblockUser(String blockerUsername, String blockedUsername) {
        Optional<User> blockOpt = userRepo.findByUsername(blockerUsername);
        Optional<User> blockedOpt = userRepo.findByUsername(blockedUsername);

        if (blockOpt.isEmpty() || blockedOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid blocker or blocked user");
        }

        User block = blockOpt.get();
        User blocked = blockedOpt.get();

        Optional<BlockedUser> existing = blockedRepo.findByBlockerAndBlocked(block, blocked);
        if (existing.isEmpty()) {
            return ResponseEntity.ok("You have not blocked this user yet");
        }

        blockedRepo.delete(existing.get());
        return ResponseEntity.ok("User unblocked");
    }

    /**
     * List users that 'blockerUsername' has blocked
     */
    public List<BlockedUser> getBlockedUsers(String blockerUsername) {
        Optional<User> blockOpt = userRepo.findByUsername(blockerUsername);
        if (blockOpt.isEmpty()) return Collections.emptyList();
        return blockedRepo.findByBlocker(blockOpt.get());
    }

    /**
     * Check if user1 is blocked by or is blocking user2
     */
    public boolean isBlockedEitherWay(User user1, User user2) {
        if (user1 == null || user2 == null) return false;
        Optional<BlockedUser> bu1 = blockedRepo.findByBlockerAndBlocked(user1, user2);
        Optional<BlockedUser> bu2 = blockedRepo.findByBlockerAndBlocked(user2, user1);
        return (bu1.isPresent() || bu2.isPresent());
    }

    /**
     * Overload to accept username strings
     */
    public boolean isBlockedEitherWay(String username1, String username2) {
        Optional<User> u1 = userRepo.findByUsername(username1);
        Optional<User> u2 = userRepo.findByUsername(username2);
        if (u1.isEmpty() || u2.isEmpty()) return false;
        return isBlockedEitherWay(u1.get(), u2.get());
    }
}
