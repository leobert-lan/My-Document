package com.lht.creationspace.module.user.register.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.creationspace.mvp.model
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> TpRegWithOauthBindModel
 * <p><b>Description</b>: 使用手机号注册并绑定三方授权 API-model,只需要区分是否成功即可
 * <p>Created by leobert on 2017/2/25.
 */

public class TpRegWithOauthBindModel implements IApiRequestModel {

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.TpRegWithOauthBindApi api;

    private RequestParams params;

    private RequestHandle handle;

    public TpRegWithOauthBindModel(TpRegWithOauthBindData data, ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.TpRegWithOauthBindApi();
        params = api.newRequestParams(data);
    }

    public TpRegWithOauthBindModel(TPOauthUserBean bean,
                                   ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.TpRegWithOauthBindApi();
        params = api.newRequestParams(bean);
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
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(url, params);
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

    public static class TpRegWithOauthBindData{
        private String mobile;
        private String pwd;
        private TPOauthUserBean bean;
        private String validCode;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public TPOauthUserBean getBean() {
            return bean;
        }

        public void setBean(TPOauthUserBean bean) {
            this.bean = bean;
        }

        public String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }
    }
}
