package com.lht.pan_android.util.internet;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.params.CoreProtocolPNames;

import com.lht.pan_android.util.CloudBoxApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import android.content.Context;
import android.os.Looper;

public class HttpUtil {
	public AsyncHttpClient syncHttpClient = new SyncHttpClient();
	public AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
	private List<RequestHandle> requestHandles = new LinkedList<RequestHandle>();

	private static final String USERAGENT_MODEL = "VSO-Pan-Android/%s (Android %s)";

	public HttpUtil() {
		this.getClient().setTimeout(10000);
		String str = String.format(USERAGENT_MODEL, CloudBoxApplication.APPVERSION, CloudBoxApplication.ANDROIDVERSION);
		this.getClient().getHttpClient().getParams().setParameter(CoreProtocolPNames.USER_AGENT, str);
	}

	public void getWithoutParams(Context mContext, String urlString, AsyncHttpResponseHandler res) {
		getClient().get(mContext, urlString, res);
	}

	public void getWithParams(Context mContext, String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		getClient().get(mContext, urlString, params, res);
	}

	public void getWithParams(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		getClient().get(urlString, params, res);
	}

	public void postWithEntity(Context context, String urlString, HttpEntity entity, String contentType,
			AsyncHttpResponseHandler res) {
		getClient().post(context, urlString, entity, contentType, res);
	}

	public void getWithParams(Context mContext, String tag, String urlString, RequestParams params,
			AsyncHttpResponseHandler res) {
		getClient().get(mContext, urlString, params, res).setTag(tag);
	}

	public void get(String uString, BinaryHttpResponseHandler bHandler) {
		getClient().get(uString, bHandler);
	}

	public void get(String headImgUrl, AsyncHttpResponseHandler asyncHttpResponseHandler) {
		getClient().get(headImgUrl, asyncHttpResponseHandler);

	}

	public void postWithoutParams(String urlString, AsyncHttpResponseHandler res) {
		getClient().post(urlString, res);
	}

	public void postWithParams(Context mContext, String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		getClient().post(mContext, urlString, params, res);
	}

	public void postWithParams(Context mContext, String tag, String urlString, RequestParams params,
			AsyncHttpResponseHandler res) {
		getClient().post(mContext, urlString, params, res).setTag(tag);
	}

	public void postWithParams(Context mContext, String url, HttpEntity entity, String contentType,
			AsyncHttpResponseHandler res) {
		getClient().post(mContext, url, entity, contentType, res);
	}

	public AsyncHttpClient getClient() {
		if (Looper.myLooper() == null)
			return syncHttpClient;
		return asyncHttpClient;
	}

	public void setTag(Context mContext, String urlString, AsyncHttpResponseHandler res, int tag) {
		getClient().get(mContext, urlString, res).setTag(tag);
	}

	public void setHandle(Context mContext, String urlString, AsyncHttpResponseHandler res) {
		addHandle(getWithoutParams2(mContext, urlString, res));
	}

	private void addHandle(RequestHandle handle) {
		if (null != handle) {
			requestHandles.add(handle);
		}
	}

	public List<RequestHandle> getRequestHandles() {
		return requestHandles;
	}

	private RequestHandle getWithoutParams2(Context mContext, String urlString, AsyncHttpResponseHandler res) {
		return getClient().get(mContext, urlString, res);
	}

	public void delete(Context mContext, String url, HttpEntity entity, String contentType,
			AsyncHttpResponseHandler res) {
		getClient().delete(mContext, url, entity, contentType, res);
	}

	public void getWithTag(int tag, String urlString, AsyncHttpResponseHandler asyncHttpResponseHandler) {
		getClient().get(urlString, asyncHttpResponseHandler).setTag(tag);
	}

}
