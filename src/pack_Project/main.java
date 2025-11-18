
package pack_Project;

import pack_Project.DAO.BudgetDAO;
import pack_Project.DAO.TransactionDAO;
import pack_Project.DAO.UserDAO;
import pack_Project.GUI.LoginFrame;

import javax.swing.*;

public class main {

    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private BudgetDAO budgetDAO;

    public main() {
        // we create the object hear
        userDAO = new UserDAO();
        transactionDAO = new TransactionDAO();
        budgetDAO = new BudgetDAO();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }


    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }


    public BudgetDAO getBudgetDAO() {
        return budgetDAO;
    }

    public static void main(String[] args) {
//       we create the obj of personalFinancialApp obj and through to LoginFram() class
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        catch (Exception e)
        {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            main app = new main();
            new LoginFrame(app); // Pass the application instance to LoginFrame
        });
    }
}
