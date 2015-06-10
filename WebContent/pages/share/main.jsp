<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>	
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/easyuiSupplement/Menu_files/spe_menu_style/style.css"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/legaldoc/main.css"/>
<title>内江市规范性文件管理系统</title>
<script type="text/javascript">

	//extend the 'equals' rule     
	$.extend($.fn.validatebox.defaults.rules, {   
	    equals: {   
	        validator: function(value,param){   
	            return value == $(param[0]).val();   
	        },   
	        message: '两次输入密码不一致'  
	    }   
	});
	//存储页面对应资源
	var pageSourceAuth = {};
	//当前页面索引
	var currentPage;
	var allHeight;//详情页table外div高度
	$(document).ready(function(){
		allHeight =  document.documentElement.clientHeight - 180;
		$('#t').tabs({    
		    height:document.documentElement.clientHeight - 122,
		    onSelect:function(title, index){    
		    	var pp = $('#t').tabs('getSelected');
		    	currentPage = pp.panel('options').pageIndex;
		    }
		});

		sayHello();
		addTab('home', "首页","<%=contextPath%>/home/index.do", "t");
		$.ajax({
			type : "post",
			url : "${pageContext.request.contextPath}/home/displayMenu.do",
			dataType : "json",
			success : function(data) {
				if(data){
					$("#spe_menu_style").append("<li class='topfirst'>"+"<a style='height:30px;line-height:30px;' onclick=\"addTab('home','首页','${pageContext.request.contextPath}"+"/home/index.do"+"','t')\">"+"首页"+"</a>"+"</li>");
					for(var i=0,len=data.length; i<len; i++){
						var menuStr = "";
						var pageStr = "";
						var menu = data[i];
						//menuStr += "<a id=\"mb"+i+"\" href=\"#\">" + menu.name + "</a>";
						menuStr += "<li class='topmenu'><a href='#' style='height:30px;line-height:30px;'><span><img src='${pageContext.request.contextPath}/css/easyuiSupplement/Menu_files/spe_menu_style/arrow43.png'>"+menu.name+"</img></span></a>";
						var pages = menu.pages;
						if(pages != null && pages.length != 0){
							//pageStr += "<div id=\"mm"+i+"\" style=\"width: 150px;\">";
							pageStr += "<ul>";
							for(var j=0,plen=pages.length; j<plen; j++){
								if (j === 0) {
									pageStr += "<li class='subfirst'>";
								} else {
									pageStr += "<li>";
								}
								var page = pages[j];
								pageStr += "<a onclick=\"addTab('page"+i+j+"','"+page.name+"','${pageContext.request.contextPath}"+page.url+"','t')\">"+page.name+"</a>";
								pageStr += "</li>";
								var pageSources = page.pageSources;
								if(pageSources != null && pageSources.length != 0){
									pageSourceAuth["page"+i+j] = pageSources;
								}
							}
							pageStr += "</ul>";
							pageStr += "</li>";
						} else {
							pageStr += "</li>";
						}
						
						$("#spe_menu_style").append(menuStr+pageStr);
						
					}
					$("#spe_menu_style").append("<li class='topmenu_camouflage_menu' id='topmenu_camouflage_menu'><p id='camouflage' style='width:341px;height:30px;line-height:30px;'><span></span></p></li>");
				}
			},
			error : function(data) {
				showMsg("加载出错,请重试!");
			}
		});
		
		$("#updatepwdId").click(function(){
			$("#updatePswDiv").dialog("open");
		});
		
		$("#updatePswBtn").click(function(){
			$('#updatePswForm').form('submit', {   
			    url:"${pageContext.request.contextPath}/user/modifyPasswordForUser.do",
			    onSubmit: function(){   
			    	var isValid = $(this).form('validate');
					return isValid;
			    },   
			    success:function(data){   
			        var dataJson = eval("(" + data + ")");
			        if(dataJson.success)
			        {
			        	$('#updatePswForm').form('clear');
			        	$('#updatePswDiv').dialog('close');
			        	showMsg("修改成功！");
			        }else{
			        	if(dataJson.msg){
			        		showMsg(dataJson.msg);
			        	}else{
			        		showMsg("修改失败！");
			        	}
			        }
			    },
			    error:function(){
			    	showMsg("修改失败！");
			    }
			});
		});
		
		$("#updatePswCloseBtn").click(function(){
			$("#updatePswDiv").dialog("close");
		});
	});
	
	function sayHello() {
		var myDate = new Date(<%=java.util.Calendar.getInstance().getTimeInMillis()%>);
		var myHour = myDate.getHours();
		if (myHour < 6)
			welcomeString = "凌晨好";
		else if (myHour < 9)
			welcomeString = "早上好";
		else if (myHour < 12)
			welcomeString = "上午好";
		else if (myHour < 14)
			welcomeString = "中午好";
		else if (myHour < 17)
			welcomeString = "下午好";
		else if (myHour < 19)
			welcomeString = "傍晚好";
		else
			welcomeString = "夜里好";

		var arrayDay = [ "日", "一", "二", "三", "四", "五", "六" ];

		document.getElementById("dateZone").innerHTML = myDate.getFullYear()
				+ "年" + (myDate.getMonth() + 1) + "月" + myDate.getDate()
				+ "日   " + "星期" + arrayDay[myDate.getDay()];
	}
	
	function addTab(pageIndex, title, href, id) {
		currentPage = pageIndex;
		var content;
		if (id == undefined) {
			id = "tabs";
		}
		var tt = $('#' + id);
		if (tt.tabs('exists', title)) {//如果tab已经存在,则选中并刷新该tab          
			tt.tabs('select', title);
			refreshTab({
				tabTitle : title,
				url : href,
				id : id
			});
		} else {
			if (href) {
				content = '<iframe scrolling="no" frameborder="0" src="' + href
						+ '" style="width:100%;height:100%;" ></iframe>';
			} else {
				content = '未实现';
			}
			tt.tabs('add', {
				title : title,
				closable : true,
				content : content,
				pageIndex : pageIndex
			});
			return tt.tabs("getTab", title);
		}
	}
	
	function refreshTab(cfg) {
		var refresh_tab = cfg.tabTitle ? $('#' + cfg.id).tabs('getTab',
				cfg.tabTitle) : $('#' + cfg.id).tabs('getSelected');
		if (refresh_tab && refresh_tab.find('iframe').length > 0) {
			var _refresh_ifram = refresh_tab.find('iframe')[0];
			var refresh_url = cfg.url ? cfg.url : _refresh_ifram.src;
			_refresh_ifram.contentWindow.location.href = refresh_url;
		}
	}
	
