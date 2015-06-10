<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>征求意见</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/legaldoc/requestComment.css">
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
			load(parseInt(listId, 10), 'REQUESTCOMMENT');
		}
		
		
		$("#btnOpen").click(function() {
			openFrame('DRAFTING');
		});

		$("#btnAdd").click(function() {
			clear();
			setModifiable(true);
		});

		//结束反馈意见阶段
		$("#btnCompleteFeedback").click(function() {
			var id = $("#id").val();
			if(id == ""){
				showMsg("请先选择一个征求意见单");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/requestComment/completeRequestback.do",
				data : {
					"id" : id
				},
				success : function(data) {
					showMsg(data.msg);
				},
				error : function(data) {
					showMsg("保存出错,请重试!");
				}
			});
		});
		
		$("#btnSave").click(function() {
			var norId = $("#norId").val();
			var id = $("#id").val();
			var requestFromUnit = $("#requestFromUnitId").combobox('getValue');
			var date= $("#latestFeedbackDateId").datebox('getValue');

			var content = editor.getContent();
			
			if(norId == ""){
				showMsg("文件名称不能为空");
				return;
			}
			if(requestFromUnit == ""){
				$("#requestFromUnitId").focus();
				showMsg("反馈单位不能为空");
				return;
			}
			if(date == ""){
				$("#latestFeedbackDateId").focus();
				showMsg("最晚反馈时间不能为空");
				return;
			}
			if(content == ""){
				showMsg("征求意见函内容不能为空");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/requestComment/save.do",
				data : {
					"id" : id,
					"normativeFile.id" : norId,
					"requestFromUnit.id" : requestFromUnit,
					"latestFeedbackDate" : date,
					"requestingDraft" : requestingDraft,
					"content" : content
				},
				dataType:"json",
				success : function(data) {
					if(data != null){
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
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/requestComment/delete.do",
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
			location.href = "${pageContext.request.contextPath}/requestComment/requestCommentList.wf";
		});

		//修改征求意见稿查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/requestComment/find.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					load(id, 'REQUESTCOMMENT');
					$('#searchrequestComment').window('close'); //关闭查询窗口
				}
			});
			$('#searchrequestComment').window('open');

		});

		//关闭修改征求意见稿查找窗口
		$("#btnCancel").click(function() {
			$('#searchrequestComment').window('close');
		});

		//查询修改征求意见稿文件
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid').datagrid({
				url : '${pageContext.request.contextPath}/requestComment/find.do?name='+ fileName
			});
			$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});

		//点击确认
		$("#btnSubmit").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			var id = selectRow.id;
			load(id, 'REQUESTCOMMENT');
			$('#searchrequestComment').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/requestComment/print.do",
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

		$("#btnExport").click(
				function() {
					var id = $('#id').val();
					if (null != id && "" != id) {
						$("#downloadId").attr(
								"action",
								"${pageContext.request.contextPath}/requestComment/export.do?id="
										+ id);
						$("#downloadId").submit();
					} else {
						showMsg("请选择意见征求修改稿！");
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

		$("#requestFromUnitId").combobox('clear');

		$("#latestFeedbackDateId").datebox('setValue', '');

		if(!init){
			editor.setContent("");
		}
		

		requestingDraft = '';
		$('#requestingDraftId').html("");
	}

	function view(fileName) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/requestComment/viewFeedback.do",
				data : {
					norId : norId,
					fileName : fileName
				},
				dataType : 'html',
				success : function(data) {
					$('#viewContent').html(data);
					$('#viewRequestComment').window('open'); //打开征求意见稿浏览
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
			url : "${pageContext.request.contextPath}/requestComment/load.do",
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

					$('#requestFromUnitId').combobox({    
					    url:'${pageContext.request.contextPath}/requestComment/getRequestFromUnit.do',    
					    valueField:'id',    
					    textField:'text',
					    onBeforeLoad: function(param){
							param.id = data.vo.id;
							param.norId = data.vo.normativeFile.id;
						},
						onLoadSuccess:function(){
							if(data.vo.requestFromUnit){
								$("#requestFromUnitId").combobox('setValue', data.vo.requestFromUnit.id);
							}
						}
					});
					$("#latestFeedbackDateId").datebox('setValue', data.vo.latestFeedbackDate);

					if (data.vo.requestingDraft != null && data.vo.requestingDraft.length > 0) {
						requestingDraft = data.vo.requestingDraft;
						$('#requestingDraftId').html("");
						var strs = data.vo.requestingDraft.split(";");
						$.each(strs, function(index, tx) {
							$("#requestingDraftId").append('<a class="attachment" href="#" style="cursor: pointer;"><div>' + tx + '</div></a>');
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
						  var content = data.vo.content;
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
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
	}
	//设置是否可以修改,true表示可以修改,false表示不可修改
	function setModifiable(modifiable){
		$('#requestFromUnitId').combobox('readonly', !modifiable);
		$('#latestFeedbackDateId').datebox('readonly', !modifiable);
		showDiv("btnSave", modifiable);
		showDiv("btnDelete", modifiable);
		showDiv("btnCompleteFeedback", modifiable);
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
				   "<p style=\"line-height:18px\"><span style=\"font-size:29px;font-family:宋体;font-variant:small-caps\">&nbsp;</span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
				   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于《"+filename+"》的征求意见函</span></p>";
				  
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
		<div id="cc" class="easyui-layout  cc" >
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
							<div id="btnCompleteFeedback" class="button_change red center" style="width: 88px" >结束反馈意见</div>
						</td>
					</tr>
				</table>

				<div>
					<form id="downloadId" class="downloadId" name="download" action="" method="post"></form>
				</div>

				<div id="dataDivWindow" class="dataDivWindow" >
					<table  cellpadding="0" cellspacing="0" class="spe_table dataTable">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="norId" name="normativeFileId" type="hidden" /> 
									<input id="id" name="requestCommentId" type="hidden" /> 
									<input id="nameId" name="requestCommentName" readonly="readonly" class="fileName" />
								</td>
								<td class="td-btn-find">
									<div id="btnOpen" class="button_change red center">查询</div>
								</td>
							</tr>
							<tr>
								<td class="td-label">主起草单位</td>
								<td class="td-data">
									<input id="draftingUnitId" name="draftingUnit" readonly="readonly" class="td-3data" />
								</td>
								<td class="td-label">主起草单位负责人</td>
								<td class="td-data">
									<input id="draftingUnitLeaderId" name="draftingUnitLeader"  readonly="readonly" class="td-3data" />
								</td>
								<td class="td-label">主起草单位经办员</td>
								<td class="td-data">
									<input id="draftingUnitClerkId" name="draftingUnitClerk"  readonly="readonly" class="td-3data" />
								</td>
								<td></td>
							</tr>
							<tr>
							<td class="td-label">反馈单位</td>
								<td class="td-data"><input
									class="easyui-combobox" id="requestFromUnitId" name="requestFromUnit"
									data-options="valueField:'id',textField:'text'," value="" /></td>
								<td class="td-label">最晚反馈时间</td>
								<td class="td-data">
									<input id="latestFeedbackDateId" type="text" class="easyui-datebox"/>
								</td>
							</tr>
							<tr>
								<td class="td-label">征求意见稿</td>
								<td colspan="6" class="td-upload-file">
									<div id="requestingDraftId"></div>
								</td>
							</tr>
							<tr>
								<td style="width: 120px;">征求意见函内容</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="editor" type="text/plain" class="td-editor"></script>
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
		<div id="viewContent" class="view-file"></div>
	</div>
	<div id="searchrequestComment" title="征求意见查询" class="easyui-window"
		data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 600, height: 300, padding: 0">
		<table class="search-fileName">
			<tr>
				<td>文件名称:</td>
				<td><input id="searchNameId" name="searchName" size="60" /></td>
				<td><input id="btnSearch" type="button" value="查询" /></td>
			</tr>
		</table>

		<table id="searchDataGrid"  data-options="pagination:true, singleSelect:true, padding:0,  rownumbers:true, height: 200">
			<thead>
				<tr>
					<th data-options="field:'id',hidden:true">id</th>
					<th data-options="field:'name',width:410">文件名</th>
					<th data-options="field:'requestFromUnit',halign:'center',width:120,formatter:function(value,row){
                            return row.requestFromUnit.text;
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