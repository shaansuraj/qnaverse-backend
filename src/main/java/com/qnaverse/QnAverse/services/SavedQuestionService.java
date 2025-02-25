package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.SavedQuestion;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.SavedQuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class SavedQuestionService {

    private final SavedQuestionRepository savedRepo;
    private final UserRepository userRepo;
    private final QuestionRepository questionRepo;
    private final BlockingService blockingService;

    public SavedQuestionService(SavedQuestionRepository savedRepo,
                                UserRepository userRepo,
                                QuestionRepository questionRepo,
                                BlockingService blockingService) {
        this.savedRepo = savedRepo;
        this.userRepo = userRepo;
        this.questionRepo = questionRepo;
        this.blockingService = blockingService;
    }

    /**
     * Save a question for later viewing.
     */
    public ResponseEntity<?> saveQuestion(String username, Long questionId) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Question> questionOpt = questionRepo.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        User user = userOpt.get();
        Question question = questionOpt.get();

        if (!question.isApproved()) {
            return ResponseEntity.badRequest().body("Question is not approved.");
        }

        // Check blocking
        if (blockingService.isBlockedEitherWay(user, question.getUser())) {
            return ResponseEntity.badRequest().body("Blocked. Cannot save question.");
        }

        // Check if already saved
        List<SavedQuestion> existing = savedRepo.findByUser(user);
        boolean alreadySaved = existing.stream()
                                       .anyMatch(sq -> sq.getQuestion().getId().equals(questionId));
        if (alreadySaved) {
            return ResponseEntity.ok("Already saved this question");
        }

        // Create new saved record
        SavedQuestion sq = new SavedQuestion(user, question);
        savedRepo.save(sq);

        return ResponseEntity.ok("Question saved for later");
    }

    /**
     * Unsave a question
     */
    public ResponseEntity<?> unsaveQuestion(String username, Long questionId) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        List<SavedQuestion> userSaved = savedRepo.findByUser(user);

        Optional<SavedQuestion> toRemove = userSaved.stream()
            .filter(sq -> sq.getQuestion().getId().equals(questionId))
            .findFirst();

        if (toRemove.isEmpty()) {
            return ResponseEntity.ok("Question is not in your saved list");
        }

        savedRepo.delete(toRemove.get());
        return ResponseEntity.ok("Question unsaved successfully");
    }

    /**
     * Get all questions saved by user, skipping any that might be blocked from them.
     */
    public List<Question> getSavedQuestions(String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new ArrayList<>();
        }
        User user = userOpt.get();
        List<SavedQuestion> saved = savedRepo.findByUser(user);
        List<Question> results = new ArrayList<>();
        for(SavedQuestion sq : saved) {
            Question q = sq.getQuestion();
            // Skip blocked
            if(!blockingService.isBlockedEitherWay(user, q.getUser()) && q.isApproved()) {
                results.add(q);
            }
        }
        return results;
    }
}
