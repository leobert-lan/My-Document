package com.lht.cloudjob.native4js.impl;

import android.app.Activity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.customview.TPSPWCreater;
import com.lht.cloudjob.customview.ThirdPartyShareItemClickListenerImpl;
import com.lht.cloudjob.customview.ThirdPartySharePopWins;
import com.lht.cloudjob.interfaces.custompopupwins.IPopupHolder;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.native4js.expandresbean.NF_TPShareReqBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package</b> com.lht.cloudjob.native4js.impl
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> VsoTPShareImpl
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/9.
 */

public class VsoTPShareImpl extends ABSApiImpl implements Native4JsExpandAPI.TPShareHandler {
    private WeakReference<IPopupHolder> popupHolderRef;

    public VsoTPShareImpl(IPopupHolder iPopupHolder) {
        popupHolderRef = new WeakReference<>(iPopupHolder);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        IPopupHolder holder = getDelegatePopupHolder();
        NF_TPShareReqBean reqBean;
        try {
            reqBean = JSON.parseObject(s, NF_TPShareReqBean.class);
        } catch (Exception e) {
            BaseResponseBean<String> resBean = newFailureResBean(0, "");
            callBackFunction.onCallBack(JSON.toJSONString(resBean));
            return;
        }

        if (isBeanError(reqBean)) {
            BaseResponseBean<String> resBean = newFailureResBean(0, "");
            callBackFunction.onCallBack(JSON.toJSONString(resBean));
            return;
        }

        if (holder == null || holder.getHolderActivity() == null) {
            BaseResponseBean<String> resBean = newFailureResBean(0, "");
            callBackFunction.onCallBack(JSON.toJSONString(resBean));
        } else {
            callShare(holder, reqBean, callBackFunction);
        }
    }

    private IPopupHolder getDelegatePopupHolder() {
        return popupHolderRef.get();
    }


    /*for test*/
//    public
    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    private void callShare(IPopupHolder iPopupHolder, NF_TPShareReqBean reqBean, final CallBackFunction callBackFunction) {
        ShareCloumnItemClickListener l =
                new ShareCloumnItemClickListener(iPopupHolder.getHolderActivity(), callBackFunction);
        l.setTitle(reqBean.getTitle());
        l.setSummary(reqBean.getSummary());


        ThirdPartySharePopWins wins = TPSPWCreater.create(iPopupHolder);
        wins.setShareContent(reqBean.getUrl());
        wins.removeItem(wins.getItemCount() - 1);

        wins.setOnThirdPartyShareItemClickListener(l);

        wins.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseResponseBean<String> resBean = newFailureResBean(0, "");
                callBackFunction.onCallBack(JSON.toJSONString(resBean));
            }
        });
        wins.show();

    }

    private class ShareCloumnItemClickListener extends ThirdPartyShareItemClickListenerImpl {

        private final CallBackFunction callBackFunction;

        ShareCloumnItemClickListener(Activity activity, CallBackFunction callBackFunction) {
            super(activity);
            this.callBackFunction = callBackFunction;
        }

        @Override
        public void onClick(ThirdPartySharePopWins popWins, int itemIndex, View item) {
            super.onClick(popWins, itemIndex, item);

            BaseResponseBean responseBean = newSuccessResBean();
            if (callBackFunction != null) {
                callBackFunction.onCallBack(JSON.toJSONString(responseBean));
            }
        }
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newFailureResBean(int ret, String msg) {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        return bean;
    }


    @Override
    protected boolean isBeanError(Object o) {
        //ignore
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(IPopupHolder iPopupHolder) {
        return new BridgeNativeFunction(API_NAME, new VsoTPShareImpl(iPopupHolder));
    }
}
