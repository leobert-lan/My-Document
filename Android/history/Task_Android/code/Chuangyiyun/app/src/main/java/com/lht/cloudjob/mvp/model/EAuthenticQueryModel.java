package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.EAuthenticQueryResBean;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> EAuthenticQueryModel
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/5.
 * <p>
 * to see API at{@link IRestfulApi.QueryEAuthenticApi}
 * to see ResBean at{@link EAuthenticQueryResBean}
 */
public class EAuthenticQueryModel implements IApiRequestModel {
    private final ApiModelCallback<EAuthenticQueryResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.QueryEAuthenticApi api;

    private RequestParams params;

    private RequestHandle handle;

    public EAuthenticQueryModel(String userName, ApiModelCallback<EAuthenticQueryResBean> loginModelCallback) {
        this.modelCallback = loginModelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.QueryEAuthenticApi();
        params = api.newRequestParams(userName);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
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
                    EAuthenticQueryResBean data = JSON.parseObject(bean.getData(), EAuthenticQueryResBean.class);
                    modelCallback.onSuccess(new BaseBeanContainer<>(data));
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
}
