import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Arrays;
import java.util.Base64;

public class User {

    private static final String DB_URL = "jdbc:sqlite:users.db";

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 600_000;

    private static final SecureRandom RANDOM = new SecureRandom();

    public User() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                "password_hash TEXT NOT NULL)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             var statement = connection.createStatement()) {

            statement.execute(sql);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not initialize the user database.", e);
        }
    }

    public boolean register(String username, char[] password) {
        if (username == null || password == null) {
            return false;
        }

        username = username.trim();

        if (username.length() < 3 || username.length() > 30) {
            return false;
        }

        if (!username.matches("[a-zA-Z0-9_]+")) {
            return false;
        }

        if (password.length < 8 || password.length > 1024) {
            return false;
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hash = null;

        try {
            hash = hashPassword(password, salt);

            String storedValue =
                    "pbkdf2-sha256$" + ITERATIONS + "$" +
                    Base64.getEncoder().encodeToString(salt) + "$" +
                    Base64.getEncoder().encodeToString(hash);

            String sql =
                    "INSERT INTO users (username, password_hash) VALUES (?, ?)";

            try (Connection connection = DriverManager.getConnection(DB_URL);
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, username);
                statement.setString(2, storedValue);

                return statement.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                return false;
            }

            throw new IllegalStateException("User registration failed.", e);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing failed.", e);

        } finally {
            Arrays.fill(password, '\0');

            if (hash != null) {
                Arrays.fill(hash, (byte) 0);
            }

            Arrays.fill(salt, (byte) 0);
        }
    }

    public boolean authenticate(String username, char[] password) {
        if (username == null || password == null) {
            return false;
        }

        String sql =
                "SELECT password_hash FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username.trim());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return false;
                }

                String storedValue = result.getString("password_hash");
                String[] parts = storedValue.split("\\$", -1);

                if (parts.length != 4 ||
                        !parts[0].equals("pbkdf2-sha256")) {
                    return false;
                }

                int iterations = Integer.parseInt(parts[1]);

                if (iterations < 100_000 || iterations > 2_000_000) {
                    return false;
                }

                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
                byte[] actualHash = null;

                try {
                    if (salt.length != SALT_LENGTH ||
                            expectedHash.length != HASH_LENGTH / 8) {
                        return false;
                    }

                    actualHash = hashPassword(password, salt);

                    return java.security.MessageDigest.isEqual(
                            expectedHash, actualHash);

                } finally {
                    Arrays.fill(salt, (byte) 0);
                    Arrays.fill(expectedHash, (byte) 0);

                    if (actualHash != null) {
                        Arrays.fill(actualHash, (byte) 0);
                    }
                }
            }

        } catch (SQLException | GeneralSecurityException |
                 IllegalArgumentException e) {
            return false;

        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private byte[] hashPassword(char[] password, byte[] salt)
            throws GeneralSecurityException {

        KeySpec spec = new PBEKeySpec(
                password, salt, ITERATIONS, HASH_LENGTH);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } finally {
            ((PBEKeySpec) spec).clearPassword();
        }
    }
}