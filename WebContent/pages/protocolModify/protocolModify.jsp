<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/protocolModify.css"/>
<title>草案修改</title>
<script type="text/javascript">

var editor = null;
var drfeditor = null;
var deliberationComment = '';
var init = true;
var drfInit = true;
$(function(){

	//显示的页面按钮
	displayBtn();
	//适应分辨率高度
	autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
	//初始化editor
	editor = UE.getEditor('editor');
	editor.addListener("ready", function(){
		init = false;
	});
	
	//初始化drfeditor
	drfeditor = UE.getEditor('drfeditor');
	drfeditor.addListener("ready", function(){
		drfInit = false;
	});
	
	var listId = "${param.id}";
	if (listId != "") {
		load(parseInt(listId, 10), 'PMO');
	}
	
	$("#btnOpen").click(function() {
		openFrame('DELIBERATION_PROTOCOL');
	});
	
	$("#btnadd").click(function(){
		clear();
		setModifiable(true);
	});
	
	$("#btnsave").click(function(){
		var fileEditor = editor.getContent();
		fileEditor = fileEditor.replace('(草案)','(草案修改稿)');
		editor.setContent(fileEditor);
		save(false);
	});
	$("#btnconfirm").click(function(){
		var fileEditor = editor.getContent();
		fileEditor = fileEditor.replace('(草案修改稿)','');
		editor.setContent(fileEditor);
		save(true);
	});
	
	$("#btndel").click(function(){
		var id = $('#pid').val();
		if(id == ""){
			 showMsg("请先选择一个草案修改单!");
			 return;
		}
		$.ajax({
		  type: "POST",
		  url: "${pageContext.request.contextPath}/protocolModify/delete.do",
		  data : {id : id},
		  dataType : 'json',
		  success : function(data){
			  if(data.message){
				  showMsg(data.message);
			  }else if (data.msg == "success") {
				 showMsg("删除成功!");
				 clear();
			 } else {
				 showMsg("删除失败!");
			 }
		  },
		  error : function(data){
			  showMsg("删除出错,请重试!");
		  }
		});
	});
	
	$("#btnExport").click(
			function() {
				var norId = $('#norId').val();
				if (null != norId && "" != norId) {
					$("#downloadId").attr(
							"action",
							"${pageContext.request.contextPath}/protocolModify/export.do?norId="+ norId);
					$("#downloadId").submit();
				} else {
					showMsg("请选择文件！");
				}
			});
	
	$("#btnPrint").click(function() {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
						type : "post",
						url : "${pageContext.request.contextPath}/protocolModify/print.do",
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
	
	$("#btnList").click(
			function() {
				location.href = "${pageContext.request.contextPath}/protocolModify/protocolModifyList.wf";
			});
	
	$("#btnfind").click(function (){
		//设置查询的datagrid的数据集url
		$('#delDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/protocolModify/find.do',
			pageNumber: 1,
			onDblClickRow: function (rowIndex, rowData) {
				clear();
				var nId = rowData.id;
				load(nId,"PMO");
				$('#findWindow').window('close'); //关闭查询窗口
			}
		});
		$('#findWindow').window('open'); //打开审议报请查询窗口
	});
});

function clear() {
	 $('#protocolModifyForm').form('clear');
	 if(!init){
		  editor.setContent("");
	  }
	 if(!drfInit){
		  drfeditor.setContent('');
	 }
	 deliberationComment = '';
	 $('#deliberationCommentId').empty();
}

//关闭window窗口
function closeFindWindow() {
	$('#findWindow').window('close');
}

//查找草案审议
function findDeliberationRequest() {
	var fileName = $('#findFileNameId').val();
	$('#delDataGridId').datagrid({
					url : '${pageContext.request.contextPath}/protocolModify/find.do?name='+ fileName
					});
	$('#delDataGridId').datagrid('reload'); //加载数据,实现文件的过滤
}

//点击确认
function comfirm() {
	var selectRow = $('#delDataGridId').datagrid('getSelected');
	if(selectRow == null){
		return;
	}
	var nId = selectRow.id;
	load(nId,"PMO");
	$('#findWindow').window('close'); //关闭查询窗口
}

function deliberationCommentContent(fileName) {
	var norId = $('#norId').val();
	if (null != norId && "" != norId) {
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/protocolModify/gainContent.do",
			  data : {
				  norId : norId,
				  fileName:fileName,
				  },
			  dataType : 'html',
			  success : function(data){
				  $('#deliberationCommentContent').html('');
				  $('#deliberationCommentContent').html(data);
				  $('#deliberationComment').window('open');	//打开送审稿浏览
			  },
			  error : function(data) {
				  showMsg("打开出错,请重试!");
			  }
			});
	} else {
		showMsg("请文件！");
	}
}

