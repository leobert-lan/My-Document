package com.lht.creationspace.module.article.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.article.model.pojo.NewArticleInfoResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ArticlePublishModel extends AbsRestfulApiModel implements IApiRequestModel {
    private RestfulApiModelCallback<NewArticleInfoResBean> callback;

    private HttpUtil mHttpUtil;

    IRestfulApi.ArticlePublishApi api;

    RequestParams params;

    private RequestHandle handle;

    public ArticlePublishModel(ArticleData articleData,
                               RestfulApiModelCallback<NewArticleInfoResBean> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.ArticlePublishApi();
        params = api.newRequestParams(articleData);
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
                NewArticleInfoResBean data = JSON.parseObject(res,
                        NewArticleInfoResBean.class);
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes,
                                  Throwable throwable) {
                if (i == 0)
                    callback.onHttpFailure(i);
                else
                    callback.onFailure(i, null);
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

    /**
     * 文章
     */
    public static final class ArticleData {
        /**
         * 目标/所属圈子id
         */
        private String circleId;

        /**
         * 文章title
         */
        private String title;

        /**
         * 文章内容
         */
        private String content;

        /**
         * 主配图-一般不使用
         */
        private String thumb;

        /**
         * 文章作者-username
         */
        private String author;

        /**
         * 内容图，cbs附件的id数组
         */
        private ArrayList<String> images;

        public String getCircleId() {
            return circleId;
        }

        public void setCircleId(String circleId) {
            this.circleId = circleId;
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

//        public String getFormatContent() {
//            return StringUtil.shortBreakLine(content);
//        }

        public void setContent(String content) {
            this.content = content;
        }

//        public String generateBrief() {
//            if (StringUtil.isEmpty(content))
//                return null;
//            if (content.length() > 60)
//                return content.substring(0, 59) + "…";
//            else
//                return content;
//        }


        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
        }
    }
}
