package com.lht.creationspace.hybrid.native4js.impl;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.customview.popup.PopupInputWin;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_InputReqBean;
import com.lht.creationspace.hybrid.native4js.expandresbean.NF_InputResBean;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

/**
 * <p><b>Package</b> com.lht.vsocyy.native4js.impl
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> InputIntentImpl
 * <p><b>Description</b>: 输入意图处理者
 * <p>Created by leobert on 2016/12/1.
 */

public class InputIntentImpl extends ABSApiImpl implements
        Native4JsExpandAPI.InputIntentHandler {

    private IPopupHolder iPopupHolder;

    public InputIntentImpl(IPopupHolder iPopupHolder) {
        this.iPopupHolder = iPopupHolder;
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        NF_InputReqBean reqBean = new NF_InputReqBean();

        PopupInputWin inputWin = new PopupInputWin(iPopupHolder,
                new InputCallback(callBackFunction, reqBean));
        inputWin.setInputMaxLength(reqBean.getMax_length());

        //暂不使用扩展数据
//        try {
//            reqBean = JSON.parseObject(s, NF_InputReqBean.class);
//
//            inputWin.overrideCancelText(reqBean.getText_cancel());
//            inputWin.overrideHint(reqBean.getText_hint());
//            inputWin.overrideSubmitText(reqBean.getText_submit());
//            inputWin.overrideTitle(reqBean.getText_title());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        inputWin.show();
    }


    /*for test*/
//    public
    private BaseResponseBean<NF_InputResBean> newSuccessResBean(NF_InputResBean data) {
        BaseResponseBean<NF_InputResBean> bean = new BaseResponseBean<>();
        bean.setData(data);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<NF_InputResBean> newFailureResBean(int ret, String msg) {
        BaseResponseBean<NF_InputResBean> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        bean.setData(new NF_InputResBean()); // give an empty object rather than null
        return bean;
    }


    @Override
    protected boolean isBeanError(Object o) {
        return BEAN_IS_CORRECT;
    }

    public static BridgeNativeFunction newInstance(IPopupHolder iPopupHolder) {
        return new BridgeNativeFunction(API_NAME, new InputIntentImpl(iPopupHolder));
    }

    private class InputCallback extends PopupInputWin.AbsInputCallback {

        private final CallBackFunction webCallback;

        private final NF_InputReqBean reqBean;

        public InputCallback(CallBackFunction webCallback, NF_InputReqBean reqBean) {
            this.webCallback = webCallback;
            this.reqBean = reqBean;
        }

        @Override
        public void onInputCancel() {
            BaseResponseBean<NF_InputResBean> res = newFailureResBean(0, "cancel");
            webCallback.onCallBack(JSON.toJSONString(res));
        }

        @Override
        public void onInputInterrupt() {
            // TODO: 2017/3/6
        }

        @Override
        public void onInputSubmit(String textInput) {
            if (StringUtil.isEmpty(textInput) || StringUtil.isEmpty(textInput.trim())) {
                BaseResponseBean<NF_InputResBean> res = newFailureResBean(0, "empty input");
                webCallback.onCallBack(JSON.toJSONString(res));
                return;
            }

            NF_InputResBean data = new NF_InputResBean();
            data.setText_input(textInput);
            BaseResponseBean<NF_InputResBean> res = newSuccessResBean(data);
            webCallback.onCallBack(JSON.toJSONString(res));
        }
    }
}
