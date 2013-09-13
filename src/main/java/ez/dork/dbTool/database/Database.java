package ez.dork.dbTool.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		return getPostgresqlConnection();
//		return getInformixConnection();
	}

	private Connection getPostgresqlConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost:5432/stock_smart";
		String user = "postgres";
		String password = "Pass@w0rd";

		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection getInformixConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.informix.jdbc.IfxDriver");
		String url = "jdbc:informix-sqli://10.0.3.102:7777/mvdis_drv:INFORMIXSERVER=devinst1;DB_LOCALE=zh_tw.utf8";
		String user = "informix";
		String password = "abcd1111";
		return DriverManager.getConnection(url, user, password);
	}

}
