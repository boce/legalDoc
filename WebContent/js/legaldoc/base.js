$(document).ready(function(){
	$(".top .div1 .updatepwd").mouseover(function(){
		  $(this).css("color","#FF9900");
		});
	$(".top .div1  .updatepwd").mouseout(function(){
		  $(this).css("color","black");
	});
	$(".top .div1 .exit").mouseover(function(){
		  $(this).css("color","#FF9900");
		});
	$(".top .div1  .exit").mouseout(function(){
		  $(this).css("color","black");
	});
	/**
	 * 设置未来(全局)的AJAX请求默认选项
	 * 主要设置了AJAX请求遇到Session过期的情况
	 */
	$.ajaxSetup({
	    complete: function(xhr,status) {
	        var responseText = xhr.responseText;
	        if(responseText.indexOf("j_spring_security_check") != -1) {
	        	var top = getTopWinow();
	        	top.location.href = contextPath + "/share/main.wf";
	        }
	    }
	});
});

/**
 * 在页面中任何嵌套层次的窗口中获取顶层窗口
 * @return 当前页面的顶层窗口对象
 */
function getTopWinow(){
    var p = window;
    while(p != p.parent){
        p = p.parent;
    }
    return p;
}

//显示按钮
function displayBtn(){
	var pageSources = window.parent.pageSourceAuth[window.parent.currentPage];
	var btns = $("#btnTools div");
	if(typeof(pageSources) != "undefined" && pageSources.length != 0){
		for(var i=0,blen=btns.length; i<blen; i++){
			var btn = btns[i];
			var isShow = false;
			for(var j=0,plen=pageSources.length; j<plen; j++){
				if(btn.id == pageSources[j].code){
					isShow = true;
					break;
				}
			}
			if(!isShow){
				$(btn).remove();
			}
		}
	}else{
		btns.remove();
	}
}

//显示隐藏
function showDiv(id, isDisplay){
	if(id == null || id == ""){
		return;
	}
	if(isDisplay){
		$("#" + id).show();
	}else{
		$("#" + id).hide();
	}
}