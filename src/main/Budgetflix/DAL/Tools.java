package Budgetflix.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Methods commonly used by both DAOs
 */
public class Tools {
    private static final BudgetConnection bc = new BudgetConnection();
    private static PreparedStatement preparedStatement;

    /**
     * Gets a free connection from the BudgetConnection class and executes a query
     * @param query to be executed
     */
    public static void executeSQLQuery(String query) throws SQLException {
        Connection connection = null;
        try{
            connection = bc.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        }finally {
            bc.releaseConnection(connection);
        }
    }

    /**
     * Gets a free connection from the BudgetConnection class and executes a query
     * Afterwards, returns a ResultSet
     * @param query to be executed
     */
    public static ResultSet executeSQLQueryWithResult(String query) throws SQLException {
        Connection connection = null;
        try{
            connection = bc.getConnection();
            preparedStatement = connection.prepareStatement(query);
            return preparedStatement.executeQuery();
        }
        finally {
            bc.releaseConnection(connection);
        }
    }

    /**
     * Replaces the ' character in a string in order to avoid syntax issues with SQL
     */
    public static String validateStringForSQL(String string) {
        if (string == null) return null;
        string = string.replace("'", "''");
        return string;
    }

    /**
     * On closing the app, closes all open connections
     */
    public static void closeAllConnections(){
        bc.closeAllConnections();
    }
}
