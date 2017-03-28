package com.bankicp.app.model;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ID = "_id";
	public static final String USERID = "userId";
	public static final String USERNO = "userno";
	public static final String USERNAME = "userName";
	public static final String USERPWD = "userpwd";
	public static final String SEX = "sex";
	public static final String MOBILE = "mobile";
	public static final String EMAIL = "email";
	public static final String USERSTATUS = "userstatus";
	public static final String COMPANYID = "companyid";
	public static final String DEPARTMENTID = "departmentid";
	public static final String EMPLOYEEID = "employeeid";
	public static final String UUID = "uuid";
	public static final String COMPANYNAME = "companyname";
	public static final String DEPARTMENTNAME = "departmentname";
	public static final String CHILDDEPARTMENTNAME = "childDepartmentname";
	public static final String USERICON = "userIcon";
	public static final String USERROLES = "userroles";
	public static final String USERAREA = "userarea";

	private String id;
	private String userId;
	private String userno;
	private String userName;
	private String userpwd;
	private String sex;
	private String mobile;
	private String email;
	private String userstatus;
	private String companyid;
	private String departmentid;
	private String employeeid;
	private String uuid;
	private String companyname;
	private String departmentname;
	private String childDepartmentname;
	private String userIcon;
	private List<RoleInfo> roles;//
	private List<AreaInfo> areaInfos;//

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserstatus() {
		return userstatus;
	}

	public void setUserstatus(String userstatus) {
		this.userstatus = userstatus;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public String getEmployeeid() {
		return employeeid;
	}

	public void setEmployeeid(String employeeid) {
		this.employeeid = employeeid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getDepartmentname() {
		return departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public List<RoleInfo> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleInfo> roles) {
		this.roles = roles;
	}

	public List<AreaInfo> getAreaInfos() {
		return areaInfos;
	}

	public void setAreaInfos(List<AreaInfo> areaInfos) {
		this.areaInfos = areaInfos;
	}

	@Override
	public String toString() {
		return ID + "," + USERID + "," + USERNO + "," + USERNAME + ","
				+ USERPWD + "," + SEX + "," + MOBILE + "," + EMAIL + ","
				+ USERSTATUS + "," + COMPANYID + "," + DEPARTMENTID + ","
				+ EMPLOYEEID + "," + UUID + "," + COMPANYNAME + ","
				+ DEPARTMENTNAME;
	}

	public String getChildDepartmentname() {
		return childDepartmentname;
	}

	public void setChildDepartmentname(String childDepartmentname) {
		this.childDepartmentname = childDepartmentname;
	}

}
