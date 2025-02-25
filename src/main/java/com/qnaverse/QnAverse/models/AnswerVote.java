package com.qnaverse.QnAverse.models;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a user's upvote/downvote on an Answer.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "answer_votes")
@IdClass(AnswerVoteKey.class)
public class AnswerVote {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    @Column(name="vote_type", nullable=false)
    private VoteType voteType; // UP or DOWN

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Date createdAt;

    public enum VoteType {
        UP, DOWN
    }

    public AnswerVote(User user, Answer answer, VoteType type) {
        this.user = user;
        this.answer = answer;
        this.voteType = type;
    }
}
