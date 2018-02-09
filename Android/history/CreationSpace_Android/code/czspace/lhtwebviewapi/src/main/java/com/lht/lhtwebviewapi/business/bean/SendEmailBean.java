package com.lht.lhtwebviewapi.business.bean;
/** 
 * @ClassName: DemoBean 
 * @Description: API:Demo 业务起始参数
 * @date 2016年2月19日 下午4:27:31
 *  
 * @author leobert.lan
 * @version 1.0
 */
public class SendEmailBean {
	
	private String[] addressee;
	
	private String message;
	
	public String[] getAddressee() {
		return addressee;
	}

	public void setAddressee(String[] addressee) {
		this.addressee = addressee;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
