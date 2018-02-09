package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
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
 * <p><b>Classname</b> BasicInfoModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/26.
 * <p>
 * to see ResBean at{@link BasicInfoResBean}
 * to see API at{@link IRestfulApi.BasicInfoApi}
 */
public class BasicInfoModel implements IApiRequestModel {
    private final ApiModelCallback<BasicInfoResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.BasicInfoApi api;

    private RequestParams params;

    private RequestHandle handle;

    public BasicInfoModel(String username, ApiModelCallback<BasicInfoResBean> callback) {
        this.modelCallback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.BasicInfoApi();
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
                DLog.e(getClass(), "check1:\r\n" + res);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean.isSuccess()) {
                    BasicInfoResBean data = JSON.parseObject(bean.getData(), BasicInfoResBean.class);
                    modelCallback.onSuccess(new BaseBeanContainer<>(data));
                } else {
                    modelCallback.onFailure(new BaseBeanContainer<>(bean));
                }
                DLog.e(getClass(), "check2:\r\n" + JSON.toJSONString(bean));
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
