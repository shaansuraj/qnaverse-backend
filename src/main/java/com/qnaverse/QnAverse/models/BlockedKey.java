package com.qnaverse.QnAverse.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BlockedKey implements Serializable {
    private Long blocker;
    private Long blocked;
}
