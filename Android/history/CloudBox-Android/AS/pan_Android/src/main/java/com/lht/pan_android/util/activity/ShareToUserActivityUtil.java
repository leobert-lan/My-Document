package com.lht.pan_android.util.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.asyncProtected.ShareToUserActivity;
import com.lht.pan_android.adapter.SearchUserListAdapter;
import com.lht.pan_android.bean.SearchUserBean;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.clazz.Events.CallFreshShareEvent;
import com.lht.pan_android.clazz.LRUCache;
import com.lht.pan_android.util.SPUtil;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.internet.HttpRequestFailureUtil;
import com.lht.pan_android.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
//import android.util.Log;
import android.widget.Toast;

/**
 * @ClassName: ShareToUserActivityUtil
 * @Description: TODO
 * @date 2016年1月26日 下午3:02:42
 * 
 * @author leobert.lan
 * @version 1.0
 */
@SuppressLint("HandlerLeak")
public class ShareToUserActivityUtil {

	private final String tag = "ShareToUserActivityUtil";

	private final ShareToUserActivity mActivity;

	private HttpUtil mHttpUtil;

	private String username = "";

	private String access_id = "";

	private String access_token = "";

	private LRUCache<String, String> contractCache;

	private SharedPreferences contractsp;

	public ShareToUserActivityUtil(ShareToUserActivity activity) {
		mActivity = activity;
		mHttpUtil = new HttpUtil();
		SharedPreferences sp = mActivity.getSharedPreferences(IKeyManager.Token.SP_TOKEN, Context.MODE_PRIVATE);

		username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
		access_id = sp.getString(IKeyManager.Token.KEY_ACCESS_ID, "");
		access_token = sp.getString(IKeyManager.Token.KEY_ACCESS_TOKEN, "");

		contractCache = new LRUCache<String, String>(100);
		contractsp = activity.getSharedPreferences(IKeyManager.ShareContractSp.SP_SUFFIX + username,
				Context.MODE_PRIVATE);
		reBulidCache();

	}

	public void startContactGet() {
		GetContractHandler h = new GetContractHandler(mActivity.getSearchUserListAdapter());
		GetContractThread t = new GetContractThread(h);
		t.start();
	}

