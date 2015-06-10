<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>所有任务</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/jquery/css/default/easyui.css" />
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-1.11.1.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		var jobN;
		var jobG;
		$(function(){
			 	
		});
		function addJobDialog()
		{
			$('#addingjob').dialog('open');
		}
		
		function addJob()
		{
			var name = $('#nameId').val();
			var group = $('#groupId').val();
			var express = $('#expressId').val();
			var status = $('#statusId').val();
			var description = $('#descriptionId').val();
			alert(express);
			$.ajax({
			  type: "POST",
			  url: "${pageContext.request.contextPath}/timerManage/addOrUpdateJob.do",
			  data : {jobName:name, jobGroup:group, cronExpression:express, jobStatus:status, description:description}
			});
		}
		
		function pauseJob(jobName, jobGroup)
		{
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/timerManage/pauseJob.do",
				  data: {jobName:jobName, jobGroup:jobGroup},
				  success:function()
				  {
					  
				  }
				});
		}
		function resumeJob(jobName, jobGroup)
		{
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/timerManage/resumeJob.do",
				  data: {jobName:jobName, jobGroup:jobGroup},
				  success:function()
				  {
					  
				  }
				});
		}
		function deleteJob(jobName, jobGroup)
		{
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/timerManage/deleteJob.do",
				  data: {jobName:jobName, jobGroup:jobGroup},
				  success:function()
				  {
					  
				  }
				});
		}
		function runOnce(jobName, jobGroup)
		{
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/timerManage/runOnce.do",
				  data: {jobName:jobName, jobGroup:jobGroup},
				  success:function()
				  {
					  
				  }
				});
		}
		function clickModify(jobName, jobGroup) {
			jobN = jobName;
			jobG = jobGroup;
			$('#modifyExpress').dialog('open');
			
		}
		function modifyExpression(jobName, jobGroup) {
			var va = $('#expressionId').val();
			alert(jobN);
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/timerManage/modifyExpression.do",
				  data: {jobName:jobN, jobGroup:jobG, expression:va},
				  success:function()
				  {
					  
				  }
				});
			$('#modifyExpress').dialog('close');
			window.location.reload();
		}
	</script>
</head>
<body>
	<input type="button" onclick="addJobDialog();" value="新增任务"/>
	<table border="1" cellpadding="0" cellspacing="0">
		<tr>
			<td>任务名</td>
			<td>任务组</td>
			<td>时间表达式</td>
			<td>状态</td>
			<td>备注</td>
			<td>操作</td>
		</tr>
		<c:forEach var="job" items="${jobList}">
		<tr>
			<td>${job.jobName}</td>
			<td>${job.jobGroup}</td>
			<td>${job.cronExpression}</td>
			<td>${job.jobStatus}</td>
			<td>${job.description}</td>
			<td>
				<input type="button" value="暂停" onclick="pauseJob('${job.jobName}','${job.jobGroup}');"/>
				<input type="button" value="恢复" onclick="resumeJob('${job.jobName}','${job.jobGroup}');"/>
				<input type="button" value="删除" onclick="deleteJob('${job.jobName}','${job.jobGroup}');"/>
				<input type="button" value="修改表达式" onclick="clickModify('${job.jobName}','${job.jobGroup}');"/>
				<input type="button" value="立即运行一次" onclick="runOnce('${job.jobName}','${job.jobGroup}');"/>
			</td>
		</tr>
		</c:forEach>
	</table>
	<div id="modifyExpress" class="easyui-dialog" closed="true" title="My Dialog" style="width:400px;height:200px"  
        data-options="iconCls:'icon-save',resizable:true,modal:true">
   		<input id="expressionId" type="text" name="expresssion" value=""/>
   		<input type="button" value="执行" onclick="modifyExpression();"/>
	</div>
	<div id="addingjob" class="easyui-dialog" closed="true" title="添加任务" style="width:400px;height:200px"  
        data-options="iconCls:'icon-save',resizable:true,modal:true">
        <table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>名称：</td><td><input id="nameId" type="text" name="name" value=""/></td>
				<td>组名称：</td><td><input id="groupId" type="text" name="group" value=""/></td>
			</tr>
			<tr><td>时间表达式:</td><td><input id="expressId" type="text" name="express" value=""/></td>
				<td>状态:</td><td><input id="statusId" type="text" name="status" value=""/></td>
			</tr>
			<tr><td>描述：</td><td colspan="3"><input id="descriptionId" type="text" name="description" value=""/></td></tr>
			<tr><td colspan="4"><input type="button" value="添加" onclick="addJob();"/></td></tr>
		</table>
	</div>
</body>
</html>