package com.lht.pan_android.bean;

/**
 * @date 2015年11月25日 上午10:29:28
 * @author leobert.lan
 * @version 1.0
 */
public class DirItemBean extends ShareItemBean {

	private String checksum;

	private String name;

	private String path;

	private String type;

	private String owner;

	private long size;

	private String datetime;

	private int flag;

	private String descendantFiles;

	private String contentType;
	private String icon;

	/**
	 * isOpen:是否打开
	 */
	private boolean isOpen = false;

	/**
	 * isSelected:是否已经选取
	 */
	private boolean isSelected = false;

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public String getDescendantFiles() {
		return descendantFiles;
	}

	@Override
	public void setDescendantFiles(String descendantFiles) {
		this.descendantFiles = descendantFiles;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
