package com.lht.pan_android.bean;

/**
 * @ClassName: ShareItemBean
 * @Description: 每一个share的item
 * @date 2016年1月21日 下午3:23:57
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class ShareItemBean {

	/**
	 * shareId: 操作唯一标识 id
	 */
	private String shareId;

	/**
	 * name:名称
	 */
	private String name;

	/**
	 * shareType:分享类型： 私密，公开，用户
	 */
	private String shareType;

	/**
	 * path:全路径
	 */
	private String path;

	/**
	 * shareTime:TODO(用一句话描述这个变量表示什么).
	 */
	private String shareTime;

	private String isDir;

	private String icon;

	private long size;

	private String suffix;

	/**
	 * owner:分享者id
	 */
	private String owner;

	private String link;

	private String shareTo;

	private String contentType;

	private String descendantFiles;

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDescendantFiles() {
		return descendantFiles;
	}

	public void setDescendantFiles(String descendantFiles) {
		this.descendantFiles = descendantFiles;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getShareTime() {
		return shareTime;
	}

	public void setShareTime(String shareTime) {
		this.shareTime = shareTime;
	}

	public String getIsDir() {
		return isDir;
	}

	public void setIsDir(String isDir) {
		this.isDir = isDir;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getShareTo() {
		return shareTo;
	}

	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
}
