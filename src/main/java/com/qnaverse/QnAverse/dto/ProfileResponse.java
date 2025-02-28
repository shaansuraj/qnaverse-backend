package com.qnaverse.QnAverse.dto;

import com.qnaverse.QnAverse.models.User;

public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePicture;
    private String instagramUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String role;
    private long followerCount;
    private long followingCount;
    private boolean isFollowing;
    private boolean isBlocked;

    public ProfileResponse() {
    }

    public ProfileResponse(User user, long followerCount, long followingCount) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.profilePicture = user.getProfilePicture();
        this.instagramUrl = user.getInstagramUrl();
        this.githubUrl = user.getGithubUrl();
        this.linkedinUrl = user.getLinkedinUrl();
        this.role = user.getRole().name();
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getInstagramUrl() {
        return instagramUrl;
    }
    
    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }
    
    public String getGithubUrl() {
        return githubUrl;
    }
    
    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }
    
    public String getLinkedinUrl() {
        return linkedinUrl;
    }
    
    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public long getFollowerCount() {
        return followerCount;
    }
    
    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }
    
    public long getFollowingCount() {
        return followingCount;
    }
    
    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
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
