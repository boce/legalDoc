<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>意见反馈</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="css/text" href="<%=contextPath%>/css/legaldoc/feedbackComment.css">
<script type="text/javascript">
	var editor = null;
	var requestingDraft = '';
	var init = true;
	
	$(function() {
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		editor = UE.getEditor("editor");
		editor.addListener("ready", function() {
			  init = false;
		});
		
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'FEEDBACKCOMMENT');
		}
		$('#actualFeedbackDateId').datebox({'editable':false});
		$('#feedbackUnitId').combobox({    
		    valueField:'id',    
		    textField:'text'
		});
		
		$("#btnOpen").click(function() {
			openFrame('REQUEST_COMMENT_REQUEST');
		});

		$("#btnAdd").click(function() {
			clear();
			setModifiable(true);
		});

		$("#btnSave").click(function() {
			var norId = $("#norId").val();
			var id = $("#id").val();
			var feedbackUnit = $("#feedbackUnitId").combobox('getValue');
			var feedbackUnitClerk = $("#feedbackUnitClerkId").combobox('getValue');
			var latestFeedbackDate= $("#latestFeedbackDateId").datebox('getValue');
			var actualFeedbackDate = $("#actualFeedbackDateId").datebox('getValue');
			
			var content = '';
			content = editor.getContent();

			if(norId == ""){
				showMsg("文件名称不能为空");
				return;
			}
			if(feedbackUnit == ""){
				$("#feedbackUnitId").focus();
				showMsg("反馈单位不能为空");
				return;
			}
			if(feedbackUnitClerk == ""){
				$("#feedbackUnitClerkId").focus();
				showMsg("反馈单位经办员不能为空");
				return;
			}
			if(latestFeedbackDate == ""){
				$("#latestFeedbackDateId").focus();
				showMsg("最晚反馈时间不能为空");
				return;
			}
			if(actualFeedbackDate == ""){
				$("#actualFeedbackDateId").focus();
				showMsg("实际反馈时间不能为空");
				return;
			}
			if(content == null || content == ""){
				showMsg("修改意见和建议不能为空");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/feedbackComment/save.do",
				data : {
					"id" : id,
					"normativeFile.id" : norId,
					"feedbackUnit.id" : feedbackUnit,
					"feedbackUnitClerk.id" : feedbackUnitClerk,

					"latestFeedbackDate" : latestFeedbackDate,
					"actualFeedbackDate" : actualFeedbackDate,
					"requestingDraft" : requestingDraft,
					"modifyOpinions" : content
				},
				dataType:"json",
				success : function(data) {
					if(data){
						showMsg(data.msg);
						if(data.success && data.vo){
							$("#id").val(data.vo.id);
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
			if(id == ""){
				showMsg("请选择一个意见反馈单！");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/feedbackComment/delete.do",
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
					showMsg("加载出错,请重试!");
				}
			});

		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/feedbackComment/feedbackCommentList.wf";
		});

		//修改征求意见稿查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/feedbackComment/find.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'FEEDBACKCOMMENT');
					$('#searchFeedbackComment').window('close'); //关闭查询窗口
				}
			});
			$('#searchFeedbackComment').window('open');

		});

		//关闭修改征求意见稿查找窗口
		$("#btnCancel").click(function() {
			$('#searchFeedbackComment').window('close');
		});

		//查询修改征求意见稿文件
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid')
					.datagrid(
							{
								url : '${pageContext.request.contextPath}/feedbackComment/find.do?name='
										+ fileName
							});
			$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});

		//点击确认
		$("#btnSubmit").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			var id = selectRow.id;
			load(id, 'FEEDBACKCOMMENT');
			$('#searchFeedbackComment').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var id = $('#id').val();
			if (null != norId && "" != norId) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/feedbackComment/print.do",
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
				showMsg("请选择意见征求修改稿！");
			}

		});

		$("#btnExport").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$("#downloadId").attr(
						"action",
						"${pageContext.request.contextPath}/feedbackComment/export.do?id="
								+ id);
				$("#downloadId").submit();
			} else {
				showMsg("请选择意见征求修改稿！");
			}
		});

	});

	//初始化反馈单位、经办员
	function initData(){
		var norId = $('#norId').val();
		if (norId != "") {
			$.ajax({
				type : "post",
				url : "${pageContext.request.contextPath}/feedbackComment/init.do",
				data:{norId:norId},
				dataType : 'json',
				success : function(data) {
					$('#feedbackUnitId').combobox('setValue',data.feedbackUnit);
					$('#feedbackUnitClerkId').combobox('setValue', data.feedbackUnitClerk);
					$('#latestFeedbackDateId').datebox('setValue', data.latestFeedbackDate);
				},
				error : function() {
					showMsg("加载出错,请重试!");
				}
			});
		}
	}
	
	function clear() {
		$("#id").val('');
		$("#norId").val('');
		$("#nameId").val('');

		$("#draftingUnitId").val('');

		$("#feedbackUnitId").combobox('clear');
		$("#feedbackUnitClerkId").combobox('clear');

		$("#latestFeedbackDateId").datebox('setValue', '');
		$("#actualFeedbackDateId").datebox('setValue', '');

		if(!init){
			editor.setContent("");
		}
		requestingDraft = '';
		$('#requestingDraftId').empty();
	}

	function view(fileName) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/feedbackComment/viewFeedback.do",
				data : {
					norId : norId,
					fileName : fileName
				},
				dataType : 'html',
				success : function(data) {
					$('#viewContent').html('');
					$('#viewContent').html(data);
					$('#viewRequestComment').window('open'); //打开送审稿浏览
				},
				error : function(data) {
					showMsg("加载出错,请重试!");
				}
			});
		} else {
			showMsg("请选择意见征求修改稿！");
		}

	}

	function load(id, searchType) {
		clear();
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/feedbackComment/load.do",
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
					$('#feedbackUnitId').combobox({    
					    url:'${pageContext.request.contextPath}/feedbackComment/getFeedbackUnit.do',    
					    valueField:'id',    
					    textField:'text',
					    onBeforeLoad: function(param){
							param.id = data.vo.id;
							param.norId = data.vo.normativeFile.id;
						},
						onChange : function(newValue, oldValue) {
							$('#feedbackUnitClerkId').combobox({
								url:'${pageContext.request.contextPath}/user/getUserByOrg.do',
								valueField : 'id',
								textField : 'name',
								onBeforeLoad: function(param){
									param.orgId = newValue;
								}
							});
							var norId = $("#norId").val();
							if(newValue != null && newValue != ""){
								$.ajax({
									type : "post",
									url : "${pageContext.request.contextPath}/feedbackComment/getLatestFeedbackDate.do",
									data:{norId:norId, reqFromUnitId:newValue},
									dataType : 'json',
									success : function(data) {
										$('#latestFeedbackDateId').datebox('setValue', data.latestFeedbackDate);
									},
									error : function() {
										showMsg("加载出错,请重试!");
									}
								});
							}
						},
						onLoadSuccess:function(){
							if(data.vo.feedbackUnit){
								$("#feedbackUnitId").combobox('setValue', data.vo.feedbackUnit.id);
								$("#feedbackUnitClerkId").combobox('setValue', data.vo.feedbackUnitClerk.id);
							}else{
								if(searchType == "NORFILE"){
									initData();
								}
							}
						}
					});
					$("#latestFeedbackDateId").datebox('setValue', data.vo.latestFeedbackDate);
					$("#actualFeedbackDateId").datebox('setValue', data.vo.actualFeedbackDate);

					if (data.vo.requestingDraft != null
							&& data.vo.requestingDraft.length > 0) {
						requestingDraft = data.vo.requestingDraft;
						$('#requestingDraftId').empty();
						var strs = new Array();
						strs = data.vo.requestingDraft.split(";");
						$.each(strs, function(index, tx) {
							$("#requestingDraftId").append(
									'<a class="attachment" href="#" style="cursor: pointer;"><div>'
											+ tx + '</div></a>');
							$(".attachment").bind('click', function() {
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
								  editor.setContent(data.vo.modifyOpinions);
							  });
						  }else{
							  editor.setContent(data.vo.modifyOpinions);
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
		$('#feedbackUnitId').combobox('readonly', !modifiable);
		setTimeout(function(){$('#feedbackUnitClerkId').combobox('readonly', !modifiable);}, 1000);
		$('#actualFeedbackDateId').datebox('readonly', !modifiable);
		showDiv("btnSave", modifiable);
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
				  filehead ="<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
				  				   "<p style=\"text-align:center\"><span style=\"font-size:18px;font-family:宋体\">&nbsp;</span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-weight:bold; font-family:宋体;font-variant:small-caps\">"+data.districtName+data.organizationName +"</span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-weight:bold; font-family:宋体;font-variant:small-caps\">关于对《"+filename+"》的修改建议意见</span></p>";
				  
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
			<div data-options="region:'center',border:false,split:false,width: 1200, height: 680" >
				<table  class="table">
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
				<table  cellpadding="0" cellspacing="0" class="spe_table  dataTable">
					<tbody>
						<tr>
							<td class="td-label">文件名称</td>
							<td id="nameIdTd" colspan="5" class="td-fileName">
								<input id="norId" name="normativeFileId" type="hidden" /> 
								<input id="id" name="feedbackCommentId" type="hidden" /> 
								<input id="nameId" name="feedbackCommentName" readonly="readonly" class="fileName"/>
							</td>
							<td class="td-btn-find">
								<div id="btnOpen" class="button_change red center">查询</div>
							</td>
						</tr>
						<tr>
							<td class="td-label">起草单位</td>
							<td class="td-data">
								<input id="draftingUnitId"  name="draftingUnit" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">反馈单位</td>
							<td class="td-data">
								<input class="easyui-combobox" id="feedbackUnitId" name="feedbackUnit" data-options="valueField:'id',textField:'text'" value="" />
							</td>
							<td class="td-label">反馈单位经办员</td>
							<td class="td-data">
								<input id="feedbackUnitClerkId" name="feedbackUnitClerk" class="easyui-combobox" data-options="valueField:'id',textField:'name'," value="" />
							</td>
							<td></td>
						</tr>
						<tr>
							<td class="td-label">最晚反馈时间</td>
							<td class="td-data">
								<input id="latestFeedbackDateId" type="text" readonly="readonly" class="easyui-datebox"/>
							</td>
							<td class="td-label">实际反馈时间</td>
							<td class="td-data">
							<input id="actualFeedbackDateId" type="text" class="easyui-datebox" />
							</td>
						</tr>
						<tr>
							<td class="td-label">征求意见稿</td>
							<td colspan="6" class="td-upload-file"><div
									id="requestingDraftId"></div></td>
						</tr>
						<tr>
							<td style="width: 120px;">修改意见和建议</td>
							<td colspan="6" class="td-file-content">
								<center>
									<script id="editor" type="text/plain" class="td-editor" ></script>
									</center>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			</div>
		</div>
	<!-- 弹出浏览征求意见稿页面 -->
	<div id="viewRequestComment" title="征求意见稿浏览" class="easyui-window"
		collapsible="false" inline="false" minimizable="false"
		maximizable="false" closed="true" resizable="false">
		<div id="viewContent"class="view-file"></div>
	</div>
	<div id="searchFeedbackComment" title="反馈意见查询" class="easyui-window"
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
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
					<th data-options="field:'name',width:'435'">文件名</th>
					<th data-options="field:'feedbackUnit',halign:'center',width:120,formatter:function(value,row){
                            return row.feedbackUnit.text;
                        }">反馈单位</th>
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