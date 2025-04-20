package com.digitalwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.digitalwallet.model.FraudLog;
import com.digitalwallet.util.DatabaseUtil;

public class FraudLogDAO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Adds a new fraud log to the database
     */
    public void addFraudLog(FraudLog fraudLog) throws SQLException {
        String sql = "INSERT INTO fraud_logs (user_id, issue, timestamp) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, fraudLog.getUserId());
            pstmt.setString(2, fraudLog.getIssue());
            pstmt.setString(3, fraudLog.getTimestamp().format(DATE_FORMATTER));
            
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fraudLog.setLogId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Gets all fraud logs from the database
     */
    public List<FraudLog> getAllFraudLogs() throws SQLException {
        String sql = "SELECT f.*, u.username FROM fraud_logs f " +
                    "JOIN users u ON f.user_id = u.user_id " +
                    "ORDER BY f.timestamp DESC";
        
        List<FraudLog> fraudLogs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                FraudLog fraudLog = mapResultSetToFraudLog(rs);
                fraudLogs.add(fraudLog);
            }
        }
        
        return fraudLogs;
    }
    
    /**
     * Gets fraud logs for a specific user
     */
    public List<FraudLog> getFraudLogsByUser(int userId) throws SQLException {
        String sql = "SELECT f.*, u.username FROM fraud_logs f " +
                    "JOIN users u ON f.user_id = u.user_id " +
                    "WHERE f.user_id = ? " +
                    "ORDER BY f.timestamp DESC";
        
        List<FraudLog> fraudLogs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FraudLog fraudLog = mapResultSetToFraudLog(rs);
                    fraudLogs.add(fraudLog);
                }
            }
        }
        
        return fraudLogs;
    }
    
    /**
     * Maps a ResultSet row to a FraudLog object
     */
    private FraudLog mapResultSetToFraudLog(ResultSet rs) throws SQLException {
        FraudLog fraudLog = new FraudLog();
        fraudLog.setLogId(rs.getInt("log_id"));
        fraudLog.setUserId(rs.getInt("user_id"));
        fraudLog.setIssue(rs.getString("issue"));
        fraudLog.setTimestamp(LocalDateTime.parse(rs.getString("timestamp"), DATE_FORMATTER));
        fraudLog.setUserName(rs.getString("username"));
        return fraudLog;
    }
} 