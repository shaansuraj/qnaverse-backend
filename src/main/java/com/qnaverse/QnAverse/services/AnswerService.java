// package com.qnaverse.QnAverse.services;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.qnaverse.QnAverse.dto.AnswerDTO;
// import com.qnaverse.QnAverse.models.Answer;
// import com.qnaverse.QnAverse.models.AnswerVote;
// import com.qnaverse.QnAverse.models.AnswerVote.VoteType;
// import com.qnaverse.QnAverse.models.Question;
// import com.qnaverse.QnAverse.models.User;
// import com.qnaverse.QnAverse.repositories.AnswerRepository;
// import com.qnaverse.QnAverse.repositories.AnswerVoteRepository;
// import com.qnaverse.QnAverse.repositories.QuestionRepository;
// import com.qnaverse.QnAverse.repositories.UserRepository;

// @Service
// public class AnswerService {

//     private final AnswerRepository answerRepository;
//     private final QuestionRepository questionRepository;
//     private final UserRepository userRepository;
//     private final AnswerVoteRepository answerVoteRepository;
//     private final BlockingService blockingService;
//     private final NotificationService notificationService;

//     public AnswerService(AnswerRepository answerRepository,
//                          QuestionRepository questionRepository,
//                          UserRepository userRepository,
//                          AnswerVoteRepository answerVoteRepository,
//                          BlockingService blockingService,
//                          NotificationService notificationService) {
//         this.answerRepository = answerRepository;
//         this.questionRepository = questionRepository;
//         this.userRepository = userRepository;
//         this.answerVoteRepository = answerVoteRepository;
//         this.blockingService = blockingService;
//         this.notificationService = notificationService;
//     }

//     /**
//      * Submits an answer to a question and notifies the question owner.
//      * Also scans the answer content for @username mentions and notifies those users.
//      */
//     public ResponseEntity<?> submitAnswer(String username, Long questionId, AnswerDTO answerDTO) {
//         Optional<User> userOptional = userRepository.findByUsername(username);
//         Optional<Question> questionOptional = questionRepository.findById(questionId);
//         if (userOptional.isEmpty() || questionOptional.isEmpty()) {
//             return ResponseEntity.badRequest().body("Invalid user or question");
//         }
//         User user = userOptional.get();
//         Question question = questionOptional.get();
//         if (blockingService.isBlockedEitherWay(user, question.getUser())) {
//             return ResponseEntity.badRequest().body("You are blocked or the user is blocked. Cannot answer.");
//         }
//         if (!question.isApproved()) {
//             return ResponseEntity.badRequest().body("Question not yet approved by admin.");
//         }
//         Answer answer = new Answer(question, user, answerDTO.getContent());
//         answerRepository.save(answer);
        
//         // Notify question owner about new answer
//         notificationService.createNotification(question.getUser().getUsername(),
//                 "Your question was answered by " + username + ".");
//         // Notify any mentioned users in the answer content
//         notifyMentionedUsers(user, answer);
//         // Increment persisted answer count on the question
//         question.setAnswerCount(question.getAnswerCount() + 1);
//         questionRepository.save(question);
        
//         return ResponseEntity.ok("Answer submitted successfully.");
//     }


package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ModeratorService moderatorService; // Inject moderator service

    public AnswerService(AnswerRepository answerRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository,
                         AnswerVoteRepository answerVoteRepository,
                         BlockingService blockingService,
                         NotificationService notificationService,
                         ModeratorService moderatorService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerVoteRepository = answerVoteRepository;
        this.blockingService = blockingService;
        this.notificationService = notificationService;
        this.moderatorService = moderatorService;
    }

    /**
     * Submits an answer to a question.
     * Moderates the answer text; if it is flagged as sensitive, returns an error message (with flagged categories if available).
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

        // Moderate answer text using the direct call to RapidAPI via ModeratorService
        boolean textSafe = false;
        String flaggedInfo = "";
        try {
            // The moderator service returns a Map with keys "safe" and optionally "flaggedCategories"
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> moderationResult = (java.util.Map<String, Object>) moderatorService.moderateText(answerDTO.getContent());
            textSafe = (Boolean) moderationResult.get("safe");
            if (!textSafe && moderationResult.containsKey("flaggedCategories")) {
                flaggedInfo = " Flagged for: " + moderationResult.get("flaggedCategories");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!textSafe) {
            return ResponseEntity.badRequest()
                    .body("Your answer contains sensitive data." + flaggedInfo + " Please ensure your content is professional.");
        }

        Answer answer = new Answer(question, user, answerDTO.getContent());
        answerRepository.save(answer);

        // Notify question owner about new answer
        notificationService.createNotification(question.getUser().getUsername(),
                "Your question was answered by " + username + ".");
        // Notify any mentioned users in the answer content
        notifyMentionedUsers(user, answer);
        // Increment persisted answer count on the question
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionRepository.save(question);

        return ResponseEntity.ok("Answer submitted successfully.");
    }


    /**
     * Parses the answer content for @username mentions and sends notifications.
     */
    private void notifyMentionedUsers(User answerer, Answer answer) {
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(answer.getContent());
        List<String> mentionedUsernames = new ArrayList<>();
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
     * Returns a list of AnswerDTO.
     */
    public ResponseEntity<?> getAnswers(Long questionId, String currentUsername) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
        Question question = questionOptional.get();
        if (blockingService.isBlockedEitherWay(currentUsername, question.getUser().getUsername())) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<Answer> answers = answerRepository.findByQuestionVisible(question);
        answers.removeIf(ans -> blockingService.isBlockedEitherWay(currentUsername, ans.getUser().getUsername()));
        List<AnswerDTO> dtos = new ArrayList<>();
        for (Answer ans : answers) {
            dtos.add(convertToDTO(ans, currentUsername));
        }
        return ResponseEntity.ok(dtos);
    }

    private AnswerDTO convertToDTO(Answer ans, String currentUsername) {
        AnswerDTO dto = new AnswerDTO();
        dto.setId(ans.getId());
        dto.setContent(ans.getContent());
        dto.setUsername(ans.getUser().getUsername());
        dto.setProfilePicture(ans.getUser().getProfilePicture()); // Include profile picture from user
        dto.setCreatedAt(ans.getCreatedAt());
        long upvoteCount = answerVoteRepository.countByAnswerAndVoteType(ans, VoteType.UP);
        long downvoteCount = answerVoteRepository.countByAnswerAndVoteType(ans, VoteType.DOWN);
        dto.setUpvotes((int) upvoteCount);
        dto.setDownvotes((int) downvoteCount);
        // Check if the current user has voted on this answer
        Optional<AnswerVote> voteOpt = answerVoteRepository.findByUserAndAnswer(
                userRepository.findByUsername(currentUsername).orElse(null), ans);
        dto.setHasUpvoted(voteOpt.isPresent() && voteOpt.get().getVoteType() == VoteType.UP);
        dto.setHasDownvoted(voteOpt.isPresent() && voteOpt.get().getVoteType() == VoteType.DOWN);
        return dto;
    }

    /**
     * Upvotes an answer. If the user already downvoted, it switches to upvote.
     * Returns an updated AnswerDTO.
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
        return ResponseEntity.ok(convertToDTO(answer, username));
    }

    /**
     * Downvotes an answer. If the user already upvoted, it switches to downvote.
     * Returns an updated AnswerDTO.
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
        return ResponseEntity.ok(convertToDTO(answer, username));
    }

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