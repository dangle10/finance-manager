
package pack_Project.DAO;

import pack_Project.DTO.Budget;
import pack_Project.GUI.CustomMessageBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    private static final String BUDGET_FILE_NAME = "budgets.txt";

    public List<Budget> getBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        try {
            File file = FileStorageUtil.getDataFile(BUDGET_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;
                int uid = Integer.parseInt(parts[1]);
                if (uid != userId) continue;
                int id = Integer.parseInt(parts[0]);
                String month = parts[2];
                String category = parts[3];
                double limitAmount = Double.parseDouble(parts[4]);
                budgets.add(new Budget(id, uid, month, category, limitAmount));
            }
        } catch (Exception se) {
            se.printStackTrace();
        }
        return budgets;
    }

    public boolean addBudget(Budget budget) {
        if (isBudgetExists(budget.getUserId(), budget.getMonth(), budget.getCategory())) {
            CustomMessageBox.showErrorMessage(null, "Ngân sách cho mục này trong tháng " + budget.getMonth() + " đã tồn tại!", "Trùng lặp");
            return false;
        }
        try {
            File file = FileStorageUtil.getDataFile(BUDGET_FILE_NAME);
            int newId = FileStorageUtil.generateNextId(file);
            budget.setBudgetId(newId);
            String line = newId + ";" +
                    budget.getUserId() + ";" +
                    budget.getMonth() + ";" +
                    budget.getCategory().replace(";", ",") + ";" +
                    budget.getLimitAmount();
            FileStorageUtil.appendLine(file, line);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBudget(Budget budget) {
        try {
            File file = FileStorageUtil.getDataFile(BUDGET_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            boolean updated = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) {
                    newLines.add(line);
                    continue;
                }
                int id = Integer.parseInt(parts[0]);
                if (id == budget.getBudgetId()) {
                    String newLine = budget.getBudgetId() + ";" +
                            budget.getUserId() + ";" +
                            budget.getMonth() + ";" +
                            budget.getCategory().replace(";", ",") + ";" +
                            budget.getLimitAmount();
                    newLines.add(newLine);
                    updated = true;
                } else {
                    newLines.add(line);
                }
            }
            if (updated) {
                FileStorageUtil.writeAllLines(file, newLines);
            }
            return updated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBudget(int budgetId) {
        try {
            File file = FileStorageUtil.getDataFile(BUDGET_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            boolean removed = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 1) continue;
                int id = Integer.parseInt(parts[0]);
                if (id == budgetId) {
                    removed = true;
                    continue;
                }
                newLines.add(line);
            }
            if (removed) {
                FileStorageUtil.writeAllLines(file, newLines);
            }
            return removed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isBudgetExists(int userId, String month, String category) {
        try {
            File file = FileStorageUtil.getDataFile(BUDGET_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;
                int uid = Integer.parseInt(parts[1]);
                String m = parts[2];
                String c = parts[3];
                if (uid == userId && m.equals(month) && c.equals(category)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
