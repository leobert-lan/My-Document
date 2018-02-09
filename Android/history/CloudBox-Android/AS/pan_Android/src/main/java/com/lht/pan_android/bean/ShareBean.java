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
public class ShareBean {

	private String[] share;

	private String pagination;

	public String[] getShare() {
		return share;
	}

	public void setShare(String[] share) {
		this.share = share;
	}

	public String getPagination() {
		return pagination;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}
}
