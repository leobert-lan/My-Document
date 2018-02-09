package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.clazz.customerror.ApiError;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.UploadResBean;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UploadModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 * <p>
 * to see API at{@link IRestfulApi.UploadApi}
 * to see ResBean at{@link UploadResBean}
 */
public class UploadModel implements IApiRequestModel {

    private final ApiModelCallback<UploadResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.UploadApi api;

    private RequestParams params;


    private RequestHandle handle;

    public UploadModel(String username, String attachmentPath, String type, ApiModelCallback<UploadResBean> modelCallback) {
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.UploadApi();
        this.modelCallback = modelCallback;
        params = api.newRequestParams(username, attachmentPath, type);
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
                try {
                    BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                    if (bean.isSuccess()) {
                        UploadResBean data = JSON.parseObject(bean.getData(), UploadResBean.class);
                        modelCallback.onSuccess(new BaseBeanContainer<>(data));
                    } else {
                        modelCallback.onFailure(new BaseBeanContainer<>(bean));
                    }
                } catch (Exception e) {
                    BaseVsoApiResBean bean = BaseVsoApiResBean.newExceptionBean("上传失败");
                    modelCallback.onFailure(new BaseBeanContainer<>(bean));
                    ApiError error = new ApiError(UploadModel.class,res);
                    error.report();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                modelCallback.onHttpFailure(i);
            }
        });
        handle = mHttpUtil.postWithParams(context, url, params, composite);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }
}
