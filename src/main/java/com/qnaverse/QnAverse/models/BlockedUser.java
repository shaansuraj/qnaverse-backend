package com.qnaverse.QnAverse.models;

import java.util.Date;

import jakarta.persistence.Column;
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
@Table(name="blocked_users")
@IdClass(BlockedKey.class)
public class BlockedUser {

    @Id
    @ManyToOne
    @JoinColumn(name="blocker_id", nullable=false)
    private User blocker;

    @Id
    @ManyToOne
    @JoinColumn(name="blocked_id", nullable=false)
    private User blocked;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at", insertable=false, updatable=false)
    private Date createdAt;

    public BlockedUser(User blocker, User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
