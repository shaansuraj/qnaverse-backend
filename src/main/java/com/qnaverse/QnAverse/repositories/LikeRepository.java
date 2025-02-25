package com.qnaverse.QnAverse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Like;
import com.qnaverse.QnAverse.models.LikeKey;
import com.qnaverse.QnAverse.models.Question;
import com.qnaverse.QnAverse.models.User;

public interface LikeRepository extends JpaRepository<Like, LikeKey> {
    Optional<Like> findByUserAndQuestion(User user, Question question);
    List<Like> findByUser(User user);
    long countByQuestion(Question question);
    
    // NEW: List all Like records for the given Question
    List<Like> findByQuestion(Question question);
}
