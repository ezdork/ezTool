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

	public String getListOfTablesSql() {
		return "SELECT table_catalog, table_name FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' ORDER BY table_type, table_name;";
	}

	public String getListOfColumnsSql(String table_catalog, String tableName) {
		return String.format("SELECT * FROM information_schema.columns where table_catalog = '%s' AND table_schema = 'public' and table_name = '%s';", table_catalog, tableName);
	}

}
