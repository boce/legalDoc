package com.cdrundle.legaldoc.enums;

import java.beans.PropertyEditorSupport;

public class StageEnumEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		boolean found = false;
		for (Stage d : Stage.values()) {
			if (text.equals(d.name())) {
				this.setValue(d);
				found = true;
				break;
			}
		}
		if (found == false) 
		{
			this.setValue(null);
		}
	}
}