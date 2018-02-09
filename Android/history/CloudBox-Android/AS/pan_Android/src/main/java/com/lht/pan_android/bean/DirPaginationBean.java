package com.lht.pan_android.bean;

/**
 * @ClassName: DirPaginationBean
 * @Description: TODO
 * @date 2015年11月30日 下午1:56:59
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DirPaginationBean {
	/*
	 * "totalItems": 80, "page": 2, "pagesize": 20, "prev": 1, "next": 3
	 */

	private int totalItems;

	private String page;

	private int pagesize;

	private String prev;

	private String next;

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

}
