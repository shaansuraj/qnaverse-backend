package com.qnaverse.QnAverse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.qnaverse.QnAverse.models.QuestionTag;
import com.qnaverse.QnAverse.models.QuestionTagKey;

public interface QuestionTagRepository extends JpaRepository<QuestionTag, QuestionTagKey> {
    // Typically no custom methods needed unless you want special queries
}
