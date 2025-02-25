package com.qnaverse.QnAverse.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Composite key for answer_votes (user_id + answer_id).
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class AnswerVoteKey implements Serializable {
    private Long user;
    private Long answer;
}
