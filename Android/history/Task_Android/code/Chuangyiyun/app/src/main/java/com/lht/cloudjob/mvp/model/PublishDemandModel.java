package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandAttachFile;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/2/7.
 */

public class PublishDemandModel implements IApiRequestModel {

    ApiModelCallback<BaseVsoApiResBean> modelCallback;
    private final HttpUtil mHttpUtil;

    private IRestfulApi.PublishDemandApi api;

    private RequestParams params;

    private RequestHandle handle;

    public PublishDemandModel(String demandDescribe, String price, String  contact,
                              ArrayList<DemandAttachFile> attachFiles, ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.PublishDemandApi();
        params = api.newRequestParams(demandDescribe, price, contact, attachFiles);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
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
}
