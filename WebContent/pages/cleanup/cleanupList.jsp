<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件清理</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">
	var cleanupUnit = 0;
	var pageN = 0;
	var pageS = 10;
	$(function() {
		$('#nameId').focus();
		$('#addBtn').click(function(){
			location.href = "${pageContext.request.contextPath}/cleanup/cleanup.wf";
		});
		
		$('#searchBtn').click(function(){
			var cUnit = $('#cleanupHidden').val();
			if (cUnit !='') {
				cleanupUnit = cUnit;
			}
			var cleanupBegDate = $('#cleanupBegDateId').datetimebox('getValue');
			var cleanupEndDate = $('#cleanupEndDateId').datetimebox('getValue');
			if (cleanupBegDate > cleanupEndDate) {
				showMsg('开始时间应该小于结束时间');
				return ;
			}
			$('#dataGridId').treegrid('getPager').pagination('loading');
          	$('#dataGridId').treegrid('options').url = 
          		'${pageContext.request.contextPath}/cleanup/searchAll.do?page=' + pageN + '&rows=' + pageS
          		 + '&cleanupUnit=' + cleanupUnit + '&cleanupBegDate=' + cleanupBegDate + '&cleanupEndDate=' + cleanupEndDate;
          	$('#dataGridId ').treegrid('reload');
          	$('#dataGridId').treegrid('getPager').pagination('loaded');
		});
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$("#nameId").focus(function() {
			$('#nameId').attr('placeholder','输入查询条件...');
			$('#nameId').css('font-size','100%');
			$('#nameId').css('color','black');
		}).blur(function() {
			$('#nameId').attr('placeholder','输入查询条件...');
			$('#nameId').css('color','rgba(0, 0, 0, 0.45)');
		});
		$('#dataGridId').treegrid({
			onDblClickRow: function (field, row) {
				var node = $('#dataGridId').treegrid('getSelected');
				if (node.id.indexOf('c') == -1) {	//只有父节点才可以有点击事件
			    	location.href = "${pageContext.request.contextPath}/cleanup/cleanup.wf?id=" + node.id;
				}
			},
			url: "${pageContext.request.contextPath}/cleanup/searchAll.do"
		});
		
		$('#dataGridId').treegrid('getPager').pagination({
			onSelectPage : function (pageNumber, pageSize) {	//分页page和rows参数的获取
				var cUnit = $('#cleanupHidden').val();
				if (cUnit !='') {
					cleanupUnit = cUnit;
				}
				var cleanupBegDate = $('#cleanupBegDateId').datetimebox('getValue');
				var cleanupEndDate = $('#cleanupEndDateId').datetimebox('getValue');
	          	$(this).pagination('loading');
	          	$('#dataGridId').treegrid('options').url = 
	          		'${pageContext.request.contextPath}/cleanup/searchAll.do?page=' + pageNumber + '&rows=' + pageSize
	          		 + '&cleanupUnit=' + cleanupUnit + '&cleanupBegDate=' + cleanupBegDate + '&cleanupEndDate=' + cleanupEndDate;
	          	$('#dataGridId ').treegrid('reload');
	          	$(this).pagination('loaded');
	          	pageN = pageNumber;
	          	pageS = pageSize;
		    },
		    onRefresh : function(pageNumber, pageSize) {
		    	var cUnit = $('#cleanupHidden').val();
				if (cUnit !='') {
					cleanupUnit = cUnit;
				}
				var cleanupBegDate = $('#cleanupBegDateId').datetimebox('getValue');
				var cleanupEndDate = $('#cleanupEndDateId').datetimebox('getValue');
		    	$(this).pagination('loading');
	          	$('#dataGridId').treegrid('options').url = 
	          	'${pageContext.request.contextPath}/cleanup/searchAll.do?page=' + pageNumber + '&rows=' + pageSize
	          	 + '&cleanupUnit=' + cleanupUnit + '&cleanupBegDate=' + cleanupBegDate + '&cleanupEndDate=' + cleanupEndDate;
	          	$('#dataGridId ').treegrid('reload');
	          	$(this).pagination('loaded');
	          	pageN = pageNumber;
	          	pageS = pageSize;
		    },
		    onChangePageSize : function(pageSize) {
		    	var cUnit = $('#cleanupHidden').val();
				if (cUnit !='') {
					cleanupUnit = cUnit;
				}
				var cleanupBegDate = $('#cleanupBegDateId').datetimebox('getValue');
				var cleanupEndDate = $('#cleanupEndDateId').datetimebox('getValue');
		    	$(this).pagination('loading');
	          	$('#dataGridId').treegrid('options').url = 
	          		'${pageContext.request.contextPath}/cleanup/searchAll.do?page=' + $(this).pageNumber + '&rows=' + pageSize
	          		 + '&cleanupUnit=' + cleanupUnit + '&cleanupBegDate=' + cleanupBegDate + '&cleanupEndDate=' + cleanupEndDate;
	          	$('#dataGridId ').treegrid('reload');
	          	$(this).pagination('loaded');
	          	pageN = $(this).pageNumber;
	          	pageS = pageSize;
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
						<div id="addBtn" class="button_change red center">新增</div>
					</td>
				</tr>
			</table>
			<table cellpadding="0" cellspacing="0" class="spe_table">
				<tr>
					<td style="width: 120px; height: 20px">清理单位</td>
					<td style="width:140px; height:20px; text-align: left">
						<input class="easyui-combotree" id="cleanupUnitId" name="cleanupUnit" 
						data-options="valueField:'id',textField:'name',
						url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
						onSelect: function(node){
				            $('#cleanupHidden').val(node.id); 
				        },
				        onBeforeSelect : function(node){
				        	if (node.attributes.nodeType == 'district') {
				        		showMsg('只能选择部门!');
				        		return false; 
				        	}
                        }" /> <input id="cleanupHidden" type="hidden" />
					</td>
					<td style="width: 120px; height: 20px">清理开始日期</td>
					
					<td style="width:140px; height:20px; text-align: left">
						<input id="cleanupBegDateId" type="text" class="easyui-datetimebox"/>
					</td>
					<td style="width: 120px; height: 20px">清理结束日期</td>
					<td style="width:140px; height:20px; text-align: left">
						<input id="cleanupEndDateId" type="text" class="easyui-datetimebox"/>
					</td>
					<td style="width: 100px; height: 20px; text-align: left;">
						<div class="button_change red center" id="searchBtn">查询</div>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow" style="height:500px;overflow-y: auto;overflow-x: hidden;">
				<table id="dataGridId" class="easyui-treegrid"
					data-options="
						url: '',
						method: 'get',
						rownumbers: true,
						idField: 'id',
						treeField: 'name',
						pagination: true,
						height:306,
						onBeforeLoad: function (row, param) { 
							if (row) { 
								$('#dataGridId').treegrid('options').url = '${pageContext.request.contextPath}/cleanup/gainChildren.do?parentId=' + row.id; 
							 }            
						}
					">
					<thead>
						<tr>
							<th data-options="field:'name',width:280">名称</th>  
				            <th data-options="field:'decUnit',width:230">指定单位</th>  
				            <th data-options="field:'docNo',width:175">文件号</th>  
				            <th data-options="field:'publishDate',width:70">发布日期</th>  
				            <th data-options="field:'status',width:40">状态</th>  
				            <th data-options="field:'remark',width:250">备注</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>

</body>
</html>