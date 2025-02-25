package com.qnaverse.QnAverse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Follow;
import com.qnaverse.QnAverse.models.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);

    void deleteByFollowerAndFollowing(User follower, User following);

    // For counting
    long countByFollowing(User user); // how many followers
    long countByFollower(User user);  // how many following
}
