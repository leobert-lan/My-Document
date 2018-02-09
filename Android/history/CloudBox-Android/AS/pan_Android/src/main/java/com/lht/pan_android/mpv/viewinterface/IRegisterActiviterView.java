package com.lht.pan_android.mpv.viewinterface;

import com.lht.pan_android.Interface.IAsyncWithProgressbar;
import com.lht.pan_android.view.popupwins.CustomDialog;

import android.content.SharedPreferences;
import android.content.res.Resources;

/**
 * @ClassName: IRegisterActiviterView
 * @Description: TODO
 * @date 2016年5月26日 上午8:45:28
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IRegisterActiviterView extends IAsyncWithProgressbar {

	/**
	 * @Title: showErrorMsg
	 * @Description: 显示错误消息
	 * @author: leobert.lan
	 * @param errMsg
	 */
	void showErrorMsg(String errMsg);

	/**
	 * @Title: enableVerifyCodeGetter
	 * @Description: 激活验证码获取
	 * @author: leobert.lan
	 */
	void enableVerifyCodeGetter();

	/**
	 * @Title: disableVerifyCodeGetter
	 * @Description: 失活验证码获取
	 * @author: leobert.lan
	 */
	void disableVerifyCodeGetter();

	/**
	 * @Title: disableRegister
	 * @Description: 失活注册按钮
	 * @author: leobert.lan
	 */
	void disableRegister();

	/**
	 * @Title: enableRegister
	 * @Description: 激活注册按钮
	 * @author: leobert.lan
	 */
	void enableRegister();

	/**
	 * @Title: showRegisteredNumDialog
	 * @Description: 显示已注册账号对话框
	 * @author: leobert.lan
	 */
	void showRegisteredNumDialog(CustomDialog dialog);

	/**
	 * @Title: newRegisteredNumDialog
	 * @Description: 新建对话框实例
	 * @author: leobert.lan
	 */
	CustomDialog newRegisteredNumDialog();

	/**
	 * @Title: freshCoolingTime
	 * @Description: 刷新冷却时间
	 * @author: leobert.lan
	 * @param seconds
	 */
	void freshCoolingTime(int seconds);

	void back2Login();

	Resources getRes();

	SharedPreferences getTimmerSp();

}
