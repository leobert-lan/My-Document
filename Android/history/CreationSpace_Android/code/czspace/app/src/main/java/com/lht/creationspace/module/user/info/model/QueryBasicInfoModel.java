package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
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
 * <p><b>Classname</b> BasicInfoModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/26.
 * <p>
 * to see ResBean at{@link BasicInfoResBean}
 * to see API at{@link IApiNewCollections.BasicInfoApi}
 */
public class QueryBasicInfoModel implements IApiRequestModel {
    private final ApiModelCallback<BasicInfoResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.BasicInfoApi api;

    private RequestParams params;

    private RequestHandle handle;

    public QueryBasicInfoModel(String username,
                               ApiModelCallback<BasicInfoResBean> callback) {
        this.modelCallback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.BasicInfoApi();
        params = api.newRequestParams(username);
    }

    public QueryBasicInfoModel(String username, String token,
                               ApiModelCallback<BasicInfoResBean> callback) {
        this.modelCallback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.BasicInfoApi();
        params = api.newRequestParams(username, token);
    }

    @Override
    public void doRequest(Context context) {
        this.doRequest(context, true);
    }


    public void doRequest(Context context, boolean needHandleUnAuth) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                new AsyncResponseHandlerComposite(HttpAction.GET, url, params);
        if (!needHandleUnAuth)
            composite.disableUnauthHandler();
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean.isSuccess()) {
                    BasicInfoResBean data = JSON.parseObject(bean.getData(), BasicInfoResBean.class);
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

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }
}
