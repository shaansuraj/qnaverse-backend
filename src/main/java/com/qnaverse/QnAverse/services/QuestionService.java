// package com.qnaverse.QnAverse.services;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.stream.Collectors;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.qnaverse.QnAverse.dto.QuestionDTO;
// import com.qnaverse.QnAverse.exceptions.ResourceNotFoundException;
// import com.qnaverse.QnAverse.models.Like;
// import com.qnaverse.QnAverse.models.Question;
// import com.qnaverse.QnAverse.models.QuestionTag;
// import com.qnaverse.QnAverse.models.Tag;
// import com.qnaverse.QnAverse.models.User;
// import com.qnaverse.QnAverse.repositories.FollowRepository;
// import com.qnaverse.QnAverse.repositories.LikeRepository;
// import com.qnaverse.QnAverse.repositories.QuestionRepository;
// import com.qnaverse.QnAverse.repositories.QuestionTagRepository;
// import com.qnaverse.QnAverse.repositories.TagRepository;
// import com.qnaverse.QnAverse.repositories.UserRepository;
// import com.qnaverse.QnAverse.utils.FileStorageUtil;

// @Service
// public class QuestionService {

//     private final QuestionRepository questionRepository;
//     private final UserRepository userRepository;
//     private final FollowRepository followRepository;
//     private final BlockingService blockingService;
//     private final TagRepository tagRepository;
//     private final QuestionTagRepository questionTagRepository;
//     private final FileStorageUtil fileStorageUtil;
//     private final NotificationService notificationService;
//     private final LikeRepository likeRepository;

