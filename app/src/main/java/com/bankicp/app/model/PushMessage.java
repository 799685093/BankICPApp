package com.bankicp.app.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class PushMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	@Expose
	private String userId;
	@Expose
	private String nickName;
	@Expose
	private long timeSamp;
	@Expose
	private String description;
	@Expose
	private String title;
	@Expose
	private long areaId;

	// public String getUserId()
	// {
	// return userId;
	// }
	//
	// public void setUserId(String userId)
	// {
	// this.userId = userId;
	// }
	//
	// public String getChannelId()
	// {
	// return channelId;
	// }
	//
	// public void setChannelId(String channelId)
	// {
	// this.channelId = channelId;
	// }

	// public long getTimeSamp()
	// {
	// return timeSamp;
	// }
	//
	// public void setTimeSamp(long timeSamp)
	// {
	// this.timeSamp = timeSamp;
	// }

	public PushMessage() {
		super();
	}

	public PushMessage(String userId, String nickName, long timeSamp,
			String description, String title, long areaId) {
		super();
		this.userId = userId;
		this.nickName = nickName;
		this.timeSamp = timeSamp;
		this.description = description;
		this.title = title;
		this.areaId = areaId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getTimeSamp() {
		return timeSamp;
	}

	public void setTimeSamp(long timeSamp) {
		this.timeSamp = timeSamp;
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	//
	// public String getWorld()
	// {
	// return world;
	// }
	//
	// public void setWorld(String world)
	// {
	// this.world = world;
	// }

}
