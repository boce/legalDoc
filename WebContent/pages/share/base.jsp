<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%
	String contextPath = request.getContextPath();
%>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/js/jquery/css/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/js/jquery/css/icon.css" />
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/css/base.css"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath %>/js/ueditor/themes/default/css/ueditor.css">
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/easyuiSupplement/css-table.css"/>
<link rel="stylesheet" type="text/css" href="<%=contextPath%>/css/easyuiSupplement/roundbutton.css"/>
<script  src="<%=contextPath %>/js/jquery/jquery-1.11.1.min.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/jquery/jquery.easyui.min.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/jquery/ajaxfileupload.js" type="text/javascript" ></script>
<script  src="<%=contextPath %>/js/jquery/local/easyui-lang-zh_CN.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/lodop/LodopFuncs.js" type="text/javascript" ></script>
<script  src="<%=contextPath %>/js/legaldoc/base.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/ueditor/ueditor.config.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/ueditor/ueditor.all.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/ueditor/ueditor.parse.js" type="text/javascript"></script>
<script  src="<%=contextPath %>/js/util.js" type="text/javascript"></script>


<script>
var contextPath = "<%=contextPath%>";
//树节点类型 org表示组织机构，district表示区域
var orgNodeType = ["org", "district"];

var autoMaticallyAdaptResolutionOfHeight = function(objectId){
	$('#'+objectId).css('height', parent.window.allHeight-5);
	/* var dataDivWindowHeight = window.screen.height;
	if (dataDivWindowHeight ===600 || dataDivWindowHeight < 600) {
		$('#'+objectId).css('height','225px');
	}
	if (dataDivWindowHeight > 600 && dataDivWindowHeight <= 800 ) {
		$('#'+objectId).css('height','460px');
	}
	if (dataDivWindowHeight >800) {
		$('#'+objectId).css('height','587px');
	} */
};
</script>