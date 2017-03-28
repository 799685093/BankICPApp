package com.bankicp.app.model;

import java.io.Serializable;

public class FileInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String _ID = "_id";

	public static final String NAME = "name";
	public static final String SIZE = "size";
	public static final String PATH = "path";
	public static final String TYPE = "type";
	public static final String REMARKS = "remarks";
	public static final String VOICEPATH = "voicePath";
	public static final String DIRECTION = "direction";
	public static final String ISUSE = "isUse";
	public static final String TASK_ID = "task_id";
	public static final String ITEM_ID = "item_id";
	public static final String USER_ID = "user_id";

	private String id;
	private String name;// 文件名
	private String size;// 大小
	private String path;// 路径
	private int type;// 类型--2图片，6文件，-1为添加
	private String remarks;// 注备
	private String voicePath;// 语音路径
	private String direction;
	private boolean isUse = false;
	private long taskId;
	private long itemId;

	public FileInfo() {
		super();
	}

	public FileInfo(String path) {
		super();
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getVoicePath() {
		return voicePath;
	}

	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

}
