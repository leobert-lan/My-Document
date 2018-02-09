package com.lht.pan_android.Interface;

/**
 * @ClassName: IAsyncWithProgressbar
 * @Description: TODO
 * @date 2016年4月11日 下午2:05:08
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IAsyncWithProgressbar extends IPreventPenetrate {

	void showProgressBarOnAsync(boolean isProtectedNeed);

	void cancelProgressBarOnAsyncFinish();

}
