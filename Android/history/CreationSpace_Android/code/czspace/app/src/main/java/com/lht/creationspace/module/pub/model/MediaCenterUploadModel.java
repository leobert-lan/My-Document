package com.lht.creationspace.module.pub.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
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
 * <p><b>Classname</b> MediaCenterUploadModel
 * <p><b>Description</b>: TODO
 * <p>
 * 该接口返回的数据结构未嵌套，注意注意
 * <p> Create by Leobert on 2016/9/12
 */
public class MediaCenterUploadModel extends AbsRestfulApiModel implements IApiRequestModel {

    private final RestfulApiModelCallback<MediaCenterUploadResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.MediaCenterUploadApi api;

    private RequestParams params;

    private RequestHandle handle;

    public static final String TYPE_PROJECT_ATTACHMENT = "project";

    public static final String TYPE_ARTICLE_ATTACHMENT = "article";

    public static final String TYPE_FEEDBACK_ATTACHMENT = "feedback";

    public static final String TYPE_AVATAR_ATTACHMENT = "avatar";


    public MediaCenterUploadModel(MediaCenterUploadData data,
                                  RestfulApiModelCallback<MediaCenterUploadResBean>
                                          modelCallback) {
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.MediaCenterUploadApi();
        //给文件流中包含文件名信息使用
        String name = data.getFilePath().substring(data.getFilePath().lastIndexOf("/"));

        params = api.newRequestParams(data, name);
    }


    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                MediaCenterUploadResBean data = JSON.parseObject(res,
                        MediaCenterUploadResBean.class);
                modelCallback.onSuccess(data);
            }

            @Override
            public void onFailure(int i, Header[] headers,
                                  byte[] bytes, Throwable throwable) {
                if (i == 0)
                    modelCallback.onHttpFailure(i);
                else
                    modelCallback.onFailure(i, null);
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

    public static class MediaCenterUploadData {
        private String filePath;
        private String type;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
