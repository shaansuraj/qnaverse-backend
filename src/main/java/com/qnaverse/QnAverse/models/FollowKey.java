package com.qnaverse.QnAverse.models;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class FollowKey implements Serializable {
    private Long follower;
    private Long following;
}
