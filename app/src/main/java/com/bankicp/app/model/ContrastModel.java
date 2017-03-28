package com.bankicp.app.model;

import java.io.Serializable;

public class ContrastModel implements Serializable {

	private FileInfo beforeFileInfo;
	private FileInfo afterFileInfo;

	public ContrastModel() {
		super();
	}

	public FileInfo getBeforeFileInfo() {
		return beforeFileInfo;
	}

	public void setBeforeFileInfo(FileInfo beforeFileInfo) {
		this.beforeFileInfo = beforeFileInfo;
	}

	public FileInfo getAfterFileInfo() {
		return afterFileInfo;
	}

	public void setAfterFileInfo(FileInfo afterFileInfo) {
		this.afterFileInfo = afterFileInfo;
	}

}
