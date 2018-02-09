package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.innerweb.SignAgreementActivity;
import com.lht.cloudjob.clazz.customerror.IllegalArgsError;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.ReadAgreementModel;
import com.lht.cloudjob.mvp.model.SignAgreementModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.ReadAgreementResBean;
import com.lht.cloudjob.mvp.viewinterface.ISignAgreementActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.numeric.NumericalUtils;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SignAgreementPresenter
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/20.
 */

public class SignAgreementPresenter implements IApiRequestPresenter {

    private final ISignAgreementActivity iSignAgreementActivity;
    //是否同意协议
    private boolean isProtocolAgreed = false;

    public SignAgreementPresenter(ISignAgreementActivity iSignAgreementActivity) {
        this.iSignAgreementActivity = iSignAgreementActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void setIsProtocolAgreed(boolean isProtocolAgreed) {
        this.isProtocolAgreed = isProtocolAgreed;
    }


    /**
     * 签署协议
     *
     * @param undertakeType 1=悬赏  2=明标  3=暗标
     * @param task_bn       需求编号
     * @param username      user
     * @param flag_agree    是否同意签署协议
     */
    public void callSignAgreement(int undertakeType, String task_bn, String username, boolean flag_agree) {
        if (flag_agree) { //签署协议-同意
            if (isProtocolAgreed) { //用户协议
                if (undertakeType == SignAgreementActivity.TYPE_REWARD) {
                    //悬赏,直接签署协议
                    doSignAgreement(task_bn, username, true);
                } else if (undertakeType == SignAgreementActivity.TYPE_OPENBID) {
                    //明标，弹出对话框
                    loadConfirmSignDialog(task_bn, username, true);
                } else if (undertakeType == SignAgreementActivity.TYPE_HIDEBID) {
                    // 暗标，直接签署协议
                    doSignAgreement(task_bn, username, true);
                } else { //代码异常
                    String errorMsg = "sign agreement type error,type:" + undertakeType;
                    DLog.e(getClass(), errorMsg);
                    IllegalArgsError error = new IllegalArgsError(errorMsg);
                    error.report(iSignAgreementActivity.getActivity());
                    iSignAgreementActivity.showErrorMsg(iSignAgreementActivity
                            .getAppResource().getString(R.string.v1010_default_sign_agreement_failure));
                }
            } else { //请阅读用户协议并确认
                iSignAgreementActivity.showErrorMsg(iSignAgreementActivity
                        .getAppResource().getString(R.string.v1010_default_sign_agreement_toast_read_agreement));
            }
        } else { //签署协议-不同意
            doSignAgreement(task_bn, username, false);
        }
    }

    /**
     * 弹出对话框，提示签约的最终金额
     *
     * @param task_bn
     * @param username
     * @param flag_agree
     */
    private void loadConfirmSignDialog(final String task_bn, final String username, final boolean flag_agree) {
        loadAgreementData(task_bn, username, flag_agree);
    }


    /**
     * 签署协议
     *
     * @param task_bn    需求编号
     * @param username   乙方
     * @param flag_agree 是否同意
     */
    private void doSignAgreement(String task_bn, String username, boolean flag_agree) {
        SignAgreementModel model = new SignAgreementModel(task_bn, username, flag_agree,
                new SignAgreementModelCallback());
        model.doRequest(iSignAgreementActivity.getActivity());
    }

    /**
     * 签署协议之后的回调，只区分是否成功；
     * 同意和不同意都一样提示
     */
    private class SignAgreementModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

//        private final boolean needToastOnSuccess;
//
//        public SignAgreementModelCallback(boolean needToastOnSuccess) {
//            this.needToastOnSuccess = needToastOnSuccess;
//        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSignAgreementActivity.cancelWaitView();
            iSignAgreementActivity.showMsg(iSignAgreementActivity.getActivity().getString(R.string.v1010_default_sign_agreement_success));
//            EventBus.getDefault().post(new AppEvent.ProtocolSignOnCompleteEvent());  ignore
            iSignAgreementActivity.finishActivity();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSignAgreementActivity.cancelWaitView();
            iSignAgreementActivity.showErrorMsg(iSignAgreementActivity.getActivity().getString(R.string.v1010_default_sign_agreement_failure));
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSignAgreementActivity.cancelWaitView();
        }
    }


    private void loadAgreementData(String task_bn, String username, boolean flag_agree) {
        iSignAgreementActivity.showWaitView(true);
        ReadAgreementModel model = new ReadAgreementModel(task_bn, username, new ReadAgreementCallback(task_bn, username, flag_agree));
        model.doRequest(iSignAgreementActivity.getActivity());
    }

    private class ReadAgreementCallback implements ApiModelCallback<ReadAgreementResBean> {

        private final String task_bn;
        private final String username;
        private final boolean flag_agree;

        ReadAgreementCallback(String task_bn, String username, boolean flag_agree) {
            this.task_bn = task_bn;
            this.username = username;
            this.flag_agree = flag_agree;
        }

        @Override
        public void onSuccess(BaseBeanContainer<ReadAgreementResBean> beanContainer) {
            iSignAgreementActivity.cancelWaitView();
            ReadAgreementResBean data = beanContainer.getData();
            double real_cash = data.getReal_cash();

            //显示价格二次确认对话框
            iSignAgreementActivity.showConfirmDialog(NumericalUtils.decimalFormat(real_cash,
                    NumericalUtils.SplitMode.THOUSAND, 2),
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            //确认签署
                            doSignAgreement(task_bn, username, flag_agree);
                        }
                    });
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSignAgreementActivity.cancelWaitView();
            iSignAgreementActivity.showMsg(iSignAgreementActivity.getAppResource().getString(R.string.v1041_toast_undertake_retry));
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSignAgreementActivity.cancelWaitView();
            //ignore
        }
    }
}
