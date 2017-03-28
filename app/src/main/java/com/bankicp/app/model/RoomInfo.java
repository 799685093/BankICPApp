package com.bankicp.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 机房
 * 
 */
public class RoomInfo implements Serializable {
	public static final String _ID = "id";
	public static final String KEY = "key";
	public static final String JSON_DATA = "json_data";
	public static final String CREATE_TIME = "create_time";
	public static final String USER_ID = "user_id";
	@Expose
	private long id;
	@Expose
	private String name;
	@Expose
	private String no;
	@Expose
	private String createDate;
	@Expose
	private long storeyId;
	@Expose
	private String storeyName;
	@Expose
	private String storeyNo;
	@Expose
	private List<TaskInfo> taskList = new ArrayList<TaskInfo>();

	public RoomInfo() {
		// TODO Auto-generated constructor stub
	}

	public RoomInfo(String name) {
		super();
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<TaskInfo> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<TaskInfo> taskList) {
		this.taskList = taskList;
	}

	public long getStoreyId() {
		return storeyId;
	}

	public void setStoreyId(long storeyId) {
		this.storeyId = storeyId;
	}

	public String getStoreyName() {
		return storeyName;
	}

	public void setStoreyName(String storeyName) {
		this.storeyName = storeyName;
	}

	public String getStoreyNo() {
		return storeyNo;
	}

	public void setStoreyNo(String storeyNo) {
		this.storeyNo = storeyNo;
	}


}
