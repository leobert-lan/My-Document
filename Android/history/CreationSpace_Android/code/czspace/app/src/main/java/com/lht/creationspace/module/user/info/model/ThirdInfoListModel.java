package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.ThirdInfoListResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2017/3/10.
 * 获取三方信息列表
 */

public class ThirdInfoListModel implements IApiRequestModel {
    private ApiModelCallback<ThirdInfoListResBean> callback;

    private HttpUtil httpUtil;

    private IApiNewCollections.ThirdinfoListApi api;

    private RequestParams params;

    private RequestHandle handle;

    public static final int RET_NULL_AUTH_RECORD = 9017;

    public ThirdInfoListModel(String username,
                              ApiModelCallback<ThirdInfoListResBean> callback) {
        this.callback = callback;
        httpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.ThirdinfoListApi();
        params = api.newRequestParams(username);
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
                    ThirdInfoListResBean data = JSON.parseObject(bean.getData(), ThirdInfoListResBean.class);
                    callback.onSuccess(new BaseBeanContainer<>(data));
                } else {
                    callback.onFailure(new BaseBeanContainer<>(bean));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                callback.onHttpFailure(i);
            }
        });
        handle = httpUtil.getWithParams(context, url, params, composite);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }
}
