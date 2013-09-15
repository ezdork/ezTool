package ez.dork.dbTool.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ez.dork.dbTool.database.Database;

public class SqlUtil {

	private static Database database = new Database();

	public static int executeUpdate(String sql) throws Exception {

		int updatecount = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();
			stmt = conn.createStatement();
			updatecount = stmt.executeUpdate(sql);
		} finally {
			close(conn, stmt, rs);
		}

		return updatecount;
	}

	public static List<Map<String, Object>> getRelationList() throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();

			DatabaseMetaData databaseMetaData = conn.getMetaData();
			rs = databaseMetaData.getExportedKeys(null, null, null);
			result = toListOfMaps(rs);

		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	public static List<Map<String, Object>> getDatabaseList() throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			rs = databaseMetaData.getCatalogs();
			result = toListOfMaps(rs);
		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	public static List<Map<String, Object>> getTableList(String table_catalog) throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();
			DatabaseMetaData databaseMetaData = conn.getMetaData();
			String[] types = { "TABLE" };
			rs = databaseMetaData.getTables(table_catalog, null, "%", types);
			result = toListOfMaps(rs);
			for (Map<String, Object> resultMap : result) {
				String sql = String.format("select * from %s", resultMap.get("table_name"));
				String relation = String.format("<div onclick='query(\"%s\")' class='easyui-linkbutton easyui-tooltip icon-search' title='%s'>&nbsp;</div>", sql, sql);
				resultMap.put("relation", relation);
			}
		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	public static List<Map<String, Object>> getColumnList(String tableCatalog, String tableName) throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();

			DatabaseMetaData databaseMetaData = conn.getMetaData();
			rs = databaseMetaData.getColumns(tableCatalog, null, tableName, null);
			result = toListOfMaps(rs);
			// for (Map<String, Object> resultMap : result) {
			// System.out.println(resultMap);
			// }

			rs = databaseMetaData.getPrimaryKeys(null, null, tableName);
			List<Map<String, Object>> primaryKeyList = toListOfMaps(rs);
			for (Map<String, Object> primaryKeyMap : primaryKeyList) {
				String columnName = (String) primaryKeyMap.get("column_name");
				for (Map<String, Object> map : result) {
					if (columnName.equals(map.get("column_name"))) {
						map.put("relation", "PK");
					}
				}
			}

			rs = databaseMetaData.getExportedKeys(null, null, null);
			List<Map<String, Object>> relationList = toListOfMaps(rs);

			for (Map<String, Object> relationMap : relationList) {
				String pktable_name = (String) relationMap.get("pktable_name");
				String pkcolumn_name = (String) relationMap.get("pkcolumn_name");
				String fktable_name = (String) relationMap.get("fktable_name");
				String fkcolumn_name = (String) relationMap.get("fkcolumn_name");
				for (Map<String, Object> resultMap : result) {
					String table_name = (String) resultMap.get("table_name");
					String column_name = (String) resultMap.get("column_name");
					if (fktable_name.equalsIgnoreCase(table_name) && fkcolumn_name.equalsIgnoreCase(column_name)) {
						String relation = String.format("<div title='%s' class='easyui-tooltip'>%s</div>", pktable_name + "." + pkcolumn_name, "FK");

						resultMap.put("relation", relation);
					}
				}
			}

		} finally {
			close(conn, stmt, rs);
		}

		return result;
	}

	public static List<Map<String, Object>> query(String sql) throws Exception {

		List<Map<String, Object>> result = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = database.getConnection();
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
			conn = database.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			result = toEditablegrid(rs);
		} finally {
			close(conn, stmt, rs);
		}

		return result;
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
				columns.put(metaData.getColumnLabel(i).toLowerCase(), rs.getObject(i));
			}

			rows.add(columns);
		}
		return rows;
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
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
