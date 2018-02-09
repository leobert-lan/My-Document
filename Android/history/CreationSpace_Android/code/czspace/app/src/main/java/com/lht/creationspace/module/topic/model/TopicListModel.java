package com.lht.creationspace.module.topic.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.topic.model.pojo.TopicResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2017/3/3.
 * 发布文章--选择圈子
 */
public class TopicListModel extends AbsRestfulApiModel implements IApiRequestModel {
    private RestfulApiModelCallback<TopicResBean> callback;
    private final HttpUtil mHttpUtil;

    private IRestfulApi.TopicListApi api;

    private RequestParams params;

    private RequestHandle handle;

    public TopicListModel(int offset, RestfulApiModelCallback<TopicResBean> callback) {
        this.callback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.TopicListApi();
        params = api.newRequestParams(offset);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite = newAsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                TopicResBean data = JSON.parseObject(res, TopicResBean.class);
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
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
}
