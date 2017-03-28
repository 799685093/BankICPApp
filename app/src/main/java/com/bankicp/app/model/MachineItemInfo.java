package com.bankicp.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

//设备的巡检项
public class MachineItemInfo implements Serializable {
	@Expose
	private long id;
	// / <summary>
	// / 巡检项名
	// / </summary>
	@Expose
	private String name;

	// / <summary>
	// / 标准阈值
	// / </summary>
	@Expose
	private String standardValue;
	// 输入值
	@Expose
	private String inputValue;

	@Expose
	private List<ItemFiled> InspectionItemFileds = new ArrayList<ItemFiled>();
	// / <summary>
	// / 巡检项序列
	// / </summary>
	@Expose
	private int order;

	public MachineItemInfo() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandardValue() {
		return standardValue;
	}

	public void setStandardValue(String standardValue) {
		this.standardValue = standardValue;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public List<ItemFiled> getInspectionItemFileds() {
		return InspectionItemFileds;
	}

	public void setInspectionItemFileds(List<ItemFiled> inspectionItemFileds) {
		InspectionItemFileds = inspectionItemFileds;
	}

	public String itemFiledToString() {

		return "";
	}
}
