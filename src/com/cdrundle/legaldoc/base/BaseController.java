package com.cdrundle.legaldoc.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.util.WordUtils;


public class BaseController
{
	private final Log	logger	= LogFactory.getLog(getClass());
	/**
	 * 输出文本字符串
	 * 
	 * @param res
	 * @param msg
	 */
	protected void outWrite(HttpServletResponse res, String msg) {
		outWrite(res, msg, "text/xml");
	}

	/**
	 * 输出xml
	 * 
	 * @param res
	 * @param xml
	 */
	protected void outWriteXml(HttpServletResponse res, String xml) {
		outWrite(res, xml, "application/xml");
	}

	/**
	 * 输出JSON
	 * 
	 * @param res
	 * @param json
	 */
	protected void outWriteJSON(HttpServletResponse res, String json) {
		outWrite(res, json, "application/json");
	}
	/**
	 * 返回流写入信息，并可以指定内容类型
	 * 
	 * @param res 返回流
	 * @param msg 内容
	 * @param contentType 内容类型
	 */
	protected void outWrite(HttpServletResponse res, String msg, String contentType) {
		res.setCharacterEncoding("utf-8");
		res.setContentType(contentType);
		PrintWriter out = null;
		try {
			out = res.getWriter();
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			logger.error(e);
		}
		finally
		{
			if(out != null)
			{
				out.close();
			}
		}
	}
	
	/**
	 * 实现传入内容转化成word文件
	 * @filePath  		文件路径
	 * @htmlContent		 编辑器中的内容
	 * 
	 * 返回是否转换成功boolean
	 */
	public boolean create(String filePath, String fileName, String htmlContent) {
		return WordUtils.htmlToWord(filePath, fileName, htmlContent);
	}
	
	/**
	 * 实现传入内容转化成word文件
	 * @throws ServiceException 
	 * @filePath 文件路径
	 * 
	 * 返回word中html内容
	 */
	public String read(String filePath) throws ServiceException {
		return WordUtils.readFile(filePath);
	}
	
	
	/**
	 * 文件下载功能
	 * @param response
	 * @filePath 文件路径
	 * @downloadName 下载文件名称
	 * @return 是否下载成功
	 */
	@SuppressWarnings("static-access")
	public boolean download(HttpServletResponse response, String filePath, String downloadName) {
		boolean downloadFlag = true;	//是否下载成功的标志
		ServletOutputStream out = null;
		InputStream inStream = null;
		try
        {
	       response.reset();
	       response.setContentType("APPLICATION/OCTET-STREAM");

	       downloadName = response.encodeURL(new String(downloadName.getBytes(),"ISO8859_1"));//要显示到客户端的文件名转码是必需的，特别是中文名，  否则可能出现文件名乱码甚至是浏览器显示无法下载的问题
	       response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
	      
	       out = response.getOutputStream();
	       inStream = new FileInputStream(filePath);
	        //循环取出流中的数据
	       byte[] b = new byte[1024];
	       int len;
	       while((len = inStream.read(b)) >0)
	    	   out.write(b,0,len);
	      
	       response.setStatus( response.SC_OK );
		   response.flushBuffer();	//清除缓存
	       out.close();
	       inStream.close();
        }
        catch (Exception e) {
           downloadFlag = false;
           logger.error("文件下载报错！", e);
        } finally {
        	try {
				out.close();
				inStream.close();
			} catch (IOException e) {
				logger.error("文件下载流关闭时报错！", e);
			}
        }
		return downloadFlag;
		
	}
	
	/**
	 * 文件上传功能
	 * @param request
	 * @filePath 文件路径
	 * @return 是否上传成功
	 */
	public boolean upload(HttpServletRequest request, String filePath, String fileName) {
		boolean uploadFlag = true;	//是否上传成功的标志
		//强制把request转换成MultipartHttpServletRequest
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
		//获取到名称为file名称的上传文件的名称
	    MultipartFile file = multipartRequest.getFile("file");
	    File path = new File(filePath);
	    if (!path.exists()) {	//路径是否存在
	    	path.mkdirs();
	    }
	    File uploadFile = new File(filePath + File.separator + fileName);
	    try {
	    	//实现上传功能
			file.transferTo(uploadFile);
			
		} catch (IllegalStateException e) {
			uploadFlag = false;
            logger.error("文件上传报错！", e);
		} catch (IOException e) {
			uploadFlag = false;
            logger.error("文件上传报错！", e);
		}  
		return uploadFlag;
	}
	
	/**
	 * 获取项目根路径
	 * @param request
	 * @return
	 */
	public String getProjectPath(HttpServletRequest request){
		return request.getServletContext().getRealPath("/");
	}
}
