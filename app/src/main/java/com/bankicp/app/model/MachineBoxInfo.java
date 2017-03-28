package com.bankicp.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 机柜
 * 
 * @author yezw
 * 
 */
public class MachineBoxInfo implements Serializable {
	@Expose
	private long cabinetId;
	@Expose
	private String cabinetName;
	@Expose
	private String cabinetNo;
	@Expose
	private String fixedAssetsNo;
	@Expose
	private String createDate;
	@Expose
	private long roomId;
	@Expose
	private String roomName;
	@Expose
	private int inspectionOrder;
	// / <summary>
	// / 设备集合
	// / </summary>
	@Expose
	private List<MachineInfo> equipmentList = new ArrayList<MachineInfo>();

	public MachineBoxInfo() {
	}

	public long getCabinetId() {
		return cabinetId;
	}

	public void setCabinetId(long cabinetId) {
		this.cabinetId = cabinetId;
	}

	public String getCabinetName() {
		return cabinetName;
	}

	public void setCabinetName(String cabinetName) {
		this.cabinetName = cabinetName;
	}

	public String getCabinetNo() {
		return cabinetNo;
	}

	public void setCabinetNo(String cabinetNo) {
		this.cabinetNo = cabinetNo;
	}

	public String getFixedAssetsNo() {
		return fixedAssetsNo;
	}

	public void setFixedAssetsNo(String fixedAssetsNo) {
		this.fixedAssetsNo = fixedAssetsNo;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<MachineInfo> getEquipmentList() {
		return equipmentList;
	}

	public void setEquipmentList(List<MachineInfo> equipmentList) {
		this.equipmentList = equipmentList;
	}

	public long getRoomId() {
		return roomId;
	}

	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public int getInspectionOrder() {
		return inspectionOrder;
	}

	public void setInspectionOrder(int inspectionOrder) {
		this.inspectionOrder = inspectionOrder;
	}

}
