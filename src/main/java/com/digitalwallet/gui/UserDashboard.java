package com.digitalwallet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.digitalwallet.dao.TransactionDAO;
import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.Transaction;
import com.digitalwallet.model.User;

public class UserDashboard extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private JLabel balanceLabel;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private ImageIcon backgroundGif;
    private JLabel backgroundLabel;

    // Modern UI Colors and Fonts
    private Color primaryColor = new Color(79, 70, 229); // Indigo-600
    private Color successColor = new Color(22, 163, 74); // Green-600
    private Color warningColor = new Color(217, 119, 6); // Amber-600
    private Color dangerColor = new Color(220, 38, 38);  // Red-600
    private Color backgroundColor = new Color(230, 224, 212);  // White coffee color (#E6E0D4)
    private Color cardBackground = Color.WHITE;
    private Color textColor = new Color(17, 24, 39); // Gray-900
    private Color secondaryTextColor = new Color(107, 114, 128); // Gray-500
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font headingFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font mediumFont = new Font("Segoe UI", Font.BOLD, 14);

    public UserDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        initializeUI();
        loadTransactions();
    }

    private void initializeUI() {
        setTitle("Digital Wallet - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(backgroundColor);

        // Main panel with modern layout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with user info and balance
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with actions and transactions
        JSplitPane centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerPanel.setDividerLocation(300);
        centerPanel.setDividerSize(0); // Make divider invisible
        centerPanel.setBorder(null);
        centerPanel.setOpaque(false);
        centerPanel.setBackground(backgroundColor);

        // Left panel with action buttons
        JPanel actionsPanel = createActionsPanel();
        centerPanel.setLeftComponent(actionsPanel);

        // Right panel with transactions
        JPanel transactionsPanel = createTransactionsPanel();
        centerPanel.setRightComponent(transactionsPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // User info
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back,");
        welcomeLabel.setFont(regularFont);
        welcomeLabel.setForeground(secondaryTextColor);
        
        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(headingFont);
        nameLabel.setForeground(textColor);
        
        userInfoPanel.add(welcomeLabel, BorderLayout.NORTH);
        userInfoPanel.add(nameLabel, BorderLayout.CENTER);

        // Balance info
        JPanel balancePanel = new JPanel(new BorderLayout());
        balancePanel.setOpaque(false);
        
        JLabel balanceTitle = new JLabel("Current Balance");
        balanceTitle.setFont(regularFont);
        balanceTitle.setForeground(secondaryTextColor);
        balanceTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        
        balanceLabel = new JLabel("₹" + currentUser.getBalance().toString());
        balanceLabel.setFont(titleFont);
        balanceLabel.setForeground(primaryColor);
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        balancePanel.add(balanceTitle, BorderLayout.NORTH);
        balancePanel.add(balanceLabel, BorderLayout.CENTER);

        panel.add(userInfoPanel, BorderLayout.WEST);
        panel.add(balancePanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Add action buttons
        panel.add(createActionButton("Add Money", successColor, "➕"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createActionButton("Transfer Money", primaryColor, "↗"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createActionButton("Pay Bills", warningColor, "💰"));
        panel.add(Box.createVerticalStrut(15));
        panel.add(createActionButton("Logout", dangerColor, "←"));

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Transactions title
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(headingFont);
        titleLabel.setForeground(textColor);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Transactions table
        String[] columnNames = {"Date", "Type", "Amount", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(regularFont);
        transactionTable.setRowHeight(40);
        transactionTable.setShowGrid(true);  // Enable grid lines
        transactionTable.setGridColor(new Color(229, 231, 235));  // Light gray grid color
        transactionTable.setIntercellSpacing(new Dimension(1, 1));  // Add spacing between cells
        
        // Style the table header
        transactionTable.getTableHeader().setFont(mediumFont);
        transactionTable.getTableHeader().setBackground(new Color(243, 244, 246));  // Light gray background
        transactionTable.getTableHeader().setForeground(textColor);
        transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 45));  // Make header taller
        
        // Style the table content
        transactionTable.setSelectionBackground(new Color(243, 244, 246));  // Light gray selection
        transactionTable.setSelectionForeground(textColor);
        transactionTable.setBackground(cardBackground);
        transactionTable.setForeground(textColor);
        
        // Add borders to cells
        transactionTable.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        
        // Center align the header text
        ((DefaultTableCellRenderer)transactionTable.getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);
        
        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(cardBackground);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createStyledPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Enhanced shadow effect with more layers and stronger opacity
                int shadowSize = 18;  // Increased from 15
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = 0.32f - (i * 0.013f);  // Increased initial opacity and reduced fade rate
                    g2d.setColor(new Color(0, 0, 0, Math.max((int)(opacity * 255), 0)));
                    int offset = i * 2;
                    g2d.fill(new RoundRectangle2D.Float(
                        offset, 
                        offset, 
                        getWidth() - (offset * 2), 
                        getHeight() - (offset * 2), 
                        15, 
                        15
                    ));
                }
                
                // Card background
                g2d.setColor(cardBackground);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15));
            }
        };
    }

    private JButton createActionButton(String text, Color color, String icon) {
        JButton button = new JButton(icon + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced button shadow with more layers and stronger opacity
                int shadowSize = 8;  // Increased from 6
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = 0.35f - (i * 0.035f);  // Increased initial opacity and adjusted fade rate
                    g2d.setColor(new Color(0, 0, 0, Math.max((int)(opacity * 255), 0)));
                    int offset = i;
                    g2d.fillRoundRect(
                        offset, 
                        offset, 
                        getWidth() - (offset * 2), 
                        getHeight() - (offset * 2), 
                        10, 
                        10
                    );
                }
                
                // Button background with hover and press effects
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        
        button.setFont(mediumFont);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add padding to the button
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Add action listeners
        if (text.equals("Add Money")) {
            button.addActionListener(e -> showAddMoneyDialog());
        } else if (text.equals("Transfer Money")) {
            button.addActionListener(e -> showTransferDialog());
        } else if (text.equals("Pay Bills")) {
            button.addActionListener(e -> showPayBillsDialog());
        } else if (text.equals("Logout")) {
            button.addActionListener(e -> handleLogout());
        }

        return button;
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getTransactionsByUserId(currentUser.getUserId());
            tableModel.setRowCount(0);
            
            for (Transaction transaction : transactions) {
                tableModel.addRow(new Object[]{
                    new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a").format(transaction.getCreatedAt()),
                    transaction.getType(),
                    "₹" + transaction.getAmount().toString(),
                    transaction.getDescription()
                });
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading transactions: " + e.getMessage());
        }
    }

    private void showAddMoneyDialog() {
        JDialog dialog = new JDialog(this, "Add Money", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(backgroundColor);
        
        // Create main panel with padding for curved edges visibility
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(backgroundColor);
        
        JPanel panel = createStyledPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel titleLabel = new JLabel("Add Money to Wallet", SwingConstants.CENTER);
        titleLabel.setFont(headingFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(25));

        // Amount label
        JLabel amountLabel = new JLabel("Amount (₹)", SwingConstants.CENTER);
        amountLabel.setFont(mediumFont);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(amountLabel);
        panel.add(Box.createVerticalStrut(10));

        // Amount field
        JTextField amountField = createStyledTextField();
        amountField.setMaximumSize(new Dimension(340, 40));
        amountField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(25));

        // Add flexible space
        panel.add(Box.createVerticalGlue());

        // Buttons
        JButton addButton = createStyledButton("Add Money", successColor, Color.WHITE);
        addButton.setMaximumSize(new Dimension(340, 45));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(addButton);
        panel.add(Box.createVerticalStrut(12));

        JButton cancelButton = createStyledButton("Cancel", new Color(243, 244, 246), textColor);
        cancelButton.setMaximumSize(new Dimension(340, 45));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cancelButton);

        // Add flexible space
        panel.add(Box.createVerticalGlue());

        addButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    throw new NumberFormatException();
                }
                handleAddMoney(amount);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid amount");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        mainPanel.add(panel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void handleAddMoney(double amount) {
        try {
            BigDecimal amountDecimal = BigDecimal.valueOf(amount);
            userDAO.updateBalance(currentUser.getUserId(), amountDecimal);
            
            // Refresh current user data to get updated balance
            User updatedUser = userDAO.getUserById(currentUser.getUserId());
            if (updatedUser != null) {
                currentUser = updatedUser;
                balanceLabel.setText("₹" + currentUser.getBalance());
            }
            
            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setUserId(currentUser.getUserId());
            transaction.setAmount(amountDecimal);
            transaction.setType("DEPOSIT");
            transaction.setDescription("Money added to wallet");
            transaction.setStatus("COMPLETED");
            transaction.setCreatedAt(new java.util.Date());
            
            transactionDAO.createTransaction(transaction);
            loadTransactions();
            
            JOptionPane.showMessageDialog(this,
                    "Money added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showErrorDialog("Error: " + ex.getMessage());
        }
    }

    private void showTransferDialog() {
        JDialog dialog = new JDialog(this, "Transfer Money", true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(backgroundColor);
        
        // Create main panel with padding for curved edges visibility
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(backgroundColor);
        
        JPanel panel = createStyledPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel titleLabel = new JLabel("Transfer Money", SwingConstants.CENTER);
        titleLabel.setFont(headingFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(25));

        // Recipient label
        JLabel receiverLabel = new JLabel("Recipient Email", SwingConstants.CENTER);
        receiverLabel.setFont(mediumFont);
        receiverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(receiverLabel);
        panel.add(Box.createVerticalStrut(10));

        // Recipient field
        JTextField receiverField = createStyledTextField();
        receiverField.setMaximumSize(new Dimension(340, 40));
        receiverField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(receiverField);
        panel.add(Box.createVerticalStrut(20));

        // Amount label
        JLabel amountLabel = new JLabel("Amount (₹)", SwingConstants.CENTER);
        amountLabel.setFont(mediumFont);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(amountLabel);
        panel.add(Box.createVerticalStrut(10));

        // Amount field
        JTextField amountField = createStyledTextField();
        amountField.setMaximumSize(new Dimension(340, 40));
        amountField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(25));

        // Add flexible space
        panel.add(Box.createVerticalGlue());

        // Buttons
        JButton transferButton = createStyledButton("Transfer", primaryColor, Color.WHITE);
        transferButton.setMaximumSize(new Dimension(340, 45));
        transferButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(transferButton);
        panel.add(Box.createVerticalStrut(12));

        JButton cancelButton = createStyledButton("Cancel", new Color(243, 244, 246), textColor);
        cancelButton.setMaximumSize(new Dimension(340, 45));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cancelButton);

        // Add flexible space
        panel.add(Box.createVerticalGlue());

        transferButton.addActionListener(e -> {
            try {
                String receiverEmail = receiverField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());
                handleTransfer(receiverEmail, amount);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid amount");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        mainPanel.add(panel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean verifyPassword(String title) {
        // Create custom password dialog with increased size
        JDialog dialog = new JDialog(this, "Security Verification", true);
        dialog.setSize(600, 450);  // Increased size significantly
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(backgroundColor);
        
        // Create main panel with padding for curved edges visibility
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));  // Increased padding
        mainPanel.setBackground(backgroundColor);
        
        JPanel panel = createStyledPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(45, 45, 45, 45));  // Increased padding

        // Title with proper alignment
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleFont);  // Using larger title font
        titlePanel.add(titleLabel, BorderLayout.WEST);
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(45));  // Increased spacing

        // Password label and field with proper alignment
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Enter your password");
        passwordLabel.setFont(mediumFont);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        panel.add(passwordPanel);
        panel.add(Box.createVerticalStrut(20));  // Increased spacing

        // Create styled password field with increased width
        JPasswordField passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof EmptyBorder) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        passwordField.setFont(regularFont);
        passwordField.setMaximumSize(new Dimension(540, 50));  // Increased size
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)  // Increased padding
            )
        ));
        passwordField.setBackground(Color.WHITE);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(45));  // Increased spacing

        // Buttons with increased width
        JButton confirmButton = createStyledButton("Confirm", primaryColor, Color.WHITE);
        confirmButton.setMaximumSize(new Dimension(540, 55));  // Increased size
        panel.add(confirmButton);
        panel.add(Box.createVerticalStrut(20));  // Increased spacing

        JButton cancelButton = createStyledButton("Cancel", new Color(243, 244, 246), textColor);
        cancelButton.setMaximumSize(new Dimension(540, 55));  // Increased size
        panel.add(cancelButton);

        final boolean[] result = {false};

        confirmButton.addActionListener(e -> {
            try {
                String password = new String(passwordField.getPassword());
                if (userDAO.verifyPassword(currentUser.getUsername(), password)) {
                    result[0] = true;
                    dialog.dispose();
                } else {
                    showErrorDialog("Incorrect password. Please try again.");
                    passwordField.setText("");
                }
            } catch (SQLException ex) {
                showErrorDialog("Error verifying password: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        passwordField.addActionListener(e -> confirmButton.doClick());

        mainPanel.add(panel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);

        return result[0];
    }

    private void handleTransfer(String receiverEmail, double amount) {
        try {
            // First verify the password
            if (!verifyPassword("Confirm Money Transfer")) {
                return;
            }

            BigDecimal amountDecimal = BigDecimal.valueOf(amount);
            if (amountDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                showErrorDialog("Please enter a valid amount");
                return;
            }

            // Check if user has zero balance
            if (currentUser.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                showErrorDialog("Your balance is zero. Please add money to your wallet first.");
                return;
            }

            User receiver = userDAO.getUserByEmail(receiverEmail);
            if (receiver == null) {
                showErrorDialog("Recipient not found");
                return;
            }

            if (currentUser.getBalance().compareTo(amountDecimal) < 0) {
                showErrorDialog("Insufficient balance. Your current balance is ₹" + currentUser.getBalance());
                return;
            }

            // Update balances - subtract from sender
            userDAO.updateBalance(currentUser.getUserId(), amountDecimal.negate());
            // Add to receiver
            userDAO.updateBalance(receiver.getUserId(), amountDecimal);

            // Refresh current user data to get updated balance
            User updatedUser = userDAO.getUserById(currentUser.getUserId());
            if (updatedUser != null) {
                currentUser = updatedUser;
                balanceLabel.setText("₹" + currentUser.getBalance());
            }

            // Create transaction record for sender
            Transaction senderTransaction = new Transaction();
            senderTransaction.setUserId(currentUser.getUserId());
            senderTransaction.setAmount(amountDecimal.negate());
            senderTransaction.setType("TRANSFER_SENT");
            senderTransaction.setDescription("Transfer to " + receiver.getEmail());
            senderTransaction.setStatus("COMPLETED");
            senderTransaction.setCreatedAt(new java.util.Date());
            
            // Create transaction record for receiver
            Transaction receiverTransaction = new Transaction();
            receiverTransaction.setUserId(receiver.getUserId());
            receiverTransaction.setAmount(amountDecimal);
            receiverTransaction.setType("TRANSFER_RECEIVED");
            receiverTransaction.setDescription("Transfer from " + currentUser.getEmail());
            receiverTransaction.setStatus("COMPLETED");
            receiverTransaction.setCreatedAt(new java.util.Date());

            transactionDAO.createTransaction(senderTransaction);
            transactionDAO.createTransaction(receiverTransaction);

            loadTransactions();

            JOptionPane.showMessageDialog(this,
                    "Transfer successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showErrorDialog("Error: " + ex.getMessage());
        }
    }

    private void showPayBillsDialog() {
        // Check if user has zero balance
        if (currentUser.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            showErrorDialog("Your balance is zero. Please add money to your wallet first.");
            return;
        }
        
        PayBillsDialog dialog = new PayBillsDialog(this, currentUser);
        dialog.setVisible(true);
        
        // Refresh the balance and transactions after bill payment
        try {
            User updatedUser = userDAO.getUserById(currentUser.getUserId());
            if (updatedUser != null) {
                currentUser = updatedUser;
                balanceLabel.setText("₹" + currentUser.getBalance().toString());
                loadTransactions();
            }
        } catch (SQLException e) {
            showErrorDialog("Error refreshing data: " + e.getMessage());
        }
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof EmptyBorder) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        field.setFont(regularFont);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
            )
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced button shadow
                int shadowSize = 3;
                for (int i = 0; i < shadowSize; i++) {
                    float opacity = 0.15f - (i * 0.05f);
                    g2d.setColor(new Color(0, 0, 0, Math.max((int)(opacity * 255), 0)));
                    int offset = i;
                    g2d.fillRoundRect(
                        offset, 
                        offset, 
                        getWidth() - (offset * 2), 
                        getHeight() - (offset * 2), 
                        10, 
                        10
                    );
                }
                
                if (getModel().isPressed()) {
                    g2d.setColor(backgroundColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(backgroundColor.brighter());
                } else {
                    g2d.setColor(backgroundColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        
        button.setFont(mediumFont);
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add padding to the button
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        return button;
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
} 