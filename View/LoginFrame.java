package View;

import Controller.AppController;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AppController controller;
    private JTextField usernameField;
    private JButton loginButton;

    public LoginFrame(AppController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Login");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Student ID or 'admin':"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            controller.handleLogin(username, this);
        });

        getRootPane().setDefaultButton(loginButton);
        
        setVisible(true);
    }
}