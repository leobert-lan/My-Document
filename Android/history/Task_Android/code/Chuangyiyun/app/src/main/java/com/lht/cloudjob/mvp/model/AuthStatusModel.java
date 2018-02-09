package com.lht.cloudjob.mvp.model;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.LoginResBean;
import com.lht.cloudjob.mvp.model.bean.VerifyStatusResBean;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AuthStatusModel
 * <p><b>Description</b>: to query the status of auth like mobile,real-name and so on;
 * Created by leobert on 2016/7/12.
 *
 * to see Model at{@link AuthStatusModel}
 * to see API at{@link IRestfulApi.AuthStatusApi}
 */
public class AuthStatusModel implements IApiRequestModel {

    private final VerifyStatusModelCallback modelCallback;

    private final HttpUtil mHttpUtil;

    private final String usr;

    IRestfulApi.AuthStatusApi api;

    RequestParams params;

    public AuthStatusModel(VerifyStatusModelCallback modelCallback, String usr) {
        this.modelCallback = modelCallback;
        this.usr = usr;

        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.AuthStatusApi();
        params = api.newQuery(usr);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        mHttpUtil.getClient().cancelRequests(context, true);
    }

    @Override
    public void doRequest(Context context) {
        //TODO dummy
        String url = api.formatUrl(null);
        DLog.e(getClass(), url);
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.GET,url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (!bean.isSuccess()) {
                    modelCallback.onFailure(new BaseBeanContainer(bean));
                }
                VerifyStatusResBean data = JSON.parseObject(bean.getData(), VerifyStatusResBean.class);
                BaseBeanContainer<VerifyStatusResBean> container = new BaseBeanContainer<>(data);
                modelCallback.onSuccess(container);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                modelCallback.onHttpFailure(i);
            }
        });
        mHttpUtil.getWithParams(context, url, params, composite);

    }

    public interface VerifyStatusModelCallback {
        void onSuccess(BaseBeanContainer beanContainer);

        void onFailure(BaseBeanContainer beanContainer);

        void onHttpFailure(int httpStatus);

    }
}
