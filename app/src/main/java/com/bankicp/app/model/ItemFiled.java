package com.bankicp.app.model;

import java.io.Serializable;

public class ItemFiled implements Serializable {
	private long id;

	// / <summary>
	// / 名称
	// / </summary>
	private String name;

	// / <summary>
	// / 单位
	// / </summary>
	private String unit;
	private String inputValue;

	public ItemFiled() {
		super();
		// TODO Auto-generated constructor stub
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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

}
