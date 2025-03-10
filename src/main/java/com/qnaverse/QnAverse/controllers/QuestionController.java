package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qnaverse.QnAverse.dto.QuestionDTO;
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

    //create a question

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

    //approves a question
    @PutMapping("/approve/{questionId}")
    public ResponseEntity<?> approveQuestion(@PathVariable Long questionId) {
        return questionService.approveQuestion(questionId);
    }

    //rejects a questions
    @DeleteMapping("/unapprove/{questionId}")
    public ResponseEntity<?> unapproveQuestion(@PathVariable Long questionId){
        return questionService.unapproveQuestion(questionId);
    }

    //get the user feed
    @GetMapping("/feed/{username}")
    public ResponseEntity<?> getUserFeed(@PathVariable String username) {
        return questionService.getUserFeed(username);
    }

    //fetches the trending questions

    @GetMapping("/trending")
public ResponseEntity<List<QuestionDTO>> getTrendingQuestions(
    @RequestParam(required = false) String tag,
    @RequestParam String viewerUsername) {
    return questionService.getTrendingQuestionsDTO(tag, viewerUsername);
}

    //Prints the details in the question
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getQuestionDetails(@PathVariable Long id) {
        return questionService.getQuestionDetails(id);
    }

    //Lists the likers of the question

    @GetMapping("/{id}/likers")
    public ResponseEntity<?> getQuestionLikers(@PathVariable Long id) {
        try {
            List<User> likers = questionService.getLikersForQuestion(id);
            return ResponseEntity.ok(likers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Editing the question

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

            //delete a question
    @DeleteMapping("/{username}/delete/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String username,
                                            @PathVariable Long questionId) {
        return questionService.deleteQuestion(username, questionId);
    }
}
