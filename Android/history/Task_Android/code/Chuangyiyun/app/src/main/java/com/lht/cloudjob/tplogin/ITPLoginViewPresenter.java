package com.lht.cloudjob.tplogin;

/**
 * @ClassName: ITPLoginViewPresenter
 * @Description: TODO
 * @date 2016年3月16日 上午11:33:18
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface ITPLoginViewPresenter {

	void onTPPullUp();

	void onTPPullUpFinish();

	void onVarifyDecoded(TPLoginVerifyBean bean);

}
