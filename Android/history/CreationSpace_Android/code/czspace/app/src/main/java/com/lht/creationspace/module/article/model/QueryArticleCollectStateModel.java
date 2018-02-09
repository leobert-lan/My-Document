package com.lht.creationspace.module.article.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


/**
 * 查询用户是否收藏文章model
 */
public class QueryArticleCollectStateModel extends AbsRestfulApiModel
        implements IApiRequestModel {
    private RestfulApiModelCallback<ModelResBean> callback;
    private HttpUtil mHttpUtil;
    private IRestfulApi.QueryArticleCollectStateApi api;
    private RequestParams params;
    private RequestHandle handle;

    public QueryArticleCollectStateModel(QueryArticleCollectStateData data,
                                         RestfulApiModelCallback<ModelResBean> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.QueryArticleCollectStateApi();
        params = api.newRequestParams(data);
    }


    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String res = new String(bytes);
                ModelResBean bean = JSON.parseObject(res, ModelResBean.class);
                if (bean.isResult())
                    callback.onSuccess(bean);
                else
                    callback.onFailure(404, null);
            }

            @Override
            public void onFailure(int i, Header[] headers,
                                  byte[] bytes, Throwable throwable) {
                if (i == 0)
                    callback.onHttpFailure(i);
                else
                    callback.onFailure(i, null);
            }
        });
        handle = mHttpUtil.getWithParams(context, url, params, composite);
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public static final class ModelResBean {
        private boolean result;

        private String message;

        private Data data;

        public boolean isResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static final class Data {
            private String id;
            private String subscriber;
            private String article_id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSubscriber() {
                return subscriber;
            }

            public void setSubscriber(String subscriber) {
                this.subscriber = subscriber;
            }

            public String getArticle_id() {
                return article_id;
            }

            public void setArticle_id(String article_id) {
                this.article_id = article_id;
            }
        }
    }

    public static class QueryArticleCollectStateData {
        private String user;
        private String target;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
