package com.lht.creationspace.module.user.info;

import android.app.Activity;
import android.content.Context;

import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.info.model.ThirdAccountBindModel;
import com.lht.creationspace.module.user.info.model.ThirdInfoListModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.info.model.pojo.ThirdInfoListResBean;
import com.lht.creationspace.module.user.info.ui.IThirdAccountActivity;
import com.lht.creationspace.social.oauth.IOauthPresenter;
import com.lht.creationspace.social.oauth.QQOAuth;
import com.lht.creationspace.social.oauth.SinaOAuth;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.social.oauth.WeChatOAuth;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.IUiListener;

import java.util.Locale;

/**
 * Created by chhyu on 2017/2/22.
 */

public class ThirdAccountActivityPresenter
        implements IApiRequestPresenter, IOauthPresenter {

    private IThirdAccountActivity iThirdAccountActivity;

    public ThirdAccountActivityPresenter(IThirdAccountActivity iThirdAccountActivity) {
        this.iThirdAccountActivity = iThirdAccountActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }


    /**
     * 获取三方信息列表
     *
     * @param username
     */
    public void getThirdInfoList(String username) {
        iThirdAccountActivity.showWaitView(true);
        ThirdInfoListModel model = new ThirdInfoListModel(username,
                new ThirdInfoListModelCallback());
        model.doRequest(iThirdAccountActivity.getActivity());
    }

    private ThirdInfoListResBean thirdInfo;

    private class ThirdInfoListModelCallback
            implements ApiModelCallback<ThirdInfoListResBean> {
        @Override
        public void onSuccess(BaseBeanContainer<ThirdInfoListResBean> beanContainer) {
            thirdInfo = beanContainer.getData();
            iThirdAccountActivity.updateBindStateDisplay(thirdInfo);
            iThirdAccountActivity.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            thirdInfo = new ThirdInfoListResBean();
            if (beanContainer.getData().getRet() == ThirdInfoListModel.RET_NULL_AUTH_RECORD) {
                iThirdAccountActivity.updateBindStateDisplay(thirdInfo);
            } else {
                iThirdAccountActivity.showMsg(beanContainer.getData().getMessage());
            }
            iThirdAccountActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            thirdInfo = new ThirdInfoListResBean();
            iThirdAccountActivity.cancelWaitView();
        }
    }

    /**
     * 绑定QQ
     *
     * @param qqLoginListener
     */
    public void callBindQqAcccount(Activity activity, IUiListener qqLoginListener) {
        if (thirdInfo.getQq() == null
                || StringUtil.isEmpty(thirdInfo.getQq().getOauth_uid())) {
            iThirdAccountActivity.showWaitView(true);
            QQOAuth.login(activity, qqLoginListener, this);
        } else {
            // TODO: 2017/3/21  need toast? or show info
        }
    }

    /**
     * 绑定微博
     *
     * @param activity
     * @param sinaSsoHandler
     */
    public void callBindSinaAcccount(BaseActivity activity,
                                     SsoHandler sinaSsoHandler) {
        if (thirdInfo.getWeibo() == null
                || StringUtil.isEmpty(thirdInfo.getWeibo().getOauth_uid())) {
            iThirdAccountActivity.showWaitView(true);
            SinaOAuth.login(activity, sinaSsoHandler, this);
        } else {
            // TODO: 2017/3/21  need toast? or show info
        }
    }

    /**
     * 绑定微信
     *
     * @param activity
     */
    public void callBindWeChatAcccount(BaseActivity activity) {
        if (thirdInfo.getWeixin() == null
                || StringUtil.isEmpty(thirdInfo.getWeixin().getOauth_uid())) {
            iThirdAccountActivity.showWaitView(true);
            WeChatOAuth.login(activity, this);
        } else {
            // TODO: 2017/3/21   need toast? or show info
        }
    }

    @Override
    public void onTPPullUp() {
        iThirdAccountActivity.showWaitView(true);
    }

    @Override
    public void onTPPullUpFinish() {
        iThirdAccountActivity.cancelWaitView();
    }

    @Override
    public void onOauthFinish(TPOauthUserBean bean) {
        if (!bean.isSuccess()) { //三方授权失败
            iThirdAccountActivity.cancelWaitView();
        } else {
            iThirdAccountActivity.showWaitView(true);
            doBind(bean);
        }
    }

    private void doBind(TPOauthUserBean bean) {
        ThirdAccountBindModel model = new ThirdAccountBindModel(IVerifyHolder.mLoginInfo.getUsername(),
                bean.getType(), bean.getUniqueId(), new ThirdAccountBindModelCallback(bean.getType()));
        model.doRequest(iThirdAccountActivity.getActivity());
    }

    @Override
    public void onUserInfoGet(TPOauthUserBean bean) {

    }

    private class ThirdAccountBindModelCallback
            implements ApiModelCallback<BaseVsoApiResBean> {
        private int type;

        public ThirdAccountBindModelCallback(int type) {
            this.type = type;
        }

        private String getFormatType() {
            String ret;
            switch (type) {
                case TPOauthUserBean.TYPE_QQ:
                    ret = "QQ";
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    ret = "微博";
                    break;
                case TPOauthUserBean.TYPE_WECHAT: //as default
                default:
                    ret = "微信";
                    break;

            }
            return ret;
        }

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iThirdAccountActivity.cancelWaitView();
            iThirdAccountActivity.showMsg(beanContainer.getData().getMessage());
            iThirdAccountActivity.updateView(type);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iThirdAccountActivity.cancelWaitView();
            final int CODE_CONFLICT = 14242;
            if (beanContainer.getData().getRet() == CODE_CONFLICT) {
                String _t = "该%s号已被绑定";
                iThirdAccountActivity.showMsg(String.format(Locale.CHINESE, _t, getFormatType()));
            } else {
                iThirdAccountActivity.showMsg(beanContainer.getData().getMessage());
            }
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iThirdAccountActivity.cancelWaitView();
        }
    }
}
