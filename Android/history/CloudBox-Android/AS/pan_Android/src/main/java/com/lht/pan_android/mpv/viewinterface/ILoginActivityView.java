package com.lht.pan_android.mpv.viewinterface;

import android.content.SharedPreferences;

/** 
 * @ClassName: ILoginActivityView 
 * @Description: TODO
 * @date 2016年6月28日 下午1:56:55
 *  
 * @author leobert.lan
 * @version 1.0
 */
public interface ILoginActivityView {
	
	void onLoginSuccess();
	
	void showLoginError(String errorInfo);
	
	void showLoginError(int errorInfoRes);
	
	void showRegisterIndicator();
	
	void jumpRegister();
	
	SharedPreferences getSP(String name, int mode);
	
}
