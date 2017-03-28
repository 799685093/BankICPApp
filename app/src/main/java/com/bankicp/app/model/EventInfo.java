package com.bankicp.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventInfo implements Serializable {

	@Expose
	private long id ;

	@Expose
	private String title;

	@Expose
	private String content;

	@Expose
	private long createUserId ;
	@Expose
	private String createUserName ;
	@Expose
	private EventLog lastLog;// 最后回复
	@Expose
	private String createDate ;
	@Expose
	private int status ;
	@Expose
	private List<String> images;


	@Expose
	private List<EventLog> logs = new ArrayList<EventLog>();


	public EventInfo() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<EventLog> getLogs() {
		return logs;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public EventLog getLastLog() {
		return lastLog;
	}

	public void setLastLog(EventLog lastLog) {
		this.lastLog = lastLog;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setLogs(List<EventLog> logs) {
		this.logs = logs;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
	
	
}
