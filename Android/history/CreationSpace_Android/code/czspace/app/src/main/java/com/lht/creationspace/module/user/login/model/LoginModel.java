package com.lht.creationspace.module.user.login.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
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
 * <p><b>Classname</b> LoginModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/4.
 * <p>
 * to see ResBean at{@link LoginResBean}
 * to see API at{@link IApiNewCollections.LoginApi}
 */
public class LoginModel implements IApiRequestModel {

    public static final class ModelRequestData {
        private String usr;
        private String pwd;

        public ModelRequestData() {
        }

        public ModelRequestData(String usr, String pwd) {
            this.usr = usr;
            this.pwd = pwd;
        }

        public String getUsr() {
            return usr;
        }

        public void setUsr(String usr) {
            this.usr = usr;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }
    }

    private final ApiModelCallback<LoginResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.LoginApi api;

    private RequestParams params;

    private RequestHandle handle;

    public LoginModel(String usr, String pwd,
                      ApiModelCallback<LoginResBean> loginModelCallback) {
        this.modelCallback = loginModelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.LoginApi();
        params = api.newRequestParams(usr, pwd);
    }

    public LoginModel(ModelRequestData data,
                      ApiModelCallback<LoginResBean> loginModelCallback) {
        this(data.getUsr(),data.getPwd(),loginModelCallback);
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
                    LoginResBean data = JSON.parseObject(bean.getData(), LoginResBean.class);
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
