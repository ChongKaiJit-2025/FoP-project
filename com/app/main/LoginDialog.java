package com.app.main;

import java.awt.*;
import javax.swing.*;

public class LoginDialog extends JDialog {
    private boolean succeeded;
    private AuthSystem auth;

    public LoginDialog(JFrame parent, AuthSystem authSystem) {
        super(parent, "Login", true); // true = modal
        this.auth = authSystem;

        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        btnLogin.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (auth.login(username, password)) {
                succeeded = true;
                dispose(); // close login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        btnRegister.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (auth.register(username, password)) {
                JOptionPane.showMessageDialog(this, "Registered! Please login.");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(btnLogin);
        buttons.add(btnRegister);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}