package com.lht.creationspace.module.user.security.model;

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
 * <p><b>Classname</b> SendSmsModel
 * <p><b>Description</b>: TODO 注意，可能有多种业务
 * Created by leobert on 2016/5/5.
 * <p>
 * to see API at{@link IApiNewCollections.SendSmsApi}
 * to see ResBean at{@link BaseVsoApiResBean}
 */
public class SendSmsModel implements IApiRequestModel {

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.SendSmsApi api;

    private RequestParams params;

    private RequestHandle handle;


    public SendSmsModel(SmsRequestType type, String target,
                        ApiModelCallback<BaseVsoApiResBean> sendSmsModelCallback) {
        this.modelCallback = sendSmsModelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.SendSmsApi();
        params = api.newRequestParams(target, type);
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

    public enum SmsRequestType {
        Register(1), ResetPwd(2), BindPhone(3);
//        , Normal(4);
        private final int type;

        SmsRequestType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

}
