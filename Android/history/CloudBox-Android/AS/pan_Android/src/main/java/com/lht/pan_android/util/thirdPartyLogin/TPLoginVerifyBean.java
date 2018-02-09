package com.lht.pan_android.util.thirdPartyLogin;

/**
 * @ClassName: TPLoginVerifyBean
 * @Description: TODO
 * @date 2016年3月16日 上午11:07:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TPLoginVerifyBean {

	public static final int TYPE_QQ = 1;

	public static final int TYPE_SINA = 2;

	public static final int TYPE_WECHAT = 3;

	/**
	 * type:第三方平台
	 */
	private int type;

	private boolean isSuccess = false;

	/**
	 * uniqueId:第三方用户唯一标识
	 */
	private String uniqueId;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public static int getTypeQq() {
		return TYPE_QQ;
	}

	public static int getTypeSina() {
		return TYPE_SINA;
	}

	public static int getTypeWechat() {
		return TYPE_WECHAT;
	}

}
