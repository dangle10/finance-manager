package pack_Project.DAO;

import pack_Project.DTO.Goal;
import pack_Project.GUI.CustomMessageBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public boolean addGoal(Goal goal) {

        if (goal == null) {
            CustomMessageBox.showErrorMessage(null, "Goal object is null!", "Validation Error");
            return false;
        }
        
        if (goal.getGoalName() == null || goal.getGoalName().trim().isEmpty()) {
            CustomMessageBox.showErrorMessage(null, "Goal name cannot be empty!", "Validation Error");
            return false;
        }
        
        if (goal.getStartDate() == null || goal.getEndDate() == null) {
            CustomMessageBox.showErrorMessage(null, "Start date and end date cannot be null!", "Validation Error");
            return false;
        }
        
        if (goal.getEndDate().before(goal.getStartDate())) {
            CustomMessageBox.showErrorMessage(null, "End date must be after start date!", "Validation Error");
            return false;
        }
        
        if (goal.getTargetAmount() <= 0) {
            CustomMessageBox.showErrorMessage(null, "Target amount must be greater than 0!", "Validation Error");
            return false;
        }

        double maxAmount = 999999999999999.99;
        if (goal.getTargetAmount() > maxAmount) {
            CustomMessageBox.showErrorMessage(null, 
                "Target amount is too large!\n" +
                "Maximum allowed: 999,999,999,999,999.99\n" +
                "Please enter a smaller amount.", 
                "Validation Error");
            return false;
        }
        
        if (goal.getCurrentAmount() < 0) {
            CustomMessageBox.showErrorMessage(null, "Current amount cannot be negative!", "Validation Error");
            return false;
        }
        
        if (goal.getCurrentAmount() > maxAmount) {
            CustomMessageBox.showErrorMessage(null, 
                "Current amount is too large!\n" +
                "Maximum allowed: 999,999,999,999,999.99", 
                "Validation Error");
            return false;
        }
        
        String sql = "INSERT INTO goals (user_id, goal_name, target_amount, current_amount, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                CustomMessageBox.showErrorMessage(null, "Cannot connect to database!", "Connection Error");
                return false;
            }

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getGoalName().trim());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getCurrentAmount());

            pstmt.setDate(5, new java.sql.Date(goal.getStartDate().getTime()));
            pstmt.setDate(6, new java.sql.Date(goal.getEndDate().getTime()));
            pstmt.setString(7, goal.getStatus() != null ? goal.getStatus() : "Đang thực hiện");

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goal.setGoalId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Goal added successfully with ID: " + goal.getGoalId());
                return true;
            } else {
                CustomMessageBox.showErrorMessage(null, "Failed to add goal. No rows affected.", "Database Error");
                return false;
            }
        } catch (SQLException se) {
            System.err.println("SQL Error adding goal:");
            System.err.println("Error Code: " + se.getErrorCode());
            System.err.println("SQL State: " + se.getSQLState());
            System.err.println("Message: " + se.getMessage());
            se.printStackTrace();
            
            String errorMsg = "Database error adding goal.\n\n";

            if (se.getErrorCode() == 1264) {

                errorMsg += "Value out of range!\n\n";
                if (se.getMessage().contains("target_amount")) {
                    errorMsg += "Target amount is too large.\n";
                    errorMsg += "Maximum allowed: 999,999,999,999,999.99\n";
                    errorMsg += "Please enter a smaller amount.";
                } else if (se.getMessage().contains("current_amount")) {
                    errorMsg += "Current amount is too large.\n";
                    errorMsg += "Maximum allowed: 999,999,999,999,999.99";
                } else {
                    errorMsg += "One of the values is too large for the database.\n";
                    errorMsg += "Please check your input values.";
                }
            } else if (se.getErrorCode() == 1452) {
                errorMsg += "Foreign key constraint failed.\n";
                errorMsg += "User ID " + goal.getUserId() + " does not exist in users table.\n";
                errorMsg += "Please make sure you are logged in correctly.";
            } else if (se.getErrorCode() == 1062) {
                errorMsg += "Duplicate entry.\n";
                errorMsg += "A goal with this name already exists.";
            } else if (se.getErrorCode() == 1146) {
                errorMsg += "Table 'goals' does not exist.\n";
                errorMsg += "Please run database_setup.sql to create the table.";
            } else if (se.getErrorCode() == 1045) {
                errorMsg += "Access denied.\n";
                errorMsg += "Check database username and password.";
            } else {
                errorMsg += "SQL Error: " + se.getMessage() + "\n";
                errorMsg += "Error Code: " + se.getErrorCode();
            }
            
            CustomMessageBox.showErrorMessage(null, errorMsg, "Goal Error");
        } catch (Exception e) {
            System.err.println("Unexpected error adding goal:");
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, 
                "Unexpected error: " + e.getMessage() + "\n\nCheck console for details.", 
                "Error");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public List<Goal> getGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return goals;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(new Goal(
                        rs.getInt("goal_id"),
                        rs.getInt("user_id"),
                        rs.getString("goal_name"),
                        rs.getDouble("target_amount"),
                        rs.getDouble("current_amount"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException se) {
            System.err.println("Error retrieving goals: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Database error retrieving goals.", "Data Retrieval Error");
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return goals;
    }
    public boolean updateGoal(Goal goal) {
        String sql = "UPDATE goals SET goal_name=?, target_amount=?, current_amount=?, start_date=?, end_date=?, status=? WHERE goal_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, goal.getGoalName());
            pstmt.setDouble(2, goal.getTargetAmount());
            pstmt.setDouble(3, goal.getCurrentAmount());
            pstmt.setDate(4, new java.sql.Date(goal.getStartDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(goal.getEndDate().getTime()));
            pstmt.setString(6, goal.getStatus());
            pstmt.setInt(7, goal.getGoalId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException se) {
            System.err.println("Error updating goal: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Database error updating goal.", "Update Error");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }

    public boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM goals WHERE goal_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, goalId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException se) {
            System.err.println("Error deleting goal: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Database error deleting goal.", "Delete Error");
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            DatabaseConnection.closeConnection(conn);
        }
        return false;
    }
}
