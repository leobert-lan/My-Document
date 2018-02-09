package com.lht.creationspace.social.oauth;

/**
 * @ClassName: ITPLoginViewPresenter
 * @Description: TODO
 * @date 2016年3月16日 上午11:33:18
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IOauthPresenter {

    /**
     * 三方授权拉起
     */
    void onTPPullUp();

    /**
     * 三方收取收起
     */
    void onTPPullUpFinish();

    /**
     * 三方授权结束
     * @param bean
     */
    void onOauthFinish(TPOauthUserBean bean);

    /**
     * 三方信息获取结束
     * @param bean
     */
    void onUserInfoGet(TPOauthUserBean bean);

}
