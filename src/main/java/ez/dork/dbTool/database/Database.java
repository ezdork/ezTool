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

	public String getListOfDatabaseSql() {
		return "SELECT table_catalog as view_column FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' GROUP BY table_catalog;";
	}

	public String getListOfTablesSql(String table_catalog) {
		return String
				.format("SELECT table_name as view_column FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' AND table_catalog = '%s' ORDER BY table_type, table_name;",
						table_catalog);
	}

	public String getListOfColumnsSql(String tableCatalog, String tableName) {
		return String.format("SELECT column_name as view_column FROM information_schema.columns where table_catalog = '%s' AND table_schema = 'public' and table_name = '%s';", tableCatalog, tableName);
	}

}
