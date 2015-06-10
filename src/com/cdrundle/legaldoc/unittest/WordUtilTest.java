package com.cdrundle.legaldoc.unittest;

import junit.framework.TestCase;

import org.junit.Test;

import com.cdrundle.legaldoc.util.WordUtils;

/**
 * 
 * @author gang.li
 * IDoc功能测试类
 */
public class WordUtilTest extends TestCase {
	
	@Test
	public void testCreat(){
//		String path = "D:\\documentTest\\document\\start\\test1.doc";
//		String content = "<html>" + "<head>你好<p><span class='Bold'>标题11</span></p>  <p><span class='Bold'>4</span></p>  <p><span class='Bold'>4</span></p></head>" + "<body>" + "</body>"
//				+ "</html>";
//		boolean flag = wdp.htmlToWord(path, content);
//		System.out.println(flag);
	}
	
	@Test
	public void testGetFileNames(){
		String path = "D:\\wildfly\\wildfly-8.0.0.Final\\standalone\\deployments\\legalDoc.war\\files\\规范性文件\\20140723002\\合法性审查";
		String filter = "审核意见";
		String outStr = WordUtils.getFileNames(path, filter);
		System.out.println(outStr);
	}
	
	@Test
	public void testRead(){
//		String path = "D:\\test1.doc";
//		wdp.readFile(path);
//		System.out.println(wdp.readFile(path));
	}
	
	@Test
	public void testDocx2Html(){
		String path = "D:\\test.docx";
		WordUtils.docx2Html(path);
	}
}
