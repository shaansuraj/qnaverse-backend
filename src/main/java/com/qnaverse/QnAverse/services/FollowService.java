package com.qnaverse.QnAverse.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.Follow;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.FollowRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowService(FollowRepository followRepository,
                         UserRepository userRepository,
                         NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ResponseEntity<?> followUser(String followerUsername, String followingUsername) {
        Optional<User> followerOpt = userRepository.findByUsername(followerUsername);
        Optional<User> followingOpt = userRepository.findByUsername(followingUsername);

        if (followerOpt.isEmpty() || followingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid follower or following user");
        }

        User follower = followerOpt.get();
        User following = followingOpt.get();

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            return ResponseEntity.badRequest().body("You are already following this user");
        }

        Follow follow = new Follow(follower, following);
        followRepository.save(follow);
        // Create a notification for the followed user
        notificationService.createNotification(following.getUsername(), "You have a new follower: " + follower.getUsername());
        return ResponseEntity.ok("Followed successfully");
    }

    public ResponseEntity<?> unfollowUser(String followerUsername, String followingUsername) {
        Optional<User> followerOpt = userRepository.findByUsername(followerUsername);
        Optional<User> followingOpt = userRepository.findByUsername(followingUsername);

        if (followerOpt.isEmpty() || followingOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid follower or following user");
        }

        User follower = followerOpt.get();
        User following = followingOpt.get();

        followRepository.deleteByFollowerAndFollowing(follower, following);
        return ResponseEntity.ok("Unfollowed successfully");
    }

    public ResponseEntity<?> getFollowers(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        List<Follow> followers = followRepository.findByFollowing(user);
        return ResponseEntity.ok(followers);
    }

    public ResponseEntity<?> getFollowing(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        List<Follow> following = followRepository.findByFollower(user);
        return ResponseEntity.ok(following);
    }
}
