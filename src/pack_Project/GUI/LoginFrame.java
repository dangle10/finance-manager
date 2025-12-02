package pack_Project.GUI;

import pack_Project.main;
import pack_Project.DTO.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class LoginFrame extends JFrame {
    protected main app; // Đổi thành protected để lớp con dễ truy cập
    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected JButton loginButton;
    protected JButton signUpButton;

    public LoginFrame(main app) {
        this.app = app;
        setTitle("Personal Finance Manager - Login");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        addListeners();
        setVisible(true);
    }

    protected abstract void onLoginSuccess(User authenticatedUser);

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to PFM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(63, 150, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(40, 100, 150), 2));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(90, 170, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(63, 150, 219));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.PLAIN, 14));
        signUpButton.setBackground(new Color(76, 175, 80));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setOpaque(true);
        signUpButton.setPreferredSize(new Dimension(100, 35));
        signUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(new Color(102, 187, 106));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signUpButton.setBackground(new Color(76, 175, 80));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(signUpButton, gbc);

        this.signUpButton = signUpButton;

        add(panel);
    }

    private void addListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegisterDialog();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(this, app.getUserDAO());
        registerDialog.setVisible(true);

        if (registerDialog.isRegistrationSuccess()) {
            User newUser = registerDialog.getRegisteredUser();
            if (newUser != null) {
                usernameField.setText(newUser.getUsername());
                passwordField.requestFocus();
                CustomMessageBox.showInfoMessage(this,
                        "Registration successful!\n" +
                                "You can now login with your new account.",
                        "Success");
            }
        }
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User authenticatedUser = app.getUserDAO().authenticateUser(username, password);

        if (authenticatedUser != null) {
            CustomMessageBox.showInfoMessage(this, "Login Successful!", "Success");
            dispose();
            // Gọi phương thức trừu tượng để lớp con xử lý logic tiếp theo
            onLoginSuccess(authenticatedUser);
        } else {
            CustomMessageBox.showErrorMessage(this, "Invalid Username or Password.", "Login Failed");
            passwordField.setText("");
        }
    }
}
