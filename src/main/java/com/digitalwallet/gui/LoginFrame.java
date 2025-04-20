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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.digitalwallet.dao.UserDAO;
import com.digitalwallet.model.User;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    private boolean isAdminLogin = false;
    private JButton switchButton;
    private Color primaryColor = new Color(79, 70, 229); // Indigo-600
    private Color successColor = new Color(22, 163, 74); // Green-600
    private Color backgroundColor = new Color(249, 250, 251); // Gray-50
    private Color textColor = new Color(17, 24, 39); // Gray-900
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font mediumFont = new Font("Segoe UI", Font.BOLD, 14);

    public LoginFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Digital Wallet");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
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
        JLabel titleLabel = createStyledLabel("User Login", titleFont, textColor);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Username field
        JLabel usernameLabel = createStyledLabel("Username", mediumFont, textColor);
        usernameField = createStyledTextField();
        mainPanel.add(usernameLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password field
        JLabel passwordLabel = createStyledLabel("Password", mediumFont, textColor);
        passwordField = createStyledPasswordField();
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));

        // Login button
        JButton loginButton = createStyledButton("Login", primaryColor, Color.WHITE);
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(15));

        // Create Account button
        JButton createAccountButton = createStyledButton("Create Account", successColor, Color.WHITE);
        mainPanel.add(createAccountButton);
        mainPanel.add(Box.createVerticalStrut(15));

        // Switch to Admin Login button
        switchButton = createStyledButton("Switch to Admin Login", new Color(243, 244, 246), textColor);
        mainPanel.add(switchButton);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        createAccountButton.addActionListener(e -> showRegisterFrame());
        switchButton.addActionListener(e -> switchLoginMode());

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

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Please enter both username and password");
            return;
        }

        try {
            if (userDAO.verifyPassword(username, password)) {
                User user = userDAO.getUserByUsername(username);
                if (user != null) {
                    // Check if user is blocked
                    if (user.isBlocked()) {
                        showErrorDialog("Your account has been blocked. Please contact the administrator.");
                        return;
                    }

                    if (isAdminLogin && "ADMIN".equals(user.getRole())) {
                        new AdminDashboard(user).setVisible(true);
                        dispose();
                    } else if (!isAdminLogin && "USER".equals(user.getRole())) {
                        new UserDashboard(user).setVisible(true);
                        dispose();
                    } else {
                        showErrorDialog("Invalid login type. Please use the correct login mode.");
                    }
                }
            } else {
                showErrorDialog("Invalid username or password");
            }
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

    private void showRegisterFrame() {
        new RegisterFrame().setVisible(true);
    }

    private void switchLoginMode() {
        isAdminLogin = !isAdminLogin;
        JLabel titleLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(0);

        if (isAdminLogin) {
            titleLabel.setText("Admin Login");
            switchButton.setText("Switch to User Login");
        } else {
            titleLabel.setText("User Login");
            switchButton.setText("Switch to Admin Login");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
} 