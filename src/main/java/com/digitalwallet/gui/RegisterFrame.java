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
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.User;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private UserDAO userDAO;

    // Modern UI Colors and Fonts
    private Color primaryColor = new Color(79, 70, 229); // Indigo-600
    private Color successColor = new Color(22, 163, 74); // Green-600
    private Color backgroundColor = new Color(249, 250, 251); // Gray-50
    private Color textColor = new Color(17, 24, 39); // Gray-900
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font mediumFont = new Font("Segoe UI", Font.BOLD, 14);

    public RegisterFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Digital Wallet - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(backgroundColor);

        // Main panel with shadow effect
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Card background
                g2d.setColor(Color.WHITE);
                int padding = 20;
                int width = getWidth() - (padding * 2);
                int height = getHeight() - (padding * 2);
                int arcSize = 15;
                
                // Draw shadow
                for (int i = 0; i < 5; i++) {
                    g2d.setColor(new Color(0, 0, 0, 5));
                    g2d.fill(new RoundRectangle2D.Float(padding - i, padding - i, 
                            width + (i * 2), height + (i * 2), arcSize, arcSize));
                }
                
                // Draw card
                g2d.setColor(Color.WHITE);
                g2d.fill(new RoundRectangle2D.Float(padding, padding, width, height, arcSize, arcSize));
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setOpaque(false);

        // Title
        JLabel titleLabel = createStyledLabel("Create Account", titleFont, textColor);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Username field
        JLabel usernameLabel = createStyledLabel("Username", mediumFont, textColor);
        usernameField = createStyledTextField();
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Name field
        JLabel nameLabel = createStyledLabel("Full Name", mediumFont, textColor);
        nameField = createStyledTextField();
        mainPanel.add(nameLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Email field
        JLabel emailLabel = createStyledLabel("Email", mediumFont, textColor);
        emailField = createStyledTextField();
        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password field
        JLabel passwordLabel = createStyledLabel("Password", mediumFont, textColor);
        passwordField = createStyledPasswordField();
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Confirm Password field
        JLabel confirmPasswordLabel = createStyledLabel("Confirm Password", mediumFont, textColor);
        confirmPasswordField = createStyledPasswordField();
        mainPanel.add(confirmPasswordLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(30));

        // Register button
        JButton registerButton = createStyledButton("Create Account", primaryColor, Color.WHITE);
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(15));

        // Back to login button
        JButton backButton = createStyledButton("Back to Login", new Color(243, 244, 246), textColor);
        mainPanel.add(backButton);

        // Add action listeners
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> dispose());

        // Set content pane with custom background
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20) {
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
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(backgroundColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(backgroundColor.brighter());
                } else {
                    g2d.setColor(backgroundColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        button.setFont(mediumFont);
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            showErrorDialog("Password must be at least 6 characters long");
            return;
        }

        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                showErrorDialog("Username already taken");
                return;
            }

            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                showErrorDialog("Email already registered");
                return;
            }

            // Create new user
            User newUser = new User(username, name, email, password, "USER");
            userDAO.createUser(newUser);

            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (SQLException ex) {
            showErrorDialog("Database error: " + ex.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
} 