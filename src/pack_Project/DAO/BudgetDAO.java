package pack_Project.DAO;

import pack_Project.DTO.Budget;
import pack_Project.GUI.CustomMessageBox;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public List<Budget> getBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY month DESC, category ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                budgets.add(new Budget(
                        rs.getInt("budget_id"),
                        rs.getInt("user_id"),
                        rs.getString("month"),
                        rs.getString("category"),
                        rs.getDouble("limit_amount")
                ));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return budgets;
    }

    public boolean addBudget(Budget budget) {
        if (isBudgetExists(budget.getUserId(), budget.getMonth(), budget.getCategory())) {
            CustomMessageBox.showErrorMessage(null, "Ngân sách cho mục này trong tháng " + budget.getMonth() + " đã tồn tại!", "Trùng lặp");
            return false;
        }

        String sql = "INSERT INTO budgets (user_id, month, category, limit_amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budget.getUserId());
            pstmt.setString(2, budget.getMonth());
            pstmt.setString(3, budget.getCategory());
            pstmt.setDouble(4, budget.getLimitAmount());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE budgets SET limit_amount = ?, month = ?, category = ? WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, budget.getLimitAmount());
            pstmt.setString(2, budget.getMonth());
            pstmt.setString(3, budget.getCategory());
            pstmt.setInt(4, budget.getBudgetId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean deleteBudget(int budgetId) {
        String sql = "DELETE FROM budgets WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budgetId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public boolean isBudgetExists(int userId, String month, String category) {
        String sql = "SELECT count(*) FROM budgets WHERE user_id = ? AND month = ? AND category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, month);
            pstmt.setString(3, category);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    }
}
