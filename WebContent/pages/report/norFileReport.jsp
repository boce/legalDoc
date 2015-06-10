<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>规范性文件查询</title>
<%@include file="/pages/share/base.jsp"%>
<script type="text/javascript">

	var choseArr = new Array();
	var nameArr = new Array();
	var searchArr = new Array();
	var searchNameArr = new Array();
	
	var colChoseArr = new Array();
	var colNameArr = new Array();
	var exportArr = new Array();
	var exportNameArr = new Array();
	
	var idArrs = new Array();
	var condtionArrs = new Array();
	var valueArrs = new Array();
	choseArr = ['name','decUnit','publishNo','status'];	//查询默认选中属性
	nameArr = ['文件名称','制定单位','发文号','状态'];
	colChoseArr = ['name','decUnit','publishNo','status'];	//栏目默认选中属性
	colNameArr = ['文件名称','制定单位','发文号','状态'];
	searchArr = ['name','decUnit','publishNo','status'];	//默认查询属性
	searchNameArr = ['文件名称','制定单位','发文号','状态'];
	exportArr = ['name','decUnit','publishNo','status'];	//默认栏目属性(导出)
	exportNameArr = ['文件名称','制定单位','发文号','状态'];
	
	
	var fields = {
			"name":{"id":"name","name":"文件名称","type":"string", "compType":"text"},
			"applyUnit":{"id":"applyUnit","name":"申请单位","type":"int", "compType":"combotree"},
			"decUnit":{"id":"decUnit","name":"制定单位","type":"int", "compType":"combotree"},
			"decUnitLeader":{"id":"decUnitLeader","name":"制定单位负责人","type":"int", "compType":"combobox"},
			"decUnitClerk":{"id":"decUnitClerk","name":"制定单位经办员","type":"int", "compType":"combobox"},
			"drtUnit":{"id":"drtUnit","name":"主起草单位","type":"int", "compType":"combotree"},
			"drtUnitLeader":{"id":"drtUnitLeader","name":"主起草单位负责人","type":"int", "compType":"combobox"},
			"drtUnitClerk":{"id":"drtUnitClerk","name":"主起草单位经办员","type":"int", "compType":"combobox"},
			"unionDrtUnit":{"id":"unionDrtUnit","name":"联合起草单位","type":"string", "compType":"text"},
			"unionDrtUnitLeader":{"id":"unionDrtUnitLeader","name":"联合起草单位负责人","type":"string", "compType":"text"},
			"unionDrtUnitClerk":{"id":"unionDrtUnitClerk","name":"联合起草单位经办员","type":"string", "compType":"text"},
			"revUnit":{"id":"revUnit","name":"审查单位","type":"int", "compType":"combotree"},
			"revUnitLeader":{"id":"revUnitLeader","name":"审查单位负责人","type":"int", "compType":"combobox"},
			"revUnitClerk":{"id":"revUnitClerk","name":"审查单位经办员","type":"int", "compType":"combobox"},
			"involvedOrges":{"id":"involvedOrges","name":"涉及部门","type":"string", "compType":"text"},
			"delUnit":{"id":"delUnit","name":"审议部门","type":"string", "compType":"text"},
			"recRevUnit":{"id":"recRevUnit","name":"备案审查单位","type":"int", "compType":"combotree"},
			"recRevUnitLeader":{"id":"recRevUnitLeader","name":"备案审查单位负责人","type":"int", "compType":"combobox"},
			"registerCode":{"id":"registerCode","name":"备案号","type":"string", "compType":"text"},
			"applyDate":{"id":"applyDate","name":"申请日期","type":"string", "compType":"date"},
			"draftDate":{"id":"draftDate","name":"起草日期","type":"string", "compType":"date"},
			"requestDate":{"id":"requestDate","name":"报请日期","type":"string", "compType":"date"},
			"delDate":{"id":"delDate","name":"审议日期","type":"string", "compType":"date"},
			"publishDate":{"id":"publishDate","name":"发布日期","type":"string", "compType":"date"},
			"docNo":{"id":"docNo","name":"单据号","type":"string", "compType":"text"},
			"reviewDate":{"id":"reviewDate","name":"备案审查日期","type":"string", "compType":"date"},
			"registerDate":{"id":"registerDate","name":"备案日期","type":"string", "compType":"date"},
			"invalidDate":{"id":"invalidDate","name":"失效日期","type":"string", "compType":"date"},
			"validDate":{"id":"validDate","name":"有效期","type":"int", "compType":"text"},
			"status":{"id":"status","name":"状态","type":"enum", "compType":"combobox"},
			"stage":{"id":"stage","name":"阶段","type":"enum", "compType":"combobox"},
			"publishNo":{"id":"publishNo","name":"发文号","type":"string", "compType":"text"},
			"legalDoc":{"id":"legalDoc","name":"规范性文件","type":"string", "compType":"text"},
			"draftInstruction":{"id":"draftInstruction","name":"起草说明","type":"string", "compType":"text"},
			"legalBasis":{"id":"legalBasis","name":"相关依据","type":"string", "compType":"text"}
		};
	
	$(function() {
		//显示的页面按钮
		displayBtn();
		autoMaticallyAdaptResolutionOfHeight('dataDivWindow');
		$('.layout-panel').css('position','absolute');
		$('#scheme').click(function() {
			$('#searchScheme').window('open');
		});
		
		//打开查询选择window
		$('#attrBtn').click(function() {
			$('#norFileAttr').window('open');
		});
		
		//打开栏目选择window
		$('#columnBtn').click(function() {
			$('#columnAttr').window('open');
		});
		
		//初始化两个选择window
		selectRows($('#chooseGrid'), choseArr);
		selectRows($('#colChooseGrid'), exportArr);
		addRows($('#selectedGrid'), choseArr, nameArr);
		addRows($('#colSelectedGrid'), exportArr, exportNameArr);
		
		//初始化查询条件
		createAllField(fields, $('#searchTable')); //生成所有的组件
		for(var f in fields){
			createCompenont(fields[f]);	//创建editor
		}
		$.each(searchArr, function(index){	//显示默认查询条件
			showCompenont(fields[searchArr[index]]);
		});
		
		//初始化栏目列表
		createColumn(exportArr, exportNameArr, $('#columnTable'));
		
		
		
		//添加属性(查询)
		$('#addBtn').click(function() {
			var chrows = $('#chooseGrid').datagrid('getSelections');
			if (chrows != null && chrows.length > 0) {	//返回数组不为空
				choseArr = [];	
				nameArr = [];
				$.each(chrows, function(index){	 //重新获取选中值
					var id = chrows[index].id;
					var name = chrows[index].name;
					choseArr.push(id);
					nameArr.push(name);
				}); 
				$('#selectedGrid').datagrid('loadData',{total:0,rows:[]});
				addRows($('#selectedGrid'), choseArr, nameArr);
			} else { //没有选中任何行,已选择属性表清空
				$('#selectedGrid').datagrid('loadData',{total:0,rows:[]});
			}
			
		});
		
		//去除属性(查询)
		$('#removeBtn').click(function() {
			var chrows = $('#selectedGrid').datagrid('getSelections');
			if (chrows != null && chrows.length > 0) {	//返回数组不为空
				choseArr = [];	
				nameArr = [];
				$.each(chrows, function(index){	 //重新获取选中值
					var id = chrows[index].id;
					var name = chrows[index].name;
					choseArr.push(id);
					nameArr.push(name);
				}); 
				$('#selectedGrid').datagrid('loadData',{total:0,rows:[]});
				addRows($('#selectedGrid'), choseArr, nameArr);
				$('#chooseGrid').datagrid('unselectAll');
				selectRows($('#chooseGrid'), choseArr);
			} else { 
				$('#selectedGrid').datagrid('loadData',{total:0,rows:[]});
				$('#chooseGrid').datagrid('unselectAll');	//取消所有的选中行
			}
		});
		
		//属性确认(查询)
		$('#attrConfirm').click(function() {
			searchArr = [];
			searchNameArr = [];
			searchArr = choseArr;	//确认时向查询数组赋值
			searchNameArr = nameArr;
			hideAllCompenont(fields);
			$.each(searchArr, function(index){	//重新显示查询条件
				showCompenont(fields[searchArr[index]]);
			});
			$('#norFileAttr').window('close');
		});
		
		//属性取消(查询)
		$('#attrCancel').click(function() {
			$('#norFileAttr').window('close');
		});
		
		
		//添加属性(栏目)
		$('#colAddBtn').click(function() {
			var chrows = $('#colChooseGrid').datagrid('getSelections');
			if (chrows != null && chrows.length > 0) {	//返回数组不为空
				colChoseArr = [];	
				colNameArr = [];
				$.each(chrows, function(index){	 //重新获取选中值
					var id = chrows[index].id;
					var name = chrows[index].name;
					colChoseArr.push(id);
					colNameArr.push(name);
				}); 
				$('#colSelectedGrid').datagrid('loadData',{total:0,rows:[]});
				addRows($('#colSelectedGrid'), colChoseArr, colNameArr);
			} else { //没有选中任何行,已选择属性表清空
				$('#colSelectedGrid').datagrid('loadData',{total:0,rows:[]});
			}
			
		});
		
		//去除属性(栏目)
		$('#colRemoveBtn').click(function() {
			var chrows = $('#colSelectedGrid').datagrid('getSelections');
			if (chrows != null && chrows.length > 0) {	//返回数组不为空
				colChoseArr = [];	
				colNameArr = [];
				$.each(chrows, function(index){	 //重新获取选中值
					var id = chrows[index].id;
					var name = chrows[index].name;
					colChoseArr.push(id);
					colNameArr.push(name);
				}); 
				$('#colSelectedGrid').datagrid('loadData',{total:0,rows:[]});
				addRows($('#colSelectedGrid'), colChoseArr, colNameArr);
				$('#colChooseGrid').datagrid('unselectAll');
				selectRows($('#colChooseGrid'), colChoseArr);
			} else { 
				$('#colSelectedGrid').datagrid('loadData',{total:0,rows:[]});
				$('#colChooseGrid').datagrid('unselectAll');	//取消所有的选中行
			}
		});
		
		//属性确认(栏目)
		$('#colAttrConfirm').click(function() {
			exportArr = [];
			exportNameArr = [];
			exportArr = colChoseArr;	//确认时向栏目数组赋值
			exportNameArr = colNameArr;
			deleteAllTr($('#columnTable'));	//删除原来表中tr
			createColumn(exportArr, exportNameArr, $('#columnTable')); //重新显示查询条件
			$('#columnAttr').window('close');
		});
		
		//属性取消(栏目)
		$('#colAttrCancel').click(function() {
			$('#columnAttr').window('close');
		});
		
		
		//查询确认
		$('#searchConfirm').click(function() {
			//获取显示字段的id和value
			idArrs = [];
			condtionArrs = [];
			valueArrs = [];
			for(var field in fields){
				if (!$('#query' + field).is(':hidden')) {
					var cond = $("#condtion" + field).val();
					var val = '';
					if (fields[field].compType == 'text') {
						val = $('#value' + field).val();
					} else if (fields[field].compType == 'combotree') {	
						val = $('#value' + field).combotree('getValue');
						
					} else if (fields[field].compType == 'combobox') {	
						val = $('#value' + field).combobox('getValue');
						
					} else if (fields[field].compType == 'date') {
						val = $('#value' + field).datebox('getValue'); 
					} 
					if (val != null && val != '') {
						condtionArrs.push(cond);
						idArrs.push(field);
						valueArrs.push(val);
					}
				}
			}
			
			createColumns($('#dataGrid'), '${pageContext.request.contextPath}/report/getNorFiles.do', idArrs, 
					condtionArrs, valueArrs, exportNameArr, exportArr);	//查询结果表中的字段变化有exportArr决定

			$('#searchScheme').window('close');	//关闭查询窗口
		});
		
		
		//导出
		$('#download').click(function() {
			var page = $('#dataGrid').datagrid('options').pageNumber;
			var rows = $('#dataGrid').datagrid('options').pageSize;
			$("#downloadId").attr("action", "${pageContext.request.contextPath}/report/download.do?nameList=" + idArrs.toString() +
					"&idList=" + exportArr.toString() + "&condList=" + condtionArrs.toString() + "&valueList=" + valueArrs.toString() +
					"&labelList=" + exportNameArr.toString() + "&page=" + page + "&rows=" + rows);
			$("#downloadId").submit();
		});
		
		//查询取消
		$('#searchCancel').click(function() {
			$('#searchScheme').window('close');
		});
			
	});
	
	//选择grid中指定的行，arrs为存放行id的数组	
	function selectRows(grid, arrs) {
		var rows = grid.datagrid('getRows');
		if (arrs != null) {
			for (var i = 0; i < arrs.length; i++) {
				for (var rl = 0; rl < rows.length; rl++) {
					if (arrs[i] == rows[rl].id) {
						var rowIndex = grid.datagrid('getRowIndex', rows[rl]);
						grid.datagrid('selectRow', rowIndex);
					}
				}
			}
		}
		
	}
	
	//填充表
	function addRows(grid, arrs, narrs) {
		if (arrs != null && arrs.length > 0) {
			$.each(arrs, function(index){	
				var id = arrs[index];
				grid.datagrid('insertRow',{
					index: index,
					row: {
						name: narrs[index],
						id: id
					}
				});
				grid.datagrid('selectRow', index);
			});
		}
	}
	
	//查询初始化生成所有input
	function createAllField(fields, table) {
		for(var field in fields){	
			var str = "";
			str += "<tr id='query" + field + "' style='display:none'>" +
						"<td style='width: 120px; height: 24px;  text-align: center'>" + fields[field].name + "</td>" +
						"<td style='width: 60px; height: 24px; text-align: center '>" +
						"<select id='condtion" + field + "'  style='width:60px;'>" +  
							    "<option value='et'>等于</option>";
			if(field == 'validDate' || fields[field].type == 'date'){
				str += "<option value='gt'>大于</option>" +  
			    	   "<option value='lt'>小于</option>"; 
			}			 
							    
			if(fields[field].type == 'string'){
				str += "<option value='ct'>包含</option>"; 
			}
							   
			str += "</select>" +
				"</td>" +
				"<td style='width: 280px; height: 24px;  text-align: left;'>" +
					"<input type='text' id='value" + field + "'/>" +
				"</td>" +
			"</tr>";
			table.append(str);
		};
	}
	
	//初始化栏目列表
	function createColumn(cols, colsName, table) {
		$.each(cols, function(index){	//显示默认查询条件
			var str = "";
			str += "<tr>" +
						"<td style='height: 24px; text-align: center;'>" + index + "</td>" +
						"<td style='height: 24px; text-align: center;'>" + colsName[index] + "</td>" +
					"</tr>";
			table.append(str);
		});
		
	}
	
	//创建组件
	function createCompenont(field) {
		if (field.compType == 'combotree') {	//查询数的生成
			$('#value' + field.id).combotree({   
				valueField:'id',
				textField:'name',
				url:'${pageContext.request.contextPath}/org/getOrgShortReference.do',
				onBeforeSelect : function(node){
		        	if (node.attributes.nodeType == 'district') {
		        		alert('只能选择部门!');
		        		return false; 
		        	}
	            }
			});
			
		} else if (field.compType == 'combobox' && field.id != 'status' && field.id != 'stage') {	//查询box的生成
			$('#value' + field.id).combobox({   
				valueField:'id',
				textField:'name',
				url:'${pageContext.request.contextPath}/user/getUserByOrg.do'  
			});
			
		} else if (field.compType == 'combobox' && field.id == 'status') {
			$('#value' + field.id).combobox({   
				valueField:'name',
				textField:'title',
				url:'${pageContext.request.contextPath}/report/getStatus.do'  
			});
			
		} else if (field.compType == 'combobox' && field.id == 'stage') {
			$('#value' + field.id).combobox({   
				valueField:'name',
				textField:'title',
				url:'${pageContext.request.contextPath}/report/getStage.do'  
			});
			
		} else if (field.compType == 'date') {
			$('#value' + field.id).datebox({   
			    required : false  
			}); 
		}
	}
	
	function showCompenont(field) {
		$('#query' + field.id).show();
	}
	
	function hideAllCompenont(fields) {
		for(var field in fields){
			$('#query' + field).hide();
		}
	}
	
	//删除table中所有的tr
	function deleteAllTr(table) {
		table.find("tr").remove(); 
	}
	
	//动态生成
	function createColumns(grid, url, idArray, conditionArray, valueArray, searchNameArray, searchArray) {
		var s = "";  
	    s = "[[";  
	    s = s + "]]";  
		options={};  
	    options.url = url;  
	    options.queryParams = {  
	    	nameList : idArray.toString(), 
	    	condList : conditionArray.toString(), 
			valueList : valueArray.toString(),
			labelList : searchNameArray.toString(),
			idList : searchArray.toString() 
	    };  
	    options.columns = eval(s);
	    //增加查询数组中的列
	    $.each(searchArray, function(index){	//重新显示查询条件
			showCompenont(fields[searchArray[index]]);
	    	if (searchArray[index] == 'name') {
	    		options.columns[0].push({  
		            field:'name',title:'文件名称',width:200, halign:'center' 
		    	});
	    	} else if (searchArray[index] == 'name') {
	    		options.columns[0].push({  
	    			field:'applyUnit', title:'申请单位', halign:'center', formatter:function(value,row){
                        return row.applyUnit.text;
                    }
		    	});
	    	} else if (searchArray[index] == 'decUnit') {
	    		options.columns[0].push({  
		            field:'decUnit',title:'制定单位',width:60, halign:'center', formatter:function(value,row){
                        return row.decUnit.text;
                    } 
		    	});
	    	} else if (searchArray[index] == 'decUnitClerk') {
	    		options.columns[0].push({  
	    			field:'decUnitClerk', title: '制定单位经办员', halign:'center', formatter:function(value,row){
                        return row.decUnitClerk.name;
                    }
		    	});
	    	} else if (searchArray[index] == 'decUnitLeader') {
	    		options.columns[0].push({  
	    			field:'decUnitLeader', title:'制定单位负责人', halign:'center', formatter:function(value,row){
                        return row.decUnitLeader.name;
                    }
		    	});
	    	} else if (searchArray[index] == 'drtUnit') {
	    		options.columns[0].push({  
	    			field:'drtUnit', halign:'center', title:'主起草单位', formatter:function(value,row){
                        return row.drtUnit.text;
                    }
		    	});
	    	} else if (searchArray[index] == 'drtUnitClerk') {
	    		options.columns[0].push({  
	    			field:'drtUnitClerk',halign:'center', title:'起草单位经办员' ,formatter:function(value,row){
	                    return row.drtUnitClerk.name;
	                } 
		    	});
	    	} else if (searchArray[index] == 'drtUnitLeader') {
	    		options.columns[0].push({  
	    			field:'drtUnitLeader',halign:'center', title:'起草单位负责人' ,formatter:function(value,row){
	                    return row.drtUnitLeader.name;
	                }
		    	});
	    	} else if (searchArray[index] == 'unionDrtUnit') {
	    		options.columns[0].push({  
	    			field:'unionDrtUnitName',halign:'center', title:'联合起草单位'
		    	});
	    	} else if (searchArray[index] == 'unionDrtUnitClerk') {
	    		options.columns[0].push({  
	    			field:'unionDrtUnitClerkName',halign:'center', title:'联合起草单位经办员' 
		    	});
	    	} else if (searchArray[index] == 'unionDrtUnitLeader') {
	    		options.columns[0].push({  
	    			field:'unionDrtUnitLeaderName',halign:'center', title:'联合起草单位负责人' 
		    	});
	    	} else if (searchArray[index] == 'revUnit') {
	    		options.columns[0].push({  
	    			 field:'revUnit', halign:'center', title:'审查单位', formatter:function(value,row){
	    	                return row.revUnit.text;
	    	            } 
		    	});
	    	} else if (searchArray[index] == 'revUnitClerk') {
	    		options.columns[0].push({  
	    			field:'revUnitClerk', halign:'center', title:'审查单位经办员', formatter:function(value,row){
	                    return row.revUnitClerk.name;
	                } 
		    	});
	    	} else if (searchArray[index] == 'revUnitLeader') {
	    		options.columns[0].push({  
	    			field:'revUnitLeader', halign:'center', title:'审查单位负责人', formatter:function(value,row){
	                    return row.revUnitLeader.name;
	                }
		    	});
	    	} else if (searchArray[index] == 'involvedOrges') {
	    		options.columns[0].push({  
	    			field:'involvedOrgesName', halign:'center', title:'涉及部门' 
		    	});
	    	} else if (searchArray[index] == 'delUnit') {
	    		options.columns[0].push({  
	    			field:'delUnit',halign:'center', title:'审议单位' 
		    	});
	    	} else if (searchArray[index] == 'recRevUnit') {
	    		options.columns[0].push({  
	    			field:'recRevUnit', halign:'center', title:'备案审查单位', formatter:function(value,row){
	                    return row.recRevUnit.text;
	                }
		    	});
	    	} else if (searchArray[index] == 'recRevUnitClerk') {
	    		options.columns[0].push({  
	    			field:'recRevUnitClerk', halign:'center', title:'备案审查单位经办员', formatter:function(value,row){
	                    return row.recRevUnitClerk.name;
	                }
		    	});
	    	} else if (searchArray[index] == 'recRevUnitLeader') {
	    		options.columns[0].push({  
	    			field:'recRevUnitLeader', halign:'center', title:'备案审查单位负责人', formatter:function(value,row){
	                    return row.recRevUnitLeader.name;
	                }
		    	});
	    	} else if (searchArray[index] == 'applyDate') {
	    		options.columns[0].push({  
	    			field:'applyDate', halign:'center', title:'申请日期' 
		    	});
	    	} else if (searchArray[index] == 'draftDate') {
	    		options.columns[0].push({  
	    			field:'draftDate',halign:'center', title:'起草日期' 
		    	});
	    	} else if (searchArray[index] == 'requestDate') {
	    		options.columns[0].push({  
	    			field:'requestDate',halign:'center', title:'报请日期' 
		    	});
	    	} else if (searchArray[index] == 'delDate') {
	    		options.columns[0].push({  
	    			field:'delDate',halign:'center', title:'审议日期'
		    	});
	    	} else if (searchArray[index] == 'publishDate') {
	    		options.columns[0].push({  
	    			field:'publishDate',halign:'center', title:'发布日期' 
		    	});
	    	} else if (searchArray[index] == 'docNo') {
	    		options.columns[0].push({  
	    			field:'docNo',halign:'center', title:'单据号'
		    	});
	    	} else if (searchArray[index] == 'reviewDate') {
	    		options.columns[0].push({  
	    			field:'reviewDate',halign:'center', title:'备案审查日期'
	    		});
	    	} else if (searchArray[index] == 'registerDate') {
	    		options.columns[0].push({  
	    			field:'registerDate',halign:'center', title:'备案日期' 
		    	});
	    	} else if (searchArray[index] == 'invalidDate') {
	    		options.columns[0].push({  
	    			field:'invalidDate',halign:'center', title:'失效日期' 
		    	});
	    	} else if (searchArray[index] == 'validDate') {
	    		options.columns[0].push({  
	    			field:'validDate',halign:'center', titlt:'有效期'
		    	});
	    	} else if (searchArray[index] == 'publishNo') {
	    		options.columns[0].push({  
	    			field:'publishNo',halign:'center', title:'发文号'
	    		});
	    	} else if (searchArray[index] == 'status') {
	    		options.columns[0].push({  
	    			field:'status',halign:'center', title:'状态', formatter:function(value,row){
	    				if(value == 'VALID')
	                    	return '生效';
	                    if(value == 'INVALID')
	                    	return '失效';
	                    if(value == 'MODIFY')
	                    	return '修订';
	                    if(value == 'REVOKE')
	                    	return '撤销';
	                    if(value == 'ABOLISH')
	                    	return '废止';
	                }
		    	});
	    	} else if (searchArray[index] == 'stage') {
	    		options.columns[0].push({  
	    			field:'stage',halign:'center', title:'阶段', formatter:function(value,row){
	    				if(value == 'SETUP')
	                    	return '立项';
	                    if(value == 'DRAFTING')
	                    	return '起草';
	                    if(value == 'REQUEST_COMMENT')
	                    	return '意见征求';
	                    if(value == 'LEGAL_REVIEW')
	                    	return '合法性审查';
	                    if(value == 'DELIBERATION')
	                    	return '审议';
	                    if(value == 'PUBLISH')
	                    	return '发布';
	                    if(value == 'RECORD')
	                    	return '备案';
	                }
		    	});
	    	} else if (searchArray[index] == 'legalDoc') {
	    		options.columns[0].push({  
	    			field:'legalDoc',halign:'center', title:'规范性文件'
		    	});
	    	} else if (searchArray[index] == 'draftInstruction') {
	    		options.columns[0].push({  
	    			field:'draftInstruction',halign:'center', title:'起草原因'
		    	});
	    	} else if (searchArray[index] == 'legalBasis') {
	    		options.columns[0].push({  
	    			field:'legalBasis',halign:'center', title:'相关依据', formatter:function(value,row){
	    				if(value != ""){
	    					return value.replace(/.docx|.doc/g, "");
	    				}
	                }
		    	});
	    	} 
           
		});
	   
	    grid.datagrid(options);
	}
	
