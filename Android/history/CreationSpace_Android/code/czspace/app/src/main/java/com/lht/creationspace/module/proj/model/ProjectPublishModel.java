package com.lht.creationspace.module.proj.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.module.api.IRestfulApi;
import com.lht.creationspace.base.model.apimodel.AbsRestfulApiModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.proj.model.pojo.ProjectPublishResBean;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2017/3/8.
 */

public class ProjectPublishModel extends AbsRestfulApiModel
        implements IApiRequestModel {
    private RestfulApiModelCallback<ProjectPublishResBean> callback;
    private HttpUtil httpUtil;
    private IRestfulApi.ProjectPublishApi api;
    private RequestParams params;

    private RequestHandle handle;

    public ProjectPublishModel(ProjectData bean,
                               RestfulApiModelCallback<ProjectPublishResBean> callback) {
        this.callback = callback;
        httpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.ProjectPublishApi();
        params = api.newRequestParams(bean);
    }

    @Override
    public void doRequest(Context context) {
        String url = api.formatUrl(null);
        AsyncResponseHandlerComposite composite =
                newAsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                ProjectPublishResBean bean = JSON.parseObject(res, ProjectPublishResBean.class);
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

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }



    /**
     * 新建项目参数
     */
    public static final class ProjectData {

        private String projectName;

        private String province;

        private String city;

        private String qq;

        private String mobile;

        private int primaryProType = -1;

        private int secondaryProType = -1;

        private String projectState;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }


        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getPrimaryProType() {
            return primaryProType;
        }

        public void setPrimaryProType(int primaryProType) {
            this.primaryProType = primaryProType;
        }

        public int getSecondaryProType() {
            return secondaryProType;
        }

        public void setSecondaryProType(int secondaryProType) {
            this.secondaryProType = secondaryProType;
        }

        public String getProjectState() {
            return projectState;
        }

        public void setProjectState(String projectState) {
            this.projectState = projectState;
        }
    }

}