function load(id,sreachType) {
	$.ajax({
		type : "POST",
		url : "${pageContext.request.contextPath}/protocolModify/load.do",
		data : {
			id : id,
			sreachType :  sreachType
		},
		dataType : 'json',
		success : function(data) {
			if (data != null) {
				$("#pid").val(data.vo.id);
				$("#norId").val(data.vo.normativeFile.id);
				$("#protocolModifyNameId").val(data.vo.normativeFile.name);
				$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
				$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
				$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
				
				if (data.vo.deliberationComment != null && data.vo.deliberationComment.length > 0) {
					deliberationComment = data.vo.deliberationComment;
					$('#deliberationCommentId').empty();
					var strs = new Array();
					strs = data.vo.deliberationComment.split(";");
					$.each(strs,function(index, tx) {
										$("#deliberationCommentId").append(
												'<a class="delcom" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
										$(".delcom").bind('click',function(){  
											deliberationCommentContent(tx);
									    });
									});
				}
				var content = data.vo.content;
				if(sreachType == "NORFILE"){
					$.ajax({	//加载草案
						  type: "POST",
						  url: "${pageContext.request.contextPath}/protocolModify/gainFileContent.do",
						  data : {id : id, sreachType : sreachType, fileType : 'PROTOCOL'},
						  dataType : 'html',
						  success : function(data){
							  if(init){
								  editor.addListener("ready", function() {
									  editor.setContent(data);
								  });
							  }else{
								  editor.setContent(data);
							  }
						  },
						  error : function(data)
						  {
							  showMsg("读取出错,请重试!");
						  }
					});
				}else{
					if(init){
						editor.addListener("ready", function() {
							editor.setContent(content);
						});
					}else{
						editor.setContent(content);
					}
				}
					
					$.ajax({	//加载起草说明
						  type: "POST",
						  url: "${pageContext.request.contextPath}/protocolModify/gainFileContent.do",
						  data : {id : id, sreachType : sreachType, fileType : 'INSTRUCTION'},
						  dataType : 'html',
						  success : function(data){
							  if(drfInit){
								  drfeditor.addListener("ready", function() {
									  drfeditor.setContent(data);
								  });
							  }else{
								  drfeditor.setContent(data);
							  }
						  },
						  error : function(data)
						  {
							  showMsg("读取出错,请重试!");
						  }
					});
					setModifiable(data.modifiable);
			
				
				
			}
		},
		error : function(data) {
			showMsg("加载出错,请重试!");
		}
	});
}

function save(isConfirm){
	
	var id =  $("#pid").val();
	var norId = $('#norId').val();
	if(norId == ""){
		showMsg("文件名称不能为空");
		return;
	}
	var delComment = deliberationComment;
	var mcontent = '';
	var drfcontent = '';
	mcontent = editor.getContent();
	drfcontent = drfeditor.getContent();
	if(mcontent == "" ){
		$("#editor").focus();
		showMsg("请填写要发布的规范性文件!");
		return;
	}
	if(drfcontent == ""){
		$("#drfeditor").focus();
		showMsg("请填写要发布的起草说明!");
		return;
	}
	$.ajax({
		type: "POST",
		  url: "${pageContext.request.contextPath}/protocolModify/save.do",
		  data:{id:id,'normativeFile.id':norId,content:mcontent,deliberationComment:delComment,isConfirm: isConfirm, drfcontent:drfcontent},
		  dataType : 'json',
		  success : function(data){
			  showMsg(data.msg);
			  if(data.success && data.vo){
					$("#pid").val(data.vo.id);
				}
		  },
		  error : function(data){
			  showMsg("保存出错,请重试!");
		  }
		});
}

function setModifiable(modifiable){
	   
	   showDiv("btnsave", modifiable);
		showDiv("btndel", modifiable);
		showDiv("btnconfirm", modifiable);
	   
	   if(modifiable){
			 if(init){
				 editor.addListener("ready", function() {
					 editor.setEnabled();
				});
				 
			  }else{
				  editor.setEnabled();
			  }
			 if(drfInit){
				 drfeditor.addListener("ready", function() {
					 drfeditor.setEnabled();
				 }); 
			 }else{
				 drfeditor.setEnabled();
			 }
			
		}else{
			if(init){
				editor.addListener("ready", function() {
					 editor.setDisabled('fullscreen');
				});
			  }else{
				  editor.setDisabled('fullscreen');
			  }
			 if(drfInit){
				 drfeditor.addListener("ready", function() {
					 drfeditor.setDisabled('fullscreen');
				 }); 
			 }else{
				 drfeditor.setDisabled('fullscreen');
			 }
		}
}

</script>
</head>
<body>
		<div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680">
				<table class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
		    				<div id="btnadd" class="button_change red center">新增</div>
		    				<div id="btnsave" class="button_change red center">保存</div>
		    				<div id="btnconfirm" class="button_change red center">定稿</div>
		    				<div id="btndel" class="button_change red center">删除</div>
		    				<div id="btnList" class="button_change red center">列表</div>
		    				<div id="btnfind" class="button_change red center">查找</div>
		    				<div id="btnExport" class="button_change red center">导出</div>
		    				<div id="btnPrint" class="button_change red center">打印</div>
	    				</td>
					</tr>
				</table>
				<div><form id="downloadId"  class="downloadId" name="download" action="" method="post" ></form></div>
				<div id="dataDivWindow"  class="dataDivWindow" >
					<form id="protocolModifyForm" method="post">
					<table  cellpadding="0" cellspacing="0"class="spe_table  dataTable">
						<tbody>
							<tr>
								<td  class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
								<input id="norId" name="normativeFile.id" type="hidden" /> 
								<input id="pid" name="protocolModifyId" type="hidden" /> 
								<input id="protocolModifyNameId" name="protocolModifyName" readonly="readonly"  class="fileName" /></td>
								<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div></td>
							</tr>
							<tr>
								<td  class="td-label">主起草单位</td>
								<td class="td-data">
								<input id="draftingUnitId" name="draftingUnit" readonly="readonly" class="td-3data" /></td>
								<td  class="td-label">主起草单位负责人</td>
								<td class="td-data">
								<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data" /></td>
								<td  class="td-label">主起草单位经办员</td>
								<td class="td-data">
								<input  id="draftingUnitClerkId" name="draftingUnitClerk" class="td-3data"  readonly="readonly" /></td>
							</tr>
							<tr>
								<td  class="td-label">审议意见</td>
								<td colspan="6" class="td-upload-file">
								<div  id="deliberationCommentId" ></div></td>
							</tr>
							<tr>
								<td  class="td-label">起草说明</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="drfeditor" type="text/plain" class="td-drfeditor"></script>
										</center>
								</td>
							</tr>
							<tr>
								<td  class="td-label">修改内容</td>
								<td colspan="6"class="td-file-content">
									<center>
										<script id="editor" type="text/plain" class="td-editor"></script>
									</center>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>
		</div>	
		
		<!-- 弹出浏览审议意见页面 -->
	<div id="deliberationComment" title="审议意见浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false" maximizable="false"
		closed="true" resizable="false">
	   <div id="deliberationCommentContent" class="view-file"></div>
	</div>
	
		<!-- 弹出页面 -->
		<div id="findWindow" title="草案修改查询" class="easyui-window"
			 	data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
			<table class="search-fileName">
				<tr>
					<td>文件名称:</td>
					<td><input id="findFileNameId" name="deliberationRequest"size="60" /></td>
					<td><input type="button" onclick="findDeliberationRequest()" value="查询" /></td>
				</tr>
			</table>
			<table id="delDataGridId" data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
				<thead>
					<tr>
						<th data-options="field:'id',hidden:'true'">id</th>
						<th data-options="field:'name',width:'557'">文件名</th>
					</tr>
				</thead>
			</table>
			<table class="search-button">
				<tr>
					<td width="570px"></td>
					<td><input id="confirm" onclick="comfirm()" type="button" value="确认" /></td>
					<td><input id="close" type="button" onclick="closeFindWindow();" value="取消" /></td>
				</tr>
		</table>
	</div>
</body>
</html>