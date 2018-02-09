package com.lht.creationspace.module.user.login.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.login.model.pojo.TpLoginResBean;
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
 * <p><b>Classname</b> TpLoginModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/15.
 * <p>
 * to see API at{@link IApiNewCollections.TpLoginApi}
 * to see ResBean at{@link TpLoginResBean}
 */
public class TpLoginModel implements IApiRequestModel {
    private final ApiModelCallback<TpLoginResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.TpLoginApi api;

    private RequestParams params;

    private RequestHandle handle;


    public TpLoginModel(TpLoginData data,
                        ApiModelCallback<TpLoginResBean> callback) {
        this.modelCallback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.TpLoginApi();
        params = api.newRequestParams(data);
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
                    TpLoginResBean data = JSON.parseObject(bean.getData(), TpLoginResBean.class);
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
        handle = mHttpUtil.postWithParams(context, url, params, composite);

    }

    public static class TpLoginData{
        private int platform;
        private String openId;

        public int getPlatform() {
            return platform;
        }

        public void setPlatform(int platform) {
            this.platform = platform;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }
}
