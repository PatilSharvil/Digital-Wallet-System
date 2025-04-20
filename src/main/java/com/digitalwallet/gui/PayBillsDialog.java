package com.digitalwallet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.digitalwallet.dao.ServiceDAO;
import com.digitalwallet.dao.TransactionDAO;
import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.Service;
import com.digitalwallet.model.Transaction;
import com.digitalwallet.model.User;

public class PayBillsDialog extends JDialog {
    private User currentUser;
    private UserDAO userDAO;
    private ServiceDAO serviceDAO;
    private TransactionDAO transactionDAO;
    
    private JComboBox<String> serviceTypeCombo;
    private JComboBox<String> providerCombo;
    private JTextField customerIdField;
    private JTextField amountField;
    private JLabel amountLabel;
    
    // Modern UI Colors and Fonts
    private Color primaryColor = new Color(79, 70, 229); // Indigo-600
    private Color successColor = new Color(22, 163, 74); // Green-600
    private Color backgroundColor = new Color(249, 250, 251); // Gray-50
    private Color cardBackground = Color.WHITE;
    private Color textColor = new Color(17, 24, 39); // Gray-900
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font mediumFont = new Font("Segoe UI", Font.BOLD, 14);

    public PayBillsDialog(JFrame parent, User user) {
        super(parent, "Pay Bills", true);
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.serviceDAO = new ServiceDAO();
        this.transactionDAO = new TransactionDAO();
        
        initializeUI();
        loadServices();
    }

    private void initializeUI() {
        setTitle("Pay Bills");
        setModal(true);
        setSize(400, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(backgroundColor);

        // Main panel with padding for curved edges visibility
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(backgroundColor);

        // Content panel with white background, rounded corners and enhanced shadow
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create subtle shadow effect
                int shadowSize = 5;
                for (int i = 0; i < shadowSize; i++) {
                    g2d.setColor(new Color(0, 0, 0, 5));
                    g2d.fill(new RoundRectangle2D.Float(
                        shadowSize - i, 
                        shadowSize - i,
                        getWidth() - ((shadowSize - i) * 2), 
                        getHeight() - ((shadowSize - i) * 2), 
                        15, 
                        15
                    ));
                }

                // Draw the main background
                g2d.setColor(cardBackground);
                g2d.fillRoundRect(
                    shadowSize, 
                    shadowSize, 
                    getWidth() - (shadowSize * 2), 
                    getHeight() - (shadowSize * 2), 
                    15, 
                    15
                );
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                // Add extra space for shadow
                return new Dimension(size.width + 10, size.height + 10);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(cardBackground);
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Pay Bills");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(0.0f);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Service Type
        JLabel serviceTypeLabel = new JLabel("Service Type");
        serviceTypeLabel.setFont(mediumFont);
        serviceTypeLabel.setForeground(textColor);
        serviceTypeLabel.setAlignmentX(0.0f);
        contentPanel.add(serviceTypeLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        serviceTypeCombo = new JComboBox<>();
        serviceTypeCombo.setFont(regularFont);
        serviceTypeCombo.setMaximumSize(new Dimension(340, 40));
        serviceTypeCombo.setAlignmentX(0.0f);
        serviceTypeCombo.addActionListener(e -> updateProviders());
        contentPanel.add(serviceTypeCombo);
        contentPanel.add(Box.createVerticalStrut(20));

        // Provider
        JLabel providerLabel = new JLabel("Service Provider");
        providerLabel.setFont(mediumFont);
        providerLabel.setForeground(textColor);
        providerLabel.setAlignmentX(0.0f);
        contentPanel.add(providerLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        providerCombo = new JComboBox<>();
        providerCombo.setFont(regularFont);
        providerCombo.setMaximumSize(new Dimension(340, 40));
        providerCombo.setAlignmentX(0.0f);
        contentPanel.add(providerCombo);
        contentPanel.add(Box.createVerticalStrut(20));

        // Customer ID
        JLabel customerIdLabel = new JLabel("Customer ID / Bill Number");
        customerIdLabel.setFont(mediumFont);
        customerIdLabel.setForeground(textColor);
        customerIdLabel.setAlignmentX(0.0f);
        contentPanel.add(customerIdLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        customerIdField = createStyledTextField();
        customerIdField.setMaximumSize(new Dimension(340, 40));
        customerIdField.setAlignmentX(0.0f);
        contentPanel.add(customerIdField);
        contentPanel.add(Box.createVerticalStrut(20));

        // Amount
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(mediumFont);
        amountLabel.setForeground(textColor);
        amountLabel.setAlignmentX(0.0f);
        contentPanel.add(amountLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        amountField = createStyledTextField();
        amountField.setMaximumSize(new Dimension(340, 40));
        amountField.setAlignmentX(0.0f);
        contentPanel.add(amountField);
        contentPanel.add(Box.createVerticalStrut(30));

        // Pay Button
        JButton payButton = createStyledButton("Pay Now", successColor, Color.WHITE);
        payButton.setMaximumSize(new Dimension(340, 45));
        payButton.setAlignmentX(0.0f);
        payButton.addActionListener(e -> handlePayment());
        contentPanel.add(payButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        return button;
    }

    private void loadServices() {
        try {
            List<String> serviceTypes = serviceDAO.getServiceTypes();
            for (String serviceType : serviceTypes) {
                serviceTypeCombo.addItem(serviceType);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading services: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
        }
    }

    private void updateProviders() {
        String selectedService = (String) serviceTypeCombo.getSelectedItem();
        if (selectedService != null) {
            try {
                List<String> providers = serviceDAO.getServiceProviders(selectedService);
                providerCombo.removeAllItems();
                for (String provider : providers) {
                    providerCombo.addItem(provider);
                }
            } catch (SQLException e) {
                showErrorDialog("Error loading providers: " + e.getMessage());
                e.printStackTrace(); // Add this for debugging
            }
        }
    }

    private void handlePayment() {
        String customerId = customerIdField.getText().trim();
        String amountStr = amountField.getText().trim();
        String serviceType = (String) serviceTypeCombo.getSelectedItem();
        String provider = (String) providerCombo.getSelectedItem();

        // Validation
        if (customerId.isEmpty() || amountStr.isEmpty() || serviceType == null || provider == null) {
            showErrorDialog("Please fill in all fields");
            return;
        }

        try {
            // Create password verification dialog
            JDialog passwordDialog = new JDialog(this, "Security Verification", true);
            passwordDialog.setSize(600, 500);  // Increased height further
            passwordDialog.setLocationRelativeTo(this);
            passwordDialog.setBackground(backgroundColor);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            mainPanel.setBackground(backgroundColor);
            
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Enhanced shadow effect to match login page
                    int shadowSize = 15;  // Increased shadow size
                    for (int i = 0; i < shadowSize; i++) {
                        float opacity = 0.25f - (i * 0.015f);  // Increased initial opacity
                        g2d.setColor(new Color(0, 0, 0, Math.max((int)(opacity * 255), 0)));
                        int offset = i * 2;
                        g2d.fillRoundRect(
                            offset,
                            offset,
                            getWidth() - (offset * 2),
                            getHeight() - (offset * 2),
                            15,
                            15
                        );
                    }
                    
                    g2d.setColor(cardBackground);
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                }
            };
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(45, 45, 45, 45));
            panel.setOpaque(false);

            JLabel titleLabel = new JLabel("Confirm Bill Payment");
            titleLabel.setFont(titleFont);  // Using larger title font
            titleLabel.setAlignmentX(0.0f);
            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(45));

            JLabel passwordLabel = new JLabel("Enter your password");
            passwordLabel.setFont(mediumFont);
            passwordLabel.setAlignmentX(0.0f);
            panel.add(passwordLabel);
            panel.add(Box.createVerticalStrut(20));

            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(regularFont);
            passwordField.setMaximumSize(new Dimension(540, 50));
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 0, 0, 0),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true),
                    BorderFactory.createEmptyBorder(12, 20, 12, 20)
                )
            ));
            passwordField.setBackground(Color.WHITE);
            passwordField.setAlignmentX(0.0f);
            panel.add(passwordField);
            panel.add(Box.createVerticalStrut(45));

