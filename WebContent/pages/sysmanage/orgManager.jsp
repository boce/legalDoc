<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>组织结构管理</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/sysmanage/orgManagerSearch.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/orgManager.css"/>
<script type="text/javascript">

	//extend the 'number' rule   
	$.extend($.fn.validatebox.defaults.rules, {   
	    number: {   
	    	validator: function(value){
	            return /^(0|[1-9][0-9]*)$/.test(value);   
	        },   
	        message: '只能输入整数'  
	    }   
	});
	//当前选择类型
	var currentType;
	$(function(){
		//显示的页面按钮
		displayBtn();
		$('.layout-panel').css('position','absolute');
		//初始化组织机构
		$("#orgTree").tree({
			url:"${pageContext.request.contextPath}/org/getOrgShortReference.do",
			onClick:function(node)
			{
				currentType = node.attributes.nodeType;
				if(currentType == orgNodeType[0])
				{
					getOrgById(node.id);
				}
				else if(currentType == orgNodeType[1]){
					getDistrictById(node.id);
				}
			}
		});
		
		//初始化区域下拉列表
		$('#district').combotree({   
		    url: '${pageContext.request.contextPath}/org/findAllDistrictForTree.do',   
		    required: true,
		    onClick:function(districtNode)
		    {
		    	$('#parentOrganization').combotree({   
				    url: '${pageContext.request.contextPath}/org/findOrgByDistrict.do',   
				    onBeforeLoad:function(node, param)
				    {
				    	param.districtId = districtNode.id;
				    },
				    onLoadSuccess:function(node, nodeData)
				    {
				    	$('#parentOrganization').combotree("clear");
				    }
				});
		    }
		}); 
		
		//初始化区域下拉列表
		$('#parentDistrict').combotree({   
		    url: '${pageContext.request.contextPath}/org/findAllDistrictForTree.do'
		}); 
		
		//初始化合法性审查单位下拉列表
		$("#reviewUnit").combotree({
			editable : true,
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "不能选择地区！");
					return false;
				}
			}
		});
		
		//初始化备案审查单位下拉列表
		$("#recordUnit").combotree({
			editable : true,
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "不能选择地区！");
					return false;
				}
			}
		});
		
		//初始化组织机构类型
		$('#orgType').combobox({
		    url:'${pageContext.request.contextPath}/org/getOrgType.do',
		    valueField:'name',
		    textField:'title',
		    required: true
		});
		
		$("#addOrgBtn").click(function(){
			addOrg();
		});
		$("#addDistrictBtn").click(function(){
			addDistrict();
		});
		$("#saveBtn").click(function(){
			if(currentType == orgNodeType[0])
			{
				saveOrg();
			}
			else if(currentType == orgNodeType[1])
			{
				saveDistrict();
			}
		});
		$("#deleteBtn").click(function(){
			if(currentType == orgNodeType[0])
			{
				deleteOrg();
			}
			else if(currentType == orgNodeType[1])
			{
				deleteDistrict();
			}
			
		});
		$("#openSearchBtn").click(function(){
			openFrame();
		});
	});
	//根据组织机构id查询，成功后将结果赋给表单
	function getOrgById(orgId)
	{
		$('#districtForm').hide();
		$('#orgForm').form('clear');
		if($('#orgForm').is(":hidden"))
		{
			$('#orgForm').show();
		}
		$.ajax({
		   type: "POST",
		   url: "${pageContext.request.contextPath}/org/getOrgById.do",
		   data: {id:orgId},
		   dataType:"json",
		   success: function(data)
		   {
			   $("#orgForm").form('load', {
				   id:data.id,
				   name:data.name,
				   "district.id":data.district.id,
				   "reviewUnit.id":data.reviewUnit.id,
				   "recordUnit.id":data.recordUnit.id,
				   phone:data.phone,
				   address:data.address,
				   orgType:data.orgType,
				   webserviceUrl:data.webserviceUrl,
				   displayOrder:data.displayOrder
				});
			   $("input[name='isUsed']").prop("checked", data.isUsed);
			 	//初始化下拉组织机构列表
				$('#parentOrganization').combotree({   
				    url: '${pageContext.request.contextPath}/org/findOrgByDistrict.do',   
				    onBeforeLoad:function(node, param)
				    {
				    	param.districtId = data.district.id;
				    },
				    onLoadSuccess:function(node, nodeData)
				    {
				    	$('#parentOrganization').combotree("setValue", data.parentOrganization.id);
				    }
				}); 
		   }
		});
	}
	//通过区域id查询
	function getDistrictById(districtId){
		$('#orgForm').hide();
		$('#districtForm').form('clear');
		if($('#districtForm').is(":hidden"))
		{
			$('#districtForm').show();
		}
		$.ajax({
		   type: "POST",
		   url: "${pageContext.request.contextPath}/org/getDistrictById.do",
		   data: {id:districtId},
		   dataType:"json",
		   success: function(data)
		   {
			   var parent = data.parent;
			   $("#districtForm").form('load', {
				   districtId:data.id,
				   districtName:data.name,
				   parentDistrict:(parent?parent.id:"")
				});
			   $("input[name='districtIsUsed']").prop("checked", data.isUsed);
		   }
		});
	}
	
	//新增组织结构
	function addOrg()
	{
		currentType = orgNodeType[0];
		$('#districtForm').hide();
		$('#orgForm').form('clear');
		if($('#orgForm').is(":hidden"))
		{
			$('#orgForm').show();
		}
		$("input[name='isUsed']").prop("checked", true);
	}
	//新增区域
	function addDistrict()
	{
		currentType = orgNodeType[1];
		$('#orgForm').hide();
		$('#districtForm').form('clear');
		if($('#districtForm').is(":hidden"))
		{
			$('#districtForm').show();
		}
		$("input[name='districtIsUsed']").prop("checked", true);
	}
	//保存组织机构
	function saveOrg()
	{
		$('#orgForm').form('submit', {   
		    url:"${pageContext.request.contextPath}/org/saveOrg.do",
		    onSubmit: function(){
		    	if($("#reviewUnit").combotree("getText") == ""){
		    		$("#reviewUnit").combotree("clear");
		    	}
		    	if($("#recordUnit").combotree("getText") == ""){
		    		$("#recordUnit").combotree("clear");
		    	}
		    	var isValid = $(this).form('validate');
				return isValid;
		    }, 
		    success:function(data){   
		        var dataJson = eval("(" + data + ")");
		        if(dataJson.success){
		        	$('#id').val(dataJson.id);
		        	reloadTree();
		        	showMsg("保存成功！");
		        }else{
		        	showMsg("保存失败！");
		        }
		    }
		});  
	}
	//保存区域
	function saveDistrict()
	{
		if(!$('#districtForm').form('validate'))
		{
			return;
		}
		var param = {
			id:$("#districtId").val(),
			name:$("#districtName").val(),
			"parent.id":$("#parentDistrict").combotree("getValue"),
			isUsed:$("input[name='districtIsUsed']").prop("checked")
		};
		$.ajax({
		   type: "POST",
		   url: "${pageContext.request.contextPath}/org/saveDistrict.do",
		   data: param,
		   dataType:"json",
		   success: function(data)
		   {
			   if(data.success)
			   {
				   $('#districtId').val(data.districtId);
				   reloadTree();
				   showMsg("保存成功！");
			   }
			   else{
				   showMsg("保存失败！");
			   }
		   }
		});
	}
	//删除
	function deleteOrg()
	{
		var orgId = $("#id").val();
		if(orgId == "")
		{
			showMsg("请先选择一个组织机构！");
			return;
		}
		if(confirm("确认禁用？"))
		{
			$.ajax({
				   type: "POST",
				   url: "${pageContext.request.contextPath}/org/deleteOrgById.do",
				   data: {orgId:orgId},
				   dataType:"json",
				   success: function(data)
				   {
					   if(data.success)
					   {
						   showMsg("禁用成功！");
						   $('#orgForm').form('clear');
						   $('#orgForm').hide();
					   }
				   }
			});
		}
		
	}
	//禁用区域
	function deleteDistrict()
	{
		var districtId = $("#districtId").val();
		if(districtId == "")
		{
			showMsg("请先选择一个区域！");
			return;
		}
		if(confirm("确认禁用？"))
		{
			$.ajax({
				   type: "POST",
				   url: "${pageContext.request.contextPath}/org/deleteDistrictById.do",
				   data: {districtId: districtId},
				   dataType:"json",
				   success: function(data)
				   {
					   if(data.success)
					   {
						   showMsg("禁用成功！");
						   $('#districtForm').form('clear');
						   $('#districtForm').hide();
					   }
				   }
			});
		}
		
	}
	function reloadTree()
	{
		$("#orgTree").tree({
			url:"${pageContext.request.contextPath}/org/getOrgShortReference.do",
		});
	}
