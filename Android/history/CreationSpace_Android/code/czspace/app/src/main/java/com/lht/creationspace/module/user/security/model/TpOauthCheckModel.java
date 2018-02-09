package com.lht.creationspace.module.user.security.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.creationspace.mvp.model
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> TpOauthCheckModel
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/25.
 */

public class TpOauthCheckModel implements IApiRequestModel {

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.TpOauthCheckApi api;

    private RequestParams params;

    private RequestHandle handle;


    public TpOauthCheckModel(TpOauthCheckData data,
                             AbsTpOauthCheckModelCallback modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.TpOauthCheckApi();
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
        handle = mHttpUtil.getWithParams(context, url, params, composite);
    }

    public static abstract class AbsTpOauthCheckModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        private final TPOauthUserBean oauthBean;

        public AbsTpOauthCheckModelCallback(TPOauthUserBean oauthBean) {
            this.oauthBean = oauthBean;
        }

        @Override
        public final void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            onTpOauthExist(oauthBean);
        }

        @Override
        public final void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            int ret = beanContainer.getData().getRet();
            final int RET_OPENID_NOT_IN_DB = 14124;
            if (ret == RET_OPENID_NOT_IN_DB) {
                onTpOauthNotExist(oauthBean);
            }
        }


        /**
         * 存在该三方信息，可登录
         */
        protected abstract void onTpOauthExist(TPOauthUserBean oauthBean);

        /**
         * 不存在该三方信息，可用于注册
         */
        protected abstract void onTpOauthNotExist(TPOauthUserBean oauthBean);
    }

    public static class TpOauthCheckData{
        private int type;
        private String uniqueId;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }
    }
}
