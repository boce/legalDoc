<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>角色管理</title>
<%@include file="/pages/share/base.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/roleManager.css"/>
<script type="text/javascript">

	//表示打开数据授权窗口后第一次加载区域和组织机构
	var orgInit = true;
	$(function(){
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('#roleDataGridId').datagrid({   
		    url:'${pageContext.request.contextPath}/role/getRoleRef.do'
		}); 
		$("#addBtn").click(function(){
			add();
		});
		$("#modifyBtn").click(function(){
			modify();
		});
		$("#deleteBtn").click(function(){
			deleteRole();
		});
		$("#openSearchBtn").click(function(){
			openSearch();
		});
		$("#openOprateAuthBtn").click(function(){
			openOprateAuth();
		});
		$("#openDataAuthBtn").click(function(){
			openDataAuth();
		});
		$("#saveBtn").click(function(){
			save();
		});
		$("#saveCloseBtn").click(function(){
			$('#addOrModifyDiv').dialog('close');
		});
		$("#searchBtn").click(function(){
			searchRole();
		});
		$("#searchCloseBtn").click(function(){
			$('#searchDiv').dialog('close');
		});
		$("#addOptAuthBtn").click(function(){
			addOptAuth();
		});
		$("#removeOptAuthBtn").click(function(){
			removeOptAuth();
		});
		$("#optAuthBtn").click(function(){
			authOpt();
		});
		$("#optAuthCloseBtn").click(function(){
			$('#optAuthDiv').dialog('close');
		});
		$("#dataAuthBtn").click(function(){
			authData();
		});
		$("#dataAuthCloseBtn").click(function(){
			$('#dataAuthDiv').dialog('close');
		});
	});
	//打开新增窗口
	function add()
	{
		$("#roleForm").form("clear");
		$("#isUsed").prop("checked", true);
		$('#addOrModifyDiv').dialog('open');
	}
	//保存角色
	function save()
	{
		$('#roleForm').form('submit', {   
		    url:"${pageContext.request.contextPath}/role/saveOrUpdate.do",
		    onSubmit: function(){   
		    	var isValid = $(this).form('validate');
				return isValid;
		    },   
		    success:function(data){   
		        var dataJson = eval("(" + data + ")");
		        if(dataJson.success)
		        {
		        	$('#roleForm').form('clear');
		        	$('#addOrModifyDiv').dialog('close');
		        	$('#roleDataGridId').datagrid('reload');
		        	showMsg("保存成功！");
		        }
		    }
		});  
	}
	//打开修改角色窗口
	function modify()
	{
		$("#roleForm").form("clear");
		var selectRow = $('#roleDataGridId').datagrid('getSelected');
		if(!isEnable(selectRow))
		{
			return;
		}
		$("#roleForm").form('load', {
		   id:selectRow.id,
		   name:selectRow.name,
		   description:selectRow.description,
		   isUsed:selectRow.isUsed
		});
		$("input[name='isUsed']").prop("checked", selectRow.isUsed);
		if($('#addOrModifyDiv').is(":hidden"))
		{
			$('#addOrModifyDiv').dialog('open');
		}
	}
	
	//假删除角色
	function deleteRole()
	{
		var selectRow = $('#roleDataGridId').datagrid('getSelected');
		if(!isEnable(selectRow))
		{
			return;
		}
		if(confirm("确认禁用？"))
		{
			$.ajax({
				   type: "POST",
				   url: "${pageContext.request.contextPath}/role/deleteRoleById.do",
				   data: {roleId:selectRow.id},
				   dataType:"json",
				   success: function(data)
				   {
					   if(data.success)
					   {
						   $('#roleDataGridId').datagrid('reload');
					   }
					   else
					   {
						   showMsg('禁用失败！');
					   }
											   
				   }
			});
		}
		
	}
	//判断选中的行是否可操作
	function isEnable(selectRow)
	{
		if(selectRow == null)
		{
			showMsg('请先选择一行记录！');
			return false;
		}
		//ROLE_USER是系统默认登录角色 不能修改
		if(selectRow.name == "ROLE_USER")
		{
			showMsg("系统默认角色不能修改");
			return false;
		}
		return true;
	}
	//打开查询窗口
	function openSearch()
	{
		if($('#searchDiv').is(":hidden"))
		{
			$('#searchDiv').dialog('open');
		}
	}
	//查询角色
	function searchRole()
	{
		$('#roleDataGridId').datagrid('load', {
		    description: $("#descriptionSearch").val(),    
		}); 
		$('#searchDiv').dialog('close');
	}
	//格式化表格显示是否启用
	function formatUsed(value)
	{
		return (!!value) ? "是" : "否";
	}
	//打开操作授权窗口
	function openOprateAuth()
	{
		var selectRow = $('#roleDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg('请先选择一行记录！');
			return;
		}
		$('#optAuthDiv').dialog('open');
		$("#menuTree").tree({
			url:"${pageContext.request.contextPath}/role/getAllMenu.do",
			checkbox:true
		});
		$("#authMenuTree").tree({
			url:"${pageContext.request.contextPath}/role/getAllAuthMenu.do",
			checkbox:true,
			onBeforeLoad:function(node, param)
			{
				param.roleId = selectRow.id;
			}
		});
	}
	//节点类型
	var nodeType = ["menu", "page", "pageSource"];
	//添加操作授权
	function addOptAuth()
	{
		var menuSelectedNodes = $('#menuTree').tree('getChecked');
        for(var i=0; i<menuSelectedNodes.length; i++){
        	addOne(menuSelectedNodes[i]);
        }
	}
	function addOne(selectedNode)
	{
		var nodeList = getAllNodes($("#authMenuTree"));
		//如果节点存在，不作操作
		if(isNodeExist(nodeList, selectedNode))
		{
			return;
		}
		var newSelectedNode = jQuery.extend(true, {}, selectedNode);
		if(selectedNode.attributes.nodeType == nodeType[0])
		{
			appendNode(nodeList, null, newSelectedNode);
		}
		else if(selectedNode.attributes.nodeType == nodeType[1])
		{
			var menuNode = $("#menuTree").tree("getParent", selectedNode.target);
			if(isNodeExist(nodeList, menuNode))
			{
				appendNode(nodeList, menuNode, newSelectedNode);
			}
			else
			{
				var newMenuNode = jQuery.extend(true, {}, menuNode);
				newMenuNode.children = [newSelectedNode];
				appendNode(nodeList, null, newMenuNode);
			}
		}
		else if(selectedNode.attributes.nodeType == nodeType[2])
		{
			var pageNode = $("#menuTree").tree("getParent", selectedNode.target);
			var menuNode = $("#menuTree").tree("getParent", pageNode.target);
			if(isNodeExist(nodeList, pageNode) && isNodeExist(nodeList, menuNode))
			{
				appendNode(nodeList, pageNode, newSelectedNode);
			}
			else if(!isNodeExist(nodeList, pageNode) && isNodeExist(nodeList, menuNode))
			{
				var newPageNode = jQuery.extend(true, {}, pageNode);
				newPageNode.children = [newSelectedNode];
				appendNode(nodeList, menuNode, newPageNode);
			}
			else if(!isNodeExist(nodeList, pageNode) && !isNodeExist(nodeList, menuNode))
			{
				var newPageNode = jQuery.extend(true, {}, pageNode);
				var newMenuNode = jQuery.extend(true, {}, menuNode);
				newPageNode.children = [newSelectedNode];
				newMenuNode.children = [newPageNode];
				appendNode(nodeList, null, newMenuNode);
			}
		}
	}
	//添加授权节点
	function appendNode(nodeList, targetNode, selectedNode)
	{
		selectedNode.checked = false;
		var childs = selectedNode.children;
		if(childs && childs.length > 0){
			setNoChecked(childs);
		}
		if(targetNode == null)
		{
			$("#authMenuTree").tree("append",{
				parent : null,
				data : [selectedNode]
			});
		}
		else
		{
			for (var i=0; i<nodeList.length; i++)
			{
				var node = nodeList[i];
				if (node.id == targetNode.id && node.attributes.nodeType == targetNode.attributes.nodeType)
				{
					$("#authMenuTree").tree("append",{
						parent : node.target,
						data : [selectedNode]
					});
				}
			}
		}
	}
	//把传入节点的checked属性设置为false
	function setNoChecked(nodes){
		for (var i=0; i<nodes.length; i++){
			var node = nodes[i];
			node.checked = false;
			var childs = node.children;
			if(childs && childs.length > 0){
				setNoChecked(childs);
			}
		}
	}
	//判断节点是否存在
	function isNodeExist(nodeList, selectedNode)
	{
		var isExist = false;
		if (nodeList && nodeList.length > 0)
		{
			for (var i=0; i<nodeList.length; i++)
			{
				node = nodeList[i];
				if (node.id == selectedNode.id && node.attributes.nodeType == selectedNode.attributes.nodeType)
				{
					isExist = true;
					break;
				}
			}
		}
		return isExist;
	}
	
	//获取树下的所有节点
	function getAllNodes(jqTree) {  
        var roots = jqTree.tree("getRoots");  
        var allNodeList = getChildNodeList(jqTree, roots);  
        return allNodeList;
    }  
      
    /**  
     * 定义获取easyui tree的子节点的递归算法  
     */  
    function getChildNodeList(jqTree, nodes) {  
        var childNodeList = [];  
        if (nodes && nodes.length>0) {             
            var node = null;  
            for (var i=0; i<nodes.length; i++) {  
                node = nodes[i];  
                childNodeList.push(node);  
                if (!jqTree.tree("isLeaf", node.target)) {  
                    var children = jqTree.tree("getChildren", node.target);  
                    childNodeList = childNodeList.concat(children);  
                }  
            }  
        }  
        return childNodeList;  
    }
    //移除授权
    function removeOptAuth()
    {
    	var selectedNodes = $('#authMenuTree').tree('getChecked');
        for(var i=0; i<selectedNodes.length; i++){
        	$("#authMenuTree").tree("remove", selectedNodes[i].target);
        }
    }
    //操作授权
    function authOpt()
    {
    	var selectRow = $('#roleDataGridId').datagrid('getSelected');
    	var roots = $("#authMenuTree").tree("getRoots");
    	var nodeData = getSaveOptData(roots);
    	$.ajax({
			   type: "POST",
			   url: "${pageContext.request.contextPath}/role/optAuth.do",
			   data: {nodes:JSON.stringify(nodeData), roleId:selectRow.id},
			   dataType:"json",
			   success: function(data)
			   {
				   if(data.success)
				   {
					   showMsg("操作授权成功！");
				   }
				   else
				   {
					   showMsg("操作授权失败！");
				   }
										   
			   }
		});
    }
    
    //组装要保存的操作授权数据[[menuId,pageId,pageSourceId]]
    function getSaveOptData(nodeList)
    {
    	var nodeData = [];
    	for(var i=0, len=nodeList.length; i < len; i++ )
    	{
    		var node = nodeList[i];
    		var menuChildren = node.children;
    		if(menuChildren && menuChildren.length > 0)
    		{
    			for(var j=0, plen=menuChildren.length; j<plen; j++)
    			{
    				var pageNode = menuChildren[j];
    				var pageChildren = pageNode.children;
    				if(pageChildren && pageChildren.length > 0)
    	    		{
    					for(var k=0, pslen=pageChildren.length; k<pslen; k++)
    	    			{
    						var pageSourceNode = pageChildren[k];
    						nodeData.push([node.id, pageNode.id, pageSourceNode.id]);
    	    			}
    	    		}
    				else
    				{
    					nodeData.push([node.id, pageNode.id, null]);
    				}
    			}
    		}
    		else
    		{
    			nodeData.push([node.id, null, null]);
    		}
    	}
    	return nodeData;
    }
    
    //打开数据授权窗口
    function openDataAuth()
    {
    	var selectRow = $('#roleDataGridId').datagrid('getSelected');
		if(selectRow == null)
		{
			showMsg('请先选择一行记录！');
			return;
		}
		$('#dataAuthDiv').dialog('open');
		orgInit = true;
		//加载所有区域和组织机构
		$("#orgTree").tree({
			url:"${pageContext.request.contextPath}/org/getOrgShortReference.do",
			checkbox:true,
			cascadeCheck:false,
			onCheck:function(node, checked){
				if(!orgInit)
				{
					var isLeaf = $("#orgTree").tree("isLeaf", node.target);
					if(!isLeaf)
					{
						var childNodes = $("#orgTree").tree("getChildren", node.target);
						if(childNodes)
						{
							var method = node.checked ? "check" : "uncheck";
							for(var i=0, len=childNodes.length; i<len; i++)
							{
								$("#orgTree").tree(method, childNodes[i].target);
							}
						}
					}
				}
			},
			onLoadSuccess:function(node, data)
			{
				var roots = $("#orgTree").tree("getRoots");
				//获得所有授权组织机构
				$.ajax({
					   type: "POST",
					   url: "${pageContext.request.contextPath}/role/getAllAuthOrg.do",
					   data: {roleId:selectRow.id},
					   dataType:"json",
					   success: function(data)
					   {
						   if(data != null && data.length > 0){
							   initOrgTreeChecked(roots, data);
						   }
						   orgInit = false;
					   }
				});
			}
		});
    }
    function initOrgTreeChecked(roots, data)
   	{
    	for(var i=0, len=roots.length; i<len; i++)
    	{
    		if(roots[i].attributes.nodeType == orgNodeType[0] && isContainsNode(data, roots[i]))
    		{
    			$("#orgTree").tree("check", roots[i].target);
    		}
    		var isLeaf = $("#orgTree").tree("isLeaf",  roots[i].target);
			if(!isLeaf)
			{
				var childNodes = $("#orgTree").tree("getChildren", roots[i].target);
				if(childNodes)
				{
					initOrgTreeChecked(childNodes, data);
				}
			}
    	}
   	}
    function isContainsNode(data, node)
   	{
    	var flag = false;
    	for(var i=0, len=data.length; i<len; i++)
    	{
    		if(data[i].id == node.id)
    		{
    			flag = true;
    		}
    	}
    	return flag;
   	}
    //数据授权
    function authData()
    {
    	var selectRow = $('#roleDataGridId').datagrid('getSelected');
    	var orgs = [];//存储选中组织机构编号
    	var nodes = $('#orgTree').tree('getChecked');
    	for(var i=0, len=nodes.length; i<len; i++)
    	{
    		if(nodes[i].attributes.nodeType == orgNodeType[0])
    		{
    			orgs.push(nodes[i].id);
    		}
    	}
    	$.ajax({
			   type: "POST",
			   url: "${pageContext.request.contextPath}/role/dataAuth.do",
			   data: {orgs:orgs, roleId:selectRow.id},
			   dataType:"json",
			   success: function(data)
			   {
				   if(data.success)
				   {
					   showMsg("数据授权成功！");
				   }
				   else
				   {
					   showMsg("数据授权失败！");
				   }
			   }
		});
    }
