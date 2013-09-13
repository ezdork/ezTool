package ez.dork.dbTool.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

	String getRelation = request.getParameter("getRelation");

	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

	if (getRelation != null) {
	    getRelationList(result);
	} else if (tableCatalog != null && tableName != null) {
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

    private void getRelationList(List<Map<String, Object>> result) {
	try {
	    List<Map<String, Object>> data = SqlUtil.getRelationList();
	    for (Map<String, Object> d : data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pktable_name", d.get("pktable_name"));
		map.put("pkcolumn_name", d.get("pkcolumn_name"));
		map.put("fktable_name", d.get("fktable_name"));
		map.put("fkcolumn_name", d.get("fkcolumn_name"));
		result.add(map);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void getDatabaseList(List<Map<String, Object>> result) {
	try {
	    List<Map<String, Object>> data = SqlUtil.getDatabaseList();

	    int i = 1;
	    for (Map<String, Object> d : data) {
		d.put("id", i++);
		d.put("iconCls", "icon-database");
		d.put("view_column", d.get("table_cat"));
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
		d.put("iconCls", "icon-table");
		d.put("view_column", d.get("table_name"));
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
		d.put("iconCls", "icon-column");
		d.put("view_column", d.get("column_name"));
	    }
	    result.addAll(data);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
