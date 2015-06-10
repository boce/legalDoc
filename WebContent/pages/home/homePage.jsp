<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<%@include file="/pages/share/base.jsp"%>
	<%@include file="/pages/home/norFileDisplay.jsp"%>
	<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/legaldoc/home.css" />
	<title>首页</title>
	<script type="text/javascript">
		var fileStatus = eval('(${fileStatus})');
		var orgTypeEnum = eval('(${orgTypeEnum})');
		var orgType = "${orgType}";
		
		$(function(){
			var dataDivWindowHeight = window.screen.height;
			if (dataDivWindowHeight ===600 || dataDivWindowHeight < 600) {
				$('#dataDivWindow').css('height','285px');
				$('#displayWindow').window({'height': 280});
				$('#fileDocContent').css({'height': 240});
			}
			if (dataDivWindowHeight > 600 && dataDivWindowHeight <= 800 ) {
				$('#dataDivWindow').css('height','455px');
				$('#displayWindow').window({'height': 440});
				$('#fileDocContent').css({'height': 400});
			}
			$('#toDoGridId').datagrid({   
			    url:'${pageContext.request.contextPath}/home/getMyTasks.do'
			}); 
			//初始化本部门起草或制定的规范性文件
			$('#norFileGridId').datagrid({   
			    url:'${pageContext.request.contextPath}/home/getOwnOrgNorFiles.do',
			    tools: [{
			    	iconCls:'icon-search',
			    	handler:openSearch
			    }],
			    onDblClickRow:function(rowIndex, rowData){
			    	$("#displayWindow").window("open");
			    	$("#displayWindow").window("center");
			    	load(rowData.id);
			    }
			}); 
			//查询本部门起草或制定的规范性文件
			$("#searchBtn").click(function(){
				doSearch();
			});
			//关闭查询窗口
			$("#searchCloseBtn").click(function(){
				$("#norFileSearch").dialog("close");
			});
			//根据登录用户的组织机构类型添加能查看的规范性文件
			if(orgType == "PROVINCE_GOV" || orgType == "PROVINCE_WORK_DEPART"){
				addTab(orgTypeEnum["PROVINCE_GOV"], "PROVINCE_GOV");
				addTab(orgTypeEnum["PROVINCE_WORK_DEPART"], "PROVINCE_WORK_DEPART");
				addTab(orgTypeEnum["CITY_GOV"], "CITY_GOV");
				addTab(orgTypeEnum["CITY_WORK_DEPART"], "CITY_WORK_DEPART");
				addTab(orgTypeEnum["COUNTY_GOV"], "COUNTY_GOV");
				addTab(orgTypeEnum["COUNTY_WORK_DEPART"], "COUNTY_WORK_DEPART");
				addTab(orgTypeEnum["COUNTRY_GOV"], "COUNTRY_GOV");
				addTab(orgTypeEnum["VERTICAL_MGT_DEPT"], "VERTICAL_MGT_DEPT");
				$("#norFileTab").tabs('select', orgTypeEnum[orgType]);
			}else if(orgType == "CITY_GOV" || orgType == "CITY_WORK_DEPART"){
				addTab(orgTypeEnum["CITY_GOV"], "CITY_GOV");
				addTab(orgTypeEnum["CITY_WORK_DEPART"], "CITY_WORK_DEPART");
				addTab(orgTypeEnum["COUNTY_GOV"], "COUNTY_GOV");
				addTab(orgTypeEnum["COUNTY_WORK_DEPART"], "COUNTY_WORK_DEPART");
				addTab(orgTypeEnum["COUNTRY_GOV"], "COUNTRY_GOV");
				addTab(orgTypeEnum["VERTICAL_MGT_DEPT"], "VERTICAL_MGT_DEPT");
				$("#norFileTab").tabs('select', orgTypeEnum[orgType]);
			}else if(orgType == "COUNTY_GOV" || orgType == "COUNTY_WORK_DEPART"){
				addTab(orgTypeEnum["COUNTY_GOV"], "COUNTY_GOV");
				addTab(orgTypeEnum["COUNTY_WORK_DEPART"], "COUNTY_WORK_DEPART");
				addTab(orgTypeEnum["COUNTRY_GOV"], "COUNTRY_GOV");
				addTab(orgTypeEnum["VERTICAL_MGT_DEPT"], "VERTICAL_MGT_DEPT");
				$("#norFileTab").tabs('select', orgTypeEnum[orgType]);
			}else if(orgType == "COUNTRY_GOV"){
				addTab(orgTypeEnum[orgType], orgType);
				addTab(orgTypeEnum["VERTICAL_MGT_DEPT"], "VERTICAL_MGT_DEPT");
				$("#norFileTab").tabs('select', orgTypeEnum[orgType]);
			}else if(orgType == "VERTICAL_MGT_DEPT"){
				addTab(orgTypeEnum[orgType], orgType);
				$("#norFileTab").tabs('select', orgTypeEnum[orgType]);
			}
			
			//根据名称查询已发布规范性文件
			$("#norFileSearchBtn").click(function(){
				doNorFileSearch();
			});
			//关闭已发布规范性文件查询窗口
			$("#norFileSearchCloseBtn").click(function(){
				$("#norFileTabSearch").dialog("close");
			});
		});
		//
		function addTab(title, orgType, id) {
			var content;
			if (typeof(id) == "undefined") {
				id = "norFileTab";
			}
			var tt = $('#' + id);
			if (tt.tabs('exists', title)) {//如果tab已经存在,则选中并刷新该tab          
				tt.tabs('select', title);
				$('#'+orgType+"Id").datagrid('reload');    
			} else {
				if (orgType) {
					content = "<table id=\""+orgType+"Id\"></table>";
				} else {
					content = '未实现';
				}
				tt.tabs('add', {
					title : title,
					content : content,
					orgType : orgType
				});
				if(content != "未实现"){
					var gridId = orgType + "Id";
					$('#'+gridId).datagrid({   
					    url:'${pageContext.request.contextPath}/home/getOrgNorFiles.do',
					    rownumbers:true,
					    singleSelect:true,
					    pagination:true,
					    border:false,
					    height:315,
					    queryParams:{orgType:orgType},
					    columns:[[
					          {field:'id',title:'编号',width:100,hidden:true},    
				              {field:'name',title:'文件名称',width:450},    
				              {field:'fileStatus',title:'状态',width:100,formatter:formatStatus}    
				        ]],
				        onDblClickRow:function(rowIndex, rowData){
					    	$("#displayWindow").window("open");
					    	load(rowData.id);
					    }
					}); 
				}
				return tt.tabs("getTab", title);
			}
		}
		function formatStatus(value){
			return fileStatus[value];
		}
		
		function openSearch(){
			$("#norFileSearch").dialog("open");
		}
		function doSearch(){
			$('#norFileGridId').datagrid('load', {
			    name: $("#nameSearch").val()
			}); 
		}
		
		function openNorFileSearch(){
			$("#norFileTabSearch").dialog("open");
		}
		//根据名称查询已发布规范性文件
		function doNorFileSearch(){
			var selectedTab = $('#norFileTab').tabs('getSelected');
			var currentOrgType = selectedTab.panel('options').orgType;
			$('#'+currentOrgType+"Id").datagrid('load', {    
			    name: $("#norFileSearchName").val(),    
			    orgType: currentOrgType  
			});
		}
	</script>
