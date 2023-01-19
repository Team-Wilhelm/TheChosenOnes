package Budgetflix.DAL;

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
     * If there is a connection, which is not in use, get the available connection.
     * Otherwise, opens a new connection.
     * @return database connection.
     */
    public Connection getConnection() throws SQLException {
        Connection connection;
        if (freeConnections.isEmpty()){
            connection = ds.getConnection();
        }
        else{
            connection = freeConnections.remove(0);
            if (!connection.isValid(50)) {
                connection = ds.getConnection();
            }
        }
        usedConnections.add(connection);
        return connection;
    }

    /**
     * After a query is done, the connection is added to the pool of free connections to use again.
     * @param connection The connection to release
     */
    public void releaseConnection(Connection connection){
        if (usedConnections.contains(connection)){
            usedConnections.remove(connection);
            freeConnections.add(0, connection);
        }
    }

    /**
     * Closes all open connections
     */
    public void closeAllConnections() {
        usedConnections.forEach(this::releaseConnection);
        for (Connection con : freeConnections){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}