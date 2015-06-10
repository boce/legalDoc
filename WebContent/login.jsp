<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>登录页</title>
<link href="${pageContext.request.contextPath}/css/login/style.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/css/login/demo.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/css/login/animate-custom.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript">
<!--
if(window != top){
	top.location.href = "${pageContext.request.contextPath}/share/main.wf";
}
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
  document.getElementById("j_username").focus();
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function login()
{
	document.getElementById("login_form_").submit();
}
document.onkeypress= test;
function test(evt)
{
	var e=window.event||evt;
	if(e.keyCode==13)
	{
	 	login();
    }
}
//-->
</script>
</head>
<body onload="javascript:document.getElementById('j_username').focus();">
	
	<div class="container">
		<div id="container_demo" >
			<!-- hidden anchor to stop jump http://www.css3create.com/Astuce-Empecher-le-scroll-avec-l-utilisation-de-target#wrap4  -->
			<a class="hiddenanchor" id="toregister"></a>
			<a class="hiddenanchor" id="tologin"></a>
			<div id="wrapper">
				<div id="login" class="animate form">
					<form  name="login_form_" id="login_form_" action="<c:url value='j_spring_security_check'/>" method="post">
						<p class="sysname_wrapper">
							<label class="sysname">规范性文件管理系统</label>
						</p>
						<p>
							<label for="j_username" class="uname" data-icon="u" > 用户名 </label>
							<input id="j_username" name="j_username" required="required" type="text" placeholder="请输入用户名" value="admin"/>
						</p>
						<p>
							<label for="j_password" class="youpasswd" data-icon="p"> 密码 </label>
							<input id="j_password" name="j_password" required="required" type="password" placeholder="请输入密码" value="1"/>
						</p>
						<p class="login button">
							<input type="button" onclick="login();" value="登录" />
						</p>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
