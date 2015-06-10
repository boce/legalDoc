<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="/pages/share/base.jsp"%>
<title>说明</title>
<style type="text/css">
	.layout-panel{
		position: absolute;
	}
</style>
<script  type="text/javascript">
$(function(){
$('#tt').tree({
	onClick: function(node){
		if(node.text == '立项'){
			$("#content").prop("src","<%=contextPath %>/pages/help/developApplication.html");
		}
		if(node.text == '起草'){
			$("#content").prop("src","<%=contextPath %>/pages/help/draft.html");
		}
		if(node.text == '征求意见'){
			$("#content").prop("src","<%=contextPath %>/pages/help/requestComment.html");
		}
		if(node.text == '反馈意见'){
			$("#content").prop("src","<%=contextPath %>/pages/help/feedbackComment.html");
		}
		if(node.text == '反馈意见处理情况'){
			$("#content").prop("src","<%=contextPath %>/pages/help/adoptComment.html");
		}
		if(node.text == '修改《征求意见稿》'){
			$("#content").prop("src","<%=contextPath %>/pages/help/modifyDraft.html");
		}
		if(node.text == '送审稿报送'){
			$("#content").prop("src","<%=contextPath %>/pages/help/examDraftSubmit.html");
		}
		if(node.text == '送审稿审查'){
			$("#content").prop("src","<%=contextPath %>/pages/help/examDraftReview.html");
		}
		if(node.text == '送审稿修改'){
			$("#content").prop("src","<%=contextPath %>/pages/help/examDraftModify.html");
		}
		if(node.text == '审议报请'){
			$("#content").prop("src","<%=contextPath %>/pages/help/deliberationRequest.html");
		}
		if(node.text == '草案审议'){
			$("#content").prop("src","<%=contextPath %>/pages/help/protocolDeliberation.html");
		}
		if(node.text == '草案修改'){
			$("#content").prop("src","<%=contextPath %>/pages/help/protocolModify.html");
		}
		if(node.text == '签署与发布'){
			$("#content").prop("src","<%=contextPath %>/pages/help/signAndPublish.html");
		}
		if(node.text == '备案报送'){
			$("#content").prop("src","<%=contextPath %>/pages/help/recordRequest.html");
		}
		if(node.text == '备案审查'){
			$("#content").prop("src","<%=contextPath %>/pages/help/recordReview.html");
		}
	}
});
});



</script>
</head>
<body>
<div class="easyui-layout" style="width:998px;height:680px;">
		<div data-options="region:'west'" title="说明手册" style="width:200px;">
				<div class="easyui-panel" style="padding:5px; border: 0px;">
		<ul class="easyui-tree" data-options="state:'closed'" id="tt">
			<li >
				<span>帮助</span>
				<ul>
					<li >
						<span>立&nbsp;项</span>
						<ul>
							<li >
								<span >立项</span>
							</li>
						</ul>
					</li>
					<li>
						<span>起&nbsp;草</span>
						<ul>
							<li>起草</li>
						</ul>
					</li>
					<li>
						<span>意见征求</span>
						<ul>
							<li>征求意见</li>
						</ul>
						<ul>
							<li>反馈意见</li>
						</ul>
						<ul>
							<li>反馈意见处理情况</li>
						</ul>
						<ul>
							<li>修改《征求意见稿》</li>
						</ul>
					</li>
					<li>
						<span>合法性审查</span>
						<ul>
							<li>送审稿报送</li>
						</ul>
						<ul>
							<li>送审稿审查</li>
						</ul>
						<ul>
							<li>送审稿修改</li>
						</ul>
					</li>
					<li>
						<span>审议</span>
						<ul>
							<li>审议报请</li>
						</ul>
						<ul>
							<li>草案审议</li>
						</ul>
						<ul>
							<li>草案修改</li>
						</ul>
					</li>
					<li>
						<span>签署发布</span>
						<ul>
							<li>签署与发布</li>
						</ul>
					</li>
					<li>
						<span>备案</span>
						<ul>
							<li>备案报送</li>
						</ul>
						<ul>
							<li>备案审查</li>
						</ul>
					</li>
				</ul>
			</li>
		</ul>
	</div>
				
		</div>
		<div  data-options="region:'center',title:'帮助内容'" >
			<iframe id="content" style="width: 100%;height: 100%; border: 0px;" ></iframe>
		</div>
	</div>
</body>
</html>