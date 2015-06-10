<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	var validDate = {"2":"2年", "5":"5年"};
	var priority = eval('(${priority})');
	var stage = eval('(${stage})');
	$(function() {
		
	});

	var unionDratUnitIndex = 0;
	//添加联合起草单位tr
	function addDraftUnion(unionDrtUnit, unionDrtUnitLeader){
		unionDratUnitIndex++;
		var draftUnionHtml = "<tr class=\"trH\" id=\"unionDrtUnit"+unionDratUnitIndex+"\">"
							+"<td class=\"td-label\">联合起草单位</td>"
							+"<td class=\"td-data\">"
							+"<input readonly=\"readonly\" class=\"td-3data\" value=\""+unionDrtUnit+"\"/>"
							+"</td>"
							+"<td class=\"td-label\">联合起草单位负责人</td>"
							+"<td class=\"td-data\">"
							+"<input readonly=\"readonly\" class=\"td-3data\" value=\""+unionDrtUnitLeader+"\"/>"
							+"</td>"
							+"<td></td>"
							+"<td></td>"
							+"</tr>";
		return draftUnionHtml;
	}
	
	//移除联合起草tr
	function removeDraftUnion(){
		if(unionDratUnitIndex > 0)
		{
			for(var i=1; i<=unionDratUnitIndex; i++){
				$("#unionDrtUnit" + i).remove();
			}
		}
		unionDratUnitIndex = 0;
	}
	
	function load(id) {
		clear();
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/home/loadNorFile.do",
			data : {
				norId : id,
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$("#norId").val(data.id);
					$("#nameId").val(data.name);
					$("#decUnit").val(data.decUnit.text);
					$("#decUnitLeader").val(data.decUnitLeader.name);
					$("#decUnitClerk").val(data.decUnitClerk.name);
					$("#drtUnit").val(data.drtUnit.text);
					$("#drtUnitLeader").val(data.drtUnitLeader.name);
					$("#drtUnitClerk").val(data.drtUnitClerk.name);
					var unionDrtUnitName = data.unionDrtUnitName;
					if (unionDrtUnitName != null && unionDrtUnitName != "") {
						var unionDraftUnits = unionDrtUnitName.split(",");
						var unionDraftUnitLeaders = data.unionDrtUnitLeaderName.split(",");
						var unionDrtUnitHtml = "";
						for(var i=0; i<unionDraftUnits.length; i++){
							unionDrtUnitHtml += addDraftUnion(unionDraftUnits[i], unionDraftUnitLeaders[i]);
						}
						$("#drtUnitTr").after(unionDrtUnitHtml);
					}
					$("#revUnit").val(data.revUnit.text);
					$("#revUnitLeader").val(data.revUnitLeader.name);
					$("#revUnitClerk").val(data.revUnitClerk.name);
					$("#recRevUnit").val(data.revUnit.text);
					$("#recRevUnitLeader").val(data.revUnitLeader.name);
					$("#recRevUnitClerk").val(data.revUnitClerk.name);
					$("#applyDate").val(data.applyDate);
					$("#draftDate").val(data.draftDate);
					$("#publishDate").val(data.publishDate);
					$("#registerDate").val(data.registerDate);
					$("#invalidDate").val(data.invalidDate);
					$("#validDate").val(validDate[data.validDate]);
					$("#priority").val(priority[data.priority]);
					$("#fileStatus").val(fileStatus[data.status]);
					$("#stage").val(stage[data.stage]);
					$("#publishNo").val(data.publishNo);
					$("#registerCode").val(data.registerCode);
					var legalDoc = data.legalDoc;
					if (legalDoc != null && legalDoc.length > 0) {
						$('#legalDocDiv').empty();
						var strs = legalDoc.split(";");
						$.each(strs,function(index, tx) {
							if($("#legalDocDiv").html() != ""){
								$("#legalDocDiv").append("；");
							}
							$("#legalDocDiv").append('<a class="leg" href="#" style="cursor: pointer;">'+ tx+ '</a>');
							$(".leg").bind('click',function(){  
								displayContent(tx, "LEGALDOC", "规范性文件浏览");
						    });
						});
					}
					var draftingInstruction = data.draftInstruction;
					if (draftingInstruction != null && draftingInstruction.length > 0) {
						$('#draftInstructionDiv').empty();
						var strs = draftingInstruction.split(";");
						$.each(strs,function(index, tx) {
							if($("#draftInstructionDiv").html() != ""){
								$("#draftInstructionDiv").append("；");
							}
							$("#draftInstructionDiv").append('<a class="dra" href="#" style="cursor: pointer;">'+ tx+ '</a>');
							$(".dra").bind('click',function(){  
								displayContent(tx, "INSTRUCTION", "起草说明浏览");
						    });
						});
					}
					var legalBasis = data.legalBasis;
					if (legalBasis != null && legalBasis.length > 0) {
						$('#legalBasisDiv').empty();
						var strs = legalBasis.split(";");
						$.each(strs,function(index, tx) {
							if($("#legalBasisDiv").html() != ""){
								$("#legalBasisDiv").append("；");
							}
							$("#legalBasisDiv").append('<a class="legb'+ index +' " href="#" style="cursor: pointer;">'+ tx+ '</a>');
							$(".legb" +  index).bind('click',function(){  
									displayContent(tx, "LEGALBASIS", "相关依据浏览");
							});
		   				});
					}
					var legalBasisNoAtta = data.legalBasisNoAtta;
					if (legalBasisNoAtta != null && legalBasisNoAtta.length > 0) {
						var strs = legalBasisNoAtta.split(";");
						$.each(strs,function(index, tx) {
							if($("#legalBasisDiv").html() != ""){
								$("#legalBasisDiv").append("；");
							}
							$("#legalBasisDiv").append(tx);
		   				});
					}
					$("#invalidReasonDiv").html(data.invalidReason);
					/* var moreFiles = data.moreFiles;
					if (moreFiles != null && moreFiles.length > 0) {
						var strs = new Array();
						strs = data.legalBasis.split(";");
						$.each(strs,function(index, tx) {
							if($("#moreFilesDiv").html() != ""){
								$("#moreFilesDiv").append("；");
							}
							$("#moreFilesDiv").append('<a class="moref'+ index +' " href="#" style="cursor: pointer;">'+ tx+ '</a>');
							$(".moref" +  index).bind('click',function(){
								
							});
		   				});
					} */
				}
			},
			error : function(data,textStatus, errorThrown) {
				showMsg("加载出错,请重试!");
			}
		});
	}
	
	function clear(){
		$("#detailForm").form("clear");
		removeDraftUnion();
		$("#legalDocDiv").html("");
		$("#draftInstructionDiv").html("");
		$("#legalBasisDiv").html("");
		$("#invalidReasonDiv").html("");
		//$("#moreFilesDiv").html("");
	}
	
	function displayContent(fileName, fileType, title) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/home/gainContent.do",
				  data : {
					  norId : norId,
					  fileName:fileName,
					  fileType:fileType
				  },
				  dataType : 'html',
				  success : function(data){
					  $('#fileDocContent').html(data);
					  $('#fileDoc').window('open');	//打开文件浏览
					  $('#fileDoc').window('center');
					  $('#fileDoc').window('setTitle', title);
				  },
				  error : function(data) {
					  showMsg(data.responseText);
				  }
				});
		} else {
			showMsg("请选择文件！");
		}
	}
