package com.cdrundle.legaldoc.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.cdrundle.security.WebPlatformUser;


public class SysUtil {
	public static final int PAGE_INDEX = 0;
	
    public static final int PAGE_SIZE = 10;
    
    public static final String EXTENSION_NAME=".doc";
    
    public static final String EXTENSION_DOCX=".docx";
    
    public static final String SEMICOLON = ";";		//分号
    
    public static final int ENTER_ASCII = 13;		//回车符ASCII码
    
    public static final int SPACE_ASCII = 32;		//空格符ASCII码
    
    public static final int TABULATION_ASCII = 9;		//水平制表符ASCII码
    
    public static final String COMMA = ",";		//逗号
    
    public static final String CHILD_NODE = "c";		//子节点ID标示
    
    public static final String LEGAL_EN = "规范性文件";		//文件路径时的“规范性文件”
    
    public static final String DATE_FORMAT = "yyyyMMdd";	//日期格式化格式
    
    public static final String DATE_FORMAT_NORMAL = "yyyy-MM-dd";	//日期格式化格式
    
    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";	//日期格式化格式
    
    public static final String FILE_PATH = "files";		//存储文件的总文件夹
    
    public static final String FILE_PATH_TEMP = "files\\temp";		//存储临时文件
    
    public static final String STAGE_LEGAL_DEVAPP = "立项";		//阶段之立项
    
    public static final String STAGE_LEGAL_DRAFT = "初稿";		//阶段之起草初稿
    
    public static final String STAGE_LEGAL_INSTRUCTION = "起草说明";		//阶段之合法性审查起草说明
    
    public static final String STAGE_LEGAL_FEEDBACK = "反馈意见";		//阶段之征求意见
    
    public static final String STAGE_LEGAL_REQUESTOPINIONLETTER="征求意见函"; //阶段之征求意见函
    
    public static final String STAGE_LEGAL_REQUESTCOMMENT = "征求意见稿";
    
    public static final String STAGE_LEGAL_ADOPTCOMMENT = "反馈意见处理情况";		//阶段之征求意见
    
    public static final String STAGE_LEGAL_REQCOMMENTREVISE = "征求意见修改稿";		//阶段之征求意见
    
    public static final String STAGE_LEGAL_EXAMDRAFTING = "送审稿";		//阶段之合法性审查
    
    public static final String STAGE_LEGAL_EXAMDRAFTING_MODIFY = "送审稿修改稿";		//阶段之合法性审查
    
    public static final String STAGE_LEGAL_PROTOCOL= "草案";		//阶段之草案
    
    public static final String STAGE_LEGAL_REVCOMMENT = "审核意见";		//阶段之合法性审查审核意见
   
    public static final String STAGE_LEGAL_REVIEWINSTRUCTION= "审议请示";		//阶段之审议
    
    public static final String STAGE_LEGAL_DELIBERATIONCOMMENT = "审议意见";		//阶段之审议
    
    public static final String STAGE_LEGAL_CONTENT = "草案修改稿";		//阶段之审议
    
    public static final String STAGE_LEGAL_RECORDREQUEST = "备案报告";		//阶段之备案
    
    public static final String STAGE_LEGAL_REVIEWOPINIONPAPER = "备案审查意见书";		//阶段之备案
    
    public static final String STAGE_LEGAL_LEGALDOC = "规范性文件内容";		//阶段之发布
    
    public static final String STAGE_LEGAL_LEGALBASIS= "相关依据";		//阶段之合法性审查
    
    public static final String JSON_MSG_SUCCESS = "{\"msg\":\"success\"}";	//JSON格式返回成功
    
    public static final String JSON_MSG_FAIL = "{\"msg\":\"fail\"}";		//JSON格式返回失败
    
    public static final String SEARCH_TYPE_NORFILE = "NORFILE";			//确认时的查询类型之规范性文件查询 
    
    public static final String SEARCH_TYPE_DRAFTSUB = "DRAFTSUB";		//确认时的查询类型之送审稿查询
    
    public static final String SEARCH_TYPE_REQUESTCOMMENT="REQUESTCOMMENT"; //确认时的查询类型之征求意见查询
    
