package com.lht.lhtwebviewapi.business.bean;
/** 
 * @ClassName: ThirdPartyLoginBean 
 * @Description: TODO
 * @date 2016年2月26日 下午5:14:02
 *  
 * @author leobert.lan
 * @version 1.0
 */
public class ThirdPartyLoginBean {
	public final static int TYPE_QQ = 1;
	
	public final static int TYPE_SINABLOG = 2;
	
	public final static int TYPE_WECHAT = 3;
	
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
