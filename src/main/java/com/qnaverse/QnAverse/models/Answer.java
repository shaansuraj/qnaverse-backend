package com.qnaverse.QnAverse.models;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an answer entity in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The question to which this answer belongs
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // The user who posted this answer
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Hide the answer if it gets auto-flagged or admin sets it hidden
    private boolean hidden = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Answer(Question question, User user, String content) {
        this.question = question;
        this.user = user;
        this.content = content;
    }
}
