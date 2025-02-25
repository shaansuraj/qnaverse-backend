package com.qnaverse.QnAverse.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.services.SearchService;

/**
 * Controller for searching questions & users.
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search questions by keywords, tags, likes; skip blocked content is handled inside service.
     */
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String query,
                                                          @RequestParam(required=false) String currentUsername) {
        return ResponseEntity.ok(searchService.searchQuestions(query, currentUsername));
    }

    /**
     * Search users by username (partial match).
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(searchService.searchUsers(query));
    }
}
