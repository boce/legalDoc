<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>文件清理</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/cleanup.css"/>
<script type="text/javascript">
	var fileStatus = {"VALID":"生效","INVALID":"失效","MODIFY":"修订","REVOKE":"撤销","ABOLISH":"废止"};
	$(function(){
		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'DRAFT');
		}
		
		$('#addBtn').click(function(){
			cleanPage();
		});
		
		$('#saveBtn').click(function(){ 	//保存送审稿审查
			var validateFlag = $("#formId").form('validate');
			if (validateFlag) {
				var cleanId = $('#cleanId').val();
				var cUnit = $("#cleanupUnitId").combotree('getValue');
				var cUnitL = $("#cleanupLeaderId").combobox('getValue');
				var cUnitC = $("#cleanupClerkId").combobox('getValue');
				
				var aUnit = $("#appUnitId").combotree('getValue');
				var aUnitL = $("#appUnitLeaderId").combobox('getValue');
				var aUnitC = $("#appUnitClerkId").combobox('getValue');
				
				var mLeader = $("#mainLeadersId").val();
				var cDate = $("#cleanupDateId").datetimebox('getValue');
				
				//子文件ID值
				var rows = $('#cLineList').datagrid('getRows');
				var arrIDs = [];
				$.each(rows, function(index){
					var id = rows[index].id;
					id = id.replace('c', '');
					arrIDs.push(id);
				}); 
				
				var lineIds = arrIDs.toString();
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/cleanup/saveCleanup.do",
					  data : {id : cleanId, 'cleanupUnit.id' : cUnit, 'cleanupUnitLeader.id' : cUnitL, 
						  	  'cleanupUnitClerk.id' : cUnitC, 'approvalUnit.id' : aUnit, 'approvalUnitLeader.id' : aUnitL,
						  	  'approvalUnitClerk.id' : aUnitC, 'mainLeaders' : mLeader, 'cleanupDate' : cDate, fileIds : lineIds},
					  dataType : 'json',
					  success : function(data)
					  {
						 if (data.msg == "success") {
							 $('#cleanId').val(data.cleanupId);
							 showMsg("保存成功!");
						 } else {
							 showMsg("保存失败!");
						 }
					  },
					  error : function(data)
					  {
						  showMsg("保存出错,请重试!");
					  }
					});
			} else {
				showMsg("请填上表单必填项!");
			}
			
		});
		
		$('#searchBtn').click(function(){
			$('#cleanId').val('');
			location.href = "${pageContext.request.contextPath}/cleanup/cleanupList.wf";
		});
		
		$('#exportBtn').click(function(){ 
			var cleanId = $('#cleanId').val();
			if (null != cleanId && "" != cleanId) {
				$("#downloadId").attr("action", "${pageContext.request.contextPath}/cleanup/download.do?cleanId=" + cleanId);
				$("#downloadId").submit();
			} else {
				showMsg("请选择清理文件！");
			}
		});
		
		
		$('#printBtn').click(function(){ 
			var cleanId = $('#cleanId').val();
			if (null != cleanId && "" != cleanId) {
				$.ajax({
					  type : "post",
					  url : "${pageContext.request.contextPath}/cleanup/print.do",
					  data : {id : cleanId},
					  dataType : "html",
					  success : function(data)
					  {
						  lodopPrint(data);
					  },
					  error:function(data)
					  {
						  showMsg("打印失败!");
					  }
					});
			} else {
				showMsg("请选择清理文件！");
			}
		});
		
		
		$('#deleteBtn').click(function(){ 
			var cleanId = $('#cleanId').val();
			if (null != cleanId && "" != cleanId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/cleanup/delete.do",
					  data : {id : cleanId},
					  dataType : 'json',
					  success : function(data)
					  {
						  if (data.msg == 'success') {
		                		showMsg("删除成功!");
		                		cleanPage();	//清空页面
		                	} else {
		                		showMsg("删除失败!");
		                	}
					  },
					  error : function(data)
					  {
						  showMsg("删除出错,请重试!");
					  }
					});
			} else {
				showMsg("请选择规清理文件！");
			}
		});
		
		$('#cleanBtn').click(function(){ 
			$('#fileList').datagrid({
				url : '${pageContext.request.contextPath}/cleanup/searchNorFiles.do',
				queryParams : {}
			});
			$('#searchNorFile').window('open');
		});
		
		
		$('#searchFileBtn').click(function(){
			var lgs = $('#legalBasis').val();
			var name = $('#filename').val();
			var draftUnit = $('#draftUnit').combotree('getValue');
			var bdate = $('#begDate').datetimebox('getValue');
			var edate = $('#endDate').datetimebox('getValue');
			$('#fileList').datagrid({
				url : '${pageContext.request.contextPath}/cleanup/searchNorFiles.do',
				queryParams : {legalBasis : lgs, 'drtUnit.id' : draftUnit, name : name, startDate : bdate, endDate : edate}
			});
			$('#fileList').datagrid('reload');
		});
		
		$('#cancel').click(function(){
			$('#searchNorFile').window('close');
		});
		
		//确认时保存子文件，同时在页面显示对应的记录
		$('#confirm').click(function(){
			var cleanID = $('#cleanId').val();
			var checkedItems = $('#fileList').datagrid('getChecked');
			var ids = [];
			$.each(checkedItems, function(index, item) {
				ids.push(item.id);
			}); 
			var fileIds = ids.toString();
			//如果父表存在,进行保存
			if (cleanID != null && cleanID != '') {	
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/cleanup/saveLineFiles.do",
					  data : {fileIds : fileIds, parentId : cleanID},
					  dataType : 'json',
					  success : function(data)
					  {
						  $.each(checkedItems, function(index, item) {
								$('#cLineList').datagrid('insertRow',{
									index: 0,	
									row: {
										'id' : item.id + 'c',
										'name' : item.name,
										'decisionUnit.text' : item.decUnit.text,
										'publishNo' : item.publishNo,
										'publishDate' : item.publishDate,
										'status' : item.status,
										'invalidReason' : item.invalidReason
									}
								});
							}); 
					  },
					  error : function(data)
					  {
						  showMsg("清理子文件保存报错!");
					  }
				});
			} else {
				var rows = $('#cLineList').datagrid('getRows');
				
				$.each(checkedItems, function(index, item) {
					var flag = 0;
					$.each(rows, function(i){	//新增时不能重复加入页面列表中
						var id = rows[i].id;
						id = id.replace('c', '');
						if (id == item.id) {
							flag = 1;
						};
					}); 
					if (flag == 0) {
						$('#cLineList').datagrid('insertRow',{
							index: 0,	
							row: {
								'id' : item.id + 'c',
								'name' : item.name,
								'decisionUnit.text' : item.decUnit.text,
								'publishNo' : item.publishNo,
								'publishDate' : item.publishDate,
								'status' : item.status,
								'invalidReason' : item.invalidReason
							}
						});
					} 
				}); 
			}
			$('#searchNorFile').window('close');
			
		});
		
		//初始化comboxtree和combobox
		$('#cleanupUnitId').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onChange:function(newValue, oldValue) {
				var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId=' + newValue;    
	            $('#cleanupLeaderId').combobox('reload', url);    
	            $('#cleanupClerkId').combobox('reload', url);
			},
	        onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		showMsg('只能选择部门!');
	        		return false; 
	        	}
            },
		    required: true  
		});  

		$('#appUnitId').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onChange:function(newValue, oldValue) {
				var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId=' + newValue;    
	            $('#appUnitLeaderId').combobox('reload', url);    
	            $('#appUnitClerkId').combobox('reload', url);
			},  
			onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		showMsg('只能选择部门!');
	        		return false; 
	        	}
            },
		    required: true  
		}); 
		
		$('#draftUnit').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
		});
		
		
	});
	
	//清除页面
	function cleanPage() {
		$('#cleanId').val('');
		
		$("#cleanupUnitId").combotree('setValue', '');
		$("#cleanupLeaderId").combobox('setValue', '');
		$("#cleanupClerkId").combobox('setValue', '');
		
		$("#appUnitId").combotree('setValue', '');
		$("#appUnitLeaderId").combobox('setValue', '');
		$("#appUnitClerkId").combobox('setValue', '');
		
		$("#mainLeadersId").val('');
		$("#cleanupDateId").datetimebox('setValue', '');
		
		//清理子表grid
		$('#cLineList').datagrid('loadData',{total:0, rows:[]});
	}
	
	//删除子清理文件
	function deleteCkes(index, id) {
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/cleanup/deleteLineFile.do",
			  data : {fileId : id},
			  dataType : 'json',
			  success : function(data)
			  {
				  //删除对应的行
				  $('#cLineList').datagrid('deleteRow', index);
			  },
			  error : function(data)
			  {
				  showMsg("删除文件报错!");
			  }
			});
	}
	
	function load(id) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/cleanup/load.do",
			data : {
				id : id
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$('#cleanId').val(data.id);
					
					$("#cleanupUnitId").combotree('setValue', data.cleanupUnit.id);
					$("#cleanupLeaderId").combobox('setValue', data.cleanupUnitLeader.id);
					$("#cleanupClerkId").combobox('setValue', data.cleanupUnitClerk.id);
					
					$("#appUnitId").combotree('setValue', data.approvalUnit.id);
					$("#appUnitLeaderId").combobox('setValue', data.approvalUnitLeader.id);
					$("#appUnitClerkId").combobox('setValue', data.approvalUnitClerk.id);
					
					$("#mainLeadersId").val(data.mainLeaders);
					
					var cdate = new Date();	//日期转换
					if ('' != data.cleanupDate && null != data.cleanupDate) {
						cdate.setTime(data.cleanupDate);
					}
					
					var dateStr = cdate.getFullYear() + '-';
					dateStr += cdate.getMonth() + 1;
					dateStr += '-' + cdate.getDate();
					dateStr += ' ';
					dateStr += cdate.getHours();
					dateStr += ':' + cdate.getMinutes();
					dateStr += ':' + cdate.getSeconds();
					$("#cleanupDateId").datetimebox('setValue', dateStr);
					
					$('#cLineList').datagrid({
						url : '${pageContext.request.contextPath}/cleanup/gainChildren.do',
						queryParams : {parentId : data.id}
					});
				}
			},
			error : function(data) {
				showMsg("清理文件加载出错,请重试!");
			}
		});
	}
	
	function formatOper(val,row,index) {
		var id = row.id;
		id = id.substring(0, id.length - 1); 
		return '<a href="#" onclick="deleteCkes(' + index + ',' + id + ')">删除</a>';
	}
	
	function formatDate (date) {
		var fdate = new Date();	//日期转换
		if ('' != date && null != date) {
			fdate.setTime(date);
		}
		var dateStr = fdate.getFullYear() + '-';
		dateStr += fdate.getMonth() + 1;
		dateStr += '-' + fdate.getDate();
		return dateStr;
	}
	
	function formatSearchDate(val,row,index) {
		return formatDate(val);
	}
	
	function formatStatus(val, row) {
		return fileStatus[val];
	}
