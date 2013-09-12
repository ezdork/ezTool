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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setCharacterEncoding("utf8");

		String tableCatalog = request.getParameter("tableCatalog");
		String tableName = request.getParameter("tableName");

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		if (tableCatalog != null && tableName != null) {
			getColumnList(tableCatalog, tableName, result);
		} else if (tableCatalog != null) {
			getTableList(tableCatalog, result);
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
				d.put("id", i++);
				d.put("iconCls", "database");
			}
			result.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getTableList(String table_catalog, List<Map<String, Object>> result) {
		try {
			List<Map<String, Object>> data = SqlUtil.getTableList(table_catalog);
			for (Map<String, Object> d : data) {
				d.put("iconCls", "table");
			}
			result.addAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getColumnList(String tableCatalog, String tableName, List<Map<String, Object>> result) {
		try {
			List<Map<String, Object>> data = SqlUtil.getColumnList(tableCatalog, tableName);
			for (Map<String, Object> d : data) {
				d.put("iconCls", "column");
			}
			result.addAll(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String genErrMsg(String msg) {
		return String.format("<div class='err'>%s.</div>", msg);
	}

	private static String genOkMsg(String msg) {
		return String.format("<div class='ok'>%s.</div>", msg);
	}

}
