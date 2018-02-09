package com.lht.creationspace.module.user.info.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
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
 * <p><b>Classname</b> InfoModifyModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/26.
 * to see API at{@link IApiNewCollections.ModifyInfoApi}
 * to see ResBean at{@link BaseVsoApiResBean}
 */
public class InfoModifyModel implements IApiRequestModel {

    private IApiNewCollections.ModifyInfoApi.IRequestParamsBuilder builder;

    private IApiNewCollections.ModifyInfoApi api;

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private RequestHandle handle;

    public InfoModifyModel(String username,
                           ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        mHttpUtil = HttpUtil.getInstance();
        this.modelCallback = modelCallback;
        api = new IApiNewCollections.ModifyInfoApi();
        this.builder = api.newRequestParamsBuilder(username);
    }

    public void modifySex(boolean isMale) {
        if (isMale) {
            builder.male();
        } else {
            builder.female();
        }
    }


    public void modifyNickname(String nickname) {
        builder.setNickname(nickname);
    }

    public void modifyAvatar(String avatar) {
        builder.setAvatar(avatar);
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
        RequestParams params = builder.build();
        AsyncResponseHandlerComposite composite =
                new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean == null) {
                    bean = new BaseVsoApiResBean();
                }
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

    public void doRequest(Context context, final ApiModelCallback<BasicInfoResBean> customCallback) {
        String url = api.formatUrl(null);
        RequestParams params = builder.build();
        AsyncResponseHandlerComposite composite =
                new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean == null) {
                    bean = new BaseVsoApiResBean();
                }
                if (bean.isSuccess()) {
                    try {
                        BasicInfoResBean resBean = JSON.parseObject(bean.getData(),
                                BasicInfoResBean.class);
                        customCallback.onSuccess(new BaseBeanContainer<>(resBean));
                    } catch (Exception e) {
                        customCallback.onSuccess(new BaseBeanContainer<>(new BasicInfoResBean()));
                    }

                } else {
                    customCallback.onFailure(new BaseBeanContainer<>(bean));
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