</script>

</head>
<body>
	    <div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
	    	<table class="table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div class="button_change red center" id="cleanBtn">清理</div>
	    				<div class="button_change red center" id="addBtn">新增</div>
	    				<div class="button_change red center" id="saveBtn">保存</div>
	    				<div class="button_change red center" id="deleteBtn">删除</div>
	    				<div class="button_change red center" id="submitBtn">提交</div>
	    				<div class="button_change red center" id="approveBtn">审核</div>
	    				<div class="button_change red center" id="unApproveBtn">弃审</div>
	    				<div class="button_change red center" id="flowBtn">流程</div>
	    				<div class="button_change red center" id="examBtn">公布</div>
	    				<div class="button_change red center" id="searchBtn">查找</div>
	    				<div class="button_change red center" id="exportBtn">导出</div>
	    				<div class="button_change red center" id="printBtn">打印</div>
	    			</td>
	    		</tr>
	    	</table>
	    	<form id="downloadId"  class="downloadId" name="download" action="" method="post" ></form>
	    	<div id="dataDivWindow"  class="dataDivWindow" >
	    	<form id="formId" action="">
	    	<table  class="spe_table table-cleanup" id="tableId">
				<tbody id="cleanTable">
					<tr>
						<td class="td-label">清理单位</td>
						<td class="td-data">
							<input id="cleanId" type="hidden" /><!-- id隐藏 -->
							<input id="cleanupUnitId"  name="cleanupUnit" value=""/>
						</td>
						<td class="td-label">清理单位负责人</td>
						<td class="td-data">
							<input id="cleanupLeaderId" name="cleanupUnitLeader" class="easyui-combobox"
							data-options="valueField:'id',textField:'name'" />
						</td>
						<td class="td-label">清理单位经办员</td>
						<td class="td-fileName" colspan="2">
							<input id="cleanupClerkId" name="cleanupUnitClerk" class="easyui-combobox"
							data-options="valueField:'id',textField:'name'" /> 
						</td>
					</tr>
					<tr>
						<td class="td-label">审查单位</td>
						<td class="td-data">
							<input id="appUnitId" name="appUnit" value=""/>
						</td>
						<td class="td-label">审查单位负责人</td>
						<td class="td-data">
							<input id="appUnitLeaderId" name="appUnitLeader" class="easyui-combobox"
							data-options="valueField:'id',textField:'name'" />
						</td>
						<td class="td-label">审查单位经办员</td>
						<td class="td-fileName" colspan="2">
							<input id="appUnitClerkId" name="appUnitClerk" class="easyui-combobox"
							data-options="valueField:'id',textField:'name'" />
						</td>
					</tr>
					<tr>
						<td class="td-label">主要审查领导</td>
						<td class="td-data" colspan="3">
							<input id="mainLeadersId" name="mainLeaders" value="" style="width: 99%;"/>
						</td>
						<td style="height:20px">清理日期</td>
						<td class="td-fileName" colspan="2">
							<input id="cleanupDateId" type="text" class="easyui-datetimebox" required="required"/>
						</td>
					</tr>
				</tbody>
			</table>
			</form>
			<table id="cLineList" class="easyui-datagrid" data-options=
			"pagination:true, rownumbers:true, singleSelect:true, padding: 0, height: 320" >
			<thead>
				<tr>
					<th data-options="field:'id', hidden:true">序号</th>
					<th data-options="field:'name', width:240">规范性文件名称</th>
					<th data-options="field:'decisionUnit.text', width:140">制定单位</th>
					<th data-options="field:'publishNo', width:65">文件号</th>
					<th data-options="field:'publishDate', width:80">发布日期</th>
					<th data-options="field:'status', width:60,formatter:formatStatus">状态</th>
					<th data-options="field:'invalidReason', width:210">备注</th>
					<th data-options="field:'operate',
					formatter:formatOper">操作</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		</div>
		</div>
		</div>
	    
	
	<!-- 弹出清理查询页面 -->
	<div id="searchNorFile" title="规范性文件查询" class="easyui-window" data-options=
		"collapsible:false, minimizable:false, maximizable:false, closed:true, width: 800, height: 500, padding: 0">
		<table  class="spe_table  table-btn-clean-table">
			<tr>
				<td class="table-btn-clean-td">起草单位:</td>
				<td class="table-btn-clean-td-data"><input id="draftUnit" /></td>
				<td class="table-btn-clean-td">开始日期:</td>
				<td class="table-btn-clean-td-data"><input id="begDate" type="text" class="easyui-datetimebox"/></td>
				<td class="table-btn-clean-td">结束日期:</td>
				<td class="table-btn-clean-td-data"><input id="endDate" type="text" class="easyui-datetimebox"/></td>
			</tr>
			<tr>
				<td class="table-btn-clean-td">文件名称:</td>
				<td colspan="3">
					<input id="filename" style="width: 99%;"/>
				</td>
				<td class="table-btn-clean-td">依据:</td>
				<td class="table-btn-clean-td-data"><input id="legalBasis" type="text" /></td>
			</tr>
			<tr>
				<td colspan="6" class="table-btn-clean-td">
					<div class="button_change red center" id="searchFileBtn">查询</div>
				</td>
			</tr>
		</table>
		
		<table id="fileList" class="easyui-datagrid" data-options= "pagination:true, rownumbers:true, height: 320, padding: 0" >
			<thead>
				<tr>
					<th  data-options="field:'id', checkbox:true, height:20, ">序号</th>
					<th  data-options="field:'name', width:240, height:20, ">规范性文件名称</th>
					<th  data-options="field:'decUnit.text', width:100, height:20, ">制定单位</th>
					<th  data-options="field:'publishNo', width:100, height:20, ">文件号</th>
					<th  data-options="field:'publishDate', width:80, height:20, ">发布日期</th>
					<th  data-options="field:'status', width:100,formatter:formatStatus, width:30, height:20, " >状态</th>
					<th  data-options="field:'invalidReason', width:100, height:20, ">备注</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
		

		<table class="search-button">
			<tr>
				<td width="320px"></td>
				<td><input id="confirm" type="button" value="提审" /></td>
				<td><input id="cancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
</body>
</html>