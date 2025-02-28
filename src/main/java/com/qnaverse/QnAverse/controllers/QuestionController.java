package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.services.QuestionService;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping(value = "/{username}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQuestion(
            @PathVariable String username,
            @RequestPart("content") String content,
            @RequestPart(value = "tags", required = false) String tagsStr,
            @RequestPart(value = "media", required = false) MultipartFile media) {
        
        List<String> tags = null;
        if (tagsStr != null && !tagsStr.isBlank()) {
            try {
                tags = objectMapper.readValue(tagsStr, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                tags = List.of(tagsStr.split(","));
            }
        }
        return questionService.createQuestion(username, content, tags, media);
    }

    @PutMapping("/approve/{questionId}")
    public ResponseEntity<?> approveQuestion(@PathVariable Long questionId) {
        return questionService.approveQuestion(questionId);
    }

    @GetMapping("/feed/{username}")
    public ResponseEntity<?> getUserFeed(@PathVariable String username) {
        return questionService.getUserFeed(username);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Question>> getTrendingQuestions(@RequestParam(required = false) String tag) {
        return questionService.getTrendingQuestions(tag);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getQuestionDetails(@PathVariable Long id) {
        return questionService.getQuestionDetails(id);
    }

    @GetMapping("/{id}/likers")
    public ResponseEntity<?> getQuestionLikers(@PathVariable Long id) {
        try {
            List<User> likers = questionService.getLikersForQuestion(id);
            return ResponseEntity.ok(likers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{username}/edit/{questionId}")
    public ResponseEntity<?> editQuestion(
            @PathVariable Long questionId,
            @PathVariable String username,
            @RequestPart("content") String content,
            @RequestPart(value = "tags", required = false) String tagsStr,
            @RequestPart(value = "media", required = false) MultipartFile media) {
        
        List<String> tags = null;
        if (tagsStr != null && !tagsStr.isBlank()) {
            try {
                tags = objectMapper.readValue(tagsStr, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                tags = List.of(tagsStr.split(","));
            }
        }
        return questionService.editQuestion(questionId, content, tags, media, username);
    }
}
