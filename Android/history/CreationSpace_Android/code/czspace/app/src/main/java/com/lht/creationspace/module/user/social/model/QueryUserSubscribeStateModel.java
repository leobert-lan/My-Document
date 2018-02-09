package com.lht.creationspace.module.user.social.model;

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
 * 查询用户是否关注某人model
 */
public class QueryUserSubscribeStateModel extends AbsRestfulApiModel implements IApiRequestModel {
    private RestfulApiModelCallback<ModelResBean> callback;
    private HttpUtil mHttpUtil;
    private IRestfulApi.QueryUserSubscribeStateApi api;
    private RequestParams params;
    private RequestHandle handle;

    public QueryUserSubscribeStateModel(QueryUserSubscribeStateData data,
                                        RestfulApiModelCallback<ModelResBean> callback) {
        this.callback = callback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.QueryUserSubscribeStateApi();
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
//        private
//        "result": true,
//                "message": "已经关注",
//                "data": {
//            "id": 135,
//                    "subscriber": "87061211",
//                    "subscribe_to": "zhou88",
//                    "created_at": "2017-03-14 17:14:41",
//                    "updated_at": "2017-03-14 17:14:41"
////        }

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
            private String subscribe_to;

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

            public String getSubscribe_to() {
                return subscribe_to;
            }

            public void setSubscribe_to(String subscribe_to) {
                this.subscribe_to = subscribe_to;
            }
        }
    }

    public static class QueryUserSubscribeStateData {
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
