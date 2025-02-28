package com.qnaverse.QnAverse.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qnaverse.QnAverse.dto.ProfileResponse;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.FollowRepository;
import com.qnaverse.QnAverse.repositories.QuestionRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;
import com.qnaverse.QnAverse.utils.FileStorageUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BlockingService blockingService;
    private final QuestionRepository questionRepository;
    private final FileStorageUtil fileStorageUtil;

    public UserService(UserRepository userRepository,
                       FollowRepository followRepository,
                       BlockingService blockingService,
                       QuestionRepository questionRepository,
                       FileStorageUtil fileStorageUtil) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.blockingService = blockingService;
        this.questionRepository = questionRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    // Get User Profile with follow/block flags using the viewer parameter
    public ResponseEntity<?> getUserProfile(String username, String viewerUsername) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User profileUser = userOptional.get();
        boolean isFollowing = false;
        boolean isBlocked = false;
        if (viewerUsername != null && !viewerUsername.isBlank()) {
            Optional<User> viewerOpt = userRepository.findByUsername(viewerUsername);
            if (viewerOpt.isPresent()) {
                User viewer = viewerOpt.get();
                isFollowing = followRepository.findByFollowerAndFollowing(viewer, profileUser).isPresent();
                isBlocked = blockingService.isBlockedEitherWay(viewer, profileUser);
            }
        }
        ProfileResponse response = new ProfileResponse(profileUser,
                followRepository.countByFollowing(profileUser),
                followRepository.countByFollower(profileUser));
        response.setIsFollowing(isFollowing);
        response.setIsBlocked(isBlocked);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateUserProfile(String username, User updatedUser) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();
        user.setBio(updatedUser.getBio());
        user.setInstagramUrl(updatedUser.getInstagramUrl());
        user.setGithubUrl(updatedUser.getGithubUrl());
        user.setLinkedinUrl(updatedUser.getLinkedinUrl());
        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    public ResponseEntity<?> updateProfilePicture(String username, MultipartFile file) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid file");
        }
        User user = userOptional.get();
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            fileStorageUtil.deleteFromCloudinary(user.getProfilePicture());
        }
        String mediaUrl = fileStorageUtil.saveToCloudinary(file, "profile_pictures");
        user.setProfilePicture(mediaUrl);
        userRepository.save(user);
        return ResponseEntity.ok("Profile picture updated successfully");
    }

    // Updated: Return posts only if the viewer follows the profile (if not the same user)
    public ResponseEntity<?> getUserQuestions(String username, String viewerUsername) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User profileUser = userOpt.get();
        if (viewerUsername != null && !viewerUsername.isBlank()) {
            Optional<User> viewerOpt = userRepository.findByUsername(viewerUsername);
            if (viewerOpt.isPresent()) {
                User viewer = viewerOpt.get();
                // If viewer is not the profile owner, require a follow relationship
                if (!viewer.getUsername().equals(profileUser.getUsername())) {
                    boolean isFollowing = followRepository.findByFollowerAndFollowing(viewer, profileUser).isPresent();
                    if (!isFollowing) {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }
                // Check blocking status
                if (blockingService.isBlockedEitherWay(viewer, profileUser)) {
                    return ResponseEntity.ok(Collections.emptyList());
                }
            }
        }
        List<Question> posts = questionRepository.findByUserIdsApproved(List.of(profileUser.getId()));
        return ResponseEntity.ok(posts);
    }
}
