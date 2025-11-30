package pack_Project.GUI;

import pack_Project.DAO.BudgetDAO;
import pack_Project.DTO.Budget;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BudgetPlanningWindow extends JFrame {

    private int userId;
    private BudgetDAO budgetDAO;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbCategory;
    private JTextField txtMonth;
    private JTextField txtLimit;
    private JTextField txtId;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public BudgetPlanningWindow(int userId) {
        this.userId = userId;
        this.budgetDAO = new BudgetDAO();

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setTitle("Budget Planning");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Budget Setting"));
        pnlInput.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        cbCategory = new JComboBox<>(new String[]{"Food", "Rent", "Transport", "Utilities", "Entertainment", "Healthcare", "Other"});
        txtMonth = new JTextField(new SimpleDateFormat("yyyy-MM").format(new Date()));
        txtLimit = new JTextField();
        txtId = new JTextField();
        txtId.setVisible(false);


        gbc.gridx = 0; gbc.gridy = 0; pnlInput.add(new JLabel("Month (YYYY-MM):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; pnlInput.add(txtMonth, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlInput.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; pnlInput.add(cbCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlInput.add(new JLabel("Limit Amount (VND):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; pnlInput.add(txtLimit, gbc);


        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 5, 5));


        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");


        pnlButtons.add(btnAdd);
        pnlButtons.add(btnUpdate);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        pnlInput.add(pnlButtons, gbc);

        add(pnlInput, BorderLayout.WEST);


        String[] columns = {"ID", "Month", "Category", "Limit Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtMonth.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    cbCategory.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    txtLimit.setText(tableModel.getValueAt(selectedRow, 3).toString());

                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);


        addActionListeners();
    }

    private void addActionListeners() {

        btnAdd.addActionListener(e -> {
            if (validateInput()) {
                Budget b = new Budget(0, userId, txtMonth.getText(),
                        cbCategory.getSelectedItem().toString(),
                        Double.parseDouble(txtLimit.getText()));

                if (budgetDAO.addBudget(b)) {
                    JOptionPane.showMessageDialog(this, "Add budget successfully!");
                    loadDataToTable();
                    clearForm();
                }
            }
        });


        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            if (validateInput()) {
                Budget b = new Budget(Integer.parseInt(txtId.getText()), userId,
                        txtMonth.getText(),
                        cbCategory.getSelectedItem().toString(),
                        Double.parseDouble(txtLimit.getText()));

                if (budgetDAO.updateBudget(b)) {
                    JOptionPane.showMessageDialog(this, "Update successfully!");
                    loadDataToTable();
                    clearForm();
                }
            }
        });


        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this budget?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (budgetDAO.deleteBudget(Integer.parseInt(txtId.getText()))) {
                    JOptionPane.showMessageDialog(this, "Deleated!");
                    loadDataToTable();
                    clearForm();
                }
            }
        });


        btnClear.addActionListener(e -> clearForm());
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Budget> list = budgetDAO.getBudgetsByUserId(userId);
        for (Budget b : list) {
            tableModel.addRow(new Object[]{
                    b.getBudgetId(),
                    b.getMonth(),
                    b.getCategory(),
                    String.format("%.0f", b.getLimitAmount())
            });
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtLimit.setText("");
        cbCategory.setSelectedIndex(0);
        table.clearSelection();
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private boolean validateInput() {
        if (txtMonth.getText().isEmpty() || txtLimit.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all information!");
            return false;
        }
        try {
            Double.parseDouble(txtLimit.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền giới hạn phải là số!");
            return false;
        }
        return true;
    }
}
