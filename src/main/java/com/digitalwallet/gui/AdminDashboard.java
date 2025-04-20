package com.digitalwallet.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.digitalwallet.dao.TransactionDAO;
import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.FraudLog;
import com.digitalwallet.model.Transaction;
import com.digitalwallet.model.User;
import com.digitalwallet.service.FraudDetectionService;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private FraudDetectionService fraudDetectionService;
    private JTable userTable;
    private JTable transactionTable;
    private JTable fraudLogTable;
    private DefaultTableModel userTableModel;
    private DefaultTableModel transactionTableModel;
    private DefaultTableModel fraudLogTableModel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton usersTabButton;
    private JButton transactionsTabButton;
    private JButton fraudTabButton;
    private Color activeTabColor = new Color(79, 70, 229);  // Active tab background
    private Color inactiveTabColor = new Color(243, 244, 246);  // Inactive tab background

    // Modern UI Colors
    private Color primaryColor = new Color(79, 70, 229);    // Indigo-600
    private Color successColor = new Color(22, 163, 74);    // Green-600
    private Color warningColor = new Color(217, 119, 6);    // Amber-600
    private Color dangerColor = new Color(220, 38, 38);     // Red-600
    private Color infoColor = new Color(6, 182, 212);       // Cyan-600
    private Color purpleColor = new Color(147, 51, 234);    // Purple-600
    private Color backgroundColor = new Color(230, 224, 212);  // White coffee color (#E6E0D4)
    private Color textColor = new Color(17, 24, 39);
    private Color cardBackground = Color.WHITE;
    private Color secondaryTextColor = new Color(107, 114, 128); // Gray-500
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font headingFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font mediumFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
    private Color tabBackgroundColor = new Color(243, 244, 246);  // Light gray background for all tabs

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        this.fraudDetectionService = new FraudDetectionService();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("Digital Wallet - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);
        
        // Main panel
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with admin info
        JPanel topPanel = createStyledPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // User info section
        JPanel userInfoPanel = new JPanel(new BorderLayout(5, 5));
        userInfoPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back,");
        welcomeLabel.setFont(regularFont);
        welcomeLabel.setForeground(secondaryTextColor);
        
        JLabel nameLabel = new JLabel("Admin " + currentUser.getName());
        nameLabel.setFont(headingFont);
        nameLabel.setForeground(textColor);
        
        userInfoPanel.add(welcomeLabel, BorderLayout.NORTH);
        userInfoPanel.add(nameLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = createStyledButton("Logout", dangerColor, Color.WHITE);
        logoutButton.addActionListener(e -> handleLogout());

        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Navigation panel with more spacing
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));  // Increased spacing between buttons
        navigationPanel.setBackground(backgroundColor);
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create tab buttons - start with Users tab not active
        usersTabButton = createTabButton("Users", false);
        transactionsTabButton = createTabButton("Transactions", false);
        fraudTabButton = createTabButton("Fraud Detection", false);

        navigationPanel.add(usersTabButton);
        navigationPanel.add(transactionsTabButton);
        navigationPanel.add(fraudTabButton);

        // Content panel with card layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Create content panels
        JPanel usersPanel = createUsersPanel();
        JPanel transactionsPanel = createTransactionsPanel();
        JPanel fraudPanel = createFraudPanel();

        // Add panels to card layout with consistent names
        contentPanel.add(usersPanel, "USERS");
        contentPanel.add(transactionsPanel, "TRANSACTIONS");
        contentPanel.add(fraudPanel, "FRAUD");

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(navigationPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Show initial panel and set Users tab as active
        cardLayout.show(contentPanel, "USERS");
        switchTab("USERS");
    }

    private JButton createTabButton(String text, boolean isActive) {
        // Define colors for each tab
        Color tabColor = switch(text) {
            case "Users" -> new Color(79, 70, 229);      // Indigo for Users
            case "Transactions" -> new Color(16, 185, 129);  // Emerald for Transactions
            case "Fraud Detection" -> new Color(245, 158, 11);  // Amber for Fraud Detection
            default -> new Color(79, 70, 229);
        };

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                for (int i = 0; i < 4; i++) {
                    float shadowOpacity = 0.1f - (i * 0.02f);
                    g2d.setColor(new Color(0, 0, 0, (int)(shadowOpacity * 255)));
                    g2d.fill(new RoundRectangle2D.Float(
                        i,
                        i,
                        getWidth() - (i * 2),
                        getHeight() - (i * 2),
                        15,
                        15
                    ));
                }

                // Draw button background
                if (getModel().isPressed() || isActive) {
                    g2d.setColor(tabColor);
                } else if (getModel().isRollover()) {
                    // Lighter version of the tab color
                    g2d.setColor(new Color(
                        Math.min((int)(tabColor.getRed() * 1.2), 255),
                        Math.min((int)(tabColor.getGreen() * 1.2), 255),
                        Math.min((int)(tabColor.getBlue() * 1.2), 255)
                    ));
                } else {
                    // Even lighter version for inactive state
                    g2d.setColor(new Color(
                        Math.min((int)(tabColor.getRed() * 1.4), 255),
                        Math.min((int)(tabColor.getGreen() * 1.4), 255),
                        Math.min((int)(tabColor.getBlue() * 1.4), 255)
                    ));
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));

                // Draw text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Text color - white for active/pressed, dark for inactive
                if (getModel().isPressed() || isActive) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(new Color(55, 65, 81));  // Gray-700
                }
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border painting needed
            }
        };

        button.setPreferredSize(new Dimension(150, 45));  // Slightly taller
        button.setFont(regularFont);  // Always start with regular font
        button.setForeground(isActive ? Color.WHITE : textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Add hover effect
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(boldFont);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    button.setFont(regularFont);
                    button.repaint();
                }
            }
        });

        // Add action listener
        button.addActionListener(e -> {
            String tabName = switch(text) {
                case "Users" -> "USERS";
                case "Transactions" -> "TRANSACTIONS";
                case "Fraud Detection" -> "FRAUD";
                default -> "USERS";
            };
            switchTab(tabName);
        });

        return button;
    }

    private void switchTab(String tabName) {
        // Update button fonts and styles
        usersTabButton.setFont(tabName.equals("USERS") ? boldFont : regularFont);
        transactionsTabButton.setFont(tabName.equals("TRANSACTIONS") ? boldFont : regularFont);
        fraudTabButton.setFont(tabName.equals("FRAUD") ? boldFont : regularFont);

        // Show selected panel
        cardLayout.show(contentPanel, tabName);

        // Repaint buttons
        usersTabButton.repaint();
        transactionsTabButton.repaint();
        fraudTabButton.repaint();
    }

    private JPanel createUsersPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // Initialize user table
        initializeUserTable();
        JScrollPane scrollPane = createStyledScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // User actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setOpaque(false);

        JButton refreshButton = createStyledButton("Refresh", infoColor, Color.WHITE);
        JButton blockButton = createStyledButton("Block/Unblock", warningColor, Color.WHITE);
        JButton deleteButton = createStyledButton("Delete User", dangerColor, Color.WHITE);

        refreshButton.addActionListener(e -> loadUsers());
        blockButton.addActionListener(e -> toggleBlockUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        actionsPanel.add(refreshButton);
        actionsPanel.add(blockButton);
        actionsPanel.add(deleteButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));

        initializeTransactionTable();
        JScrollPane scrollPane = createStyledScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setOpaque(false);

        JButton refreshButton = createStyledButton("Refresh", infoColor, Color.WHITE);
        JButton fraudButton = createStyledButton("View Fraud Logs", purpleColor, Color.WHITE);

        refreshButton.addActionListener(e -> loadTransactions());
        fraudButton.addActionListener(e -> switchTab("FRAUD"));

        actionsPanel.add(refreshButton);
        actionsPanel.add(fraudButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createFraudPanel() {
        JPanel panel = createStyledPanel();
        panel.setLayout(new BorderLayout(10, 10));

        initializeFraudLogTable();
        JScrollPane scrollPane = createStyledScrollPane(fraudLogTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionsPanel.setOpaque(false);

        JButton analyzeButton = createStyledButton("Analyze Transactions", successColor, Color.WHITE);
        JButton refreshButton = createStyledButton("Refresh", infoColor, Color.WHITE);

        analyzeButton.addActionListener(e -> analyzeTransactions());
        refreshButton.addActionListener(e -> loadFraudLogs());

        actionsPanel.add(analyzeButton);
        actionsPanel.add(refreshButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void initializeTransactionTable() {
        String[] columns = {"Date", "User Name", "Type", "Amount", "Description", "Status"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(regularFont);
        transactionTable.setRowHeight(40);
        transactionTable.setShowGrid(true);
        transactionTable.setGridColor(new Color(229, 231, 235));
        transactionTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Style the table header
        transactionTable.getTableHeader().setFont(mediumFont);
        transactionTable.getTableHeader().setBackground(new Color(243, 244, 246));
        transactionTable.getTableHeader().setForeground(textColor);
        transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void initializeUserTable() {
        String[] userColumns = {"ID", "Name", "Email", "Balance", "Role", "Status", "Created At"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = createStyledTable(userTableModel);
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Enhanced shadow effect (matching login page)
                for (int i = 0; i < 8; i++) {
                    float shadowOpacity = 0.2f - (i * 0.02f);
                    g2d.setColor(new Color(0, 0, 0, (int)(shadowOpacity * 255)));
                    g2d.fill(new RoundRectangle2D.Float(
                        i,
                        i,
                        getWidth() - (i * 2),
                        getHeight() - (i * 2),
                        20,
                        20
                    ));
                }

                // Paint panel with rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Float(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    20,
                    20
                ));

                g2d.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                for (int i = 0; i < 4; i++) {
                    float shadowOpacity = 0.1f - (i * 0.02f);
                    g2d.setColor(new Color(0, 0, 0, (int)(shadowOpacity * 255)));
                    g2d.fill(new RoundRectangle2D.Float(
                        i,
                        i,
                        getWidth() - (i * 2),
                        getHeight() - (i * 2),
                        12,
                        12
                    ));
                }

                // Draw button background
                if (getModel().isPressed()) {
                    g2d.setColor(darker(bgColor));
                } else if (getModel().isRollover()) {
                    g2d.setColor(brighter(bgColor));
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));

                // Draw text with proper font
                g2d.setColor(fgColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };
        
        button.setFont(mediumFont);
        button.setForeground(fgColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }

    private Color darker(Color c) {
        return new Color(
            Math.max((int)(c.getRed() * 0.8), 0),
            Math.max((int)(c.getGreen() * 0.8), 0),
            Math.max((int)(c.getBlue() * 0.8), 0),
            c.getAlpha()
        );
    }

    private Color brighter(Color c) {
        return new Color(
            Math.min((int)(c.getRed() * 1.2), 255),
            Math.min((int)(c.getGreen() * 1.2), 255),
            Math.min((int)(c.getBlue() * 1.2), 255),
            c.getAlpha()
        );
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(regularFont);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(243, 244, 246));  // Lighter grid color
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(243, 244, 246));
        table.setSelectionForeground(textColor);
        table.setBackground(Color.WHITE);
        table.setForeground(textColor);
        
        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(mediumFont);
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(textColor);
        header.setPreferredSize(new Dimension(0, 45));
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        return table;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint background
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Paint panel with rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Float(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    20,
                    20
                ));

                g2d.dispose();
            }
        };
        
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Customize scrollbars
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
        scrollPane.getHorizontalScrollBar().setBackground(Color.WHITE);
        
        return scrollPane;
    }

    private void loadData() {
        loadUsers();
        loadTransactions();
        loadFraudLogs();
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userTableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            
            for (User user : users) {
                userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    "₹" + user.getBalance(),
                    user.getRole(),
                    user.isBlocked() ? "Blocked" : "Active",
                    user.getCreatedAt().format(formatter)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getAllTransactions();
            transactionTableModel.setRowCount(0);
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            for (Transaction transaction : transactions) {
                transactionTableModel.addRow(new Object[]{
                    dateFormat.format(transaction.getCreatedAt()),
                    transaction.getUserName(),
                    transaction.getType(),
                    "₹" + transaction.getAmount().toString(),
                    transaction.getDescription(),
                    transaction.getStatus()
                });
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading transactions: " + e.getMessage());
        }
    }

    private void toggleBlockUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to block/unblock",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) userTableModel.getValueAt(selectedRow, 5);
        boolean isCurrentlyBlocked = "Blocked".equals(currentStatus);

        int choice = JOptionPane.showConfirmDialog(this,
                isCurrentlyBlocked ? 
                "Are you sure you want to unblock user " + userName + "?" :
                "Are you sure you want to block user " + userName + "?",
                "Confirm Action",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.blockUser(userId, !isCurrentlyBlocked)) {
                    loadUsers(); // Refresh the table
                    JOptionPane.showMessageDialog(this,
                            "User " + (isCurrentlyBlocked ? "unblocked" : "blocked") + " successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating user status: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String userName = (String) userTableModel.getValueAt(selectedRow, 1);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user " + userName + "?\nThis action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUser(userId)) {
                    loadUsers(); // Refresh the table
                    JOptionPane.showMessageDialog(this,
                            "User deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting user: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showFraudLogs() {
        // Switch to fraud panel and load logs
        switchTab("FRAUD");
        loadFraudLogs();
    }

    private void initializeFraudLogTable() {
        String[] columnNames = {"Log ID", "User ID", "Username", "Issue", "Timestamp"};
        fraudLogTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        fraudLogTable = createStyledTable(fraudLogTableModel);
        
        // Set column widths
        fraudLogTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Log ID
        fraudLogTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // User ID
        fraudLogTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Username
        fraudLogTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Issue
        fraudLogTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Timestamp
    }

    private void loadFraudLogs() {
        try {
            List<FraudLog> fraudLogs = fraudDetectionService.getAllFraudLogs();
            
            // Clear existing data
            fraudLogTableModel.setRowCount(0);
            
            // Add data to table
            if (fraudLogs != null) {
                for (FraudLog log : fraudLogs) {
                    if (log != null) {
                        Object[] rowData = {
                            log.getLogId(),
                            log.getUserId(),
                            log.getUserName(),
                            log.getIssue(),
                            log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        };
                        fraudLogTableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading fraud logs: " + e.getMessage());
        }
    }

    private void analyzeTransactions() {
        try {
            // Show a progress dialog
            JOptionPane.showMessageDialog(this,
                    "Analyzing transactions for fraud patterns...",
                    "Fraud Detection",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Run the analysis
            List<FraudLog> newFraudLogs = fraudDetectionService.analyzeAllTransactions();
            
            // Reload the fraud logs
            loadFraudLogs();
            
            // Show results
        JOptionPane.showMessageDialog(this,
                    "Analysis complete. Found " + newFraudLogs.size() + " potential fraud patterns.",
                    "Fraud Detection",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            showErrorDialog("Error analyzing transactions: " + e.getMessage());
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

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
} 