package com.lht.pan_android.bean;

/**
 * Created by zhangbin on 2015/11/30.
 */
public class ShareDownloadInfoBean extends DownloadInfoBean {

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

	private String shareId;

	private String owner;

	private String type;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ShareDownloadInfoBean(String username, String localpath, String remotepath, long begin_time, long end_time,
			long compeleteSize, long size, int status, String shareId, String owner, String type, String name) {
		this.username = username;
		this.localpath = localpath;
		this.remotepath = remotepath;
		this.compeleteSize = compeleteSize;
		this.size = size;
		this.begin_time = begin_time;
		this.end_time = end_time;
		this.status = status;
		this.shareId = shareId;
		this.owner = owner;
		this.type = type;
		this.name = name;
	}

	public ShareDownloadInfoBean() {
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getLocalpath() {
		return localpath;
	}

	@Override
	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}

	@Override
	public String getRemotepath() {
		return remotepath;
	}

	@Override
	public void setRemotepath(String remotepath) {
		this.remotepath = remotepath;
	}

	@Override
	public long getCompeleteSize() {
		return compeleteSize;
	}

	@Override
	public void setCompeleteSize(long compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public long getBegin_time() {
		return begin_time;
	}

	@Override
	public void setBegin_time(long begin_time) {
		this.begin_time = begin_time;
	}

	@Override
	public long getEnd_time() {
		return end_time;
	}

	@Override
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public void setIcon(String icon) {
		this.icon = icon;
	}

}
