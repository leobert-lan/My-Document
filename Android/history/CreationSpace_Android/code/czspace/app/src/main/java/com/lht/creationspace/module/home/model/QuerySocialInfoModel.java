package com.lht.creationspace.module.home.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


/**
 * 查询用户关注概述信息model
 */
public class QuerySocialInfoModel extends AbsRestfulApiModel
        implements IApiRequestModel {
    private RestfulApiModelCallback<ModelResBean> callback;
    private HttpUtil mHttpUtil;
    private IRestfulApi.QuerySocialInfoApi api;
    private RequestParams params;
    private RequestHandle handle;

    public QuerySocialInfoModel(String user,
                                RestfulApiModelCallback<ModelResBean> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.QuerySocialInfoApi();
        params = api.newRequestParams(user);
    }


    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                ModelResBean bean = JSON.parseObject(res, ModelResBean.class);
                callback.onSuccess(bean);
            }

            @Override
            public void onFailure(int i, Header[] headers,
                                  byte[] bytes, Throwable throwable) {
                if (i == 0)
                    callback.onHttpFailure(i);
                else
                    callback.onFailure(i, null);
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

    public static final class ModelResBean {

        private String subscribe_count = "0";

        private String favorite_count = "0";

        private String fans_count = "0";

        /**
         * 是否有未读消息
         */
        private boolean has_unread_favorite;

        public boolean isHas_unread_favorite() {
            return has_unread_favorite;
        }

        public void setHas_unread_favorite(boolean has_unread_favorite) {
            this.has_unread_favorite = has_unread_favorite;
        }

        public String getSubscribe_count() {
            return subscribe_count;
        }

        public void setSubscribe_count(String subscribe_count) {
            this.subscribe_count = subscribe_count;
        }

        public String getFavorite_count() {
            return favorite_count;
        }

        public void setFavorite_count(String favorite_count) {
            this.favorite_count = favorite_count;
        }

        public String getFans_count() {
            return fans_count;
        }

        public void setFans_count(String fans_count) {
            this.fans_count = fans_count;
        }
    }
}