</script>
</head>
<body>
	<div id="displayWindow" title="规范性文件" class="easyui-window" data-options="collapsible:false,minimizable:false,maximizable:false,closed:true,resizable:false,draggable:false,width:1195,height:570">
		<div style="height:100%;overflow: auto;">
			<form id="detailForm" action="">
				<input id="norId" name="norId" type="hidden"/>
				<table cellpadding="0" cellspacing="0" class="spe_table">
					<tbody>
						<tr class="trH">
							<td class="td-label">文件名称</td>
							<td colspan="5" class="alignLeft">
								<input id="nameId" name="name" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">制定单位</td>
							<td class="td-data">
								<input id="decUnit" name="decUnit" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">制定单位负责人</td>
							<td class="td-data">
								<input id="decUnitLeader" name="decUnitLeader" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">制定单位经办员</td>
							<td class="td-data">
								<input id="decUnitClerk" name="decUnitClerk" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr id="drtUnitTr" class="trH">
							<td class="td-label">主起草单位</td>
							<td class="td-data">
								<input id="drtUnit" name="drtUnit" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">主起草单位负责人</td>
							<td class="td-data">
								<input id="drtUnitLeader" name="drtUnitLeader" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">主起草单位经办员</td>
							<td class="td-data">
								<input id="drtUnitClerk" name="drtUnitClerk" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">合法性审查单位</td>
							<td class="td-data">
								<input id="revUnit" name="revUnit" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">合法性审查单位负责人</td>
							<td class="td-data">
								<input id="revUnitLeader" name="revUnitLeader" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">合法性审查单位经办员</td>
							<td class="td-data">
								<input id="revUnitClerk" name="revUnitClerk" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">备案审查单位</td>
							<td class="td-data">
								<input id="recRevUnit" name="recRevUnit" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">备案审查单位负责人</td>
							<td class="td-data">
								<input id="recRevUnitLeader" name="recRevUnitLeader" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label">备案审查单位经办员</td>
							<td class="td-data">
								<input id="recRevUnitClerk" name="recRevUnitClerk" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">申请日期</td>
							<td class="td-data">
								<input id="applyDate" name="applyDate" type="text" readonly="readonly" class="td-3data"/>
							</td>
							<td class="td-label">起草日期</td>
							<td class="td-data">
								<input id="draftDate" name="draftDate" type="text" readonly="readonly" class="td-3data">
							</td>
							<td class="td-label">发布日期</td>
							<td class="td-data">
								<input id="publishDate" name="publishDate" type="text" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">备案日期</td>
							<td class="td-data">
								<input id="registerDate" name="registerDate" type="text" readonly="readonly" class="td-3data"></input>
							</td>
							<td class="td-label">失效日期</td>
							<td class="td-data">
								<input id="invalidDate" name="invalidDate" type="text" readonly="readonly" class="td-3data"></input>
							</td>
							<td class="td-label">有效期</td>
							<td class="td-data">
								<input id="validDate" name="validDate" type="text" readonly="readonly" class="td-3data"></input>
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">阶段</td>
							<td class="td-data">
								<input id="stage" name="stage" type="text" readonly="readonly" class="td-3data"></input>
							</td>
							<td class="td-label">优先级</td>
							<td class="td-data">
								<input id="priority" name="priority" type="text" readonly="readonly" class="td-3data"></input>
							</td>
							<td class="td-label">状态</td>
							<td class="td-data">
								<input id="fileStatus" name="fileStatus" type="text" readonly="readonly" class="td-3data" />
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">发文号</td>
							<td class="td-data">
								<input id="publishNo" name="publishNo" type="text" readonly="readonly" class="td-3data"></input>
							</td>
							<td class="td-label">备案号</td>
							<td class="td-data">
								<input id="registerCode" name="registerCode" type="text" readonly="readonly" class="td-3data" />
							</td>
							<td class="td-label"></td>
							<td class="td-data">
								
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">规范性文件</td>
							<td colspan="5" class="alignLeft">
								<div id="legalDocDiv"></div>
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">起草说明</td>
							<td colspan="5" class="alignLeft">
								<div id="draftInstructionDiv"></div>
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">相关依据</td>
							<td colspan="5" class="alignLeft">
								<div id="legalBasisDiv"></div>
							</td>
						</tr>
						<tr class="trH">
							<td class="td-label">修订、撤销、废止原因</td>
							<td colspan="5" class="alignLeft">
								<div id="invalidReasonDiv"></div>
							</td>
						</tr>
						<!--  
						<tr class="trH">
							<td class="td-label">更多文件</td>
							<td colspan="5" class="alignLeft">
								<div id="moreFilesDiv"></div>
							</td>
						</tr>
						-->
					</tbody>
				</table>
			</form>
		</div>
		
	</div>
	<!-- 弹出浏览文件页面 -->
	<div id="fileDoc" title="规范性文件浏览"  class="easyui-window" data-options="collapsible:false,inline:false,minimizable:false,maximizable:false,closed:true">
	   <div id="fileDocContent" class="view-file"></div>
	</div>
</body>
</html>