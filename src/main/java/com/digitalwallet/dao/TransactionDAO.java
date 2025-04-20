package com.digitalwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.digitalwallet.model.Transaction;
import com.digitalwallet.util.DatabaseUtil;

public class TransactionDAO {
    
    public Transaction createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, amount, type, description, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setString(3, transaction.getType());
            pstmt.setString(4, transaction.getDescription());
            pstmt.setString(5, transaction.getStatus());
            pstmt.setTimestamp(6, new java.sql.Timestamp(transaction.getCreatedAt().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
            
            return transaction;
        }
    }
    
    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.name as user_name FROM transactions t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.user_id = ? ORDER BY t.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransactionWithUserName(rs));
                }
            }
        }
        return transactions;
    }
    
    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.name as user_name FROM transactions t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "ORDER BY t.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransactionWithUserName(rs));
            }
        }
        return transactions;
    }
    
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setType(rs.getString("type"));
        transaction.setDescription(rs.getString("description"));
        transaction.setStatus(rs.getString("status"));
        transaction.setCreatedAt(rs.getTimestamp("created_at"));
        return transaction;
    }

    private Transaction mapResultSetToTransactionWithUserName(ResultSet rs) throws SQLException {
        Transaction transaction = mapResultSetToTransaction(rs);
        transaction.setUserName(rs.getString("user_name"));
        return transaction;
    }
    
    public List<Transaction> getRecentTransactions(int limit) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.name as user_name FROM transactions t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "ORDER BY t.created_at DESC LIMIT ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransactionWithUserName(rs));
                }
            }
        }
        return transactions;
    }
    
    public List<Transaction> getTransactionsByType(String type) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, u.name as user_name FROM transactions t " +
                    "JOIN users u ON t.user_id = u.user_id " +
                    "WHERE t.type = ? ORDER BY t.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransactionWithUserName(rs));
                }
            }
        }
        return transactions;
    }
} 