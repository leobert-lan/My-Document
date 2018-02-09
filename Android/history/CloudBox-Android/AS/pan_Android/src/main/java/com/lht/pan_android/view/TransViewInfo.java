package com.lht.pan_android.view;

/**
 * @ClassName: TransViewInfo
 * @Description: 传输管理每个view元素的信息
 * @date 2015年12月9日 上午10:38:29
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TransViewInfo {

	private String iconUrl;

	private String name;

	private boolean isUpload;

	private String comment;

	private Status status;

	private int dbIndex = -1;

	private String contentType = "";

	private boolean isToogled;

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	/**
	 * @ClassName: Status
	 * @Description: 状态
	 * @date 2016年1月15日 下午2:55:39
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public enum Status {
		wait, pause, start, complete, failure
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isToogled() {
		return isToogled;
	}

	public void setToogled(boolean isToogled) {
		this.isToogled = isToogled;
	}

}
