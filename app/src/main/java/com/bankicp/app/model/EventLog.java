package com.bankicp.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class EventLog implements Serializable {

	@Expose
	private long id;

	@Expose
	private String title;

	@Expose
	private String content;

	@Expose
	private long createUserId;
	@Expose
	private String createUserName;
	@Expose
	private String createDate;
	@Expose
	private int status;
	@Expose
	private long eventInfoId;
	@Expose
	private List<String> images;
	
	public EventLog() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getEventInfoId() {
		return eventInfoId;
	}

	public void setEventInfoId(long eventInfoId) {
		this.eventInfoId = eventInfoId;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
	
	
}
