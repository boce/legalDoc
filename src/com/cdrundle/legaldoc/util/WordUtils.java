package com.cdrundle.legaldoc.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordUtils {
	// 日志
	private static final Log logger = LogFactory.getLog(WordUtils.class);
	private static int beginArray[]; // table开始点数组
	private static int endArray[]; // table结束点数组
	private static String htmlTextArray[]; // table生成html内容的数组

	/**
	 * 读取word文件当中的内容
	 * @throws ServiceException 
	 * 
	 * @filePath 需要转换的文件名称 返回 读取html格式内容
	 */

	public static String readFile(String filePath) throws ServiceException {
		// 创建文件
		File file = new File(filePath);
		String content = ""; // 返回字符串内容
		byte[] byteArray = null;
		try(FileInputStream inStream = new FileInputStream(file)) {
			byteArray = new byte[inStream.available()]; // 初始化byte数组
			inStream.read(byteArray); // 读取功能
			content = new String(byteArray, "GBK"); // 设置读取的编码为 GBK
			int frontPoint = content.indexOf("<html>");
			int lastPoint = content.indexOf("</html>");
			content = content.substring(frontPoint + 6, lastPoint); // 获取到word文档中关于文档的内容
		} catch (FileNotFoundException e) {
			logger.error(e);
			throw new ServiceException("文件不存在", e);
		} catch (IOException e) {
			logger.error(e);
			throw new ServiceException("文件读取错误，可能因为文件已经打开", e);
		}
		return content;
	}

	/**
	 * 从html代码转换成word文件内容，主要是将fckeditor编辑器中的内容转换 成word文件当中，并且格式要一样。
	 * 
	 * @filePath 文件所在位置
	 * @htmlContent html内容
	 */
	@SuppressWarnings("unused")
	public static boolean htmlToWord(String filePath, String fileName, String htmlContent) {
		boolean flag = false; // 设置执行是否完成标志
		FileOutputStream ostream = null;
		ByteArrayInputStream bais = null;
		try {
			File fileP = new File(filePath);
			if (!fileP.exists()) {
				fileP.mkdirs();
			}
			htmlContent = "<html>" + htmlContent + "</html>";
			byte b[] = htmlContent.getBytes("GBK"); // 设置编码
			bais = new ByteArrayInputStream(b);
			POIFSFileSystem fs = new POIFSFileSystem();
			DirectoryEntry directory = fs.getRoot();
			DocumentEntry de = directory.createDocument("WordDocument", bais); // 设置转换成word
			ostream = new FileOutputStream(filePath + File.separator + fileName);
			fs.writeFilesystem(ostream); // 将流写入文档中文(word)
			flag = true;
		} catch (IOException e) {
			flag = false;
			logger.error("html转换成word时IO报错！", e);
		} finally {
			try {
				if(bais != null){
					bais.close();
				}
				if(ostream != null){
					ostream.close();
				}
			} catch (IOException e) {
				logger.error("html转换成word时文件关闭报错！", e);
			}
		}
		return flag;
	}

	/**
	 * 删除word文件,用于删除相关的文档的word,如果规范性文件、审核意见书等
	 * 
	 * @filePath 文件所在位置
	 * 
	 * @return
	 */
	public static boolean deleteWord(String filePath) {
		File file = new File(filePath);
		if (file.exists()) { // 判断文件是否存在
			file.delete();
			file = null;
			return true;
		}
		return false;
	}

	/**
	 * 生成规范性文件路径
	 * 
	 * @projectPath 项目路径
	 * @norFile 规范性文件
	 * @stage 阶段
	 * 
	 */
	public static String getFilePath(String projectPath, NormativeFileVo norFile, String stage) {
//		Date date = null;
//		if (norFile.getApplyDate() != null) { // 有制定过程的用立项日期
//			date = norFile.getApplyDate();
//		} else if (norFile.getRegisterDate() != null) { // 没有制定过程的用注册日期
//			date = norFile.getRegisterDate();
//		} else {
//			return "";
//		}
//		SimpleDateFormat dateformat = new SimpleDateFormat(SysUtil.DATE_FORMAT);
//		String dateStr = dateformat.format(date);
		String filePath = projectPath + File.separator + SysUtil.FILE_PATH + File.separator + SysUtil.LEGAL_EN + File.separator /*+ dateStr*/ + norFile.getDocNo() + File.separator + stage;
		return filePath;
	}

	/**
	 * 获取指定路径下面多个文件名称,以“,”隔开
	 * 
	 * @path 文件路径
	 * @nameFilter 规范性文件
	 * @return
	 * 
	 */
	public static String getFileNames(String path, String nameFilter) {
		StringBuffer rBuffer = new StringBuffer("");
		boolean flag = false; // 是否加上";"
		File filePath = new File(path);
		File[] files = filePath.listFiles();
		if (files != null && files.length > 0) {
			for (File f : files) {
				if (f.getName().contains(nameFilter)) {
					if (flag) {
						rBuffer.append(SysUtil.SEMICOLON);
						rBuffer.append(f.getName());
					} else {
						rBuffer.append(f.getName());
						flag = true;
					}
				}
			}
		}
		return rBuffer.toString();
	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            原文件路径 如：c:/fqf
	 * @param newPath
	 *            复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static boolean copyFolder(String oldPath, String newPath) {
		boolean flag = true;
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			File file = new File(newPath);
			if (!file.exists()) {
				file.mkdirs(); // 如果文件夹不存在 则建立新文件夹
			}
			File a = new File(oldPath);
			String[] files = a.list();
			File temp = null;
			for (int i = 0; i < files.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + files[i]);
				} else {
					temp = new File(oldPath + File.separator + files[i]);
				}

				if (temp.isFile()) {
					input = new FileInputStream(temp);
					output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
				}
			}
		} catch (Exception e) {
			flag = false;
			logger.error("复制文件出错", e);
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				flag = false;
				logger.error("复制文件出错", e);
			}
		}
		return flag;
	}

	/**
	 * 读取外部word(包括doc和docx)
	 * 
	 * @filePath 文件路径
	 * @return
	 * 
	 */
	public static String readWord(String filePath) {
		String returnStr = "";
		String extendName = filePath.substring(filePath.lastIndexOf("."), filePath.length());
		if (extendName.equals(SysUtil.EXTENSION_NAME)) {
			returnStr = word2Html(filePath);
		} else if (extendName.equals(SysUtil.EXTENSION_DOCX)) {
			returnStr = wordToHtml(filePath, filePath.substring(0, filePath.lastIndexOf("."))+".html");
		}
		return returnStr;
	}

	/**
	 * 将word(doc)转换成html格式
	 * 
	 * @filePath 文件路径
	 * @return
	 * 
	 */
	private static String word2Html(String filePath) {
		FileInputStream in = null;
		HWPFDocument doc = null;
		StringBuffer html = null; // 生成的html代码
		boolean tblExist = false; // word中是否有table
		StringBuffer tempcon = new StringBuffer(""); // 临时内容
		try {
			in = new FileInputStream(new File(filePath));
			doc = new HWPFDocument(in);
			Range rangetbl = doc.getRange(); // 得到文档的读取范围
			TableIterator it = new TableIterator(rangetbl);

			int num = 100;
			beginArray = new int[num];
			endArray = new int[num];
			htmlTextArray = new String[num];
			int length = doc.characterLength(); // 取得文档中字符的总数
			html = new StringBuffer("<div>");

			if (it.hasNext()) {
				try {
					readTable(it, rangetbl);
					tblExist = true; // word中有table内容
				} catch (Exception e) {
					logger.error("读取文件出错", e);
				}
			}

			int currentNum = 0; // 遍历table编号
			for (int index = 0; index < length - 1; index++) { // 整篇文章的字符通过一个个字符的来判断,range为得到文档的范围
				Range range = new Range(index, index + 1, doc);
				CharacterRun cr = range.getCharacterRun(0);
				if (tblExist && index == beginArray[currentNum]) {
					html.append(tempcon + htmlTextArray[currentNum]);
					tempcon.setLength(0);
					index = endArray[currentNum] - 1;
					currentNum++;
					continue;
				} else {
					Range range2 = new Range(index + 1, index + 2, doc);
					CharacterRun cr2 = range2.getCharacterRun(0);
					char c = cr.text().charAt(0);

					if (c == SysUtil.ENTER_ASCII) { // 判断是否为回车符
						tempcon.append("<br/>");
					} else if (c == SysUtil.SPACE_ASCII) { // 判断是否为空格符
						tempcon.append("&nbsp;");
					} else if (c == SysUtil.TABULATION_ASCII) { // 判断是否为水平制表符
						tempcon.append(" &nbsp;&nbsp;&nbsp;");
					}

					boolean flag = compareCharStyle(cr, cr2); // 比较前后2个字符是否具有相同的格式

					if (flag) {
						tempcon.append(cr.text());
					} else {
						String fontStyle = "<span style=\"font-family:" + cr.getFontName() + ";font-size:" + cr.getFontSize() / 2 + "pt;";
						if (cr.getColor() == 6) { // 部分颜色的配置
							fontStyle += "color: red;";
						}
						if (cr.isBold()) {
							fontStyle += "font-weight:bold;";
						}
						if (cr.isItalic()) {
							fontStyle += "font-style:italic;";
						}
						// 自定义样式
						// htmlText += fontStyle + " mce_style=font-family:" +
						// cr.getFontName() + ";font-size:" + cr.getFontSize() /
						// 2 + "pt;";
						// if (cr.isBold()) {
						// fontStyle += "font-weight:bold;";
						// }
						// if (cr.isItalic()) {
						// fontStyle += "font-style:italic;";
						// }
						html.append(fontStyle + "\">" + tempcon + cr.text() + "</span>");
						tempcon.setLength(0); // 清空
					}
				}
			}

			html.append(tempcon + "</div>");
		} catch (FileNotFoundException e) {
			logger.error("读取文件出错", e);
		} catch (IOException e) {
			logger.error("读取文件出错", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("读取文件出错", e);
			}
			doc = null;
		}
		return html.toString();
	}

	/**
	 * 判断cr1和cr2的字体样式是否一样(doc)
	 * 
	 * @param cr1
	 * @param cr2
	 * @return
	 */
	private static boolean compareCharStyle(CharacterRun cr1, CharacterRun cr2) {
		if (cr1.isBold() == cr2.isBold() && cr1.isItalic() == cr2.isItalic() && cr1.getFontName().equals(cr2.getFontName())
				&& cr1.getFontSize() == cr2.getFontSize() && cr1.getColor() == cr2.getColor()) {
			return true;
		}
		return false;
	}

	/**
	 * 读写文档中的表格(doc)
	 * 
	 * @param pTable
	 * @param cr
	 * @throws Exception
	 */
	private static void readTable(TableIterator it, Range rangetbl) throws Exception {
		StringBuffer htmlContent = null;
		int counter = -1;
		while (it.hasNext()) { // 迭代文档中的表格
			htmlContent = new StringBuffer("");
			Table tb = (Table) it.next();
			int begPos = tb.getStartOffset();
			int endPos = tb.getEndOffset();
			counter = counter + 1;
			beginArray[counter] = begPos; // 迭代行，默认从0开始
			endArray[counter] = endPos;
			htmlContent.append("<table border='1' cellpadding='0' cellspacing='0' >");
			for (int i = 0; i < tb.numRows(); i++) {
				TableRow tr = tb.getRow(i);
				htmlContent.append("<tr align='center'>");

				for (int j = 0; j < tr.numCells(); j++) { // 迭代列，默认从0开始
					TableCell td = tr.getCell(j); // 取得单元格
					int cellWidth = td.getWidth();

					for (int k = 0; k < td.numParagraphs(); k++) { // 取得单元格的内容
						Paragraph para = td.getParagraph(k);
						CharacterRun crTemp = para.getCharacterRun(0);
						String fontStyle = "<span style=\"font-family:" + crTemp.getFontName() + ";font-size:" + crTemp.getFontSize() / 2 + "pt;";
						if (crTemp.getColor() == 6) { // 部分颜色的配置
							fontStyle += "color: red;";
						}
						if (crTemp.isBold()) {
							fontStyle += "font-weight:bold;";
						}
						if (crTemp.isItalic()) {
							fontStyle += "font-style:italic;";
						}

						String content = fontStyle + "\">" + para.text().toString().trim() + "</span>";
						if (StringUtils.isEmpty(content)) {
							content = " ";
						}
						htmlContent.append("<td width=" + cellWidth + ">" + content + "</td>");

					}
				}
			}
			htmlContent.append("</table>");
			htmlTextArray[counter] = htmlContent.toString();
		}
	}

	/**
	 * 读取docx
	 * 
	 * @filePath 文件路径
	 * @return String
	 * 
	 */
	public static String docx2Html(String filePath) {
		InputStream is = null;
		XWPFDocument doc = null;
		List<XWPFTable> tableList = new ArrayList<XWPFTable>();
		List<XWPFParagraph> paras = new ArrayList<XWPFParagraph>();
		StringBuffer html = new StringBuffer();
		StringBuffer tempHtml = new StringBuffer(""); // 临时内容
		try {
			is = new FileInputStream(filePath);
			doc = new XWPFDocument(is);
			tableList = doc.getTables();
			paras = doc.getParagraphs(); // 取得所有的段落
			int length = tableList.size() + paras.size();
			List<Integer> posList = new ArrayList<Integer>();
			for (XWPFTable t : tableList) {
				posList.add(doc.getPosOfTable(t));
			}
			html = new StringBuffer("<div>");
			int tablepos = 0;
			int parapos = 0;
			for (int index = 0; index < length; index++) {
				if (posList.size() > 0 && posList.contains(index)) {
					XWPFTable p = tableList.get(tablepos);
					tablepos++;
					String tableContent = "";
					try {
						tableContent = readTable(p);
					} catch (Exception e) {
						logger.error("读取docx报错！", e);
					}
					html.append(tableContent);
				} else {
					XWPFParagraph p = paras.get(parapos);
					parapos++;
					List<XWPFRun> runList = p.getRuns();
					for (int i = 0; i < runList.size(); i++) {
						XWPFRun r1 = runList.get(i);
						XWPFRun r2;
						boolean flag = true;
						if (i != runList.size() - 1) {
							r2 = runList.get(i + 1);
							flag = compareXWPFRun(r1, r2);
						} else {
							flag = false;
						}
						if (flag) {
							tempHtml.append(r1.getText(0));
						} else {
							String fontStyle = "<span style=\"font-family:" + r1.getFontFamily() + ";font-size:" + (12 + r1.getFontSize() / 2)
									+ "pt;";
							if (r1.getColor() != null) { // 部分颜色的配置
								fontStyle += "color: " + r1.getColor() + ";";
							}
							if (r1.isBold()) {
								fontStyle += "font-weight:bold;";
							}
							if (r1.isItalic()) {
								fontStyle += "font-style:italic;";
							}
							String text = r1.getText(0);
							if (text == null) {
								text = "";
							}
							html.append(fontStyle + "\">" + tempHtml + r1.getText(0) + "</span>");
							tempHtml.setLength(0); // 清空
						}
					}
					html.append("<br/>");
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("读取文件出错", e);
		} catch (IOException e) {
			logger.error("读取文件出错", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("读取文件出错", e);
			}
		}
		return html.toString();
	}

	/**
	 * 判断两个XWPFRun格式是否一样(docx)
	 * 
	 * @param cr1
	 * @param cr2
	 * @return
	 */
	private static boolean compareXWPFRun(XWPFRun cr1, XWPFRun cr2) {
		if (cr1.isBold() == cr2.isBold()
				&& cr1.isItalic() == cr2.isItalic()
				&& cr1.getFontSize() == cr2.getFontSize()
				&& ((cr1.getFontFamily() == null && cr2.getFontFamily() == null) || (cr1.getFontFamily() != null && cr2.getFontFamily() != null && cr1
						.getFontFamily().equals(cr2.getFontFamily())))
				&& ((cr1.getColor() == null && cr2.getColor() == null) || (cr1.getColor() != null && cr2.getColor() != null && cr1.getColor() == cr2
						.getColor()))) {
			return true;
		}
		return false;
	}

	/**
	 * 读写文档中的表格(docx)
	 * 
	 * @param pTable
	 * @param cr
	 * @throws Exception
	 */
	private static String readTable(XWPFTable table) throws Exception {
		StringBuffer htmlContent = null;
		htmlContent = new StringBuffer("");
		htmlContent.append("<table border='1' cellpadding='0' cellspacing='0' width=" + table.getWidth() + " >");
		for (int i = 0; i < table.getRows().size(); i++) {
			XWPFTableRow tr = table.getRow(i);
			htmlContent.append("<tr align='center' height=" + tr.getHeight() + ">");
			for (int j = 0; j < tr.getTableCells().size(); j++) { // 迭代列，默认从0开始
				XWPFTableCell td = tr.getCell(j); // 取得单元格
				for (int k = 0; k < td.getParagraphs().size(); k++) { // 取得单元格的内容
					XWPFParagraph para = td.getParagraphs().get(k);
					List<XWPFRun> runList = para.getRuns();
					StringBuffer content = new StringBuffer("");
					for (XWPFRun crTemp : runList) {
						String fontStyle = "<span style=\"font-family:" + crTemp.getFontFamily() + ";font-size:" + (12 + crTemp.getFontSize() / 2)
								+ "pt;";
						if (crTemp.getColor() != null) { // 部分颜色的配置
							fontStyle += "color: " + crTemp.getColor() + ";";
						}
						if (crTemp.isBold()) {
							fontStyle += "font-weight:bold;";
						}
						if (crTemp.isItalic()) {
							fontStyle += "font-style:italic;";
						}
						String tempContent = "";
						if (crTemp.getText(0) != null) {
							tempContent = crTemp.getText(0).toString();
						} else {
							tempContent = "  ";
						}
						content.append(fontStyle + "\">" + tempContent + "</span>");
					}
					htmlContent.append("<td width=" + 20 + ">" + content + "</td>");
				}
			}
			htmlContent.append("<tr/>");
		}
		htmlContent.append("<table/>");
		return htmlContent.toString();
	}

	/**
	 * 读取docx
	 * 
	 * @filePath 文件路径
	 * @return String
	 * 
	 */
	public static String readDocx(String filePath) {
		InputStream is = null;
		XWPFDocument doc = null;
		StringBuffer content = new StringBuffer();
		try {
			is = new FileInputStream(filePath);
			doc = new XWPFDocument(is);
			List<XWPFParagraph> paras = doc.getParagraphs();
			for (XWPFParagraph para : paras) {
				// 当前段落的属性
				content.append(para.getText());
			}
			// 获取文档中所有的表格
			List<XWPFTable> tables = doc.getTables();
			List<XWPFTableRow> rows;
			List<XWPFTableCell> cells;
			for (XWPFTable table : tables) {
				// 获取表格对应的行
				rows = table.getRows();
				for (XWPFTableRow row : rows) {
					// 获取行对应的单元格
					cells = row.getTableCells();
					for (XWPFTableCell cell : cells) {
						content.append(cell.getText());
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("读取文件错误！", e);
		} catch (IOException e) {
			logger.error("读取文件错误！", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error("读取文件错误！", e);
			}
		}

		return content.toString();
	}

	/**
	 * 获取html文件代码
	 * 
	 * @param filepath 文档的保存位置
	 * @return 转换成功的html代码
	 * @throws IOException
	 */
	public static String toHtmlString(String filepath) {
		// 获取html文件流
		StringBuffer htmlSb = new StringBuffer();
		File htmlFile = new File(filepath);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(htmlFile)))) {
			while (br.ready()) {
				htmlSb.append(br.readLine());
			}
			br.close();
			// 删除临时文件
			htmlFile.delete();
		} catch (FileNotFoundException e) {
			logger.error("文件没找到！", e);
		} catch (IOException e) {
			logger.error("文件读取错误！", e);
		}
		return htmlSb.toString();
	}

	/**
	 * 
	 * @param inPath
	 * @param toPath
	 * @return
	 */
	public static String wordToHtml(String inPath, String toPath) {

		// 启动word
		ActiveXComponent axc = new ActiveXComponent("Word.Application");

		try {
			// 设置word不可见
			axc.setProperty("Visible", new Variant(false));

			Dispatch docs = axc.getProperty("Documents").toDispatch();

			// 打开word文档
			Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method, new Object[] { inPath, new Variant(false), new Variant(true) }, new int[1])
					.toDispatch();

			// 作为html格式保存到临时文件
			Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { toPath, new Variant(8) }, new int[1]);

			Variant f = new Variant(false);
			Dispatch.call(doc, "Close", f);

		} catch (Exception e) {
			logger.error("word转换成html时报错！", e);
		} finally {
			axc.invoke("Quit", new Variant[] {});
		}
		return toHtmlString(toPath);
	}

}
