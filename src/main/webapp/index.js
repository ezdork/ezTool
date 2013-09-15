function query(sql) {
	$('#dg').edatagrid('loading');
	$.post('gridViewServlet', {
		sql : sql
	}, function(data) {
		var json = JSON.parse(data);
		$('#msg').html(json.msg);
		if (json.result) {
			$('#dg').edatagrid({
				columns : json.result.columns,
				data : json.result.data
			});
		}
		$('#dg').edatagrid('loaded');
	});
}

function getSelectedText() {
	var result = '';
	if (window.getSelection) { // all browsers, except IE before version 9
		var range = window.getSelection();
		result = range.toString();
	} else {
		if (document.selection.createRange) { // Internet Explorer
			var range = document.selection.createRange();
			result = range.text;
		}
	}
	return result;
}

var isA = false;
function check() {
	// $('#message').html(event.keyCode);
	if (event.keyCode == 17)
		isA = true;
	if (isA && event.keyCode == 13) {
		if (getSelectedText().length > 0) {
			query(getSelectedText());
		} else {
			query($('#sql').val());
		}
	}
}

function pagerFilter(data) {
	if (typeof data.length == 'number' && typeof data.splice == 'function') { // is
		// array
		data = {
			total : data.length,
			rows : data
		};
	}
	var dg = $(this);
	var opts = dg.datagrid('options');
	var pager = dg.datagrid('getPager');
	pager.pagination({
		onSelectPage : function(pageNum, pageSize) {
			opts.pageNumber = pageNum;
			opts.pageSize = pageSize;
			pager.pagination('refresh', {
				pageNumber : pageNum,
				pageSize : pageSize
			});
			dg.datagrid('loadData', data);
		}
	});
	if (!data.originalRows) {
		data.originalRows = (data.rows);
	}
	var start = (opts.pageNumber - 1) * parseInt(opts.pageSize);
	var end = start + parseInt(opts.pageSize);
	data.rows = (data.originalRows.slice(start, end));
	return data;
}

var maxId = 0;
function getMaxId(datas) {
	if (!datas)
		datas = $('#tt').treegrid('getRoots');

	for ( var i in datas) {
		var data = datas[i];
		var currentId = parseInt(data.id, 10);
		maxId = (maxId > currentId ? maxId : currentId);
		if (data.children) {
			getMaxId(data.children);
		}
	}
	return maxId;
}

$(function() {
	$('#tt').treegrid({
		onDblClickRow : function(row) {
			if ($('#tt').treegrid('getChildren', row.id).length > 0) {
				return;
			}
			var param = {
				maxId : getMaxId()
			};
			if (row.iconCls == 'icon-database') {
				param.tableCatalog = row.name;
			} else if (row.iconCls == 'icon-table') {
				// query('select * from ' + row.name);

				param.tableName = row.name;
				param.tableCatalog = $('#tt').treegrid('getParent', row.id).name;
			} else if (row.iconCls == 'icon-column') {
				return;
			}
			$("#tt").treegrid("loading");
			$.post('treeViewServlet', param, function(data) {
				var data_list = JSON.parse(data);
				if (data_list) {
					$('#tt').treegrid('append', {
						parent : row.id,
						data : data_list
					});
				}
				$("#tt").treegrid("loaded");
			});

		}
	});

	$('#dg').edatagrid({
		title : '列表',
		height : 'auto',
		collapsible : true, // 是否可折叠的
		fit : true, // 自动大小
		pagination : true, // 分页控件
		editing : true,
		loadFilter : pagerFilter,
		toolbar : [ {
			text : '新增',
			iconCls : 'icon-add',
			handler : function() {
				$('#dg').edatagrid('addRow', 0);
			}
		}, '-', {
			text : '儲存',
			iconCls : 'icon-save',
			handler : function() {
				$('#dg').edatagrid('saveRow');
			}
		}, '-', {
			text : '删除',
			iconCls : 'icon-remove',
			handler : function() {
				$('#dg').edatagrid('destroyRow');
			}
		} ]
	});
});