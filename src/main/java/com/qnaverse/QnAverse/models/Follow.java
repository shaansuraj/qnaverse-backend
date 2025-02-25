package com.qnaverse.QnAverse.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "follows")
@IdClass(FollowKey.class) 
public class Follow {

    @Id
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @Id
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }
}
