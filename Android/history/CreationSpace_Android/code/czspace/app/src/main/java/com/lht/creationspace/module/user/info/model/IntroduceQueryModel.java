package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
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
 * 获取用户个性签名
 */

public class IntroduceQueryModel extends AbsRestfulApiModel
        implements IApiRequestModel {

    private final RestfulApiModelCallback<ModelResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private RequestParams params;

    private IRestfulApi.IntroQueryApi api;

    private RequestHandle handle;

    public IntroduceQueryModel(String user,
                               RestfulApiModelCallback<ModelResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.IntroQueryApi();
        params = api.newRequestParams(user);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                ModelResBean bean = JSON.parseObject(res,ModelResBean.class);
                modelCallback.onSuccess(bean);
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
        handle = mHttpUtil.getWithParams(context, url, params, composite);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public static final class ModelResBean {
        private String brief;

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }
    }
}
