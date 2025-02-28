package com.qnaverse.QnAverse.dto;

import java.util.Date;
import java.util.List;

public class QuestionDTO {
    private Long id;
    private String content;
    private String username;
    private Date createdAt;
    private List<String> tags;
    private String mediaUrl;
    private int likes;
    private int answerCount;
    private boolean userHasLiked;
    private boolean isFollowing;
    private boolean isBlocked;

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
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public String getMediaUrl() {
        return mediaUrl;
    }
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
    public int getAnswerCount() {
        return answerCount;
    }
    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }
    public boolean isUserHasLiked() {
        return userHasLiked;
    }
    public void setUserHasLiked(boolean userHasLiked) {
        this.userHasLiked = userHasLiked;
    }
    public boolean isFollowing() {
        return isFollowing;
    }
    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
    public boolean isBlocked() {
        return isBlocked;
    }
    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
