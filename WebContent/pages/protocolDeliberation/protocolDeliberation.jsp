<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/protocolDeliberation.css"/>
<title>草案审议</title>
<script type="text/javascript">
var editor = null;
var requestComments = '';
var protocol = '';
var reviewComments = '';
var draftingInstruction = '';
var reviewInstruction = '';
var init = true;
$(function(){
	
	//显示的页面按钮
	displayBtn();
	//适应分辨率高度
	autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
	//初始化editor
	editor = UE.getEditor('editor');
	editor.addListener("ready", function() {
		  init = false;
	});

	$("#deliberationDateId").combo({"editable":false});
	//List页面初始化
	var listId = "${param.id}";
	if (listId != "") {
		load(parseInt(listId, 10), 'PDEL');
	}
	
	$("#btnOpen").click(function() {
		openFrame('DELIBERATION_REQUEST');
	});
	
	$("#btnadd").click(function(){
		clear();
		setModifiable(true);
	});
	
	$("#btnsave").click(function(){
		var pd =  $("#pid").val();
		var norId = $('#norId').val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var delUnitId = $("#delUnitId").val();
		var deliberationDateId = $("#deliberationDateId").datebox('getValue');;
		var protocolContent = protocol;
		var request = requestComments;
		var review = reviewComments;
		var rInstruction = reviewInstruction;
		var content = '';
		content = editor.getContent();
		if( content == null){
			$("#editor").focus();
			showMsg("请填写审议意见");
			return;
		}
		var isNeedModifyVal = $("input[name=isNeedModify]:checked").val();
		if(typeof(isNeedModifyVal) == "undefined" || isNeedModifyVal == ""){
			showMsg("请选择是否需要修改草案！");
			return;
		}
		var isNeedModify = isNeedModifyVal == "0" ? true : false;
		$.ajax({
			type: "POST",
			url: "${pageContext.request.contextPath}/protocolDeliberation/save.do",
			data:{id:pd,'normativeFile.id':norId,deliberationUnit:delUnitId,
			 		deliberationDate:deliberationDateId,deliberationComment:content,
			 		reviewComments:review,requestComments:request,protocol:protocolContent,
			 		deliberationComment:content,reviewInstruction:rInstruction, isNeedModify:isNeedModify},
			dataType : 'json',
			success : function(data){
				showMsg(data.msg);
				if(data.success && data.vo){
					$("#pid").val(data.vo.id);
				}
			},
			error : function(data)
			{
				showMsg("保存出错,请重试!");
			}
		});
	});
	
	$("#btndel").click(function(){
		var pid = $('#pid').val();
		if(pid == ""){
			 showMsg("请先选择一个草案审议单!");
			 return;
		}
			$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/protocolDeliberation/delete.do",
			  data : {id : pid},
			  dataType : 'json',
			  success : function(data){
				  if(data.message){
					  showMsg(data.message);
				  }else if (data.msg == "success") {
					 showMsg("删除草案审议成功!");
					 clear();
				 } else {
					 showMsg("删除草案审议失败!");
				 }
			  },
			  error : function(data)
			  {
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
							"${pageContext.request.contextPath}/protocolDeliberation/export.do?norId="+ norId);
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
						url : "${pageContext.request.contextPath}/protocolDeliberation/print.do",
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
				location.href = "${pageContext.request.contextPath}/protocolDeliberation/protocolDeliberationList.wf";
			});
	
	$("#btnfind").click(function (){
		//设置查询的datagrid的数据集url
		$('#delDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/protocolDeliberation/find.do',
			pageNumber: 1,
			onDblClickRow: function (rowIndex, rowData) {
				var nId = rowData.id;
				load(nId, "PDEL");
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
						url : '${pageContext.request.contextPath}/protocolDeliberation/find.do?name='+ fileName
					});
	$('#delDataGridId').datagrid('reload'); //加载数据,实现文件的过滤
}

//点击确认
function comfirm() {
	var selectRow = $('#delDataGridId').datagrid('getSelected');
	var nId = selectRow.id;
	load(nId,"PDEL");
	$('#findWindow').window('close'); //关闭查询窗口
}

function matter(fileName,fileType) {
	var norId = $('#norId').val();
	if (null != norId && "" != norId) {
		$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/protocolDeliberation/gainContent.do",
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
	$('#protocolDeliberationForm').form('clear');
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
	
	reviewInstruction = '';
	$('#reviewInstructionId').empty();
	
	$("input[name=isNeedModify]:checked").prop("checked", false);
}

function load(id,sreachType) {
	$.ajax({
		type : "POST",
		url : "${pageContext.request.contextPath}/protocolDeliberation/load.do",
		data : {
			id : id,
			sreachType :  sreachType
		},
		dataType : 'json',
		success : function(data) {
			clear();
			if (data != null) {
				$('#pid').val(data.vo.id);
				$("#norId").val(data.vo.normativeFile.id);
				$("#protocolDeliberationNameId").val(data.vo.normativeFile.name);
				$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
				$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
				$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
				$("#delUnitId").val(data.vo.normativeFile.delUnit);
				
				if( data.vo.id == null){
				$("#deliberationDateId").datebox('setValue',formatterDate(new Date()));
				}else{
				$("#deliberationDateId").datebox('setValue',data.vo.deliberationDate);					
				}
				
				if (data.vo.protocol != null && data.vo.protocol.length > 0) {
					protocol = data.vo.protocol;
					$('#protocolId').empty();
					var strs = new Array();
					strs = data.vo.protocol.split(";");
					$.each(strs,function(index, tx) {
										$("#protocolId").append(
												'<a class="pro" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
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
										$("#draftingInstructionId").append(
												'<a class="dra" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
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
				
				if (data.vo.reviewInstruction != null && data.vo.reviewInstruction.length > 0) {
					reviewInstruction = data.vo.reviewInstruction;
					$('#reviewInstructionId').empty();
					var strs = data.vo.reviewInstruction.split(";");
					$.each(strs,function(index, tx) {
						$("#reviewInstructionId").append('<a class="rin" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
						$(".rin").bind('click',function(){  
							matter(tx,"REVIEWINSTRUCTION");
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
							  editor.setContent(data.vo.deliberationComment);
						  });
					  }else{
						  editor.setContent(data.vo.deliberationComment);
					  }
				}
				if(data.vo.isNeedModify != null){
					var needModifyValue = data.vo.isNeedModify ? 0 : 1;
					$("input[name=isNeedModify][value=" + needModifyValue + "]").prop("checked", true);
				}
				setModifiable(data.modifiable);
			}
		},
		error : function(data) {
			showMsg("加载出错,请重试!");
		}
	});
}


//得到当前日期
formatterDate = function(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
	+ (date.getMonth() + 1);
	return date.getFullYear() + '-' + month + '-' + day;
	};

function setModifiable(modifiable){
	$("#deliberationDateId").datebox("readonly",!modifiable);
	showDiv("btnsave", modifiable);
	showDiv("btndel", modifiable);
	$("input[name=isNeedModify]").prop("disabled", !modifiable);
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
			  				   "<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
							   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
							   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于《"+filename+"》的审议意见</span></p>";
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
			<div data-options="region:'center',border:false,split:false, width: 1200,  height: 630" >
				<table class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
		    				<div id="btnadd" class="button_change red center">新增</div>
		    				<div id="btnsave" class="button_change red center">保存</div>
		    				<div id="btndel" class="button_change red center">删除</div>
		    				<div id="btnList" class="button_change red center">列表</div>
		    				<div id="btnfind" class="button_change red center">查找</div>
		    				<div id="btnExport" class="button_change red center">导出</div>
		    				<div id="btnprint" class="button_change red center">打印</div>
	    			</td>
					</tr>
				</table>
				<div>
					<form id="downloadId" class="downloadId" name="download" action="" method="post" ></form>
				</div>
				<div id="dataDivWindow"  class="dataDivWindow" >
					<form id="protocolDeliberationForm" method="post">
					<table  cellpadding="0" cellspacing="0" class="spe_table  dataTable">
						<tbody>
							<tr>
								<td  class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
								<input id="norId" name="normativeFile.id" type="hidden" /> 
								<input id="pid" name="protocolDeliberationId" type="hidden" /> 
								<input id="protocolDeliberationNameId" name="protocolDeliberationName" readonly="readonly"  class="fileName" /></td>
								<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div></td>
							</tr>
							<tr>
								<td  class="td-label">主起草单位</td>
								<td class="td-data">
								<input id="draftingUnitId" name="draftingUnit" readonly="readonly" class="td-3data"  /></td>
								<td  class="td-label">主起草单位负责人</td>
								<td class="td-data">
								<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data"  /></td>
								<td  class="td-label">主起草单位经办员</td>
								<td class="td-data">
								<input  id="draftingUnitClerkId" name="draftingUnitClerk" class="td-3data"   readonly="readonly" /></td>
							</tr>
							<tr>
								<td  class="td-label">审议单位</td>
								<td class="td-data">
								<input id="delUnitId" name="delUnit"  class="td-3data"  readonly="readonly" /></td>
								<td class="td-label">审议日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="deliberationDateId" name="deliberationDate"  type="text"  /></td>
							</tr>
							<tr>
								<td  class="td-label">审议请示</td>
								<td colspan="6" class="td-upload-file">
								<div  id="reviewInstructionId"></div></td>
							</tr>
							<tr>
								<td  class="td-label">草案</td>
								<td colspan="6" class="td-upload-file">
								<div id="protocolId"></div></td>
							</tr>
							<tr>
								<td  class="td-label">起草说明</td>
								<td colspan="6" class="td-upload-file">
								<div id="draftingInstructionId" ></div></td>
							</tr>
							<tr>
								<td  class="td-label">征求意见的相关材料</td>
								<td colspan="6" class="td-upload-file">
								<div id="requestCommentsId" ></div></td>
							</tr>
							<tr>
								<td  class="td-label">法律审查意见书</td>
								<td colspan="6" class="td-upload-file">
								<div id="reviewCommentsId" ></div></td>
							</tr>
							<tr>
								<td  class="td-label">审议意见</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="editor" type="text/plain" class="td-editor"></script>
									</center>
								</td>
							</tr>
							<tr>
								<td class="td-label">是否需要修改草案</td>
								<td colspan="6" class="td-upload-file">
									<input name="isNeedModify" type="radio" value="0"/>是
									<input name="isNeedModify" type="radio" value="1"/>否
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>
		</div>	
		
		<!-- 打开文件内容 -->
	<div id="paper" title="文件浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false" maximizable="false"
		closed="true" resizable="false">
	   <div id="matter" class="view-file" ></div>
	</div>
		
		
		<!-- 弹出页面 -->
		<div id="findWindow" title="草案审议查询" class="easyui-window"
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
</body>
</html>