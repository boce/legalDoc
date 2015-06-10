<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>期满评估列表</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">
	$(function() {
		$('#daName').focus();
		$("#btnAdd").click(function(){
			location.href = "${pageContext.request.contextPath}/da/da.wf";
		});
		$('#status').combobox({
			url : '${pageContext.request.contextPath}/da/getStatus.do',
			valueField : 'name',
			textField : 'title'
		});
		$("#daName").focus(function() {
			$('#daName').attr('placeholder','输入查询条件...');
			$('#daName').css('font-size','100%');
			$('#daName').css('color','black');
		}).blur(function() {
			$('#daName').attr('placeholder','输入查询条件...');
			$('#daName').css('color','rgba(0, 0, 0, 0.45)');
		});
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$("#btnQuery").click(function() {
			$('#dataGrid').datagrid('load', {
				name : $('#daName').val(),
				draftingUnit : $('#draftingUnit').combotree('getValue'),
				decisionUnit : $('#decisionUnit').combotree('getValue'),
				status : $('#status').combobox('getValue')
			});
		});

		$('#dataGrid').datagrid({
			url : '${pageContext.request.contextPath}/da/searchAll.do',
			onDblClickRow: function (rowIndex, rowData) {
				$('#dataGrid').datagrid('selectRow',rowIndex);
				  var selectRow =$("#dataGrid").datagrid("getSelected");
				  var id=selectRow.id;
				  location.href = "${pageContext.request.contextPath}/da/da.wf?id="+id;
			}
		});
		
		//初始化combotree
		$('#draftingUnit').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		alert('只能选择部门!');
	        		return false; 
	        	}
            }
		}); 
		
		$('#decisionUnit').combotree({   
			valueField:'id',
			textField:'name',
			url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
			onBeforeSelect : function(node){
	        	if (node.attributes.nodeType == 'district') {
	        		alert('只能选择部门!');
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
					<td id="daNameTd" style="text-align: left; height: 20px">
						<input class="spe_table_input" id="daName" placeholder=""
							onfocus="if(this.value==this.defaultValue)this.value='';" 
							onblur="if(this.value=='')this.value=this.defaultValue;" style="border: 0; margin: 0;" />
					</td>
					<td style="width: 120px; height: 20px">起草单位</td>
					<td style="text-align: left; height: 20px">
						<input id="draftingUnit" name="draftingUnitName" class="easyui-combotree" />
					</td>
				</tr>
				<tr>
					<td style="width: 120px; height: 20px">制定单位</td>
					<td style="text-align: left; height: 20px">
						<input id="decisionUnit" name="decisionUnitName" class="easyui-combotree" />
					</td>
					<td style="width: 120px; height: 20px">状态</td>
					<td style="text-align: left; height: 20px">
						<input id="status" type="text" class="easyui-combobox" />
					</td>
						
					<td style="width: 100px; height: 20px; text-align: left;">
						<div class="button_change red center" id="btnQuery">查询</div>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
				<table id="dataGrid" data-options="fitColumns:true,singleSelect:true,
						pagination:true,rownumbers:true,height:306,border:0">
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
			</div>
		</div>
	</div>

</body>
</html>