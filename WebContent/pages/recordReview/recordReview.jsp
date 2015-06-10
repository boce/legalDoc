<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link  rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/recordReview.css"/>
<title>备案审查</title>
<script type="text/javascript">

	var editor = null;
	var legalDoc = '';
	var legalBasis = '';
	var draftingInstruction = '';
	var recordReport = '';
	var init = true;
	$(function(){
		
		$("#registerDateId").combo({"editable":false});
		$("#rRevDateId").combo({"editable":false});
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
			load(parseInt(listId, 10), 'RECREVIEW');
		}

	$("#recordUnitId").combotree({
		url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
		onBeforeSelect : function(node) {
			if (node.attributes.nodeType == orgNodeType[1]) {
				$.messager.alert("提示", "不能选择地区！");
				return false;
			}
		}
	}); 
	
	$("#btnOpen").click(function() {
		openFrame('RECORD_REQUEST');
	});
	
	$("#btnadd").click(function(){
		clear();
		setModifiable(true);
	});
	
	$("#btnsave").click(function(){
		var revId =  $("#recordReviewId").val();
		var norId = $('#norId').val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var rRevDateId = $("#rRevDateId").datebox('getValue');
		var reviewResultId = $("#reviewResultId").combobox('getValue');
		var legalDocContent = legalDoc;
		var recordReportContent = recordReport;
		var decUnitOop = $("input[name='decUnitOop']").prop("checked");
		var decProcedureOop = $("input[name='decProcedureOop']").prop("checked");
		var contentOop = $("input[name='contentOop']").prop("checked");
		var decTechHasDefects = $("input[name='decTechHasDefects']").prop("checked");
		var others = $("input[name='others']").prop("checked");
		var reviewOpinionPaper = editor.getContent();
		
		if(rRevDateId == "" ){
			$("#rRevDateId").focus();
			showMsg("请选择审查日期");
			return;
		}
		if(reviewResultId == "" ){
			$("#reviewResultId").focus();
			showMsg("请选择审查结果");
			return;
		}
		if(reviewOpinionPaper == "" ){
			$("#editor").focus();
			showMsg("请填写审查意见");
			return;
		}
		$.ajax({
			type: "POST",
			  url: "${pageContext.request.contextPath}/recordReview/save.do",
			  data:{id:revId,"normativeFile.id":norId,recordReviewDate:rRevDateId,reviewResult:reviewResultId,legalDoc:legalDocContent,
				  recordReport:recordReportContent,decUnitOop:decUnitOop,decProcedureOop:decProcedureOop,
				  contentOop:contentOop,decTechHasDefects:decTechHasDefects,others:others,reviewOpinionPaper:reviewOpinionPaper
				  		},
			  dataType : 'json',
			  success : function(data){
				  showMsg(data.msg);
				  if(data.success && data.vo){
						$("#recordReviewId").val(data.vo.id);
					}
			  },
			  error : function(data){
				  showMsg("保存出错,请重试!");
			  }
			});
	});
	
	$("#btnList").click(
			function() {
				location.href = "${pageContext.request.contextPath}/recordReview/recordReviewList.wf";
			});
	
	$("#btndel").click(function(){
		var id = $('#recordReviewId').val();
		if(id == ""){
			 showMsg("请先选择一个备案审查单!");
			 return;
		}
		$.ajax({
		  type: "POST",
		  url: "${pageContext.request.contextPath}/recordReview/delete.do",
		  data : {id : id},
		  dataType : 'json',
		  success : function(data) {
			  if(data.message){
				  showMsg(data.message);
			  }
			 if (data.msg == "success") {
				 showMsg("删除备案审查成功!");
				 clear();
			 } else {
				 showMsg("删除备案审查失败!");
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
							"${pageContext.request.contextPath}/recordReview/export.do?norId="+ norId);
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
						url : "${pageContext.request.contextPath}/recordReview/print.do",
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
	
	//打开登记窗口
	$("#btnregister").click(function() {
		var id = $("#recordReviewId").val();
		if(id == ""){
			showMsg("请选择备案审查单！");
			return;
		}
		$('#registerRecordReview').window('open');
		load(id, 'RECREVIEW');
	});
	//关闭登记窗口
	$("#btnclos").click(function() {
		$('#registerRecordReview').window('close');
	});
	//确定登记
	$("#btncf").click(function() {
		var revId =  $("#rReviewId").val();
		var nId = $('#norId').val();
		var registerDateId = $("#registerDateId").datebox('getValue');
		var registerCodeId = $("#registerCodeId").val();
		$.ajax({
			type: "POST",
			  url: "${pageContext.request.contextPath}/recordReview/register.do",
			  data:{id:revId,"normativeFile.id":nId,registerDate:registerDateId,registerCode:registerCodeId},
			  dataType : 'json',
			  success : function(data) {
				if (data) {
					 showMsg("备案登记成功!");
					 $("#rReviewId").val(data.id);
					
				 } else {
					 showMsg("备案登记失败!");
				 }
			  },
			  error : function(data){
				  showMsg("备案登记出错,请重试!");
			  }
			});
		
		$('#registerRecordReview').window('close'); //关闭查询窗口
	});
	
	//打开报备窗口
	$("#btnsend").click(function() {
		var id = $("#recordReviewId").val();
		if(id == ""){
			showMsg("请选择备案审查单！");
			return;
		}
		$('#sendRecordReview').window('open');
		load(id, 'RECREVIEW');
	});
	//关闭报备窗口
	$("#btncan").click(function() {
		$('#sendRecordReview').window('close');
	});
	//确定报备
	$("#btndetermine").click(function() {
		var rcId = $("#rcId").val();
		if(rcId == ""){
			showMsg("请先登记再报备！");
			return;
		}
		var revId =  $("#reReviewId").val();
		var nmId =  $("#norId").val();
		var recordUnitId = $('#recordUnitId').combobox('getValue');
		$.ajax({
			type: "POST",
			  url: "${pageContext.request.contextPath}/recordReview/send.do",
			  data:{id:revId,"normativeFile.id":nmId,"recordUnit.id":recordUnitId},
			  dataType : 'json',
			  success : function(data){
				if (data) {
					 showMsg("报备成功!");
					 $("#reReviewId").val(data.id);
				 } else {
					 showMsg("报备失败!");
				 }
			  },
			  error : function(data){
				  showMsg("报备出错,请重试!");
			  }
			});
		
		$('#sendRecordReview').window('close'); //关闭查询窗口
	});
	
	
	//查找备案报送
	$("#btnfind").click(function() {
		$('#searchDataGrid').datagrid({
			url : '${pageContext.request.contextPath}/recordReview/find.do',
			pageNumber: 1,
			onDblClickRow: function (rowIndex, rowData) {
				clear();
				var id = rowData.id;
				load(id, 'RECREVIEW');
				$('#searchRecordReview').window('close'); //关闭查询窗口
			}
		});
		$('#searchRecordReview').window('open');

	});

	//关闭备案报送查找窗口
	$("#btnCancel").click(function() {
		$('#searchRecordReview').window('close');
	});

	//通过文件名查询备案报送
	$("#btnSearch").click(function() {
		var fileName = $('#searchNameId').val();
		$('#searchDataGrid').datagrid({
			url : '${pageContext.request.contextPath}/recordReview/find.do?name='+ fileName
		});
		$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
	});

	//点击确认
	$("#btnConfirm").click(function() {
		var selectRow = $('#searchDataGrid').datagrid('getSelected');
		var id = selectRow.id;
		load(id, 'RECREVIEW');
		$('#searchRecordReview').window('close'); //关闭查询窗口
	});
	
});

	function matter(fileName,fileType) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/recordReview/gainContent.do",
				  data : {
					  norId : norId,
					  fileName:fileName,
					  fileType:fileType
					  },
				  dataType : 'html',
				  success : function(data){
					  $('#matter').html('');
					  $('#matter').html(data);
					  $('#papper').window('open');	//打开送审稿浏览
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
	$('#recordReviewForm').form('clear');
	if(!init){
		editor.setContent("");
	}
	legalDoc = '';
	$('#legalDocId').empty();
	
	legalBasis = '';
	$('#legalBasisId').empty();
	
	draftingInstruction = '';
	$('#draftingInstructionId ').empty();
	
	recordReport = '';
	$("#recordReportId").empty();
		
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
		url : "${pageContext.request.contextPath}/recordReview/load.do",
		data : {
			id : id,
			sreachType :  sreachType
		},
		dataType : 'json',
		success : function(data) {
			if (data != null) {
				$("#recordReviewId").val(data.vo.id);
				$("#rReviewId").val(data.vo.id);
				$("#reReviewId").val(data.vo.id);
				$("#norId").val(data.vo.normativeFile.id);
				$("#nameId").val(data.vo.normativeFile.name);
				$("#reNameId").val(data.vo.normativeFile.name);
				$("#rnameId").val(data.vo.normativeFile.name);
				$("#nId").val(data.vo.normativeFile.name);
				$("#decUnitId").val(data.vo.normativeFile.decUnit.text);
				$("#decUnitLeaderId").val(data.vo.normativeFile.decUnitLeader.name);
				$("#decUnitClerkId").val(data.vo.normativeFile.decUnitClerk.name);
				$("#rUnitId").val(data.vo.normativeFile.recRevUnit.text);
				$("#rUnitLeaderId").val(data.vo.normativeFile.recRevUnitLeader.name);
				$("#rUnitClerkId").val(data.vo.normativeFile.recRevUnitClerk.name);
				$("#reviewResultId").combobox('setValue',data.vo.reviewResult);
				$("#registerCodeId").val(data.vo.registerCode);
				$("#rcId").val(data.vo.registerCode);
				
				if(data.vo.id == null){
				$("#rRevDateId").datebox('setValue',formatterDate(new Date()));
				}else{
				$("#rRevDateId").datebox('setValue',data.vo.recordReviewDate);
				$("#registerDateId").datebox('setValue',data.vo.registerDate);	
				}
				
				if (data.vo.recordUnit != null) {
					$("#recordUnitId").combotree({
						url : '${pageContext.request.contextPath}/org/getOrgShortReference.do',
						onLoadSuccess : function(node){
							$("#recordUnitId").combotree('setValue',data.vo.recordUnit.id);
						}
					});
				}
				
				
				$("input[name='decUnitOop']").prop("checked",data.vo.decUnitOop);
				$("input[name='decProcedureOop']").prop("checked",data.vo.decProcedureOop);
				$("input[name='contentOop']").prop("checked",data.vo.contentOop);
				$("input[name='decTechHasDefects']").prop("checked",data.vo.decTechHasDefects);
				$("input[name='others']").prop("checked",data.vo.others);
				
				if (data.vo.recordReport != null && data.vo.recordReport.length > 0) {
					legalDoc = data.vo.recordReport;
					$('#recordReportId').empty();
					var strs = new Array();
					strs = data.vo.recordReport.split(";");
					$.each(strs,function(index, tx) {
						$("#recordReportId").append(
								'<a class="rdp" href="#" style="cursor: pointer;">'+ tx+ '</a>');
						$(".rdp").bind('click',function(){  
							matter(tx,"RECORDREQUEST");
					    });
					});
				}
				
				if (data.vo.legalDoc != null && data.vo.legalDoc.length > 0) {
					legalDoc = data.vo.legalDoc;
					$('#legalDocId').empty();
					var strs = new Array();
					strs = data.vo.legalDoc.split(";");
					$.each(strs,function(index, tx) {
						$("#legalDocId").append(
								'<a class="leg" href="#" style="cursor: pointer;">'+ tx+ '</a>');
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
						$("#draftingInstructionId").append(
								'<a class="dra" href="#" style="cursor: pointer;">'+ tx+ '</a>');
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
						$("#legalBasisId").append('<div><a class="legb'+ index +'" href="#" style="cursor: pointer;">'+ tx+ '</a></div>');
						$(".legb" +  index).bind('click',function(){
							matter(tx,"LEGALBASIS");
						});
	   				});
				}
				
				if (data.vo.legalDoc != null && data.vo.legalDoc.length > 0) {
					legalDoc = data.vo.legalDoc;
					$('#lgId').empty();
					var strs = new Array();
					strs = data.vo.legalDoc.split(";");
					$.each(strs,function(index, tx) {
						$("#lgId").append(
								'<a class="lg" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
						$(".lg").bind('click',function(){  
							matter(tx,"LEGALDOC");
					    });
					});
				}
				
				if (data.vo.draftingInstruction != null && data.vo.draftingInstruction.length > 0) {
					draftingInstruction = data.vo.draftingInstruction;
					$('#dfId').empty();
					var strs = new Array();
					strs = data.vo.draftingInstruction.split(";");
					$.each(strs,function(index, tx) {
										$("#dfId").append(
												'<a class="df" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
										$(".df").bind('click',function(){  
											matter(tx,"INSTRUCTION");
									    });
									});
				}
				
				if (data.vo.legalBasis != null && data.vo.legalBasis.length > 0) {
					legalBasis = data.vo.legalBasis;
					$('#lbId').empty();
					var strs = new Array();
					strs = data.vo.legalBasis.split(";");
					$.each(strs,function(index, tx) {
						$("#lbId").append(
								'<a class="lb'+ index +'" href="#" style="cursor: pointer;"><div>'+ tx+ '</div></a>');
									$(".lb" +  index).bind('click',function(){  
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
							  editor.setContent(data.vo.reviewOpinionPaper);
						  });
					  }else{
						  editor.setContent(data.vo.reviewOpinionPaper);
					  }
				  }
					
				setModifiable(data.modifiable);
			}
		},
		error : function(data) {
			showMsg("加载出错,请重试!");
		}
	});
}
function setModifiable(modifiable){
	$("#registerDateId").datebox("readonly",!modifiable);
	$("#rRevDateId").datebox("readonly",!modifiable);
	$("#reviewResultId").prop("readonly",!modifiable);
	$("#recordUnitId").prop("readonly",!modifiable);
	showDiv("btnsave", modifiable);
	showDiv("btndel", modifiable);
	showDiv("btnsubmit", modifiable);
	showDiv("btnapprove", modifiable);
	showDiv("btnunApprove", modifiable);
	showDiv("btnsend", modifiable);
	showDiv("btnregister", modifiable);
	showDiv("btnpublish", modifiable);
	
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
function gainFileHead(filecontent,filename){
	$.ajax({
		  type: "POST",
		  url: contextPath+"/gainFileHead/gainFileHeadName.do",
		  dataType : 'json',
		  success : function(data){
			  filehead ="<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
				   "<p style=\"text-align:center;line-height:54px\"><span style=\"font-size:54px;font-family:宋体;color:red;font-weight:bold; letter-spacing:-0\">"+data.districtName+data.organizationName+"文件</span></p>"+
				   "<hr style=\"-webkit-user-select: none;display:block;width:649px;margin:0px;\" color=\"red\"/>"+
				   "<p style=\"text-align:right\"><span style=\"font-size:18px;font-family:宋体\">x府法函〔xxxx〕xx号</span></p>"+
				   "<p style=\"line-height:18px\"><span style=\"font-size:18px;font-family:宋体;font-variant:small-caps\">&nbsp;</span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">规范性文件备案审查意见书</span></p>";
			  
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
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
				<table  class="table">
				<tr>
						<td id="btnTools" style="text-align: left;">
		    				<div id="btnadd" class="button_change red center">新增</div>
		    				<div id="btnsave" class="button_change red center">保存</div>
		    				<div id="btnsubmit" class="button_change red center">提交</div>
		    				<div id="btnapprove" class="button_change red center">审核</div>
		    				<div id="btnunApprove" class="button_change red center">弃审</div>
		    				<div id="btnflow" class="button_change red center">流程</div>
		    				<div id="btndel" class="button_change red center">删除</div>
		    				<div id="btnList" class="button_change red center">列表</div>
		    				<div id="btnregister" class="button_change red center">登记</div>
		    				<div id="btnpublish" class="button_change red center">公布</div>
		    				<div id="btnsend" class="button_change red center">报备</div>
		    				<div id="btnfind" class="button_change red center">查找</div>
		    				<div id="btnExport" class="button_change red center">导出</div>
		    				<div id="btnprint" class="button_change red center">打印</div>
	    			</td>
					</tr>
				</table>
				<div>
					<form id="downloadId" class="downloadId" name="download" action="" method="post"></form>
				</div>
				<div id="dataDivWindow" class="dataDivWindow" >
					<form id="recordReviewForm" method="post">
					<table cellpadding="0" cellspacing="0" class="spe_table">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="norId" name="normativeFileId" type="hidden" /> 
									<input id="recordReviewId" name="recordReviewId" type="hidden" /> 
									<input id="nameId" name="recordReviewName" class="fileName"  readonly="readonly"/>
								</td>
								<td class="td-btn-find">
									<div id="btnOpen" class="button_change red center">查询</div>
								</td>
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
								<td></td>
							</tr>
							<tr>
								<td class="td-label">备案审查单位</td>
								<td class="td-data">
								<input id="rUnitId" name="recordUnit" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">备案审查单位负责人</td>
								<td class="td-data">
								<input id="rUnitLeaderId" name="recordUnitLeader" readonly="readonly" class="td-3data" /></td>
								<td class="td-label">备案审查单位经办员</td>
								<td class="td-data">
								<input  id="rUnitClerkId" name="recordUnitClerk" class="td-3data"  readonly="readonly" /></td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">审查日期</td>
								<td class="td-data">
								<input  class="easyui-datebox"  id="rRevDateId" name="recordReviewDate" type="text"  />
								</td>
								<td colspan="5" class="td-fileName"></td>
							</tr>
							<tr>
								<td class="td-label">备案报告</td>
								<td colspan="6" class="td-upload-file">
								<div id="recordReportId"></div></td>
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
								<td class="td-label">存在的问题类型</td>
								<td colspan="6" class="td-upload-file">
								<input type="checkbox" name="decUnitOop" /> 制定主体不合规
								<input type="checkbox" name="decProcedureOop" /> 制定程序部合规
								<input type="checkbox" name="contentOop" /> 文件内容部合法
								<input type="checkbox" name="decTechHasDefects" /> 制定技术有缺陷
								<input type="checkbox" name="others"  />其他 </td>
							</tr>
							<tr>
								<td class="td-label">审查结果</td>
								<td class="td-data">
								<input id="reviewResultId" class="easyui-combobox" data-options="
													 panelHeight :100,
													valueField: 'value',
													textField: 'label',
													data: [{
														label: '合格',
														value: 'QUALIFIED'
													},{
														label: '自行纠正',
														value: 'SELFCORRECTION'
													},{
														label: '撤销',
														value: 'REVOKE'
													}]"/></td>
								<td colspan="5" class="td-fileName"></td>
							</tr>
							<tr class="editorTr">
								<td class="td-label">备案审查意见书</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="editor" type="text/plain" class="td-editor" ></script>
									</center>
								</td>
							</tr>
						</tbody>
					</table>
					</form>
				</div>
			</div>
		</div>	
		<!-- 弹出备案审查查找页面 -->
		<div id="searchRecordReview" title="备案审查查询" class="easyui-window"
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
	<div id="papper" title="规范性文件浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false" maximizable="false"
		closed="true" resizable="false">
	   <div id="matter" class="view-file" ></div>
	</div>
	
	<!-- 弹出登记页面 -->
		<div id="registerRecordReview" title="登记" class="easyui-window"  
				data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, width: 600, height:150, padding: 0,modal:true">
			<table  class="table  table-recordReview-record">
				<tr>
					<td class="td-recordReview-label">文件名称</td>
					<td colspan="3" class="td-fileName">
					<input id="rReviewId" name="rReviewId" type="hidden" /> 
					<input id="reNameId" name="recordReviewName" class="td-3data" readonly="readonly"/></td>
				</tr>
				<tr>
					<td class="td-recordReview-label">备案登记日期</td>
					<td class="td-recordReview-data">
					<input  class="easyui-datebox"  id="registerDateId" name="registerDate" type="text"  /></td>
					<td class="td-recordReview-label">备案号</td>
					<td class="td-recordReview-data">
					<input id="registerCodeId" name="registerCode"  class="td-3data" /></td>
				</tr>
				<tr>
					<td class="td-recordReview-data" colspan="4"><input id="btncf" type="button"  value="确定" /><input id="btnclos" type="button"  value="取消" /></td>
				</tr>
		</table>
	</div>
	
	<!-- 弹出报备页面 -->
		<div id="sendRecordReview" title="报备" class="easyui-window" 
				data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, width: 600, height:210, padding: 0,modal:true">
			<table  class="table table-recordReview-submit">
				<tr>
					<td class="td-recordReview-label">文件名称</td>
					<td colspan="3" class="td-fileName">
					<input id="reReviewId" name="reReviewId" type="hidden" /> 
					<input id="rnameId" name="recordReviewName" class="td-3data" readonly="readonly"/></td>
				</tr>
				<tr>
					<td class="td-recordReview-label">备案单位</td>
					<td class="td-recordReview-data">
					<input id="recordUnitId" name="recordUnit" class="easyui-combotree"  data-options="valueField:'id',textField:'text'," value="" /></td>
					<td class="td-recordReview-label">备案号</td>
					<td class="td-recordReview-data">
					<input id="rcId" name="registerCode" readonly="readonly" class="td-3data" /></td>
				</tr>
				<tr>
							<td class="td-label">规范性文件</td>
							<td colspan="3" class="td-upload-file">
							<div id="lgId"></div></td>
						</tr>
						<tr>
							<td class="td-label">起草说明</td>
							<td colspan="3" class="td-upload-file">
							<div id="dfId" ></div></td>
						</tr>
						<tr>
							<td class="td-label">相关依据</td>
							<td colspan="3" class="td-upload-file">
							<div id="lbId" ></div></td>
						</tr>
				<tr>
					<td colspan="4" class="td-recordReview-data" ><input id="btndetermine" type="button" value="确定" /><input id="btncan" type="button" value="取消" /></td>
				</tr>
		</table>
	</div>
	
</body>
</html>