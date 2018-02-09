package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.annotation.antideadcode.TempVersion;
import com.lht.cloudjob.annotation.antideadcode.TempVersionEnum;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.PAuthenticQueryResBean;
import com.lht.cloudjob.util.debug.DLog;
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
 * <p><b>Classname</b> PAutenticQueryModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/5.
 * <p>
 * to see API at{@link IRestfulApi.QueryPAuthenticApi}
 * to see ResBean at{@link PAuthenticQueryResBean}
 */
public class PAuthenticQueryModel implements IApiRequestModel {

    private final ApiModelCallback<PAuthenticQueryResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.QueryPAuthenticApi api;

    private RequestParams params;

    private RequestHandle handle;

    public PAuthenticQueryModel(String usr, ApiModelCallback<PAuthenticQueryResBean> loginModelCallback) {
        this.modelCallback = loginModelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.QueryPAuthenticApi();
        params = api.newRequestParams(usr);
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
                    PAuthenticQueryResBean data = JSON.parseObject(bean.getData(), PAuthenticQueryResBean.class);
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
