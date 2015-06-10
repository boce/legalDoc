<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>备案台账</title>
<%@include file="/pages/share/base.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/recRevReport.css"/>
<script type="text/javascript">

	var idArrs = new Array();
	var condtionArrs = new Array();
	var valueArrs = new Array();
	var bdate = null;
	var edate = null;
	
	$(function() {
		
		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('#dataGrid').datagrid({
			url : '${pageContext.request.contextPath}/report/getRecReviews.do'
		});
		
		$('#scheme').click(function() {
			$('#searchScheme').window('open');
		});
		$('#valuedecUnit').combotree({   
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
		
		$('#confirm').click(function() {
			idArrs = [];
			condtionArrs = [];
			valueArrs = [];
			var pno = $('#valuepublishNo').val();
			var cpno = $('#condtionpublishNo').val();
			var name = $('#valuename').val();
			var condname = $('#condtionname').val();
			var decu = $('#valuedecUnit').combotree('getValue');
			bdate = $('#valuebegDate').datebox('getValue');
			edate = $('#valueendDate').datebox('getValue');
			if (name != '' && name != null) {
				idArrs.push('name');
				valueArrs.push(name);
				condtionArrs.push(condname);
			}
			if (pno != '' && pno != null) {
				idArrs.push('publishNo');
				valueArrs.push(pno);
				condtionArrs.push(cpno);
			} 
			if (decu != null && decu != ''){
				idArrs.push('decUnit');
				valueArrs.push(decu);
				condtionArrs.push('et');
			}
			$('#dataGrid').datagrid({
				url : '${pageContext.request.contextPath}/report/getRecReviews.do?nameList=' + idArrs.toString() +
						'&condList=' + condtionArrs.toString() + '&valueList=' + valueArrs.toString() +
						'&begDate=' + bdate + '&endDate=' + edate
			});
			$('#dataGrid').datagrid('reload');
			$('#searchScheme').window('close');
		});
		
		
		
		$('#cancel').click(function() {
			$('#searchScheme').window('close');
		});
		
		//导出
		$('#download').click(function() {
			var page = $('#dataGrid').datagrid('options').pageNumber;
			var rows = $('#dataGrid').datagrid('options').pageSize;
			bdate = $('#valuebegDate').datebox('getValue');
			edate = $('#valueendDate').datebox('getValue');
			$("#downloadId").attr("action", "${pageContext.request.contextPath}/report/recRevDownload.do?nameList=" + 
					idArrs.toString() + '&condList=' + condtionArrs.toString() + '&valueList=' + valueArrs.toString() +
					'&begDate=' + bdate + '&endDate=' + edate + "&page=" + page + "&rows=" + rows);
			$("#downloadId").submit();
		});
		
	});
	
</script>
</head>
<body>
		<div id="cc" class="easyui-layout cc">
			<table  class="table">
				<tr>
					<td id="btnTools" style="text-align: left;">
						<div id="scheme" class="button_change red center">查询方案</div>
						<div id="download" class="button_change red center">下载</div>
						<form id="downloadId" class="downloadId" action="" method="post" ></form>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow"  class="dataDivWindow" >
				<table id="dataGrid" class="easyui-datagrid" data-options="fitColumns:true,singleSelect:true,
						pagination:true,rownumbers:true,height:306,border:0">
					<thead>
						<tr>
							<th data-options="field:'publishNo',halign:'center', width:80, formatter:function(value,row){return row.normativeFile.publishNo;}">发文号</th>
							<th data-options="field:'name',halign:'center', width:280">文件名称</th>
							<th data-options="field:'decisionMakingUnit', halign:'center', width:120, formatter:function(value,row){
	                            return row.decisionMakingUnit.text;
	                        }">制定单位</th>
	                        <th data-options="field:'drtUnit', halign:'center', width:120, formatter:function(value,row){
	                            return row.normativeFile.drtUnit.text;
	                        }">起草单位</th>
	                        <th data-options="field:'publishDate',halign:'center', width:120, formatter:function(value,row){return row.normativeFile.publishDate;} ">发布日期</th>
							<th data-options="field:'recordDate',halign:'center', width:120, formatter:function(value,row){return row.normativeFile.registerDate;}">备案日期</th>
							<th data-options="field:'recordRevUnitClerk', halign:'center', width:120, formatter:function(value,row){
	                            return row.recordRevUnitClerk.name;
	                        }">备案人</th>
	                        <th data-options="field:'recordRevUnit', halign:'center', width:120, formatter:function(value,row){
	                            return row.recordRevUnit.text;
	                        }">备案机关</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	
	<!-- 查询方案页面 -->
	<div id="searchScheme" title="查询方案" class="easyui-window" data-options=
		"collapsible:false, minimizable:false, maximizable:false, closed:true,width: 500, height: 260, padding: 0">
		<div class="searchScheme">
			<table id="searchTable" class="table">
				<tr>
					<th class="searchScheme-th1">条件</th>
					<th class="searchScheme-th1" >栏目</th>
					<th class="searchScheme-th1">内容</th>
				</tr>
				<tr>
					<td  class="searchScheme-td-label" >文件名称</td>
						<td  class="condtionname" >
							<select id='condtionname'>
							    <option value='et'>等于</option>
							    <option value='ct'>包含</option>
							</select>
						</td>
						<td  class="valuename" >
							<input type='text' id='valuename'/>
						</td>
				</tr>
				<tr>
					<td class="searchScheme-td-label">制作单位</td>
						<td class="condtionname">
							<select id='condtiondecUnit' >
							    <option value='et'>等于</option>
							</select>
						</td>
						<td class="valuename">
							<input type='text' id='valuedecUnit'/>
						</td>
				</tr>
				<tr>
					<td class="searchScheme-td-label">发文号</td>
						<td class="condtionname">
							<select id='condtionpublishNo' >
							    <option value='et'>等于</option>
							    <option value='ct'>包含</option>
							</select>
						</td>
						<td class="valuename">
							<input type='text' id='valuepublishNo'/>
						</td>
				</tr>
				<tr>
					<td class="searchScheme-td-label">开始时间</td>
						<td class="condtionname">
							<select id='condtionbegDate' >
							    <option value='et'>等于</option>
							</select>
						</td>
						<td class="valuename">
							<input type='text' id='valuebegDate' class='easyui-datebox'/>
						</td>
				</tr>
				<tr>
					<td class="searchScheme-td-label">结束时间</td>
						<td class="condtionname">
							<select id='condtionendDate' >
							    <option value='et'>等于</option>
							</select>
						</td>
						<td class="valuename">
							<input type='text' id='valueendDate' class='easyui-datebox'/>
						</td>
				</tr>
			</table>
		</div>
		
		<div data-options="region:'south', collapsible:false, height:30, padding: 0, margin: 0" >
	    	<table class="table-button" >
				<tr >
					<td class="td-space"></td>
					<td><input id="confirm" type="button" value="确认" /></td>
					<td><input id="cancel" type="button" value="取消" /></td>
				</tr>
			</table>
	    </div> 
	</div>
	
</body>
</html>