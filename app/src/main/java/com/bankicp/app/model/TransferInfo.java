package com.bankicp.app.model;

import java.io.Serializable;

public class TransferInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4893355000942039371L;

	private long id ;

    /// <summary>
    /// 交班用户ID
    /// </summary>
    private long preUserId ;
    private String preUserName ;
    
    private long nextUserId ;
    private String nextUserName;
    /// <summary>
    /// 接班用户ID
    /// </summary>
    private long[] nextUseIds;

    /// <summary>
    /// 交接内容
    /// </summary>
    private String transferContent ;

    /// <summary>
    /// 留底内容
    /// </summary>
    private String copyContent ;
    private String createTime ;
    private String modifyTime ;
    /// <summary>
    /// 交接状态
    /// </summary>
    private int transferStatus ;

	public TransferInfo() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPreUserId() {
		return preUserId;
	}

	public void setPreUserId(long preUserId) {
		this.preUserId = preUserId;
	}

	public String getPreUserName() {
		return preUserName;
	}

	public void setPreUserName(String preUserName) {
		this.preUserName = preUserName;
	}

	public long[] getNextUseIds() {
		return nextUseIds;
	}

	public void setNextUseIds(long[] nextUseIds) {
		this.nextUseIds = nextUseIds;
	}

	public String getTransferContent() {
		return transferContent;
	}

	public void setTransferContent(String transferContent) {
		this.transferContent = transferContent;
	}

	public String getCopyContent() {
		return copyContent;
	}

	public void setCopyContent(String copyContent) {
		this.copyContent = copyContent;
	}

	public int getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(int transferStatus) {
		this.transferStatus = transferStatus;
	}

	public long getNextUserId() {
		return nextUserId;
	}

	public void setNextUserId(long nextUserId) {
		this.nextUserId = nextUserId;
	}

	public String getNextUserName() {
		return nextUserName;
	}

	public void setNextUserName(String nextUserName) {
		this.nextUserName = nextUserName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
