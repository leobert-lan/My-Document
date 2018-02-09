package com.lht.cloudjob.mvp;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.net.IPagedApiRequestModel;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.bean.DemandWorksResBean;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandWorksListModel
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/29
 */
public class DemandWorksListModel implements IPagedApiRequestModel {

    private final ApiModelCallback<DemandWorksResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private IRestfulApi.DemandWorksListApi api;

    private RequestParams params;

    private RequestHandle handle;

    private final String taskBn;


    public DemandWorksListModel(String taskBn, ApiModelCallback<DemandWorksResBean>
            modelCallback) {
        this.taskBn = taskBn;
        this.modelCallback = modelCallback;
        mHttpUtil = HttpUtil.getInstance();
        api = new IRestfulApi.DemandWorksListApi();
    }

    @Override
    public void setParams(String usr, int offset) {
        params = api.newRequestParams(usr, taskBn, offset);
    }

    @Override
    public void setParams(String usr, int offset, int limit) {
        params = api.newRequestParams(usr, taskBn, offset, limit);
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
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.GET, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
                if (bean.isSuccess()) {
                    DemandWorksResBean data = JSON.parseObject(bean.getData(), DemandWorksResBean.class);
//                    ArrayList<DemandInfoResBean.Work> data =
//                            (ArrayList<DemandInfoResBean.Work>) JSON.parseArray(bean.getData(),
//                                    DemandInfoResBean.Work.class);
                    modelCallback.onSuccess(new BaseBeanContainer<>(data));
                } else {
                    modelCallback.onFailure(new BaseBeanContainer<>(bean));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                modelCallback.onHttpFailure(i);
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


}
