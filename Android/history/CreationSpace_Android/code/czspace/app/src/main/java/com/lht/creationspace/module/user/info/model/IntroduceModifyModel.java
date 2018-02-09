package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * 修改个性签名 用户简介
 * Created by chhyu on 2017/2/20.
 */

public class IntroduceModifyModel extends AbsRestfulApiModel
        implements IApiRequestModel {

    private final RestfulApiModelCallback<String> modelCallback;

    private final HttpUtil mHttpUtil;

    private RequestParams params;

    private IRestfulApi.IntroModifyApi api;

    private RequestHandle handle;

    public IntroduceModifyModel(IntroduceModifyData data, RestfulApiModelCallback<String> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.IntroModifyApi();
        params = api.newRequestParams(data);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.PUT, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                String res = new String(bytes);
                modelCallback.onSuccess(null);
            }

            @Override
            public void onFailure(int i, Header[] headers,
                                  byte[] bytes, Throwable throwable) {
                if (i == 0)
                    modelCallback.onHttpFailure(i);
                else
                    modelCallback.onFailure(i, null);
            }
        });
        handle = mHttpUtil.putWithParams(context, url, params, composite);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public static class IntroduceModifyData {
        private String user;
        private String introduce;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }
    }
}
