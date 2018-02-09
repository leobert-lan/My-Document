package com.lht.creationspace.social.oauth;

import android.app.Activity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.module.user.security.model.pojo.WeChatCodeBean;
import com.lht.creationspace.module.user.security.model.pojo.WeChatUserInfoBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.apache.http.Header;

import java.util.HashMap;

/**
 * @author leobert.lan
 * @version 1.0
 * @ClassName: WeChatOAuth
 * @Description: TODO
 * @date 2016年3月16日 上午11:20:23
 */
public class WeChatOAuth implements IWXAPIEventHandler {

    private static HashMap<String, IWechatOAuthCallback> callbacks = new HashMap<>();

    public static void login(final Activity activity, IOauthPresenter presenter) {
//        微信先不处理进度条，防止微信未登录、关闭，home等问题
//        if (presenter != null)
//            presenter.onTPPullUp();
        loginWithWeixin(activity, new WechatOAuthListener(presenter));
    }

    private static void loginWithWeixin(final Activity activity, IWechatOAuthCallback callback) {

        IWXAPI api = MainApplication.getWechat();
        if (!api.isWXAppInstalled()) {
            Toast.makeText(activity, activity.getString(R.string.v1010_default_tp_failure_wxnotinstalled), Toast.LENGTH_SHORT).show();
            TPOauthUserBean bean = new TPOauthUserBean();
            bean.setType(TPOauthUserBean.TYPE_WECHAT);
            callback.onFinish(bean);
            return;
        }
        api.registerApp(WeChatConstants.APP_ID);

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = WeChatConstants.SCOPE;
        req.state = WeChatConstants.STATE;

        // TODO 业务
        String transaction = buildTransaction("login");
        req.transaction = transaction;
        callbacks.put(transaction, callback);
        DLog.d(DLog.Lmsg.class, "send wechat oath req");
        api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onReq(BaseReq req) {
        DLog.d(DLog.Lmsg.class, "on wechat req:" + JSON.toJSONString(req));
    }

    @Override
    public void onResp(BaseResp resp) {
        String weChatCode;

        DLog.d(DLog.Lmsg.class, "on wechat response:\r\n" + JSON.toJSONString(resp));

        String result;
        // TODO 区分业务
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    result = "授权成功";
                    weChatCode = ((SendAuth.Resp) resp).code;
                    IWechatOAuthCallback callback = callbacks.get(resp.transaction);
                    getTokenAndId(weChatCode, callback);
                    callbacks.remove(resp.transaction);
                    DLog.d(getClass(), "util 登录时成功的回调");
                } else {
                    result = "成功";
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                onError(resp);
                result = "取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                onError(resp);
                result = "发生错误";
                break;
            default:
                onError(resp);
                result = "发生未知错误";
                break;
        }

        Toast.makeText(MainApplication.getOurInstance(), result, Toast.LENGTH_LONG).show();
    }

    private void onError(BaseResp resp) {
        DLog.e(getClass(), "resp type:" + resp.getType());

        if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            IWechatOAuthCallback callback = callbacks.get(resp.transaction);
            callbacks.remove(resp.transaction);
            TPOauthUserBean bean = new TPOauthUserBean();
            bean.setType(TPOauthUserBean.TYPE_WECHAT);
            bean.setSuccess(false);
            callback.onFinish(bean);
        }
    }

    /**
     * @param callback
     * @Title: getTokenAndId
     * @Description: 刷新token和id
     * @author: leobert.lan
     */
    private void getTokenAndId(String weChatCode, final IWechatOAuthCallback callback) {
        HttpUtil httpUtil = HttpUtil.getInstance();

        String codeUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatConstants.APP_ID
                + "&secret=" + WeChatConstants.APP_SECRET + "&code=" + weChatCode + "&grant_type=authorization_code";

        httpUtil.get(codeUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                TPOauthUserBean bean = new TPOauthUserBean();
                bean.setType(TPOauthUserBean.TYPE_WECHAT);
                bean.setSuccess(false);
                callback.onFinish(bean);
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String codeData = new String(arg2);
                WeChatCodeBean weChatCodeBean = JSON.parseObject(codeData, WeChatCodeBean.class);
//                String access_token = weChatCodeBean.getAccess_token();
                String uniqueId = weChatCodeBean.getUnionid();
                // 回调登录后续
                TPOauthUserBean bean = new TPOauthUserBean();
                bean.setType(TPOauthUserBean.TYPE_WECHAT);
                bean.setSuccess(true);
                bean.setUniqueId(uniqueId);
                bean.setWeChatCodeBean(weChatCodeBean);
                callback.onFinish(bean);

//                // get user's information
//                getUserInfo(access_token, weChatCodeBean.getOpenid(), bean, callback);
            }
        });
    }

    public static void getUserInfo(TPOauthUserBean bean, IOauthPresenter callback) {
        final WeChatUserQueryCallback userQueryCallback = new WeChatUserQueryCallback(bean, callback);
        WeChatCodeBean codeBean = bean.getWeChatCodeBean();

        HttpUtil httpUtil = HttpUtil.getInstance();

        String access_token_Url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + codeBean.getAccess_token()
                + "&openid=" + codeBean.getOpenid();
        httpUtil.get(access_token_Url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                String userInfo = new String(arg2);
                WeChatUserInfoBean weChatUserInfoBean = JSON.parseObject(userInfo, WeChatUserInfoBean.class);
                userQueryCallback.onFinish(weChatUserInfoBean);
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                // Log.e(TAG, "http failure");
                userQueryCallback.onFinish(new WeChatUserInfoBean());
            }
        });
    }

}
