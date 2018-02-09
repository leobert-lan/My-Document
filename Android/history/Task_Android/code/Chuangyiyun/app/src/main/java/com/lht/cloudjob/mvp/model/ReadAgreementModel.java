package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.ReadAgreementResBean;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2017/1/10.
 */

public class ReadAgreementModel implements IApiRequestModel {

    private final ApiModelCallback<ReadAgreementResBean> modelCallback;
    private final HttpUtil mHttpUtil;
    private IRestfulApi.ReadAgreementApi api;
    private RequestParams params;
    private RequestHandle handle;

    public ReadAgreementModel(String task_bn, String username, ApiModelCallback<ReadAgreementResBean> modelCallback) {
        this.modelCallback = modelCallback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.ReadAgreementApi();
        params = api.newRequestParams(task_bn, username);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean.isSuccess()) {
                    ReadAgreementResBean ReadAgreementResBean = JSON.parseObject(bean.getData(), ReadAgreementResBean.class);
                    modelCallback.onSuccess(new BaseBeanContainer<>(ReadAgreementResBean));
                } else {
                    modelCallback.onFailure(new BaseBeanContainer<>(bean));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                modelCallback.onHttpFailure(i);
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
}
