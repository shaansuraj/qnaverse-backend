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

import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.services.LikeService;
import com.qnaverse.QnAverse.utils.JwtUtil;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    public LikeController(LikeService likeService, JwtUtil jwtUtil) {
        this.likeService = likeService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<?> likeQuestion(@RequestHeader("Authorization") String token,
                                          @PathVariable Long questionId) {
        String username = extractUsername(token);
        return likeService.likeQuestion(username, questionId);
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<?> unlikeQuestion(@RequestHeader("Authorization") String token,
                                            @PathVariable Long questionId) {
        String username = extractUsername(token);
        return likeService.unlikeQuestion(username, questionId);
    }

    @GetMapping("/question/{questionId}/likers")
    public ResponseEntity<?> getQuestionLikers(@PathVariable Long questionId) {
        List<User> likers = likeService.getQuestionLikers(questionId);
        return ResponseEntity.ok(likers);
    }

    private String extractUsername(String bearer) {
        if (bearer.startsWith("Bearer ")) {
            bearer = bearer.substring(7);
        }
        return jwtUtil.extractUsername(bearer);
    }
}
