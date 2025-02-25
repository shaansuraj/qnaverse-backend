package com.qnaverse.QnAverse.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.dto.AnswerDTO;
import com.qnaverse.QnAverse.services.AnswerService;

/**
 * Handles answer-related API endpoints.
 */
@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    /**
     * Submits an answer to a question.
     */
    @PostMapping("/{username}/submit/{questionId}")
    public ResponseEntity<?> submitAnswer(@PathVariable String username,
                                          @PathVariable Long questionId,
                                          @RequestBody AnswerDTO answerDTO) {
        return answerService.submitAnswer(username, questionId, answerDTO);
    }

    /**
     * Fetches all answers for a given question, excluding blocked or hidden ones.
     */
    @GetMapping("/question/{questionId}/{currentUsername}")
    public ResponseEntity<?> getAnswers(@PathVariable Long questionId,
                                        @PathVariable String currentUsername) {
        return answerService.getAnswers(questionId, currentUsername);
    }

    /**
     * Upvotes an answer. If the user already downvoted, it switches to upvote.
     */
    @PutMapping("/upvote/{answerId}/{username}")
    public ResponseEntity<?> upvoteAnswer(@PathVariable Long answerId,
                                          @PathVariable String username) {
        return answerService.upvoteAnswer(answerId, username);
    }

    /**
     * Downvotes an answer. If the user already upvoted, it switches to downvote.
     */
    @PutMapping("/downvote/{answerId}/{username}")
    public ResponseEntity<?> downvoteAnswer(@PathVariable Long answerId,
                                            @PathVariable String username) {
        return answerService.downvoteAnswer(answerId, username);
    }

     /**
     * NEW: Returns list of users who upvoted the answer.
     */
    @GetMapping("/{answerId}/upvoters")
    public ResponseEntity<?> getAnswerUpvoters(@PathVariable Long answerId) {
        return ResponseEntity.ok(answerService.getAnswerUpvoters(answerId));
    }

    /**
     * NEW: Returns list of users who downvoted the answer.
     */
    @GetMapping("/{answerId}/downvoters")
    public ResponseEntity<?> getAnswerDownvoters(@PathVariable Long answerId) {
        return ResponseEntity.ok(answerService.getAnswerDownvoters(answerId));
    }

}
