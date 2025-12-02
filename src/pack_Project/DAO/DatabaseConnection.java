
package pack_Project.DAO;

import pack_Project.GUI.CustomMessageBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "snute";
    private static final String DB_PASSWORD = "";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";


    public static Connection getConnection() {
        Connection conn = null;
        try {

            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);


        } catch (ClassNotFoundException e) {
            // JDBC Driver not found
            System.err.println("JDBC Driver not found");
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Error to connect to database", "Driver Error");
        } catch (SQLException se) {

            System.err.println("SQL Exception during database connection:");
            se.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Error to connect to database", "Database Connection Error");
        } catch (Exception e) {
            

            System.err.println("JDBC Driver not found");
            e.printStackTrace();
            CustomMessageBox.showErrorMessage(null, "Error to connect to database", "Driver Error");
        }
        return conn;
    }


    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException se) {
                System.err.println("Error closing database connection:");
                se.printStackTrace();
            }
        }
    }


}
