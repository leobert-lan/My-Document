package com.lht.lhtwebviewlib.base.model;

/**
 * @ClassName: JSAlertDataBean
 * @Description: TODO
 * @date 2016年2月23日 下午4:38:03
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class JSAlertDataBean {
	/**
	 * title:title内容
	 */
	private String title;

	/**
	 * message:消息文字
	 */
	private String message;

	/**
	 * positiveContent:确定键文字
	 */
	private String positiveContent;

	private boolean debug = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPositiveContent() {
		return positiveContent;
	}

	public void setPositiveContent(String positiveContent) {
		this.positiveContent = positiveContent;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
