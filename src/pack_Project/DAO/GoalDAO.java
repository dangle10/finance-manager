package pack_Project.DAO;

import pack_Project.DTO.Goal;
import pack_Project.GUI.CustomMessageBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    // 1. Thêm mục tiêu mới
    public boolean addGoal(Goal goal) {
        String sql = "INSERT INTO goals (user_id, goal_name, target_amount, current_amount, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getGoalName());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getCurrentAmount());
            // Chuyển đổi java.util.Date sang java.sql.Date
            pstmt.setDate(5, new java.sql.Date(goal.getStartDate().getTime()));
            pstmt.setDate(6, new java.sql.Date(goal.getEndDate().getTime()));
            pstmt.setString(7, goal.getStatus());

            int rowsAffected = pstmt.executeUpdate();

            // Lấy ID tự động sinh ra và gán lại vào object Goal
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goal.setGoalId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException se) {
            System.err.println("Error adding goal: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Database error adding goal.", "Goal Error");
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

    // 2. Lấy danh sách mục tiêu theo User ID
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
                        rs.getDate("start_date"), // JDBC tự động map sang java.sql.Date (con của java.util.Date)
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

    // 3. Cập nhật thông tin mục tiêu (Ví dụ: nạp thêm tiền, đổi tên, đổi trạng thái)
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

    // 4. Xóa mục tiêu
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
