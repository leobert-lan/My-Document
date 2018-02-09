package com.lht.creationspace.social.oauth;

/**
 * @ClassName: IWechatOAuthCallback
 * @Description: TODO
 * @date 2016年5月30日 下午1:16:11
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IWechatOAuthCallback {

	void onFinish(TPOauthUserBean bean);

}
