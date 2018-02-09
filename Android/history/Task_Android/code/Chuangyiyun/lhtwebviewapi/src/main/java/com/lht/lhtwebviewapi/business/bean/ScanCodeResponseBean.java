package com.lht.lhtwebviewapi.business.bean;

import com.lht.lhtwebviewapi.business.API.NativeRet;

/** 
 * @ClassName: ScanCodeResponseBean 
 * @Description: TODO
 * @date 2016年2月24日 上午11:36:18
 *  
 * @author leobert.lan
 * @version 1.0
 */
public class ScanCodeResponseBean implements NativeRet.NativeScanCodeRet{
	
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
