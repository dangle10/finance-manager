package pack_Project.DTO;

import java.util.Date;

public class Goal {
    private int goalId;
    private int userId;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private Date startDate;
    private Date endDate;
    private String status; // "Đang thực hiện", "Hoàn thành", "Hủy"

    // Constructor đầy đủ tham số
    public Goal(int goalId, int userId, String goalName, double targetAmount,
                double currentAmount, Date startDate, Date endDate, String status) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Constructor không có goalId (dùng khi tạo mới trước khi lưu vào DB)
    public Goal(int userId, String goalName, double targetAmount,
                double currentAmount, Date startDate, Date endDate, String status) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // --- Getters ---
    public int getGoalId() {
        return goalId;
    }

    public int getUserId() {
        return userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    // --- Setters ---
    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Helper Methods (Phương thức bổ trợ để hiển thị lên UI) ---

    // Tính phần trăm hoàn thành
    public double getProgressPercentage() {
        if (targetAmount == 0) return 0;
        double percent = (currentAmount / targetAmount) * 100;
        return Math.min(percent, 100); // Không vượt quá 100%
    }

    // Tính số tiền còn thiếu
    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentAmount);
    }
}