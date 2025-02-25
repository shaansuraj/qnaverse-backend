package com.qnaverse.QnAverse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.BlockedKey;
import com.qnaverse.QnAverse.models.BlockedUser;
import com.qnaverse.QnAverse.models.User;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, BlockedKey> {
    Optional<BlockedUser> findByBlockerAndBlocked(User blocker, User blocked);

    List<BlockedUser> findByBlocker(User blocker);

    // We also want all relations where user is blocked or is blocking
    List<BlockedUser> findByBlocked(User blocked);
}
