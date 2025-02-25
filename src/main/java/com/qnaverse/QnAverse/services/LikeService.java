package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.Like;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.LikeRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final BlockingService blockingService;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository,
                       QuestionRepository questionRepository,
                       UserRepository userRepository,
                       BlockingService blockingService,
                       NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.blockingService = blockingService;
        this.notificationService = notificationService;
    }

    public ResponseEntity<?> likeQuestion(String username, Long questionId) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        User user = userOpt.get();
        Question question = questionOpt.get();

        if (blockingService.isBlockedEitherWay(user, question.getUser())) {
            return ResponseEntity.badRequest().body("Blocked. Cannot like question.");
        }

        if (!question.isApproved()) {
            return ResponseEntity.badRequest().body("Question not yet approved by admin.");
        }

        Optional<Like> existing = likeRepository.findByUserAndQuestion(user, question);
        if (existing.isPresent()) {
            return ResponseEntity.ok("Already liked");
        }

        Like like = new Like(user, question);
        likeRepository.save(like);

        question.setLikes(question.getLikes() + 1);
        questionRepository.save(question);

        // Notify the question owner about the like
        notificationService.createNotification(question.getUser().getUsername(),
                "Your question was liked by " + username + ".");

        return ResponseEntity.ok("Question liked successfully");
    }

    public ResponseEntity<?> unlikeQuestion(String username, Long questionId) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }

        User user = userOpt.get();
        Question question = questionOpt.get();

        if (blockingService.isBlockedEitherWay(user, question.getUser())) {
            return ResponseEntity.badRequest().body("Blocked. Cannot unlike question.");
        }

        Optional<Like> existing = likeRepository.findByUserAndQuestion(user, question);
        if (existing.isEmpty()) {
            return ResponseEntity.ok("You have not liked this question yet");
        }

        likeRepository.delete(existing.get());

        if (question.getLikes() > 0) {
            question.setLikes(question.getLikes() - 1);
            questionRepository.save(question);
        }

        return ResponseEntity.ok("Question unliked successfully");
    }

    /**
     * Returns a list of users who liked the question.
     */
    public List<User> getQuestionLikers(Long questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return new ArrayList<>();
        }
        Question question = questionOpt.get();
        List<Like> likes = likeRepository.findByQuestion(question);
        List<User> result = new ArrayList<>();
        for (Like lk : likes) {
            result.add(lk.getUser());
        }
        return result;
    }
}
