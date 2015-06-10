<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>签署发布</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/signAndPublish.css" />
<title>Insert title here</title>
<script type="text/javascript">

var init = true;
var editor = null;
	$(function() {

		$("#invalidDateId").combo({"editable":false});
		$("#signDateId").combo({"editable":false});
		$("#publishDateId").combo({"editable":false});
		
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');

		//初始化editor
		editor = UE.getEditor('editor');
		editor.addListener("ready", function() {
			  init = false;
			  editor.setDisabled('fullscreen');
		});
		
		//初始化有效期
		$('#validDateId').combobox({
			valueField: 'value',
			textField: 'label',
			readonly : true,
			data: [{
				label: '2年',
				value: '2'
			},{
				label: '5年',
				value: '5'
			}]
		});
		
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'SIGNANDPUBLISH');
		}
		
 		$("#decUnitId").combotree({
					url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
					onSelect : function(node) {
						var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId='+ node.id;
						$('#decUnitClerkId').combobox("clear");
						$('#decUnitLeaderId').combobox("clear");
						$('#decUnitClerkId').combobox('reload', url);
						$('#decUnitLeaderId').combobox('reload', url);
					},
					onBeforeSelect : function(node) {
						if (node.attributes.nodeType == orgNodeType[1]) {
							$.messager.alert("提示", "不能选择地区！");
							return false;
						}
					}
				}); 
		
		
		
		$("#btnOpen").click(function() {
			openFrame('DELIBERATION_MODIFY');
		});

		$("#btnAdd").click(function() {
			clear();
			setModifiable(true);
		});

		$("#btnSave").click(function() {
				var norId = $("#norId").val();
				if(norId == ""){
					showMsg("文件名称不能为空");
					return;
				}
				var spId= $("#signAndPublishId").val();
				var duId = $("#decUnitId").combobox('getValue');
				var decUnitClerk = $("#decUnitClerkId").combobox('getValue');
				var decUnitLeader = $("#decUnitLeaderId").combobox('getValue');
				var validDate = $("#validDateId").combobox('getValue');
				var invalidDateId = $("#invalidDateId").datebox('getValue');
				var signLeadersId = $("#signLeadersId").val();
				var signDateId = $("#signDateId").datebox('getValue');
				var publishNoId = $("#publishNoId").val();
				var publishDateId = $("#publishDateId").datebox('getValue');
				var legalDocId = '';
				legalDocId = editor.getContent();
				if(duId == "" ){
					showMsg("请选择制定单位");
					return;
				}
				if(decUnitClerk == "" ){
					showMsg("请选择制定单位经办员");
					return;
				}
				if(decUnitLeader == "" ){
					showMsg("请选择制定单位负责人");
					return;
				}
				if(signLeadersId == "" ){
					showMsg("请填写签署领导");
					return;
				}
				if(publishNoId == "" ){
					showMsg("请录入发文号");
					return;
				}
				$.ajax({
					type : "POST",
					url : "${pageContext.request.contextPath}/signAndPublish/save.do",
					data : {id : spId,"normativeFile.id":norId,"decisionMakingUnit.id":duId,"decisionMakingUnitLeader.id":decUnitLeader,"decisionMakingUnitClerk.id":decUnitClerk,signLeaders:signLeadersId,
								validDate:validDate,invalidDate:invalidDateId,signDate:signDateId,publishNo:publishNoId,publishDate:publishDateId,legalDoc:legalDocId
					},
					dataType : 'json',
					success : function(data) {
						showMsg(data.msg);
						if(data.success && data.vo){
							$("#signAndPublishId").val(data.vo.id);
						}
					},
					error : function(data) {
						showMsg("保存出错,请重试!");
					}
				});
	});

		$("#btnDelete").click(function() {
			var id = $("#signAndPublishId").val();
			if(id == ""){
				 showMsg("请先选择一个签署发布单!");
				 return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/signAndPublish/delete.do",
				data : {id : id},
				dataType:"json",
				success : function(data) {
					if(data.message){
						showMsg(data.message);
					}else if(data.msg = "success"){
						showMsg("删除成功!");
						clear();
					}else{
						showMsg("删除失败!");
					}
				},
				error : function(data) {
					showMsg("删除出错,请重试!");
				}
			});
		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/signAndPublish/signAndPublishList.wf";
		});

		//查找签署与发布
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/signAndPublish/find.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					clear();
					var id = rowData.id;
					load(id, 'SIGNANDPUBLISH');
					$('#searchSignAndPublish').window('close'); //关闭查询窗口
				}
			});
			$('#searchSignAndPublish').window('open');

		});

		//关闭签署与发布查找窗口
		$("#btnCancel").click(function() {
			$('#searchSignAndPublish').window('close');
		});

		//通过文件名查询签署与发布
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/signAndPublish/find.do?name='+ fileName
			});
			$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});

		//点击确认
		$("#btnConfirm").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			var id = selectRow.id;
			load(id, 'SIGNANDPUBLISH');
			$('#searchSignAndPublish').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var id = $("#signAndPublishId").val();
			if(id == ""){
				showMsg("请先选择一个签署发布单!");
				return;
			}
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/signAndPublish/print.do",
					data : {
						norId : norId
					},
					dataType : "html",
					success : function(data) {
						lodopPrint(data);
					},
					error : function(data) {
						showMsg("打印失败!");
					}
				});
			} else {
				showMsg("请选择文件！");
			}
		});

		$("#btnExport").click(function() {
			var id = $("#signAndPublishId").val();
			if(id == ""){
				showMsg("请先选择一个签署发布单!");
				return;
			}
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$("#downloadId").attr(
						"action",
						"${pageContext.request.contextPath}/signAndPublish/export.do?norId="+ norId);
				$("#downloadId").submit();
			} else {
				showMsg("请选择文件！");
			}
		});
	});

	function clear() {
		$('#signAndPublishForm').form('clear');
		if(!init){
			editor.setContent("");
		}
	}
	
	//得到当前日期
	formatterDate = function(date) {
		var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
		var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
		+ (date.getMonth() + 1);
		return date.getFullYear() + '-' + month + '-' + day;
	};
	
	function legalDoc(){
		var norId = $('#norId').val();
		$.ajax({
			type : "post",
			url : "${pageContext.request.contextPath}/signAndPublish/gainContent.do",
				data : {
					norId : norId
				},
				dataType : "html",
				success : function(data) {
					if(init){
						 editor.addListener("ready", function() {
							 editor.setContent(data);
						});
					  }else{
						  editor.setContent(data);
					  }
				},
				error : function(data) {
					showMsg("获取内容失败!");
				}
		});
	}
		
	

	function load(id, searchType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/signAndPublish/load.do",
			data : {
				id:id,
				sreachType : searchType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$("#signAndPublishId").val(data.vo.id);
					$("#norId").val(data.vo.normativeFile.id);
					$("#nameId").val(data.vo.normativeFile.name);
					$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
					if (data.vo.decisionMakingUnit != null) {
							$("#decUnitId").combotree({
								url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
								onLoadSuccess : function(node){
							$("#decUnitId").combotree('setValue',data.vo.decisionMakingUnit.id);
							var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId='+ data.vo.decisionMakingUnit.id;
							$('#decUnitClerkId').combobox('reload', url);
							$("#decUnitClerkId").combobox('setValue',data.vo.decisionMakingUnitClerk.id);
							$('#decUnitLeaderId').combobox('reload', url);
							$("#decUnitLeaderId").combobox('setValue',data.vo.decisionMakingUnitLeader.id);
						}
						});
					}else{
						$("#decUnitId").combotree("clear");
						$('#decUnitClerkId').combobox('clear');
						$('#decUnitLeaderId').combobox('clear');
					}
					if(data.vo.id == null){
						$("#signDateId").datebox('setValue',formatterDate(new Date()));
						$("#publishDateId").datebox('setValue',formatterDate(new Date()));
						$("#invalidDateId").datebox('setValue',formatterDate(new Date()));
					}else{
						$("#signDateId").datebox('setValue',data.vo.signDate);
						$("#publishDateId").datebox('setValue',data.vo.publishDate);
						$("#invalidDateId").datebox('setValue',data.vo.invalidDate);
					}

					$("#signLeadersId").val(data.vo.signLeaders);
					$("#publishNoId").val(data.vo.publishNo);
					$('#validDateId').combobox('setValue', data.vo.normativeFile.validDate);
					
					legalDoc();
					setModifiable(data.modifiable);
				}
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
	}
	function setModifiable(modifiable){
		$('#decUnitId').combotree('readonly', !modifiable);
		$('#decUnitLeaderId').combobox({
			onLoadSuccess:function(){
				$('#decUnitLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#decUnitClerkId').combobox({
			onLoadSuccess:function(){
				$('#decUnitClerkId').combobox('readonly', !modifiable);
			}
		});
		   $("#invalidDateId").datebox("readonly",!modifiable);
		   $("#signDateId").datebox("readonly",!modifiable);
		   $("#publishDateId").datebox("readonly",!modifiable);
		   $("#publishNoId").prop("readonly",!modifiable);
		   $("#signLeadersId").prop("readonly",!modifiable);
		   
		   	showDiv("btnSave", modifiable);
			showDiv("btnDelete", modifiable);
			showDiv("btnsubmit", modifiable);
			showDiv("btnunApprove", modifiable);
			showDiv("btnapprove", modifiable);
			showDiv("btnpublish", modifiable);
	}
		
</script>
</head>
<body>
		<div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
				<table style="height: 30px;" class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
							<div id="btnAdd" class="button_change red center" >新增</div> 
							<div id="btnSave" class="button_change red center" >保存</div> 
							<div id="btnsubmit" class="button_change red center" >提交</div> 
							<div id="btnapprove" class="button_change red center" >审核</div> 
							<div id="btnunApprove" class="button_change red center" >弃审</div> 
							<div id="btnpublish" class="button_change red center" >发布</div> 
							<div id="btnflow" class="button_change red center" >流程</div>
							<div id="btnDelete" class="button_change red center" >删除</div> 
							<div id="btnList" class="button_change red center" >列表</div>
							<div id="btnFind" class="button_change red center" >查找</div> 
							<div id="btnExport" class="button_change red center" >导出</div>
							<div id="btnPrint" class="button_change red center" >打印</div>
						</td>
					</tr>
				</table>

				<div><form id="downloadId"  class="downloadId" name="download" action="" method="post"></form></div>
				<div id="dataDivWindow"  class="dataDivWindow" >
					<form  id="signAndPublishForm" method="post">
					<table  cellpadding="0" cellspacing="0"class="spe_table dataTable">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
								<input id="norId" name="normativeFileId" type="hidden" /> 
								<input id="signAndPublishId" name="signAndPublishId" type="hidden" /> 
								<input id="nameId" name="signAndPublishName" class="fileName" readonly="readonly"/></td>
								<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div></td>
							</tr>
							<tr>
								<td class="td-label">制定单位</td>
								<td class="td-data">
								<input id="decUnitId" name="decisionMakingUnit" class="easyui-combotree"  data-options="valueField:'id',textField:'text'," value="" /></td>
								<td class="td-label">制定单位负责人</td>
								<td class="td-data">
								<input id="decUnitLeaderId" name="decisionMakingUnitLeader" class="easyui-combobox"  data-options="valueField:'id',textField:'name'," value=""  /></td>
								<td class="td-label">制定单位经办员</td>
								<td class="td-data">
								<input id="decUnitClerkId" name="decisionMakingUnitClerk" class="easyui-combobox"  data-options="valueField:'id',textField:'name'," value="" /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">起草单位</td>
								<td class="td-data">
								<input id="draftingUnitId" name="draftingUnit" class="td-3data" readonly="readonly"/></td>
								<td class="td-label">有效期</td>
								<td class="td-data">
								<input id="validDateId" name="validDate"  readonly="readonly"/></td>
								<td class="td-label">失效期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="invalidDateId" name="invalidDate" type="text"  /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">主要签署领导</td>
								<td colspan="3" class="td-fileName">
								<input id="signLeadersId" class="td-input-signLeaders" /></td>
								<td class="td-label">签署日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="signDateId" name="signDate" type="text"  /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">发文号</td>
								<td class="td-data">
								<input id="publishNoId" name="publishNo" class="td-3data" /></td>
								<td class="td-label">发布日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="publishDateId" name="publishDate" type="text"  /></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">规范性文件</td>
								<td colspan="6" class="td-file-content">
										<center>
										<script id="editor" type="text/plain" style="width:860px;height:420px;"></script>
										</center>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>
		</div>

	<div id="searchSignAndPublish" title="签署与发布查询" class="easyui-window"
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName">
			<tr>
				<td>文件名称:</td>
				<td><input id="searchNameId" name="searchName" size="60" /></td>
				<td><input id="btnSearch" type="button" value="查询" /></td>
			</tr>
		</table>

		<table id="searchDataGrid" class="easyui-datagrid" data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
			<thead>
				<tr>
					<th data-options="field:'id',hidden:'true'">id</th>
					<th data-options="field:'name',width:'557'">文件名</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>

		<table class="search-button">
			<tr>
				<td width="570px"></td>
				<td><input id="btnConfirm" type="button" value="确认" /></td>
				<td><input id="btnCancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
</body>
</html>