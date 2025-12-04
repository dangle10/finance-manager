
package pack_Project.DAO;

import pack_Project.DTO.User;
import pack_Project.GUI.CustomMessageBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    private static final String USER_FILE_NAME = "users.txt";

    public User registerUser(String username, String password, String email) {

        if (username == null || username.trim().isEmpty()) {
            CustomMessageBox.showErrorMessage(null, "Username cannot be empty!", "Registration Error");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            CustomMessageBox.showErrorMessage(null, "Password cannot be empty!", "Registration Error");
            return null;
        }

        if (password.length() < 3) {
            CustomMessageBox.showErrorMessage(null, "Password must be at least 3 characters long!", "Registration Error");
            return null;
        }

        if (isUsernameExists(username)) {
            CustomMessageBox.showErrorMessage(null, "Username already exists! Please choose another one.", "Registration Error");
            return null;
        }

        File file = FileStorageUtil.getDataFile(USER_FILE_NAME);
        try {
            int newId = FileStorageUtil.generateNextId(file);
            String safeEmail = (email != null) ? email.trim() : "";
            String line = newId + ";" + username.trim() + ";" + password + ";" + safeEmail;
            FileStorageUtil.appendLine(file, line);
            return new User(newId, username.trim(), password, safeEmail.isEmpty() ? null : safeEmail);
        } catch (IOException e) {
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Error saving user to file.", "File Error");
            return null;
        }
    }

    public boolean isUsernameExists(String username) {
        String target = username.trim();
        File file = FileStorageUtil.getDataFile(USER_FILE_NAME);
        try {
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 2 && parts[1].equals(target)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User authenticateUser(String username, String password) {
        File file = FileStorageUtil.getDataFile(USER_FILE_NAME);
        try {
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 4) {
                    String storedUsername = parts[1];
                    String storedPassword = parts[2];
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        int userId = Integer.parseInt(parts[0]);
                        String email = parts[3].isEmpty() ? null : parts[3];
                        return new User(userId, storedUsername, storedPassword, email);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Error reading user file.", "Login Error");
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        File file = FileStorageUtil.getDataFile(USER_FILE_NAME);
        try {
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 4) {
                    int userId = Integer.parseInt(parts[0]);
                    String username = parts[1];
                    String password = parts[2];
                    String email = parts[3].isEmpty() ? null : parts[3];
                    users.add(new User(userId, username, password, email));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return users;
    }
}

