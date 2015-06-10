<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>送审稿审查</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link  rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/examDraftReview.css"/> 
<script type="text/javascript">
	var editor = null;	
	var init = true;	
	$(function(){
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
			load(parseInt(listId, 10), 'DRAFTREVIEW');
		}
		
		$('#saveBtn').click(function(){ 	//保存送审稿审查
			var norId = $('#norId').val();
			if(norId == ""){
				showMsg("文件名称不能为空");
				return;
			}
			var name = $('#examDraftReviewNameId').val();
			var revDate = $('#reviewDateId').datebox('getValue');
			var reviewComment = '';
			reviewComment = editor.getContent();
			if (revDate == null || revDate == '') {
				showMsg("请选择审核时间!");
				return;
			}
			var isNeedModifyVal = $("input[name=isNeedModify]:checked").val();
			if(typeof(isNeedModifyVal) == "undefined" || isNeedModifyVal == ""){
				showMsg("请选择是否需要修改送审稿！");
				return;
			}
			var isNeedModify = isNeedModifyVal == "0" ? true : false;
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/examDraftRev/addExamDraftRev.do",
				  data : {norId : norId, name : name, reviewDate : revDate, reviewComment : reviewComment, isNeedModify:isNeedModify},
				  dataType : 'json',
				  success : function(data)
				  {
					 if (data.msg == "success") {
						 showMsg("保存成功!");
					 } else if (data.msg == "fail") {
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
		});
		
		$('#addBtn').click(function(){ 	//新增
			cleanPage();
			setModifiable(true);
		});
		
		$("#listBtn").click(function() {
			location.href = "${pageContext.request.contextPath}/examDraft/examDraftReviewList.wf";
		});
		
		$('#exportBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
			$("#downloadId").attr("action", "${pageContext.request.contextPath}/examDraftRev/downloadWord.do?norId=" + norId);
			$("#downloadId").submit();
		});
		
		
		$('#printBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type : "post",
					  url : "${pageContext.request.contextPath}/examDraftRev/gainFileContent.do",
					  data : {norId : norId, fileType : "REVIEWCOMMENT"},
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
				showMsg("请选择规范性文件或送审稿审核！");
			}
		});
		
		
		$('#deleteBtn').click(function(){ 
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/examDraftRev/delete.do",
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
			$('#dataGrid').datagrid("reload");
			$('#searchReview').window('open');
		});
		
		
		//送审稿审查查询 begin-->
		
		$('#cancel').click(function(){
			$('#searchReview').window('close');
		});
		
		
		$('#confirm').click(function(){
			var selectRow =$("#dataGrid").datagrid("getSelected");
			var id=selectRow.id;
			cleanPage();
			load(id, 'DRAFTREVIEW');
			$('#searchReview').window('close');
		});
		
		$('#sStatus').combobox({
			url : '${pageContext.request.contextPath}/examDraftRev/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		
		$("#btnQuery").click(function() {
			$('#dataGrid').datagrid('load', {
				name : $('#examReviewName').val(),
				draftingUnit : $('#sDraftingUnit').combotree('getValue'),
				reviewUnit : $('#sReviewUnit').combotree('getValue'),
				status : $('#sStatus').combobox('getValue')
			});
		});

		$('#dataGrid').datagrid({
			url : '${pageContext.request.contextPath}/examDraftRev/searchDraftRevs.do',
			onDblClickRow: function (rowIndex, rowData) {
				var id = rowData.id;
				cleanPage();
				load(id, 'DRAFTREVIEW');
				$("#searchReview").window("close");
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
		
		$('#sReviewUnit').combotree({   
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
		//送审稿审查查询 end -- >
		
	});
	
	//清空页面
	function cleanPage() {
		$('#norId').val('');
		$("#examDraftReviewNameId").val('');
		
		$("#draftingUnitId").val('');
		$("#draftingUnitLeaderId").val('');
		$("#draftingUnitClerkId").val('');
		
		$("#unionDraftingUnitId").val('');
		$("#unionDraftingUnitLeaderId").val('');
		
		$("#reviewUnitId").val('');
		$("#reviewUnitLeaderId").val('');
		$("#reviewUnitClerkId").val('');
		
		var rdate = new Date();	//日期转换
		var dateStr = rdate.getFullYear() + '-';
		dateStr += rdate.getMonth() + 1;
		dateStr += '-' + rdate.getDate();
		$("#reviewDateId").datebox('setValue', dateStr);
		$("#viewDraft").html('');
		$("#viewInstruction").html('');
		$("#legalBasis").html('');
		
		if(!init){
			editor.setContent("");
		}
		
		$("input[name=isNeedModify]:checked").prop("checked", false);
	}
	
	function load(id, sreachType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/examDraftRev/load.do",
			data : {
				id : id,
				sreachType :  sreachType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$('#norId').val(data.vo.normativeFile.id);
					$("#examDraftReviewNameId").val(data.vo.normativeFile.name);
					
					$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
					$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
					$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
					
					$("#unionDraftingUnitId").val(data.vo.normativeFile.unionDrtUnitName);
					$("#unionDraftingUnitLeaderId").val(data.vo.normativeFile.unionDrtUnitLeaderName);
					
					$("#reviewUnitId").val(data.vo.normativeFile.revUnit.text);
					$("#reviewUnitLeaderId").val(data.vo.normativeFile.revUnitLeader.name);
					$("#reviewUnitClerkId").val(data.vo.normativeFile.revUnitClerk.name);
					
					$("#reviewDateId").datebox('setValue', data.vo.reviewDate);
					
					//生成起草说明的浏览
					var insStr = data.vo.normativeFile.draftInstruction;
					if (insStr != null && insStr.length > 0) {
						$('#viewInstruction').empty();
						$("#viewInstruction").append(
								"<a class='insclass' href='#' style='cursor: pointer;'><div>"
										+ insStr + "</div></a>");
						$(".insclass").bind('click', function() {
							view(insStr, 'INSTRUCTION');
						});
					}
					//生成送审稿的浏览
					var draftStr = data.vo.examinationDraft;
					if (draftStr != null && draftStr.length > 0) {
						$('#viewDraft').empty();
						$("#viewDraft").append(
								"<a class='draftclass' href='#' style='cursor: pointer;'><div>"
										+ draftStr + "</div></a>");
						$(".draftclass").bind('click', function() {
							view(draftStr, 'DRAFT');
						});
					}
					
					//生成相关依据浏览
					var lgStr = data.vo.normativeFile.legalBasis;
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
					var lgNoAttaStr = data.vo.normativeFile.legalBasisNoAtta;
					if (lgNoAttaStr != null && lgNoAttaStr.length > 0) {
						var arrStr = lgNoAttaStr.split(";");
						$.each(arrStr, function(index, tx) {
							var str = "<div>" + tx + "</div>";
							$("#legalBasis").append(str);
						});
					}
					var filename = data.vo.normativeFile.name;
					if(sreachType == 'NORFILE'){
						  //得到文件头
						  gainFileHead("",filename);
					}else{
						  if(init){
							  editor.addListener("ready", function() {
								  editor.setContent(data.vo.reviewComment);
							  });
						  }else{
							  editor.setContent(data.vo.reviewComment);
						  }
					}
					
					if(data.vo.isNeedModify != null){
						var needModifyValue = data.vo.isNeedModify ? 0 : 1;
						$("input[name=isNeedModify][value=" + needModifyValue + "]").prop("checked", true);
					}
					
		            setModifiable(data.modifiable);
				}
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
	}
	
	function view(fileName, fileType) {
		var norId = $('#norId').val();
		if (null != norId && "" != norId) {
			$.ajax({
					type : "POST",
					url : "${pageContext.request.contextPath}/examDraftSub/gainFileContent.do",
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
			showMsg("请选择规范性文件或送审稿审核！");
		}

	}
	
	//设置是否可以修改,true表示可以修改,false表示不可修改
	function setModifiable(modifiable){
		$('#reviewDateId').datebox('readonly', !modifiable);
		$("input[name=isNeedModify]").prop("disabled", !modifiable);
		
		showDiv("saveBtn", modifiable);
		showDiv("deleteBtn", modifiable);
		showDiv("submitBtn", modifiable);
		showDiv("approveBtn", modifiable);
		showDiv("unApproveBtn", modifiable);
		showDiv("flowBtn", modifiable);
		
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
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">"+data.districtName+data.organizationName +" </span></p>"+
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-family:宋体;font-weight:bold; font-variant:small-caps\">关于对《"+filename+"》的审核意见</span></p>";
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
	    <div>
	    	<table class="spe_table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div class="button_change red center" id="addBtn">新增</div>
	    				<div class="button_change red center" id="saveBtn">保存</div>
	    				<div class="button_change red center" id="deleteBtn">删除</div>
	    				<div class="button_change red center" id="submitBtn">提交</div>
	    				<div class="button_change red center" id="approveBtn">审核</div>
	    				<div class="button_change red center" id="unApproveBtn">弃审</div>
	    				<div class="button_change red center" id="flowBtn">流程</div>
	    				<div class="button_change red center" id="listBtn">列表</div>
	    				<div class="button_change red center" id="searchBtn">查找</div>
	    				<div class="button_change red center" id="exportBtn">导出</div>
	    				<div class="button_change red center" id="printBtn">打印</div>
	    			</td>
	    		</tr>
	    	</table>
	    </div>
    	<div id="dataDivWindow" class="dataDivWindow" >
    	<form id="downloadId" class="downloadId"  name="download" action="" method="post"></form>
	    	<table  class="spe_table  dataTable">
				<tbody>
					<tr>
						<td class="td-label">文件名称</td>
						<td colspan="5" class="td-fileName">
							<input id="norId" name="normativeFileId" type="hidden" /> 
							<input id="examDraftReviewNameId" type="text" class="fileName" readonly="readonly" />     
						</td>
						<td class="td-btn-find">
							<div class="button_change red center" onclick="openFrame('LEGAL_REVIEW_SUBMIT')">查询</div>
						</td>
					</tr>
					<tr>
						<td class="td-label">起草单位</td>
						<td class="td-data">
							<input id="draftingUnitId"  name="draftingUnit"  readonly="readonly" class="td-3data"/>
						</td>
						<td class="td-label">起草单位负责人</td>
						<td class="td-data">
							<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data"/>
						</td>
						<td class="td-label">起草单位经办员</td>
						<td class="td-data">
							<input id="draftingUnitClerkId" name="draftingUnitClerk" readonly="readonly" class="td-3data"/> 
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="td-label">联合起草单位</td>
						<td class="td-data">
							<input id="unionDraftingUnitId" name="unionDraftingUnit" readonly="readonly" class="td-3data"/>
						</td>
						<td class="td-label">联合起草负责人</td>
						<td class="td-data" colspan="3">
							<input id="unionDraftingUnitLeaderId" name="unionDraftingUnitLeader" readonly="readonly" class="td-3data"/>
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="td-label">审查单位</td>
						<td class="td-data">
							<input id="reviewUnitId" name="reviewUnit" readonly="readonly" class="td-3data"/>
						</td>
						<td class="td-label">审查单位负责人</td>
						<td class="td-data">
							<input id="reviewUnitLeaderId" name="reviewUnitLeader" readonly="readonly" class="td-3data"/>
						</td>
						<td class="td-label">审查单位经办员</td>
						<td class="td-data">
							<input id="reviewUnitClerkId" name="reviewUnitClerk" readonly="readonly" class="td-3data"/>
						</td>
						<td></td>
					</tr>
					<tr>
						<td class="td-label">审核日期</td>
						<td colspan="6" style="text-align: left;">
							<input id="reviewDateId" type="text" class="easyui-datebox"/>
						</td>
					</tr>
					<tr>
						<td class="td-label">送审稿</td>
						<td colspan="6" class="td-upload-file">
							<div id="viewDraft"></div>
						</td>
					</tr>
					<tr>
						<td class="td-label">起草说明</td>
						<td colspan="6" class="td-upload-file">
							<div id="viewInstruction"></div>
						</td>
					</tr>
					<tr>
						<td class="td-label">相关依据</td>
						<td colspan="6" class="td-upload-file">
							<div id="legalBasis"></div>
						</td>
					</tr>
					<tr class="editorTr">
						<td>审查意见</td>
						<td colspan="6" class="td-file-content">
							<center>
								<script id="editor" type="text/plain"  class="td-editor"></script>
							</center>
						</td>
					</tr>
					<tr>
						<td class="td-label">是否需要修改送审稿</td>
						<td colspan="6" class="td-upload-file">
							<input name="isNeedModify" type="radio" value="0"/>是
							<input name="isNeedModify" type="radio" value="1"/>否
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	    
	<!-- 浏览页面 -->
	<div id="view" title="文件浏览"  class="easyui-window" data-options="collapsible:false, 
		inline:false, minimizable:false, maximizable:false, closed:true, resizable:false" >
	   <div id="viewContent" class="view-file" ></div>
	</div>
	
	<!-- 弹出页面 -->
	<div id="searchReview" title="送审稿审查查询" class="easyui-window" 
			data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 774, height: 320, padding: 0">
		<table cellpadding="0" cellspacing="0" class="table">
				<tr>
					<td class="search-td">文件名称</td>
					<td class="search-td-input">
						<input id="examReviewName"/>
					</td>
					<td class="search-td">起草单位</td>
					<td class="search-td-input">
						<input id="sDraftingUnit" class="easyui-combotree" />
					</td>
				</tr>
				<tr>
					<td class="search-td">审核单位</td>
					<td class="search-td-input">
						<input id="sReviewUnit" class="easyui-combotree" />
					</td>
					<td class="search-td">状态</td>
					<td class="search-td-input">
						<input id="sStatus" type="text" class="easyui-combobox" />
					</td>
						
					<td class="search-td-button"><input
						id="btnQuery" type="button" class="search-td-find-button" value="查询" /></td>
				</tr>
			</table>

			<table id="dataGrid" data-options="fitColumns:true,singleSelect:true,
					pagination:true,rownumbers:true,width:760,height:200,border:0">
				<thead>
					<tr>
						<th data-options="field:'id',halign:'center',hidden:true">id</th>
						<th data-options="field:'name',halign:'center',width:240">文件名称</th>
						<th data-options="field:'draftingUnit',halign:'center',width:150,formatter:function(value,row){
                            return row.draftingUnit.text;
                        }">起草单位</th>
						<th data-options="field:'reviewUnit',halign:'center',width:150,formatter:function(value,row){
                            return row.reviewUnit.text;
                        }">审核单位</th>
						<th data-options="field:'unionDraftingUnitName',halign:'center',width:120">联合起草单位</th>
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
						
					</tr>
				</thead>
				<tbody></tbody>
			</table>

		<table class="search-button">
			<tr>
				<td width="680"></td>
				<td><input id="confirm" type="button" value="确认" /></td>
				<td><input id="cancel" type="button" value="取消" /></td>
			</tr>
		</table>
	</div>
	
</body>
</html>