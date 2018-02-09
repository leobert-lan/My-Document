package com.lht.creationspace.module.user.security.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> VerifyCodeCheckModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/21.
 * <p>
 * to see ResBean at{@link BaseVsoApiResBean}
 */
public class VerifyCodeCheckWithUserModel implements IApiRequestModel {

	private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

	private final HttpUtil mHttpUtil;

	private RequestParams params;

	private IApiNewCollections.CheckVerifyCodeWithUserApi api;

	private RequestHandle handle;

	public VerifyCodeCheckWithUserModel(VerifyCodeCheckWithUserData data,
										ApiModelCallback<BaseVsoApiResBean> callback) {
		modelCallback = callback;
		mHttpUtil = HttpUtil.getInstance();
		api = new IApiNewCollections.CheckVerifyCodeWithUserApi();
		params = api.newRequestParams(data);
	}

	@Override
	public void doRequest(Context context) {
		String url = api.formatUrl(null);
		AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
		composite.addHandler(new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String res = new String(bytes);
				BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
				if (bean.isSuccess()) {
					modelCallback.onSuccess(new BaseBeanContainer<>(bean));
				} else {
					modelCallback.onFailure(new BaseBeanContainer<>(bean));
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				modelCallback.onHttpFailure(i);
			}
		});
		handle = mHttpUtil.postWithParams(context, url, params, composite);
	}

	@Override
	public void cancelRequestByContext(Context context) {
		if (handle != null) {
			handle.cancel(true);
		}
	}

	public static class VerifyCodeCheckWithUserData {
		private String username;
		private String code;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
