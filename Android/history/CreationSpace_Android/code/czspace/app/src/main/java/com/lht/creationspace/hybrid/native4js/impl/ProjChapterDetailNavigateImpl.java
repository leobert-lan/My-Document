package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_ProjChapterDetailNavigateReqBean;
import com.lht.creationspace.module.projchapter.ui.HybridProjChapterDetailActivity;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;


/**
 * 项目更新详情页面跳转桥接实现
 */
public class ProjChapterDetailNavigateImpl
        extends ABSApiImpl<NF_ProjChapterDetailNavigateReqBean>
        implements Native4JsExpandAPI.ProjChapterDetailNavigateHandler {

    public ProjChapterDetailNavigateImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_ProjChapterDetailNavigateReqBean bean =
                    JSON.parseObject(s, NF_ProjChapterDetailNavigateReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.d(getClass(), "跳转参数异常：" + s);
            } else {
                HybridProjChapterDetailActivity.getLauncher(mContext)
                        .injectData(transData(bean))
                        .launch();
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newSuccessResBean()));
            }

        } catch (Exception e) {
            if (callBackFunction != null)
                callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
        }


    }

    private
    /**/HybridProjChapterDetailActivity.LaunchData
    transData(NF_ProjChapterDetailNavigateReqBean bean) {
        HybridProjChapterDetailActivity.LaunchData data =
                new HybridProjChapterDetailActivity.LaunchData();
        data.setUrl(bean.getUrl());
        data.setTitle(bean.getTitle());
        data.setOid(bean.getOid());
        return data;
    }


    /*for test*/
//    public
    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newFailureResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }


    @Override
    protected boolean isBeanError(NF_ProjChapterDetailNavigateReqBean bean) {
        if (StringUtil.isEmpty(bean.getUrl())
                || StringUtil.isEmpty(bean.getOid()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new ProjChapterDetailNavigateImpl(context));
    }
}