</script>
</head>
<body>
	<div data-options="region:'center', split:true">
		<div id="cc" class="easyui-layout" style="width: 100%;">
			<table style="height: 30px;" class="spe_table">
				<tr>
					<td id="btnTools" style="text-align: left;">
						<div id="scheme" class="button_change red center">查询方案</div>
						<!-- <input id="attr" type="button" value="条件属性" /> -->
						<div id="download" class="button_change red center">下载</div>
						<form id="downloadId" action="" method="post" style="display: none" ></form>
					</td>
				</tr>
			</table>
			<div id="dataDivWindow" style="height:500px;">
				<table id="dataGrid" class="easyui-datagrid" data-options="singleSelect:true,pagination:true,rownumbers:true,height:324,border:0">
					<thead>
						<tr>
							<th data-options="field:'name',halign:'center'">文件名称</th>
							<th data-options="field:'decUnit', halign:'center', formatter:function(value,row){
	                            return row.decUnit.text;
	                        }">制定单位</th>
	                        <th data-options="field:'publishNo',halign:'center' ">发文号</th>
							<th data-options="field:'status',halign:'center', formatter:function(value,row){
								if(value == 'VALID')
			                    	return '生效';
			                    if(value == 'INVALID')
			                    	return '失效';
			                    if(value == 'MODIFY')
			                    	return '修订';
			                    if(value == 'REVOKE')
			                    	return '撤销';
			                    if(value == 'ABOLISH')
			                    	return '废止';
		                        }">状态</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
	
	<!-- 查询方案页面 -->
	<div id="searchScheme" title="查询方案" class="easyui-window" data-options=
		"collapsible:false, minimizable:false, maximizable:false, closed:true"
		style="width: 500px; height: 400px; padding: 0px;">
		<div style="overflow:auto; " class="easyui-tabs">
			<div title="条件" style="height: 300px; overflow:auto; ">
				<div style="height: 300px; overflow:auto; ">
					<table id="searchTable" class="spe_table">
						<tr>
							<th style="text-align: center; width: 50%">条件名称</th>
							<th style="text-align: center; width: 5%">查询关系</th>
							<th style="text-align: center; width: 45%">条件内容</th>
						</tr>
					</table>
				</div>
			</div>
			<div title="栏目" style="height: 330px; overflow:auto; ">
				<div style="height: 300px; overflow:auto; ">
					<table id="columnTable" class="spe_table">
						<tr>
							<th style="text-align: center; width: 20%">序号</th>
							<th style="text-align: center; width: 70%">属性名称</th>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div>
			<table class="search-button">
				<tr>
					<td width="120px"></td>
					<td><input id="attrBtn" type="button" value="条件选择" /></td>
					<td><input id="columnBtn" type="button" value="栏目选择" /></td>
					<td><input id="searchConfirm" type="button" value="确认" /></td>
					<td><input id="searchCancel" type="button" value="取消" /></td>
				</tr>
			</table>
		</div>
	</div>
	
	
	<!-- 查询属性选择 -->
	<div id="norFileAttr" title="查询条件选择" class="easyui-window" data-options=
		"collapsible:false, minimizable:false, maximizable:false, closed:true"
		style="height: 600px; height:400px; padding: 0px;">
		<div class="easyui-layout" style="width:560px;height:363px;">  
		    <div data-options="region:'east', collapsible:false" style="width:250px; height:300px;">
		    	<table id="selectedGrid" title="已选择属性" class="easyui-datagrid" data-options="width:250,height:330,border:0">
					<thead>
						<tr>
							<th data-options="field:'id', halign:'center', checkbox:true"></th>
							<th data-options="field:'name', halign:'center',width:204">属性名称</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		    </div>  
		    <div data-options="region:'west', collapsible:false" style="width:250px; height:300px;">
		    	<table id="chooseGrid" title="所有属性" class="easyui-datagrid" data-options="width:250,height:330,border:0">
					<thead>
						<tr>
							<th data-options="field:'id', halign:'center', checkbox:true"></th>
							<th data-options="field:'name', halign:'center',width:204">属性名称</th>
						</tr>
					</thead>
					<tbody>
						<tr><td>name</td><td>文件名称</td></tr>
						<tr><td>applyUnit</td><td>申请单位</td></tr>
						<tr><td>decUnit</td><td>制定单位</td></tr>
						<tr><td>decUnitLeader</td><td>制定单位负责人</td></tr>
						<tr><td>decUnitClerk</td><td>制定单位经办员</td></tr>
						<tr><td>drtUnit</td><td>主起草单位</td></tr>
						<tr><td>drtUnitLeader</td><td>主起草单位负责人</td></tr>
						<tr><td>drtUnitClerk</td><td>主起草单位经办员</td></tr>
						<tr><td>unionDrtUnit</td><td>联合起草单位</td></tr>
						<tr><td>unionDrtUnitLeader</td><td>联合起草单位负责人</td></tr>
						<tr><td>unionDrtUnitClerk</td><td>联合起草单位经办员</td></tr>
						<tr><td>revUnit</td><td>审查单位</td></tr>
						<tr><td>revUnitLeader</td><td>审查单位负责人</td></tr>
						<tr><td>revUnitClerk</td><td>审查单位经办员</td></tr>
						<tr><td>involvedOrges</td><td>涉及部门</td></tr>
						<tr><td>delUnit</td><td>审议单位</td></tr>
						<tr><td>recRevUnit</td><td>备案审查单位</td></tr>
						<tr><td>recRevUnitLeader</td><td>备案审查单位负责人</td></tr>
						<tr><td>registerCode</td><td>备案号</td></tr>
						<tr><td>applyDate</td><td>申请日期</td></tr>
						<tr><td>draftDate</td><td>起草日期</td></tr>
						<tr><td>requestDate</td><td>报请日期</td></tr>
						<tr><td>delDate</td><td>审议日期</td></tr>
						<tr><td>publishDate</td><td>发布日期</td></tr>
						<tr><td>docNo</td><td>单据号</td></tr>
						<tr><td>reviewDate</td><td>备案审查日期</td></tr>
						<tr><td>registerDate</td><td>备案日期</td></tr>
						<tr><td>invalidDate</td><td>失效日期</td></tr>
						<tr><td>validDate</td><td>有效期</td></tr>
						<tr><td>status</td><td>状态</td></tr>
						<tr><td>stage</td><td>阶段</td></tr>
						<tr><td>publishNo</td><td>发文号</td></tr>
						<tr><td>legalDoc</td><td>规范性文件</td></tr>
						<tr><td>draftInstruction</td><td>起草说明</td></tr>
						<tr><td>legalBasis</td><td>相关依据</td></tr>
					</tbody>
				</table>
		    </div>  
		    <div data-options="region:'center', collapsible:false" style="width:50px; text-align:center; 
		    	vertical-align:middle; padding: 0px; margin: 0px">
				<input id="addBtn" type="button" value="&gt;&gt;" />
				<input id="removeBtn" type="button" value="&lt;&lt;" />			    	
		    </div>  
		    <div data-options="region:'south', collapsible:false">
		    	<table class="search-button">
					<tr style="text-align: center;">
						<td style="width: 220px"></td>
						<td><input id="attrConfirm" type="button" value="确认" /></td>
						<td><input id="attrCancel" type="button" value="取消" /></td>
					</tr>
				</table>
		    </div>  
		</div> 
	</div>
	
	
	<!-- 栏目选择 -->
	<div id="columnAttr" title="栏目选择" class="easyui-window" data-options=
		"collapsible:false, minimizable:false, maximizable:false, closed:true"
		style="height: 600px; height:400px; padding: 0px;">
		<div class="easyui-layout" style="width:560px;height:363px;">  
		    <div data-options="region:'east', collapsible:false" style="width:250px; height:300px;">
		    	<table id="colSelectedGrid" title="已选择属性" class="easyui-datagrid" data-options="width:250,height:330,border:0"
		    		style="padding: 0px; margin: 0px">
					<thead>
						<tr>
							<th data-options="field:'id', halign:'center', checkbox:true"></th>
							<th data-options="field:'name', halign:'center',width:204">属性名称</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		    </div>  
		    <div data-options="region:'west', collapsible:false" style="width:250px; height:300px;">
		    	<table id="colChooseGrid" title="所有属性" class="easyui-datagrid" data-options="width:250,height:330,border:0"
		    		style="padding: 0px; margin: 0px">
					<thead>
						<tr>
							<th data-options="field:'id', halign:'center', checkbox:true"></th>
							<th data-options="field:'name', halign:'center',width:204">属性名称</th>
						</tr>
					</thead>
					<tbody>
						<tr><td>name</td><td>文件名称</td></tr>
						<tr><td>applyUnit</td><td>申请单位</td></tr>
						<tr><td>decUnit</td><td>制定单位</td></tr>
						<tr><td>decUnitLeader</td><td>制定单位负责人</td></tr>
						<tr><td>decUnitClerk</td><td>制定单位经办员</td></tr>
						<tr><td>drtUnit</td><td>主起草单位</td></tr>
						<tr><td>drtUnitLeader</td><td>主起草单位负责人</td></tr>
						<tr><td>drtUnitClerk</td><td>主起草单位经办员</td></tr>
						<tr><td>unionDrtUnit</td><td>联合起草单位</td></tr>
						<tr><td>unionDrtUnitLeader</td><td>联合起草单位负责人</td></tr>
						<tr><td>unionDrtUnitClerk</td><td>联合起草单位经办员</td></tr>
						<tr><td>revUnit</td><td>审查单位</td></tr>
						<tr><td>revUnitLeader</td><td>审查单位负责人</td></tr>
						<tr><td>revUnitClerk</td><td>审查单位经办员</td></tr>
						<tr><td>involvedOrges</td><td>涉及部门</td></tr>
						<tr><td>delUnit</td><td>审议单位</td></tr>
						<tr><td>recRevUnit</td><td>备案审查单位</td></tr>
						<tr><td>recRevUnitLeader</td><td>备案审查单位负责人</td></tr>
						<tr><td>registerCode</td><td>备案号</td></tr>
						<tr><td>applyDate</td><td>申请日期</td></tr>
						<tr><td>draftDate</td><td>起草日期</td></tr>
						<tr><td>requestDate</td><td>报请日期</td></tr>
						<tr><td>delDate</td><td>审议日期</td></tr>
						<tr><td>publishDate</td><td>发布日期</td></tr>
						<tr><td>docNo</td><td>单据号</td></tr>
						<tr><td>reviewDate</td><td>备案审查日期</td></tr>
						<tr><td>registerDate</td><td>备案日期</td></tr>
						<tr><td>invalidDate</td><td>失效日期</td></tr>
						<tr><td>validDate</td><td>有效期</td></tr>
						<tr><td>status</td><td>状态</td></tr>
						<tr><td>stage</td><td>阶段</td></tr>
						<tr><td>publishNo</td><td>发文号</td></tr>
						<tr><td>legalDoc</td><td>规范性文件</td></tr>
						<tr><td>draftInstruction</td><td>起草说明</td></tr>
						<tr><td>legalBasis</td><td>相关依据</td></tr>
					</tbody>
				</table>
		    </div>  
		    <div data-options="region:'center', collapsible:false" style="width:50px; text-align:center; 
		    	vertical-align:middle; padding: 0px; margin: 0px">
				<input id="colAddBtn" type="button" value="&gt;&gt;" />
				<input id="colRemoveBtn" type="button" value="&lt;&lt;" />			    	
		    </div>  
		    <div data-options="region:'south', collapsible:false">
		    	<table class="search-button">
					<tr style="text-align: center;">
						<td style="width: 220px"></td>
						<td><input id="colAttrConfirm" type="button" value="确认" /></td>
						<td><input id="colAttrCancel" type="button" value="取消" /></td>
					</tr>
				</table>
		    </div>  
		</div> 
	</div>

</body>
</html>