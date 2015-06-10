<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>送审稿修改</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/examDraftModify.css"/>
<script type="text/javascript">

	var editor = null;	
	var drfeditor = null;
	var init = true;
	var drfInit = true;
	$(function(){
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		editor = UE.getEditor('editor');
		editor.addListener("ready", function() {
			init = false;
		});
		
		drfeditor = UE.getEditor('drfeditor');
		drfeditor.addListener("ready", function() {
			drfInit = false;
		});
		
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'DRAFTMODIFY');
		}
		
		$('#saveBtn').click(function(){ 	//保存送审稿修改
			var content = editor.getContent();
			content = content.replace('(送审稿)','(送审稿修改稿)');
			editor.setContent(content);
			save(false);
		});
		
		$('#addBtn').click(function(){ 	//新增
			$('#norId').val('');
			$("#examDraftModifyNameId").val('');
			$("#draftingUnitId").val('');
			$("#draftingUnitLeaderId").val('');
			$("#draftingUnitClerkId").val('');
			$('#viewReviewComment').html('');
			editor.setContent('');
			drfeditor.setContent('');
			
			setModifiable(true);
		});
		
		$("#listBtn").click(function() {
			location.href = "${pageContext.request.contextPath}/examDraft/examDraftModifyList.wf";
		});
		
		$('#exportBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
			$("#downloadId").attr("action", "${pageContext.request.contextPath}/examDraftMod/downloadWord.do?norId=" + norId);
			$("#downloadId").submit();
		});
		
		
		$('#printBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type : "post",
					  url : "${pageContext.request.contextPath}/examDraftMod/print.do",
					  data : {norId : norId},
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
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		
		$('#deleteBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/examDraftMod/delete.do",
					  data : {norId : norId},
					  dataType : 'json',
					  success : function(data)
					  {
						  if (data.msg == 'success') {
		                		showMsg("删除成功!");
		                		$('#addBtn').click();	//清除删除在页面上内容
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
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		$('#searchBtn').click(function(){ 
			$('#draftModifyGrid').datagrid({
				url : '${pageContext.request.contextPath}/examDraftMod/searchDraftMods.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'DRAFTMODIFY');
					$('#searchDraftModify').window('close'); //关闭查询窗口
				}
			});
			$('#draftModifyGrid').datagrid('reload');
			$('#searchDraftModify').window('open');
		});
		
		$('#viewReviewComment').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/examDraftRev/gainFileContent.do",
					  data : {norId : norId, fileType : "REVIEWCOMMENT"},
					  dataType : 'html',
					  success : function(data)
					  {
						  $('#viewContent').html(data);
						  $('#commentId').window('open');	//打开审核意见浏览
					  },
					  error : function(data)
					  {
						  showMsg("保存出错,请重试!");
					  }
					});
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		$('#modifyCancel').click(function(){ 
			$('#searchDraftModify').window('close');
		});
		
		$('#draftModifyConfirmId').click(function(){ 
			var selectRow = $('#draftModifyGrid').datagrid('getSelected');
			if (selectRow != null) {
				var id = selectRow.id;
				load(id, 'DRAFTMODIFY');
				$('#searchDraftModify').window('close'); //关闭查询窗口
			} else {
				showMsg("请选择记录！");
			}
			
		});
		
		$('#searchDraftModifyBtn').click(function(){ 
			var fileName = $('#draftModifyNameId').val();
			$('#draftModifyGrid').datagrid({
				url : '${pageContext.request.contextPath}/examDraftMod/searchDraftMods.do?name=' + fileName
			});
			$('#draftModGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});
		
		//定稿
		$('#finalizeBtn').click(function(){
			var content = editor.getContent();
			content = content.replace('(送审稿修改稿)','(草案)');
			editor.setContent(content);
			save(true);
		});
		
	});
	function save(isConfirm){
		var norId = $('#norId').val();
		if(norId == ""){
			showMsg("文件名称不能为空");
			return;
		}
		var content = editor.getContent();
		var drfcontent = drfeditor.getContent();
		$.ajax({
			  type: "post",
			  url: "${pageContext.request.contextPath}/examDraftMod/addExamDraftMod.do",
			  data : {norId : norId, content : content, drfcontent : drfcontent, isConfirm:isConfirm},
			  dataType : 'json',
			  success : function(data)
			  {
				 if (data.msg == "success") {
					 showMsg("保存成功!");
				 } else if (data.msg == 'fail') {
					 showMsg("保存失败!");
				 } else {
					 showMsg(data.msg);
				 }
			  },
			  error : function(data)
			  {
				  showMsg("保存出错,请重试!");
			  }
		});
	}
	function load(id, sreachType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/examDraftMod/load.do",
			data : {
				id : id,
				sreachType : sreachType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$('#norId').val(data.vo.normativeFile.id);
					$("#examDraftModifyNameId").val(data.vo.normativeFile.name);
					
					$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
					$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
					$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
					$('#viewReviewComment').html(data.vo.reviewComment);
					var content = data.vo.content;
					if(sreachType == "NORFILE"){
						$.ajax({	//加载送审稿
							  type: "POST",
							  url: "${pageContext.request.contextPath}/examDraftMod/gainFileContent.do",
							  data : {id : id, sreachType : sreachType, fileType : 'DRAFT'},
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
						  url: "${pageContext.request.contextPath}/examDraftMod/gainFileContent.do",
						  data : {id : id, sreachType : sreachType, fileType : 'INSTRUCTION'},
						  dataType : 'html',
						  success : function(data){
								if(drfInit){
									drfeditor.addListener("ready", function(){
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
					
					//设置按钮是否显示
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
		showDiv("saveBtn", modifiable);
		showDiv("deleteBtn", modifiable);
		showDiv("submitBtn", modifiable);
		showDiv("finalizeBtn", modifiable);
		
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
	    <div>
	    	<table  class="spe_table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div class="button_change red center" id="addBtn">新增</div>
	    				<div class="button_change red center" id="saveBtn">保存</div>
	    				<div class="button_change red center" id="finalizeBtn">定稿</div>
	    				<div class="button_change red center" id="deleteBtn">删除</div>
						<div class="button_change red center" id="listBtn">列表</div>
	    				<div class="button_change red center" id="searchBtn">查找</div>
	    				<div class="button_change red center" id="exportBtn">导出</div>
	    				<div class="button_change red center" id="printBtn">打印</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
    	<div >
    	<form id="downloadId"  class="downloadId" name="download" action="" method="post"  ></form>
    	<div id="dataDivWindow"  class="dataDivWindow" >
	    	<table style="width: 1200px; height:610px;" class="spe_table dataTable" >
				<tbody>
					<tr>
						<td class="td-label">文件名称</td>
						<td colspan="5" class="td-fileName">
							<input id="norId" name="normativeFileId" type="hidden" /> 
							<input id="examDraftModifyNameId" type="text" class="fileName" readonly="readonly" />     
						</td>
						<td class="td-btn-find">	
							<div class="button_change red center" onclick="openFrame('LEGAL_REVIEW_REVIEW')">查询</div>
						</td>
					</tr>
					<tr>
						<td class="td-label">起草单位</td>
						<td class="td-data">
							<input id="draftingUnitId"  name="draftingUnit" readonly="readonly"  class="td-3data"/>
						</td>
						<td class="td-label">起草单位负责人</td>
						<td class="td-data">
							<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly"  class="td-3data"/>
						</td>
						<td class="td-label">起草单位经办员</td>
						<td class="td-data">
							<input id="draftingUnitClerkId" name="draftingUnitClerk" readonly="readonly" class="td-3data"/> 
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="td-label">审核意见</td>
						<td colspan="6" class="td-upload-file">
							<a id="viewReviewComment" style="padding: 0px; margin: 0px;  cursor: pointer;"></a>
						</td>
					</tr>
					<tr>
						<td>起草说明</td>
						<td colspan="6" class="td-file-content">
							<center>
									<script id="drfeditor" type="text/plain"  class="td-drfeditor"></script>
									</center>
						</td>
					</tr>
					<tr>
						<td>内容</td>
						<td colspan="6" class="td-file-content" >
							<center>
									<script id="editor" type="text/plain"  class="td-editor"></script>
									</center>
						</td>
					</tr>
				</tbody>
			</table>
			</div>
		</div>
	    
	<!-- 弹出浏览审核意见页面 -->
	<div id="commentId" title="审核意见浏览"  class="easyui-window" collapsible="false" inline="false" minimizable="false"
		maximizable="false" closed="true" resizable="false">
		<div id="viewContent" class="view-file" ></div>
	</div>
	
	<!-- 弹出页面 -->
	<div id="searchDraftModify" title="送审稿修改查询" class="easyui-window"
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName">
			<tr>
				<td>文件名称:</td>
				<td><input id="draftModifyNameId" name="draftModifyName"
					size="60" /></td>
				<td><input id="searchDraftModifyBtn" type="button" value="查询" /></td>
			</tr>
		</table>

		<table id="draftModifyGrid" class="easyui-datagrid" 
			data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
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
				<td><input id="draftModifyConfirmId" type="button" value="确认" /></td>
				<td><input id="modifyCancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
</body>
</html>