//     public QuestionService(QuestionRepository questionRepository,
//                            UserRepository userRepository,
//                            FollowRepository followRepository,
//                            BlockingService blockingService,
//                            TagRepository tagRepository,
//                            QuestionTagRepository questionTagRepository,
//                            FileStorageUtil fileStorageUtil,
//                            NotificationService notificationService,
//                            LikeRepository likeRepository) {
//         this.questionRepository = questionRepository;
//         this.userRepository = userRepository;
//         this.followRepository = followRepository;
//         this.blockingService = blockingService;
//         this.tagRepository = tagRepository;
//         this.questionTagRepository = questionTagRepository;
//         this.fileStorageUtil = fileStorageUtil;
//         this.notificationService = notificationService;
//         this.likeRepository = likeRepository;
//     }

    // /**
    //  * Creates a new question (pending admin approval) with optional media and tags.
    //  * Also parses the question content for @username mentions and notifies those users.
    //  */
    // public ResponseEntity<?> createQuestion(String username, String content, List<String> tags, MultipartFile media) {
    //     Optional<User> userOptional = userRepository.findByUsername(username);
    //     if (userOptional.isEmpty()) {
    //         return ResponseEntity.badRequest().body("User not found");
    //     }
    //     User user = userOptional.get();
    //     Question question = new Question(user, content);
    //     question.setApproved(false); // Pending approval
    //     question.setCreatedAt(new java.util.Date());

    //     if (media != null && !media.isEmpty()) {
    //         String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
    //         question.setMediaUrl(mediaUrl);
    //     }
    //     questionRepository.save(question);

    //     if (tags != null && !tags.isEmpty()) {
    //         for (String tagStr : tags) {
    //             if (tagStr == null || tagStr.isBlank())
    //                 continue;
    //             Tag found = tagRepository.findByTagNameIgnoreCase(tagStr.trim()).orElse(null);
    //             if (found == null) {
    //                 found = new Tag(tagStr.trim());
    //                 found = tagRepository.save(found);
    //             }
    //             QuestionTag qt = new QuestionTag(question, found, found.getTagName());
    //             questionTagRepository.save(qt);
    //             question.getQuestionTags().add(qt);
    //         }
    //     }

    //     notifyMentionedUsers(user, question);

    //     return ResponseEntity.ok("Question submitted for approval.");
    // }

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
    
    import com.qnaverse.QnAverse.dto.QuestionDTO;
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
        private final ModeratorService moderatorService; // Using the new moderator service
    
        public QuestionService(QuestionRepository questionRepository,
                               UserRepository userRepository,
                               FollowRepository followRepository,
                               BlockingService blockingService,
                               TagRepository tagRepository,
                               QuestionTagRepository questionTagRepository,
                               FileStorageUtil fileStorageUtil,
                               NotificationService notificationService,
                               LikeRepository likeRepository,
                               ModeratorService moderatorService) {
            this.questionRepository = questionRepository;
            this.userRepository = userRepository;
            this.followRepository = followRepository;
            this.blockingService = blockingService;
            this.tagRepository = tagRepository;
            this.questionTagRepository = questionTagRepository;
            this.fileStorageUtil = fileStorageUtil;
            this.notificationService = notificationService;
            this.likeRepository = likeRepository;
            this.moderatorService = moderatorService;
        }
    
        /**
         * Creates a new question with optional media and tags.
         * It directly calls the RapidAPI moderation endpoints to check the text and image.
         * If both text and media (if provided) are safe, the question is auto-approved.
         * Otherwise, it remains pending admin approval and the user is notified with details.
         */
        // public ResponseEntity<?> createQuestion(String username, String content, List<String> tags, MultipartFile media) {
        //     Optional<User> userOptional = userRepository.findByUsername(username);
        //     if (userOptional.isEmpty()) {
        //         return ResponseEntity.badRequest().body("User not found");
        //     }
        //     User user = userOptional.get();
        //     Question question = new Question(user, content);
        //     question.setCreatedAt(new Date());
            
        //     boolean textSafe = false;
        //     String moderationNotification = "";
        //     try {
        //         Map<String, Object> textModeration = moderatorService.moderateText(content);
        //         textSafe = (Boolean) textModeration.get("safe");
        //         if (!textSafe && textModeration.containsKey("flaggedCategories")) {
        //             moderationNotification += "Text flagged for: " + textModeration.get("flaggedCategories") + ". ";
        //         }
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
            
        //     boolean mediaSafe = true;
        //     if (media != null && !media.isEmpty()) {
        //         try {
        //             Map<String, Object> imageModeration = moderatorService.moderateImage(media);
        //             mediaSafe = (Boolean) imageModeration.get("safe");
        //             if (!mediaSafe) {
        //                 moderationNotification += "Image content flagged. ";
        //             }
        //             if (mediaSafe) {
        //                 String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
        //                 question.setMediaUrl(mediaUrl);
        //             }
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //             mediaSafe = false;
        //         }
        //     }
            
        //     if (textSafe && mediaSafe) {
        //         question.setApproved(true);
        //     } else {
        //         question.setApproved(false);
        //         String notifyMsg = "Your question contains sensitive content: ";
        //         if (!textSafe) {
        //             notifyMsg += "Text issues. ";
        //         }
        //         if (!mediaSafe) {
        //             notifyMsg += "Image issues. ";
        //         }
        //         notifyMsg += "It is under review.";
        //         notificationService.createNotification(user.getUsername(), notifyMsg);
        //     }
            
        //     questionRepository.save(question);
            
        //     if (tags != null && !tags.isEmpty()) {
        //         for (String tagStr : tags) {
        //             if (tagStr == null || tagStr.isBlank())
        //                 continue;
        //             Tag found = tagRepository.findByTagNameIgnoreCase(tagStr.trim()).orElse(null);
        //             if (found == null) {
        //                 found = new Tag(tagStr.trim());
        //                 found = tagRepository.save(found);
        //             }
        //             QuestionTag qt = new QuestionTag(question, found, found.getTagName());
        //             questionTagRepository.save(qt);
        //             question.getQuestionTags().add(qt);
        //         }
        //     }
            
        //     notifyMentionedUsers(user, question);
            
        //     String responseMessage = question.isApproved() ? "Question submitted and auto-approved." : "Question submitted for admin review due to sensitive content.";
        //     return ResponseEntity.ok(responseMessage);
        // }

        // public ResponseEntity<?> createQuestion(String username, String content, List<String> tags, MultipartFile media) {
        //     Optional<User> userOptional = userRepository.findByUsername(username);
        //     if (userOptional.isEmpty()) {
        //         return ResponseEntity.badRequest().body("User not found");
        //     }
        //     User user = userOptional.get();
        //     Question question = new Question(user, content);
        //     question.setCreatedAt(new Date());
    
        //     // 1) Moderate text
        //     boolean textSafe = false;
        //     String moderationNotification = "";
        //     try {
        //         Map<String, Object> textResult = moderatorService.moderateText(content);
        //         textSafe = (Boolean) textResult.get("safe");
        //         if (!textSafe && textResult.containsKey("flaggedCategories")) {
        //             moderationNotification += "Text flagged for: " + textResult.get("flaggedCategories") + ". ";
        //         }
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
    
        //     // 2) Moderate image if present
        //     boolean mediaSafe = true;
        //     if (media != null && !media.isEmpty()) {
        //         try {
        //             Map<String, Object> imageResult = moderatorService.moderateImage(media);
        //             mediaSafe = (Boolean) imageResult.get("safe");
        //             if (!mediaSafe && imageResult.containsKey("flaggedCategories")) {
        //                 moderationNotification += "Image flagged for: " + imageResult.get("flaggedCategories") + ". ";
        //             }
        //             // If image is safe, upload to Cloudinary
        //             if (mediaSafe) {
        //                 String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
        //                 question.setMediaUrl(mediaUrl);
        //             }
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //             mediaSafe = false; // If moderation call fails, treat as unsafe
        //         }
        //     }
    
        //     // 3) Final approval logic
        //     if (textSafe && mediaSafe) {
        //         question.setApproved(true);
        //     } else {
        //         question.setApproved(false);
        //         String notifyMsg = "Your question contains sensitive content: ";
        //         if (!textSafe) {
        //             notifyMsg += "Text issues. ";
        //         }
        //         if (!mediaSafe) {
        //             notifyMsg += "Image issues. ";
        //         }
        //         notifyMsg += "It is under review.";
        //         notificationService.createNotification(user.getUsername(), notifyMsg);
        //     }
    
        //     questionRepository.save(question);
    
        //     // 4) Save tags
        //     if (tags != null && !tags.isEmpty()) {
        //         for (String tagStr : tags) {
        //             if (tagStr == null || tagStr.isBlank()) continue;
        //             Tag found = tagRepository.findByTagNameIgnoreCase(tagStr.trim()).orElse(null);
        //             if (found == null) {
        //                 found = new Tag(tagStr.trim());
        //                 found = tagRepository.save(found);
        //             }
        //             QuestionTag qt = new QuestionTag(question, found, found.getTagName());
        //             questionTagRepository.save(qt);
        //             question.getQuestionTags().add(qt);
        //         }
        //     }
    
        //     // 5) Notify any mentioned users
        //     notifyMentionedUsers(user, question);
    
        //     String responseMessage = question.isApproved()
        //         ? "Question submitted and auto-approved."
        //         : "Question submitted for admin review due to sensitive content.";
        //     return ResponseEntity.ok(responseMessage);
        // }
    

        public ResponseEntity<?> createQuestion(String username, String content, List<String> tags, MultipartFile media) {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOptional.get();
            Question question = new Question(user, content);
            question.setCreatedAt(new Date());
        
            boolean textSafe = true;
            boolean imageSafe = true;
            StringBuilder flaggedInfo = new StringBuilder();
        
            // 1) Moderate text content using the ModeratorService.
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> textResult = (Map<String, Object>) moderatorService.moderateText(content);
                textSafe = (Boolean) textResult.get("safe");
                if (!textSafe && textResult.containsKey("flaggedCategories")) {
                    flaggedInfo.append("Text flagged for: ").append(textResult.get("flaggedCategories")).append(". ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                textSafe = false;
            }
        
            // 2) If media is provided, upload it to Cloudinary and moderate via URL.
            String uploadedMediaUrl = null;
            if (media != null && !media.isEmpty()) {
                uploadedMediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> imageResult = (Map<String, Object>) moderatorService.moderateImageUrl(uploadedMediaUrl);
                    imageSafe = (Boolean) imageResult.get("safe");
                    if (!imageSafe && imageResult.containsKey("flaggedCategories")) {
                        flaggedInfo.append("Image flagged for: ").append(imageResult.get("flaggedCategories")).append(". ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imageSafe = false;
                }
            }
        
            // 3) Decide approval status. If any content is flagged, mark question unapproved
            // but retain the media URL so the admin can review it.
            if (textSafe && imageSafe) {
                question.setApproved(true);
            } else {
                question.setApproved(false);
                String notifyMsg = "Your question contains sensitive content: " + flaggedInfo.toString() + "It is under review.";
                notificationService.createNotification(user.getUsername(), notifyMsg);
            }
            if (uploadedMediaUrl != null) {
                question.setMediaUrl(uploadedMediaUrl);
            }
        
            questionRepository.save(question);
        
            // 4) Save tags if provided
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
        
            // 5) Notify any mentioned users found in the question content.
            notifyMentionedUsers(user, question);
        
            String responseMessage = question.isApproved()
                    ? "Question submitted and auto-approved."
                    : "Question submitted for admin review due to sensitive content.";
            return ResponseEntity.ok(responseMessage);
        }

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
    * Unapprove a question (Admin only).
     */
    public ResponseEntity<?> unapproveQuestion(Long questionId) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
    // Delete the question instead of simply unapproving it
        questionRepository.deleteQuestionById(questionId);
        return ResponseEntity.ok("Question unapproved and deleted.");
        }

    /**
     * Returns the feed for a user – combining questions from followed users and trending questions.
     * Returns a Map with two keys: "followingQuestions" and "trendingQuestions", each as a List of QuestionDTO.
     */
    public ResponseEntity<?> getUserFeed(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User currentUser = userOpt.get();
        List<Long> followedIds = new ArrayList<>();
        followRepository.findByFollower(currentUser).forEach(f -> {
            if (!blockingService.isBlockedEitherWay(currentUser, f.getFollowing())) {
                followedIds.add(f.getFollowing().getId());
            }
        });
        List<Question> followingQuestions = followedIds.isEmpty()
                ? Collections.emptyList()
                : questionRepository.findByUserIdsApproved(followedIds);
        followingQuestions = filterBlocked(currentUser, followingQuestions);

        List<Question> trendingQuestions = questionRepository.findTrendingAll();
        trendingQuestions = filterBlocked(currentUser, trendingQuestions);
        if (trendingQuestions.size() > 20) {
            trendingQuestions = trendingQuestions.subList(0, 20);
        }

        List<QuestionDTO> followingDTOs = followingQuestions.stream()
            .map(q -> createQuestionDTO(q, currentUser))
            .collect(Collectors.toList());

        List<QuestionDTO> trendingDTOs = trendingQuestions.stream()
            .map(q -> createQuestionDTO(q, currentUser))
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("followingQuestions", followingDTOs);
        result.put("trendingQuestions", trendingDTOs);

        return ResponseEntity.ok(result);
    }

    private QuestionDTO createQuestionDTO(Question q, User currentUser) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setUsername(q.getUser().getUsername());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setMediaUrl(q.getMediaUrl());
        dto.setLikes(q.getLikes());
        dto.setAnswerCount(q.getAnswerCount());
        dto.setProfilePicture(q.getUser().getProfilePicture());

    
        // Set the userHasLiked flag based on whether the currentUser has liked the question
        boolean hasLiked = likeRepository.findByUserAndQuestion(currentUser, q).isPresent();
        dto.setUserHasLiked(hasLiked);  // This should correctly set whether the user has liked the question or not
    
        // Set following and blocking status
        boolean isFollowing = followRepository.findByFollowerAndFollowing(currentUser, q.getUser()).isPresent();
        dto.setIsFollowing(isFollowing);
        
        boolean isBlocked = blockingService.isBlockedEitherWay(currentUser, q.getUser());
        dto.setIsBlocked(isBlocked);
    
        // Set tags
        List<String> tags = q.getQuestionTags().stream()
                .map(QuestionTag::getTags)
                .collect(Collectors.toList());
        dto.setTags(tags);
    
        return dto;
    }
    

    private List<Question> filterBlocked(User viewer, List<Question> questions) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : questions) {
            if (!blockingService.isBlockedEitherWay(viewer, q.getUser())) {
                filtered.add(q);
            }
        }
        return filtered;
    }

    public ResponseEntity<List<QuestionDTO>> getTrendingQuestionsDTO(String tag, String viewerUsername) {
        Optional<User> viewerOpt = userRepository.findByUsername(viewerUsername);
        User currentUser = viewerOpt.orElse(null);
        
        List<Question> questions;
        if (tag != null && !tag.isBlank()) {
            questions = questionRepository.findTrendingByTag(tag.trim());
        } else {
            questions = questionRepository.findTrendingAll();
        }
        // Optionally limit size if needed
        if (questions.size() > 20) {
            questions = questions.subList(0, 20);
        }
        
        List<QuestionDTO> dtos = questions.stream()
                .map(q -> createQuestionDTO(q, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    

    public ResponseEntity<?> getQuestionDetails(Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isPresent()) {
            return ResponseEntity.ok(questionOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
    }

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

    // public ResponseEntity<?> editQuestion(Long questionId, String content, List<String> tags, MultipartFile media, String username) {
    //     Optional<Question> questionOpt = questionRepository.findById(questionId);
    //     if (questionOpt.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
    //     }
    //     Question question = questionOpt.get();
    //     if (!question.getUser().getUsername().equals(username)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit another user's question");
    //     }
        
    //     // Update question content and media if provided
    //     question.setContent(content);
    //     if (media != null && !media.isEmpty()) {
    //         if (question.getMediaUrl() != null && !question.getMediaUrl().isEmpty()) {
    //             fileStorageUtil.deleteFromCloudinary(question.getMediaUrl());
    //         }
    //         String mediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
    //         question.setMediaUrl(mediaUrl);
    //     }
        
    //     // Clear existing tag associations before adding new ones
    //     if (question.getQuestionTags() != null && !question.getQuestionTags().isEmpty()) {
    //         // Remove associations from DB first
    //         question.getQuestionTags().forEach(qt -> questionTagRepository.delete(qt));
    //         question.getQuestionTags().clear();
    //     }
        
    //     // If tags are provided, add them
    //     if (tags != null && !tags.isEmpty()) {
    //         // Use a Set to filter out duplicate tag names from the list
    //         Set<String> uniqueTags = tags.stream()
    //                                      .map(String::trim)
    //                                      .filter(tag -> !tag.isBlank())
    //                                      .collect(Collectors.toSet());
    //         for (String tagStr : uniqueTags) {
    //             // Check if the tag already exists
    //             Tag found = tagRepository.findByTagNameIgnoreCase(tagStr).orElse(null);
    //             if (found == null) {
    //                 // Only create a new tag if it doesn't exist
    //                 found = new Tag(tagStr);
    //                 found = tagRepository.save(found);
    //             }
    //             // Create the association using the existing or newly created tag
    //             QuestionTag qt = new QuestionTag(question, found, found.getTagName());
    //             questionTagRepository.save(qt);
    //             question.getQuestionTags().add(qt);
    //         }
    //     }
        
    //     questionRepository.save(question);
    //     return ResponseEntity.ok("Question edited successfully");
    // } 


    /**
     * Edits a question and re-runs moderation. If flagged, question remains unapproved.
     */
    // public ResponseEntity<?> editQuestion(Long questionId, String content, List<String> tags, MultipartFile media, String username) {
    //     Optional<Question> questionOpt = questionRepository.findById(questionId);
    //     if (questionOpt.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
    //     }
    //     Question question = questionOpt.get();
    //     if (!question.getUser().getUsername().equals(username)) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit another user's question");
    //     }

    //     // 1) Moderate new text
    //     boolean textSafe = true;
    //     boolean mediaSafe = true;
    //     StringBuilder flaggedInfo = new StringBuilder();

    //     try {
    //         Map<String, Object> textResult = moderatorService.moderateText(content);
    //         textSafe = (Boolean) textResult.get("safe");
    //         if (!textSafe && textResult.containsKey("flaggedCategories")) {
    //             flaggedInfo.append("Text flagged for: ").append(textResult.get("flaggedCategories")).append(". ");
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         textSafe = false;
    //     }

    //     // 2) If new media is provided, moderate it
    //     String newMediaUrl = null;
    //     if (media != null && !media.isEmpty()) {
    //         try {
    //             Map<String, Object> imageResult = moderatorService.moderateImage(media);
    //             mediaSafe = (Boolean) imageResult.get("safe");
    //             if (!mediaSafe && imageResult.containsKey("flaggedCategories")) {
    //                 flaggedInfo.append("Image flagged for: ").append(imageResult.get("flaggedCategories")).append(". ");
    //             }
    //             // If safe, upload to Cloudinary
    //             if (mediaSafe) {
    //                 // Delete old media from Cloudinary if it exists
    //                 if (question.getMediaUrl() != null && !question.getMediaUrl().isEmpty()) {
    //                     fileStorageUtil.deleteFromCloudinary(question.getMediaUrl());
    //                 }
    //                 newMediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
    //             }
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             mediaSafe = false;
    //         }
    //     }

    //     // 3) If text and image are safe, approve; else unapprove + notify
    //     if (textSafe && mediaSafe) {
    //         question.setApproved(true);
    //     } else {
    //         question.setApproved(false);
    //         String notifyMsg = "Your edited question was flagged: " + flaggedInfo.toString() + "It is under review.";
    //         notificationService.createNotification(username, notifyMsg);
    //     }

    //     // 4) Update content + media
    //     question.setContent(content);
    //     if (newMediaUrl != null) {
    //         question.setMediaUrl(newMediaUrl);
    //     }

    //     // 5) Clear existing tag associations before adding new ones
    //     if (question.getQuestionTags() != null && !question.getQuestionTags().isEmpty()) {
    //         question.getQuestionTags().forEach(qt -> questionTagRepository.delete(qt));
    //         question.getQuestionTags().clear();
    //     }

    //     // 6) Re-add tags
    //     if (tags != null && !tags.isEmpty()) {
    //         Set<String> uniqueTags = tags.stream()
    //                 .map(String::trim)
    //                 .filter(tag -> !tag.isBlank())
    //                 .collect(Collectors.toSet());
    //         for (String tagStr : uniqueTags) {
    //             Tag found = tagRepository.findByTagNameIgnoreCase(tagStr).orElse(null);
    //             if (found == null) {
    //                 found = new Tag(tagStr);
    //                 found = tagRepository.save(found);
    //             }
    //             QuestionTag qt = new QuestionTag(question, found, found.getTagName());
    //             questionTagRepository.save(qt);
    //             question.getQuestionTags().add(qt);
    //         }
    //     }

    //     questionRepository.save(question);
    //     return ResponseEntity.ok("Question edited successfully");
    // }

        /**
     * Edits a question and re-runs moderation.
     * If flagged, the question remains unapproved, but the new image (if any) is retained for admin review.
     */
    public ResponseEntity<?> editQuestion(Long questionId, String content, List<String> tags, MultipartFile media, String username) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
        Question question = questionOpt.get();
        if (!question.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot edit another user's question");
        }
    
        boolean textSafe = true;
        boolean imageSafe = true;
        StringBuilder flaggedInfo = new StringBuilder();
    
        // 1) Moderate new text content.
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> textResult = (Map<String, Object>) moderatorService.moderateText(content);
            textSafe = (Boolean) textResult.get("safe");
            if (!textSafe && textResult.containsKey("flaggedCategories")) {
                flaggedInfo.append("Text flagged for: ").append(textResult.get("flaggedCategories")).append(". ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            textSafe = false;
        }
    
        // 2) If a new image is provided, upload and moderate it.
        String newMediaUrl = null;
        if (media != null && !media.isEmpty()) {
            newMediaUrl = fileStorageUtil.saveToCloudinary(media, "question_media");
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> imageResult = (Map<String, Object>) moderatorService.moderateImageUrl(newMediaUrl);
                imageSafe = (Boolean) imageResult.get("safe");
                if (!imageSafe && imageResult.containsKey("flaggedCategories")) {
                    flaggedInfo.append("Image flagged for: ").append(imageResult.get("flaggedCategories")).append(". ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageSafe = false;
            }
        }
    
        // 3) Set approval status.
        if (textSafe && imageSafe) {
            question.setApproved(true);
            if (newMediaUrl != null) {
                // Delete the old image only if it exists and is different from the new one.
                if (question.getMediaUrl() != null && !question.getMediaUrl().isEmpty() &&
                        !question.getMediaUrl().equals(newMediaUrl)) {
                    fileStorageUtil.deleteFromCloudinary(question.getMediaUrl());
                }
                question.setMediaUrl(newMediaUrl);
            }
        } else {
            question.setApproved(false);
            // Do not delete the new image if flagged so that admin can review it.
            String notifyMsg = "Your edited question was flagged: " + flaggedInfo.toString() + "It is under review.";
            notificationService.createNotification(username, notifyMsg);
        }
    
        // 4) Update text content.
        question.setContent(content);
    
        // 5) Clear existing tags and add new ones.
        if (question.getQuestionTags() != null && !question.getQuestionTags().isEmpty()) {
            question.getQuestionTags().forEach(qt -> questionTagRepository.delete(qt));
            question.getQuestionTags().clear();
        }
        if (tags != null && !tags.isEmpty()) {
            Set<String> uniqueTags = tags.stream()
                    .map(String::trim)
                    .filter(tag -> !tag.isBlank())
                    .collect(Collectors.toSet());
            for (String tagStr : uniqueTags) {
                Tag found = tagRepository.findByTagNameIgnoreCase(tagStr).orElse(null);
                if (found == null) {
                    found = new Tag(tagStr);
                    found = tagRepository.save(found);
                }
                QuestionTag qt = new QuestionTag(question, found, found.getTagName());
                questionTagRepository.save(qt);
                question.getQuestionTags().add(qt);
            }
        }
    
        questionRepository.save(question);
        return ResponseEntity.ok("Question edited successfully");
    }
    /**
     * Delete a question (for the question owner).
     */
    public ResponseEntity<?> deleteQuestion(String username, Long questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question not found");
        }
        Question question = questionOpt.get();
        if (!question.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot delete another user's question");
        }
        if (question.getMediaUrl() != null && !question.getMediaUrl().isEmpty()) {
            fileStorageUtil.deleteFromCloudinary(question.getMediaUrl());
        }
        questionRepository.delete(question);
        return ResponseEntity.ok("Question deleted successfully");
    }
    
}