    public static final String SEARCH_TYPE_FEEDBACKCOMMENT="FEEDBACKCOMMENT"; //确认时的查询类型之反馈意见
    
    public static final String SEARCH_TYPE_DRAFTREVIEW = "DRAFTREVIEW";		//确认时的查询类型之送审稿审查查询
    
    public static final String SEARCH_TYPE_DRAFTMODIFY = "DRAFTMODIFY";		//确认时的查询类型之送审稿修改查询
    
    public static final String SEARCH_TYPE_DELREQUEST = "DELREQUEST";		//确认时的查询类型之审议报请
    
    public static final String SEARCH_TYPE_PDEL = "PDEL";		//确认时的查询类型之草案审议
    
    public static final String SEARCH_TYPE_PMO = "PMO";		//确认时的查询类型之草案修改
    
    public static final String SEARCH_TYPE_RREQ = "RECREQUEST";		//确认时的查询类型之备案报送
    
    public static final String SEARCH_TYPE_RREW = "RECREVIEW";		//确认时的查询类型之备案审查
    
    public static final String SEARCH_TYPE_SIGNANDPUBLISH = "SIGNANDPUBLISH";		//确认时的查询类型之签署与发布
    
    public static final String SEARCH_TYPE_ADOPTCOMMENT="ADOPTCOMMENT"; //确认时的查询类型之反馈意见处理情况
    
    public static final String SEARCH_TYPE_MODIFYDRAFT="MODIFYDRAFT"; //确认时的查询类型之修改征求意见稿
    
    public static final String SEARCH_TYPE_DEVELOPAPPLICATION="DEVELOPAPPLICATION"; //确认时的查询类型之制定申请
    
    public static final String SEARCH_TYPE_DRAFT="DRAFT"; //确认时的查询类型之制定申请
    
    public static final String FILE_TYPE_DRAFT="DRAFT"; 	//文件类型-起草

    public static final String SEARCH_TYPE_DEFERRED = "DEFERRED";		//确认时的查询类型之满期评估
    
    public static final String FILE_TYPE_PROTOCOL="PROTOCOL"; 	//文件类型-草案
    
    public static final String FILE_TYPE_MODIFY ="MODIFY"; 	//文件类型-征求意见的相关材料
    
    public static final String FILE_TYPE_REVCOMMENT="REVCOMMENT"; 	//文件类型-法律审查意见书
    
    public static final String FILE_TYPE_DELIBERATIONCOMMENT="DELIBERATIONCOMMENT"; 	//文件类型-审议意见
    
    public static final String FILE_TYPE_REVIEWINSTRUCTION="REVIEWINSTRUCTION"; 	//文件类型-审议请示
    
    public static final String FILE_TYPE_INSTRUCTION="INSTRUCTION"; 	//文件类型-起草说明
    
    public static final String FILE_TYPE_ADOPTCOMMENT = "ADOPTCOMMENT";		//文件类型-征求意见的相关材料
    
    public static final String FILE_TYPE_REVIEWCOMMENT="REVIEWCOMMENT"; 	//文件类型-审核意见

    public static final String FILE_TYPE_RISK="RISK"; 	//文件类型-制定的必要性、合法性，以及社会稳定性风险评估
    
    public static final String FILE_TYPE_MAIN_PROBLEM="MAINPROBLEM"; 	//文件类型-拟解决的主要问题
    
    public static final String FILE_TYPE_PLAN_REG_MEA="PLANREGMEA"; 	//文件类型-拟确定的制度或措施，以及可行性论证
    
    public static final String FILE_TYPE_RECORDREQUEST="RECORDREQUEST"; 	//文件类型-备案报告
    
    public static final String FILE_TYPE_LEGALBASIS="LEGALBASIS"; 	//文件类型-相关依据
    
    public static final String FILE_TYPE_LEGALDOC="LEGALDOC"; 	//文件类型-规范性文件
    
    /**
     * 获取登录人员信息
     * @return
     */
    public static WebPlatformUser getLoginInfo(){
    	return (WebPlatformUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
