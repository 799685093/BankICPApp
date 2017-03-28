package com.bankicp.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 时间段
 * @author yzw
 *
 */
public class TimeGroupInfo implements Serializable {

	@Expose
	private long id;
	@Expose
	private String startTime;
	@Expose
	private String endTime;
	// 机房列表
	@Expose
	private List<RoomInfo> roomList = new ArrayList<RoomInfo>();

	public TimeGroupInfo() {
		super();
	}

	public TimeGroupInfo(String startTime, String endTime,
			List<RoomInfo> roomList) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.roomList = roomList;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<RoomInfo> getRoomList() {
		return roomList;
	}

	public void setRoomList(List<RoomInfo> roomList) {
		this.roomList = roomList;
	}

	

}
