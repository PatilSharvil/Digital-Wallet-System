package com.digitalwallet.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.digitalwallet.dao.FraudLogDAO;
import com.digitalwallet.dao.TransactionDAO;
import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.FraudLog;
import com.digitalwallet.model.Transaction;

public class FraudDetectionService {
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    private FraudLogDAO fraudLogDAO;
    
    // Thresholds for fraud detection
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000.00");
    private static final int RAPID_TRANSACTION_THRESHOLD = 5; // Number of transactions
    private static final int RAPID_TRANSACTION_TIME_WINDOW = 10; // Minutes
    private static final int UNUSUAL_PATTERN_THRESHOLD = 3; // Number of unusual patterns
    
    public FraudDetectionService() {
        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
        this.fraudLogDAO = new FraudLogDAO();
    }
    
    /**
     * Analyzes all transactions for potential fraud
     * @return List of fraud logs
     */
    public List<FraudLog> analyzeAllTransactions() throws SQLException {
        List<FraudLog> fraudLogs = new ArrayList<>();
        List<Transaction> allTransactions = transactionDAO.getAllTransactions();
        
        // Get existing fraud logs to avoid duplicates
        List<FraudLog> existingLogs = fraudLogDAO.getAllFraudLogs();
        Set<String> existingLogKeys = new HashSet<>();
        
        // Create unique keys for existing logs to check for duplicates
        for (FraudLog log : existingLogs) {
            existingLogKeys.add(createLogKey(log));
        }
        
        // Group transactions by user
        Map<Integer, List<Transaction>> userTransactions = new HashMap<>();
        for (Transaction transaction : allTransactions) {
            userTransactions.computeIfAbsent(transaction.getUserId(), k -> new ArrayList<>())
                           .add(transaction);
        }
        
        // Analyze each user's transactions
        for (Map.Entry<Integer, List<Transaction>> entry : userTransactions.entrySet()) {
            int userId = entry.getKey();
            List<Transaction> userTxn = entry.getValue();
            
            // Check for high-value transactions
            List<FraudLog> highValueLogs = detectHighValueTransactions(userId, userTxn);
            fraudLogs.addAll(highValueLogs);
            
            // Check for rapid transactions
            List<FraudLog> rapidTxnLogs = detectRapidTransactions(userId, userTxn);
            fraudLogs.addAll(rapidTxnLogs);
            
            // Check for unusual patterns
            List<FraudLog> unusualPatternLogs = detectUnusualPatterns(userId, userTxn);
            fraudLogs.addAll(unusualPatternLogs);
        }
        
        // Filter out duplicates and save only new fraud logs
        List<FraudLog> newFraudLogs = new ArrayList<>();
        for (FraudLog log : fraudLogs) {
            String logKey = createLogKey(log);
            if (!existingLogKeys.contains(logKey)) {
                newFraudLogs.add(log);
                existingLogKeys.add(logKey); // Add to set to prevent duplicates within the same batch
            }
        }
        
        // Save all new detected fraud logs
        for (FraudLog log : newFraudLogs) {
            fraudLogDAO.addFraudLog(log);
        }
        
        return newFraudLogs;
    }
    
    /**
     * Creates a unique key for a fraud log to check for duplicates
     */
    private String createLogKey(FraudLog log) {
        // Create a unique key based on user ID and issue
        return log.getUserId() + ":" + log.getIssue();
    }
    
    /**
     * Detects high-value transactions that exceed the threshold
     */
    private List<FraudLog> detectHighValueTransactions(int userId, List<Transaction> transactions) {
        List<FraudLog> fraudLogs = new ArrayList<>();
        
        for (Transaction transaction : transactions) {
            if (transaction.getAmount().compareTo(HIGH_VALUE_THRESHOLD) > 0) {
                String issue = "High-value transaction detected: " + transaction.getAmount() + 
                              " (Transaction ID: " + transaction.getTransactionId() + ")";
                fraudLogs.add(new FraudLog(userId, issue, LocalDateTime.now()));
            }
        }
        
        return fraudLogs;
    }
    
    /**
     * Detects rapid transactions within a short time window
     */
    private List<FraudLog> detectRapidTransactions(int userId, List<Transaction> transactions) {
        List<FraudLog> fraudLogs = new ArrayList<>();
        
        // Sort transactions by date
        transactions.sort((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
        
        // Check for rapid transactions within the time window
        for (int i = 0; i < transactions.size(); i++) {
            int count = 1;
            Date startDate = transactions.get(i).getCreatedAt();
            LocalDateTime startTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            
            for (int j = i + 1; j < transactions.size(); j++) {
                Date currentDate = transactions.get(j).getCreatedAt();
                LocalDateTime currentTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long minutesDiff = ChronoUnit.MINUTES.between(startTime, currentTime);
                
                if (minutesDiff <= RAPID_TRANSACTION_TIME_WINDOW) {
                    count++;
                } else {
                    break;
                }
            }
            
            if (count >= RAPID_TRANSACTION_THRESHOLD) {
                String issue = "Rapid transactions detected: " + count + " transactions in " + 
                              RAPID_TRANSACTION_TIME_WINDOW + " minutes";
                fraudLogs.add(new FraudLog(userId, issue, LocalDateTime.now()));
                break; // Only log once per pattern
            }
        }
        
        return fraudLogs;
    }
    
    /**
     * Detects unusual transaction patterns
     */
    private List<FraudLog> detectUnusualPatterns(int userId, List<Transaction> transactions) {
        List<FraudLog> fraudLogs = new ArrayList<>();
        
        // Check for multiple transactions to the same recipient
        Map<Integer, Integer> recipientCount = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("TRANSFER")) {
                // Assuming receiver_id is stored in the description field for simplicity
                // In a real implementation, you would use the actual receiver_id field
                String description = transaction.getDescription();
                try {
                    int receiverId = Integer.parseInt(description.split(":")[1].trim());
                    recipientCount.merge(receiverId, 1, Integer::sum);
                } catch (Exception e) {
                    // Skip if description doesn't contain receiver ID
                }
            }
        }
        
        // Check if any recipient has received multiple transactions
        for (Map.Entry<Integer, Integer> entry : recipientCount.entrySet()) {
            if (entry.getValue() >= UNUSUAL_PATTERN_THRESHOLD) {
                String issue = "Multiple transactions to the same recipient detected: " + 
                              entry.getValue() + " transactions to user ID " + entry.getKey();
                fraudLogs.add(new FraudLog(userId, issue, LocalDateTime.now()));
            }
        }
        
        return fraudLogs;
    }
    
    /**
     * Gets all fraud logs
     */
    public List<FraudLog> getAllFraudLogs() throws SQLException {
        return fraudLogDAO.getAllFraudLogs();
    }
    
    /**
     * Gets fraud logs for a specific user
     */
    public List<FraudLog> getFraudLogsByUser(int userId) throws SQLException {
        return fraudLogDAO.getFraudLogsByUser(userId);
    }
} 