</head>
<body>
	<div id="dataDivWindow" class="dataDiv">
		<table id="toDoGridId" border="0" title="我的待办事项" data-options="pagination:true,singleSelect:true,rownumbers:true,height: 252">  
		    <thead>  
		        <tr>  
		        	<th data-options="field:'id',hidden:true,width:100">id</th>
		            <th data-options="field:'taskName',width:600">待办事项</th>
		            <th data-options="field:'createDate',width:100">日期</th>
		        </tr>  
		    </thead> 
		    <tbody></tbody>  
		</table>
		<div>
			<div class="norFileLeft">
				<table id="norFileGridId" title="本部门起草或制定的规范性文件" data-options="rownumbers:true,singleSelect:true,pagination:true,border:false,height: 370">  
				    <thead>  
				        <tr>  
				        	<th data-options="field:'id',hidden:true,width:100">id</th>
				            <th data-options="field:'name',width:550">文件名称</th>
				        </tr>  
				    </thead> 
				    <tbody></tbody>
				</table>
			</div>
			<div class="norFileRight">
				<div id="p" class="easyui-panel" title="市、县（区）、工作部门已发布规范性文件" data-options="border:false,tools: [{iconCls:'icon-search',handler:openNorFileSearch}]" >
					<div id="norFileTab" class="easyui-tabs" data-options="rownumbers:true,singleSelect:true,pagination:true,border:false">
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="norFileSearch" class="easyui-dialog" title="查询规范性文件"
	        data-options="iconCls:'icon-query',buttons:'#search-buttons',resizable:false,closed:true,width:320">
	    <div class="norFileSearchName">
	    	<label>文件名称:</label>
	    	<input class="easyui-validatebox textbox" type="text" id="nameSearch" name="nameSearch"/>
	    </div>
		<div id="search-buttons" class="dialog-button">
			<a id="searchBtn" href="javascript:void(0)" class="easyui-linkbutton"">查询</a>
			<a id="searchCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
	
	<!-- 查找弹出层 -->
	<div id="norFileTabSearch" class="easyui-dialog" title="查询规范性文件"
	        data-options="iconCls:'icon-query',buttons:'#norfile-search-buttons',resizable:false,closed:true,width:320">
	    <form id="searchForm" method="post">
	    	<div class="norFileSearchName">
		    	<label>文件名称:</label>
		    	<input class="easyui-validatebox textbox" type="text" id="norFileSearchName" name="norFileSearchName"/>
		    </div>
	    </form>
		<div id="norfile-search-buttons" class="dialog-button">
			<a id="norFileSearchBtn" href="javascript:void(0)" class="easyui-linkbutton"">查询</a>
			<a id="norFileSearchCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
</body>
</html>