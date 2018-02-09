package com.lht.creationspace.module.projchapter.ui.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.module.projchapter.ui.model.bean.ProjUpdateResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/7/27.
 */

public class ProjChapterPublishModel extends AbsRestfulApiModel
        implements IApiRequestModel {

    private RestfulApiModelCallback<ProjUpdateResBean> callback;
    private HttpUtil httpUtil;
    private IRestfulApi.ProjContentPublishApi api;
    private RequestParams params;

    private RequestHandle handle;

    private String projectId;


    public ProjChapterPublishModel(RequestData paramsDate,
                                   RestfulApiModelCallback<ProjUpdateResBean> callback) {
        this.callback = callback;
        httpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.ProjContentPublishApi();
        projectId = paramsDate.getProjectId();
        params = api.newRequestParams(paramsDate);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(new String[]{projectId});
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                ProjUpdateResBean bean = JSON.parseObject(res, ProjUpdateResBean.class);
                callback.onSuccess(bean);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (i == 0)
                    callback.onHttpFailure(i);
                else
                    callback.onFailure(i, null);
            }
        });
        handle = httpUtil.postWithParams(context, url, params, composite);
    }


    public static class RequestData {
        /**
         * 需要执行更新操作的文章记录id
         */
        private String id;

        /**
         * 项目id
         */
        private String projectId;

        /**
         * 标题
         */
        private String title;

        /**
         * 内容
         */
        private String content;

        /**
         * 简介，默认为null
         */
        private String brief;

        /**
         * 内容图，cbs附件的id数组
         */
        private ArrayList<String> images;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }
    }
}
