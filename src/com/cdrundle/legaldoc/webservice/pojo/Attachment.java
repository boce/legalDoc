package com.cdrundle.legaldoc.webservice.pojo;

import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 附件传输
 * @author xiaokui.li
 *
 */
@XmlRootElement(name="Attachment")  
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlType(name="Attachment")  
public class Attachment {
	
	/**
	 * 规范性文件
	 */
	@XmlMimeType("application/octet-stream")  
    private DataHandler legalDoc;
	
	/**
	 * 备案报告
	 */
	@XmlMimeType("application/octet-stream")  
    private DataHandler recordReport;
	
	/**
	 * 起草说明
	 */
	@XmlMimeType("application/octet-stream")  
    private DataHandler draftingInstruction;
	
	/**
	 * 相关依据
	 */
	@XmlMimeType("application/octet-stream")  
    private List<DataHandler> legalBasis;

	public DataHandler getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(DataHandler legalDoc) {
		this.legalDoc = legalDoc;
	}

	public DataHandler getRecordReport() {
		return recordReport;
	}

	public void setRecordReport(DataHandler recordReport) {
		this.recordReport = recordReport;
	}

	public DataHandler getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(DataHandler draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public List<DataHandler> getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(List<DataHandler> legalBasis) {
		this.legalBasis = legalBasis;
	}
	
}
