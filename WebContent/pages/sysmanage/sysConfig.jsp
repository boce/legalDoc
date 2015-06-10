<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>系统参数配置</title>
<%@include file="/pages/share/base.jsp"%>
<style type="text/css">
	#orgForm{display: none;}
	table td{border: 0px;}
</style>
<script type="text/javascript">

	$(function(){
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		//初始化系统参数管理列表
		$('#sysConfigDataGridId').datagrid({   
		    url:'${pageContext.request.contextPath}/sysConfig/getSysConfigRef.do',
		    singleSelect:true,
		    pagination:true,
		    rownumbers:true,
		    height:306
		}); 
		$("#addBtn").click(function(){
			add();
		});
		$("#modifyBtn").click(function(){
			modify();
		});
		$("#deleteBtn").click(function(){
			deleteSysConfig();
		});
		$("#openSearchBtn").click(function(){
			openSearch();
		});
		$("#saveBtn").click(function(){
			save();
		});
		$("#saveCloseBtn").click(function(){
			$('#addOrModifyDiv').dialog('close');
		});
		$("#searchBtn").click(function(){
			searchSysConfig();
		});
		$("#searchCloseBtn").click(function(){
			$('#searchDiv').dialog('close');
		});
	});
	
	function formatUsed(value)
	{
		return (!!value) ? "是" : "否";
	}
	
	//打开新增窗口
	function add()
	{
		$("#sysConfigForm").form("clear");
		$("input[name='isUsed']").prop("checked", true);
		$('#addOrModifyDiv').dialog('open');
	}
	//保存系统配置参数
	function save()
	{
		$('#sysConfigForm').form('submit', {   
		    url:"${pageContext.request.contextPath}/sysConfig/saveOrUpdate.do",
		    onSubmit: function(){   
		    	var isValid = $(this).form('validate');
				return isValid;
		    },   
		    success:function(data){   
		        var dataJson = eval("(" + data + ")");
		        if(dataJson.success){
		        	$('#sysConfigForm').form('clear');
		        	$('#addOrModifyDiv').dialog('close');
		        	$('#sysConfigDataGridId').datagrid('reload');
		        	showMsg("保存成功！");
		        }
		        else{
		        	showMsg("保存失败！");
		        }
		    }
		});  
	}
	//打开修改参数窗口
	function modify()
	{
		var selectRow = $('#sysConfigDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg("请先选择一行记录！");
			return;
		}
		$("#sysConfigForm").form('load', {
		   id:selectRow.id,
		   code:selectRow.code,
		   name:selectRow.name,
		   value:selectRow.value,
		   description:selectRow.description,
		   isUsed:selectRow.isUsed,
		});
		$("input[name='isUsed']").prop("checked", selectRow.isUsed);
		if($('#addOrModifyDiv').is(":hidden"))
		{
			$('#addOrModifyDiv').dialog('open');
		}
	}
	
	//假删除系统参数配置
	function deleteSysConfig()
	{
		var selectRow = $('#sysConfigDataGridId').datagrid('getSelected');
		if(selectRow == null){
			showMsg("请先选择一行记录！");
			return;
		}
		if(confirm("确认禁用？")){
			$.ajax({
				   type: "POST",
				   url: "${pageContext.request.contextPath}/sysConfig/deleteSysConfigById.do",
				   data: {id:selectRow.id},
				   dataType:"json",
				   success: function(data){
					   if(data.success){
						   showMsg("禁用成功！");
						   $('#sysConfigDataGridId').datagrid('reload');
					   }
					   else{
						   showMsg("禁用失败！");
					   }
				   }
			});
		}
		
	}
	
	//打开查询窗口
	function openSearch()
	{
		if($('#searchDiv').is(":hidden"))
		{
			$('#searchDiv').dialog('open');
		}
	}
	//查询系统参数配置
	function searchSysConfig()
	{
		$('#sysConfigDataGridId').datagrid('load', {
		    code: $("#codeSearch").val(),
		    name: $("#nameSearch").val()
		}); 
		$('#searchDiv').dialog('close');
	}
	
</script>

</head>
<body>
    <div data-options="region:'center', split:true">
	    <div>
	    	<table style="width: 1200px; height:30px;" class="spe_table">
	    		<tr>
	    			<td style="text-align: left;">
	    				<div id="addBtn" class="button_change red center">新增</div>
	    				<div id="modifyBtn" class="button_change red center">修改</div>
	    				<div id="deleteBtn" class="button_change red center">禁用</div>
	    				<div id="openSearchBtn" class="button_change red center">查找</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
		<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
	    	<table id="sysConfigDataGridId">  
			    <thead>  
			        <tr>  
			        	<th data-options="field:'id'" hidden="true" width="100px">id</th>
			            <th data-options="field:'code'" width="100px">编码</th>
			            <th data-options="field:'name'" width="100px">名称</th>
			            <th data-options="field:'value'" width="100px">值</th>
			            <th data-options="field:'description'" width="100px">描述</th>
			            <th data-options="field:'isUsed'" formatter="formatUsed" width="100px">是否启用</th>
			        </tr>  
			    </thead> 
			    <tbody></tbody>  
			</table>
		</div>
	</div>
	<!-- 新增或修改弹出层 -->
	<div id="addOrModifyDiv" class="easyui-dialog" title="新增/修改系统参数" style="width:400px;height:425px;"   
	        data-options="iconCls:'icon-add',buttons:'#dlg-buttons',resizable:false,closed:true">   
	    <form id="sysConfigForm" method="post">
	    	<input type="hidden" id="id" name="id" value=""/>
	    	<table cellpadding="5">
	    		<tr>
	    			<td>编码:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="code" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>名称:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="name" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>值:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" id="value" name="value" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>描述:</td>
	    			<td><input class="easyui-validatebox textbox" type="text" name="description"/></td>
	    		</tr>
	    		<tr>
	    			<td>启用:</td>
	    			<td style="text-align: left;"><input type="checkbox" name="isUsed"></input></td>
	    		</tr>
	    	</table>
	    </form>
	    <div id="dlg-buttons" class="dialog-button">
			<a id="saveBtn" href="javascript:void(0)" class="easyui-linkbutton">保存</a>
			<a id="saveCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div> 
	<!-- 查找弹出层 -->
	<div id="searchDiv" class="easyui-dialog" title="查找系统参数" style="width:400px;height:150px;"   
	        data-options="iconCls:'icon-query',buttons:'#search-buttons',resizable:false,closed:true">
	    <form id="searchForm" method="post">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>编码:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" id="codeSearch" name="codeSearch"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>名称:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" id="nameSearch"  name="nameSearch"/>
	    			</td>
	    		</tr>
	    	</table>
	    </form>
		<div id="search-buttons" class="dialog-button">
			<a id="searchBtn" href="javascript:void(0)" class="easyui-linkbutton"">查询</a>
			<a id="searchCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
</body>
</html>