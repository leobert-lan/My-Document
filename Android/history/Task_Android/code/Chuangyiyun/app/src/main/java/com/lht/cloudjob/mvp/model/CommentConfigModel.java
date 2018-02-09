package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CommentConfigResBean;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CommentItemModel
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/9.
 */

public class CommentConfigModel implements IApiRequestModel {
    private final ApiModelCallback<ArrayList<CommentConfigResBean>> CommentItemResBeanCallback;
    private final IRestfulApi.GetCommentConfig api;
    private final HttpUtil mHttpUtil;
    private final RequestParams params;
    private RequestHandle handle;

    public CommentConfigModel(String task_bn, String username, ApiModelCallback<ArrayList<CommentConfigResBean>> CommentItemResBeanCallback) {
        this.CommentItemResBeanCallback = CommentItemResBeanCallback;
        api = new IRestfulApi.GetCommentConfig();
        this.mHttpUtil = HttpUtil.getInstance();
        params = api.newnewRequestParams(task_bn, username);
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
                    ArrayList<CommentConfigResBean> data =
                            (ArrayList<CommentConfigResBean>) JSON.parseArray(bean.getData(), CommentConfigResBean.class);

                    CommentItemResBeanCallback.onSuccess(new BaseBeanContainer<>(data));
                } else {
                    CommentItemResBeanCallback.onFailure(new BaseBeanContainer<>(bean));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                CommentItemResBeanCallback.onHttpFailure(i);
            }
        });
        handle = mHttpUtil.getWithParams(context, url, params, composite);
    }
}
