package com.bankicp.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class TaskInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String _ID = "id";
	public static final String TASK_ID = "task_id";
	public static final String JSON_DATA = "json_data";
	public static final String TASK_TYPE = "task_type";
	public static final String ROOM_ID = "room_id";
	public static final String CREATE_TIME = "create_time";
	public static final String USER_ID = "user_id";

	@Expose
	private long id;

	// / <summary>
	// / 任务编号
	// / </summary>
	@Expose
	private String templateName;

	// / <summary>
	// / 任务类型
	// / </summary>
	@Expose
	private String taskType;

	// / <summary>
	// / 任务状态 0:新下发,1:已认领 2:已完成
	// / </summary>
	@Expose
	private int taskState;

	// / <summary>
	// / 预计任务时间
	// / </summary>
	@Expose
	private String taskTime;
	@Expose
	private String taskEndTime;
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
	// / 间隔时长（M）
	// / </summary>
	@Expose
	private int intervalTime;

	// / <summary>
	// / 持续时长（M）
	// / </summary>
	@Expose
	private int sustainedTime;
	@Expose
	private boolean isException;
	@Expose
	private boolean isWarn;
	// / <summary>
	// / 是否必须手动
	// / </summary>
	@Expose
	private boolean isManual;
	// / <summary>
	// / 当前批次id
	// / </summary>
	@Expose
	private int currentBatch;
	// / <summary>
	// / 机柜
	// / </summary>
	@Expose
	private MachineBoxInfo cabinet;

	@Expose
	private List<String> imageList = new ArrayList<String>();

	public TaskInfo() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public int getTaskState() {
		return taskState;
	}

	public void setTaskState(int taskState) {
		this.taskState = taskState;
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

	public MachineBoxInfo getCabinet() {
		return cabinet;
	}

	public void setCabinet(MachineBoxInfo cabinet) {
		this.cabinet = cabinet;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void setImageList(List<String> imageList) {
		this.imageList = imageList;
	}

	public int getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(int currentBatch) {
		this.currentBatch = currentBatch;
	}

	public String getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(String taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	public boolean isException() {
		return isException;
	}

	public void setException(boolean isException) {
		this.isException = isException;
	}

	public boolean isWarn() {
		return isWarn;
	}

	public void setWarn(boolean isWarn) {
		this.isWarn = isWarn;
	}

}
