package ez.dork.dbTool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	public Connection getConnection() throws ClassNotFoundException, SQLException {

		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost:5432/stock_smart";
		String user = "postgres";
		String password = "Pass@w0rd";

		return DriverManager.getConnection(url, user, password);
	}
	
}
