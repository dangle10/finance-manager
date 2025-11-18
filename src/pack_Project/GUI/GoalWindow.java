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
    private JPanel goalsContainer; // Panel chứa danh sách các thẻ mục tiêu
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public GoalWindow(int userId) {
        this.userId = userId;
        this.goalDAO = new GoalDAO();

        setTitle("Financial Goals Manager");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chỉ đóng cửa sổ này, không tắt App
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        initComponents();
        loadGoals(); // Tải dữ liệu từ Database
    }

    private void initComponents() {
        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182)); // Màu tím chủ đạo
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("My Financial Goals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // --- BODY (SCROLL PANE) ---
        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setBackground(Color.WHITE);
        goalsContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(goalsContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER (ADD BUTTON) ---
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addGoalBtn = new JButton("+ Add New Goal");
        addGoalBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addGoalBtn.setBackground(Color.BLACK);
        addGoalBtn.setFocusPainted(false);
        addGoalBtn.setPreferredSize(new Dimension(200, 45));


        addGoalBtn.addActionListener(e -> showGoalDialog(null)); // Null = Chế độ thêm mới

        footerPanel.add(addGoalBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- HÀM TẢI DỮ LIỆU ---
    private void loadGoals() {
        goalsContainer.removeAll(); // Xóa danh sách cũ
        List<Goal> goals = goalDAO.getGoalsByUserId(userId);

        if (goals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No goals set yet. Start saving today!");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
            goalsContainer.add(emptyLabel);
        } else {
            for (Goal g : goals) {
                goalsContainer.add(createGoalCard(g));
                goalsContainer.add(Box.createVerticalStrut(15)); // Khoảng cách giữa các thẻ
            }
        }
        goalsContainer.revalidate();
        goalsContainer.repaint();
    }

    // --- TẠO GIAO DIỆN CHO TỪNG MỤC TIÊU (CARD) ---
    private JPanel createGoalCard(Goal goal) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160)); // Chiều cao cố định

        // 1. Top: Tên và Trạng thái
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

        // 2. Center: Progress Bar và Số tiền
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Progress Bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) goal.getProgressPercentage());
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(155, 89, 182)); // Màu tím
        progressBar.setBackground(new Color(236, 240, 241));

        // Info Text
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

        // 3. Right: Action Buttons (Nạp tiền, Sửa, Xóa)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton depositBtn = new JButton("Deposit");
        depositBtn.setBackground(Color.BLACK);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(Color.BLACK);


        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(Color.BLACK);


        // Logic Nút Nạp tiền
        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter amount to save for: " + goal.getGoalName());
            if (input != null && !input.isEmpty()) {
                try {
                    double amount = Double.parseDouble(input);
                    goal.setCurrentAmount(goal.getCurrentAmount() + amount);

                    // Tự động cập nhật trạng thái nếu hoàn thành
                    if(goal.getCurrentAmount() >= goal.getTargetAmount()) {
                        goal.setStatus("Hoàn thành");
                        JOptionPane.showMessageDialog(this, "Congratulations! Goal Completed!");
                    }

                    goalDAO.updateGoal(goal);
                    loadGoals(); // Refresh lại giao diện
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount!");
                }
            }
        });

        // Logic Nút Sửa
        editBtn.addActionListener(e -> showGoalDialog(goal));

        // Logic Nút Xóa
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

    // --- DIALOG THÊM / SỬA MỤC TIÊU ---
    private void showGoalDialog(Goal goalToEdit) {
        JDialog dialog = new JDialog(this, goalToEdit == null ? "Add New Goal" : "Edit Goal", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 10, 10)); // 6 hàng, 2 cột

        JTextField nameField = new JTextField(goalToEdit != null ? goalToEdit.getGoalName() : "");
        JTextField targetField = new JTextField(goalToEdit != null ? String.valueOf(goalToEdit.getTargetAmount()) : "");

        // Xử lý ngày tháng (Đơn giản hóa bằng Text field dd/MM/yyyy nếu chưa có JCalendar)
        // Tốt nhất bạn nên thêm thư viện JCalendar sau này.
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
            dialog.add(new JLabel("")); // Placeholder
            dialog.add(new JLabel(""));
        }

        JButton saveBtn = new JButton("Save Goal");
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                double target = Double.parseDouble(targetField.getText());
                Date start = (Date) startDateSpinner.getValue();
                Date end = (Date) endDateSpinner.getValue();

                if (goalToEdit == null) {
                    // ADD NEW
                    Goal newGoal = new Goal(userId, name, target, 0, start, end, "Đang thực hiện");
                    goalDAO.addGoal(newGoal);
                } else {
                    // UPDATE
                    goalToEdit.setGoalName(name);
                    goalToEdit.setTargetAmount(target);
                    goalToEdit.setStartDate(start);
                    goalToEdit.setEndDate(end);
                    // Nếu có combo box status thì cập nhật thêm status ở đây
                    goalDAO.updateGoal(goalToEdit);
                }
                dialog.dispose();
                loadGoals(); // Refresh list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input! Please check fields.");
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel("")); // Spacer
        dialog.add(saveBtn);

        dialog.setVisible(true);
    }
}