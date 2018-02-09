package com.lht.pan_android.mpv.presenter;

import com.lht.pan_android.mpv.viewinterface.ILoginActivityView;
import com.lht.pan_android.util.thirdPartyLogin.TPLoginVerifyBean;

/**
 * @ClassName: LoginActivityPresenter
 * @Description: TODO
 * @date 2016年5月26日 上午9:45:21
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class LoginActivityPresenter implements INewLoginActivityPresenter {

	private final ILoginActivityView iView;

	public LoginActivityPresenter(ILoginActivityView iView) {
		this.iView = iView;
	}

	@Override
	public void doLogin(String usr, String pwd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTPLogin(TPLoginVerifyBean bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub 取消所有的请求
		
		
	}

}

interface INewLoginActivityPresenter {
	void doLogin(String usr, String pwd);

	void doTPLogin(TPLoginVerifyBean bean);
	
	void destroy();

}
