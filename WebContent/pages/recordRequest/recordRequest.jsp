<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link  rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/recordRequest.css"/>
<title>备案报送</title>
<script type="text/javascript">
var editor = null;
var legalDoc = '';
var legalBasis = '';
var draftingInstruction = '';
var priority = {NORMAL:'一般',EMERGENCY:'紧急',EXTRA_URGENT:'特急',VERSION:'特提'};
var init = true;
	$(function(){
		
		$("#rReqDateId").combo({"editable":false});
		
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		//初始化editor
		editor = UE.getEditor('editor');
		editor.addListener("ready", function() {
			  init = false;
		});
		
		//List页面初始化
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'RECREQUEST');
		}
	
	$("#rUnitId").combotree({
		url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
		onBeforeSelect : function(node) {
			if (node.attributes.nodeType == orgNodeType[1]) {
				$.messager.alert("提示", "不能选择地区！");
				return false;
			}
		},
		onSelect : function(node) {
			var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId='+ node.id;
			$('#rUnitLeaderId').combobox("clear");
			$('#rUnitClerkId').combobox("clear");
			$('#rUnitLeaderId').combobox('reload', url);
			$('#rUnitClerkId').combobox('reload', url);
		},
	}); 
	
	$("#btnOpen").click(function() {
		openFrame('PUBLISH');
	});
	
	$("#btnadd").click(function(){
		clear();
		setModifiable(true);
	});
	
	$("#btnsave").click(function(){
		var recId =  $("#recordRequestId").val();
		var norId = $('#norId').val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var rUnitId = $("#rUnitId").combobox('getValue');
		var rUnitLeaderId = $("#rUnitLeaderId").combobox('getValue');
		var rUnitClerkId  = $("#rUnitClerkId").combobox('getValue');
		var rReqDateId = $("#rReqDateId").datebox('getValue');
		var phoneId = $("#phoneId").val();
		var legalDocContent = legalDoc;
		var recordReportContent = '';
		recordReportContent = editor.getContent();
		if(rUnitId == "" ){
			$("#rUnitId").focus();
			showMsg("请先配置备案审查单位");
			return;
		}
		if(rUnitLeaderId == "" ){
			$("#rUnitLeaderId").focus();
			showMsg("请选择备案审查单位负责人");
			return;
		}
		if(rUnitClerkId == "" ){
			$("#rUnitClerkId").focus();
			showMsg("请选择备案审查单位经办员");
			return;
		}
		if(phoneId == "" ){
			$("#editor").focus();
			showMsg("请录入联系电话");
			return;
		}
		if(recordReportContent == "" ){
			$("#nameId").focus();
			showMsg("请填写备案报告");
			return;
		}
		$.ajax({
			type: "POST",
			  url: "${pageContext.request.contextPath}/recordRequest/save.do",
			  data:{id:recId,"normativeFile.id":norId,"recordUnit.id":rUnitId,"recordUnitLeader.id":rUnitLeaderId,"recordUnitClerk.id":rUnitClerkId,
				 		recordRequestDate:rReqDateId,phone:phoneId,legalDoc:legalDocContent,
				 		recordReport:recordReportContent
				  		},
			  dataType : 'json',
			  success : function(data) {
				  showMsg(data.msg);
				  if(data.success && data.vo){
						$("#recordRequestId").val(data.vo.id);
					}
			  },
			  error : function(data)
			  {
				  showMsg("保存出错,请重试!");
			  }
			});
	});
	
	$("#btnList").click(
			function() {
				location.href = "${pageContext.request.contextPath}/recordRequest/recordRequestList.wf";
			});
	
	$("#btndel").click(function(){
		var id = $('#recordRequestId').val();
		if(id == ""){
			 showMsg("请先选择一个备案报送单!");
			 return;
		}
		$.ajax({
		  type: "POST",
		  url: "${pageContext.request.contextPath}/recordRequest/delete.do",
		  data : {id : id},
		  dataType : 'json',
		  success : function(data) {
			  if(data.message){
				  showMsg(data.message);
			  }else if (data.msg == "success") {
						 showMsg("删除备案报送成功!");
						 clear();
					 	  } else {
						 	showMsg("删除备案报送失败!"); 
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
							"${pageContext.request.contextPath}/recordRequest/export.do?norId="+ norId);
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
						url : "${pageContext.request.contextPath}/recordRequest/print.do",
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
	
	//查找备案报送
	$("#btnfind").click(function() {
		$('#searchDataGrid').datagrid({
			url : '${pageContext.request.contextPath}/recordRequest/find.do',
			pageNumber: 1,
			onDblClickRow: function (rowIndex, rowData) {
				clear();
				var id = rowData.id;
				load(id, 'RECREQUEST');
				$('#searchRecordRequest').window('close'); //关闭查询窗口
			}
		});
		$('#searchRecordRequest').window('open');

	});

	//关闭备案报送查找窗口
	$("#btnCancel").click(function() {
		$('#searchRecordRequest').window('close');
	});

	//通过文件名查询备案报送
	$("#btnSearch").click(function() {
		var fileName = $('#searchNameId').val();
		$('#searchDataGrid').datagrid({
			url : '${pageContext.request.contextPath}/recordRequest/find.do?name='+ fileName
		});
		$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
	});

	//点击确认
	$("#btnConfirm").click(function() {
		var selectRow = $('#searchDataGrid').datagrid('getSelected');
		var id = selectRow.id;
		load(id, 'RECREQUEST');
		$('#searchRecordRequest').window('close'); //关闭查询窗口
	});
	
	//备案报送
	$("#btnsend").click(function(){
		var recordRequestId = $("#recordRequestId").val();
		if(recordRequestId == ""){
			showMsg("请先选择一个备案报送单！");
			return;
		}
		$.ajax({
			type: "post",
			url: "${pageContext.request.contextPath}/recordRequest/send.do",
			data : {
				id : recordRequestId
			},
			dataType : 'json',
			success : function(data){
				if(data.success){
					showMsg("报送成功!");
				}else{
					showMsg("报送出错,请重试!");
				}
			},
			error : function(data) {
				showMsg(data.responseText);
			}
		});
	});
});


function matter(fileName,fileType) {
	var norId = $('#norId').val();
	if (null != norId && "" != norId) {
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/recordRequest/gainContent.do",
			  data : {
				  norId : norId,
				  fileName:fileName,
				  fileType:fileType
				  },
			  dataType : 'html',
			  success : function(data){
				  $('#matter').html('');
				  $('#matter').html(data);
				  $('#paper').window('open');	//打开送审稿浏览
			  },
			  error : function(data) {
				  showMsg("打开出错,请重试!");
			  }
			});
	} else {
		showMsg("请选择文件！");
	}
}

function clear() {
	 $('#recordRequestForm').form('clear');
	 if(!init){
		  editor.setContent("");
	  }		
		legalDoc = '';
		$('#legalDocId').empty();
		
		legalBasis = '';
		$('#legalBasisId').empty();
		
		draftingInstruction = '';
		$('#draftingInstructionId ').empty();
		
}

//得到当前日期
formatterDate = function(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
	+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-' + day;
	};

function load(id,sreachType) {
	$.ajax({
		type : "POST",
		url : "${pageContext.request.contextPath}/recordRequest/load.do",
		data : {
			id : id,
			sreachType :  sreachType
		},
		dataType : 'json',
		success : function(data) {
			if(data == null){
				return;
			}
			$("#recordRequestId").val(data.vo.id);
			$("#norId").val(data.vo.normativeFile.id);
			$("#nameId").val(data.vo.normativeFile.name);
			$("#draUnitId").val(data.vo.normativeFile.drtUnit.text);
			$("#draUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
			$("#draUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
			$("#decUnitId").val(data.vo.normativeFile.decUnit.text);
			$("#decUnitLeaderId").val(data.vo.normativeFile.decUnitLeader.name);
			$("#decUnitClerkId").val(data.vo.normativeFile.decUnitClerk.name);
			
			if (data.vo.recordUnit != null) {
				$("#rUnitId").combotree({
					url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
					onLoadSuccess : function(node){
						if(data.vo.recordUnit.id != null){
							$("#rUnitId").combotree('setValue',data.vo.recordUnit.id);
							var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId='+ data.vo.recordUnit.id;
							$('#rUnitLeaderId').combobox('reload', url);
							if(data.vo.recordUnitLeader != null){
								$("#rUnitLeaderId").combobox('setValue',data.vo.recordUnitLeader.id);
							}
							$('#rUnitClerkId').combobox('reload', url);
							if(data.vo.recordUnitClerk != null){
								$("#rUnitClerkId").combobox('setValue',data.vo.recordUnitClerk.id);
							}
						}
					}
				});
			}
			
			$("#priorityId").val(priority[data.vo.normativeFile.priority]);
			
			$("#rReqDateId").datebox('setValue',formatterDate(new Date()));
			$("#phoneId").val(data.vo.phone);
			
			
			if (data.vo.legalDoc != null && data.vo.legalDoc.length > 0) {
				legalDoc = data.vo.legalDoc;
				$('#legalDocId').empty();
				var strs = new Array();
				strs = data.vo.legalDoc.split(";");
				$.each(strs,function(index, tx) {
					$("#legalDocId").append('<a class="leg" href="#" style="cursor: pointer;">'+ tx+ '</a>');
					$(".leg").bind('click',function(){  
						matter(tx,"LEGALDOC");
				    });
				});
			}
			
			if (data.vo.draftingInstruction != null && data.vo.draftingInstruction.length > 0) {
				draftingInstruction = data.vo.draftingInstruction;
				$('#draftingInstructionId').empty();
				var strs = new Array();
				strs = data.vo.draftingInstruction.split(";");
				$.each(strs,function(index, tx) {
					$("#draftingInstructionId").append('<a class="dra" href="#" style="cursor: pointer;">'+ tx+ '</a>');
					$(".dra").bind('click',function(){  
						matter(tx,"INSTRUCTION");
				    });
				});
			}
			
			if (data.vo.legalBasis != null && data.vo.legalBasis.length > 0) {
				legalBasis = data.vo.legalBasis;
				$('#legalBasisId').empty();
				var strs = new Array();
				strs = data.vo.legalBasis.split(";");
				$.each(strs,function(index, tx) {
					$("#legalBasisId").append(
							'<a class="legb'+ index +'" href="#" style="cursor: pointer;">'+ tx+ '</a>');
								$(".legb" + index).bind('click',function(){  
									matter(tx,"LEGALBASIS");
				   			 });
   					 });
				}
			
			var filename = data.vo.normativeFile.name;
			if(sreachType == 'NORFILE'){
				  //得到文件头
				  gainFileHead("",filename);
			}else{
				  if(init){
					  editor.addListener("ready", function() {
						  editor.setContent(data.vo.recordReport);
					  });
				  }else{
					  editor.setContent(data.vo.recordReport);
				  }
			}
				
			setModifiable(data.modifiable);
		
		},
		error : function(data) {
			showMsg("加载出错,请重试!");
		}
	});
}
					
function setModifiable(modifiable){
	   //$("#rUnitId").combotree("readonly",!modifiable);
	   $("#rUnitLeaderId").combobox("readonly",!modifiable);
	   $("#rUnitClerkId").combobox("readonly",!modifiable);
	   $("#phoneId").prop("readonly",!modifiable);
	   $("#rReqDateId").datebox("readonly",!modifiable);
	   showDiv("btnsave", modifiable);
	   showDiv("btndel", modifiable);
	   showDiv("btnsubmit", modifiable);
	   showDiv("btnapprove", modifiable);
	   showDiv("btnunApprove", modifiable);
	   showDiv("btnsend", modifiable);
	   
	   if(modifiable){
			 if(init){
				 editor.addListener("ready", function() {
					 editor.setEnabled();
				});
			  }else{
				  editor.setEnabled();
			  }
			
		}else{
			if(init){
				 editor.addListener("ready", function() {
					 editor.setDisabled('fullscreen');
				});
			  }else{
				  editor.setDisabled('fullscreen');
			  }
		}
}
//得到文件头
function gainFileHead(filecontent,filename){
	$.ajax({
		  type: "POST",
		  url: contextPath+"/gainFileHead/gainFileHeadName.do",
		  dataType : 'json',
		  success : function(data){
			  filehead ="<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
			  "<p style=\"text-align:center;line-height:54px\"><span style=\"font-size:54px;font-weight:bold; font-family:宋体;color:red;letter-spacing:-0\">"+data.districtName+data.organizationName+"文件</span></p>"+
			  "<hr style=\"-webkit-user-select: none;display:block;width:649px;margin:0px;\" color=\"red\"/>"+
			  "<p style=\"text-align:right\"><span style=\"font-size:18px;font-family:宋体\">x府文备字〔xxxx〕xx号</span></p>"+
			  "<p style=\"line-height:18px\"><span style=\"font-size:18px;font-family:宋体;font-variant:small-caps\">&nbsp;</span></p>"+
			  "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
			  "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于报送《"+filename+"》的备案报告</span></p>";
			  editor.setContent(filehead+"<br/>"+filecontent);
		  },
		  error : function(data){
			  showMsg("加载出错,请重试!");
		  }
	});
}

</script>
</head>
<body>
		<div id="cc" class="easyui-layout cc " style="background-color: red;">
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
				<table class="table">
				<tr>
						<td id="btnTools" style="text-align: left;">
		    				<div id="btnadd" class="button_change red center">新增</div>
		    				<div id="btnsave" class="button_change red center">保存</div>
		    				<div id="btnsubmit" class="button_change red center">提交</div>
		    				<div id="btnapprove" class="button_change red center">审核</div>
		    				<div id="btnunApprove" class="button_change red center">弃审</div>
		    				<div id="btnflow" class="button_change red center">流程</div>
		    				<div id="btnsend" class="button_change red center">报送</div>
		    				<div id="btndel" class="button_change red center">删除</div>
		    				<div id="btnList" class="button_change red center">列表</div>
		    				<div id="btnfind" class="button_change red center">查找</div>
		    				<div id="btnExport" class="button_change red center">导出</div>
		    				<div id="btnPrint" class="button_change red center">打印</div>
	    				</td>
					</tr>
				</table>
				<div>
					<form id="downloadId" class="downloadId" name="download" action="" method="post"></form>
				</div>
				<div id="dataDivWindow"  class="dataDivWindow" >
					<form  id="recordRequestForm" method="post">
					<table  cellpadding="0" cellspacing="0"class="spe_table dataTable">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
								<input id="norId" name="normativeFileId" type="hidden" /> 
								<input id="recordRequestId" name="recordRequestId" type="hidden" /> 
								<input id="nameId" name="recordRequestName" class="fileName" readonly="readonly"/></td>
								<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div></td>
							</tr>
							<tr>
								<td class="td-label">制定单位</td>
								<td class="td-data">
								<input id="decUnitId" name="decisionMakingUnit" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">制定单位负责人</td>
								<td class="td-data">
								<input id="decUnitLeaderId" name="decisionMakingUnitLeader" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">制定单位经办员</td>
								<td class="td-data">
								<input  id="decUnitClerkId" name="decisionMakingUnitClerk"  readonly="readonly"  class="td-3data" /></td>
							</tr>
							<tr>
								<td class="td-label">主起草单位</td>
								<td class="td-data">
								<input id="draUnitId" name="draftingUnit" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">主起草单位负责人</td>
								<td class="td-data">
								<input id="draUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">主起草单位经办员</td>
								<td class="td-data">
								<input  id="draUnitClerkId" name="draftingUnitClerk" class="td-3data"  readonly="readonly" /></td>
							</tr>
							<tr>
								<td class="td-label">备案审查单位</td>
								<td class="td-data">
								<input id="rUnitId" name="recordUnit" class="easyui-combotree"  data-options="valueField:'id',textField:'text',readonly:true" value="" /></td>
								<td class="td-label">备案审查单位负责人</td>
								<td class="td-data">
								<input id="rUnitLeaderId" name="recordUnitLeader" class="easyui-combobox"  data-options="valueField:'id',textField:'name'," value=""  /></td>
								<td class="td-label">备案审查单位经办员</td>
								<td class="td-data">
								<input id="rUnitClerkId" name="recordUnitClerk" class="easyui-combobox"  data-options="valueField:'id',textField:'name'," value="" /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">报送日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="rReqDateId" name="recordRequestDate" type="text"  /></td>
								<td class="td-label">联系电话</td>
								<td class="td-data">
								<input id="phoneId" name="phone" class="td-3data"/></td>
								<td class="td-label">优先级</td>
								<td class="td-data">
								<input id="priorityId" name="priority" readonly="readonly" class="td-3data" /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">规范性文件</td>
								<td colspan="6" class="td-upload-file">
								<div id="legalDocId"></div></td>
							</tr>
							<tr>
								<td class="td-label">起草说明</td>
								<td colspan="6" class="td-upload-file">
								<div id="draftingInstructionId" ></div></td>
							</tr>
							<tr>
								<td class="td-label">相关依据</td>
								<td colspan="6" class="td-upload-file">
								<div id="legalBasisId" ></div></td>
							</tr>
							<tr>
								<td class="td-label">备案报告</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="editor" type="text/plain"  class="td-editor" ></script>
										</center>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>
		</div>	
		<!-- 弹出页面 -->
		<div id="searchRecordRequest" title="备案报送查询" class="easyui-window"
			 	data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
			<table class="search-fileName">
				<tr>
					<td>文件名称:</td>
					<td><input id="searchNameId" name="searchName"size="60" /></td>
					<td><input type="button" id="btnSearch" value="查询" /></td>
				</tr>
			</table>
			<table id="searchDataGrid" class="easyui-datagrid" data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
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
					<td><input id="btnConfirm" type="button" value="确认" /></td>
					<td><input id="btnCancel" type="button" value="取消" /></td>
				</tr>
			</table>
	</div>
	
	<!-- 浏览文件 -->
	<div id="paper" title="文件浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false" maximizable="false"
		closed="true" resizable="false" style="padding:0px;">
	   <div id="matter" class="view-file"></div>
	</div>
	
	
</body>
</html>