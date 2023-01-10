package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tools {
    private static final BudgetConnection bc = new BudgetConnection();
    private static PreparedStatement preparedStatement;

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

    public static String validateStringForSQL(String string) {
        if (string == null) return null;
        string = string.replace("'", "''");
        return string;
    }
}
