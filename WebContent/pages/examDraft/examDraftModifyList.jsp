<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>送审稿修改列表</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">
	$(function() {
		$('#nameId').focus();
		$("#btnAdd")
				.click(
						function() {
							location.href = "${pageContext.request.contextPath}/examDraft/examDraftModify.wf";
						});
		$("#btnQuery").click(function() {
			
			$('#dataGridId').datagrid('load', {
				name : $('#nameId').val()
			});
		});

		$('#dataGridId').datagrid({
			url : '${pageContext.request.contextPath}/examDraftMod/searchDraftMods.do'
		});
		$("#nameId").focus(function() {
			$('#nameId').attr('placeholder','输入查询条件...');
			$('#nameId').css('font-size','100%');
			$('#nameId').css('color','black');
		}).blur(function() {
			$('#nameId').attr('placeholder','输入查询条件...');
			$('#nameId').css('color','rgba(0, 0, 0, 0.45)');
		});
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('#dataGridId').datagrid({
			onDblClickRow: function (rowIndex, rowData) {
				$('#dataGridId').datagrid('selectRow',rowIndex);
				  var selectRow =$("#dataGridId").datagrid("getSelected");
				  var id=selectRow.id;
				  location.href = "${pageContext.request.contextPath}/examDraft/examDraftModify.wf?id="+id;
			}
		});
	});
</script>
</head>
<body>
	<div data-options="region:'center', split:true">
		<div id="cc" class="easyui-layout" style="width: 1200px; height: 733px;">
			<table style="height: 30px;" class=spe_table>
				<tr>
					<td style="text-align: left;">
						<div id="btnAdd" class="button_change red center">新增</div>
					</td>
				</tr>
			</table>
			<table cellpadding="0" cellspacing="0" class="spe_table">
				<tr>
					<td style="width: 120px; height: 20px">文件名称</td>
					<td id="nameIdTd" style="text-align: left; height: 20px">
						<input class="spe_table_input" id="nameId" name="examDraftModifyName" placeholder=""
							onfocus="if(this.value==this.defaultValue)this.value='';" 
							onblur="if(this.value=='')this.value=this.defaultValue;"
							style="margin: 3px; border: 0px none;" />
					</td>
					<td style="width: 100px; height: 20px; text-align: left;">
						<div class="button_change red center" id="btnQuery">查询</div>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
					<table id="dataGridId" data-options="fitColumns:true,singleSelect:true,pagination:true,rownumbers:true,height:306,border:0">
						<thead>
							<tr>
								<th data-options="field:'id',halign:'center',hidden:true,width:100">id</th>
								<th data-options="field:'name',halign:'center',width:878">文件名称</th>
								<th
									data-options="field:'draftingUnit',halign:'center',width:120,formatter:function(value,row){
		                            return row.draftingUnit.text;
		                        }">起草单位</th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
		</div>
	</div>

</body>
</html>