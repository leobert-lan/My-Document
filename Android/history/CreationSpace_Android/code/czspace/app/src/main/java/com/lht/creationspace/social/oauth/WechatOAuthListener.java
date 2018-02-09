package com.lht.creationspace.social.oauth;


import com.lht.creationspace.util.debug.DLog;

/**
 * @ClassName: WechatOAuthListener
 * @Description: TODO
 * @date 2016年5月30日 下午1:21:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class WechatOAuthListener implements IWechatOAuthCallback {

	private IOauthPresenter mLoginViewPresenter;

	public WechatOAuthListener(IOauthPresenter presenter) {
		this.mLoginViewPresenter = presenter;
	}

	@Override
	public void onFinish(TPOauthUserBean bean) {
		DLog.d(getClass(), "wechat onfinish");
		mLoginViewPresenter.onTPPullUpFinish();
		mLoginViewPresenter.onOauthFinish(bean);
	}

}
