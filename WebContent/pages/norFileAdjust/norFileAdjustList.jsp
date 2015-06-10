<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件调整列表</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">
	var fileStatus = {"VALID":"生效","INVALID":"失效","MODIFY":"修订","REVOKE":"撤销","ABOLISH":"废止"};
	$(function() {
		$('#nameId').focus();
		$("#btnAdd").click(function() {
			location.href = "${pageContext.request.contextPath}/norFileAdjust/norFileAdjust.wf";
		});
		//autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$("#btnQuery").click(function() {
			var startdateid = $("#startDateId").datebox('getValue');
			var enddateid = $("#endDateId").datebox('getValue');

			$('#queryDataGridId').datagrid('load', {
				name : $('#nameId').val(),
				legalBasis : $("#legalBasisId").val(),
				startDate : startdateid,
				endDate : enddateid
			});
		});
		$('#queryDataGridId').datagrid({
			url : '${pageContext.request.contextPath}/norFileAdjust/query.do'
		});

		$("#btnSave").click(function() {
			var batchdata = batchSave();
			if(batchdata == ""){
				showMsg("没有修改记录！");
				return;
			}

			$.ajax({
				type : "POST",
				url : "${pageContext.request.contextPath}/norFileAdjust/batchSave.do",
				data : batchdata,
				contentType : "application/json;charset=utf-8",
				success : function(data) {

					$('#queryDataGridId').datagrid('reload');
				},
				error : function(data) {
					showMsg("保存出错,请重试!");
				}
			});
		});
	});

	function endEdit() {
		var rows = $('#queryDataGridId').datagrid('getRows');
		for (var i = 0; i < rows.length; i++) {
			$('#queryDataGridId').datagrid('endEdit', i);
		}
	}

	function batchSave() {

		endEdit();
		if ($('#queryDataGridId').datagrid('getChanges').length) {
			var updated = $('#queryDataGridId').datagrid('getChanges', "updated");
			var effectRow = new Object();
			if (updated.length) {
				effectRow = JSON.stringify(updated);
			}

			return effectRow;
		}else{
			return "";
		}
	}

	var editIndex = undefined;
	function endEditing() {
		if (editIndex == undefined) {
			return true;
		}

		$('#queryDataGridId').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;

	}
	function formatStatus(val, row) {
		return fileStatus[val];
	}
	function onClickRow(index) {
		if (editIndex != index) {
			if (endEditing()) {
				$('#queryDataGridId').datagrid('selectRow', index).datagrid(
						'beginEdit', index);
				editIndex = index;
			} else {
				$('#queryDataGridId').datagrid('selectRow', editIndex);
			}
		}
	}
</script>
</head>
<body>
	<div data-options="region:'center', split:true">
		<div id="cc" class="easyui-layout" style="width: 1200px; height: 733px;">
			<table style="height: 30px;" class="spe_table">
				<tr>
					<td style="text-align: left;">
						<div id="btnAdd" class="button_change red center">新增</div>
						<div id="btnSave" class="button_change red center">保存</div>
					</td>
				</tr>
			</table>
			<table cellpadding="0" cellspacing="0" class="spe_table">
				<tr>
					<td style="width: 120px;">文件名称</td>
					<td id="nameIdTd" colspan="4" style="width: 848px;">
						<input id="nameId" name="name" placeholder=""
							onfocus="if(this.value==this.defaultValue)this.value='';" 
							onblur="if(this.value=='')this.value=this.defaultValue;"
							style="width: 99%;border: 0;" type="text"/>
					</td>
				</tr>
				<tr>
					<td style="width: 120px;">相关依据</td>
					<td colspan="4" style="width: 848px;">
						<input id="legalBasisId" name="legalBasis" style="width: 99%; border: 0;" type="text"/>
					</td>
				</tr>
				<tr>
					<td style="width: 120px;">发布开始日期</td>
					<td style="width: 136px;">
						<input id="startDateId" type="text" class="easyui-datebox"/>
					</td>
					<td style="width: 120px;">发布结束日期</td>
					<td style="width: 136px;">
						<input id="endDateId" type="text" class="easyui-datebox"/>
					</td>
					<td style="width: 486px; text-align: left;">
						<div id="btnQuery" class="button_change red center">查询</div>
					</td>
				</tr>
			</table>

			<table id="queryDataGridId" class="easyui-datagrid" data-options="fitColumns:true,singleSelect:false,pagination:true,rownumbers:true,onClickRow: onClickRow,height:306,border:0">
				<thead>
					<tr>
						<th data-options="field:'ck',checkbox:true"></th>
						<th
							data-options="field:'id',halign:'center',hidden:true,width:100">id</th>
						<th data-options="field:'name',halign:'center',width:300">文件名称</th>
						<th
							data-options="field:'decUnit',halign:'center',width:100,formatter:function(value,row){
                            return row.decUnit.text;
                        }">制定单位</th>
						<th
							data-options="field:'drtUnit',halign:'center',width:100,formatter:function(value,row){
                            return row.drtUnit.text;
                        }">起草单位</th>
						<th
							data-options="field:'status',halign:'center',width:50,formatter:formatStatus,editor:{
                            type:'combobox',
                            options:{
                                valueField:'name',
                                textField:'title',
                                method:'get',
                                url:'${pageContext.request.contextPath}/norFileAdjust/getStatus.do'
                            }
                        }">状态</th>
						<th
							data-options="field:'invalidReason',halign:'center',width:448,editor:'text'">修订、撤销、废止原因</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>

</body>
</html>