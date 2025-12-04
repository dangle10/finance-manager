package pack_Project.GUI;

import pack_Project.DAO.GoalDAO;
import pack_Project.DTO.Goal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class GoalWindow extends JFrame {

    private int userId;
    private GoalDAO goalDAO;
    private JPanel goalsContainer;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GoalWindow(int userId) {
        this.userId = userId;
        this.goalDAO = new GoalDAO();

        setTitle("Financial Goals Manager");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        initComponents();
        loadGoals();
    }

    private void initComponents() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("My Financial Goals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);


        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setBackground(Color.WHITE);
        goalsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(goalsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);


        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addGoalBtn = new JButton("+ Add New Goal");
        addGoalBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addGoalBtn.setBackground(Color.BLACK);
        addGoalBtn.setFocusPainted(false);
        addGoalBtn.setPreferredSize(new Dimension(200, 45));


        addGoalBtn.addActionListener(e -> showGoalDialog(null));

        footerPanel.add(addGoalBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }


    private void loadGoals() {
        goalsContainer.removeAll();
        List<Goal> goals = goalDAO.getGoalsByUserId(userId);

        if (goals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No goals set yet. Start saving today!");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            goalsContainer.add(emptyLabel);
        } else {
            for (Goal g : goals) {
                goalsContainer.add(createGoalCard(g));
                goalsContainer.add(Box.createVerticalStrut(15));
            }
        }
        goalsContainer.revalidate();
        goalsContainer.repaint();
    }


    private JPanel createGoalCard(Goal goal) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(goal.getGoalName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(52, 73, 94));

        JLabel statusLabel = new JLabel(goal.getStatus());
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        if ("Hoàn thành".equals(goal.getStatus())) statusLabel.setForeground(new Color(39, 174, 96));
        else statusLabel.setForeground(Color.GRAY);

        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.add(statusLabel, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);


        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) goal.getProgressPercentage());
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(155, 89, 182));
        progressBar.setBackground(new Color(236, 240, 241));


        String moneyInfo = String.format("Saved: %,.0f / %,.0f VND", goal.getCurrentAmount(), goal.getTargetAmount());
        String dateInfo = "Deadline: " + (goal.getEndDate() != null ? sdf.format(goal.getEndDate()) : "N/A");

        JLabel infoLabel = new JLabel(moneyInfo);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel dateLabel = new JLabel(dateInfo);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(infoLabel);
        centerPanel.add(dateLabel);
        card.add(centerPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton depositBtn = new JButton("Deposit");
        depositBtn.setBackground(Color.BLACK);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(Color.BLACK);


        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(Color.BLACK);



        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter amount to save for: " + goal.getGoalName());
            if (input != null && !input.isEmpty()) {
                try {
                    double amount = Double.parseDouble(input);
                    goal.setCurrentAmount(goal.getCurrentAmount() + amount);


                    if(goal.getCurrentAmount() >= goal.getTargetAmount()) {
                        goal.setStatus("Hoàn thành");
                        JOptionPane.showMessageDialog(this, "Congratulations! Goal Completed!");
                    }

                    goalDAO.updateGoal(goal);
                    loadGoals();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!");
                }
            }
        });


        editBtn.addActionListener(e -> showGoalDialog(goal));


        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this goal?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                goalDAO.deleteGoal(goal.getGoalId());
                loadGoals();
            }
        });

        buttonPanel.add(depositBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }


    private void showGoalDialog(Goal goalToEdit) {
        JDialog dialog = new JDialog(this, goalToEdit == null ? "Add New Goal" : "Edit Goal", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField(goalToEdit != null ? goalToEdit.getGoalName() : "");
        JTextField targetField = new JTextField(goalToEdit != null ? String.valueOf(goalToEdit.getTargetAmount()) : "");


        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startEditor);
        if(goalToEdit != null) startDateSpinner.setValue(goalToEdit.getStartDate());

        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy");
        endDateSpinner.setEditor(endEditor);
        if(goalToEdit != null) endDateSpinner.setValue(goalToEdit.getEndDate());

        dialog.add(new JLabel("  Goal Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("  Target Amount:"));
        dialog.add(targetField);
        dialog.add(new JLabel("  Start Date:"));
        dialog.add(startDateSpinner);
        dialog.add(new JLabel("  Target Date:"));
        dialog.add(endDateSpinner);

        if (goalToEdit != null) {
            dialog.add(new JLabel("  Status:"));
            String[] statuses = {"Đang thực hiện", "Hoàn thành", "Hủy"};
            JComboBox<String> statusBox = new JComboBox<>(statuses);
            statusBox.setSelectedItem(goalToEdit.getStatus());
            dialog.add(statusBox);
        } else {
            dialog.add(new JLabel(""));
            dialog.add(new JLabel(""));
        }

        JButton saveBtn = new JButton("Save Goal");
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Goal name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double target;
                try {
                    target = Double.parseDouble(targetField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid target amount! Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (target <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Target amount must be greater than 0!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                

                double maxAmount = 999999999999999.99;
                if (target > maxAmount) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Target amount is too large!\n" +
                        "Maximum allowed: 999,999,999,999,999.99\n" +
                        "Please enter a smaller amount.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Date start = (Date) startDateSpinner.getValue();
                Date end = (Date) endDateSpinner.getValue();
                
                if (start == null || end == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select both start and end dates!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (end.before(start)) {
                    JOptionPane.showMessageDialog(dialog, "End date must be after start date!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = false;
                if (goalToEdit == null) {

                    Goal newGoal = new Goal(userId, name, target, 0, start, end, "Đang thực hiện");
                    success = goalDAO.addGoal(newGoal);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Goal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {

                    goalToEdit.setGoalName(name);
                    goalToEdit.setTargetAmount(target);
                    goalToEdit.setStartDate(start);
                    goalToEdit.setEndDate(end);

                    success = goalDAO.updateGoal(goalToEdit);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Goal updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
                if (success) {
                    dialog.dispose();
                    loadGoals();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid target amount! Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(saveBtn);

        dialog.setVisible(true);
    }
}