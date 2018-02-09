package com.lht.creationspace.hybrid.native4js.impl;


import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_ProjArticlePublishNavigateReqBean;
import com.lht.creationspace.module.proj.ui.ProjChapterPublishActivity;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;


/**
 * <p><b>Package:</b> com.lht.creationspace.hybrid.native4js.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> PublishProjArticleRedirectImpl </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/7/26.
 */
public class PublishProjChapterNavigateImpl extends ABSApiImpl<NF_ProjArticlePublishNavigateReqBean>
        implements Native4JsExpandAPI.PublishProjChapterNavigateHandler {

    public PublishProjChapterNavigateImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_ProjArticlePublishNavigateReqBean bean =
                    JSON.parseObject(s, NF_ProjArticlePublishNavigateReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.d(getClass(), "跳转参数异常：" + s);
            } else {

                ProjChapterPublishActivity.getLauncher(mContext)
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

    private ProjChapterPublishActivity.LaunchData
    transData(NF_ProjArticlePublishNavigateReqBean bean) {
        ProjChapterPublishActivity.LaunchData data =
                new ProjChapterPublishActivity.LaunchData();
        data.setProjectId(bean.getOid());
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
    protected boolean isBeanError(NF_ProjArticlePublishNavigateReqBean bean) {
//        NF_GeneralRedirectReqBean bean = (NF_GeneralRedirectReqBean) o;
        if (StringUtil.isEmpty(bean.getOid()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new PublishProjChapterNavigateImpl(context));
    }
}