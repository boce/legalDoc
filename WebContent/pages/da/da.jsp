<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>期满评估</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link  rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/da.css"/>
<script type="text/javascript">

	var editor = null;	
	var html = '';
	var isHide = false;

	$(function(){
		//显示的页面按钮
		displayBtn();
		var listId = "${param.id}";
		if (listId != "") {
			load(parseInt(listId, 10), 'DEFERRED');
		}
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		
		//设置评估结果
		$('#assessResult').combobox({
			url : '${pageContext.request.contextPath}/da/getResultStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		
		$("#btnOpen").click(function(){
			openFrame('');
		});
		
		$('#addBtn').click(function(){
			cleanPage();
		});
		
		$("#listBtn").click(function() {
			location.href = "${pageContext.request.contextPath}/da/daList.wf";
		});
		
		$('#saveBtn').click(function(){ 	//保存送审稿审查
			var norId = $('#norId').val();
			if(norId == ""){
				showMsg("文件名称不能为空");
				return;
			}
			var validateFlag = $("#formId").form('validate');
			if (validateFlag) {
				
				var daId = $('#daId').val();
				var assessResult = $("#assessResult").combobox('getValue');
				var assessDate = $("#assessDate").datebox('getValue');
				var validDate = $("#validDate").datebox('getValue');
				if(assessResult == "DIRECT_DELAY" && validDate == ""){
					showMsg("请填写失效日期！");
					return;
				}
				var assessComment = $('#assessComment').val();
				
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/da/save.do",
					  data : {id : daId, 'normativeFile.id' : norId, 'assessResult' : assessResult, 
						  	  'assessDate' : assessDate, 'validDate' : validDate, 'assessComment' : assessComment},
					  dataType : 'json',
					  success : function(data)
					  {
						 if (data.msg == "success") {
							 $('#daId').val(data.id);
							 showMsg("保存成功!");
						 } else {
							 showMsg("保存失败!");
						 }
					  },
					  error : function(data)
					  {
						  showMsg("保存出错,请重试!");
					  }
					});
			} else {
				showMsg("请填上表单必填项!");
			}
			
		});
		
		$('#deleteBtn').click(function(){ 
			var daId = $('#daId').val();
			if (null != daId && "" != daId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/da/delete.do",
					  data : {id : daId},
					  dataType : 'json',
					  success : function(data)
					  {
						  if (data.msg == 'success') {
		                		showMsg("删除成功!");
		                		cleanPage();	//清空页面
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
				showMsg("请选择期满评估文件!");
			}
		});
		
		$('#searchBtn').click(function(){
			$('#dataGrid').datagrid("reload");
			$('#searchDA').window('open'); 
		});
		
		//查询页面相关组件初始化
		$('#sStatus').combobox({
			url : '${pageContext.request.contextPath}/da/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		
		$("#btnQuery").click(function() {
			$('#dataGrid').datagrid('load', {
				name : $('#sDaName').val(),
				draftingUnit : $('#sDraftingUnit').combotree('getValue'),
				decisionUnit : $('#sDecisionUnit').combotree('getValue'),
				status : $('#sStatus').combobox('getValue')
			});
		});

		$('#dataGrid').datagrid({
			url : '${pageContext.request.contextPath}/da/searchAll.do',
			onDblClickRow: function (rowIndex, rowData) {
				$('#dataGrid').datagrid('selectRow',rowIndex);
				var selectRow =$("#dataGrid").datagrid("getSelected");
				var id=selectRow.id;
				location.href = "${pageContext.request.contextPath}/da/da.wf?id="+id;
				$('#searchDA').window('close'); 
			}
		});
		
		//初始化combotree
		$('#sDraftingUnit').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		showMsg('只能选择部门!');
	        		return false; 
	        	}
            }
		}); 
		
		$('#sDecisionUnit').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		showMsg('只能选择部门!');
	        		return false; 
	        	}
            }
		}); 
		
		
		//查询确认按钮
		$('#confirm').click(function() {
			var selectRow =$("#dataGrid").datagrid("getSelected");
			if(selectRow == null){
				return;
			}
			var id=selectRow.id;
			load(id, 'DEFERRED');
			$('#searchDA').window('close'); 
		});
		
		//查询时取消按钮
		$('#cancel').click(function() {
			$('#searchDA').window('close'); 
		});
		
		
		
	});
	
	//清除页面内容
	function cleanPage() {
		$('#norId').val('');
		$('#daId').val('');
		
		$('#daName').val('');
		$("#decisionUnit").val('');
		$("#decisionUnitLeader").val('');
		$("#decisionUnitClerk").val('');
		
		$("#draftingUnit").val('');
		$("#draftingUnitLeader").val('');
		$("#draftingUnitClerk").val('');
		
		$("#assessResult").combobox('select', '');
		$("#validDate").datebox('setValue', '');
		$("#assessDate").datebox('setValue', '');
		
		$("#viewNorFile").html('');
		$("#viewInstruction").html('');
		$("#legalBasis").html('');
		
		$('#assessComment').val('');
	}
	
	function load(id, sreachType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/da/load.do",
			data : {
				id : id,
				sreachType : sreachType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$('#norId').val(data.normativeFile.id);
					$('#daId').val(data.id);
					
					$('#daName').val(data.normativeFile.name);
					$("#decisionUnit").val(data.normativeFile.decUnit.text);
					$("#decisionUnitLeader").val(data.normativeFile.decUnitLeader.name);
					$("#decisionUnitClerk").val(data.normativeFile.decUnitClerk.name);
					
					$("#draftingUnit").val(data.normativeFile.drtUnit.text);
					$("#draftingUnitLeader").val(data.normativeFile.drtUnitLeader.name);
					$("#draftingUnitClerk").val(data.normativeFile.drtUnitClerk.name);
					
					$("#assessResult").combobox('select', data.assessResult);
					$("#validDate").datebox('setValue', data.validDate);
					$("#assessDate").datebox('setValue', data.assessDate);
					var assessComment = data.assessComment;
					if(assessComment == null){
						assessComment = "";
					}
					$('#assessComment').text(assessComment);
					
					//$("#viewNorFile").html(data.normativeFile.legalDoc);
					//$("#viewInstruction").html(data.normativeFile.draftInstruction);
					//$("#viewLegalBasis").html(data.normativeFile.legalBasis);
					
					
					//生成起草说明的浏览
					var insStr = data.normativeFile.draftInstruction;
					if (insStr != null && insStr.length > 0) {
						$('#viewInstruction').empty();
						$("#viewInstruction").append(
								"<a class='insclass' href='#' style='cursor: pointer;'><div>"
										+ insStr + "</div></a>");
						$(".insclass").bind('click', function() {
							view(insStr, 'INSTRUCTION');
						});
					}
					//生成规范性文件的浏览
					var norStr = data.normativeFile.legalDoc;
					if (norStr != null && norStr.length > 0) {
						$('#viewNorFile').empty();
						$("#viewNorFile").append(
								"<a class='norclass' href='#' style='cursor: pointer;'><div>"
										+ norStr + "</div></a>");
						$(".norclass").bind('click', function() {
							view(norStr, 'LEGALDOC');
						});
					}
					
					//生成相关依据浏览
					var lgStr = data.normativeFile.legalBasis;
					if (lgStr != null && lgStr.length > 0) {
						$('#legalBasis').empty();
						var arrStr = [];
						arrStr = lgStr.split(";");
						$.each(arrStr, function(index, tx) {
							$("#legalBasis").append(
									"<a class='attachment" + index + "' href='#' style='cursor: pointer;'><div>"
											+ tx + "</div></a>");
							$(".attachment" + index).bind('click', function() {
								view(tx, 'LEGALBASIS');
							});
						});
					}
					
					
				}
			},
			error : function(data) {
				showMsg("期满评估文件加载出错!");
			}
		});
	}
	
	function view(fileName, fileType) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
					type : "POST",
					url : "${pageContext.request.contextPath}/da/gainFileContent.do",
					data : {
						norId : norId,
						fileType : fileType,
						fileName : fileName
					},
					dataType : 'html',
					success : function(data) {
						$('#viewContent').html('');
						$('#viewContent').html(data);
						$('#view').window('open'); 		//打开浏览页面
					},
					error : function(data) {
						showMsg("加载出错,请重试!");
					}
				});
		} else {
			showMsg("请选择规范性文件或期满评估！");
		}

	}
	
