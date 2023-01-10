package DAL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class BudgetConnection {
    private static final SQLServerDataSource ds = new SQLServerDataSource();
    private static final List<Connection> freeConnections = new ArrayList<>();
    private static final List<Connection> usedConnections = new ArrayList<>();

    public BudgetConnection(){
        ds.setServerName("10.176.111.31");
        ds.setDatabaseName("budgetflix_CSE22B_theChosenOnes");
        ds.setPortNumber(1433);
        ds.setUser("CSe22B_14");
        ds.setPassword("ExtremelySecurePassword_7");
        ds.setTrustServerCertificate(true);
    }

    /**
     * Creates a connection to our database.
     * @return database connection.
     */
    public Connection getConnection() throws SQLException {
        if (freeConnections.isEmpty()){
            Connection connection = ds.getConnection();
            usedConnections.add(connection);
            return connection;
        }
        else{
            Connection connection = freeConnections.remove(0);
            if (connection.isValid(50)){
                usedConnections.add(connection);
                return connection;
            } else{
                connection = ds.getConnection();
                usedConnections.add(connection);
                return connection;
            }
        }
    }

    public void releaseConnection(Connection connection){
        if (usedConnections.contains(connection)){
            usedConnections.remove(connection);
            freeConnections.add(0, connection);
        }
    }
}