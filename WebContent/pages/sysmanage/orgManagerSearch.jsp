<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
	<script type="text/javascript">
		$(function() {
			$("#confirmId").click(function(){
				comfirmWindow();
			});
			$("#cancelId").click(function(){
				closeWindow();
			});
			$("#searchOrgId").click(function(){
				searchOrg();
			});
		});
		//打开查询弹出页面
		function openFrame() {
			//设置查询的datagrid的数据集url
	        $('#edsDataGridId').datagrid({   
			    url:'${pageContext.request.contextPath}/org/findOrgShortReference.do',
			    queryParams:{orgName:""},
			    onDblClickRow:function(rowIndex, rowData){
			    	var orgId = rowData.id;
					currentType = orgNodeType[0];
					getOrgById(orgId);
					$('#searchWindow').window('close');	//关闭查询窗口
			    }
			});  
	        $('#searchWindow').window('open');	
	
	    }
		
		//关闭window窗口
		function closeWindow() {
			$('#searchWindow').window('close');
		}
		
		//查询组织机构
		function searchOrg() {
			$('#edsDataGridId').datagrid({   
				queryParams:{orgName:$('#searchOrgName').val()}
			}); 
			$('#edsDataGridId').datagrid('reload');	//重新加载数据
		}
		
		//点击确认
		function comfirmWindow() {
			var selectRow = $('#edsDataGridId').datagrid('getSelected');
			if(selectRow == null){
				showMsg("请先选择一行记录");
			}
			var orgId = selectRow.id;
			currentType = orgNodeType[0];
			getOrgById(orgId);
			$('#searchWindow').window('close');	//关闭查询窗口
		}
	</script>
</head>
<body>
	<!-- 弹出页面 -->
	<div id="searchWindow" title="组织机构查询"  class="easyui-window" 
			data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 558, height: 300, padding: 0">
		<table class="search-fileName" >
			<tr>
				<td>组织机构名称:</td>
				<td><input id="searchOrgName" name="searchOrgName" size="50" /></td>
				<td><input id="searchOrgId" type="button" value="查询" /></td>
			</tr>
		</table>
		
		<table id="edsDataGridId"
				data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">  
		    <thead>  
		        <tr>  
		        	<th data-options="field:'id', hidden:'true', width:100">id</th>
		            <th data-options="field:'name', width:500" >名称</th>  
		        </tr>  
		    </thead> 
		    <tbody></tbody>
		</table>
		
		<table class="search-button">
			<tr>
				<td width="570px"></td>
				<td><input id="confirmId" type="button" value="确认" /></td>
				<td><input id="cancelId" type="button" value="取消" /></td>
			</tr>
		</table> 
	</div>
</body>
</html>