<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href=" ${pageContext.request.contextPath}/css/legaldoc/searchNormativeFile.css"/>
<title>Insert title here</title>
<script type="text/javascript">
	var nFileName = ""; //规范性文件的名称(全局)
	//打开查询弹出页面
	function openFrame(stage) {
		//设置查询的datagrid的数据集url
		$('#edsDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/pages/norFile/getNorFileReference.do',
			queryParams : {
				stage : stage
			},
			onDblClickRow: function (rowIndex, rowData) {
				var nId = rowData.id;
				load(nId, 'NORFILE');
				$('#searchWindow').window('close'); //关闭查询窗口
			}
		});

		$('#searchWindow').window('open'); //打开规范性文件查询窗口

	}

	//关闭window窗口
	function closeWindow() {
		$('#searchWindow').window('close');
	}

	//查询规范性文件
	function searchFile() {
		var fileName = $('#searchFileNameId').val();
		$('#edsDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/pages/norFile/getNorFileReference.do?name='+fileName
		});
		$('#edsDataGridId').datagrid('reload'); //加载数据,实现文件的过滤
	}

	//点击确认
	function comfirmWindow() {
		var selectRow = $('#edsDataGridId').datagrid('getSelected');
		if(!selectRow){
			showMsg("请先选择一个规范性文件");
			return;
		}
		var nId = selectRow.id;
		//$('#norId').val(nId); //设置规范性文件id
		load(nId, 'NORFILE');
		$('#searchWindow').window('close'); //关闭查询窗口
		
		//window.location.href = "${pageContext.request.contextPath}/pages/norFile/comfrimNormativeFile.do?norId="
				//+ nId;
		
	}
</script>

</head>
<body>
	<!-- 弹出页面 -->
	<div id="searchWindow" title="规范性文件查询" class="easyui-window"
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName">
			<tr>
				<td class="bd">文件名称:</td>
				<td class="bd"><input id="searchFileNameId" name="examinationDraft" size="60" /></td>
				<td class="bd"><input type="button" onclick="searchFile()" value="查询" /></td>
			</tr>
		</table>

		<table id="edsDataGridId" data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
			<thead>
				<tr>
					<th data-options="field:'id',hidden:'true'">id</th>
					<th data-options="field:'name',width:'557'">文件名</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>

		<table class="search-button-norfile" >
			<tr>
				<td class="search-button-td">
				<input id="confirmId" onclick="comfirmWindow()" type="button" value="确认" />
				<input id="cancelId" type="button" onclick="closeWindow();" value="取消" />
				</td>
			</tr>
		</table>
	</div>
</body>
</html>