</script>
</head>
<body>
	    <div>
	    	<table  class="table">
	    		<tr>
	    			<td style="text-align: left;">
	    				<div id="addBtn" class="button_change red center">新增</div>
	    				<div id="modifyBtn" class="button_change red center">修改</div>
	    				<div id="deleteBtn" class="button_change red center">禁用</div>
	    				<div id="openSearchBtn" class="button_change red center">查找</div>
	    				<div id="openOprateAuthBtn" class="button_change red center">操作授权</div>
	    				<div id="openDataAuthBtn" class="button_change red center">数据授权</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
   		<div id="dataDivWindow"  class="dataDivWindow" >
	    	<table id="roleDataGridId" data-options="pagination:true, singleSelect:true, rownumbers:true, padding:0, height: 306">  
			    <thead>  
			        <tr>  
			        	<th data-options="field:'id', hidden:true, width:100" >id</th>
			            <th data-options="field:'name', width:100" >名称</th>
			            <th data-options="field:'description', width:100" >描述</th>
			            <th data-options="field:'isUsed', formatter:formatUsed, width:100" >是否启用</th>
			        </tr>  
			    </thead> 
			    <tbody></tbody>  
			</table>
		</div>
	<!-- 新增或修改弹出层 -->
	<div id="addOrModifyDiv" class="easyui-dialog" title="新增/修改角色"   
	        data-options="iconCls:'icon-add',buttons:'#dlg-buttons',resizable:false,closed:true, width:400, height:300">   
	    <form id="roleForm" method="post">
	    	<input type="hidden" id="id" name="id" value=""/>
	    	<table cellpadding="5">
	    		<tr>
	    			<td>名称:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="name" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>描述:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" name="description" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>启用:</td>
	    			<td style="text-align: left;"><input type="checkbox" id="isUsed" name="isUsed"></input></td>
	    		</tr>
	    	</table>
	    </form>
	    <div id="dlg-buttons" class="dialog-button">
			<a id="saveBtn" href="javascript:void(0)" class="easyui-linkbutton">保存</a>
			<a id="saveCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div> 
	<!-- 查找弹出层 -->
	<div id="searchDiv" class="easyui-dialog" title="查找角色"   
	        data-options="iconCls:'icon-query',buttons:'#search-buttons',resizable:false,closed:true, width:400, height:150">
	    <form id="searchForm" method="post">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>描述:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="text" id="descriptionSearch" name="descriptionSearch"/>
	    			</td>
	    		</tr>
	    	</table>
	    </form>
		<div id="search-buttons" class="dialog-button">
			<a id="searchBtn" href="javascript:void(0)" class="easyui-linkbutton">查询</a>
			<a id="searchCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
	<!-- 操作授权弹出层 -->
	<div id="optAuthDiv" class="easyui-dialog" title="操作授权" style="width:414px;height:375px;" 
		data-options="resizable:false,buttons:'optAuth-buttons',closed:true,modal:true">
	    <div class="easyui-layout" style="width:400px;height:300px;">
			<div data-options="region:'west',collapsible:false,split:false" title="页面资源" style="width:180px;">
				<div class="easyui-panel" style="padding:5px; border: 0px;">
					<ul id="menuTree" class="menuTree" ></ul>
				</div>
			</div>
			<div data-options="region:'east',collapsible:false,split:false" title="已授权页面资源" style="width:180px;">
				<div class="easyui-panel" style="padding:5px; border: 0px;">
					<ul id="authMenuTree" class="authMenuTree" ></ul>
				</div>
			</div>
			<div data-options="region:'center'" style="vertical-align: middle;text-align: center;">
				<div style="margin-top: 120px;"><input id="addOptAuthBtn" type="button" value="&gt;&gt;"/></div>
				<div><input id="removeOptAuthBtn" type="button" value="&lt;&lt;"/></div>
			</div>
		</div>
	    <div id="optAuth-buttons" class="dialog-button">
			<a id="optAuthBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="optAuthCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">取消</a>
		</div>
	</div>
	<!-- 数据授权弹出层 -->
	<div id="dataAuthDiv" class="easyui-dialog" title="数据授权" style="width:400px;height:375px;" 
		data-options="resizable:false,buttons:'dataAuth-buttons',closed:true,modal:true">
	    <div class="easyui-layout" style="width:385px;height:300px;">
			<div data-options="region:'center',collapsible:false,split:false" title="组织机构" style="width:180px;">
				<div class="easyui-panel" style="padding:5px; border: 0px;height: 270px;">
					<ul id="orgTree" ></ul>
				</div>
			</div>
		</div>
	    <div id="dataAuth-buttons" class="dialog-button">
			<a id="dataAuthBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="dataAuthCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">取消</a>
		</div>
	</div>
</body>
</html>