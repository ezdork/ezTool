package ez.dork.dbTool.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import ez.dork.dbTool.util.SqlUtil;

/**
 * Simple Hello servlet.
 */
public final class TreeViewServlet extends HttpServlet {
	public static String Name = "name";
	public static String iconCls = "iconCls";
	public static String id = "id";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setCharacterEncoding("utf8");

		String maxId = request.getParameter("maxId");

		String tableCatalog = request.getParameter("tableCatalog");
		String tableName = request.getParameter("tableName");

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (tableCatalog != null && tableName != null) {
			getColumnList(tableCatalog, tableName, result, Integer.valueOf(maxId));
		} else if (tableCatalog != null) {
			getTableList(tableCatalog, result, Integer.valueOf(maxId));
		} else {
			getDatabaseList(result);
		}

		PrintWriter writer = response.getWriter();
		writer.print(new Gson().toJson(result));
		writer.close();
	}

	private void getDatabaseList(List<Map<String, Object>> result) {
		try {
			List<Map<String, Object>> data = SqlUtil.getDatabaseList();

			int i = 1;
			for (Map<String, Object> d : data) {
				d.put(id, i++);
				d.put(iconCls, "icon-database");
				d.put(Name, d.get("table_cat"));
			}
			result.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getTableList(String table_catalog, List<Map<String, Object>> result, int maxId) {
		try {
			List<Map<String, Object>> data = SqlUtil.getTableList(table_catalog);
			for (Map<String, Object> d : data) {
				d.put(id, ++maxId);
				d.put(iconCls, "icon-table");
				d.put(Name, d.get("table_name"));
			}
			result.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getColumnList(String tableCatalog, String tableName, List<Map<String, Object>> result, int maxId) {
		try {
			List<Map<String, Object>> data = SqlUtil.getColumnList(tableCatalog, tableName);
			for (Map<String, Object> d : data) {
				d.put(id, ++maxId);
				d.put(iconCls, "icon-column");
				d.put(Name, d.get("column_name"));
			}
			result.addAll(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
