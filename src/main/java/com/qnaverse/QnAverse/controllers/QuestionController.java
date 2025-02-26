package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qnaverse.QnAverse.exceptions.ResourceNotFoundException;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.services.QuestionService;
import com.qnaverse.QnAverse.models.User;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Creates a new question with optional media (image/video) and tags.
     * The request should be sent as multipart/form-data.
     */
    @PostMapping(value = "/{username}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createQuestion(
            @PathVariable String username,
            @RequestPart("content") String content,
            // Accept tags as a JSON string and then parse it into a List<String>
            @RequestPart(value = "tags", required = false) String tagsStr,
            @RequestPart(value = "media", required = false) MultipartFile media) {
        
        List<String> tags = null;
        if (tagsStr != null && !tagsStr.isBlank()) {
            try {
                tags = objectMapper.readValue(tagsStr, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // If parsing fails, fall back to splitting by comma
                tags = List.of(tagsStr.split(","));
            }
        }
        return questionService.createQuestion(username, content, tags, media);
    }

    /**
     * Approves a question (Admin Only).
     */
    @PutMapping("/approve/{questionId}")
    public ResponseEntity<?> approveQuestion(@PathVariable Long questionId) {
        return questionService.approveQuestion(questionId);
    }

    /**
     * Returns the feed of questions from followed users and trending questions.
     */
    @GetMapping("/feed/{username}")
    public ResponseEntity<?> getUserFeed(@PathVariable String username) {
        return questionService.getUserFeed(username);
    }

    /**
     * Fetches trending questions overall or by tag.
     * If tag is provided, filters by that tag.
     */
    @GetMapping("/trending")
    public ResponseEntity<List<Question>> getTrendingQuestions(@RequestParam(required = false) String tag) {
        return questionService.getTrendingQuestions(tag);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getQuestionDetails(@PathVariable Long id) {
        return questionService.getQuestionDetails(id);
    }

    /**
     * Retrieves the users who liked a particular question.
     */
    @GetMapping("/{id}/likers")
    public ResponseEntity<List<User>> getQuestionLikers(@PathVariable Long id) {
        try {
            List<User> likers = questionService.getLikersForQuestion(id);
            return ResponseEntity.ok(likers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Edit an existing question (including media update if provided).
     * The user can update the content, tags, and media of the question.
     */
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
                // If parsing fails, fall back to splitting by comma
                tags = List.of(tagsStr.split(","));
            }
        }
        
        return questionService.editQuestion(questionId, content, tags, media, username);
    }
}
