package com.lht.pan_android.bean;

/**
 * @ClassName: UserInfoBean
 * @Description: UserInfo的实体类
 * @date 2015年11月26日 上午8:41:18
 * 
 * @author zhang
 * @version 1.0
 * @since JDK 1.6
 */
public class UserInfoBean {

	
	private String username;

	private String nickname;

	private String icon;

	private String email;

	private String mobile;

	private String qq;

	private String path;

	//deprecated in api
//	private String storage;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

//	public String getStorage() {
//		return storage;
//	}
//
//	public void setStorage(String storage) {
//		this.storage = storage;
//	}

}
