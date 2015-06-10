<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>送审稿审查列表</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">
	$(function() {
		$('#examSubName').focus();
		$("#btnAdd").click(function() {
			location.href = "${pageContext.request.contextPath}/examDraft/examDraftReview.wf";
		});
		$('#sStatus').combobox( {
			url : '${pageContext.request.contextPath}/examDraftRev/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		$("#examSubName").focus(function() {
			$('#examSubName').attr('placeholder','输入查询条件...');
			$('#examSubName').css('font-size','100%');
			$('#examSubName').css('color','black');
		}).blur(function() {
			$('#examSubName').attr('placeholder','输入查询条件...');
			$('#examSubName').css('color','rgba(0, 0, 0, 0.45)');
		});
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$("#btnQuery").click(function() {
			$('#dataGrid').datagrid('load', {
				name : $('#examSubName').val(),
				draftingUnit : $('#sDraftingUnit').combotree('getValue'),
				reviewUnit : $('#sReviewUnit').combotree('getValue'),
				status : $('#sStatus').combobox('getValue')
			});
		});

	$('#dataGrid').datagrid({
		url : '${pageContext.request.contextPath}/examDraftRev/searchDraftRevs.do',
		onDblClickRow: function (rowIndex, rowData) {
			$('#dataGrid').datagrid('selectRow',rowIndex);
			var selectRow =$("#dataGrid").datagrid("getSelected");
			var id=selectRow.id;
			location.href = "${pageContext.request.contextPath}/examDraft/examDraftReview.wf?id="+id;
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
});
</script>
</head>
<body>
<div data-options="region:'center', split:true">
	<div id="cc" class="easyui-layout" style="width: 1200px; height: 733px;">
		<table style="height: 30px;" class="spe_table">
			<tr>
				<td style="text-align: left;">
					<div id="btnAdd" class="button_change red center">新增</div>
				</td>
			</tr>
		</table>
		<table cellpadding="0" cellspacing="0" class="spe_table">
			<tr>
				<td style="width: 120px; height: 20px">文件名称</td>
				<td id="examSubNameTd" style="text-align: left; height: 20px">
					<input id="examSubName" class="spe_table_input" placeholder="" 
							onfocus="if(this.value==this.defaultValue)this.value='';" 
							onblur="if(this.value=='')this.value=this.defaultValue;"
							style="border: 0; margin: 0;" />
				</td>
				<td style="width: 120px; height: 20px">起草单位</td>
				<td style="text-align: left; height: 20px">
					<input id="sDraftingUnit" class="easyui-combotree" />
					</td>
					<td style="width: 100px; height: 20px; text-align: left;" rowspan="2">
						<div class="button_change red center" id="btnQuery">查询</div>
				</td>
			</tr>
			<tr>
				<td style="width: 120px; height: 20px">审核单位</td>
				<td style="text-align: left; height: 20px">
					<input id="sReviewUnit" class="easyui-combotree" />
				</td>
				<td style="width: 120px; height: 20px">状态</td>
				<td style="text-align: left; height: 20px">
					<input id="sStatus" type="text" class="easyui-combobox" />
				</td>
			</tr>
		</table>
		<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
			<table id="dataGrid" data-options="fitColumns:true,singleSelect:true,
					pagination:true,rownumbers:true,height:306,border:0">
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
		</div>
	</div>
</div>

</body>
</html>