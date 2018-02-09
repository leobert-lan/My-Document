package com.lht.pan_android.Interface;

/**
 * @ClassName: SendFileCallback
 * @Description: Original defined in SendFileExecutor define the
 *               callback-methods when some issues happened upon transporting
 * @date 2016年1月18日 上午9:34:18
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface SendFileCallback {

	void onRetry(int dbIndex);

	void onSendSuccess(int dbIndex);

	void onFailure(int dbIndex);

	/**
	 * @Title: onNotFound
	 * @Description: 上传文件本地缺失，下载文件服务器缺失
	 * @author: leobert.lan
	 * @param dbIndex
	 */
	void onNotFound(int dbIndex);

	/**
	 * @Title: onRemoteServiceBrokendown
	 * @Description: 500系列错误
	 * @author: leobert.lan
	 * @param dbIndex
	 */
	void onRemoteServiceBrokendown(int dbIndex);

	/**
	 * @Title: onTokenExpired
	 * @Description: token 失效，需要重新登录，直接终止service
	 * @author: leobert.lan
	 * @param dbIndex
	 */
	void onTokenExpired(int dbIndex);
}
