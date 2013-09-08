package ez.dork.dbTool.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlUtil {

	public static int executeUpdate(String sql) throws Exception {

		int updatecount = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			updatecount = stmt.executeUpdate(sql);
		} finally {
			close(conn, stmt, rs);
		}

		return updatecount;
	}

	public static List<Map<String, Object>> query(String sql) throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			result = toListOfMaps(rs);
		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	public static Map<String, Object> queryToEditablegrid(String sql) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			result = toEditablegrid(rs);
		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	private static Connection getConnection() throws ClassNotFoundException, SQLException {

		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost:5432/stock_smart";
		String user = "postgres";
		String password = "Pass@w0rd";

		return DriverManager.getConnection(url, user, password);
	}

	private static Map<String, Object> toEditablegrid(ResultSet rs) throws SQLException {
		Map<String, Object> result = new LinkedHashMap<String, Object>();

		List<Map<String, Object>> metadata = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		int j = 1;
		while (rs.next()) {

			Map<String, Object> columns = new LinkedHashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				String columnLabel = metaData.getColumnLabel(i);
				if (j == 1) {
					Map<String, Object> meta = new LinkedHashMap<String, Object>();
					meta.put("field", columnLabel);
					meta.put("title", columnLabel.toUpperCase());

					// meta.put("type", metaData.getColumnTypeName(i));
					meta.put("editor", "text");
					meta.put("resizable", true);
					meta.put("sortable", true);
					metadata.add(meta);
				}

				columns.put(columnLabel, rs.getObject(i));
			}

			data.add(columns);
			j++;
		}
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(metadata);

		result.put("columns", list);
		result.put("data", data);
		return result;
	}

	private static List<Map<String, Object>> toListOfMaps(ResultSet rs) throws SQLException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		while (rs.next()) {
			Map<String, Object> columns = new LinkedHashMap<String, Object>();

			for (int i = 1; i <= columnCount; i++) {
				columns.put(metaData.getColumnLabel(i), rs.getObject(i));
			}

			rows.add(columns);
		}
		return rows;
	}

	private static void close(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
}
