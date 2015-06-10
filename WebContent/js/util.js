//消息提示
function showMsg(msg, title){
	if(typeof(title) == "undefined"){
		title = "消息提示";
	}
	$.messager.show({
		title:title,
		msg:msg,
		showType:'slide',
		height:130,
		style:{
			right:0,
			top:'',
			bottom:0,
			left:''
		}
	});
}
/**
 * 使用lodop打印,宽度为A4大小
 * data:html字符串
 */
function lodopPrint(data){
	data = "<div style=\"width:649px;\">" + data + "</div>";
	var LODOP = getLodop();
	LODOP.ADD_PRINT_HTM("25.4mm",
			"19.1mm",
			"RightMargin:19.1mm",
			"BottomMargin:25.4mm",
			data);
	LODOP.PREVIEW();
}