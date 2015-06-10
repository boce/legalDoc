<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@include file="/pages/share/base.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/legaldoc/countReport.css"/>
<script type="text/javascript">

	var idArrs = new Array();
	var condtionArrs = new Array();
	var valueArrs = new Array();
	var bdate = null;
	var edate = null;
	
	$(function() {
		//适应分辨率高度
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		//导出
		$('#download').click(function() {
			var year = $('#year').numberspinner('getValue');
			if (year == "" || year == null) {
				alert("请查询年度统计记录！");
			} else {
				$("#downloadId").attr("action", "${pageContext.request.contextPath}/report/downloadNorFileCount.do?year=" + 
						year);
				$("#downloadId").submit();
			}
		});
		
		$('#search').click(function() {
			var year = $('#year').numberspinner('getValue');
			$("#yearLabel").text(year);
			$.ajax({
				  type: "POST",
				  url: "${pageContext.request.contextPath}/report/gainNorFileNums.do",
				  data : {year : year},
				  dataType : 'json',
				  success : function(data)
				  {
					  //市级政府
					  $('#cityGov').children('td').eq(1).html(data[0].all);
					  $('#cityGov').children('td').eq(2).html(data[0].publish);
					  $('#cityGov').children('td').eq(3).html(data[0].review);
					  $('#cityGov').children('td').eq(4).html(data[0].record);
					  $('#cityGov').children('td').eq(5).html(data[0].deliberation);
					  $('#cityGov').children('td').eq(6).html(data[0].decUnitOop);
					  $('#cityGov').children('td').eq(7).html(data[0].decProcedureOop);
					  $('#cityGov').children('td').eq(8).html(data[0].contentOop);
					  $('#cityGov').children('td').eq(9).html(data[0].decTechHasDefects);
					  $('#cityGov').children('td').eq(10).html(data[0].others);
					  $('#cityGov').children('td').eq(11).html(data[0].self);
					  $('#cityGov').children('td').eq(12).html(data[0].revoke);
					  $('#cityGov').children('td').eq(13).html(data[0].qualified);
					  
					  //市级政府部门
					  $('#cityGovWork').children('td').eq(1).html(data[1].all);
					  $('#cityGovWork').children('td').eq(2).html(data[1].publish);
					  $('#cityGovWork').children('td').eq(3).html(data[1].review);
					  $('#cityGovWork').children('td').eq(4).html(data[1].record);
					  $('#cityGovWork').children('td').eq(5).html(data[1].deliberation);
					  $('#cityGovWork').children('td').eq(6).html(data[1].decUnitOop);
					  $('#cityGovWork').children('td').eq(7).html(data[1].decProcedureOop);
					  $('#cityGovWork').children('td').eq(8).html(data[1].contentOop);
					  $('#cityGovWork').children('td').eq(9).html(data[1].decTechHasDefects);
					  $('#cityGovWork').children('td').eq(10).html(data[1].others);
					  $('#cityGovWork').children('td').eq(11).html(data[1].self);
					  $('#cityGovWork').children('td').eq(12).html(data[1].revoke);
					  $('#cityGovWork').children('td').eq(13).html(data[1].qualified);
					  
					  //县级政府
					  $('#countyGov').children('td').eq(1).html(data[2].all);
					  $('#countyGov').children('td').eq(2).html(data[2].publish);
					  $('#countyGov').children('td').eq(3).html(data[2].review);
					  $('#countyGov').children('td').eq(4).html(data[2].record);
					  $('#countyGov').children('td').eq(5).html(data[2].deliberation);
					  $('#countyGov').children('td').eq(6).html(data[2].decUnitOop);
					  $('#countyGov').children('td').eq(7).html(data[2].decProcedureOop);
					  $('#countyGov').children('td').eq(8).html(data[2].contentOop);
					  $('#countyGov').children('td').eq(9).html(data[2].decTechHasDefects);
					  $('#countyGov').children('td').eq(10).html(data[2].others);
					  $('#countyGov').children('td').eq(11).html(data[2].self);
					  $('#countyGov').children('td').eq(12).html(data[2].revoke);
					  $('#countyGov').children('td').eq(13).html(data[2].qualified);
					  
					  
					  //县级政府部门
					  $('#countyGovWork').children('td').eq(1).html(data[3].all);
					  $('#countyGovWork').children('td').eq(2).html(data[3].publish);
					  $('#countyGovWork').children('td').eq(3).html(data[3].review);
					  $('#countyGovWork').children('td').eq(4).html(data[3].record);
					  $('#countyGovWork').children('td').eq(5).html(data[3].deliberation);
					  $('#countyGovWork').children('td').eq(6).html(data[3].decUnitOop);
					  $('#countyGovWork').children('td').eq(7).html(data[3].decProcedureOop);
					  $('#countyGovWork').children('td').eq(8).html(data[3].contentOop);
					  $('#countyGovWork').children('td').eq(9).html(data[3].decTechHasDefects);
					  $('#countyGovWork').children('td').eq(10).html(data[3].others);
					  $('#countyGovWork').children('td').eq(11).html(data[3].self);
					  $('#countyGovWork').children('td').eq(12).html(data[3].revoke);
					  $('#countyGovWork').children('td').eq(13).html(data[3].qualified);
					  
					  //乡级政府
					  $('#countryGov').children('td').eq(1).html(data[4].all);
					  $('#countryGov').children('td').eq(2).html(data[4].publish);
					  $('#countryGov').children('td').eq(3).html(data[4].review);
					  $('#countryGov').children('td').eq(4).html(data[4].record);
					  $('#countryGov').children('td').eq(5).html(data[4].deliberation);
					  $('#countryGov').children('td').eq(6).html(data[4].decUnitOop);
					  $('#countryGov').children('td').eq(7).html(data[4].decProcedureOop);
					  $('#countryGov').children('td').eq(8).html(data[4].contentOop);
					  $('#countryGov').children('td').eq(9).html(data[4].decTechHasDefects);
					  $('#countryGov').children('td').eq(10).html(data[4].others);
					  $('#countryGov').children('td').eq(11).html(data[4].self);
					  $('#countryGov').children('td').eq(12).html(data[4].revoke);
					  $('#countryGov').children('td').eq(13).html(data[4].qualified);
					  
					  //合计
					  $('#amount').children('td').eq(1).html(data[5].all);
					  $('#amount').children('td').eq(2).html(data[5].publish);
					  $('#amount').children('td').eq(3).html(data[5].review);
					  $('#amount').children('td').eq(4).html(data[5].record);
					  $('#amount').children('td').eq(5).html(data[5].deliberation);
					  $('#amount').children('td').eq(6).html(data[5].decUnitOop);
					  $('#amount').children('td').eq(7).html(data[5].decProcedureOop);
					  $('#amount').children('td').eq(8).html(data[5].contentOop);
					  $('#amount').children('td').eq(9).html(data[5].decTechHasDefects);
					  $('#amount').children('td').eq(10).html(data[5].others);
					  $('#amount').children('td').eq(11).html(data[5].self);
					  $('#amount').children('td').eq(12).html(data[5].revoke);
					  $('#amount').children('td').eq(13).html(data[5].qualified);
					  
				  },
				  error : function(data)
				  {
					  alert("查询出错!");
				  }
			});
		});
		
	});
	
