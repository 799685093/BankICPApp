package com.bankicp.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 设备
 * 
 * @author yezw
 * 
 */
public class MachineInfo implements Serializable {

	@Expose
	private long id;

	// / <summary>
	// / 类别名称
	// / </summary>
	@Expose
	private String equipmentName;

	// / <summary>
	// / 设备类别信息标识
	// / </summary>
	@Expose
	private long equipmentType_id;

	// / <summary>
	// / 机柜信息标识
	// / </summary>
	@Expose
	private long cabinet_id;

	// / <summary>
	// / 巡检项集合
	// / </summary>
	@Expose
	private List<MachineItemInfo> inspectionItems = new ArrayList<MachineItemInfo>();

	public MachineInfo() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public long getEquipmentType_id() {
		return equipmentType_id;
	}

	public void setEquipmentType_id(long equipmentType_id) {
		this.equipmentType_id = equipmentType_id;
	}

	public long getCabinet_id() {
		return cabinet_id;
	}

	public void setCabinet_id(long cabinet_id) {
		this.cabinet_id = cabinet_id;
	}

	public List<MachineItemInfo> getInspectionItems() {
		return inspectionItems;
	}

	public void setInspectionItems(List<MachineItemInfo> inspectionItems) {
		this.inspectionItems = inspectionItems;
	}

}
