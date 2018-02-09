package com.lht.creationspace.module.setting.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> FeedbackModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public class FeedbackModel implements IApiRequestModel {

    private final ApiModelCallback<BaseVsoApiResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IApiNewCollections.FeedbackApi api;

    private RequestParams params;

    private RequestHandle handle;

    public static final int RET_ERROR_PHONE_INVALID = 13182;


    public FeedbackModel(FeedbackData data,
                         ArrayList<String> files, ApiModelCallback<BaseVsoApiResBean> modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IApiNewCollections.FeedbackApi();

        String f = generateParamFile(files);
        params = api.newRequestParams(data, f);
    }

    private String generateParamFile(ArrayList<String> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : files) {
            if (!StringUtil.isEmpty(s)) {
                builder.append(s).append(",");
            }
        }
        return builder.substring(0, builder.length() - 1);
    }


    /**
     * 执行请求
     * TODO: 合理的处理请求失败情况，
     * 当前的接口出现非200都是网络状态问题，没有赋予特殊含义。
     * 有三层可以处理失败情况：
     * 1.model层内部处理，实质是在Composite中的default实现中处理
     * 2.回调接口的缺省适配器，暂未实现之
     * 3.回调接口实现类单独处理。
     *
     * @param context 发起请求的对象的上下文
     */
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

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public static class FeedbackData {
        private String content;
        private boolean mobile_required;
        private String contact;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isMobile_required() {
            return mobile_required;
        }

        public void setMobile_required(boolean mobile_required) {
            this.mobile_required = mobile_required;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

    }
}