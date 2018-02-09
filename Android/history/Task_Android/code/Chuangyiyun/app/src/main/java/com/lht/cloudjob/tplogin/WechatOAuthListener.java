package com.lht.cloudjob.tplogin;


import com.lht.cloudjob.util.debug.DLog;

/**
 * @ClassName: WechatOAuthListener
 * @Description: TODO
 * @date 2016年5月30日 下午1:21:49
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class WechatOAuthListener implements IWechatOAuthCallback {

	private ITPLoginViewPresenter mLoginViewPresenter;

	public WechatOAuthListener(ITPLoginViewPresenter presenter) {
		this.mLoginViewPresenter = presenter;
	}

	@Override
	public void onFinish(TPLoginVerifyBean bean) {
		DLog.e(getClass(), "wechat onfinish");
		mLoginViewPresenter.onTPPullUpFinish();
		mLoginViewPresenter.onVarifyDecoded(bean);
	}

}
