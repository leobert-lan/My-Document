package com.lht.cloudjob.mvp.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
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
 * <p><b>Classname</b> InfoModifyModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/26.
 * to see API at{@link IRestfulApi.ModifyInfoApi}
 * to see ResBean at{@link BaseVsoApiResBean}
 */
public class InfoModifyModel implements IApiRequestModel {

    private IRestfulApi.ModifyInfoApi.IRequestParamsBuilder builder;

    private IRestfulApi.ModifyInfoApi api;

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private RequestHandle handle;

    public InfoModifyModel(String username, ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        mHttpUtil = HttpUtil.getInstance();
        this.modelCallback = modelCallback;
        api = new IRestfulApi.ModifyInfoApi();
        this.builder = api.newRequestParamsBuilder(username);
    }

    public void modifySex(boolean isMale) {
        if (isMale) {
            builder.male();
        } else {
            builder.female();
        }
    }

    /**
     * 一级行业
     */
    public void modifyIndustry(int industryId) {
        builder.setIndustry(industryId);
    }

    /**
     * 个人领域标签
     */
    public void modifyLable(int[] fields) {
        builder.setField(fields);
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
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
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
}
