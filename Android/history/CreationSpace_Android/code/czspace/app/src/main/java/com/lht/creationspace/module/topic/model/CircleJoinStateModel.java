package com.lht.creationspace.module.topic.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.topic.model.pojo.CircleJoinStateResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2017/3/31.
 */

public class CircleJoinStateModel extends AbsRestfulApiModel implements IApiRequestModel {
    private final RestfulApiModelCallback<CircleJoinStateResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.CheckUserIsJoinedCircleApi api;

    private RequestParams params;

    private RequestHandle handle;

    public CircleJoinStateModel(CheckUserIsJoinedCircleData data, RestfulApiModelCallback<CircleJoinStateResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.CheckUserIsJoinedCircleApi();
        params = api.newRequestParams(data);
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
                CircleJoinStateResBean bean = JSON.parseObject(res, CircleJoinStateResBean.class);
                modelCallback.onSuccess(bean);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (i == 0)
                    modelCallback.onHttpFailure(i);
                else
                    modelCallback.onFailure(i, null);
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

    /**
     * 检查用户是否已经加入了圈子data
     */
    public static class CheckUserIsJoinedCircleData {
        private String username;
        private String id;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}