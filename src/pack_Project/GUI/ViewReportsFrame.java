
package pack_Project.GUI;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.DefaultCellEditor;
import java.awt.*;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import pack_Project.DTO.Transaction;


public class ViewReportsFrame extends JFrame {
    private List<Transaction> transactions;
    private String[] expenseCategories;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private PieChartPanel pieChartPanel;
    private JButton exportButton;

    private Function<Integer, Boolean> deleteCallback;

    private int deleteButtonColumn = 6;
    private ActionListener deleteActionListener;
    public ViewReportsFrame(JFrame parent, List<Transaction> userTransactions, String[] expenseCategories, Function<Integer, Boolean> deleteCallback) {
        super("Personal Finance Manager - Reports");
        this.transactions = userTransactions;
        this.expenseCategories = expenseCategories;
        this.deleteCallback = deleteCallback;

        setSize(900, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        initComponents();
        populateTable();
        updatePieChart();
        addListeners();
        setVisible(true);
    }


    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));

        JLabel reportTitle = new JLabel("Financial Reports");
        reportTitle.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        reportTitle.setForeground(new Color(0, 102, 204));
        reportTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(reportTitle, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Date", "Type", "Category", "Amount", "Note", ""};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == deleteButtonColumn;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == deleteButtonColumn) {
                    return JButton.class;
                }
                return super.getColumnClass(column);
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        transactionTable.getTableHeader().setBackground(new Color(220, 230, 240));
        transactionTable.setFillsViewportHeight(true);


        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        transactionTable.getColumnModel().getColumn(0).setMaxWidth(60);
        transactionTable.getColumnModel().getColumn(deleteButtonColumn).setPreferredWidth(40);
        transactionTable.getColumnModel().getColumn(deleteButtonColumn).setMaxWidth(40);

        JScrollPane tableScrollPane = new JScrollPane(transactionTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "All Transactions", 0, 0, new java.awt.Font("Arial", java.awt.Font.BOLD, 14), Color.DARK_GRAY));
        tableScrollPane.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        JPanel chartsPanel = new JPanel(new BorderLayout());
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Expense Breakdown", 0, 0, new java.awt.Font("Arial", java.awt.Font.BOLD, 14), Color.DARK_GRAY));

        pieChartPanel = new PieChartPanel();
        pieChartPanel.setPreferredSize(new Dimension(400, 300));
        chartsPanel.add(pieChartPanel, BorderLayout.CENTER);

        bottomPanel.add(chartsPanel, BorderLayout.CENTER);

        JPanel exportButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportButtonPanel.setOpaque(false);
        exportButton = createActionButton("Export to PDF", new Color(0, 150, 136));
        exportButtonPanel.add(exportButton);

        bottomPanel.add(exportButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(new LineBorder(bgColor.darker(), 2));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void addListeners() {
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportReportToPdf();
            }
        });

        deleteActionListener = e -> {
            int modelRow = transactionTable.convertRowIndexToModel(transactionTable.getEditingRow());
            handleDeleteTransaction(modelRow);
        };

        transactionTable.getColumnModel().getColumn(deleteButtonColumn).setCellRenderer(new ButtonRenderer());
        transactionTable.getColumnModel().getColumn(deleteButtonColumn).setCellEditor(new ButtonEditor(new JCheckBox(), deleteActionListener));
    }


    private void populateTable() {
        tableModel.setRowCount(0);

        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Transaction t : transactions) {
            Object[] rowData = {
                    t.getTransactionId(),
                    sdf.format(t.getDate()),
                    t.getType(),
                    t.getCategory(),
                    String.format("%,.0f VND", t.getAmount()),
                    t.getNote() != null ? t.getNote() : "",
                    "x"
            };
            tableModel.addRow(rowData);
        }
    }


    private void updatePieChart() {
        List<Transaction> expenses = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());

        Map<String, Double> categoryExpenses = expenses.stream()
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));

        String[] chartNames = new String[categoryExpenses.size()];
        double[] chartValues = new double[categoryExpenses.size()];
        int i = 0;
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            chartNames[i] = entry.getKey();
            chartValues[i] = entry.getValue();
            i++;
        }

        pieChartPanel.setChartData(chartNames, chartValues);
        pieChartPanel.repaint();
    }

    private void handleDeleteTransaction(int modelRow) {
        if (modelRow == -1) {
            CustomMessageBox.showWarningMessage(this, "No transaction selected for deletion.", "Error");
            return;
        }

        int transactionId = (int) tableModel.getValueAt(modelRow, 0);

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to PERMANENTLY delete transaction ID: " + transactionId + "?\n" +
                        "This will update your dashboard and cannot be undone.",
                "Confirm Permanent Deletion",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean dbSuccess = deleteCallback.apply(transactionId);

                if (dbSuccess) {
                    transactions.removeIf(t -> t.getTransactionId() == transactionId);
                    populateTable();
                    updatePieChart();
                    CustomMessageBox.showInfoMessage(this, "Transaction " + transactionId + " permanently deleted.", "Success");
                } else {
                    CustomMessageBox.showErrorMessage(this, "Failed to delete from Database.", "Error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                CustomMessageBox.showErrorMessage(this, "Error: " + e.getMessage(), "Error");
            }
        }
    }

    private void exportReportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report to PDF");
        fileChooser.setSelectedFile(new File("PersonalFinanceReport_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            Document document = new Document(PageSize.A4);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(0, 102, 204));
                Paragraph title = new Paragraph("Personal Finance Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                Font dateFont = new Font(Font.HELVETICA, 12, Font.ITALIC, Color.GRAY);
                Paragraph reportDate = new Paragraph("Report Date: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), dateFont);
                reportDate.setAlignment(Element.ALIGN_RIGHT);
                reportDate.setSpacingAfter(10);
                document.add(reportDate);


                document.add(new Paragraph("All Transactions:", new Font(Font.HELVETICA, 16, Font.BOLD)));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount() - 1);
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(10f);
                pdfTable.setSpacingAfter(10f);

                float[] columnWidths = {0.8f, 1.2f, 0.8f, 1.2f, 1.0f, 2.0f};
                pdfTable.setWidths(columnWidths);

                Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
                Color headerBgColor = new Color(63, 150, 219);
                for (int i = 0; i < tableModel.getColumnCount() - 1; i++) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(tableModel.getColumnName(i), headerFont));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    headerCell.setBackgroundColor(headerBgColor);
                    headerCell.setPadding(5);
                    pdfTable.addCell(headerCell);
                }

                Font dataFont = new Font(Font.HELVETICA, 9);
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount() - 1; j++) {
                        Object cellValue = tableModel.getValueAt(i, j);
                        String cellText = (cellValue != null) ? cellValue.toString() : "";

                        PdfPCell dataCell = new PdfPCell(new Phrase(cellText, dataFont));
                        dataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        dataCell.setPadding(4);
                        if (i % 2 == 0) {
                            dataCell.setBackgroundColor(new Color(245, 245, 245));
                        }
                        pdfTable.addCell(dataCell);
                    }
                }
                document.add(pdfTable);

                CustomMessageBox.showInfoMessage(this, "Report saved successfully to: \n" + fileToSave.getAbsolutePath(), "Export Successful");

            } catch (Exception ex) {
                CustomMessageBox.showErrorMessage(this, "Failed to export report to PDF: " + ex.getMessage(), "Export Error");
            } finally {
                if (document.isOpen()) {
                    document.close();
                }
            }
        }
    }
}


class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setForeground(Color.WHITE);
        setBackground(new Color(220, 53, 69));
        setText("x");
        setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        setBorderPainted(false);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private ActionListener actionListener;

    public ButtonEditor(JCheckBox checkBox, ActionListener listener) {
        super(checkBox);
        this.actionListener = listener;

        button = new JButton();
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(220, 53, 69));
        button.setText("x");
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        button.addActionListener(e -> {
            if (isPushed) {

                if (actionListener != null) {
                    actionListener.actionPerformed(e);
                }
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "x" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
