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

import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.services.SavedQuestionService;
import com.qnaverse.QnAverse.utils.JwtUtil;

/**
 * Controller for saving/un-saving questions.
 */
@RestController
@RequestMapping("/api/saved")
public class SavedQuestionController {

    private final SavedQuestionService savedService;
    private final JwtUtil jwtUtil;

    public SavedQuestionController(SavedQuestionService savedService, JwtUtil jwtUtil) {
        this.savedService = savedService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/question/{questionId}")
    public ResponseEntity<?> saveQuestion(@RequestHeader("Authorization") String token,
                                          @PathVariable Long questionId) {
        String username = extractUsername(token);
        return savedService.saveQuestion(username, questionId);
    }

    @DeleteMapping("/question/{questionId}")
    public ResponseEntity<?> unsaveQuestion(@RequestHeader("Authorization") String token,
                                            @PathVariable Long questionId) {
        String username = extractUsername(token);
        return savedService.unsaveQuestion(username, questionId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Question>> getAllSaved(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        return ResponseEntity.ok(savedService.getSavedQuestions(username));
    }

    private String extractUsername(String bearer) {
        if (bearer.startsWith("Bearer ")) {
            bearer = bearer.substring(7);
        }
        return jwtUtil.extractUsername(bearer);
    }
}
