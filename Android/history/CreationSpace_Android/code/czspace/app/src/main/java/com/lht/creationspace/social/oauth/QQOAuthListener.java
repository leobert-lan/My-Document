package com.lht.creationspace.social.oauth;

import android.text.TextUtils;

import com.lht.creationspace.util.debug.DLog;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: QQOAuthListener
 * @Description: TODO
 * @date 2016年3月16日 上午11:01:08
 */
public class QQOAuthListener implements IUiListener {

    private Tencent mTencent;

    private IOauthPresenter mLoginViewPresenter;

    public QQOAuthListener(Tencent tencent, IOauthPresenter presenter) {
        mTencent = tencent;
        this.mLoginViewPresenter = presenter;
    }

    @Override
    public void onError(UiError arg0) {
        cancelWait();
        DLog.d(getClass(), "on error");
    }

    @Override
    public void onComplete(Object response) {
        cancelWait();

        TPOauthUserBean bean = new TPOauthUserBean();
        bean.setType(TPOauthUserBean.TYPE_QQ);

        if (null == response) {
            DLog.e(getClass(), "qq login info:\r\n null");
            bean.setSuccess(false);
            mLoginViewPresenter.onOauthFinish(bean);
            return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (jsonResponse.length() == 0) {
            DLog.e(getClass(), "qq login info:\r\n 返回为空");
            bean.setSuccess(false);
            mLoginViewPresenter.onOauthFinish(bean);
            return;
        }

        try {
            String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonResponse.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                bean.setSuccess(true);
                bean.setUniqueId(openId);
                mLoginViewPresenter.onOauthFinish(bean);
            }

            DLog.d(getClass(), "qq login info:\r\n" + "token:" + token + "\r\nexpires:" + expires + "\r\nopenid:" + openId
                    + "\r\ntotal length" + jsonResponse.length() + "\r\n content:" + jsonResponse.toString());

        } catch (Exception e) {
            bean.setSuccess(false);
            DLog.e(getClass(), "qq login exception");
            mLoginViewPresenter.onOauthFinish(bean);
        }

    }

    @Override
    public void onCancel() {
        DLog.d(getClass(), "qq login oncancel");
        cancelWait();
    }

    private void cancelWait() {
        mLoginViewPresenter.onTPPullUpFinish();
    }

}