</script>
</head>
<body>
	<div class="easyui-layout pageHead">
		<div data-options="region:'north',border:false,width: 1200, height: 120" >
			<div class="top pageHead-top" >
				<div  class="pageHead-top-content" >
						<div class="pageHead-top-content-div1">
							<label>今天是：</label>
							<label id="dateZone"></label>
							<label>欢迎您：</label>
							<label>
								<sec:authentication property="principal.realName" />
							</label>
							<a id="updatepwdId" class="updatepwd">修改密码</a>
							<a href="<%=contextPath%>/j_spring_security_logout"><label class="exit">退出</label></a>
							<a class="exit" onclick="window.open('<%=contextPath%>/help/main.wf')" >帮助</a>
						</div>
				</div>
			</div>
			<div class="topmenu_shadow pageHead-top-content-div2">
				<ul class="topmenu  pageHead-top-content-ul" id="spe_menu_style" >
				</ul>
			</div>
		</div>
		
		<div  class="pageContent" >
			<div id="contentDiv" data-options="region:'center',border:'false', width: 1200">
				<div id="t" class="easyui-tabs" style="margin: 0 auto;" ></div>
			</div>
		</div>
<!-- 		<div style="width: 1200px; height:20px; text-align: right; margin:0 auto"> -->
<!-- 			<H1 style="font-family: sans-serif, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif; font-size: 1.2em; font-weight: 1px;margin: 0px auto;color: rgba(0, 0, 0, 0.6);"> 内江规范性文件管理系统</H1> -->
<!-- 		</div> -->
	</div>
	<!-- 修改密码弹出层 -->
	<div id="updatePswDiv" class="easyui-dialog" title="修改密码" style="width:300px;height:200px;"   
	        data-options="iconCls:'icon-modify',buttons:'#dlg-buttons',resizable:false,closed:true">
	    <form id="updatePswForm" method="post">
	    	<table cellpadding="5">
	    		<tr>
	    			<td>当前密码:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="password" id="oldPassword" name="oldPassword" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>新密码:</td>
	    			<td>
	    				<input class="easyui-validatebox textbox" type="password" id="newPassword" name="newPassword" data-options="required:true"/>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>确认密码:</td>
	    			<td>
	    				<input class="easyui-validatebox  textbox" type="password" id="rpwd" name="rpwd" data-options="required:true" validType="equals['#newPassword']" />
	    			</td>
	    		</tr>
	    	</table>
	    </form>
	    <div id="dlg-buttons" class="dialog-button">
			<a id="updatePswBtn" href="javascript:void(0)" class="easyui-linkbutton">确认</a>
			<a id="updatePswCloseBtn" href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
		</div>
	</div>
</body>
</html>