</script>

</head>
<body>
	    <div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680">
	    	<table class="table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div class="button_change red center" id="addBtn" >新增</div>
	    				<div class="button_change red center" id="saveBtn" >保存</div>
	    				<div class="button_change red center" id="deleteBtn" >删除</div>
	    				<div class="button_change red center" id="submitBtn" >提交</div>
	    				<div class="button_change red center" id="approveBtn" >审核</div>
	    				<div class="button_change red center" id="unApproveBtn" >弃审</div>
	    				<div class="button_change red center" id="flowBtn" >流程</div>
	    				<div class="button_change red center" id="examBtn" >公布</div>
						<div class="button_change red center" id="listBtn" >列表</div>
	    				<div class="button_change red center" id="searchBtn" >查找</div>
	    			</td>
	    		</tr>
	    	</table>
	    	<form id="formId" action="">
	    	<div id="dataDivWindow"  class="dataDivWindow" >
		    	<table  class="spe_table dataTable" id="tableId">
					<tbody id="cleanTable">
						<tr>
							<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="norId" name="normativeFileId" type="hidden" />
									<input id="daId" name="daIdName" type="hidden" /> 
									<input id="daName" readonly="readonly" class="fileName" />
								</td>
								<td class="td-btn-find">
									<div id="btnOpen" class="button_change red center">查询</div>
								</td>
						</tr>
						<tr>
							<td class="td-label">制定单位</td>
							<td class="td-data">
								<input id="decisionUnit"  name="decisionUnitName" value="" readonly="readonly"/>
							</td>
							<td class="td-label">制定单位负责人</td>
							<td class="td-data">
								<input id="decisionUnitLeader" name="decisionUnitLeaderName" readonly="readonly"/>
							</td>
							<td class="td-label">制定单位经办员</td>
							<td class="td-upload-file" colspan="2">
								<input id="decisionUnitClerk" name="decisionUnitClerkName" readonly="readonly"/> 
							</td>
						</tr>
						<tr>
							<td class="td-label">主起草单位</td>
							<td class="td-data">
								<input id="draftingUnit"  name="draftingUnitName" value="" readonly="readonly"/>
							</td>
							<td class="td-label">主起草单位负责人</td>
							<td class="td-data">
								<input id="draftingUnitLeader" name="draftingUnitLeaderName" readonly="readonly"/>
							</td>
							<td class="td-label">主起草单位经办员</td>
							<td class="td-upload-file" colspan="2">
								<input id="draftingUnitClerk" name="draftingUnitClerkName" readonly="readonly"/> 
							</td>
						</tr>
						<tr>
							<td class="td-label">评估结果</td>
							<td class="td-data">
								<input id="assessResult"  name="assessResultName" class="easyui-combobox" data-options="required:'required'"/>
							</td>
							<td class="td-label">失效日期</td>
							<td class="td-data">
								<input id="validDate" name="validDateName" class="easyui-datebox"/>
							</td>
							<td class="td-label">评估日期</td>
							<td style="height:20px; text-align: left" colspan="2">
								<input id="assessDate" name="assessDateName" 
								class="easyui-datebox" data-options="required:'required'" /> 
							</td>
						</tr>
						<tr>
							<td class="td-label">规范性文件</td>
							<td colspan="6" class="td-upload-file">
								<div id="viewNorFile"></div>
							</td>
						</tr>
						<tr>
							<td class="td-label">起草说明</td>
							<td colspan="6"  class="td-upload-file">
								<div id="viewInstruction"></div>
							</td>
						</tr>
						<tr>
							<td class="td-label">相关依据</td>
							<td colspan="6"  class="td-upload-file">
								<div id="legalBasis"></div>
							</td>
						</tr>
						<tr>
							<td >评估意见</td>
							<td colspan="6" class="td-file-content">
								<textarea id="assessComment" name="assessCommentName" class="td-editor" ></textarea>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			</form>
		</div>
		</div>
	    
	<!-- 浏览页面 -->
	<div id="view" title="文件浏览"  class="easyui-window" data-options="collapsible:false, 
		inline:false, minimizable:false, maximizable:false, closed:true, resizable:false" >
	   <div id="viewContent" class="view-file"></div>
	</div>
	
	<!-- 弹出清理查询页面 -->
	<div id="searchDA" title="期满评估文件查询" class="easyui-window" data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 796, height: 446, padding: 0">
		<table cellpadding="0" cellspacing="0" class="table">
				<tr>
					<td class="td-label">文件名称</td>
					<td class="td-data">
						<input id="sDaName" name="width: 90%;" />
					</td>
					<td class="td-label">起草单位</td>
					<td class="td-data">
						<input id="sDraftingUnit" class="easyui-combotree" />
					</td>
				</tr>
				<tr>
					<td class="td-label">制定单位</td>
					<td class="td-data">
						<input id="sDecisionUnit" class="easyui-combotree" />
					</td>
					<td class="td-label">状态</td>
					<td class="td-data">
						<input id="sStatus" type="text" class="easyui-combobox" />
					</td>
						
					<td class="td-btn-find"><input
						id="btnQuery" type="button" style="margin: 0; padding: 0;"
						value="查询" /></td>
				</tr>
			</table>

			<table id="dataGrid" data-options="fitColumns:true,singleSelect:true,
					pagination:true,rownumbers:true,width:780,height:306,border:0">
				<thead>
					<tr>
						<th data-options="field:'id',halign:'center',hidden:true">id</th>
						<th data-options="field:'name',halign:'center',width:200">文件名称</th>
						<th data-options="field:'draftingUnit',halign:'center',width:120,formatter:function(value,row){
                            return row.draftingUnit.text;
                        }">起草单位</th>
						<th data-options="field:'decisionUnit',halign:'center',width:120,formatter:function(value,row){
                            return row.decisionUnit.text;
                        }">制定单位</th>
						<th data-options="field:'validDate',halign:'center',width:80">有效期</th>
						<th data-options="field:'assessDate',halign:'center',width:80">评估日期</th>
						<th data-options="field:'status',halign:'center',width:80,formatter:function(value,row){
							if(value == 'OPEN')
                            	return '开立';
                            if(value == 'SUBMIT')
                            	return '提交';
                            if(value == 'APPROVE')
                            	return '审核';
                            if(value == 'UNAPPROVE')
                            	return '弃审';
                        }">状态</th>
						<th data-options="field:'assessResult',halign:'center',width:80,formatter:function(value,row){
                            if(value == 'DIRECT_DELAY')
                            	return '直接延期';
                            if(value == 'MODIFY_DELAY')
                            	return '修订延期';
                            if(value == 'REVOKE')
                            	return '撤销';
                        }">评估结果</th>
						
					</tr>
				</thead>
				<tbody></tbody>
			</table>

		<table class="search-button">
			<tr>
				<td width="320px"></td>
				<td><input id="confirm" type="button" value="确认" /></td>
				<td><input id="cancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
	
</body>
</html>