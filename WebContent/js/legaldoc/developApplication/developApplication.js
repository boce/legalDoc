	
	var legalBasisTypeData = {"LAW_AND_REG":"法律法规","STANDARD":"规范标准","RELATED_DOC":"相关文件","REFERENCE":"借鉴"};
	//记录当前要上传附件的div id
	var currentAttaId = "";
	$(function() {
		//显示的页面按钮
		displayBtn();
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		if (listId != "") {
			load(parseInt(listId, 10), 'DEVELOPAPPLICATION');
		}
		$("#planDraftDateId").datebox({"editable":false});
		$("#planReviewDateId").datebox({"editable":false});
		$("#applyDateId").datebox({"editable":false});
		$("#nameId").bind("blur", function(){
			var name = $(this).val();
			if(name.indexOf("暂行") >= 0 || name.indexOf("试行") >= 0){
				$('#validDateId').combobox('setValue', 2);
			}else{
				$('#validDateId').combobox('setValue', 5);
			}
		});
		//初始化区域和组织机构下拉列表
		$("#applyOrgId").combotree({
			url : contextPath+"/org/getOrgShortReference.do",
			required : true,
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "地区不能选择！");
					return false;
				}
			},
			onChange : function(newValue, oldValue) {
				$('#approvalLeaderId').combobox({
					url: contextPath+"/user/getUserByOrg.do",
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
				$('#applyLeaderId').combobox({
					url: contextPath+"/user/getUserByOrg.do",
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
				$('#applyClerkId').combobox({
					url: contextPath+'/user/getUserByOrg.do',
					valueField : 'id',
					textField : 'name',
					required : true,
					onBeforeLoad: function(param){
						param.orgId = newValue;
					}
				});
			}
		});
		if(listId == ""){
			init();
		}
		//初始化有效期
		$('#validDateId').combobox({
			valueField: 'value',
			textField: 'label',
			readonly : true,
			data: [{
				label: '2年',
				value: '2'
			},{
				label: '5年',
				value: '5'
			}]
		});
		//初始化优先级
		$('#priorityId').combobox({
			url: contextPath+'/developApplication/getPriority.do',
			valueField : 'name',
			textField : 'title',
			required : true
		});

		$('#legalBasisTable').datagrid({    
		    singleSelect:"true",
		    rownumbers:"true",
		    onClickRow:onClickRow,
		    columns:[[
		        {field:'id',hidden:true},
		        {field:'name',title:'名称',width:200,align:"left",editor:"text",formatter:function(value, row){
		        	return "<span title=\""+value+"\">" + value + "</span>";
		        }},
		        {field:'basisInvalidDate',title:'失效日期',width:100,align:"left",editor:{type:"datebox"}},
		        {field:'legalBasisType',title:'类型',width:100,align:"left",
			        formatter:function(value,row){
	                    return legalBasisTypeData[value];
	                },editor:{
			        	type:'combobox',
						options:{
							"editable":false,
							valueField:'name',
							textField:'title',
							url: contextPath+'/developApplication/getLegalBasisType.do',
							required:true
						}
	                }
		        },
		        {field:'legalBasisAtta',title:'附件',width:300,align:"left",formatter:function(value, row){
			        	var atta = "<a title=\""+value+"\" class=\"attachment\" onclick=\"view(\'"+value+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + value + "</a>";
			        	return atta;
			        },editor:{
			        	type:'text'
					}
		        }
		    ]],
		    toolbar : [{
				text:'添加',
				iconCls:'icon-add',
				handler:append
			},{
				text:'删除',
				iconCls:'icon-remove',
				handler:removeit
			},{
				text:'添加附件',
				iconCls:'icon-save',
				handler:function(){
					if(editIndex == undefined){
						showMsg("请先选择一行记录");
						return;
					}
					currentUploadFileName = "";
					$("#legalBasisUploadDiv").dialog("open");
				}
			}]
		});
		
		$("#btnAdd").click(function() {
			clear();
			init();
			setModifiable(true);
		});

		$("#involvedOrges").combotree({
			url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
			multiple:true,
			cascadeCheck:false,
			width:700,
			onBeforeSelect : function(node) {
				if (node.attributes.nodeType == orgNodeType[1]) {
					$.messager.alert("提示", "地区不能选择！");
					return false;
				}
			}
		});
		$("#btnSave").click(function() {
			var norId = $("#norId").val();
			var id = $("#id").val();
			var name = $("#nameId").val();
			var applyOrg = $('#applyOrgId').combotree('getValue');
			var approvalLeader = $('#approvalLeaderId').combobox('getValue');
			var applyLeader = $('#applyLeaderId').combobox('getValue');
			var planDraftDate = $('#planDraftDateId').datebox('getValue');
			var planReviewDate = $('#planReviewDateId').datebox('getValue');
			var applyDate = $('#applyDateId').datebox('getValue');
			var validDate = $('#validDateId').combobox('getValue');
			var priority = $('#priorityId').combobox('getValue');
			var applyClerk = $('#applyClerkId').combobox('getValue');
			var legalBasis = "";
			var legalBasisAttachment = "";
			var rows = $('#legalBasisTable').datagrid("getRows");
			if(rows != null && rows.length > 0){
				legalBasis = JSON.stringify(rows);
				var len = rows.length;
				for(var i = 0; i < len; i++)
		        {
					if(rows[i].name == null || rows[i].name==""){
						continue;
					}
					if(rows[i].legalBasisAtta != null && rows[i].legalBasisAtta != ""){
						if(legalBasisAttachment == ""){
							legalBasisAttachment += rows[i].legalBasisAtta;
						}else{
							legalBasisAttachment += ";" + rows[i].legalBasisAtta;
						}
					}
		        }
			}
			var necessityLegalAndRisk = $("#necLegalText").val();
			var necessityLegalAndRiskAttachment = genFileName("necLegalAttaDivId");
			var mainProblem = $("#mainProblemText").val();
			var mainProblemAttachment = genFileName("mainProblemAttaDivId");
			var planRegAndMea = $("#planRegAndMeaText").val();
			var planRegAndMeaAtta = genFileName("planRegAndMeaAttaDivId");
			var involvedOrges = $('#involvedOrges').combobox('getValues');
			var involvedOrgesStr = involvedOrges.join("\",\"");
			if(involvedOrgesStr != ""){
				involvedOrgesStr = "\"" + involvedOrgesStr + "\"";
			}
			var applyLeaderComment = $('#appLeaderComm').val();
			var approvalLeaderComment = $('#approvalLeaderComm').val();
			var remarks = $('#remarks').val();
			if(name == ""){
				$("#nameId").focus();
				showMsg("文件名称不能为空");
				return;
			}
			if(applyOrg == ""){
				$("#applyOrgId").focus();
				showMsg("申报单位不能为空");
				return;
			}
			if(approvalLeader == ""){
				$("#approvalLeaderId").focus();
				showMsg("批准申请领导不能为空");
				return;
			}
			if(applyLeader == ""){
				$("#applyLeaderId").focus();
				showMsg("申报单位负责人不能为空");
				return;
			}
			if(planDraftDate == ""){
				$("#planDraftDateId").focus();
				showMsg("拟起草日期不能为空");
				return;
			}
			if(planReviewDate == ""){
				$("#planReviewDateId").focus();
				showMsg("拟送审日期不能为空");
				return;
			}
			if(applyDate == ""){
				$("#applyDateId").focus();
				showMsg("申报日期不能为空");
				return;
			}
			if(validDate == ""){
				$("#validDateId").focus();
				showMsg("有效期不能为空");
				return;
			}
			if(priority == ""){
				$("#priorityId").focus();
				showMsg("优先级不能为空");
				return;
			}
			if(applyClerk == ""){
				$("#applyClerkId").focus();
				showMsg("申报经办员不能为空");
				return;
			}
			if(legalBasis == "" && legalBasisAttachment == ""){
				showMsg("制定依据不能为空");
				return;
			}
			if(necessityLegalAndRisk == "" && necessityLegalAndRiskAttachment == ""){
				showMsg("制定的必要性、合法性，以及社会稳定性风险评估不能为空");
				return;
			}
			if(mainProblem == "" && mainProblemAttachment == ""){
				showMsg("拟解决的主要问题不能为空");
				return;
			}
			if(planRegAndMea == "" && planRegAndMeaAtta == ""){
				showMsg("拟确定的制度或措施，以及可行性论证不能为空");
				return;
			}
			if(involvedOrges == ""){
				$("#involvedOrges").focus();
				showMsg("涉及的部门不能为空");
				return;
			}
			if(applyLeaderComment == ""){
				$("#appLeaderComm").focus();
				showMsg("申报单位负责人意见不能为空");
				return;
			}
			if(approvalLeaderComment == ""){
				$("#approvalLeaderComm").focus();
				showMsg("批准申请领导意见不能为空");
				return;
			}
			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/developApplication/save.do",
				data : {
					"id" : id,
					"name" : name,
					"normativeFile.id" : norId,
					"applyOrg.id" : applyOrg,
					"approvalLeader.id" : approvalLeader,
					"applyLeader.id" : applyLeader,
					"planDraftDate":planDraftDate,
					"planReviewDate":planReviewDate,
					"applyDate":applyDate,
					"validDate":validDate,
					"priority":priority,
					"applyClerk.id":applyClerk,
					"legalBasis":legalBasis,
					"legalBasisAttachment":legalBasisAttachment,
					"necessityLegalAndRisk":necessityLegalAndRisk,
					"necessityLegalAndRiskAttachment":necessityLegalAndRiskAttachment,
					"mainProblem":mainProblem,
					"mainProblemAttachment":mainProblemAttachment,
					"planRegulationMeasureAndFeasibility":planRegAndMea,
					"planRegulationMeasureAndFeasibilityAtta":planRegAndMeaAtta,
					"involvedOrges":involvedOrgesStr,
					"applyLeaderComment":applyLeaderComment,
					"approvalLeaderComment":approvalLeaderComment,
					"remarks":remarks,
					"tempFileId" : tempFileId
				},
				success : function(data) {
					if(data){
						showMsg(data.msg);
						if(data.success){
							$("#id").val(data.vo.id);
							$("#norId").val(data.vo.normativeFile.id);
							var legalBasises = data.vo.legalBasises;
							removeLegalBasisTable();
							if(legalBasises != null && legalBasises.length > 0){
								for(var i = 0; i < legalBasises.length; i++)
						        {
									legalBasises[i].basisInvalidDate = formatDate(legalBasises[i].basisInvalidDate);
									$('#legalBasisTable').datagrid('appendRow', legalBasises[i]);
						        }
							}
							accept();
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
			if(id == null || id == ""){
				showMsg("请先选择一个立项单");
				return;
			}
			$.messager.confirm('确认','确认想要删除吗？',function(r){    
			    if (r){    
					$.ajax({
						type : "POST",
						url : "${pageContext.request.contextPath}/developApplication/delete.do",
						data : {
							id : id
						},
						dataType:"json",
						success : function(data) {
							if(data){
								showMsg(data.msg);
								if(data.success){
									clear();
								}
							}
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							showMsg("删除失败!");
						}
					});
			    }    
			}); 

		});

		$("#btnList").click(function() {
			location.href = "${pageContext.request.contextPath}/developApplication/developApplicationList.wf";
		});

		//立项查找开始
		$("#btnFind").click(function() {
			$('#searchDataGrid').datagrid({
				url :  contextPath+'/developApplication/find.do',
			});
			$('#searchDevelopApplicationDraft').window('open');

		});

		//关闭立项查找窗口
		$("#btnSearchCancel").click(function() {
			$('#searchDevelopApplicationDraft').window('close');
		});

		//查询立项
		$("#btnSearch").click(function() {
			var fileName = $('#searchNameId').val();
			$('#searchDataGrid').datagrid({
				url :  contextPath+'/developApplication/find.do?name='+ fileName
			});
			$('#searchDataGrid').datagrid('reload'); //加载数据,实现文件的过滤
		});

		//点击确认
		$("#btnSearchSubmit").click(function() {
			var selectRow = $('#searchDataGrid').datagrid('getSelected');
			if(selectRow == null){
				return;
			}
			var id = selectRow.id;
			load(id, 'DEVELOPAPPLICATION');
			$('#searchDevelopApplicationDraft').window('close'); //关闭查询窗口
		});

		$("#btnPrint").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$.ajax({
					type : "post",
					url : "${pageContext.request.contextPath}/developApplication/print.do",
					data : {
						id : id
					},
					dataType : "html",
					success : function(data) {
						LODOP = getLodop();
						LODOP.ADD_PRINT_HTM("25.4mm",
								"19.1mm",
								"RightMargin:19.1mm",
								"BottomMargin:25.4mm",
								data);
						LODOP.PREVIEW();
					},
					error : function(data) {
						showMsg("打印失败!");
					}
				});
			} else {
				showMsg("请选择立项单！");
			}

		});

		$("#btnExport").click(function() {
			var id = $('#id').val();
			if (null != id && "" != id) {
				$("#downloadId").attr(
						"action",
						"${pageContext.request.contextPath}/developApplication/export.do?id="
								+ id);
				$("#downloadId").submit();
			} else {
				showMsg("请选择立项单！");
			}
		});
		//附件上传
		$("#btnAddAtta").click(function(){
			$('#attaUploadDiv').dialog('open');
			$("#attaFileName").val("");
		});
		$("#attaFileUploadId").click(function(){
			attaFileUpload();
		});
		$("#attaBtn").click(function(){
			var addFile = $("#attaFileName").val();
			if(addFile != "" && !isFileExist(currentAttaId, addFile))
			{
				$("#"+currentAttaId).append("<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+addFile+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + addFile 
				+ "</a><img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>" + "</div>");
			}
			$('#attaUploadDiv').dialog('close');
		});
		$("#attaCloseBtn").click(function(){
			$('#attaUploadDiv').dialog('close');
		});
		//打开制定依据详细信息窗口
		$("#btnAddLegalBasis").click(function(){
			$('#legalBasisDetailDiv').dialog('open');
		});
		//制定依据详细信息窗口-确定
		$("#legalBasisBtn").click(function(){
			endEditing();
			accept();
			var rows = $('#legalBasisTable').datagrid('getRows');
			$("#legalBasisDivId").html(genLegalBasisAttaHtml(rows));
			$('#legalBasisDetailDiv').dialog('close');
		});
		//制定依据详细信息窗口-取消
		$("#legalBasisCloseBtn").click(function(){
			reject();
			$('#legalBasisDetailDiv').dialog('close');
		});
		
		//制定依据文件上传
		$("#legalFileUploadId").click(function(){
			legalFileUpload();
		});
		
		//制定依据文件上传-确定
		$("#legalBasisUploadBtn").click(function(){
			var ed = $('#legalBasisTable').datagrid('getEditor', {index:editIndex,field:'legalBasisAtta'});
			ed.target.val(currentUploadFileName);
			$('#legalBasisUploadDiv').dialog('close');
		});
		//制定依据文件上传-取消
		$("#legalBasisUploadCloseBtn").click(function(){
			$('#legalBasisUploadDiv').dialog('close');
		});
		
		//制定的必要性、合法性，以及社会稳定性风险评估上传
		$("#necLegalUploadInput").click(function(){
			$('#necLegalUploadDiv').dialog('open');
		});
		$("#necLegalFileUploadId").click(function(){
			necLegalFileUpload();
		});
		$("#necLegalBtn").click(function(){
			var addFile = $("#necLegalFileName").val();
			if(addFile != "" && !isFileExist("necLegalAttaDivId", addFile))
			{
				$("#necLegalAttaDivId").append("<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+addFile+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + addFile 
				+ "</a><img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>" + "</div>");
			}
			$('#necLegalUploadDiv').dialog('close');
		});
		$("#necLegalCloseBtn").click(function(){
			$('#necLegalUploadDiv').dialog('close');
		});
		$("#necLegalText").click(function(){
			currentAttaId = "necLegalAttaDivId";
		});
		
		//拟解决的主要问题
		$("#mainProblemUploadInput").click(function(){
			$('#mainProblemUploadDiv').dialog('open');
		});
		$("#mainProblemFileUploadId").click(function(){
			mainProblemFileUpload();
		});
		$("#mainProblemBtn").click(function(){
			var addFile = $("#mainProblemFileName").val();
			if(addFile != "" && !isFileExist("mainProblemAttaDivId", addFile))
			{
				$("#mainProblemAttaDivId").append("<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+addFile+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + addFile
						+ "</a><img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>" + "</div>");
			}
			$('#mainProblemUploadDiv').dialog('close');
		});
		$("#mainProblemCloseBtn").click(function(){
			$('#mainProblemUploadDiv').dialog('close');
		});
		$("#mainProblemText").click(function(){
			currentAttaId = "mainProblemAttaDivId";
		});
		//拟确定的制度或措施，以及可行性论证
		$("#planRegAndMeaUploadInput").click(function(){
			$('#planRegAndMeaUploadDiv').dialog('open');
		});
		$("#planRegAndMeaFileUploadId").click(function(){
			planRegAndMeaFileUpload();
		});
		$("#planRegAndMeaBtn").click(function(){
			var addFile = $("#planRegAndMeaFileName").val();
			if(addFile != "" && !isFileExist("planRegAndMeaAttaDivId", addFile))
			{
				$("#planRegAndMeaAttaDivId").append("<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+addFile+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + addFile 
				+ "</a><img onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\"${pageContext.request.contextPath}\\images\\icons\\fileDelete.jpg\"/>" + "</div>");
			}
			$('#planRegAndMeaUploadDiv').dialog('close');
		});
		$("#planRegAndMeaCloseBtn").click(function(){
			$('#planRegAndMeaUploadDiv').dialog('close');
		});
		$("#planRegAndMeaText").click(function(){
			currentAttaId = "planRegAndMeaAttaDivId";
		});
	});

	//格式化日期字符串
	function formatDate(time){
	    var datetime = new Date(time);
	    var year = datetime.getFullYear();
	    var month = (datetime.getMonth() + 1) < 10 ? "0" + (datetime.getMonth() + 1) : (datetime.getMonth() + 1);
	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	    return year + "-" + month + "-" + date;
	}
	//初始化申报单位、负责人、经办员、日期
	function init(){
		var id = $('#id').val();
		if (null == id || "" == id) {
			$.ajax({
				type : "post",
				url : "${pageContext.request.contextPath}/developApplication/init.do",
				dataType : 'json',
				success : function(data) {
					$('#applyOrgId').combotree('setValue',data.applyOrgId);
					if(data.applyLeaderId){
						$('#applyLeaderId').combobox('setValue', data.applyLeaderId);
					}
					$('#applyClerkId').combobox('setValue', data.applyClerkId);
					$('#applyDateId').datebox('setValue', data.applyDate);
				},
				error : function() {
					showMsg("加载出错,请重试!");
				}
			});
		}
	}
	
	//保存时获取上传文件名称，以“;”分隔
	function genFileName(fileId){
		var fileName = "";
		$("#" + fileId + " div").each(function(i){
			if(fileName == ""){
				fileName = $(this).text();
			}
			else{
				fileName += (";" + $(this).text());
			}
		});
		return fileName;
	}
	
	function clear() {
		$("#detailForm").form("clear");
		$("#legalBasisDivId").html("");
		$("#necLegalDivId").html("");
		$("#necLegalAttaDivId").html("");
		$("#mainProblemDivId").html("");
		$("#mainProblemAttaDivId").html("");
		$("#planRegAndMeaDivId").html("");
		$("#planRegAndMeaAttaDivId").html("");
		removeLegalBasisTable();
	}

	function removeLegalBasisTable(){
		var rows = $('#legalBasisTable').datagrid("getRows");
		if(rows != null && rows.length > 0){
			var len = rows.length;
			for(var i = 0; i < len; i++)
	        {
				var index = $('#legalBasisTable').datagrid('getRowIndex',rows[i]);
				$('#legalBasisTable').datagrid('deleteRow', index);
	        }
		}
		accept();
	}
	
	function view(fileName) {
		var id = $('#id').val();
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/developApplication/viewFile.do",
			data : {
				id : id,
				fileName : fileName,
				tempFileId: tempFileId
			},
			dataType : 'html',
			success : function(data) {
				$('#viewContent').html(data);
				$('#viewFile').window('open'); //打开附件浏览
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});

	}
	
	function load(id, searchType) {
		$.ajax({
			type : "POST",
			url : "${pageContext.request.contextPath}/developApplication/load.do",
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
					$('#applyOrgId').combotree({
						url : "${pageContext.request.contextPath}/org/getOrgShortReference.do",
						onLoadSuccess:function()
						{
							$('#applyOrgId').combotree('setValue',data.vo.applyOrg.id);
							$('#approvalLeaderId').combobox('setValue', data.vo.approvalLeader.id);
							$('#applyLeaderId').combobox('setValue', data.vo.applyLeader.id);
							$('#applyClerkId').combobox('setValue', data.vo.applyClerk.id);
						}
					});
					$('#planDraftDateId').datebox('setValue', data.vo.planDraftDate);
					$('#planReviewDateId').datebox('setValue', data.vo.planReviewDate);
					$('#applyDateId').datebox('setValue', data.vo.applyDate);
					$('#validDateId').combobox('setValue', data.vo.validDate);
					$('#priorityId').combobox('setValue', data.vo.priority);
					var legalBasises = data.vo.legalBasises;
					$("#legalBasisDivId").html(genLegalBasisAttaHtml(legalBasises));
					removeLegalBasisTable();
					if(legalBasises != null && legalBasises.length > 0){
						for(var i = 0, len = legalBasises.length; i < len; i++)
				        {
							$('#legalBasisTable').datagrid('appendRow', legalBasises[i]);
				        }
					}
					accept();
					
					$("#necLegalText").val(data.vo.necessityLegalAndRisk);
					var necLegalRiskAtta = genAttaHtml(data.vo.necessityLegalAndRiskAttachment, data.modifiable);
					$("#necLegalAttaDivId").html(necLegalRiskAtta);
					
					$("#mainProblemText").val(data.vo.mainProblem);
					var mainProblemAtta = genAttaHtml(data.vo.mainProblemAttachment, data.modifiable);
					$("#mainProblemAttaDivId").html(mainProblemAtta);
					
					$("#planRegAndMeaText").val(data.vo.planRegulationMeasureAndFeasibility);
					var planRegAndMeaAtta = genAttaHtml(data.vo.planRegulationMeasureAndFeasibilityAtta, data.modifiable);
					$("#planRegAndMeaAttaDivId").html(planRegAndMeaAtta);
					
					var involvedOrges = data.vo.involvedOrges;
					if(involvedOrges != null)
					{
						involvedOrges = involvedOrges.replace(/\"/g, "");
						$('#involvedOrges').combotree('setValues',involvedOrges.split(","));
					}
					$('#appLeaderComm').val(data.vo.applyLeaderComment);
					$('#approvalLeaderComm').val(data.vo.approvalLeaderComment);
					$('#remarks').val(data.vo.remarks);
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
		$('#nameId').prop("readonly", !modifiable);
		$('#applyOrgId').combotree('readonly', !modifiable);
		$('#approvalLeaderId').combobox({
			onLoadSuccess:function(){
				$('#approvalLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#applyLeaderId').combobox({
			onLoadSuccess:function(){
				$('#applyLeaderId').combobox('readonly', !modifiable);
			}
		});
		$('#planDraftDateId').datebox('readonly', !modifiable);
		$('#planReviewDateId').datebox('readonly', !modifiable);
		$('#applyDateId').datebox('readonly', !modifiable);
		$('#priorityId').combobox('readonly', !modifiable);
		$('#applyClerkId').combobox({
			onLoadSuccess:function(){
				$('#applyClerkId').combobox('readonly', !modifiable);
			}
		});
		$('#necLegalText').prop("readonly", !modifiable);
		$('#mainProblemText').prop("readonly", !modifiable);
		$('#planRegAndMeaText').prop("readonly", !modifiable);
		$('#involvedOrges').combobox('readonly', !modifiable);
		$('#appLeaderComm').prop("readonly", !modifiable);
		$('#approvalLeaderComm').prop("readonly", !modifiable);
		$('#remarks').prop("readonly", !modifiable);
		showDiv("btnSave", modifiable);
		showDiv("btnDelete", modifiable);
		showDiv("btnSubmit", modifiable);
		showDiv("btnApprove", modifiable);
		showDiv("btnUnApprove", modifiable);
		showDiv("btnAddLegalBasis", modifiable);
		showDiv("btnAddAtta", modifiable);
	}
	//获得制定依据显示div，legalBasises为json数组
	function genLegalBasisAttaHtml(legalBasises){
		var legalBasisStr = "";
		if(legalBasises != null && legalBasises.length > 0){
			for(var i=0,len=legalBasises.length; i<len; i++){
				var legalBasis = legalBasises[i];
				if(legalBasis.name == null || legalBasis.name == ""){
					continue;
				}
				if(legalBasis.legalBasisAtta != null && legalBasis.legalBasisAtta != ""){
					legalBasisStr += "<div><a class=\"attachment\" onclick=\"view(\'"+legalBasis.legalBasisAtta+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + legalBasis.name + "</a></div>";
				}else{
					legalBasisStr += "<div>" + legalBasis.name + "</div>";
				}
			}
		}
		return legalBasisStr;
	}
	//初始化时根据后台的附件文件名称生成页面显示的div
	function genAttaHtml(atta, modifiable)
	{
		var attaHtml = ""; 
		if(atta && atta != "")
		{
			var legalBasisAttas = atta.split(";");
			for(var i=0, len=legalBasisAttas.length; i<len; i++)
			{
				attaHtml += "<div style=\"line-height:20px;\"><a class=\"attachment\" onclick=\"view(\'"+legalBasisAttas[i]+"\');\" href=\"#1\" style=\"cursor: pointer;\">" + legalBasisAttas[i] + "</a>";
				if(modifiable){
					attaHtml += "<img class=\"imgDelete\" onclick=\"deleteFile(this)\" style=\"cursor:pointer;\" src=\""+ contextPath+"\\images\\icons\\fileDelete.jpg\"/>";
				}
				attaHtml += "</div>";
			}
		}
		return attaHtml;
	}
	//临时文件目录唯一标识
	var tempFileId = "";
	var currentUploadFileName = "";
	//附件上传
	function attaFileUpload(){
		var fileName = $('#attaFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				$("#attaFileName").val(data.fileName);
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "attaFile", success);
	}
	//制定依据上传
	function legalFileUpload(){
		var fileName = $('#legalBaseFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				currentUploadFileName = data.fileName;
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "legalBaseFile", success);
	}
	//制定的必要性、合法性，以及社会稳定性风险评估上传
	function necLegalFileUpload()
	{
		var fileName = $('#necLegalFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				$("#necLegalFileName").val(data.fileName);
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "necLegalFile", success);
	}
	//拟解决的主要问题上传
	function mainProblemFileUpload()
	{
		var fileName = $('#mainProblemFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				$("#mainProblemFileName").val(data.fileName);
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "mainProblemFile", success);
	}
	//拟确定的制度或措施，以及可行性论证上传
	function planRegAndMeaFileUpload()
	{
		var fileName = $('#planRegAndMeaFile').val();
		if(fileName == null || fileName == "")
		{
			showMsg("请选择一个文件");
			return;
		}
		var success = function(data){
			if(data.success)
			{
				$("#planRegAndMeaFileName").val(data.fileName);
				if(data.tempFileId)
				{
					tempFileId = data.tempFileId;
				}
				showMsg("上传成功！");
			}
		};
		ajaxFileUpload(fileName, "planRegAndMeaFile", success);
	}
	function isFileExist(divId, fileName)
	{
		var flag = false;
		$("#" + divId + " div").each(function(i){
			if($(this).text() == fileName){
				flag = true;
				return false;
			}
		});
		return flag;
	}
	
	function deleteFile(img)
	{
		$(img).parent().remove();
	}
	//文件上传
	function ajaxFileUpload(fileName, fileId, success) {
		var id = $('#id').val();
		var param = {id : id , fileName : fileName};
		if(tempFileId != "")
		{
			param.tempFileId = tempFileId;
		}
        $.ajaxFileUpload({
             url : contextPath+'/developApplication/uploadFile.do',
             data : param,
             secureuri :false,
             fileElementId :fileId,
             dataType : 'json',
             success : function (data)
             { 
             	if(success)
             	{
             		success(data);
             	}
             },
             error : function (data)
             {
             	showMsg("上传失败!");
             }
		});
	}
	
	var editIndex = undefined;
	function endEditing(){
		if (editIndex == undefined){return true;}
		if ($('#legalBasisTable').datagrid('validateRow', editIndex)){
			$('#legalBasisTable').datagrid('endEdit', editIndex);
			editIndex = undefined;
			return true;
		} else {
			return false;
		}
	}
	function onClickRow(index){
		if (editIndex != index){
			if (endEditing()){
				$('#legalBasisTable').datagrid('selectRow', index).datagrid('beginEdit', index);
				editIndex = index;
			} else {
				$('#legalBasisTable').datagrid('selectRow', editIndex);
			}
		}
	}
	function append(){
		if (endEditing()){
			$('#legalBasisTable').datagrid('appendRow',{});
			editIndex = $('#legalBasisTable').datagrid('getRows').length-1;
			$('#legalBasisTable').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
		}
	}
	function removeit(){
		if (editIndex == undefined){return;}
		$('#legalBasisTable').datagrid('cancelEdit', editIndex).datagrid('deleteRow', editIndex);
		editIndex = undefined;
	}
	function accept(){
        if (endEditing()){
            $('#legalBasisTable').datagrid('acceptChanges');
        }
    }
	function reject(){
        $('#legalBasisTable').datagrid('rejectChanges');
        editIndex = undefined;
    }
