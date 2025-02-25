package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.dto.AnswerDTO;
import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.AnswerVote;
import com.qnaverse.QnAverse.models.AnswerVote.VoteType;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.AnswerRepository;
import com.qnaverse.QnAverse.repositories.AnswerVoteRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerVoteRepository answerVoteRepository;
    private final BlockingService blockingService;
    private final NotificationService notificationService;

    public AnswerService(AnswerRepository answerRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository,
                         AnswerVoteRepository answerVoteRepository,
                         BlockingService blockingService,
                         NotificationService notificationService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerVoteRepository = answerVoteRepository;
        this.blockingService = blockingService;
        this.notificationService = notificationService;
    }

    /**
     * Submits an answer to a question and notifies the question owner.
     * Also scans the answer content for @username mentions and notifies those users.
     */
    public ResponseEntity<?> submitAnswer(String username, Long questionId, AnswerDTO answerDTO) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Question> questionOptional = questionRepository.findById(questionId);

        if (userOptional.isEmpty() || questionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user or question");
        }

        User user = userOptional.get();
        Question question = questionOptional.get();
        if (blockingService.isBlockedEitherWay(user, question.getUser())) {
            return ResponseEntity.badRequest().body("You are blocked or the user is blocked. Cannot answer.");
        }

        if (!question.isApproved()) {
            return ResponseEntity.badRequest().body("Question not yet approved by admin.");
        }

        Answer answer = new Answer(question, user, answerDTO.getContent());
        answerRepository.save(answer);

        // Notify the question owner about the new answer
        notificationService.createNotification(question.getUser().getUsername(),
                "Your question was answered by " + username + ".");

        // Scan answer content for @username mentions and notify those users
        notifyMentionedUsers(user, answer);

        return ResponseEntity.ok("Answer submitted successfully.");
    }

    /**
     * Parses the answer content for @username mentions and sends notifications.
     */
    private void notifyMentionedUsers(User answerer, Answer answer) {
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(answer.getContent());
        Set<String> mentionedUsernames = new HashSet<>();
        while (matcher.find()) {
            mentionedUsernames.add(matcher.group(1));
        }
        for (String mentionedUsername : mentionedUsernames) {
            Optional<User> mentionedUserOpt = userRepository.findByUsername(mentionedUsername);
            if (mentionedUserOpt.isPresent()) {
                notificationService.createNotification(mentionedUsername,
                        "You were mentioned in an answer by " + answerer.getUsername() + ".");
            }
        }
    }

    /**
     * Fetches all visible (non-hidden) answers for a given question.
     */
    public ResponseEntity<?> getAnswers(Long questionId, String currentUsername) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
        Question question = questionOptional.get();

        if (blockingService.isBlockedEitherWay(currentUsername, question.getUser().getUsername())) {
            return ResponseEntity.ok(List.of());
        }

        List<Answer> answers = answerRepository.findByQuestionVisible(question);
        answers.removeIf(ans ->
            blockingService.isBlockedEitherWay(currentUsername, ans.getUser().getUsername())
        );
        return ResponseEntity.ok(answers);
    }

    /**
     * Upvotes an answer.
     */
    public ResponseEntity<?> upvoteAnswer(Long answerId, String username) {
        Optional<Answer> answerOptional = answerRepository.findById(answerId);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (answerOptional.isEmpty() || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Answer or user not found");
        }

        Answer answer = answerOptional.get();
        User user = userOptional.get();

        if (blockingService.isBlockedEitherWay(user, answer.getUser())) {
            return ResponseEntity.badRequest().body("Blocked. Cannot upvote.");
        }
        if (answer.isHidden()) {
            return ResponseEntity.badRequest().body("Answer is hidden/removed.");
        }

        Optional<AnswerVote> existing = answerVoteRepository.findByUserAndAnswer(user, answer);
        if (existing.isPresent()) {
            AnswerVote vote = existing.get();
            if (vote.getVoteType() == VoteType.UP) {
                return ResponseEntity.ok("Already upvoted.");
            } else {
                vote.setVoteType(VoteType.UP);
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote newVote = new AnswerVote(user, answer, VoteType.UP);
            answerVoteRepository.save(newVote);
        }
        return ResponseEntity.ok("Answer upvoted.");
    }

    /**
     * Downvotes an answer.
     */
    public ResponseEntity<?> downvoteAnswer(Long answerId, String username) {
        Optional<Answer> answerOptional = answerRepository.findById(answerId);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (answerOptional.isEmpty() || userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Answer or user not found");
        }

        Answer answer = answerOptional.get();
        User user = userOptional.get();

        if (blockingService.isBlockedEitherWay(user, answer.getUser())) {
            return ResponseEntity.badRequest().body("Blocked. Cannot downvote.");
        }
        if (answer.isHidden()) {
            return ResponseEntity.badRequest().body("Answer is hidden/removed.");
        }

        Optional<AnswerVote> existing = answerVoteRepository.findByUserAndAnswer(user, answer);
        if (existing.isPresent()) {
            AnswerVote vote = existing.get();
            if (vote.getVoteType() == VoteType.DOWN) {
                return ResponseEntity.ok("Already downvoted.");
            } else {
                vote.setVoteType(VoteType.DOWN);
                answerVoteRepository.save(vote);
            }
        } else {
            AnswerVote newVote = new AnswerVote(user, answer, VoteType.DOWN);
            answerVoteRepository.save(newVote);
        }
        return ResponseEntity.ok("Answer downvoted.");
    }

    /**
     * Returns a list of users who upvoted the answer.
     */
    public List<User> getAnswerUpvoters(Long answerId) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            return new ArrayList<>();
        }
        Answer answer = answerOpt.get();
        List<AnswerVote> votes = answerVoteRepository.findByAnswer(answer);
        List<User> result = new ArrayList<>();
        for (AnswerVote vote : votes) {
            if (vote.getVoteType() == VoteType.UP) {
                result.add(vote.getUser());
            }
        }
        return result;
    }

    /**
     * Returns a list of users who downvoted the answer.
     */
    public List<User> getAnswerDownvoters(Long answerId) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);
        if (answerOpt.isEmpty()) {
            return new ArrayList<>();
        }
        Answer answer = answerOpt.get();
        List<AnswerVote> votes = answerVoteRepository.findByAnswer(answer);
        List<User> result = new ArrayList<>();
        for (AnswerVote vote : votes) {
            if (vote.getVoteType() == VoteType.DOWN) {
                result.add(vote.getUser());
            }
        }
        return result;
    }
}
