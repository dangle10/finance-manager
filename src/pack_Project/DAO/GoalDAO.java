package pack_Project.DAO;

import pack_Project.DTO.Goal;
import pack_Project.GUI.CustomMessageBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoalDAO {

    private static final String GOAL_FILE_NAME = "goals.txt";

    public boolean addGoal(Goal goal) {
        try {
            File file = FileStorageUtil.getDataFile(GOAL_FILE_NAME);
            int newId = FileStorageUtil.generateNextId(file);
            goal.setGoalId(newId);
            String line = newId + ";" +
                    goal.getUserId() + ";" +
                    safe(goal.getGoalName()) + ";" +
                    goal.getTargetAmount() + ";" +
                    goal.getCurrentAmount() + ";" +
                    FileStorageUtil.formatDate(goal.getStartDate()) + ";" +
                    FileStorageUtil.formatDate(goal.getEndDate()) + ";" +
                    safe(goal.getStatus());
            FileStorageUtil.appendLine(file, line);
            return true;
        } catch (IOException e) {
            System.err.println("Error adding goal: " + e.getMessage());
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error adding goal.", "Goal Error");
        }
        return false;
    }

    public List<Goal> getGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        try {
            File file = FileStorageUtil.getDataFile(GOAL_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 8) continue;
                int uid = Integer.parseInt(parts[1]);
                if (uid != userId) continue;
                int id = Integer.parseInt(parts[0]);
                String name = parts[2];
                double targetAmount = Double.parseDouble(parts[3]);
                double currentAmount = Double.parseDouble(parts[4]);
                Date startDate = FileStorageUtil.parseDate(parts[5]);
                Date endDate = FileStorageUtil.parseDate(parts[6]);
                String status = parts[7];
                goals.add(new Goal(id, uid, name, targetAmount, currentAmount, startDate, endDate, status));
            }
        } catch (Exception se) {
            System.err.println("Error retrieving goals: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error retrieving goals.", "Data Retrieval Error");
        }
        return goals;
    }


    public boolean updateGoal(Goal goal) {
        try {
            File file = FileStorageUtil.getDataFile(GOAL_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            boolean updated = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 1) {
                    newLines.add(line);
                    continue;
                }
                int id = Integer.parseInt(parts[0]);
                if (id == goal.getGoalId()) {
                    String newLine = goal.getGoalId() + ";" +
                            goal.getUserId() + ";" +
                            safe(goal.getGoalName()) + ";" +
                            goal.getTargetAmount() + ";" +
                            goal.getCurrentAmount() + ";" +
                            FileStorageUtil.formatDate(goal.getStartDate()) + ";" +
                            FileStorageUtil.formatDate(goal.getEndDate()) + ";" +
                            safe(goal.getStatus());
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
        } catch (Exception se) {
            System.err.println("Error updating goal: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error updating goal.", "Update Error");
        }
        return false;
    }


    public boolean deleteGoal(int goalId) {
        try {
            File file = FileStorageUtil.getDataFile(GOAL_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            boolean removed = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 1) continue;
                int id = Integer.parseInt(parts[0]);
                if (id == goalId) {
                    removed = true;
                    continue;
                }
                newLines.add(line);
            }
            if (removed) {
                FileStorageUtil.writeAllLines(file, newLines);
            }
            return removed;
        } catch (Exception se) {
            System.err.println("Error deleting goal: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error deleting goal.", "Delete Error");
        }
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
