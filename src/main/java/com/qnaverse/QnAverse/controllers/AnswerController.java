package com.qnaverse.QnAverse.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.dto.AnswerDTO;
import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.repositories.AnswerRepository;
import com.qnaverse.QnAverse.services.AnswerService;


@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;
    private final AnswerRepository answerRepository;

    public AnswerController(AnswerService answerService, AnswerRepository answerRepository) {
        this.answerService = answerService;
        this.answerRepository = answerRepository;
    }

    @PostMapping("/{username}/submit/{questionId}")
    public ResponseEntity<?> submitAnswer(@PathVariable String username,
                                          @PathVariable Long questionId,
                                          @RequestBody AnswerDTO answerDTO) {
        return answerService.submitAnswer(username, questionId, answerDTO);
    }

    @GetMapping("/question/{questionId}/{currentUsername}")
    public ResponseEntity<?> getAnswers(@PathVariable Long questionId,
                                        @PathVariable String currentUsername) {
        return answerService.getAnswers(questionId, currentUsername);
    }

    @PutMapping("/upvote/{answerId}/{username}")
    public ResponseEntity<?> upvoteAnswer(@PathVariable Long answerId,
                                          @PathVariable String username) {
        return answerService.upvoteAnswer(answerId, username);
    }

    @PutMapping("/downvote/{answerId}/{username}")
    public ResponseEntity<?> downvoteAnswer(@PathVariable Long answerId,
                                            @PathVariable String username) {
        return answerService.downvoteAnswer(answerId, username);
    }

    @GetMapping("/{answerId}/upvoters")
    public ResponseEntity<?> getAnswerUpvoters(@PathVariable Long answerId) {
        return ResponseEntity.ok(answerService.getAnswerUpvoters(answerId));
    }

    @GetMapping("/{answerId}/downvoters")
    public ResponseEntity<?> getAnswerDownvoters(@PathVariable Long answerId) {
        return ResponseEntity.ok(answerService.getAnswerDownvoters(answerId));
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getAnswerDetails(@PathVariable Long id) {
    Optional<Answer> answerOpt = answerRepository.findById(id);
    if (answerOpt.isPresent()) {
        return ResponseEntity.ok(answerOpt.get());
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Answer not found");
}

}
