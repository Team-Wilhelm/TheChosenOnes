package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class BudgetConnection {
    private static final SQLServerDataSource ds = new SQLServerDataSource();

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
        return ds.getConnection();
    }
}
