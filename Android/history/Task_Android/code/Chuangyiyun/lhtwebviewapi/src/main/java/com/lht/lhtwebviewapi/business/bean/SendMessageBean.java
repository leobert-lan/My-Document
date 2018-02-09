package com.lht.lhtwebviewapi.business.bean;
/** 
 * @ClassName: DemoBean 
 * @Description: API:Demo 业务起始参数
 * @date 2016年2月19日 下午4:27:31
 *  
 * @author leobert.lan
 * @version 1.0
 */
public class SendMessageBean {
	
	private String contacts;
	
	private String messageContent;

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}


}
