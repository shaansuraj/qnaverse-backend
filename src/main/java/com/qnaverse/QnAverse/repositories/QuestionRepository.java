package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qnaverse.QnAverse.models.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByApprovedTrueOrderByCreatedAtDesc();

    // Replaces old "searchByKeyword" with a new join on questionTags -> tag
    @Query("""
           SELECT DISTINCT q
           FROM Question q
           LEFT JOIN q.questionTags qt
           LEFT JOIN qt.tag t
           WHERE q.approved = true
             AND (
               LOWER(q.content) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(t.tagName) LIKE LOWER(CONCAT('%', :query, '%'))
             )
           ORDER BY q.likes DESC
           """)
    List<Question> searchByKeywordOrTag(@Param("query") String query);

    // Find all questions by a list of userIds
    @Query("""
           SELECT q FROM Question q
           WHERE q.user.id IN :userIds 
             AND q.approved = true
           ORDER BY q.createdAt DESC
           """)
    List<Question> findByUserIdsApproved(@Param("userIds") List<Long> userIds);

    // A simpler approach for trending by the built-in 'likes' field
    @Query("SELECT q FROM Question q WHERE q.approved = true ORDER BY q.likes DESC")
    List<Question> findTrendingAll();

    // Filter by tag if provided
    @Query("""
           SELECT DISTINCT q
           FROM Question q
           LEFT JOIN q.questionTags qt
           LEFT JOIN qt.tag t
           WHERE q.approved = true
             AND LOWER(t.tagName) = LOWER(:tag)
           ORDER BY q.likes DESC
           """)
    List<Question> findTrendingByTag(@Param("tag") String tag);
    
}