            JButton confirmButton = createStyledButton("Confirm", successColor, Color.WHITE);
            confirmButton.setMaximumSize(new Dimension(540, 55));
            confirmButton.setAlignmentX(0.0f);
            panel.add(confirmButton);
            panel.add(Box.createVerticalStrut(20));

            JButton cancelButton = createStyledButton("Cancel", new Color(243, 244, 246), textColor);
            cancelButton.setMaximumSize(new Dimension(540, 55));
            cancelButton.setAlignmentX(0.0f);
            panel.add(cancelButton);
            panel.add(Box.createVerticalStrut(20));  // Added extra spacing at bottom

            final boolean[] passwordVerified = {false};

            confirmButton.addActionListener(e -> {
                try {
                    String password = new String(passwordField.getPassword());
                    if (userDAO.verifyPassword(currentUser.getUsername(), password)) {
                        passwordVerified[0] = true;
                        passwordDialog.dispose();
                    } else {
                        showErrorDialog("Incorrect password. Please try again.");
                        passwordField.setText("");
                    }
                } catch (SQLException ex) {
                    showErrorDialog("Error verifying password: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> passwordDialog.dispose());
            passwordField.addActionListener(e -> confirmButton.doClick());

            mainPanel.add(panel, BorderLayout.CENTER);
            passwordDialog.add(mainPanel);
            passwordDialog.setVisible(true);

            if (!passwordVerified[0]) {
                return;
            }

            // Convert amount to BigDecimal for precise calculations
            BigDecimal amount = new BigDecimal(amountStr);
            
            // Get the service details
            Service service = serviceDAO.getServiceByNameAndProvider(serviceType, provider);
            if (service == null) {
                showErrorDialog("Selected service is not available");
                return;
            }

            // Check if amount is valid
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showErrorDialog("Please enter a valid amount");
                return;
            }

            // Check if user has sufficient balance
            if (currentUser.getBalance().compareTo(amount) < 0) {
                showErrorDialog("Insufficient balance. Your current balance is ₹" + currentUser.getBalance());
                return;
            }

            // Update user's balance - subtract the payment amount
            userDAO.updateBalance(currentUser.getUserId(), amount.negate());

            // Refresh current user data to get updated balance
            User updatedUser = userDAO.getUserById(currentUser.getUserId());
            if (updatedUser != null) {
                currentUser = updatedUser;
            }

            // Create transaction record
            Transaction transaction = new Transaction();
            transaction.setUserId(currentUser.getUserId());
            transaction.setAmount(amount.negate());
            transaction.setType("BILL_PAYMENT");
            transaction.setDescription("Bill payment for " + service.getServiceName() + " - " + 
                                    service.getServiceProvider() + " (Customer ID: " + customerId + ")");
            transaction.setStatus("COMPLETED");
            transaction.setCreatedAt(new java.util.Date());

            transactionDAO.createTransaction(transaction);

            JOptionPane.showMessageDialog(this,
                    "Bill payment successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid amount");
        } catch (SQLException ex) {
            showErrorDialog("Error processing payment: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
} 