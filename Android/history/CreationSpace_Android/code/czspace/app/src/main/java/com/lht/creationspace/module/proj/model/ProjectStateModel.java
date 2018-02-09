package com.lht.creationspace.module.proj.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/8.
 * 获取所有支持的项目状态 - 发布项目需要使用
 */

public class ProjectStateModel extends AbsRestfulApiModel implements IApiRequestModel {
    private RestfulApiModelCallback<ArrayList<ProjectStateResBean>> callback;
    private HttpUtil mHttpUtil;
    IRestfulApi.ProjectStateListApi api;
    RequestParams params;
    private RequestHandle handle;

    public ProjectStateModel(RestfulApiModelCallback<ArrayList<ProjectStateResBean>> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.ProjectStateListApi();
        params = api.newRequestParams();
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
                ArrayList<ProjectStateResBean> data = (ArrayList<ProjectStateResBean>) JSON.parseArray(res, ProjectStateResBean.class);
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
