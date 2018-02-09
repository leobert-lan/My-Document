package com.lht.lhtwebviewlib.base.model;

/**
 * @ClassName: JSConfirmDialog
 * @Description: TODO
 * @date 2016年2月23日 下午4:59:14
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class JSConfirmDataBean {
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

	/**
	 * negativeContent:取消键文字
	 */
	private String negativeContent;

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

	public String getNegativeContent() {
		return negativeContent;
	}

	public void setNegativeContent(String negativeContent) {
		this.negativeContent = negativeContent;
	}

}
