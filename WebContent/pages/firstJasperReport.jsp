<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Jasper报表例子</title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-1.11.1.js"></script>
	<style type="text/css">
		body{margin: 0px;padding: 0px;width: 100%;height: 100%;}
		.queryDiv{
			border: 1px solid #000000;
			width: 98%;
			height: 30px;
			margin-top: 10px;
			margin-left: 5px;
			text-align: center;
		}
		.reportDiv{
			border: 1px solid #000000;
			width: 98%;
			margin-top: 5px;
			margin-left: 5px;
			text-align: center;
		}
		table{
			margin-left: auto;
			margin-right: auto;
		}
	</style>
	<script type="text/javascript">
		function query()
		{
			$("#queryForm").submit();
		}
	</script>
</head>
<body>
	<div class="queryDiv">
		<form id="queryForm" method="post" action="${pageContext.request.contextPath}/jasperReport/showReport.do;">
			<label>编号：</label><input id="id_" name="id_" type="text" value="${id_}"/>
			<input type="button" value="查询" onclick="query();"/>
		</form>
		
	</div>
	<div class="reportDiv">
		${reportContent}
	</div>
</body>
</html>