package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Basic query for a question's answers (ordered by creation ascending)
    @Query("SELECT a FROM Answer a WHERE a.question = ?1 AND a.hidden = false ORDER BY a.createdAt ASC")
    List<Answer> findByQuestionVisible(Question question);
}
