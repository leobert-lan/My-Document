package com.lht.creationspace.module.user.social.model;

import android.content.Context;

import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


/**
 * 用户关注model
 */
public class UserSubscribeModel extends AbsRestfulApiModel implements IApiRequestModel {
    private RestfulApiModelCallback<String> callback;
    private HttpUtil mHttpUtil;
    private IRestfulApi.SubscribeUserApi api;
    private RequestParams params;
    private RequestHandle handle;

    public UserSubscribeModel(UserSubscribeData data, RestfulApiModelCallback<String> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.SubscribeUserApi();
        params = api.newRequestParams(data);
    }


    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (i == 0)
                    callback.onHttpFailure(i);
                else
                    callback.onFailure(i, null);
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

    public static class UserSubscribeData {
        private String user;
        private String target;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
