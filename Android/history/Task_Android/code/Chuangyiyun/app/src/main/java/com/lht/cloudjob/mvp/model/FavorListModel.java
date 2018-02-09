package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.FavorListItemResBean;
import com.lht.cloudjob.util.debug.DLog;
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
 * <p><b>Classname</b> FavorListModel
 * <p><b>Description</b>: 我关注的（人）列表
 * Created by leobert on 2016/8/11.
 */
public class FavorListModel implements IPagedApiRequestModel {

    private final ApiModelCallback<ArrayList<FavorListItemResBean>> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.FavorListApi api;

    private RequestParams params;

    private RequestHandle handle;


    public FavorListModel(ApiModelCallback<ArrayList<FavorListItemResBean>> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.FavorListApi();
    }

    @Override
    public void setParams(String usr, int offset) {
        params = api.newRequestParams(usr, offset);
    }

    @Override
    public void setParams(String usr, int offset, int limit) {
        params = api.newRequestParams(usr, offset, limit);
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
                    ArrayList<FavorListItemResBean> data =
                            (ArrayList<FavorListItemResBean>) JSON.parseArray(bean.getData(),
                                    FavorListItemResBean.class);
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
}
