package com.qnaverse.QnAverse.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qnaverse.QnAverse.exceptions.ResourceNotFoundException;
import com.qnaverse.QnAverse.models.Like;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.QuestionTag;
import com.qnaverse.QnAverse.models.Tag;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.FollowRepository;
import com.qnaverse.QnAverse.repositories.LikeRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.QuestionTagRepository;
import com.qnaverse.QnAverse.repositories.TagRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;
import com.qnaverse.QnAverse.utils.FileStorageUtil;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BlockingService blockingService;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final FileStorageUtil fileStorageUtil;
    private final NotificationService notificationService;
    private final LikeRepository likeRepository;

    public QuestionService(QuestionRepository questionRepository,
                           UserRepository userRepository,
                           FollowRepository followRepository,
                           BlockingService blockingService,
                           TagRepository tagRepository,
                           QuestionTagRepository questionTagRepository,
                           FileStorageUtil fileStorageUtil,
                           NotificationService notificationService,
                           LikeRepository likeRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.blockingService = blockingService;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.fileStorageUtil = fileStorageUtil;
        this.notificationService = notificationService;
        this.likeRepository = likeRepository;
    }

    /**
     * Creates a new question (pending admin approval) with optional media and tags.
     * Also parses the question content for @username mentions and notifies those users.
     */
    public ResponseEntity<?> createQuestion(String username, String content, List<String> tags, MultipartFile media) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();
        Question question = new Question(user, content);
        question.setApproved(false); // Pending approval
        question.setCreatedAt(new Date());

        // Handle media upload using Cloudinary
        if (media != null && !media.isEmpty()) {
            String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
            question.setMediaUrl(mediaUrl);
        }

        // Save the question
        questionRepository.save(question);

        // Process tags if provided
        if (tags != null && !tags.isEmpty()) {
            for (String tagStr : tags) {
                if (tagStr == null || tagStr.isBlank())
                    continue;
                Tag found = tagRepository.findByTagNameIgnoreCase(tagStr.trim()).orElse(null);
                if (found == null) {
                    found = new Tag(tagStr.trim());
                    found = tagRepository.save(found);
                }
                QuestionTag qt = new QuestionTag(question, found, found.getTagName());
                questionTagRepository.save(qt);
                question.getQuestionTags().add(qt);
            }
        }

        // Parse content for @username mentions and notify the mentioned users
        notifyMentionedUsers(user, question);

        return ResponseEntity.ok("Question submitted for approval.");
    }

    /**
     * Parses the question content for @username mentions and sends notifications.
     */
    private void notifyMentionedUsers(User asker, Question question) {
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(question.getContent());
        Set<String> mentionedUsernames = new HashSet<>();
        while (matcher.find()) {
            mentionedUsernames.add(matcher.group(1));
        }
        for (String mentionedUsername : mentionedUsernames) {
            Optional<User> mentionedUserOpt = userRepository.findByUsername(mentionedUsername);
            if (mentionedUserOpt.isPresent()) {
                notificationService.createNotification(mentionedUsername,
                        "You were mentioned in a question by " + asker.getUsername() + ".");
            }
        }
    }

    /**
     * Approves a question (Admin only).
     */
    public ResponseEntity<?> approveQuestion(Long questionId) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
        Question question = questionOptional.get();
        question.setApproved(true);
        questionRepository.save(question);
        return ResponseEntity.ok("Question approved.");
    }

    /**
     * Returns the feed for a user – combining questions from followed users and trending questions.
     */
    public ResponseEntity<?> getUserFeed(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        List<Long> followedIds = new ArrayList<>();
        followRepository.findByFollower(user).forEach(f -> {
            if (!blockingService.isBlockedEitherWay(user, f.getFollowing())) {
                followedIds.add(f.getFollowing().getId());
            }
        });
        List<Question> followingQuestions = followedIds.isEmpty() ? Collections.emptyList() : questionRepository.findByUserIdsApproved(followedIds);
        followingQuestions = filterBlocked(user, followingQuestions);

        List<Question> trendingQuestions = questionRepository.findTrendingAll();
        trendingQuestions = filterBlocked(user, trendingQuestions);
        if (trendingQuestions.size() > 20) {
            trendingQuestions = trendingQuestions.subList(0, 20);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("followingQuestions", followingQuestions);
        result.put("trendingQuestions", trendingQuestions);

        return ResponseEntity.ok(result);
    }

    /**
     * Returns trending questions overall or filtered by tag.
     */
    public ResponseEntity<List<Question>> getTrendingQuestions(String tag) {
        List<Question> questions;
        if (tag != null && !tag.isBlank()) {
            questions = questionRepository.findTrendingByTag(tag.trim());
        } else {
            questions = questionRepository.findTrendingAll();
        }
        return ResponseEntity.ok(questions);
    }

    /**
     * Helper method to filter out questions from blocked users.
     */
    private List<Question> filterBlocked(User viewer, List<Question> questions) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : questions) {
            if (!blockingService.isBlockedEitherWay(viewer, q.getUser())) {
                filtered.add(q);
            }
        }
        return filtered;
    }
    
    /**
     * Retrieves unapproved questions for the admin dashboard.
     */
    public ResponseEntity<List<Question>> getUnapprovedQuestions() {
        List<Question> unapproved = questionRepository.findAll().stream()
                .filter(q -> !q.isApproved())
                .toList();
        return ResponseEntity.ok(unapproved);
    }

    /**
     * Retrieves the details of a question, including media URL.
     */
    public ResponseEntity<?> getQuestionDetails(Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isPresent()) {
            return ResponseEntity.ok(questionOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
    }

    /**
     * Retrieves the users who liked a particular question.
     */
    public List<User> getLikersForQuestion(Long questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new ResourceNotFoundException("Question not found");
        }
        
        Question question = questionOpt.get();
        List<Like> likes = likeRepository.findByQuestion(question);
        return likes.stream()
                    .map(Like::getUser)
                    .collect(Collectors.toList());
    }

    /**
     * Edit an existing question (including media update if provided).
     */
    public ResponseEntity<?> editQuestion(Long questionId, String content, List<String> tags, MultipartFile media, String username) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }

        Question question = questionOpt.get();
        
        // Check if the user is the one who posted the question
        if (!question.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit another user's question");
        }

        // Update content
        question.setContent(content);

        // Handle media update if provided
        if (media != null && !media.isEmpty()) {
            // Delete old media if it exists
            if (question.getMediaUrl() != null && !question.getMediaUrl().isEmpty()) {
                fileStorageUtil.deleteFromCloudinary(question.getMediaUrl());
            }

            // Upload new media to Cloudinary
            String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
            question.setMediaUrl(mediaUrl);
        }

        // Update tags if provided
        if (tags != null && !tags.isEmpty()) {
            questionTagRepository.deleteAll(question.getQuestionTags());
            question.getQuestionTags().clear();
            for (String tagStr : tags) {
                Tag found = tagRepository.findByTagNameIgnoreCase(tagStr.trim()).orElse(null);
                if (found == null) {
                    found = new Tag(tagStr.trim());
                    found = tagRepository.save(found);
                }
                QuestionTag qt = new QuestionTag(question, found, found.getTagName());
                questionTagRepository.save(qt);
                question.getQuestionTags().add(qt);
            }
        }

        // Save the updated question
        questionRepository.save(question);
        return ResponseEntity.ok("Question edited successfully");
    }
}
