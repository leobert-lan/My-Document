package com.lht.creationspace.social.oauth;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.util.debug.DLog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * <p><b>Package</b> com.lht.creationspace.tplogin
 * <p><b>Project</b> czspace
 * <p><b>Classname</b> QQUserQueryCallback
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/27.
 */

public class QQUserQueryCallback implements IUiListener {

    private IOauthPresenter mLoginViewPresenter;

    private final TPOauthUserBean savedBean;

    public QQUserQueryCallback(TPOauthUserBean savedBean, IOauthPresenter presenter) {
        this.mLoginViewPresenter = presenter;
        this.savedBean = savedBean;
    }

    @Override
    public void onError(UiError arg0) {
        cancelWait();
        DLog.d(getClass(), "on error:" + JSON.toJSONString(arg0));
    }

    @Override
    public void onComplete(Object response) {
        cancelWait();

        if (null == response) {
            DLog.e(getClass(), "qq login info:\r\n null");
            mLoginViewPresenter.onUserInfoGet(savedBean);
            return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (jsonResponse.length() == 0) {
            DLog.e(getClass(), "qq login info:\r\n 返回为空");
            mLoginViewPresenter.onUserInfoGet(savedBean);
            return;
        }

        try {
            String nickname = jsonResponse.getString(QQConstants.QQ_PARAM_NICKNAME);
            String avatar = jsonResponse.getString(QQConstants.QQ_PARAM_AVATAR);
            savedBean.setNickname(nickname);
            savedBean.setAvatar(avatar);
            DLog.i(getClass(), "check decode:\r\n" + JSON.toJSONString(savedBean));
            mLoginViewPresenter.onUserInfoGet(savedBean);
        } catch (Exception e) {
            mLoginViewPresenter.onUserInfoGet(savedBean);
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
