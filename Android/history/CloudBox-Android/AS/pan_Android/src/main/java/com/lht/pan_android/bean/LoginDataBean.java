package com.lht.pan_android.bean;

/**
 * @ClassName: LoginDataBean
 * @Description: login vso返回字段中的data核心解析bean
 * @date 2015年11月25日 上午10:31:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class LoginDataBean {

	private String access_id;

	private String access_token;

	private String username;

	public String getAccess_id() {
		return access_id;
	}

	public void setAccess_id(String access_id) {
		this.access_id = access_id;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
