package com.lht.pan_android.bean;

/**
 * @ClassName: UpChunkResultBean
 * @Description: TODO
 * @date 2015年12月8日 下午3:11:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UpChunkResultBean {
	// {"jsonrpc" : "2.0", "message" : "file uploaded", "ret" : 2}
	private String jsonrpc;

	private String message;

	private int ret;

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	// public final String FILE_SUCCESS = "2";

}
