package com.lht.pan_android.bean;

/**
 * @ClassName: LoginBean
 * @Description: Login vso返回信息解析bean
 * @date 2015年11月25日 上午10:29:28
 * 
 * @author leobert.lan
 * @version 1.0
 */
@Deprecated
public class LoginBean {

	private int ret;

	private String cmd;

	private String data;

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
