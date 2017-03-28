package com.bankicp.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FileFolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<FileInfo> fileInfos;

	public FileFolder(String name, ArrayList<FileInfo> fileInfos) {
		super();
		this.name = name;
		this.fileInfos = fileInfos;
	}

	public FileFolder() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<FileInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(ArrayList<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

}
