<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>用户管理</title>
<%@include file="/pages/share/base.jsp"%>
<style type="text/css">
	#orgForm{display: none;}
	.textbox{
        height:20px;
        margin:0;
        padding:0 2px;
        box-sizing:content-box;
    }
    table td{border: 0px;}
    .layout-panel{
    	position:absolute;
    }
</style>
<script type="text/javascript">

	//extend the 'equals' rule   
	$.extend($.fn.validatebox.defaults.rules, {   
	    equals: {   
	        validator: function(value,param){   
	            return value == $(param[0]).val();   
	        },   
	        message: '两次输入密码不一致'  
	    }   
	});

	$(function(){
		
		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('#userDataGridId').datagrid({   
		    url:'${pageContext.request.contextPath}/user/getUserRef.do'
		}); 
		//初始化下拉组织机构列表
		$('#organization').combotree({   
		    url: '${pageContext.request.contextPath}/org/getOrgShortReference.do',   
		    required: true,
		    onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "不能选择地区！");
					return false;
				}
			}
		}); 
		//初始化查询下拉组织机构列表
		$('#orgSearch').combotree({   
		    url: '${pageContext.request.contextPath}/org/getOrgShortReference.do',
		    editable:true,
		    onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "不能选择地区！");
					return false;
				}
			}
		}); 
		$("#addBtn").click(function(){
			add();
		});
		$("#modifyBtn").click(function(){
			modify();
		});
		$("#deleteBtn").click(function(){
			deleteUser();
		});
		$("#openSearchBtn").click(function(){
			openSearch();
		});
		$("#openAuthBtn").click(function(){
			openAuthorize();
		});
		$("#saveBtn").click(function(){
			save();
		});
		$("#saveCloseBtn").click(function(){
			$('#addOrModifyDiv').dialog('close');
		});
		$("#searchBtn").click(function(){
			searchUser();
		});
		$("#searchCloseBtn").click(function(){
			$('#searchDiv').dialog('close');
		});
		$("#addRoleBtn").click(function(){
			addRole();
		});
		$("#removeRoleBtn").click(function(){
			removeRole();
		});
		$("#authBtn").click(function(){
			authorize();
		});
		$("#authCloseBtn").click(function(){
			$('#roleAuthDiv').dialog('close');
		});
	});
	
	function formatOrg(value)
	{
		return value.text;
	}
	
	function formatIncharge(value)
	{
		return (!!value) ? "是" : "否";
	}
	
	function formatUsed(value)
	{
		return (!!value) ? "是" : "否";
	}
	
	function formatRole(value, rows)
	{
		return "<a href='#1' onclick='getRoleRef(\""+rows.id+"\")'>查看</a>";
	}
	//查看用户角色
	function getRoleRef(userId)
	{
		$('#roleDiv').dialog('open');
		$('#roleGridId').datagrid({   
		    url:'${pageContext.request.contextPath}/user/getRoleRef.do',
		    queryParams:{userId:userId}
		}); 
	}
	//打开新增窗口
	function add()
	{
		$("#userForm").form("clear");
		$("#password").prop("disabled", false);
		$("#rpwd").prop("disabled", false);
		$("#isUsed").prop("checked", true);
		$('#addOrModifyDiv').dialog('open');
	}
	//保存用户
	function save()
	{
		$('#userForm').form('submit', {   
		    url:"${pageContext.request.contextPath}/user/saveOrUpdate.do",
		    onSubmit: function(){   
		    	var isValid = $(this).form('validate');
				return isValid;
		    },   
		    success:function(data){   
		        var dataJson = eval("(" + data + ")");
		        if(dataJson.success)
		        {
		        	$('#userForm').form('clear');
		        	$('#addOrModifyDiv').dialog('close');
		        	$('#userDataGridId').datagrid('reload');
		        	showMsg("保存成功！");
		        }
		    }
		});  
	}
	//打开修改用户窗口
	function modify()
	{
		$("#userForm").form("clear");
		var selectRow = $('#userDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg("请先选择一行记录！");
			return;
		}
		$("#userForm").form('load', {
		   id:selectRow.id,
		   userName:selectRow.userName,
		   name:selectRow.name,
		   password:selectRow.password,
		   rpwd:selectRow.password,
		   phone:selectRow.phone,
		   mobile:selectRow.mobile,
		   email:selectRow.email,
		   isIncharge:selectRow.isIncharge,
		   isUsed:selectRow.isUsed,
		   "organization.id":selectRow.organization.id
		});
		$("input[name='isIncharge']").prop("checked", selectRow.isIncharge);
		$("input[name='isUsed']").prop("checked", selectRow.isUsed);
		$("#password").prop("disabled", "disabled");
		$("#rpwd").prop("disabled", "disabled");
		if($('#addOrModifyDiv').is(":hidden"))
		{
			$('#addOrModifyDiv').dialog('open');
		}
	}
	
	//假删除用户
	function deleteUser()
	{
		var selectRow = $('#userDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg("请先选择一行记录！");
			return;
		}
		if(confirm("确认禁用？"))
		{
			$.ajax({
				   type: "POST",
				   url: "${pageContext.request.contextPath}/user/deleteUserById.do",
				   data: {userId:selectRow.id},
				   dataType:"json",
				   success: function(data)
				   {
					   if(data.success)
					   {
						   showMsg("禁用成功！");
						   $('#userDataGridId').datagrid('reload');
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
	//查询用户
	function searchUser()
	{
		$('#userDataGridId').datagrid('load', {
		    name: $("#nameSearch").val(),    
		    orgId: $("input[name='orgSearch']").val()
		}); 
		$('#searchDiv').dialog('close');
	}
	
	//打开角色授权窗口
	function openAuthorize()
	{
		var selectRow = $('#userDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg("请选择一行记录");
			return;
		}
		if($('#roleAuthDiv').is(":hidden"))
		{
			$('#roleAuthDiv').dialog('open');
		}
		$('#roleDataGridId').datagrid({   
		    url:'${pageContext.request.contextPath}/role/getAllRole.do'
		}); 
		$('#roleAuthDataGridId').datagrid({   
			url:'${pageContext.request.contextPath}/user/getRoleRef.do',
		    queryParams:{userId:selectRow.id}
		}); 
	}
	//赋予角色
	function authorize()
	{
		var selectRow = $('#userDataGridId').datagrid('getSelected');
		var rows = $("#roleAuthDataGridId").datagrid("getRows");
		var roleIds = [];
		for(var i = 0, len = rows.length; i < len; i++)
		{
			roleIds.push(rows[i].id);
		}
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/user/authorize.do",
			data : {authRoles:roleIds, userId:selectRow.id},
			dataType : "json",
			success : function(data) 
			{
				if(data.success)
				{
					$('#roleAuthDiv').dialog('close');
					showMsg("授权成功");
				}
			},
			error : function(data) {
				showMsg("授权失败");
			}
		});
	}
	
	//添加角色
	function addRole()
	{
		var rows = $('#roleDataGridId').datagrid('getSelections');
		for(var i = 0, len = rows.length; i < len; i++)
        {
			if(!(cotainsRow("roleAuthDataGridId", rows[i])))
			{
				$('#roleAuthDataGridId').datagrid('appendRow', rows[i]);
			}
        }
	}
	//移除角色
	function removeRole()
	{
		var rows = $('#roleAuthDataGridId').datagrid('getSelections');
		var copyRows = [];
		for ( var j= 0, len = rows.length; j < len; j++)
        {
        	copyRows.push(rows[j]);
        }
		for(var i = 0, len = copyRows.length; i < len; i++){    
            var index = $('#roleAuthDataGridId').datagrid('getRowIndex',copyRows[i]);//获取某行的行号
            $('#roleAuthDataGridId').datagrid('deleteRow',index);	//通过行号移除该行
        }
	}
	/**
	* 通过目标表格行数据的id和传入的行数据id比较，如果相等，说明包含
	* gridId 表格id
	* row grid的行对象
	**/
	function cotainsRow(gridId, row)
	{
		var isContains = false;
		var rows = $("#" + gridId).datagrid("getRows");
		for(var i = 0, len = rows.length; i < len; i++)
		{
			if(rows[i].id == row.id)
			{
				isContains = true;
				break;
			}
		}
		return isContains;
	}
</script>

</head>
<body>
    <div data-options="region:'center', split:true">
	    <div>
	    	<table style="width: 1218px; height:30px;" class="spe_table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div id="addBtn" class="button_change red center">新增</div>
	    				<div id="modifyBtn" class="button_change red center">修改</div>
	    				<div id="deleteBtn" class="button_change red center">禁用</div>
	    				<div id="openSearchBtn" class="button_change red center">查找</div>
	    				<div id="openAuthBtn" class="button_change red center">授权</div>
	    				<div id="upToBtn" class="button_change red center">上传</div>
	    				<div id="downToBtn" class="button_change red center">下发</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
    	<div>
    	<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
	    	<table id="userDataGridId" pagination="true" singleSelect="true" rownumbers="true" 
				style="padding:0px; border-bottom-width: 0px; height: 306px">  
			    <thead>  
			        <tr>  
			        	<th data-options="field:'id'" hidden="true" width="100px">id</th>
			            <th data-options="field:'userName'" width="100px">登录名</th>
			            <th data-options="field:'password'" hidden="true" width="100px">密码</th>
			            <th data-options="field:'name'" width="100px">姓名</th>
			            <th data-options="field:'phone'" width="100px">固定电话</th>
			            <th data-options="field:'mobile'" width="100px">移动电话</th>
			            <th data-options="field:'email'" width="100px">邮件地址</th>
			            <th data-options="field:'isIncharge'" formatter="formatIncharge" width="100px">是否负责人</th>
			            <th data-options="field:'isUsed'" formatter="formatUsed" width="100px">是否启用</th>
			            <th data-options="field:'organization'" formatter="formatOrg" width="100px">单位</th>
			            <th data-options="field:'roles'" formatter="formatRole" width="100px">角色</th>
			        </tr>  
			    </thead> 
			    <tbody></tbody>  
			</table>
		</div>
		</div>
	</div>
	<!-- 新增或修改弹出层 -->
	<div id="addOrModifyDiv" class="easyui-dialog" title="新增/修改用户" style="width:400px;height:425px;"   
	        data-options="iconCls:'icon-add',buttons:'#dlg-buttons',resizable:false,closed:true">   
	    <form id="userForm" method="post">
	    	<input type="hidden" id="id" name="id" value=""/>
	    	<table cellpadding="5">
	    		<tr>
	    			<td>登录名:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="userName" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>姓名:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="name" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>密码:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="password" id="password" name="password" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>确认密码:</td>
	    			<td>
	    				<input id="rpwd" name="rpwd" type="password" class="easyui-validatebox  textbox" required="required" validType="equals['#password']" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>固定电话:</td>
	    			<td><input class="easyui-validatebox textbox" type="text" name="phone"/></td>
	    		</tr>
	    		<tr>
	    			<td>移动电话:</td>
	    			<td><input class="easyui-validatebox textbox" type="text" name="mobile"/></td>
	    		</tr>
	    		<tr>
	    			<td>邮件地址:</td>
	    			<td><input class="easyui-validatebox textbox" type="text" name="email" data-options="validType:'email'"/></td>
	    		</tr>
	    		<tr>
	    			<td>负责人:</td>
	    			<td style="text-align: left;"><input type="checkbox" id="isIncharge" name="isIncharge"></input></td>
	    		</tr>
	    		<tr>
	    			<td>启用:</td>
	    			<td style="text-align: left;"><input type="checkbox" id="isUsed" name="isUsed"></input></td>
	    		</tr>
	    		<tr>
	    			<td>单位:</td>
	    			<td>
	    				<input id="organization"  name="organization.id"/>
	    			</td>
	    		</tr>
	    	</table>
	    </form>
	    <div id="dlg-buttons" class="dialog-button">
			<a id="saveBtn" href="javascript:void(0)" class="easyui-linkbutton">保存</a>
			<a id="saveCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div> 
	<!-- 查找弹出层 -->
	<div id="searchDiv" class="easyui-dialog" title="查找用户" style="width:400px;height:180px;"   
	        data-options="iconCls:'icon-query',buttons:'#search-buttons',resizable:false,closed:true">
	    <form id="searchForm" method="post">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>姓名:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" id="nameSearch" name="nameSearch"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>单位:</td>
	    			<td>
	    				<input id="orgSearch"  name="orgSearch"/>
	    			</td>
	    		</tr>
	    	</table>
	    </form>
		<div id="search-buttons" class="dialog-button">
			<a id="searchBtn" href="javascript:void(0)" class="easyui-linkbutton"">查询</a>
			<a id="searchCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
	<!-- 用户角色查看弹出层 -->
	<div id="roleDiv" class="easyui-dialog" title="授予角色" style="width:400px;height:300px;" data-options="resizable:false,closed:true">
	    <table id="roleGridId" pagination="false" singleSelect="true" rownumbers="true"
			style="height: 260px;">  
		    <thead>  
		        <tr>  
		            <th data-options="field:'description'" width="350px">角色名</th>
		        </tr>  
		    </thead> 
		    <tbody></tbody>  
		</table>
	</div>
	<!-- 用户角色赋予弹出层 -->
	<div id="roleAuthDiv" class="easyui-dialog" title="授权" style="width:414px;height:375px;" 
		data-options="resizable:false,buttons:'auth-buttons',closed:true,modal:true">
	    <div class="easyui-layout" style="width:400px;height:300px;">
			<div data-options="region:'west',collapsible:false,split:false" title="所有角色" style="width:180px;">
				<table id="roleDataGridId" data-options="showHeader:false,striped:false" border="0" style="padding:0px; border-bottom-width: 0px; height: 200px">  
				    <thead>  
				        <tr>  
				        	<th data-options="field:'id'" hidden="true">id</th>
				            <th data-options="field:'description'" width="150px">文件名</th>  
				        </tr>  
				    </thead> 
				    <tbody></tbody>
				</table>
			</div>
			<div data-options="region:'east',collapsible:false,split:false" title="已授权角色" style="width:180px;">
				<table id="roleAuthDataGridId" data-options="showHeader:false,striped:false,idField:'id'" border="0" style="padding:0px; border-bottom-width: 0px; height: 200px">  
				    <thead>  
				        <tr>  
				        	<th data-options="field:'id'" hidden="true">id</th>
				            <th data-options="field:'description'" width="150px">文件名</th>  
				        </tr>  
				    </thead> 
				    <tbody></tbody>
				</table>
			</div>
			<div data-options="region:'center'" style="vertical-align: middle;text-align: center;">
				<div style="margin-top: 120px;"><input id="addRoleBtn" type="button" value="&gt;&gt;"/></div>
				<div><input id="removeRoleBtn" type="button" value="&lt;&lt;"/></div>
			</div>
		</div>
	    <div id="auth-buttons" class="dialog-button">
			<a id="authBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="authCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">取消</a>
		</div>
	</div>
</body>
</html>