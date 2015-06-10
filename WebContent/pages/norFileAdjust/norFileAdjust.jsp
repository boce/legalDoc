<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件调整</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/norFileAdjust.css"/>
<script type="text/javascript">
	$(function() {
		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('#statusId').combobox({
			url : '${pageContext.request.contextPath}/norFileAdjust/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
	

		$("#btnOpen").click(function(){
			openFrame('');
		});
		
	
		$("#btnSave").click(function() {
			var norId = $('#norId').val();
			var status = $('#statusId').combobox('getValue');
			var invalidReason = $('#invalidReasonId').val();
			if(norId == ""){
				showMsg("请先选择一个规范性文件！");
				return;
			}
			if(status == ""){
				showMsg("请选择文件状态！");
				return;
			}
			if(invalidReason == ""){
				showMsg("请填写修订、撤销、废止原因！");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/norFileAdjust/save.do",
				data : {
					id : norId,
					status : status,
					invalidReason : invalidReason
				},
				success : function(data) {
	
				},
				error : function(data) {
					alert("保存出错,请重试!");
				}
			});
		});
		
		$("#btnAdd").click(function(){
			clear();
		});
		
		$("#btnList").click(function(){
			location.href="${pageContext.request.contextPath}/norFileAdjust/norFileAdjustList.wf";
		});
	});
	function clear(){
		$(":input").not(':button').val('');
		$("#invalidReasonId").val('');
		$('#statusId').combobox('clear');
		$('#draftingInstructionId').empty();
		$('#legalDocId').empty();
		$('#legalBasisId').empty();
	}
	function load(id) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/norFileAdjust/load.do",
			data : {
				norId : id
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$("#norId").val(data.id);
					$("#norFileAdjustId").val(data.name);
					$("#decisionUnitId").val(data.decUnit.text);
					$("#decisionUnitLeaderId").val(data.decUnitLeader.name);
					$("#decisionUnitClerkId").val(data.decUnitClerk.name);
					$("#draftingUnitId").val(data.drtUnit.text);
					$("#draftingUnitLeaderId").val(data.drtUnitLeader.name);
					$("#draftingUnitClerkId").val(data.drtUnitClerk.name);
					var invalidReason = data.invalidReason;
					if(invalidReason == null){
						invalidReason = "";
					}
					$("#invalidReasonId").text(invalidReason);

					//生成起草说明的浏览
					var insStr = data.draftInstruction;
					if (insStr != null && insStr.length > 0) {
						$('#draftingInstructionId').empty();
						$("#draftingInstructionId").append(
								"<a class='insclass' href='#' style='cursor: pointer;'><div>" + insStr + "</div></a>");
						$(".insclass").bind('click', function() {
							view(insStr, 'INSTRUCTION');
						});
					}
					//生成规范性文件的浏览
					var norStr = data.legalDoc;
					if (norStr != null && norStr.length > 0) {
						$('#legalDocId').empty();
						$("#legalDocId").append(
								"<a class='norclass' href='#' style='cursor: pointer;'><div>"
										+ norStr + "</div></a>");
						$(".norclass").bind('click', function() {
							view(norStr, 'LEGALDOC');
						});
					}
					
					//生成相关依据浏览
					var lgStr = data.legalBasis;
					if (lgStr != null && lgStr.length > 0) {
						$('#legalBasisId').empty();
						var arrStr = [];
						arrStr = lgStr.split(";");
						$.each(arrStr, function(index, tx) {
							$("#legalBasisId").append(
									"<a class='attachment" + index + "' href='#' style='cursor: pointer;'><div>"
											+ tx + "</div></a>");
							$(".attachment" + index).bind('click', function() {
								view(tx, 'LEGALBASIS');
							});
						});
					}
					var statusval = data.status;
					if (statusval != null) {
						$('#statusId').combobox('select', statusval);
					}
				}
			},
			error : function(data) {
				alert("加载出错,请重试!");
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
			<div data-options="region:'center',border:false,split:false, width: 1200, height: 680" >
				<table  class="table">
					<tr>
						<td id="btnTools" style="text-align: left;">
						    <div id="btnAdd" class="button_change red center">新增</div>
							<div id="btnSave" class="button_change red center">保存</div> 
							<div id="btnList" class="button_change red center">列表</div> 
						</td>
					</tr>
				</table>

				<div id="dataDivWindow"  class="dataDivWindow" >
					<table cellpadding="0" cellspacing="0" class="spe_table dataTable">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="norId" name="normativeFileId" type="hidden" />
									<input id="norFileAdjustId" name="norFileAdjustName" class="fileName" readonly="readonly"/>
								</td>
								<td class="td-btn-find">
									<div class="button_change red center" id="btnOpen">查询</div>
								</td>
							</tr>
							<tr>
								<td class="td-label">制定单位</td>
								<td class="td-data">
								<input id="decisionUnitId" name="decisionUnit" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td class="td-label">制定单位负责人</td>
								<td class="td-data">
								<input id="decisionUnitLeaderId" name="decisionUnitLeader" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td class="td-label">制定单位经办员</td>
								<td class="td-data">
								<input id="decisionUnitClerkId" name="decisionUnitClerk" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">主起草单位</td>
								<td class="td-data">
								<input id="draftingUnitId" name="draftingUnit" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td class="td-label">主起草单位负责人</td>
								<td class="td-data">
								<input id="draftingUnitLeaderId" name="draftingUnitLeader" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td class="td-label">主起草单位经办员</td>
								<td class="td-data">
								<input id="draftingUnitClerkId" name="draftingUnitClerk" style="width: 100%;border:0;" readonly="readonly"/>
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">状态</td>
								<td class="td-data">
								<input id="statusId" name="status"  style="border:0;"/>
								</td>
								<td colspan="5"></td>
							</tr>
							<tr>
								<td class="td-label">规范性文件</td>
								<td colspan="6" class="td-data">
									<div id="legalDocId" name="legalDoc" ></div>
								</td>
							</tr>
							<tr>
								<td class="td-label">起草说明</td>
								<td colspan="6" class="td-data">
									<div id="draftingInstructionId" name="draftingInstruction" ></div>
								</td>
							</tr>
							<tr>
								<td class="td-label">相关依据</td>
								<td colspan="6" class="td-data">
									<div id="legalBasisId" name="legalBasis" ></div>
								</td>
							</tr>
							<tr>
								<td >修订、撤销、废止原因</td>
								<td colspan="6" style="text-align: left;">
									<textarea id="invalidReasonId" name="invalidReason" class="td-editor" ></textarea>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>

		</div>
	<!-- 浏览页面 -->
	<div id="view" title="文件浏览"  class="easyui-window" data-options="collapsible:false, 
		inline:false, minimizable:false, maximizable:false, closed:true,resizable:false" >
	   <div id="viewContent" class="view-file"></div>
	</div>
</body>
</html>