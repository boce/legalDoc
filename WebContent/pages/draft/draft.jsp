<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>起草</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/legaldoc/draft.css" />
<script type="text/javascript">
	//extend the 'equals' rule     
	$.extend($.fn.validatebox.defaults.rules, {   
		compareDate : {   
	        validator: function(value,param){
	            return value >= $(param[0]).datebox("getValue");   
	        },   
	        message: '结束日期不能小于开始日期'  
	    }   
	});
	var editor = null;
	var init = true;
	var draftUnionIndex = 0;//联合起草单位计数

	$(function() {
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		//初始化editor
		editor = UE.getEditor('editor');
		editor.addListener("ready", function() {
			  init = false;
		});
		
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'DRAFT');
		}
		
		$("#draftingStartDate").datebox({"editable":false});
		$("#draftingEndDate").datebox({"editable":false});
		//初始化区域和组织机构下拉列表
		$("#draftingUnitId").combotree({
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			required : true,
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "不能选择地区！");
					return false;
				}
			},
			onChange : function(newValue, oldValue) {
				$('#draftingUnitLeaderId').combobox({
					url:'${pageContext.request.contextPath}/user/getUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
				$('#draftingUnitClerkId').combobox({
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
		
		//初始化起草方式
		$('#draftMode').combobox({
			url:'${pageContext.request.contextPath}/org/getDraftMode.do',
			valueField : 'name',
			textField : 'title',
			required : true,
			onChange:function(newValue, oldValue){
				if(newValue == "INDEPENDENT_DRAFTING")
				{
					removeAllDraftUnion();
					$("#btnAddDraftUnion").hide();
				}else if(newValue == "UNION_DRAFTING"){
					$("#btnAddDraftUnion").show();
				}else{
					$("#btnAddDraftUnion").hide();
				}
			}
		});
		
		//定稿
		$("#btnConfirm").click(function() {
			var fileEditor = editor.getContent();
			fileEditor = fileEditor.replace('(草稿)','(征求意见稿)');
			editor.setContent(fileEditor);
			save(true);
		});
		$("#btnOpen").click(function() {
			openFrame('SETUP');
		});

		$("#btnAdd").click(function() {
			clear();
			setModifiable(true);
		});

		$("#btnSave").click(function() {
			save(false);
		});

		$("#btnDelete").click(function() {
			var id = $("#id").val();
			if(id == null || id == ""){
				showMsg("请先选择一个起草单");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/draft/delete.do",
				data : {
					id : id
				},
				dataType:"json",
				success : function(data) {
					showMsg(data.msg);
					if(data.success){
						clear();
					}
				},
				error : function(data) {
					showMsg("加载出错,请重试!");
				}
			});

		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/draft/draftList.wf";
		});

		//起草单查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url: '${pageContext.request.contextPath}/draft/find.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'DRAFT');
					$('#searchDraft').window('close'); //关闭查询窗口
				}
			});
			$('#searchDraft').window('open');

		});

		//关闭起草单查找窗口
		$("#btnCancel").click(function() {
			$('#searchDraft').window('close');
		});

		//查询起草单
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid').datagrid('load', {name: fileName});  
		});

		//点击确认
		$("#btnSubmit").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			var id = selectRow.id;
			load(id, 'DRAFT');
			$('#searchDraft').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/draft/print.do",
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
				showMsg("请选择起草单！");
			}

		});

		$("#btnExport").click(function() {
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$("#downloadId").attr("action", "${pageContext.request.contextPath}/draft/export.do?norId=" + norId);
				$("#downloadId").submit();
			} else {
				showMsg("请选择起草单！");
			}
		});
		
		//添加联合起草单位
		$("#btnAddDraftUnion").click(function() {
			addDraftUnion();
		});
		
	});

	//添加联合起草单位tr
	function addDraftUnion(){
		draftUnionIndex ++;
		var draftUnionHtml = "<tr id=\"draftUnion"+draftUnionIndex+"\" style=\"height: 28px\" >"
							+"<td>联合起草单位</td>"
							+"<td style=\"text-align: left\">"
							+"<input id=\"draftUnionId"+draftUnionIndex+"\" name=\"draftUnionName"+draftUnionIndex+"\" class=\"form_input\" />"
							+"</td>"
							+"<td>联合起草单位负责人</td>"
							+"<td style=\"text-align: left\">"
							+"<input id=\"draftUnionLeaderId"+draftUnionIndex+"\" name=\"draftUnionLeader"+draftUnionIndex+"\" class=\"form_input\" />"
							+"</td>"
							+"<td colspan=\"2\" style=\"text-align: left;\">"
							+"<input class=\"unionDeleteBtn\" type=\"button\" value=\"删除\" onclick=\"removeUnionDraft(\'draftUnion"+draftUnionIndex+"\')\"/>"
							+"</td>"
							+"<td></td>"
							+"</tr>";
		if(draftUnionIndex == 1){
			$("#draftModeTr").after(draftUnionHtml);
		}else{
			$("#draftUnion" + (draftUnionIndex-1)).after(draftUnionHtml);
		}
		var p = draftUnionIndex;
		$("#draftUnionId" + draftUnionIndex).combotree({
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			required : true,
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "地区不能选择！");
					return false;
				}
			},
			onChange : function(newValue, oldValue) {
				$('#draftUnionLeaderId' + p).combobox({
					url:'${pageContext.request.contextPath}/user/getUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
			}
		});
	}
	
	//移除所有联合起草tr
	function removeAllDraftUnion(){
		if(draftUnionIndex > 0)
		{
			for(var i=1; i<=draftUnionIndex; i++){
				$("#draftUnion" + i).remove();
			}
		}
		draftUnionIndex = 0;
	}
	
	//移除指定id联合起草tr
	function removeUnionDraft(unionDraftId){
		draftUnionIndex--;
		$("#" + unionDraftId).remove();
	}
	//保存,isConfirm为true表示定稿
	function save(isConfirm){
		var norId = $("#norId").val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var id = $("#id").val();
		var draftingUnit = $('#draftingUnitId').combotree('getValue');
		var draftingUnitLeader = $('#draftingUnitLeaderId').combobox('getValue');
		var draftingUnitClerk = $('#draftingUnitClerkId').combobox('getValue');
		var draftingStartDate = $("#draftingStartDate").datebox('getValue');
		var draftingEndDate = $("#draftingEndDate").datebox('getValue');
		var draftMode = $('#draftMode').combobox('getValue');
		var unionDraftUnits = "";
		var unionDraftLeaders = "";
		if(draftUnionIndex > 0){
			for(var i=1; i<=draftUnionIndex; i++){
				if(unionDraftUnits != ""){
					unionDraftUnits += ",";
					unionDraftLeaders += ",";
				}
				var unionDraftUnit = $('#draftUnionId' + i).combotree('getValue');
				unionDraftUnits += "\"" + unionDraftUnit + "\"";
				if(unionDraftUnit != ""){
					unionDraftLeaders += "\"" + $('#draftUnionLeaderId' + i).combobox('getValue') + "\""; 
				}
			}
		}
		var content = editor.getContent();

		if(draftingUnit == ""){
			$("#draftingUnitId").focus();
			showMsg("主起草单位不能为空");
			return;
		}
		if(draftingUnitLeader == ""){
			$("#draftingUnitLeaderId").focus();
			showMsg("主起草单位负责人不能为空");
			return;
		}
		if(draftingUnitClerk == ""){
			$("#draftingUnitClerkId").focus();
			showMsg("主起草单位经办员不能为空");
			return;
		}
		if(draftingStartDate == ""){
			$("#draftingStartDate").focus();
			showMsg("起草开始时间不能为空");
			return;
		}
		if(draftingEndDate == ""){
			$("#draftingEndDate").focus();
			showMsg("起草结束时间不能为空");
			return;
		}
		if(draftingEndDate < draftingStartDate){
			$("#draftingEndDate").focus();
			showMsg("起草结束时间不能小于开始时间");
			return;
		}
		if(draftMode == ""){
			$("#draftMode").focus();
			showMsg("起草方式不能为空");
			return;
		}
		if(draftUnionIndex > 0)
		{
			for(var i=1; i<=draftUnionIndex; i++){
				var unionDraftUnit =  $('#draftUnionId' + i).combotree('getValue');
				if(unionDraftUnit == ""){
					$('#draftUnionId' + i).focus();
					showMsg("联合起草单位不能为空");
					return;
				}
				var unionDraftLeader = $('#draftUnionLeaderId' + i).combobox('getValue');
				if(unionDraftLeader == ""){
					$('#draftUnionLeaderId' + i).focus();
					showMsg("联合起草单位负责人不能为空");
					return;
				}
			}
		}
		if(content == ""){
			showMsg("内容不能为空");
			return;
		}
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/draft/save.do",
			data : {
				"id" : id,
				"normativeFile.id" : norId,
				"draftingUnit.id" : draftingUnit,
				"draftingUnitLeader.id" : draftingUnitLeader,
				"draftingUnitClerk.id" : draftingUnitClerk,
				"draftingStartDate" : draftingStartDate,
				"draftingEndDate" : draftingEndDate,
				"draftingMode" : draftMode,
				"unionDraftingUnit" : unionDraftUnits,
				"unionDraftingUnitLeader" : unionDraftLeaders,
				"content" : content,
				"isConfirm" : isConfirm
			},
			dataType:"json",
			success : function(data) {
				showMsg(data.msg);
				if(data.success && data.vo){
					$("#id").val(data.vo.id);
				}
			},
			error : function(data) {
				showMsg("保存出错,请重试!");
			}
		});
	}
	function clear() {
		$("#detailForm").form("clear");
		removeAllDraftUnion();
		$("#btnAddDraftUnion").hide();
		if(!init){
			  editor.setContent("");
		  }
	}

	function load(id, searchType) {
		
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/draft/load.do",
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
					$('#draftingUnitId').combotree({
						url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
						onLoadSuccess:function(){
							if(data.vo.id == null){
								$('#draftingUnitId').combotree('setValue',data.vo.normativeFile.drtUnit.id);
								$('#draftingUnitLeaderId').combobox('setValue', data.vo.normativeFile.drtUnitLeader.id);
								$('#draftingUnitClerkId').combobox('setValue', data.vo.normativeFile.drtUnitClerk.id);
							}
							else{
								$('#draftingUnitId').combotree('setValue',data.vo.draftingUnit.id);
								$('#draftingUnitLeaderId').combobox('setValue', data.vo.draftingUnitLeader.id);
								$('#draftingUnitClerkId').combobox('setValue', data.vo.draftingUnitClerk.id);
							}
						}
					});
					$("#draftingStartDate").datebox('setValue',data.vo.draftingStartDate);
					$("#draftingEndDate").datebox('setValue',data.vo.draftingEndDate);
					$('#draftMode').combobox('setValue', data.vo.draftingMode);
					if(draftUnionIndex > 0){
						removeAllDraftUnion();
					}
					var unionDraftUnit = data.vo.unionDraftingUnit;
					if (unionDraftUnit != null && unionDraftUnit != "") {
						unionDraftUnit = unionDraftUnit.replace(/\"/g, "");
						var unionDraftUnits = unionDraftUnit.split(",");
						var unionDraftingUnitLeader = data.vo.unionDraftingUnitLeader.replace(/\"/g, "");
						var unionDraftUnitLeaders = unionDraftingUnitLeader.split(",");
						for(var i=0; i<unionDraftUnits.length; i++){
							addDraftUnion();
							$('#draftUnionId' + (i+1)).combotree('setValue',unionDraftUnits[i]);
							$('#draftUnionLeaderId' + (i+1)).combobox('setValue', unionDraftUnitLeaders[i]);
						}
					}
					$("#involvedOrges").val(data.vo.normativeFile.involvedOrges);
					$("#involvedOrgNames").val(data.vo.involvedOrgNames);
					
					var filename = data.vo.normativeFile.name;
					if(searchType == 'NORFILE'){
						  //得到文件头
						  gainFileHead("",filename);
					  }else{
						  if(init){
							  editor.addListener("ready", function() {
								  editor.setContent(data.vo.content);
							  });
						  }else{
							  editor.setContent(data.vo.content);
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
	//设置是否可以修改,true表示可以修改,false表示不可修改
	function setModifiable(modifiable){
		$('#draftingUnitId').combotree('readonly', !modifiable);
		$('#draftingUnitLeaderId').combobox({
			onLoadSuccess:function(){
				$('#draftingUnitLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#draftingUnitClerkId').combobox({
			onLoadSuccess:function(){
				$('#draftingUnitClerkId').combobox('readonly', !modifiable);
			}
		});
		$('#draftingStartDate').datebox('readonly', !modifiable);
		$('#draftingEndDate').datebox('readonly', !modifiable);
		$('#draftMode').combobox('readonly', !modifiable);
		$("#btnAddDraftUnion").prop("disabled", !modifiable);
		if(draftUnionIndex > 0)
		{
			for(var i=1; i<=draftUnionIndex; i++){
				$("#draftUnionId" + i).combotree('readonly', !modifiable);
				$('#draftUnionLeaderId' + i).combobox('readonly', !modifiable);
			}
			$(".unionDeleteBtn").prop("disabled", !modifiable);
		}
		showDiv("btnSave", modifiable);
		showDiv("btnConfirm", modifiable);
		showDiv("btnDelete", modifiable);
		
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
				  var filehead = "<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
				  				   "<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+filename+"</span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:18px;font-family:宋体; font-variant:small-caps\">(草稿)</span></p>";
				  
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
		<div id="cc" class="easyui-layout cc">
			<div data-options="region:'center',border:false,split:false,width: 1200, height: 680" >
				<table  class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
							<div id="btnAdd" class="button_change red center" >新增</div> 
							<div id="btnSave" class="button_change red center" >保存</div> 
							<div id="btnConfirm" class="button_change red center" >定稿</div> 
							<div id="btnDelete" class="button_change red center" >删除</div> 
							<div id="btnList" class="button_change red center" >列表</div> 
							<div id="btnFind" class="button_change red center" >查找</div> 
							<div id="btnExport" class="button_change red center" >导出</div>
							<div id="btnPrint" class="button_change red center" >打印</div>
						</td>
					</tr>
				</table>

				<form id="downloadId" class="downloadId" name="download" action="" method="post" ></form>
				<div id="dataDivWindow" class="dataDivWindow" >
					<form id="detailForm" action="">
						<table cellpadding="0" cellspacing="0" class="spe_table">
							<tbody>
								<tr>
									<td class="td-label">文件名称</td>
									<td colspan="5" class="td-fileName">
										<input id="norId" name="normativeFileId" type="hidden" /> 
										<input id="id" name="id" type="hidden" /> 
										<input id="nameId" name="draftName" readonly="readonly" class="inputWidth noBorder" />
									</td>
									<td class="td-btn-find">
										<div id="btnOpen" class="button_change red center">查询</div>
									</td>
								</tr>
								<tr>
									<td class="td-label">主起草单位</td>
									<td class="td-data">
										<input id="draftingUnitId" name="draftingUnit" class="form_input_spe" />
									</td>
									<td class="td-label">主起草单位负责人</td>
									<td class="td-data">
										<input id="draftingUnitLeaderId" name="draftingUnitLeader"  />
									</td>
									<td class="td-label">主起草单位经办员</td>
									<td class="td-data">
										<input id="draftingUnitClerkId" name="draftingUnitClerk"  />
									</td>
									<td></td>
								</tr>
								<tr id="draftModeTr">
									<td class="td-label">起草开始时间</td>
									<td class="td-data">
										<input id="draftingStartDate" name="draftingStartDate" type="text" class="easyui-datebox"></input>
									<td class="td-label">起草结束时间</td>
									<td class="td-data">
										<input id="draftingEndDate" name="draftingEndDate" type="text" class="easyui-datebox" validType="compareDate['#draftingStartDate']"></input>
									</td>
									<td class="td-label">起草方式</td>
									<td class="td-data">
										<input id="draftMode" name="draftMode"  />
									</td>
									<td style="text-align: left;">
										<input id="btnAddDraftUnion" type="button" value="添加" class="hidden"/>
									</td>
								</tr>
								<tr>
										<td class="td-label">涉及的部门</td>
										<td colspan="5" style="text-align: left;">
											<input id="involvedOrges" name="involvedOrges" type="hidden"/>
											<input id="involvedOrgNames" name="involvedOrgNames" readonly="readonly" class="inputWidth noBorder"/>
										</td>
										<td></td>
									</tr>
								<tr class="editorTr">
									<td class="td-label">内容</td>
									<td align="center" colspan="6" class="td-file-content">
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
	<!-- 弹出文件起草页面 -->
	<div id="viewRequestComment" title="文件起草浏览" class="easyui-window"
		collapsible="false" inline="false" minimizable="false"
		maximizable="false" closed="true" resizable="false" style="padding: 0px;">
		<div id="viewContent"class="class="view-file"></div>
	</div>
	<div id="searchDraft" title="文件起草查询" class="easyui-window"
		collapsible="false" minimizable="false" maximizable="false"data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName">
			<tr>
				<td>文件名称:</td>
				<td><input id="searchNameId" name="searchName" size="60" /></td>
				<td><input id="btnSearch" type="button" value="查询" /></td>
			</tr>
		</table>

		<table id="searchDataGrid" data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
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
				<td><input id="btnSubmit" type="button" value="确认" /></td>
				<td><input id="btnCancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
</body>
</html>