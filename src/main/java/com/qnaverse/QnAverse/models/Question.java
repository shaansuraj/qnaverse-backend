package com.qnaverse.QnAverse.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a question entity in the database.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who posted the question
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Removed the old `List<String> tags` approach
    // Instead, we define a one-to-many to the bridging table "question_tags"
    @JsonManagedReference
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags = new HashSet<>();

    // Path to media if uploaded (image/video)
    private String mediaUrl;

    // Admin must approve before visible
    private boolean approved = false;

    // We'll store an integer count for total likes to facilitate quick sorting
    private int likes = 0;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Question(User user, String content) {
        this.user = user;
        this.content = content;
    }
}
