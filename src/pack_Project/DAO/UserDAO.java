
package pack_Project.DAO;

import pack_Project.GUI.CustomMessageBox;
import pack_Project.DTO.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserDAO {

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
        
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User newUser = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return null;
            }
            
            pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            pstmt.setString(3, email != null && !email.trim().isEmpty() ? email.trim() : null);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {

                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    newUser = new User(userId, username.trim(), password, email != null ? email.trim() : null);
                    System.out.println("User registered successfully with ID: " + userId);
                }
            }
        } catch (SQLException se) {
            System.err.println("Error registering user:");
            System.err.println("Error Code: " + se.getErrorCode());
            System.err.println("SQL State: " + se.getSQLState());
            System.err.println("Message: " + se.getMessage());
            se.printStackTrace();
            
            String errorMsg = "Database error during registration.\n\n";
            
           
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return newUser;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return false;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username.trim());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException se) {
            System.err.println("Error checking username: " + se.getMessage());
            se.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT user_id, username, password, email FROM users WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException se) {
            System.err.println("Error authenticating user: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Database error during login", "Login Error");
        }
        finally
        {

            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null) pstmt.close();
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return user;
    }
}
