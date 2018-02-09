package com.lht.pan_android.bean;

/**
 * @ClassName: LoginBean
 * @Description: Login vso返回信息解析bean
 * @date 2015年11月25日 上午10:29:28
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class VirtualAppBean {

	public String url;

	public int flagCode;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFlagCode() {
		return flagCode;
	}

	public void setFlagCode(int flagCode) {
		this.flagCode = flagCode;
	}
}