	public void searchUser(String key) {
		// IUrlManager.SearchUsersOnPlatform
		String url = getUserSaerchUrl(key);

		// Log.d("lmsg", "check url:" + url);

		RequestParams params = new RequestParams();

		params.add(IUrlManager.SearchUsersOnPlatform.KEY_USERNAME, username);
		params.add(IUrlManager.SearchUsersOnPlatform.KEY_SEARCHKEY, key);
		params.add(IUrlManager.SearchUsersOnPlatform.KEY_LIMIT, IUrlManager.SearchUsersOnPlatform.VALUE_DEFAULT_LIMIT);
		params.add(IUrlManager.SearchUsersOnPlatform.KEY_ACCESSID, access_id);
		params.add(IUrlManager.SearchUsersOnPlatform.KEY_ACCESSTOKEN, access_token);

		mActivity.showWaitView(true);

		final long requestBeginTimeStamp = TimeUtil.getCurrentTimeStamp();

		mHttpUtil.getWithParams(mActivity, url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String retData = new String(arg2);
				SearchUserBean userBean = JSON.parseObject(retData, SearchUserBean.class);
				ArrayList<SearchUserItemBean> list = new ArrayList<SearchUserItemBean>();
				String[] users = userBean.getUsers();
				for (int i = 0; i < users.length; i++) {
					SearchUserItemBean bean = JSON.parseObject(users[i], SearchUserItemBean.class);
					// 过滤自身
					if (bean.getUsername().equals(username)) {
						continue;
					}
					list.add(bean);
				}
				if (searchUserCallback != null)
					searchUserCallback.onUserSearched(list);
				mActivity.cancelWaitView();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// Log.d("lmsg", "search status:" + arg0);
				if (TimeUtil.getCurrentTimeStamp() - requestBeginTimeStamp > 3)
					ToastUtil.show(mActivity, R.string.search_timeout, Duration.s);
				else if (arg0 == 0) {
					ToastUtil.show(mActivity, R.string.no_internet, Duration.s);
				} else {
					HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
					// 我就想吐槽一下，能不能不要请求成功了但是使用status来代表失败含义！
					failureUtil.handleFailureWithCode(arg0, false);
				}
				if (searchUserCallback != null)
					searchUserCallback.onUserSearched(new ArrayList<SearchUserItemBean>());
				mActivity.cancelWaitView();
			}
		});

	}

	private String getUserSaerchUrl(String key) {

		String ret = IUrlManager.SearchUsersOnPlatform.DOMAIN + IUrlManager.SearchUsersOnPlatform.ADDRESS;
		return ret;
	}

	private SearchUserCallback searchUserCallback;

	public void setSearchUserCallback(SearchUserCallback callback) {
		searchUserCallback = callback;
	}

	public interface SearchUserCallback {
		void onUserSearched(ArrayList<SearchUserItemBean> list);
	}

	/**
	 * @Title: callFresh
	 * @Description: 刷新分享页
	 * @author: leobert.lan
	 */
	private void callFresh() {
		EventBus.getDefault().post(new CallFreshShareEvent());
	}

	public void share(String path, ArrayList<SearchUserItemBean> selectedItems) {
		String url = getShareUrl();
		StringEntity entity = getEntity(path, selectedItems);
		mActivity.showWaitView(true);

		mHttpUtil.postWithEntity(mActivity, url, entity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mActivity.cancelWaitView();
				Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
				callFresh();
				mActivity.finish();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(mActivity, "分享失败", Toast.LENGTH_SHORT).show();
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, false);
				mActivity.cancelWaitView();
			}
		});
	}

	/**
	 * @Title: modify
	 * @Description: 修改分享
	 * @author: leobert.lan
	 * @param shareId
	 * @param selectedItems
	 */
	public void modify(String shareId, ArrayList<SearchUserItemBean> selectedItems) {
		String url = getModifyUrl(shareId);
		StringEntity entity = getModifyEntity(selectedItems);
		mActivity.showWaitView(true);

		mHttpUtil.postWithEntity(mActivity, url, entity, "application/json", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				mActivity.cancelWaitView();
				Toast.makeText(mActivity, "修改成功", Toast.LENGTH_SHORT).show();
				callFresh();
				mActivity.finish();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(mActivity, "修改失败", Toast.LENGTH_SHORT).show();
				HttpRequestFailureUtil failureUtil = new HttpRequestFailureUtil(mActivity);
				failureUtil.handleFailureWithCode(arg0, false);
				mActivity.cancelWaitView();
			}
		});
	}

	private StringEntity getEntity(String path, ArrayList<SearchUserItemBean> selectedItems) {
		JSONObject jObj = new JSONObject();
		jObj.put(IUrlManager.ShareToUser.KEY_PATH, path);
		JSONArray jArray = new JSONArray();
		for (int i = 0; i < selectedItems.size(); i++) {
			SearchUserItemBean bean = selectedItems.get(i);
			jArray.add(bean.getUsername());
		}
		jObj.put(IUrlManager.ShareToUser.KEY_SHARETO, jArray);
		StringEntity ret = null;
		try {
			ret = new StringEntity(jObj.toJSONString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// Log.d(tag, "check entity:" + jObj.toJSONString());
		return ret;
	}

	private StringEntity getModifyEntity(ArrayList<SearchUserItemBean> selectedItems) {
		JSONObject jObj = new JSONObject();
		JSONArray jArray = new JSONArray();
		for (int i = 0; i < selectedItems.size(); i++) {
			SearchUserItemBean bean = selectedItems.get(i);
			jArray.add(bean.getUsername());
		}
		jObj.put("users", jArray);
		StringEntity ret = null;
		try {
			ret = new StringEntity(jObj.toJSONString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// Log.d("lmsg", "check modify entity:" + jObj.toJSONString());
		return ret;
	}

	private String getShareUrl() {
		String ret = IUrlManager.ShareToUser.DOMAIN + IUrlManager.ShareToUser.ADDRESS + username
				+ IUrlManager.ShareToUser.FUNCTION + "?" + IUrlManager.ShareToUser.KEY_ACCESSID + "=" + access_id + "&"
				+ IUrlManager.ShareToUser.KEY_ACCESSTOKEN + "=" + access_token;
		// Log.d(tag, "check url:" + ret);
		return ret;
	}

	private String getModifyUrl(String shareId) {
		// POST /v3/users/zhou81/share/56dd31f18a510da40406072c/friend?
		String ret = IUrlManager.ShareToUser.DOMAIN + IUrlManager.ShareToUser.ADDRESS + username + "/share/" + shareId
				+ "/friend?" + IUrlManager.ShareToUser.KEY_ACCESSID + "=" + access_id + "&"
				+ IUrlManager.ShareToUser.KEY_ACCESSTOKEN + "=" + access_token;
		// Log.d(tag, "check url:" + ret);
		return ret;
	}

	/**
	 * @Title: saveContract
	 * @Description: 存储这些用户
	 * @author: leobert.lan
	 * @param selectedItems
	 */
	public void saveContract(ArrayList<SearchUserItemBean> selectedItems) {
		for (int i = 0; i < selectedItems.size(); i++) {
			SearchUserItemBean bean = selectedItems.get(i);
			contractCache.put(bean.getUsername(), JSON.toJSONString(bean));
		}

		JSONArray jArray = new JSONArray();

		for (Map.Entry<String, String> e : contractCache.getAll()) {
			jArray.add(e.getValue());
		}
		SPUtil.modifyString(contractsp, IKeyManager.ShareContractSp.KEY_CONTENT, jArray.toJSONString());
	}

	private void reBulidCache() {
		String temp = contractsp.getString(IKeyManager.ShareContractSp.KEY_CONTENT, null);
		if (temp != null) {
			JSONArray jArray = JSON.parseArray(temp);
			int count = jArray.size();
			for (int i = count - 1; i > -1; i--) {
				SearchUserItemBean bean = JSON.parseObject(jArray.getString(i), SearchUserItemBean.class);
				contractCache.put(bean.getUsername(), jArray.getString(i));
			}
		}
	}

	ArrayList<SearchUserItemBean> ret;

	/**
	 * @Title: getContract
	 * @Description: 获取最近联系人
	 * @author: leobert.lan
	 * @return
	 */
	private ArrayList<SearchUserItemBean> getContract() {
		ret = new ArrayList<SearchUserItemBean>();
		for (Map.Entry<String, String> e : contractCache.getAll()) {
			SearchUserItemBean bean = JSON.parseObject(e.getValue(), SearchUserItemBean.class);
			bean.setSelect(false);
			ret.add(bean);
		}
		return ret;
	}

	public class GetContractThread extends Thread {

		private GetContractHandler handler;

		public GetContractThread(GetContractHandler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			getContract();
			Message msg = new Message();
			msg.what = GetContractHandler.MSG_OK;
			handler.sendMessage(msg);
			super.run();
		}
	}

	public class GetContractHandler extends Handler {

		private SearchUserListAdapter adapter;

		public GetContractHandler(SearchUserListAdapter adapter) {
			this.adapter = adapter;
		}

		public final static int MSG_OK = 1;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_OK) {
				adapter.ReplaceAll(ret, false);
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void destory() {
		mHttpUtil.getClient().cancelRequests(mActivity, true);
	}

}
