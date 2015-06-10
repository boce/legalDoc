<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>立项</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/developApplication.css" />
<script type="text/javascript">
	var listId = "${param.id}";
	var legalBasisTypeData = {"LAW_AND_REG":"法律法规","STANDARD":"规范标准","RELATED_DOC":"相关文件","REFERENCE":"借鉴"};
	//记录当前要上传附件的div id
	var currentAttaId = "";
	//记录添加依据窗口是否打开
	var isLegalBasisOpen = false;
	$(document).keypress(function(e) {
		// 回车键事件
		if(e.which == 13 && isLegalBasisOpen) {
			append();
		}
	}); 
	$(function() {
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		if (listId != "") {
			load(parseInt(listId, 10), 'DEVELOPAPPLICATION');
		}
		$("#planDraftDateId").datebox({"editable":false});
		$("#planReviewDateId").datebox({"editable":false});
		$("#applyDateId").datebox({"editable":false});
		$("#nameId").bind("blur", function(){
			var name = $(this).val();
			if(name.indexOf("暂行") >= 0 || name.indexOf("试行") >= 0){
				$('#validDateId').combobox('setValue', 2);
			}else{
				$('#validDateId').combobox('setValue', 5);
			}
		});
		//初始化区域和组织机构下拉列表
		$("#applyOrgId").combotree({
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			required : true,
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "地区不能选择！");
					return false;
				}
			},
			onChange : function(newValue, oldValue) {
				$('#approvalLeaderId').combobox({
					url:'${pageContext.request.contextPath}/user/getSuperUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
				$('#applyLeaderId').combobox({
					url:'${pageContext.request.contextPath}/user/getUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
				$('#applyClerkId').combobox({
					url:'${pageContext.request.contextPath}/user/getUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
			}
		});
		if(listId == ""){
			init();
		}
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
		//初始化优先级
		$('#priorityId').combobox({
			url:'${pageContext.request.contextPath}/developApplication/getPriority.do',
			valueField : 'name',
			textField : 'title',
			required : true
		});

		$('#legalBasisTable').datagrid({    
		    singleSelect:"true",
		    rownumbers:"true",
		    onClickRow:onClickRow,
		    columns:[[
		        {field:'id',hidden:true},
		        {field:'name',title:'名称',width:200,align:"left",editor:"text",formatter:function(value, row){
		        	return "<span title=\""+value+"\">" + value + "</span>";
		        }},
		        {field:'basisInvalidDate',title:'失效日期',width:100,align:"left",editor:{type:"datebox",options:{required:true}}},
		        {field:'legalBasisType',title:'类型',width:100,align:"left",
			        formatter:function(value,row){
	                    return legalBasisTypeData[value];
	                },editor:{
			        	type:'combobox',
						options:{
							"editable":true,
			        		required:true,
							valueField:'name',
							textField:'title',
							url:'${pageContext.request.contextPath}/developApplication/getLegalBasisType.do'
						}
	                }
		        },
		        {field:'legalBasisAtta',title:'附件',width:300,align:"left",formatter:function(value, row){
			        	var atta = "<a title=\""+value+"\" class=\"attachment\" onclick=\"view(\'"+value+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + value + "</a>";
			        	return atta;
			        },editor:{
			        	type:'text',
			        	options:{
			        		readonly:true,
			        		required:true
			        	}
					}
		        }
		    ]],
		    toolbar : [{
				text:'添加',
				iconCls:'icon-add',
				handler:append
			},{
				text:'删除',
				iconCls:'icon-remove',
				handler:removeit
			},{
				text:'添加附件',
				iconCls:'icon-save',
				handler:function(){
					if(editIndex == undefined){
						showMsg("请先选择一行记录");
						return;
					}
					currentUploadFileName = "";
					$("#legalBasisUploadDiv").dialog("open");
				}
			}]
		});
		
		$("#btnAdd").click(function() {
			clear();
			init();
			setModifiable(true);
		});

		$("#involvedOrges").combotree({
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			multiple:true,
			cascadeCheck:false,
			width:700,
			onBeforeCheck : function(node, checked){
				if (checked && node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "地区不能选择！");
					return false;
				}
			}
		});
		$("#btnSave").click(function() {
			var norId = $("#norId").val();
			var id = $("#id").val();
			var name = $("#nameId").val();
			var applyOrg = $('#applyOrgId').combotree('getValue');
			var approvalLeader = $('#approvalLeaderId').combobox('getValue');
			var applyLeader = $('#applyLeaderId').combobox('getValue');
			var planDraftDate = $('#planDraftDateId').datebox('getValue');
			var planReviewDate = $('#planReviewDateId').datebox('getValue');
			var applyDate = $('#applyDateId').datebox('getValue');
			var validDate = $('#validDateId').combobox('getValue');
			var priority = $('#priorityId').combobox('getValue');
			var applyClerk = $('#applyClerkId').combobox('getValue');
			var legalBasis = "";
			var legalBasisAttachment = "";
			var legalBasisNoAtta = "";
			var rows = $('#legalBasisTable').datagrid("getRows");
			if(rows != null && rows.length > 0){
				legalBasis = JSON.stringify(rows);
				var len = rows.length;
				for(var i = 0; i < len; i++)
		        {
					if(rows[i].name == null || rows[i].name==""){
						continue;
					}
					if(rows[i].legalBasisAtta != null && rows[i].legalBasisAtta != ""){
						if(legalBasisAttachment == ""){
							legalBasisAttachment += rows[i].legalBasisAtta;
						}else{
							legalBasisAttachment += ";" + rows[i].legalBasisAtta;
						}
					}else{
						if(legalBasisNoAtta == ""){
							legalBasisNoAtta += rows[i].name;
						}else{
							legalBasisNoAtta += ";" + rows[i].name;
						}
					}
		        }
			}
			var necessityLegalAndRisk = $("#necLegalText").val();
			var necessityLegalAndRiskAttachment = genFileName("necLegalAttaDivId");
			var mainProblem = $("#mainProblemText").val();
			var mainProblemAttachment = genFileName("mainProblemAttaDivId");
			var planRegAndMea = $("#planRegAndMeaText").val();
			var planRegAndMeaAtta = genFileName("planRegAndMeaAttaDivId");
			var involvedOrges = $('#involvedOrges').combobox('getValues');
			var involvedOrgesStr = involvedOrges.join("\",\"");
			if(involvedOrgesStr != ""){
				involvedOrgesStr = "\"" + involvedOrgesStr + "\"";
			}
			var applyLeaderComment = $('#appLeaderComm').val();
			var approvalLeaderComment = $('#approvalLeaderComm').val();
			var remarks = $('#remarks').val();
			if(name == ""){
				$("#nameId").focus();
				showMsg("文件名称不能为空");
				return;
			}
			if(applyOrg == ""){
				$("#applyOrgId").focus();
				showMsg("申报单位不能为空");
				return;
			}
			if(approvalLeader == ""){
				$("#approvalLeaderId").focus();
				showMsg("批准申请领导不能为空");
				return;
			}
			if(applyLeader == ""){
				$("#applyLeaderId").focus();
				showMsg("申报单位负责人不能为空");
				return;
			}
			if(planDraftDate == ""){
				$("#planDraftDateId").focus();
				showMsg("拟起草日期不能为空");
				return;
			}
			if(planReviewDate == ""){
				$("#planReviewDateId").focus();
				showMsg("拟送审日期不能为空");
				return;
			}
			if(applyDate == ""){
				$("#applyDateId").focus();
				showMsg("申报日期不能为空");
				return;
			}
			if(validDate == ""){
				$("#validDateId").focus();
				showMsg("有效期不能为空");
				return;
			}
			if(priority == ""){
				$("#priorityId").focus();
				showMsg("优先级不能为空");
				return;
			}
			if(applyClerk == ""){
				$("#applyClerkId").focus();
				showMsg("申报经办员不能为空");
				return;
			}
			if(legalBasis == "" && legalBasisAttachment == ""){
				showMsg("制定依据不能为空");
				return;
			}
			if(necessityLegalAndRisk == "" && necessityLegalAndRiskAttachment == ""){
				showMsg("制定的必要性、合法性，以及社会稳定性风险评估不能为空");
				return;
			}
			if(mainProblem == "" && mainProblemAttachment == ""){
				showMsg("拟解决的主要问题不能为空");
				return;
			}
			if(planRegAndMea == "" && planRegAndMeaAtta == ""){
				showMsg("拟确定的制度或措施，以及可行性论证不能为空");
				return;
			}
			/*
			if(involvedOrges == ""){
				$("#involvedOrges").focus();
				showMsg("涉及的部门不能为空");
				return;
			}*/
			if(applyLeaderComment == ""){
				$("#appLeaderComm").focus();
				showMsg("申报单位负责人意见不能为空");
				return;
			}
			if(approvalLeaderComment == ""){
				$("#approvalLeaderComm").focus();
				showMsg("批准申请领导意见不能为空");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/developApplication/save.do",
				data : {
					"id" : id,
					"name" : name,
					"normativeFile.id" : norId,
					"applyOrg.id" : applyOrg,
					"approvalLeader.id" : approvalLeader,
					"applyLeader.id" : applyLeader,
					"planDraftDate":planDraftDate,
					"planReviewDate":planReviewDate,
					"applyDate":applyDate,
					"validDate":validDate,
					"priority":priority,
					"applyClerk.id":applyClerk,
					"legalBasis":legalBasis,
					"legalBasisAttachment":legalBasisAttachment,
					"legalBasisNoAtta":legalBasisNoAtta,
					"necessityLegalAndRisk":necessityLegalAndRisk,
					"necessityLegalAndRiskAttachment":necessityLegalAndRiskAttachment,
					"mainProblem":mainProblem,
					"mainProblemAttachment":mainProblemAttachment,
					"planRegulationMeasureAndFeasibility":planRegAndMea,
					"planRegulationMeasureAndFeasibilityAtta":planRegAndMeaAtta,
					"involvedOrges":involvedOrgesStr,
					"applyLeaderComment":applyLeaderComment,
					"approvalLeaderComment":approvalLeaderComment,
					"remarks":remarks,
					"tempFileId" : tempFileId
				},
				success : function(data) {
					if(data){
						showMsg(data.msg);
						if(data.success){
							$("#id").val(data.vo.id);
							$("#norId").val(data.vo.normativeFile.id);
							var legalBasises = data.vo.legalBasises;
							removeLegalBasisTable();
							if(legalBasises != null && legalBasises.length > 0){
								for(var i = 0; i < legalBasises.length; i++)
						        {
									legalBasises[i].basisInvalidDate = formatDate(legalBasises[i].basisInvalidDate);
									$('#legalBasisTable').datagrid('appendRow', legalBasises[i]);
						        }
							}
							accept();
						}
					}
				},
				error : function(data) {
					showMsg("保存出错,请重试!");
				}
			});

		});

		$("#btnDelete").click(function() {
			var id = $("#id").val();
			if(id == null || id == ""){
				showMsg("请先选择一个立项单");
				return;
			}
			$.messager.confirm('确认','确认想要删除吗？',function(r){    
			    if (r){    
					$.ajax({
						type : "POST",
						url : "${pageContext.request.contextPath}/developApplication/delete.do",
						data : {
							id : id
						},
						dataType:"json",
						success : function(data) {
							if(data){
								showMsg(data.msg);
								if(data.success){
									clear();
								}
							}
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							showMsg("删除失败!");
						}
					});
			    }    
			}); 

		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/developApplication/developApplicationList.wf";
		});

		//立项查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/developApplication/find.do',
				pageNumber : 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'DEVELOPAPPLICATION');
					$('#searchDevelopApplicationDraft').window('close'); //关闭查询窗口
				}
			});
			$('#searchDevelopApplicationDraft').window('open');

		});

		//关闭立项查找窗口
		$("#btnSearchCancel").click(function() {
			$('#searchDevelopApplicationDraft').window('close');
		});

		//查询立项
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/developApplication/find.do?name='+ fileName
			});
			$('#searchDataGrid').datagrid('reload');
		});

		//点击确认
		$("#btnSearchSubmit").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			if(selectRow == null){
				return;
			}
			var id = selectRow.id;
			load(id, 'DEVELOPAPPLICATION');
			$('#searchDevelopApplicationDraft').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/developApplication/print.do",
					data : {
						id : id
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
				showMsg("请选择立项单！");
			}

		});

		$("#btnExport").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$("#downloadId").attr(
						"action",
						"${pageContext.request.contextPath}/developApplication/export.do?id=" + id);
				$("#downloadId").submit();
			} else {
				showMsg("请选择立项单！");
			}
		});
		//附件上传
		$("#btnAddAtta").click(function(){
			$('#attaUploadDiv').dialog('open');
			$("#attaFileName").val("");
		});
		$("#attaFileUploadId").click(function(){
			attaFileUpload();
		});
		$("#attaBtn").click(function(){
			var addFile = $("#attaFileName").val();
			if(addFile != "" && !isFileExist(currentAttaId, addFile))
			{
				$("#"+currentAttaId).append("<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+addFile+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + addFile 
				+ "</a><img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>" + "</div>");
			}
			$('#attaUploadDiv').dialog('close');
		});
		$("#attaCloseBtn").click(function(){
			$('#attaUploadDiv').dialog('close');
		});
		$('#legalBasisDetailDiv').dialog({"onOpen":function(){
				isLegalBasisOpen = true;
			},"onClose":function(){
				isLegalBasisOpen = false;
			}
		});
		//打开制定依据详细信息窗口
		$("#btnAddLegalBasis").click(function(){
			$('#legalBasisDetailDiv').dialog('open');
		});
		//制定依据详细信息窗口-确定
		$("#legalBasisBtn").click(function(){
			accept();
			var rows = $('#legalBasisTable').datagrid('getRows');
			$("#legalBasisDivId").html(genLegalBasisAttaHtml(rows));
			$('#legalBasisDetailDiv').dialog('close');
		});
		//制定依据详细信息窗口-取消
		$("#legalBasisCloseBtn").click(function(){
			reject();
			$('#legalBasisDetailDiv').dialog('close');
		});
		
		//制定依据文件上传
		$("#legalFileUploadId").click(function(){
			legalFileUpload();
		});
		
		//制定依据文件上传-确定
		$("#legalBasisUploadBtn").click(function(){
			var ed = $('#legalBasisTable').datagrid('getEditor', {index:editIndex,field:'legalBasisAtta'});
			ed.target.val(currentUploadFileName);
			$('#legalBasisUploadDiv').dialog('close');
		});
		//制定依据文件上传-取消
		$("#legalBasisUploadCloseBtn").click(function(){
			$('#legalBasisUploadDiv').dialog('close');
		});
		
		
		$("#necLegalText").click(function(){
			currentAttaId = "necLegalAttaDivId";
		});
		
		$("#mainProblemText").click(function(){
			currentAttaId = "mainProblemAttaDivId";
		});
		
		$("#planRegAndMeaText").click(function(){
			currentAttaId = "planRegAndMeaAttaDivId";
		});
		
		$("#legalBasisDivId").click(function(){
			var isVisible = $("#btnAddLegalBasis").is(":visible");
			if(isVisible){
				$("#btnAddLegalBasis").click();
			}
		});
	});

	//格式化日期字符串
	function formatDate(time){
	    var datetime = new Date(time);
	    var year = datetime.getFullYear();
	    var month = (datetime.getMonth() + 1) < 10 ? "0" + (datetime.getMonth() + 1) : (datetime.getMonth() + 1);
	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	    return year + "-" + month + "-" + date;
	}
	//初始化申报单位、负责人、经办员、日期
	function init(){
		var id = $('#id').val();
		if (null == id || "" == id) {
			$.ajax({
				type : "post",
				url : "${pageContext.request.contextPath}/developApplication/init.do",
				dataType : 'json',
				success : function(data) {
					$('#applyOrgId').combotree('setValue',data.applyOrgId);
					if(data.applyLeaderId){
						$('#applyLeaderId').combobox('setValue', data.applyLeaderId);
					}
					$('#applyClerkId').combobox('setValue', data.applyClerkId);
					$('#applyDateId').datebox('setValue', data.applyDate);
				},
				error : function() {
					showMsg("加载出错,请重试!");
				}
			});
		}
	}
	
	//保存时获取上传文件名称，以“;”分隔
	function genFileName(fileId){
		var fileName = "";
		$("#" + fileId + " div").each(function(i){
			if(fileName == ""){
				fileName = $(this).text();
			}
			else{
				fileName += (";" + $(this).text());
			}
		});
		return fileName;
	}
	
	function clear() {
		tempFileId = "";
		$("#detailForm").form("clear");
		$("#legalBasisDivId").html("");
		$("#necLegalDivId").html("");
		$("#necLegalAttaDivId").html("");
		$("#mainProblemDivId").html("");
		$("#mainProblemAttaDivId").html("");
		$("#planRegAndMeaDivId").html("");
		$("#planRegAndMeaAttaDivId").html("");
		removeLegalBasisTable();
	}

	function removeLegalBasisTable(){
		var rows = $('#legalBasisTable').datagrid("getRows");
		if(rows != null && rows.length > 0){
			var len = rows.length;
			for(var i = 0; i < len; i++)
	        {
				var index = $('#legalBasisTable').datagrid('getRowIndex',rows[i]);
				$('#legalBasisTable').datagrid('deleteRow', index);
	        }
		}
		accept();
	}
	
	function view(fileName, evt) {
		var id = $('#id').val();
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/developApplication/viewFile.do",
			data : {
				id : id,
				fileName : fileName,
				tempFileId: tempFileId
			},
			dataType : 'html',
			success : function(data) {
				if(data == ""){
					showMsg("文件未找到!");
				}else{
					$('#viewContent').html(data);
					$('#viewFile').window('open'); //打开附件浏览
				}
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
		if(window.event){
			event.cancelBubble = true;
		}else if(evt){
			evt.stopPropagation();
		}
	}
	
	function load(id, searchType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/developApplication/load.do",
			data : {
				id : id,
				sreachType : searchType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$("#id").val(data.vo.id);
					$("#norId").val(data.vo.normativeFile.id);
					$("#nameId").val(data.vo.normativeFile.name);
					$('#applyOrgId').combotree({
						url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
						onLoadSuccess:function()
						{
							$('#applyOrgId').combotree('setValue',data.vo.applyOrg.id);
							$('#approvalLeaderId').combobox('setValue', data.vo.approvalLeader.id);
							$('#applyLeaderId').combobox('setValue', data.vo.applyLeader.id);
							$('#applyClerkId').combobox('setValue', data.vo.applyClerk.id);
						}
					});
					$('#planDraftDateId').datebox('setValue', data.vo.planDraftDate);
					$('#planReviewDateId').datebox('setValue', data.vo.planReviewDate);
					$('#applyDateId').datebox('setValue', data.vo.applyDate);
					$('#validDateId').combobox('setValue', data.vo.validDate);
					$('#priorityId').combobox('setValue', data.vo.priority);
					var legalBasises = data.vo.legalBasises;
					$("#legalBasisDivId").html(genLegalBasisAttaHtml(legalBasises));
					removeLegalBasisTable();
					if(legalBasises != null && legalBasises.length > 0){
						for(var i = 0, len = legalBasises.length; i < len; i++)
				        {
							$('#legalBasisTable').datagrid('appendRow', legalBasises[i]);
				        }
					}
					accept();
					
					$("#necLegalText").val(data.vo.necessityLegalAndRisk);
					var necLegalRiskAtta = genAttaHtml(data.vo.necessityLegalAndRiskAttachment, data.modifiable);
					$("#necLegalAttaDivId").html(necLegalRiskAtta);
					
					$("#mainProblemText").val(data.vo.mainProblem);
					var mainProblemAtta = genAttaHtml(data.vo.mainProblemAttachment, data.modifiable);
					$("#mainProblemAttaDivId").html(mainProblemAtta);
					
					$("#planRegAndMeaText").val(data.vo.planRegulationMeasureAndFeasibility);
					var planRegAndMeaAtta = genAttaHtml(data.vo.planRegulationMeasureAndFeasibilityAtta, data.modifiable);
					$("#planRegAndMeaAttaDivId").html(planRegAndMeaAtta);
					
					var involvedOrges = data.vo.involvedOrges;
					if(involvedOrges != null)
					{
						involvedOrges = involvedOrges.replace(/\"/g, "");
						$('#involvedOrges').combotree('setValues',involvedOrges.split(","));
					}
					$('#appLeaderComm').val(data.vo.applyLeaderComment);
					$('#approvalLeaderComm').val(data.vo.approvalLeaderComment);
					$('#remarks').val(data.vo.remarks);
					setModifiable(data.modifiable);
				}
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
	}
	//设置是否可以修改,true表示可以修改,false表示不可修改
	function setModifiable(modifiable){
		$('#nameId').prop("readonly", !modifiable);
		$('#applyOrgId').combotree('readonly', !modifiable);
		$('#approvalLeaderId').combobox({
			onLoadSuccess:function(){
				$('#approvalLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#applyLeaderId').combobox({
			onLoadSuccess:function(){
				$('#applyLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#planDraftDateId').datebox('readonly', !modifiable);
		$('#planReviewDateId').datebox('readonly', !modifiable);
		$('#applyDateId').datebox('readonly', !modifiable);
		$('#priorityId').combobox('readonly', !modifiable);
		$('#applyClerkId').combobox({
			onLoadSuccess:function(){
				$('#applyClerkId').combobox('readonly', !modifiable);
			}
		});
		$('#necLegalText').prop("readonly", !modifiable);
		$('#mainProblemText').prop("readonly", !modifiable);
		$('#planRegAndMeaText').prop("readonly", !modifiable);
		/* $('#legalDetailInput').prop("disabled", !modifiable);
		$('#necLegalUploadInput').prop("disabled", !modifiable);
		$('#mainProblemUploadInput').prop("disabled", !modifiable);
		$('#planRegAndMeaUploadInput').prop("disabled", !modifiable); */
		$('#involvedOrges').combobox('readonly', !modifiable);
		$('#appLeaderComm').prop("readonly", !modifiable);
		$('#approvalLeaderComm').prop("readonly", !modifiable);
		$('#remarks').prop("readonly", !modifiable);
		showDiv("btnSave", modifiable);
		showDiv("btnDelete", modifiable);
		showDiv("btnSubmit", modifiable);
		showDiv("btnApprove", modifiable);
		showDiv("btnUnApprove", modifiable);
		showDiv("btnAddLegalBasis", modifiable);
		showDiv("btnAddAtta", modifiable);
	}
	//获得制定依据显示div，legalBasises为json数组
	function genLegalBasisAttaHtml(legalBasises){
		var legalBasisStr = "";
		if(legalBasises != null && legalBasises.length > 0){
			for(var i=0,len=legalBasises.length; i<len; i++){
				var legalBasis = legalBasises[i];
				if(legalBasis.name == null || legalBasis.name == ""){
					continue;
				}
				if(legalBasis.legalBasisAtta != null && legalBasis.legalBasisAtta != ""){
					legalBasisStr += "<div><a class=\"attachment\" onclick=\"view(\'"+legalBasis.legalBasisAtta+"\',event);\" href=\"#1\" style=\"cursor: pointer;\">《" + legalBasis.name + "》</a></div>";
				}else{
					legalBasisStr += "<div>《" + legalBasis.name + "》</div>";
				}
			}
		}
		return legalBasisStr;
	}
	//初始化时根据后台的附件文件名称生成页面显示的div
	function genAttaHtml(atta, modifiable)
	{
		var attaHtml = ""; 
		if(atta && atta != "")
		{
			var legalBasisAttas = atta.split(";");
			for(var i=0, len=legalBasisAttas.length; i<len; i++)
			{
				attaHtml += "<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+legalBasisAttas[i]+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + legalBasisAttas[i] + "</a>";
				if(modifiable){
					attaHtml += "<img class=\"imgDelete\" onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>";
				}
				attaHtml += "</div>";
			}
		}
		return attaHtml;
	}
	//临时文件目录唯一标识
	var tempFileId = "";
	var currentUploadFileName = "";
	//附件上传
	function attaFileUpload(){
		var fileName = $('#attaFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				$("#attaFileName").val(data.fileName);
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "attaFile", success);
	}
	//制定依据上传
	function legalFileUpload(){
		var fileName = $('#legalBaseFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				currentUploadFileName = data.fileName;
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "legalBaseFile", success);
	}
	
	function isFileExist(divId, fileName){
		var flag = false;
		$("#" + divId + " div").each(function(i){
			if($(this).text() == fileName){
				flag = true;
				return false;
			}
		});
		return flag;
	}
	
	function deleteFile(img){
		$(img).parent().remove();
	}
	//文件上传
	function ajaxFileUpload(fileName, fileId, success) {
		var id = $('#id').val();
		var param = {id : id , fileName : fileName};
		if(tempFileId != ""){
			param.tempFileId = tempFileId;
		}
        $.ajaxFileUpload({
             url :'${pageContext.request.contextPath}/developApplication/uploadFile.do',
             data : param,
             secureuri :false,
             fileElementId :fileId,
             dataType : 'content',
             success : function (data){
				var start = data.indexOf(">");  
				if(start != -1) {
					var end = data.indexOf("<", start + 1);
					if(end != -1) {
						data = data.substring(start + 1, end);
					}  
				}
				data = eval("(" + data + ")");
				if(success){
					success(data);
				}
             },
             error : function (data, textStatus, errorThrown){
             	showMsg("上传失败!");
             }
		});
	}
	
	var editIndex = undefined;
	function endEditing(){
		if (editIndex == undefined){return true;}
		if ($('#legalBasisTable').datagrid('validateRow', editIndex)){
			$('#legalBasisTable').datagrid('endEdit', editIndex);
			editIndex = undefined;
			return true;
		} else {
			return false;
		}
	}
	function onClickRow(index){
		if (editIndex != index){
			if (endEditing()){
				$('#legalBasisTable').datagrid('selectRow', index).datagrid('beginEdit', index);
				editIndex = index;
			} else {
				$('#legalBasisTable').datagrid('selectRow', editIndex);
			}
		}
	}
	function append(){
		if (endEditing()){
			$('#legalBasisTable').datagrid('appendRow',{});
			editIndex = $('#legalBasisTable').datagrid('getRows').length-1;
			$('#legalBasisTable').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
		}
	}
	function removeit(){
		if (editIndex == undefined){return;}
		$('#legalBasisTable').datagrid('cancelEdit', editIndex).datagrid('deleteRow', editIndex);
		editIndex = undefined;
	}
	function accept(){
        if (endEditing()){
            $('#legalBasisTable').datagrid('acceptChanges');
        }
    }
	function reject(){
        $('#legalBasisTable').datagrid('rejectChanges');
        editIndex = undefined;
    }
</script>
</head>
<body>
		<div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
				<table  class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
							<div id="btnAdd" class="button_change red center">新增</div>
							<div id="btnSave" class="button_change red center">保存</div> 
							<div id="btnDelete" class="button_change red center">删除</div> 
							<div id="btnSubmit" class="button_change red center">提交</div> 
							<div id="btnApprove" class="button_change red center">审核</div>
							<div id="btnUnApprove" class="button_change red center">弃审</div> 
							<div id="btnFlow" class="button_change red center">流程</div> 
							<div id="btnList" class="button_change red center">列表</div> 
							<div id="btnFind" class="button_change red center">查找</div> 
							<div id="btnExport" class="button_change red center">导出</div> 
							<div id="btnPrint" class="button_change red center">打印</div>
							<div id="btnAddLegalBasis" class="button_change red center">添加依据</div>
							<div id="btnAddAtta" class="button_change red center">添加附件</div>
						</td>
					</tr>
				</table>
				<form id="downloadId" class="downloadId" name="download" action="" method="post"></form>
				<div id="dataDivWindow" class="dataDivWindow">
				<form id="detailForm">
					<table  cellpadding="0" cellspacing="0" class="spe_table  dataTable">

						<tbody>
							<tr>
								<td class="td-label-first" >文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="norId" name="normativeFileId" type="hidden" /> 
									<input id="id" name="developApplicationId" type="hidden" /> 
									<input id="nameId" name="developApplicationName" class="fileName inputHeight"/>
								</td>
								<td class="td-spac"></td>
								
							</tr>
							<tr>
								<td class="td-label-first">申报单位</td>
								<td class="td-data" >
									<input id="applyOrgId" name="applyOrg"  class="td-3data-dev"  />
								</td>
								<td class="td-label" >批准申请领导</td>
								<td class="td-data">
									<input id="approvalLeaderId" name="approvalLeader" class="td-3data-dev" />
								</td>
								<td class="td-label">申报单位负责人</td>
								<td class="td-data">
								<input id="applyLeaderId" name="applyLeader" class="td-3data-dev" />
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label-first">拟起草日期</td>
								<td class="td-data">
									<input id="planDraftDateId" name="planDraftDate" class="easyui-datebox  td-3data-dev" />
								</td>
								<td class="td-label">合法性审查拟送审日期</td>
								<td class="td-data">
								<input id="planReviewDateId" name="planReviewDate" class="easyui-datebox  td-3data-dev" />
								</td>
								<td class="td-label">申报日期</td>
								<td class="td-data">
								<input id="applyDateId" name="applyDate" class="easyui-datebox  td-3data-dev"  />
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label-first">有效期</td>
								<td class="td-data">
								<input id="validDateId" name="validDate" class="td-3data-dev" />
								</td>
								<td class="td-label">优先级别</td>
								<td class="td-data">
								<input id="priorityId" name="priority" class="td-3data-dev"/>
								</td>
								<td class="td-label">申报经办员</td>
								<td class="td-data">
								<input id="applyClerkId" name="applyClerk" class="td-3data-dev" />
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label-atta" >制定依据</td>
								<td  class="td-atta" valign="top" colspan="6">
									<div id="legalBasisDivId" class="td-label-legalbasic"></div>
								</td>
							</tr>
							<tr>
								<td class="td-label-atta">制定的必要性、合法性，以及社会稳定性风险评估</td>
								<td colspan="6" class="td-atta">
									<div  style="height: 50px;">
										<textarea id="necLegalText" class="spe_table_textarea"></textarea>
										<div id="necLegalAttaDivId" class="attaDivId" ></div>
									</div>
								</td>
							</tr>
							<tr>
								<td class="td-label-atta">拟解决的主要问题</td>
								<td colspan="6" class="td-atta">
									<div class="td-label-legalbasic">
										<textarea id="mainProblemText"></textarea>
										<div id="mainProblemAttaDivId" class="attaDivId" ></div>
									</div>
								</td>
							</tr>
							<tr>
								<td class="td-label-atta">拟确定的制度或措施，以及可行性论证</td>
								<td colspan="6" class="td-atta">
									<div class="td-label-legalbasic">
										<textarea id="planRegAndMeaText"></textarea>
										<div id="planRegAndMeaAttaDivId" class="attaDivId"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td class="td-label-first">涉及的部门</td>
								<td colspan="6" style="text-align: left;">
									<input id="involvedOrges" name="involvedOrges"/>
								</td>
								
							</tr>
							<tr>
								<td class="td-label-atta">申报单位负责人意见</td>
								<td colspan="6" class="td-atta">
									<textarea id="appLeaderComm" name="appLeaderComm" "></textarea>
								</td>
								
							</tr>
							<tr>
								<td class="td-label-atta">批准申请领导意见</td>
								<td colspan="6" class="td-atta">
									<textarea id="approvalLeaderComm" name="approvalLeaderComm"></textarea>
								</td>
								
							</tr>
							<tr>
								<td class="td-label-atta">备注</td>
								<td colspan="6" class="td-atta">
									<textarea id="remarks" name="remarks" ></textarea>
								</td>
								
							</tr>
						</tbody>
					</table>
				</form>
				</div>
			</div>

		</div>
	<!-- 立项查询弹出层 -->
	<div id="searchDevelopApplicationDraft" title="立项查询" class="easyui-window"  
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
					<th data-options="field:'name',width:'535'">文件名</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>

		<table class="search-button">
			<tr>
				<td width="570px"></td>
				<td><input id="btnSearchSubmit" type="button" value="确认" /></td>
				<td><input id="btnSearchCancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
	
	<!-- 制定依据添加弹出层 -->
	<div id="legalBasisDetailDiv" class="easyui-dialog" title="制定依据"   
	        data-options="iconCls:'icon-query',buttons:'#legalBasis-buttons',resizable:false,closed:true,closable:false,modal:true,width:750,height:300">
	    <table id="legalBasisTable" style="height:227px;"></table>
		<div id="legalBasis-buttons" class="dialog-button">
			<a id="legalBasisBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="legalBasisCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">取消</a>
		</div>
	</div>
	<!-- 制定依据附件添加弹出层 -->
	<div id="legalBasisUploadDiv" class="easyui-dialog" title="附件上传"    
	        data-options="iconCls:'icon-query',buttons:'#legalBasisUpload-buttons',resizable:false,closed:true,modal:true,width:400,height:150">
	    <form id="legalBasisForm" name="legalBasisForm" method="post" action="" enctype="multipart/form-data">
	    	<table>
		    	<tr>
	    			<td>上传附件：</td>
	    			<td><input name="file" id="legalBaseFile" type="file" size="20" /></td>
	    			<td><input id="legalFileUploadId" type="button" value="上传" /></td>
	    		</tr>
    		</table>
		</form>
		<div id="legalBasisUpload-buttons" class="dialog-button">
			<a id="legalBasisUploadBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="legalBasisUploadCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">取消</a>
		</div>
	</div>
	<!-- 附件上传弹出层 -->
	<div id="attaUploadDiv" class="easyui-dialog" title="附件上传" "   
	        data-options="iconCls:'icon-query',buttons:'#atta-buttons',resizable:false,closed:true,modal:true,width:400,height:150">
	    <form id="attaForm" name="attaForm" method="post" action="" enctype="multipart/form-data">
	    	<input id="attaFileName" name="attaFileName" type="hidden"/>
	    	<table class="table" border="0" align="center">
	    		<tr>
	    			<td>上传文件：</td>
	    			<td><input name="file" id="attaFile" type="file" size="20" /></td>
	    			<td><input id="attaFileUploadId" type="button" value="上传" /></td>
	    		</tr>
	    	</table>
		</form>
		<div id="atta-buttons" class="dialog-button">
			<a id="attaBtn" href="javascript:void(0)" class="easyui-linkbutton"">确定</a>
			<a id="attaCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
	
	<!-- 弹出浏览附件页面 -->
	<div id="viewFile" title="附件浏览" class="easyui-window" collapsible="false" inline="false" minimizable="false"
		maximizable="false" closed="true" resizable="false" style="padding: 0px;">
		<div id="viewContent"  class="view-file" ></div>
	</div>
</body>
</html>