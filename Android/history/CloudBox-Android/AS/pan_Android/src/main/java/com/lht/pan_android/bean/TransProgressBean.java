package com.lht.pan_android.bean;

import com.lht.pan_android.view.TransViewInfo.Status;

/**
 * @ClassName: UploadProgressBean
 * @Description: 更新上传进度时传输数据使用
 * @date 2015年12月8日 下午1:29:06
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TransProgressBean {

	private int id;

	private String message;

	// private boolean isComplete;

	private Status status;

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setId(int id) {
		this.id = id;
	}

}
