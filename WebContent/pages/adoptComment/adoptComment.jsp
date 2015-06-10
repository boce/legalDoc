<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>反馈意见处理情况</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/adoptComment.css">
<title>Insert title here</title>
<script type="text/javascript">
	var editor = null;
	var feedbackComment = '';
	var init = true;
	
	$(function() {

		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		editor = UE.getEditor("editor");
		editor.addListener("ready", function() {
			  init = false;
		});
		
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'ADOPTCOMMENT');
		}
		
		$("#btnOpen").click(function() {
			openFrame('REQUEST_COMMENT_FEEDBACK');
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
			var id = $("#id").val();
			var comment = feedbackComment;
			var feedbackProcess = editor.getContent();
			var isNeedModifyVal = $("input[name=isNeedModify]:checked").val();
			if(typeof(isNeedModifyVal) == "undefined" || isNeedModifyVal == ""){
				showMsg("请选择是否需要修改征求意见稿！");
				return;
			}
			var isNeedModify = isNeedModifyVal == "0" ? true : false;
			
			if(feedbackProcess == ""){
				showMsg("反馈意见处理情况不能为空");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/adoptComment/save.do",
				data : {
					"id" : id,
					"normativeFile.id" : norId,
					"feedbackComment" : comment,
					"feedbackProcess" : feedbackProcess,
					"isNeedModify" : isNeedModify
				},
				dataType:"json",
				success : function(data) {
					if(data){
						showMsg(data.msg);
						if(data.success && data.vo){
							$("#id").val(data.vo.id);
						}
					}else{
						showMsg("保存出错!");
					}
				},
				error : function(data) {
					showMsg("保存出错,请重试!");
				}
			});

		});

		$("#btnDelete").click(function() {
			var id = $("#id").val();
			if(id == ""){
				showMsg("请选择一个反馈意见处理情况单");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/adoptComment/delete.do",
				data : {
					id : id
				},
				success : function(data) {
					showMsg(data.msg);
					if(data.success){
						clear();
					}
				},
				error : function(data) {
					showMsg("删除出错,请重试!");
				}
			});

		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/adoptComment/adoptCommentList.wf";
		});

		//反馈意见处理情况查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/adoptComment/find.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'ADOPTCOMMENT');
					$('#searchAdoptComment').window('close'); //关闭查询窗口
				}
			});
			$('#searchAdoptComment').window('open');

		});

		//关闭反馈意见处理情况查找窗口
		$("#btnCancel").click(function() {
			$('#searchAdoptComment').window('close');
		});

		//查询反馈意见处理情况文件
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid')
					.datagrid(
							{
								url : '${pageContext.request.contextPath}/adoptComment/find.do?name='
										+ fileName
							});
			$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});

		//点击确认
		$("#btnConfirm").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			var id = selectRow.id;
			load(id, 'ADOPTCOMMENT');
			$('#searchAdoptComment').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/adoptComment/print.do",
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
				showMsg("请选择规范性文件或送审稿！");
			}

		});

		$("#btnExport").click(
				function() {
					var norId = $('#norId').val();
					if (null != norId && "" != norId) {
						$("#downloadId").attr(
								"action",
								"${pageContext.request.contextPath}/adoptComment/export.do?norId="
										+ norId);
						$("#downloadId").submit();
					} else {
						showMsg("请选择规范性文件或送审稿！");
					}
				});

	});

	function clear() {
		$("#id").val('');
		$("#norId").val('');
		$("#nameId").val('');

		$("#draftingUnitId").val('');
		$("#draftingUnitLeaderId").val('');
		$("#draftingUnitClerkId").val('');

		if (!init) {
			editor.setContent('');
		}
		feedbackComment = '';
		$('#feedbackCommentId').empty();
		
		$("input[name=isNeedModify]:checked").prop("checked", false);
	}

	function view(fileName) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/adoptComment/viewFeedback.do",
				data : {
					norId : norId,
					fileName : fileName
				},
				dataType : 'html',
				success : function(data) {
					$('#viewContent').html('');
					$('#viewContent').html(data);
					$('#viewFeedback').window('open'); //打开送审稿浏览
				},
				error : function(data) {
					showMsg("加载出错,请重试!");
				}
			});
		} else {
			showMsg("请选择规范性文件或送审稿！");
		}

	}

	function load(id, searchType) {
		clear();
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/adoptComment/load.do",
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
					$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
					$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
					$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
					if(data.vo.isNeedModify != null){
						var needModifyValue = data.vo.isNeedModify ? 0 : 1;
						$("input[name=isNeedModify][value=" + needModifyValue + "]").prop("checked", true);
					}
					
					if (data.vo.feedbackComment != null
							&& data.vo.feedbackComment.length > 0) {
						feedbackComment = data.vo.feedbackComment;
						$('#feedbackCommentId').empty();
						var strs = new Array();
						strs = data.vo.feedbackComment.split(";");
						$.each(strs, function(index, tx) {
							$("#feedbackCommentId").append(
									'<a class="attachment'+index+'" href="#" style="cursor: pointer;"><div>'
											+ tx + '</div></a>');
							$(".attachment"+index).bind('click', function() {
								view(tx);
							});
						});
					}

					var filename = data.vo.normativeFile.name;
					if(searchType == 'NORFILE'){
						  //得到文件头
						  gainFileHead("",filename);
					  }else{
						  if(init){
							  editor.addListener("ready", function() {
								  editor.setContent(data.vo.feedbackProcess);
							  });
						  }else{
							  editor.setContent(data.vo.feedbackProcess);
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
		showDiv("btnSave", modifiable);
		showDiv("btnDelete", modifiable);
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
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-weight:bold; font-family:宋体;font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于对《"+filename+"》的反馈意见处理情况</span></p>";
				  
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
			<div data-options="region:'center',border:false,split:false,width: 1200,height: 680">
				<table class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
							<div id="btnAdd" class="button_change red center">新增</div>
							<div id="btnSave" class="button_change red center">保存</div> 
							<div id="btnDelete" class="button_change red center">删除</div> 
							<div id="btnList" class="button_change red center">列表</div>
							<div id="btnFind" class="button_change red center">查找</div> 
							<div id="btnExport" class="button_change red center">导出</div>
							<div id="btnPrint" class="button_change red center">打印</div>
						</td>
					</tr>
				</table>
				<div>
					<form id="downloadId" class="downloadId" name="download" action="" method="post" ></form>
				</div>
				<div id="dataDivWindow" class="dataDivWindow" >
					<table  cellpadding="0" cellspacing="0" class="spe_table">
						<tbody>
							<tr>
								<td  class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName" >
									<input id="norId" name="normativeFileId" type="hidden" /> 
									<input id="id" name="adoptCommentId" type="hidden" /> 
									<input id="nameId" name="adoptCommentName" readonly="readonly" class="fileName" />
								</td>
								<td  class="td-btn-find" >
									<div id="btnOpen" class="button_change red center" >查询</div>
								</td>
							</tr>
							<tr>
								<td  class="td-label">主起草单位</td>
								<td class="td-data" >
									<input id="draftingUnitId" name="draftingUnit" readonly="readonly" class="td-3data" />
								</td>
								<td  class="td-label">主起草单位负责人</td>
								<td class="td-data">
									<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data"/>
								</td>
								<td  class="td-label">主起草单位经办员</td>
								<td class="td-data">
									<input id="draftingUnitClerkId" name="draftingUnitClerk" readonly="readonly" class="td-3data" />
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">反馈意见</td>
								<td colspan="6" class="td-upload-file">
									<div id="feedbackCommentId"></div>
								</td>
							</tr>
							<tr class="editorTr">
								<td class="td-label">反馈意见处理情况</td>
								<td colspan="6" class="td-file-content">
								<center>
									<script id="editor" type="text/plain" class="td-editor"></script>
									</center>
								</td>
							</tr>
							<tr>
								<td class="td-label">是否需要修改征求意见稿</td>
								<td colspan="6" class="td-upload-file">
									<input name="isNeedModify" type="radio" value="0"/>是
									<input name="isNeedModify" type="radio" value="1"/>否
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

	<!-- 弹出浏览反馈意见页面 -->
	<div id="viewFeedback" title="反馈意见浏览" class="easyui-window"
		collapsible="false" inline="false" minimizable="false"
		maximizable="false" closed="true" resizable="false">
		<div id="viewContent" class="view-file" ></div>
	</div>

	<div id="searchAdoptComment" title="反馈意见处理情况查询" class="easyui-window "
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName" >
			<tr>
				<td>文件名称:</td>
				<td><input id="searchNameId" name="searchName" size="60" /></td>
				<td><input id="btnSearch" type="button" value="查询" /></td>
			</tr>
		</table>

		<table id="searchDataGrid" data-options="pagination:true, singleSelect:true, padding:0, rownumbers:true, height: 200">
			<thead>
				<tr>
					<th data-options="field:'id',hidden:'true'">id</th>
					<th data-options="field:'name',width:557">文件名</th>
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