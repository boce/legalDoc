package com.cdrundle.legaldoc.webservice;

import java.io.UnsupportedEncodingException;

import javax.jws.WebService;

import com.cdrundle.legaldoc.vo.RecordRequestVo;
import com.cdrundle.legaldoc.webservice.pojo.Attachment;

/**
 * 备案报送
 * @author xiaokui.li
 *
 */
@WebService(targetNamespace="http://webservice.legaldoc.cdrundle.com/")
public interface IRecordSend {
	public boolean saveRecord(RecordRequestVo recordRequestVo, Attachment atta) throws UnsupportedEncodingException;
}
