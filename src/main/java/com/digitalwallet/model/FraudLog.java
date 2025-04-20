package com.digitalwallet.model;

import java.time.LocalDateTime;

public class FraudLog {
    private int logId;
    private int userId;
    private String issue;
    private LocalDateTime timestamp;
    private String userName; // For display purposes
    
    public FraudLog() {
    }
    
    public FraudLog(int userId, String issue, LocalDateTime timestamp) {
        this.userId = userId;
        this.issue = issue;
        this.timestamp = timestamp;
    }
    
    // Getters
    public int getLogId() {
        return logId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getIssue() {
        return issue;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getUserName() {
        return userName;
    }
    
    // Setters
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setIssue(String issue) {
        this.issue = issue;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    @Override
    public String toString() {
        return "FraudLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", issue='" + issue + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 