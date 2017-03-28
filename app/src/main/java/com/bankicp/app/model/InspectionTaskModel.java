package com.bankicp.app.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class InspectionTaskModel implements Serializable {
	@Expose
	private long id;
	// / <summary>
	// / 任务类型
	// / </summary>
	@Expose
	private String taskType;

	// / <summary>
	// / 预计任务时间
	// / </summary>
	@Expose
	private String taskTime;

	// / <summary>
	// / 时间段
	// / </summary>
	@Expose
	private String timeQuantum;

	// / <summary>
	// / 是否发现问题
	// / </summary>
	@Expose
	private boolean isProblem;

	// / <summary>
	// / 问题描述
	// / </summary>
	@Expose
	private String problemDescription;

	// / <summary>
	// / 实际开始时间
	// / </summary>
	@Expose
	private String actualStartTime;

	// / <summary>
	// / 实际结束时间
	// / </summary>
	@Expose
	private String actualEndTime;

	// / <summary>
	// / 机柜标识
	// / </summary>
	@Expose
	private long cabinet_id;
	// / <summary>
	// / 机柜名称
	// / </summary>
	@Expose
	private String cabinetName;

	// / <summary>
	// / 间隔时长（M）
	// / </summary>
	@Expose
	private int intervalTime;

	// / <summary>
	// / 持续时长（M）
	// / </summary>
	@Expose
	private int sustainedTime;

	// / <summary>
	// / 是否必须手动
	// / </summary>
	@Expose
	private boolean isManual;

	public InspectionTaskModel() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(String taskTime) {
		this.taskTime = taskTime;
	}

	public String getTimeQuantum() {
		return timeQuantum;
	}

	public void setTimeQuantum(String timeQuantum) {
		this.timeQuantum = timeQuantum;
	}

	public boolean isProblem() {
		return isProblem;
	}

	public void setProblem(boolean isProblem) {
		this.isProblem = isProblem;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	public String getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(String actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public String getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(String actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public long getCabinet_id() {
		return cabinet_id;
	}

	public void setCabinet_id(long cabinet_id) {
		this.cabinet_id = cabinet_id;
	}

	public String getCabinetName() {
		return cabinetName;
	}

	public void setCabinetName(String cabinetName) {
		this.cabinetName = cabinetName;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public int getSustainedTime() {
		return sustainedTime;
	}

	public void setSustainedTime(int sustainedTime) {
		this.sustainedTime = sustainedTime;
	}

	public boolean isManual() {
		return isManual;
	}

	public void setManual(boolean isManual) {
		this.isManual = isManual;
	}

}
