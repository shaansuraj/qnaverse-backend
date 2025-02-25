package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class SearchService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final BlockingService blockingService;

    public SearchService(QuestionRepository questionRepository,
                         UserRepository userRepository,
                         BlockingService blockingService) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.blockingService = blockingService;
    }

    /**
     * Searches questions by keyword in content or tag.
     */
    public List<Question> searchQuestions(String query, String currentUsername) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }
        List<Question> rawResults = questionRepository.searchByKeywordOrTag(query.trim());

        System.out.println("Searched values:"+rawResults);

        if (currentUsername == null || currentUsername.isBlank()) {
            return rawResults;
        }

        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        if (currentUser == null) {
            return rawResults;
        }

        List<Question> filtered = new ArrayList<>();
        for (Question q : rawResults) {
            if (!blockingService.isBlockedEitherWay(currentUser, q.getUser())) {
                filtered.add(q);
            }
        }
        return filtered;
    }

    /**
     * Searches users by partial username.
     */
    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }
        return userRepository.findByUsernameContainingIgnoreCase(query.trim());
    }
}
