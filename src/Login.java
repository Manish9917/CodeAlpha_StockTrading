import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("CodeAlpha | Stock Trading Platform - Login");
        setSize(430, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(13, 25, 43));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel titleLabel = new JLabel("Stock Trading Platform", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        titleLabel.setForeground(Color.WHITE);

        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Login to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(170, 190, 220));

        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(280, 35));
        gbc.gridy = 3;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridy = 4;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(280, 35));
        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBackground(new Color(0, 200, 150));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);

        gbc.gridy = 6;
        mainPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> login());

        JButton signupButton = new JButton("Create Account");
        signupButton.setFont(new Font("Arial", Font.BOLD, 14));
        signupButton.setBackground(new Color(45, 65, 90));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);

        gbc.gridy = 7;
        mainPanel.add(signupButton, gbc);

        
signupButton.addActionListener(e -> {
    JTextField newUsernameField = new JTextField();
    JPasswordField newPasswordField = new JPasswordField();
    JPasswordField confirmPasswordField = new JPasswordField();

    Object[] fields = {
        "Choose a username (3-30 characters):",
        newUsernameField,
        "Choose a password (minimum 8 characters):",
        newPasswordField,
        "Confirm password:",
        confirmPasswordField
    };

    int choice = JOptionPane.showConfirmDialog(
        this,
        fields,
        "Create New Account",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    if (choice != JOptionPane.OK_OPTION) {
        return;
    }

    String newUsername = newUsernameField.getText().trim();
    char[] newPassword = newPasswordField.getPassword();
    char[] confirmPassword = confirmPasswordField.getPassword();

    try {
        if (newUsername.length() < 3 ||
                newUsername.length() > 30 ||
                !newUsername.matches("[a-zA-Z0-9_]+")) {

            JOptionPane.showMessageDialog(
                this,
                "Username must be 3-30 characters and contain only letters, numbers, or underscores.",
                "Invalid Username",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (newPassword.length < 8) {
            JOptionPane.showMessageDialog(
                this,
                "Password must contain at least 8 characters.",
                "Invalid Password",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!java.security.MessageDigest.isEqual(
                newPassword.length == 0 ? new byte[0] :
                    new String(newPassword).getBytes(
                        java.nio.charset.StandardCharsets.UTF_8),
                new String(confirmPassword).getBytes(
                    java.nio.charset.StandardCharsets.UTF_8))) {

            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match.",
                "Password Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        User userManager = new User();

        if (userManager.register(newUsername, newPassword)) {
            JOptionPane.showMessageDialog(
                this,
                "Account created successfully! You can now log in.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

            usernameField.setText(newUsername);
            passwordField.setText("");
            passwordField.requestFocusInWindow();

        } else {
            JOptionPane.showMessageDialog(
                this,
                "Account could not be created. This username may already exist.",
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }

    } catch (RuntimeException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Unable to create account. Please try again.",
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );

    } finally {
        java.util.Arrays.fill(newPassword, '\0');
        java.util.Arrays.fill(confirmPassword, '\0');
    }
});


        getRootPane().setDefaultButton(loginButton);
        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            java.util.Arrays.fill(password, '\0');

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User userManager = new User();

            if (userManager.authenticate(username, password)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Welcome, " + username + "!");

                dispose();
                Main.openDashboard(username);

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

                passwordField.setText("");
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to access the user database. Please try again.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();

        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
