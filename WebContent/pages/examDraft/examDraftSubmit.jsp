<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>送审稿报送</title>
<%@include file="/pages/share/base.jsp"%>
<%@include file="/pages/share/searchNormativeFile.jsp"%>
<link rel="stylesheet"  type="text/css" href="<%=contextPath %>/css/legaldoc/examDraftSubmit.css"/> 
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
			load(parseInt(listId, 10), 'DRAFTSUB');
		}
		
		//新增
		$('#addBtn').click(function(){
			cleanPage();
			setModifiable(true);
		});
		
		//保存
		$("#saveBtn").click(function() {
			var norId = $('#norId').val();
			if(norId == ""){
				showMsg("文件名称不能为空");
				return;
			}
			var name = $('#examDraftSubNameId').val();
			var reviewUnit = $("#reviewUnitId").combotree('getValue');
			var reviewUnitLeader = $("#reviewUnitLeaderId").combobox('getValue');
			var reviewUnitClerk = $("#reviewUnitClerkId").combobox('getValue');
			var draftingInstruction = editor.getContent();
			var legalBasises = genFileName("legalBasis");
			var legalBasisesNoAtta = genFileNameNoAtta("legalBasis");
			if (reviewUnit == '') {
				showMsg("请先配置审查单位!");
				return;
			}
			if (reviewUnitLeader == '') {
				showMsg("请选择审查单位负责人!");
				return;
			}
			if (reviewUnitClerk == '') {
				showMsg("请选择审查单位经办员!");
				return;
			}
			if(draftingInstruction == ""){
				showMsg("请填写起草说明!");
				return;
			}
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/examDraftSub/addExamDraftSub.do",
				  data : {norId : norId, name : name, reviewUnit : reviewUnit, reviewUnitLeader : reviewUnitLeader, 
					  		reviewUnitClerk : reviewUnitClerk, draftingInstruction : draftingInstruction,legalBasises:legalBasises,legalBasisesNoAtta:legalBasisesNoAtta},
				  dataType : 'json',
				  success : function(data){
					  if (data.msg == 'success') {
						  showMsg("保存成功!");
						  $('#id').val(data.id);
					  } else if (data.msg == 'fail'){
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
		
		$('#deleteBtn').click(function(){
			
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type: "POST",
					  url: "${pageContext.request.contextPath}/examDraft/delete.do",
					  data : {norId : norId},
					  dataType : 'json',
					  success : function(data)
					  {
						  if (data.msg == 'success') {
		                		showMsg("删除成功!");
		                		cleanPage();	//清除删除在页面上内容
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
		
		$("#listBtn").click(function() {
			location.href = "${pageContext.request.contextPath}/examDraft/examDraftSubmitList.wf";
		});
		
		//导出
		$('#exportBtn').click(function(){
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
			$("#downloadId").attr("action", "${pageContext.request.contextPath}/examDraft/downloadWord.do?norId=" + norId);
			$("#downloadId").submit();
		});
		
		//打开查询页面
		$('#searchBtn').click(function(){
			$('#dataGrid').datagrid({
				url : '${pageContext.request.contextPath}/examDraft/searchDraftSubs.do',
				pageNumber: 1,
				onDblClickRow: function (rowIndex, rowData) {
					var id = rowData.id;
					cleanPage();
					load(id, 'DRAFTSUB');
					$('#searchSubmit').window('close');
				}
			});
			$('#searchSubmit').window('open');
		});
		
		//打印
		$('#printBtn').click(function(){
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$.ajax({
					  type : "post",
					  url : "${pageContext.request.contextPath}/examDraftSub/gainFileContent.do",
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
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		//打开依据上传页面
		$('#openUpload').click(function(){
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$('#uploadLegalBasises').window('open');
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		//依据上传
		$('#ajaxFileUpload').click(function(){
			var norId = $('#norId').val();
			if (null != norId && "" != norId) {
				$('#uploadLegalBasises').window('close');
				var fileName = $('#legalfileId').val();
		        $.ajaxFileUpload
		        (
		            {
		                url :'${pageContext.request.contextPath}/examDraftSub/uploadLegal.do', 
		                data : {norId : norId , fileName : fileName},
		                secureuri :false,
		                fileElementId :'legalfileId',
		                dataType : 'json',
		                success : function (data)
		                { 
		                	if (data.msg == 'success') {
		                		var arrs = $("a[class^='attachment']");
		                		var size = 0;
		                		if (arrs.length > 0) {
		                			size = arrs.length;
		                		} 
		                		if (data.name.length > 0) {
		                			$("#legalBasis").append(
											"<div><a class='attachment" + size + "' href='#' style='cursor: pointer;'>"+ data.name + "</a>"
											+"<img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/></div>");
									$(".attachment" + size).bind('click', function() {
										view(data.name, 'LEGALBASIS');
									});
		                		}
		                		showMsg("上传成功!");
		                	} else {
		                		showMsg("上传失败!");
		                	}
		                },
		                error : function (data)
		                {
		                	showMsg("上传失败!");
		                }
		            }
		        );
			} else {
				showMsg("请选择规范性文件或送审稿！");
			}
		});
		
		
		//送审稿报送查询 begin-->
		
		$('#cancel').click(function(){
			$('#searchSubmit').window('close');
		});
		
		
		$('#confirm').click(function(){
			var selectRow =$("#dataGrid").datagrid("getSelected");
			var id=selectRow.id;
			cleanPage();
			load(id, 'DRAFTSUB');
			$('#searchSubmit').window('close');
		});
		
		$('#sStatus').combobox({
			url : '${pageContext.request.contextPath}/examDraftSub/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		
		$("#btnQuery").click(function() {
			$('#dataGrid').datagrid('load', {
				name : $('#examSubName').val(),
				draftingUnit : $('#sDraftingUnit').combotree('getValue'),
				reviewUnit : $('#sReviewUnit').combotree('getValue'),
				status : $('#sStatus').combobox('getValue')
			});
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
		
		//送审稿报送查询 end -- >
		
		
		
	});
	
	//清空页面
	function cleanPage() {
		$("#norId").val("");
		$("#examDraftSubNameId").val('');
		
		$("#draftingUnitId").val('');
		$("#draftingUnitLeaderId").val('');
		$("#draftingUnitClerkId").val('');
		
		$("#unionDraftingUnitId").val('');
		$("#unionDraftingUnitLeaderId").val('');
		
		$("#reviewUnitId").combotree('clear');
		$("#reviewUnitLeaderId").combobox('clear');
		$("#reviewUnitClerkId").combobox('clear');
		$('#editorcontents').html('');
		
		$('#legalBasis').html('');
		$('#viewDraft').html('');
		
		if(!init){
			editor.setContent("");
		}
	}
	
	//保存时获取上传文件名称，以“;”分隔
	function genFileName(fileId){
		var fileName = "";
		$("#" + fileId + " div").each(function(i){
			if($(this).has("a").length > 0){
				if(fileName == ""){
					fileName = $(this).text();
				}
				else{
					fileName += ";" + $(this).text();
				}
			}
		});
		return fileName;
	}
	
	//保存时获取上传文件名称，以“;”分隔
	function genFileNameNoAtta(fileId){
		var fileName = "";
		$("#" + fileId + " div").each(function(i){
			if($(this).has("a").length == 0){
				if(fileName == ""){
					fileName = $(this).text();
				}
				else{
					fileName += ";" + $(this).text();
				}
			}
		});
		return fileName;
	}
	
	//加载
	function load(id, sreachType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/examDraft/load.do",
			data : {
				norId : id,
				sreachType :  sreachType
			},
			dataType : 'json',
			success : function(data) {
				if (data != null) {
					$('#id').val(data.vo.id);
					$('#norId').val(data.vo.normativeFile.id);
					$("#examDraftSubNameId").val(data.vo.normativeFile.name);
					
					$("#draftingUnitId").val(data.vo.normativeFile.drtUnit.text);
					$("#draftingUnitLeaderId").val(data.vo.normativeFile.drtUnitLeader.name);
					$("#draftingUnitClerkId").val(data.vo.normativeFile.drtUnitClerk.name);
					
					$("#unionDraftingUnitId").val(data.vo.normativeFile.unionDrtUnitName);
					$("#unionDraftingUnitLeaderId").val(data.vo.normativeFile.unionDrtUnitLeaderName);
					
					$("#reviewUnitId").combotree('setValue', data.vo.normativeFile.revUnit.id);
					$("#reviewUnitLeaderId").combobox('setValue', data.vo.normativeFile.revUnitLeader.id);
					$("#reviewUnitClerkId").combobox('setValue', data.vo.normativeFile.revUnitClerk.id);
					
					//生成送审稿的浏览
					var draftStr = data.vo.examinationDraft;
					if (draftStr != null && draftStr.length > 0) {
						$('#viewDraft').empty();
						$("#viewDraft").append("<a class='draftclass' href='#' style='cursor: pointer;'>" + draftStr + "</a>");
						$(".draftclass").bind('click', function() {
							view(draftStr, 'DRAFT');
						});
					}
					
					//生成相关依据浏览
					var lgStr = data.vo.normativeFile.legalBasis;
					if (lgStr != null && lgStr.length > 0) {
						$('#legalBasis').empty();
						var arrStr = lgStr.split(";");
						$.each(arrStr, function(index, tx) {
							var str = "<div><a class='attachment" + index + "' href='#' style='cursor: pointer;'>" + tx + "</a>";
							if(data.modifiable){
								str += "<img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>";
							}
							str += "</div>";
							$("#legalBasis").append(str);
							$(".attachment" + index).bind('click', function() {
								view(tx, 'LEGALBASIS');
							});
						});
					}
					var lgNoAttaStr = data.vo.normativeFile.legalBasisNoAtta;
					if (lgNoAttaStr != null && lgNoAttaStr.length > 0) {
						var arrStr = lgNoAttaStr.split(";");
						$.each(arrStr, function(index, tx) {
							var str = "<div>" + tx;
							if(data.modifiable){
								str += "<img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>";
							}
							str += "</div>";
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
								  editor.setContent(data.vo.draftingInstruction);
							  });
						  }else{
							  editor.setContent(data.vo.draftingInstruction);
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
			showMsg("请选择规范性文件或送审稿报送！");
		}

	}
	
	//设置是否可以修改,true表示可以修改,false表示不可修改
	function setModifiable(modifiable){
		//$('#reviewUnitId').combotree('readonly', !modifiable);
		$('#reviewUnitLeaderId').combobox('readonly', !modifiable);
		$('#reviewUnitClerkId').combobox('readonly', !modifiable);
		$('#openUpload').prop("disabled", !modifiable);
		
		showDiv("saveBtn", modifiable);
		showDiv("deleteBtn", modifiable);
		showDiv("submitBtn", modifiable);
		showDiv("approveBtn", modifiable);
		showDiv("unApproveBtn", modifiable);
		showDiv("flowBtn", modifiable);
		showDiv("drfSubmitBtn", modifiable);
		
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
								   "<p style=\"text-align:center;line-height:36px\"><span style=\"font-size:36px;font-weight:bold; font-family:宋体;font-variant:small-caps\">关于《"+filename+"》的起草说明</span></p>";
				  editor.setContent(filehead+"<br/>"+filecontent);
			  },
			  error : function(data){
				  showMsg("加载出错,请重试!");
			  }
			});
	}
	function deleteFile(thisObj){
		$(thisObj).parent().remove();
	}
</script>

</head>
<body>
	    <div id="cc" class="easyui-layout cc" >
			<div data-options="region:'center',border:false,split:false,width: 1200, height: 680" >
	    	<table  class="table">
	    		<tr>
	    			<td id="btnTools" style="text-align: left;">
	    				<div class="button_change red center" id="addBtn">新增</div>
	    				<div class="button_change red center" id="saveBtn">保存</div>
	    				<div class="button_change red center" id="deleteBtn">删除</div>
	    				<div class="button_change red center" id="submitBtn">提交</div>
	    				<div class="button_change red center" id="approveBtn">审核</div>
	    				<div class="button_change red center" id="unApproveBtn">弃审</div>
	    				<div class="button_change red center" id="flowBtn">流程</div>
	    				<div class="button_change red center" id="drfSubmitBtn">报送</div>
						<div class="button_change red center" id="listBtn">列表</div>
	    				<div class="button_change red center" id="searchBtn">查找</div>
	    				<div class="button_change red center" id="exportBtn">导出</div>
	    				<div class="button_change red center" id="printBtn">打印</div>
	    			</td>
	    		</tr>
	    	</table>
	    	<div id="dataDivWindow" class="dataDivWindow" >
			    	<table  class="spe_table dataTable">
						<tbody>
							<tr>
								<td class="td-label">文件名称</td>
								<td colspan="5" class="td-fileName">
									<input id="id" name="id" type="hidden" />
									<input id="norId" name="normativeFileId" type="hidden" /> 
									<input id="examDraftSubNameId" name="examDraftSubName" type="text" class="fileName" readonly="readonly"/>  
								</td>
								<td class="td-btn-find">	  
									<div class="button_change red center" onclick="openFrame('REQUEST_COMMENT_MODIFY')">查询</div>
								</td>
							</tr>
							<tr>
								<td class="td-label">起草单位</td>
								<td class="td-data">
									<input id="draftingUnitId"  name="draftingUnit" readonly="readonly" class="td-3data" />
								</td>
								<td class="td-label">起草单位负责人</td>
								<td class="td-data">
									<input id="draftingUnitLeaderId" name="draftingUnitLeader" readonly="readonly" class="td-3data" />
								</td>
								<td class="td-label">起草单位经办员</td>
								<td class="td-data">
									<input id="draftingUnitClerkId" name="draftingUnitClerk" readonly="readonly" class="td-3data" /> 
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">联合起草单位</td>
								<td class="td-data">
									<input id="unionDraftingUnitId" name="unionDraftingUnit"  readonly="readonly" class="td-3data" />
								</td>
								<td class="td-label">联合起草负责人</td>
								<td class="td-data" colspan="4">
									<input id="unionDraftingUnitLeaderId" name="unionDraftingUnitLeader" readonly="readonly" class="td-3data" />
								</td>
							</tr>
							<tr>
								<td class="td-label">审查单位</td>
								<td class="td-data">
									<input class="easyui-combotree" id="reviewUnitId" name="reviewUnit" 
									data-options="valueField:'id',textField:'name',readonly:true,
									url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
									onSelect: function(rec){
							            var url = '${pageContext.request.contextPath}/user/getUserByOrg.do?orgId=' + rec.id;    
							            $('#reviewUnitLeaderId').combobox('clear');
							            $('#reviewUnitLeaderId').combobox('reload', url);
							            $('#reviewUnitClerkId').combobox('clear');
							            $('#reviewUnitClerkId').combobox('reload', url); 
							        },
							        onBeforeSelect : function(node){
							        	if (node.attributes.nodeType == 'district') {
							        		showMsg('只能选择部门!');
							        		return false; 
							        	}
						            }" value=""
							        />
								</td>
								<td class="td-label">审查单位负责人</td>
								<td class="td-data">
									<input id="reviewUnitLeaderId" name="reviewUnitLeader" class="easyui-combobox"
									data-options="valueField:'id',textField:'name'"  value=""/>
								</td>
								<td class="td-label">审查单位经办员</td>
								<td class="td-data">
									<input id="reviewUnitClerkId" name="reviewUnitClerk" class="easyui-combobox"
									data-options="valueField:'id',textField:'name'" value=""/> 
								</td>
								<td></td>
							</tr>
							<tr>
								<td class="td-label">送审稿</td>
								<td colspan="6" class="td-upload-file">
									<div id="viewDraft"></div>
								</td>
							</tr>
							<tr>
								<td class="td-label">相关依据</td>
								<td colspan="5" class="td-upload-file">
									<div id="legalBasis"></div>
								</td>
								<td class="td-data"><input type="button" id="openUpload" value="上传" /></td>
							</tr>
							<tr class="editorTr">
								<td>起草说明</td>
								<td colspan="6" class="td-file-content">
									<center>
										<script id="editor" type="text/plain" class="td-editor"></script>
									</center>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
				<form id="downloadId" name="download" class="download" action="" method="post"  />
			</div>
		</div>	
	
	<!-- 弹出页面 -->
	<div id="uploadLegalBasises" title="法律依据上传"  class="easyui-window"  data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, width:450, height:80, padding:0" >
	   <form id="form1" name="form1" method="post" action="" enctype="multipart/form-data">
		 <table class="uploadTable"> 
		  <tr>
		   <td>上传文件：</td>
		   <td><input name="file" id="legalfileId" type="file" size="20" /></td>
		   <td><input type="button" name="submit" id="ajaxFileUpload" value="上传" /></td>
		  </tr>    
		 </table>
		</form>
	</div>
	
	
	<!-- 浏览页面 -->
	<div id="view" title="文件浏览"  class="easyui-window" data-options="collapsible:false, inline:false, minimizable:false, maximizable:false, closed:true,resizable:false" >
	   <div id="viewContent" class="view-file" ></div>
	</div>
	
	<!-- 弹出页面 -->
	<div id="searchSubmit" title="送审稿报送查询" class="easyui-window" 
			data-options="collapsible:false, minimizable:false, maximizable:false, closed:true, resizable:false, width: 774, height: 320, padding: 0">
		<table cellpadding="0" cellspacing="0" class="table">
				<tr>
					<td class="search-td">文件名称</td>
					<td class="search-td-input">
						<input id="examSubName" />
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
						
					<td class="search-td-button">
						<input id="btnQuery" type="button"  class="search-td-find-button"  value="查询" />
					</td>
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
						<th data-options="field:'unionDraftingUnitName',halign:'center',width:120,formatter:function(value,row){return row.normativeFile.unionDrtUnitName;}">联合起草单位</th>
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