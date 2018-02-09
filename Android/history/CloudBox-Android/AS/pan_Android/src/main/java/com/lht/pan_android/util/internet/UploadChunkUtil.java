package com.lht.pan_android.util.internet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.ISendFileCallBack;
import com.lht.pan_android.Interface.ISendFileCallBack.OnFileSendSuccessListener;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.bean.UpChunkResultBean;
import com.lht.pan_android.clazz.Chunk;
import com.lht.pan_android.test.Debug;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @ClassName: UploadChunkUtil
 * @Description: 碎片上传助手
 * @date 2015年11月20日 下午3:52:26
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UploadChunkUtil {

	private final HttpUtil mHttpUtil;

	private final String UPLOAD_CHUNK_URL = IUrlManager.UpLoadFile.CHUNKUPLOAD;

	private final Context mContext;

	private ISendFileCallBack.ISendChunkCallBack mCallback = null;

	private final int dbIndex;
	private final OnFileSendSuccessListener fileSendSuccessListener;

	private String tag = "SendFileExecutor - UploadChunkUtil";

	public UploadChunkUtil(Context context, int dbIndex, OnFileSendSuccessListener onFileSendSuccessListener) {
		mHttpUtil = new HttpUtil();
		mContext = context;
		this.dbIndex = dbIndex;
		this.fileSendSuccessListener = onFileSendSuccessListener;
	}

	public void setCallBack(ISendFileCallBack.ISendChunkCallBack callback) {
		this.mCallback = callback;
	}

	public void doJob(final Chunk c, final File f) {
		try {
			upload(c, new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void doJob(final Chunk c, InputStream stream) {
		upload(c, stream);
	}

	private void upload(final Chunk c, InputStream stream) {
		RequestParams params = new RequestParams();

		SharedPreferences sp = mContext.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);
		String username = sp.getString(IKeyManager.Token.KEY_USERNAME, "null");
		// 参数
		params.add("username", username);
		// TODO 真实环境参数
		params.add("path", c.getUploadPath());
		params.add("chunkSize", c.getChunkSize());
		params.add("chunks", c.getChunkCount());
		params.add("chunk", String.valueOf(c.getChunkIndex()));
		params.add("md5", c.getParentMd5());
		params.add("name", c.getFinalName());
		params.add("isProjectAccess", c.isProjectAccess() ? "1" : "0");
		params.add("overwrite", c.isOverwrite() ? "1" : "0");
		params.put("file", stream);

		// Log.d(tag, "send round check\r\n chunk:" + c.getChunkIndex()
		// + ". total:" + c.getChunkCount());

		mHttpUtil.postWithParams(mContext, UPLOAD_CHUNK_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String s = new String(arg2);
				Log.i(tag, "upload return:" + s);
				UpChunkResultBean bean = JSON.parseObject(s, UpChunkResultBean.class);

				if (bean.getRet() == 2) {
					Log.d(tag, "call file Send Success");
					fileSendSuccessListener.onSuccess(dbIndex);
				} else if (bean.getRet() == 1)
					mCallback.OnUploadSuccess(c);
				else {
					mCallback.OnUploadFailure(c);
					if (IKeyManager.Debug.DEBUG_MODE)
						Debug.Log(JSON.toJSONString(c) + "\r\n");
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

				if (arg0 == 0) {
					Log.e("tmsg", "error:arg = 0;" + JSON.toJSONString(c));
				}

				if (arg0 != 0) {
					Log.e(tag, "failure status:" + arg0 + "\r\n msg:" + new String(arg2) + "\r\ndebug header:"
							+ debugHeaders(arg1));
					Log.e(tag, "upload failure");
					mCallback.OnUploadFailure(c);
					HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mContext);
					failureUtil.handleFailureWithCode(arg0, false);
				}

			}
		});
	}

	/**
	 * @param headers
	 * @return
	 */
	private String debugHeaders(Header[] headers) {
		if (headers != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("[++++++++++\r\n");
			for (Header h : headers) {
				String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
				builder.append(_h);
				builder.append("\n");
			}
			builder.append("++++++++++]\r\n");
			return builder.toString();
		}
		return "null header";
	}

}
