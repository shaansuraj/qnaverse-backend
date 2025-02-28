package com.qnaverse.QnAverse.dto;

import java.util.Date;

public class AnswerDTO {
    private Long id;
    private String content;
    private String username;
    private String profilePicture;  // NEW: to store the author's profile picture
    private Date createdAt;
    private int upvotes;
    private int downvotes;
    private boolean hasUpvoted;
    private boolean hasDownvoted;

    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public int getUpvotes() {
        return upvotes;
    }
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
    public int getDownvotes() {
        return downvotes;
    }
    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }
    public boolean isHasUpvoted() {
        return hasUpvoted;
    }
    public void setHasUpvoted(boolean hasUpvoted) {
        this.hasUpvoted = hasUpvoted;
    }
    public boolean isHasDownvoted() {
        return hasDownvoted;
    }
    public void setHasDownvoted(boolean hasDownvoted) {
        this.hasDownvoted = hasDownvoted;
    }
}
