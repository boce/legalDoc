<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/deliberationRequest.css"/>
<title>审议报请</title>
<script type="text/javascript">
var editor = null;
var requestComments = '';
var protocol = '';
var reviewComments = '';
var draftingInstruction = '';
var unionDratUnitIndex = 0;
var init = true;
	
	$(function(){
		
	//显示的页面按钮
	displayBtn();
	//适应分辨率高度
	autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
	editor = UE.getEditor("editor");
	editor.addListener("ready", function() {
		  init = false;
	});
	
	$("#requestDateId").combo({"editable":false});
	
	//List页面初始化
	var listId = "${param.id}";
	if (listId != "") {
		load(parseInt(listId, 10), 'DELREQUEST');
	}
	
	$("#btnOpen").click(function() {
		openFrame('LEGAL_REVIEW_MODIFY');
	});
	
	$("#btnadd").click(function(){
		clear();
		setModifiable(true);
		removeDraftUnion();
	});	
	
	$("#btnsave").click(function(){
		var delId =  $("#delId").val();
		var norId = $('#norId').val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var delUnitId = $("#delUnitId").val();
		var requestDateId = $("#requestDateId").datebox('getValue');
		var protocolContent = protocol;
		var request = requestComments;
		var review = reviewComments;
		var rcontent = '';
		rcontent = editor.getContent();

		var content = editor.getContent();
		var reg = new RegExp("${pageContext.request.contextPath}",["g"]);
		content = content.replace(reg, "{contextPath}");
		
		if(delUnitId == ""){
			$("#delUnitId").focus();
			showMsg("请输入审议单位");
			return;
		}
		if( rcontent == ""){
			$("#editor").focus();
			showMsg("请填写审议请示");
			return;
		}
		
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/deliberationRequest/save.do",
			  data:{id:delId,"normativeFile.id":norId,
				  		deliberationUnit:delUnitId,requestDate:requestDateId,reviewInstruction:rcontent,
				  		reviewComments:review,requestComments:request,protocol:protocolContent},
			  dataType : 'json',
			  success : function(data){
				  showMsg(data.msg);
				  if(data.success && data.vo){
						$("#delId").val(data.vo.id);
					}
			  },
			  error : function(data){
				  showMsg("保存出错,请重试!");
			  }
			});
	});
	
	$("#btnList").click(function() {
		location.href = "${pageContext.request.contextPath}/deliberationRequest/deliberationRequestList.wf";
	});
	
	$("#btndel").click(function(){
		var id = $('#delId').val();
		if(id == ""){
			 showMsg("请先选择一个审议报请单!");
			 return;
		}
		$.ajax({
		  type: "POST",
		  url: "${pageContext.request.contextPath}/deliberationRequest/delete.do",
		  data : {id : id},
		  dataType : 'json',
		  success : function(data) {
			  if(data.message){
				  showMsg(data.message);
			  }else if (data.msg == "success") {
						 showMsg("删除审议报请成功!");
						 clear();
					 	  } else {
						 	showMsg("删除审议报请失败!");
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
							"${pageContext.request.contextPath}/deliberationRequest/export.do?norId="+ norId);
					$("#downloadId").submit();
				} else {
					showMsg("请选择文件！");
				}
			});
	
	$("#btnprint").click(function() {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
						type : "post",
						url : "${pageContext.request.contextPath}/deliberationRequest/print.do",
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
	
	$("#btnfind").click(function (){
		//设置查询的datagrid的数据集url
		$('#delDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/deliberationRequest/find.do',
			pageNumber: 1,
			onDblClickRow: function (rowIndex, rowData) {
				var nId = rowData.id;
				load(nId,"DELREQUEST");
				$('#findWindow').window('close'); //关闭查询窗口
			}
		});
		$('#findWindow').window('open'); //打开审议报请查询窗口
	});
	
});

//关闭window窗口
function closeW() {
	$('#findWindow').window('close');
}

//查找审议报请
function findDeliberationRequest() {
	var fileName = $('#findFileNameId').val();
	$('#delDataGridId').datagrid({
						url : '${pageContext.request.contextPath}/deliberationRequest/find.do?name='+ fileName
					});
	$('#delDataGridId').datagrid('reload'); //加载数据,实现文件的过滤
}

//点击确认
function comfirm() {
	var selectRow = $('#delDataGridId').datagrid('getSelected');
	var nId = selectRow.id;
	load(nId,"DELREQUEST");
	$('#findWindow').window('close'); //关闭查询窗口
}

//得到当前日期
formatterDate = function(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
	+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-' + day;
	};

//添加联合起草单位tr
function addUnionDraUnit(unionDrtUnit, unionDrtUnitLeader){
	unionDratUnitIndex++;
	var unionDraHtml = "<tr id=\"unionDrtUnit"+unionDratUnitIndex+"\">"
						+"<td style=\"width: 120px; height: 25px\">联合起草单位</td>"
						+"<td style=\"width: 136px; height: 25px; text-align: left\">"
						+"<input readonly=\"readonly\" style=\"border: 0;; margin: 0; padding: 0;\" value=\""+unionDrtUnit+"\"/>"
						+"</td>"
						+"<td style=\"width: 120px; height: 25px;\">联合起草单位负责人</td>"
						+"<td style=\"width: 136px; height: 25px; text-align: left\">"
						+"<input readonly=\"readonly\" style=\"border: 0; margin: 0; padding: 0;\" value=\""+unionDrtUnitLeader+"\"/>"
						+"</td>"
						+"<td></td>"
						+"<td></td>"
						+"<td></td>"
						+"</tr>";
	return unionDraHtml;
	}

//移除联合起草tr
function removeDraftUnion(){
	if(unionDratUnitIndex > 0){
		for(var i=1; i<=unionDratUnitIndex; i++){
			$("#unionDrtUnit" + i).remove();
		}
	}
	unionDratUnitIndex = 0;
}	
	
function matter(fileName,fileType) {
	var norId = $('#norId').val();
	if (null != norId && "" != norId) {
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/deliberationRequest/gainContent.do",
			  data : {
				  norId : norId,
				  fileName:fileName,
				  fileType:fileType
				  },
			  dataType : 'html',
			  success : function(data){
				  $('#matter').html('');
				  $('#matter').html(data);
				  $('#paper').window('open');	
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
	 $('#deliberationRequestForm').form('clear');
	 
		 if(!init){
				editor.setContent("");
			}
		
		requestComments = '';
		$('#requestCommentsId').empty();
		
		protocol = '';
		$('#protocolId').empty();
		
		reviewComments = '';
		$('#reviewCommentsId').empty();
		
		draftingInstruction = '';
		$('#draftingInstructionId').empty();
		
}

function load(id,sreachType) {
	$.ajax({
		type : "POST",
		url : "${pageContext.request.contextPath}/deliberationRequest/load.do",
		data : {
			id : id,
			sreachType :  sreachType
		},
		dataType : 'json',
		success : function(data) {
			if (data != null) {
				$("#delId").val(data.vo.id);
				$("#norId").val(data.vo.normativeFile.id);
				$("#DeliberationRequestNameId").val(data.vo.normativeFile.name);
				$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
				$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
				$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
				
				var unionDrtUnitName = data.vo.unionDrtUnitName;
				if (unionDrtUnitName != null && unionDrtUnitName != "") {
					var unionDraftUnits = unionDrtUnitName.split(",");
					var unionDraftUnitLeaders = data.vo.unionDrtUnitLeaderName.split(",");
					var unionDrtUnitHtml = "";
					for(var i=0; i<unionDraftUnits.length; i++){
						unionDrtUnitHtml += addUnionDraUnit(unionDraftUnits[i], unionDraftUnitLeaders[i]);
					}
					$("#drtUnitTr").after(unionDrtUnitHtml);
				}
				
				$("#delUnitId").val(data.vo.deliberationUnit);
				
				if(data.vo.id == null){
					$("#requestDateId").datebox('setValue',formatterDate(new Date()));
				}else{
					$("#requestDateId").datebox('setValue',data.vo.requestDate);
				}
				
				if (data.vo.protocol != null && data.vo.protocol.length > 0) {
					protocol = data.vo.protocol;
					$('#protocolId').empty();
					var strs = new Array();
					strs = data.vo.protocol.split(";");
					$.each(strs,function(index, tx) {
						$("#protocolId").append('<a class="pro" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
						$(".pro").bind('click',function(){  
							matter(tx,"PROTOCOL");
					    });
					});
				}
				
				if (data.vo.draftingInstruction != null && data.vo.draftingInstruction.length > 0) {
					draftingInstruction = data.vo.draftingInstruction;
					$('#draftingInstructionId').empty();
					var strs = new Array();
					strs = data.vo.draftingInstruction.split(";");
					$.each(strs,function(index, tx) {
						$("#draftingInstructionId").append('<a class="dra" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
						$(".dra").bind('click',function(){  
							matter(tx,"INSTRUCTION");
					    });
					});
				}
				
				if (data.vo.requestComments != null && data.vo.requestComments.length > 0) {
					requestComments = data.vo.requestComments;
					$('#requestCommentsId').empty();
					var strs = new Array();
					strs = data.vo.requestComments.split(";");
					$.each(strs,function(index, tx) {
						$("#requestCommentsId").append('<a class="req'+ index +'" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
									$(".req" + index).bind('click',function(){  
										matter(tx,"ADOPTCOMMENT");
					    });
	   		 });
								
				}
				if (data.vo.reviewComments != null && data.vo.reviewComments.length > 0) {
					reviewComments = data.vo.reviewComments;
					$('#reviewCommentsId').empty();
					var strs = new Array();
					strs = data.vo.reviewComments.split(";");
					$.each(strs,function(index, tx) {
						$("#reviewCommentsId").append('<a class="rev" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
									$(".rev").bind('click',function(){  
										matter(tx,"REVIEWCOMMENT");
					    });
							});
				}
				
				var filename = data.vo.normativeFile.name;
				if(sreachType == 'NORFILE'){
					  //得到文件头
					  gainFileHead("",filename);
				  }else{
					  var content = data.vo.reviewInstruction;
					  content = content.replace(/\{contextPath\}/g, "${pageContext.request.contextPath}");
					  if(init){
						  editor.addListener("ready", function() {
							  editor.setContent(content);
						  });
					  }else{
						  editor.setContent(content);
					  }
				  }
				setModifiable(data.modifiable);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			showMsg("加载出错,请重试!");
		}
	});
}

function setModifiable(modifiable){
	   $("#requestDateId").datebox("readonly",!modifiable);
	   $("#delUnitId").prop("readonly",!modifiable);
	   
	   showDiv("btnsave", modifiable);
		showDiv("btndel", modifiable);
		showDiv("btnsubmit", modifiable);
		showDiv("btnunApprove", modifiable);
		showDiv("btnapprove", modifiable);
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
  				   "<p style=\"text-align:center;line-height:54px\"><span style=\"font-size:54px;font-family:宋体;font-weight:bold; color:red;letter-spacing:-0\">"+data.districtName+data.organizationName+"文件</span></p>"+
  				   "<hr style=\"-webkit-user-select: none;display:block;width:649px;margin:0px;\" color=\"red\"/>"+
  				   "<p style=\"text-align:right\"><span style=\"font-size:18px;font-family:宋体\">xxx函〔xxxx〕xx号</span></p>"+
				   "<p style=\"line-height:18px\"><span style=\"font-size:24px;font-family:宋体;font-variant:small-caps\">&nbsp;</span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于《"+filename+"》的审议请示</span></p>";
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
		<div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false,width: 1200, height: 610" >
				<table  class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
	    					<div class="button_change red center" id="btnadd">新 增</div>
	    					<div class="button_change red center" id="btnsave">保存</div>
	    					<div class="button_change red center" id="btnsubmit">提交</div>
	    					<div class="button_change red center" id="btnapprove">审核</div>
	    					<div class="button_change red center" id="btnunApprove">弃审</div>
	    					<div class="button_change red center" id="btnflow">流程</div>
	    					<div class="button_change red center" id="btndel">删除</div>
	    					<div class="button_change red center" id="btnList">列表</div>
	    					<div class="button_change red center" id="btnfind">查找</div>
	    					<div class="button_change red center" id="btnExport">导出</div>
	    					<div class="button_change red center" id="btnprint">打印</div>
	    				</td>
					</tr>
				</table>
				
				<div><form id="downloadId" class="downloadId" name="download" action="" method="post"></form></div>
				<div id="dataDivWindow"  class="dataDivWindow" >
					<form id="deliberationRequestForm" method="post">
					<table class="spe_table"  cellpadding="0" cellspacing="0">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
								<input id="norId" name="normativeFile.id" type="hidden" /> 
								<input id="delId" name="deliberationRequestId" type="hidden" /> 
								<input id="DeliberationRequestNameId" name="DeliberationRequestName" readonly="readonly"  class="fileName" /></td>
								<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div></td>
							</tr>
							<tr id="drtUnitTr">
								<td class="td-label">主起草单位</td>
								<td class="td-data">
								<input id="draftingUnitId" name="draftingUnit" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">主起草单位负责人</td>
								<td class="td-data">
								<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">主起草单位经办员</td>
								<td class="td-data">
								<input  id="draftingUnitClerkId" name="draftingUnitClerk" class="td-3data"  readonly="readonly" /></td>
								<td></td>
							</tr>
							
							<tr>
								<td class="td-label">审议单位</td>
								<td class="td-data">
								<input id="delUnitId" name="delUnit"  class="td-3data" /></td>
								<td class="td-label">报请日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="requestDateId" name="requestDate" type="text"  /></td>
								<td></td>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">草案</td>
								<td colspan="6" class="td-upload-file">
								<div id="protocolId"></div></td>
							</tr>
							<tr>
								<td class="td-label">起草说明</td>
								<td colspan="6" class="td-upload-file">
								<div id="draftingInstructionId" ></div></td>
							</tr>
							<tr>
								<td class="td-label">征求意见的相关材料</td>
								<td colspan="6" class="td-upload-file">
								<div id="requestCommentsId" ></div></td>
							</tr>
							<tr>
								<td class="td-label">法律审查意见书</td>
								<td colspan="6" class="td-upload-file">
								<div id="reviewCommentsId" ></div></td>
							</tr>
							<tr class="editorTr">
								<td class="td-label">审议请示</td>
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
		<div id="findWindow" title="审议报请查询" class="easyui-window"
			 data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
			<table class="search-fileName">
				<tr>
					<td>文件名称:</td>
					<td><input id="findFileNameId" name="deliberationRequest"size="60" /></td>
					<td><input type="button" onclick="findDeliberationRequest()" value="查询" /></td>
				</tr>
			</table>
			<table id="delDataGridId" class="easyui-datagrid" 
					data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
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
					<td><input id="confirm" onclick="comfirm()"type="button" value="确认" /></td>
					<td><input id="close" type="button" onclick="closeW();"value="取消" /></td>
				</tr>
		</table>
	</div>
	
	<!-- 打开文件内容 -->
	<div id="paper" title="文件浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false" maximizable="false"
		closed="true" resizable="false">
	   <div id="matter" class="view-file"></div>
	</div>
	
</body>
</html>