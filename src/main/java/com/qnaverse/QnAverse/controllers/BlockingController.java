package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.models.BlockedUser;
import com.qnaverse.QnAverse.services.BlockingService;
import com.qnaverse.QnAverse.utils.JwtUtil;

/**
 * Controller for blocking/unblocking users.
 */
@RestController
@RequestMapping("/api/block")
public class BlockingController {

    private final BlockingService blockingService;
    private final JwtUtil jwtUtil;

    public BlockingController(BlockingService blockingService, JwtUtil jwtUtil) {
        this.blockingService = blockingService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{blockedUsername}")
    public ResponseEntity<?> blockUser(@RequestHeader("Authorization") String token,
                                       @PathVariable String blockedUsername) {
        String blockerUsername = extractUsername(token);
        return blockingService.blockUser(blockerUsername, blockedUsername);
    }

    @DeleteMapping("/{blockedUsername}")
    public ResponseEntity<?> unblockUser(@RequestHeader("Authorization") String token,
                                         @PathVariable String blockedUsername) {
        String blockerUsername = extractUsername(token);
        return blockingService.unblockUser(blockerUsername, blockedUsername);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BlockedUser>> listBlocked(@RequestHeader("Authorization") String token) {
        String blockerUsername = extractUsername(token);
        List<BlockedUser> blocked = blockingService.getBlockedUsers(blockerUsername);
        return ResponseEntity.ok(blocked);
    }

    private String extractUsername(String bearer) {
        if (bearer.startsWith("Bearer ")) {
            bearer = bearer.substring(7);
        }
        return jwtUtil.extractUsername(bearer);
    }
}
