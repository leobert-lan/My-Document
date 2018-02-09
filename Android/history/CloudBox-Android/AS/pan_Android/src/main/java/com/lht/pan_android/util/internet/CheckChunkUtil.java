package com.lht.pan_android.util.internet;

import org.apache.http.Header;

import com.lht.pan_android.Interface.ISendFileCallBack;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.clazz.Chunk;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.util.Log;

/**
 * @ClassName: CheckChunkUtil
 * @Description: TODO
 * @date 2015年12月1日 上午9:28:35
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class CheckChunkUtil {

	private final HttpUtil mHttpUtil;

	private final String CHECK_EXIST_URL = IUrlManager.UpLoadFile.CHUNKCHECK;

	private final Context mContext;

	private final int NEED_UPLOAD = 404;

	private ISendFileCallBack.ICheckChunkCallBack mCallback = null;

	private String tag = "SendFileExecutor - checkChunkUtil";

	public CheckChunkUtil(Context context) {
		mHttpUtil = new HttpUtil();
		mContext = context;
	}

	public void setCallBack(ISendFileCallBack.ICheckChunkCallBack callback) {
		this.mCallback = callback;
	}

	public void doJob(final Chunk c) {
		Log.i(tag, "i am check chunk");
		checkExist(c);
	}

	/**
	 * @Title: upload
	 * @Description: 检验存在
	 *               <li>检验当前碎片是否存在</li>
	 *               <li>不存在切片、上传碎片</li>
	 * @author: leobert.lan
	 * @param f
	 */
	private void checkExist(final Chunk c) {

		// 查询chunk是否存在
		RequestParams params = new RequestParams();
		params.add("md5", c.getParentMd5());
		params.add("chunk", String.valueOf(c.getChunkIndex()));
		params.add("chunkSize", c.getChunkSize());
		mHttpUtil.getWithParams(mContext, CHECK_EXIST_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (null == mCallback)
					throw new NullPointerException("callback is null");
				mCallback.isChunkExist(c, true);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				if (arg0 == NEED_UPLOAD) {
					mCallback.isChunkExist(c, false);
				} else {
					mCallback.sendStatus(arg0);
				}
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
				failureUtil.handleFailureWithCode(arg0, false);

			}
		});
	}

}
