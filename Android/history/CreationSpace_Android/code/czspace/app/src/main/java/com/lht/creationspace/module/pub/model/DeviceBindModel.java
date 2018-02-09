package com.lht.creationspace.module.pub.model;

import android.content.Context;

import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;


/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> DeviceBindModel
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/20.
 */

public class DeviceBindModel implements IApiRequestModel {

//    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.DeviceBindApi api;

    private RequestParams params;

    private RequestHandle handle;


    public DeviceBindModel(String usr, String registrationId) {
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.DeviceBindApi();
        params = api.newRequestParams(usr, registrationId);
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
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.POST,
                url, params);
//        composite.addHandler(new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                if (bytes == null || bytes.length < 1) {
//                    return;
//                }
//                String res = new String(bytes);
//                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
//                if (bean.isSuccess()) {
//                    modelCallback.onSuccess(new BaseBeanContainer<>(bean));
//                } else {
//                    modelCallback.onFailure(new BaseBeanContainer<>(bean));
//                }
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                modelCallback.onHttpFailure(i);
//            }
//        });
        handle = mHttpUtil.postWithParams(context, url, params, composite);
    }


}