</script>
</head>
<body>
		<div id="cc" class="easyui-layout  cc" >
			<table  class="table">
				<tr>
					<td class="table-search"> &nbsp;&nbsp;年份:&nbsp;
						<input id="year" class="easyui-numberspinner"   data-options="width:120, min:2010,max:2030,value:2014,editable:false"> 
						<div id="search" class="button_change red center">查询</div>
						<div id="download" class="button_change red center">下载</div>
						<form id="downloadId" action="" method="post" style="display: none" ></form>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow" class="dataDivWindow" >
				<table id="dataGrid" class="spe_table" >
					<tr>
						<td id="reportName" class="reportName" colspan="15" >
							规范性文件监督管理工作情况统计表
						</td>
					</tr>
					<tr>
						<td rowspan="2" class="td-content countItem">统计项目</td>
						<td rowspan="2" class="td-content content">规范性文件主体数量</td>
						<td rowspan="2" class="td-content content">制定发布规范性文件数量</td>
						<td rowspan="2" class="td-content content">法制机构进行合法性审核文件数量</td>
						<td rowspan="2" class="td-content content">向备案机构报备的文件数量</td>
						<td rowspan="2" class="td-content content">经备案审查发现存在问题的文件数量</td>
						<td colspan="5" class="td-content problem">规范性文件存在的问题类型</td>
						<td colspan="3" class="td-content result">处理结果</td>
						<td >备注</td>
					</tr>
					<tr class="td-content ">
						<td class="dec">制定主体不合法</td>
						<td class="dec" >制定程序不合法</td>
						<td class="dec">文件内容不合法</td>
						<td class="dec">制定技术有缺陷</td>
						<td class="dec">其他</td>
						<td class="dec">自行修正</td>
						<td class="dec">废弃撤销</td>
						<td class="dec">其他</td>
						<td></td>
					</tr>
					<tr class="td-content order">
						<td >序号</td>
						<td >1</td>
						<td >2</td>
						<td >3</td>
						<td >4</td>
						<td >5</td>
						<td >6</td>
						<td >7</td>
						<td >8</td>
						<td >9</td>
						<td >10</td>
						<td >11</td>
						<td >12</td>
						<td >13</td>
						<td >14</td>
					</tr>
					<tr id="cityGov" class="td-content">
						<td >市(州)政府</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
					<tr id="cityGovWork" class="td-content">
						<td >市(州)政府部门</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
					<tr id="countyGov" class="td-content">
						<td >县(市、区)政府</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
					<tr id="countyGovWork" class="td-content">
						<td >县(市、区)政府部门</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
					
					<tr id="countryGov"   class="td-content">
						<td >乡(镇、街道办)政府</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
					
					<tr id="amount"  class="td-content total">
						<td >合计</td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
				</table>
			</div>
		</div>
	
</body>
</html>