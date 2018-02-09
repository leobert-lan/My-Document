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
public class SharePermissionBean {

	private String path;

	private String permission;

	private String password;

	private String link;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
