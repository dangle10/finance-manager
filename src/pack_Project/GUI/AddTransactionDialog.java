
package pack_Project.GUI;

import pack_Project.DAO.TransactionDAO;
import pack_Project.DTO.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddTransactionDialog extends JDialog {
    private DashboardFrame parentFrame;
    private int userId;
    private TransactionDAO transactionDAO;

    private JTextField dateField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JTextArea noteArea;


    private JButton addButton;
    private JButton cancelButton;

    private String[] expenseCategories;
    private String[] incomeCategories;

    public AddTransactionDialog(DashboardFrame parent, int userId, String[] expenseCategories, String[] incomeCategories, TransactionDAO transactionDAO) {
        super(parent, "Add New Transaction", true);
        this.parentFrame = parent;
        this.userId = userId;
        this.expenseCategories = expenseCategories;
        this.incomeCategories = incomeCategories;
        this.transactionDAO = transactionDAO;

        setLayout(new GridBagLayout());
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setBackground(Color.WHITE);

        initComponents();
        addListeners();
    }


    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));



        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(dateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(dateField, gbc);


        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] types = {"Expense", "Income"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(typeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(typeComboBox, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryComboBox = new JComboBox<>(expenseCategories);
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(categoryLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(categoryComboBox, gbc);


        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(amountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(amountField, gbc);

        JLabel noteLabel = new JLabel("Note (Optional):");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noteArea = new JTextArea(4, 25);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; formPanel.add(noteLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; formPanel.add(noteScrollPane, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        addButton = createButton("Add", new Color(46, 204, 113));
        cancelButton = createButton("Cancel", new Color(231, 76, 60));

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        add(formPanel);
    }

    private void addListeners() {
        typeComboBox.addActionListener(e -> {
            String selectedType = (String) typeComboBox.getSelectedItem();
            categoryComboBox.removeAllItems();

            if ("Expense".equals(selectedType)) {
                for (String cat : expenseCategories) {
                    categoryComboBox.addItem(cat);
                }
            } else {
                for (String cat : incomeCategories) {
                    categoryComboBox.addItem(cat);
                }
            }
            categoryComboBox.setSelectedIndex(0);
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }


    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }


    private void addTransaction() {
        try {
            String type = (String) typeComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
            String note = noteArea.getText().trim();

            if (amount <= 0) {
                CustomMessageBox.showErrorMessage(this.parentFrame, "Amount must be greater than zero.", "Input Error");
                return;
            }


            Transaction newTransaction = new Transaction(0, userId, date, type.toLowerCase(), category, amount, note);

            boolean success = transactionDAO.addTransaction(newTransaction);

            if (success) {
                CustomMessageBox.showInfoMessage(this.parentFrame, "Transaction added successfully!", "Success");
                parentFrame.refreshDashboard();
                dispose();
            } else {
                CustomMessageBox.showErrorMessage(this.parentFrame, "Failed to add transaction to database.", "Database Error");
            }

        } catch (NumberFormatException ex) {
            CustomMessageBox.showErrorMessage(this.parentFrame, "Please enter a valid number for Amount.", "Input Error");
        } catch (java.text.ParseException ex) {
            CustomMessageBox.showErrorMessage(this.parentFrame, "Please enter date in YYYY-MM-DD format.", "Input Error");
        } catch (Exception ex) {
            CustomMessageBox.showErrorMessage(this.parentFrame, "An unexpected error occurred: " + ex.getMessage(), "Error");
            ex.printStackTrace();
        }
    }
}
