package com.qnaverse.QnAverse.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.services.FollowService;
import com.qnaverse.QnAverse.utils.JwtUtil;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;
    private final JwtUtil jwtUtil;

    public FollowController(FollowService followService, JwtUtil jwtUtil) {
        this.followService = followService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/follow/{followingUsername}")
    public ResponseEntity<?> followUser(@RequestHeader("Authorization") String token,
                                        @PathVariable String followingUsername) {
        String followerUsername = extractUsernameFromToken(token);
        return followService.followUser(followerUsername, followingUsername);
    }

    @DeleteMapping("/unfollow/{followingUsername}")
    public ResponseEntity<?> unfollowUser(@RequestHeader("Authorization") String token,
                                          @PathVariable String followingUsername) {
        String followerUsername = extractUsernameFromToken(token);
        return followService.unfollowUser(followerUsername, followingUsername);
    }

    @GetMapping("/followers/{username}")
    public ResponseEntity<?> getFollowers(@PathVariable String username) {
        return followService.getFollowers(username);
    }

    @GetMapping("/following/{username}")
    public ResponseEntity<?> getFollowing(@PathVariable String username) {
        return followService.getFollowing(username);
    }

    private String extractUsernameFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.extractUsername(token);
    }
}
