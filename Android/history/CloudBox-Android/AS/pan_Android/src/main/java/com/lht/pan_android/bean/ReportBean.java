package com.lht.pan_android.bean;

/**
 * @ClassName: ReportBean
 * @Description: TODO
 * @date 2016年5月27日 上午11:16:15
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ReportBean {

	/**
	 * username:举报者
	 */
	private String username;

	/**
	 * to_username:被举报者
	 */
	private String to_username;

	private String report_file;

	private String report_desc;

	private String shareId;

	private String contact;

	public ReportBean() {
	}

	public ReportBean(ShareItemBean item) {
		setTo_username(item.getOwner());
		setReport_file(item.getPath());
		setShareId(item.getShareId());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTo_username() {
		return to_username;
	}

	public void setTo_username(String to_username) {
		this.to_username = to_username;
	}

	public String getReport_file() {
		return report_file;
	}

	public void setReport_file(String report_file) {
		this.report_file = report_file;
	}

	public String getReport_desc() {
		return report_desc;
	}

	public void setReport_desc(String report_desc) {
		this.report_desc = report_desc;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

}
