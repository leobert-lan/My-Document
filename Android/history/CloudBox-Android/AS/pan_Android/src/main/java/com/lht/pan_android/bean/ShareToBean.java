package com.lht.pan_android.bean;

/**
 * @ClassName: ShareBean
 * @Description: share的信息列表
 * @date 2016年1月21日 下午3:24:24
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class ShareToBean {

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