</script>

</head>
<body>
	    <div>
	    	<table class="table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div id="addOrgBtn" class="button_change red center">新增机构</div>
	    				<div id="addDistrictBtn" class="button_change red center">新增区域</div>
	    				<div id="saveBtn" class="button_change red center">保存</div>
	    				<div id="deleteBtn" class="button_change red center">禁用</div>
	    				<div id="openSearchBtn" class="button_change red center">查找</div>
	    				<div id="upToBtn" class="button_change red center">上传</div>
	    				<div id="downToBtn" class="button_change red center">下发</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
    	<div>
	    	<div class="easyui-layout cc" >
   				<div data-options="region:'west',split:true,  width:200 " title="组织机构">
					<div class="easyui-panel" data-options="padding:5, border: 0"  >
						<ul id="orgTree"></ul>
					</div>
				</div>
				<div data-options="region:'center',title:'详细信息'">
					<form id="orgForm" class="formHide" method="post">
				    	<table cellpadding="5" border="0">
				    		<tr>
				    			<td>单位名称:</td>
				    			<td>
				    				<input type="hidden" id="id" name="id" value=""/>
				    				<input class="easyui-validatebox textbox" type="text" name="name" data-options="required:true"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>区域:</td>
				    			<td>
				    				<input id="district"  name="district.id"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>上级单位:</td>
				    			<td>
				    				<input id="parentOrganization"  name="parentOrganization.id"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>合法性审查单位:</td>
				    			<td>
				    				<input id="reviewUnit"  name="reviewUnit.id"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>备案审查单位:</td>
				    			<td>
				    				<input id="recordUnit"  name="recordUnit.id"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>联系电话:</td>
				    			<td><input class="easyui-validatebox textbox" type="text" name="phone" data-options="required:true"/></td>
				    		</tr>
				    		<tr>
				    			<td>启用:</td>
				    			<td style="text-align: left;"><input type="checkbox" name="isUsed"></input></td>
				    		</tr>
				    		<tr>
				    			<td>地址:</td>
				    			<td>
				    				<input class="easyui-validatebox textbox" type="text" name="address" data-options="required:true"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>组织结构类型:</td>
				    			<td>
				    				<input id="orgType" name="orgType"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>服务地址:</td>
				    			<td>
				    				<input class="easyui-validatebox textbox" type="text" name="webserviceUrl"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>顺序号:</td>
				    			<td>
				    				<input class="easyui-validatebox textbox" type="text" name="displayOrder" data-options="validType:'number'"/>
				    			</td>
				    		</tr>
				    	</table>
				    </form>
				    <form id="districtForm" class="formHide" method="post">
				    	<table cellpadding="5">
				    		<tr>
				    			<td>区域名称:</td>
				    			<td>
				    				<input type="hidden" id="districtId" name="districtId" value=""/>
				    				<input class="easyui-validatebox textbox" type="text" id="districtName" name="districtName" data-options="required:true"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>上级区域:</td>
				    			<td>
				    				<input id="parentDistrict"  name="parentDistrict"/>
				    			</td>
				    		</tr>
				    		<tr>
				    			<td>启用:</td>
				    			<td style="text-align: left;"><input type="checkbox" id="districtIsUsed" name="districtIsUsed"></input></td>
				    		</tr>
				    	</table>
				    </form>
				</div>
			</div>
		</div>
	
</body>
</html>