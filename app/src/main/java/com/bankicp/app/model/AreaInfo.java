package com.bankicp.app.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

public class AreaInfo implements Serializable {

	@Expose
	long areaId;
	@Expose
	String name;
	List<SimpleUser> userlist;

	public AreaInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SimpleUser> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<SimpleUser> userlist) {
		this.userlist = userlist;
	}

	public String getUserNames() {
		StringBuilder builder = new StringBuilder();
		for (SimpleUser user : userlist) {
			if (user.getRoleID() == 30) {
				builder.append(user.getUserName());
				builder.append(" ");
			}
		}

		return builder.toString();

	}

	public String getCGNames() {
		StringBuilder builder = new StringBuilder();
		for (SimpleUser user : userlist) {
			if (user.getRoleID() == 25) {
				builder.append(user.getUserName());
				builder.append(" ");
			}
		}

		return builder.toString();
	}

	public String getUserIDs() {
		StringBuilder builder = new StringBuilder();
		for (SimpleUser user : userlist) {
			builder.append(user.getUserID());
			builder.append(",");
		}

		return builder.toString();

	}
}
