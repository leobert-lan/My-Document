package com.lht.pan_android.bean;

/**
 * @ClassName: LoginBean
 * @Description: Login vso返回信息解析bean
 * @date 2015年11月25日 上午10:29:28
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DirBean {

	private String[] items;

	private String pagination;

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	public String getPagination() {
		return pagination;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

}
