package com.qnaverse.QnAverse.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Answer;
import com.qnaverse.QnAverse.models.AnswerVote;
import com.qnaverse.QnAverse.models.AnswerVoteKey;
import com.qnaverse.QnAverse.models.User;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, AnswerVoteKey> {
    Optional<AnswerVote> findByUserAndAnswer(User user, Answer answer);

    long countByAnswerAndVoteType(Answer answer, AnswerVote.VoteType voteType);

    List<AnswerVote> findByAnswer(Answer answer);
}
