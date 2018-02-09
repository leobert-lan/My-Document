package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> BindPhoneModel
 * <p><b>Description</b>:
 * <p>Created by Administrator on 2016/9/1.
 * to see ResBean at {@link BaseVsoApiResBean}
 */

public class UpdateBindPhoneModel implements IApiRequestModel {
    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;
    private final HttpUtil mHttpUtil;
    private IApiNewCollections.UpdateBindPhoneApi api;
    private RequestParams params;
    private RequestHandle handle;

    public UpdateBindPhoneModel(ModelRequestData data,ApiModelCallback<BaseVsoApiResBean>
                                        modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.UpdateBindPhoneApi();
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


    public static final class ModelRequestData {
        private String username;
        private String phone;
        private String validCode;
        private String oldValidCode;

        public ModelRequestData() {
        }

        public ModelRequestData(String username, String phone, String validCode, String oldValidCode) {
            this.username = username;
            this.phone = phone;
            this.validCode = validCode;
            this.oldValidCode = oldValidCode;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }

        public String getOldValidCode() {
            return oldValidCode;
        }

        public void setOldValidCode(String oldValidCode) {
            this.oldValidCode = oldValidCode;
        }
    }
}
