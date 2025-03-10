package com.qnaverse.QnAverse.dto;

import java.util.Date;

public class BlockLogDTO {
    private Long id;
    private String blockerUsername;
    private String blockedUsername;
    private String action; // "BLOCKED" or "UNBLOCKED"
    private Date createdAt;

    public BlockLogDTO() {}

    public BlockLogDTO(Long id, String blockerUsername, String blockedUsername, String action, Date createdAt) {
        this.id = id;
        this.blockerUsername = blockerUsername;
        this.blockedUsername = blockedUsername;
        this.action = action;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBlockerUsername() {
        return blockerUsername;
    }
    public void setBlockerUsername(String blockerUsername) {
        this.blockerUsername = blockerUsername;
    }
    public String getBlockedUsername() {
        return blockedUsername;
    }
    public void setBlockedUsername(String blockedUsername) {
        this.blockedUsername = blockedUsername;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
