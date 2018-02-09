package com.lht.creationspace.hybrid.native4js.impl;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.module.msg.ui.HybridObjCommentListActivity;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_GeneralNavigateReqBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * 对文章、项目等评价的列表页面
 * Created by leobert on 2017/3/1.
 */

public class CommentListNavigateImpl extends ABSApiImpl<NF_GeneralNavigateReqBean>
        implements Native4JsExpandAPI.CommentListNavigateHandler {

    public CommentListNavigateImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        try {
            NF_GeneralNavigateReqBean bean = JSON.parseObject(s, NF_GeneralNavigateReqBean.class);
            boolean b = isBeanError(bean);
            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.d(getClass(), "跳转参数异常：" + s);
            } else {
//                Intent intent = new Intent(mContext, HybridObjCommentListActivity.class);
//                intent.putExtra(HybridObjCommentListActivity.KEY_DATA, bean.getUrl());
//                mContext.startActivity(intent);

                HybridObjCommentListActivity.getLauncher(mContext)
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

    private HybridObjCommentListActivity.HybridObjCommentListActivityData transData(NF_GeneralNavigateReqBean bean) {
        HybridObjCommentListActivity.HybridObjCommentListActivityData data = new HybridObjCommentListActivity.HybridObjCommentListActivityData();
        data.setUrl(bean.getUrl());
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
    protected boolean isBeanError(NF_GeneralNavigateReqBean bean) {
//        NF_GeneralRedirectReqBean bean = (NF_GeneralRedirectReqBean) o;
        if (StringUtil.isEmpty(bean.getUrl()))
            return BEAN_IS_ERROR;
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new CommentListNavigateImpl(context));
    }
}
