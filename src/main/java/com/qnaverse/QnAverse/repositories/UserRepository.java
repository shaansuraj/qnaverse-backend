package com.qnaverse.QnAverse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String username);
}
