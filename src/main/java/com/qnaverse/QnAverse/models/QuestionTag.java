package com.qnaverse.QnAverse.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bridging entity for the question_tags table:
 *  question_id (PK, int)
 *  tag_id      (PK, int)
 *  tags        (varchar(255)) <-- textual copy of Tag.tagName
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "question_tags")
@IdClass(QuestionTagKey.class)
public class QuestionTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(name = "tags", nullable = false, length = 255)
    private String tags; // store the same as Tag.tagName or user input

    public QuestionTag(Question question, Tag tag, String tags) {
        this.question = question;
        this.tag = tag;
        this.tags = tags;
    }
}
