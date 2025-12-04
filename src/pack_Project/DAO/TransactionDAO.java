
package pack_Project.DAO;

import pack_Project.DTO.Transaction;
import pack_Project.GUI.CustomMessageBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDAO {

    private static final String TX_FILE_NAME = "transactions.txt";

    public boolean addTransaction(Transaction transaction) {
        try {
            File file = FileStorageUtil.getDataFile(TX_FILE_NAME);
            int newId = FileStorageUtil.generateNextId(file);
            transaction.setTransactionId(newId);
            String line = newId + ";" +
                    transaction.getUserId() + ";" +
                    FileStorageUtil.formatDate(transaction.getDate()) + ";" +
                    safe(transaction.getType()) + ";" +
                    safe(transaction.getCategory()) + ";" +
                    transaction.getAmount() + ";" +
                    safe(transaction.getNote());
            FileStorageUtil.appendLine(file, line);
            return true;
        } catch (IOException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error adding transaction.", "Transaction Error");
        }
        return false;
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            File file = FileStorageUtil.getDataFile(TX_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 7) continue;
                int uid = Integer.parseInt(parts[1]);
                if (uid != userId) continue;
                int txId = Integer.parseInt(parts[0]);
                Date date = FileStorageUtil.parseDate(parts[2]);
                String type = parts[3];
                String category = parts[4];
                double amount = Double.parseDouble(parts[5]);
                String note = parts[6];
                transactions.add(new Transaction(
                        txId,
                        uid,
                        date,
                        type,
                        category,
                        amount,
                        note
                ));
            }
        } catch (Exception se) {
            System.err.println("Error retrieving transactions: " + se.getMessage());
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "File error retrieving transactions. Check console for details.", "Data Retrieval Error");
        }
        return transactions;
    }

    public boolean deleteTransaction(int transactionId) {
        try {
            File file = FileStorageUtil.getDataFile(TX_FILE_NAME);
            List<String> lines = FileStorageUtil.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            boolean removed = false;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 1) continue;
                int id = Integer.parseInt(parts[0]);
                if (id == transactionId) {
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
            System.err.println("Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(";", ",");
    }
}
