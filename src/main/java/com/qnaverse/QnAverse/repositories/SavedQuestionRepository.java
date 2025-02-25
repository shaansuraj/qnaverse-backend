package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.SavedQuestion;
import com.qnaverse.QnAverse.models.User;

public interface SavedQuestionRepository extends JpaRepository<SavedQuestion, Long> {
    List<SavedQuestion> findByUser(User user);
}
