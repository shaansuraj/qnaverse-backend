package com.qnaverse.QnAverse.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Composite PK for "question_tags" bridging entity.
 * question_id (Long) + tag_id (Integer).
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class QuestionTagKey implements Serializable {
    private Long question;
    private Integer tag;
}
