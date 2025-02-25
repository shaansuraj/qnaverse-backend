package com.qnaverse.QnAverse.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Composite key for the 'likes' table (user_id + question_id).
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class LikeKey implements Serializable {
    private Long user;      // user_id
    private Long question;  // question_id
}
