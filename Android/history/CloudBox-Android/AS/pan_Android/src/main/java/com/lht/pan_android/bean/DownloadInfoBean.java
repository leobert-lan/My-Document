package com.lht.pan_android.bean;

/**
 * Created by zhangbin on 2015/11/30.
 */
public class DownloadInfoBean {

	/**
	 * id:唯一标示
	 */
	private int id;
	/**
	 * username:用户名信息
	 */
	private String username;
	/**
	 * localpath:本地路径
	 * 
	 */
	private String localpath;
	/**
	 * remotepath:远程路径
	 */
	private String remotepath;
	/**
	 * compeleteSize:已完成下载量
	 */
	private long compeleteSize;
	/**
	 * size:文件大小
	 */
	private long size;
	/**
	 * begin_time:作用待定
	 */
	private long begin_time;
	/**
	 * end_time:作用待定
	 */
	private long end_time;
	/**
	 * status:下载状态
	 */
	private int status;

	private String icon;

	public DownloadInfoBean(String username, String localpath, String remotepath, long begin_time, long end_time,
			long compeleteSize, long size, int status) {
		this.username = username;
		this.localpath = localpath;
		this.remotepath = remotepath;
		this.compeleteSize = compeleteSize;
		this.size = size;
		this.begin_time = begin_time;
		this.end_time = end_time;
		this.status = status;
	}

	public DownloadInfoBean() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}

	public String getRemotepath() {
		return remotepath;
	}

	public void setRemotepath(String remotepath) {
		this.remotepath = remotepath;
	}

	public long getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(long compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(long begin_time) {
		this.begin_time = begin_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
