package com.bankicp.app.model;

import java.io.Serializable;

public class RoleInfo implements Serializable {


	private String roleId;
	private String roleLevel;
	private String roleName;

	public RoleInfo() {
		super();
	}

	public String getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

}
