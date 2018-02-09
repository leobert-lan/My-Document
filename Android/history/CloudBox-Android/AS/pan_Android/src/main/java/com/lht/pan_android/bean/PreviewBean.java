package com.lht.pan_android.bean;

/**
 * @ClassName: PreviewBean
 * @Description: TODO
 * @date 2016年3月16日 下午2:44:39
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class PreviewBean {

	private String path;

	private long size;

	private String shareId;

	private String owner;

	private String contentType;

	private String name;

	private String modifyTime;

	private int type;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	/**
	 * @Title: getType
	 * @Description: TODO
	 * @author: leobert.lan
	 * @return 1 cloudbox 2 share
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
