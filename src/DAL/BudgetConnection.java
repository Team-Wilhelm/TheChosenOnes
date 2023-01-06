package DAL;

import java.sql.Connection;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;


public class BudgetConnection {

    /**
     * Creates a connection to our database.
     * @return database connection.
     */
    public Connection getConnection() {
        SQLServerDataSource ds;
        ds = new SQLServerDataSource();
        ds.setDatabaseName("CSe22B_17_MyTunes");
        ds.setUser("CSe22B_17");
        ds.setPassword("CSe22B_17");
        ds.setServerName("10.176.111.31");
        ds.setPortNumber(1433);
        ds.setTrustServerCertificate(true);
        try {
            return ds.getConnection();
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        }
    }
}
