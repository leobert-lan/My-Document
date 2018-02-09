package com.lht.pan_android.mpv.presenter;

import java.util.regex.Pattern;

import com.lht.pan_android.mpv.model.ITextWatcher;
import com.lht.pan_android.mpv.model.TextWatcherModel;
import com.lht.pan_android.mpv.model.TimerClockModel;
import com.lht.pan_android.mpv.model.TimerClockModel.ISharedPreferenceProvider;
import com.lht.pan_android.mpv.model.TimerClockModel.OnTimeLapseListener;
import com.lht.pan_android.mpv.viewinterface.IRegisterActiviterView;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.EditText;

/**
 * @ClassName: RegisterActivityPresenter
 * @Description: TODO
 * @date 2016年5月26日 上午9:45:53
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class RegisterActivityPresenter {

	private IRegisterActiviterView iView;

	private ITextWatcher textWatcherModel;

	private TimerClockModel timerClockModel;

	private Resources res;

	public RegisterActivityPresenter(IRegisterActiviterView view) {
		this.iView = view;
		res = iView.getRes();
		textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
		timerClockModel = new TimerClockModel(new SharedPreferenceProviderImpl(), new TimeLapseListenerImpl());

	}

	public void resumeTimer() {
		timerClockModel.getTimeClock(1000).start();
	}

	public void updateTimer() {
		timerClockModel.updateTimeStamp();
	}

	public void callTextWatcherOnThis(EditText editText, int maxLength) {
		textWatcherModel.doWatcher(editText, maxLength);
	}

	public boolean checkPhone(String phoneNum) {
		// 错误提示
		String reg = "^[0-9]+$";
		if (!Pattern.matches(reg, phoneNum)) {
			iView.showErrorMsg("");
			return false;
		}
		if (phoneNum.length() != 11) {
			iView.showErrorMsg("");
			return false;
		}
		return true;
	}

	public void callRegister(String phoneNum, String verifycode, String pwd) {
		// TODO
	}

	public boolean matchPwd(String pwd, int min, int max) {
		String reg = "^[A-Za-z0-9]+$";
		boolean isIllegelChars = !Pattern.matches(reg, pwd);
		if (isIllegelChars) {
			// TODO
			iView.showErrorMsg("");
			return false;
		}
		if (pwd.length() < min) {
			// TODO
			iView.showErrorMsg("");
			return false;
		}

		if (pwd.length() > max) {
			// TODO
			iView.showErrorMsg("");
			return false;
		}
		return true;
	}

	private class TextWatcherModelCallbackImpl implements TextWatcherModel.TextWatcherModelCallback {

		@Override
		public void onOverLength(int edittextId, int maxLength) {

		}

		@Override
		public void onChanged(int edittextId, int currentCount, int remains) {

		}

		@Override
		public void onShort(int edittextId, int minLength) {
			// TODO Auto-generated method stub

		}
	}

	private final class TimeLapseListenerImpl implements OnTimeLapseListener {

		@Override
		public void onFinish() {
			iView.enableVerifyCodeGetter();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			iView.disableVerifyCodeGetter();
			iView.freshCoolingTime((int) (millisUntilFinished / 1000));
		}
	}

	private final class SharedPreferenceProviderImpl implements ISharedPreferenceProvider {

		@Override
		public SharedPreferences getSharedPreferences() {
			return iView.getTimmerSp();
		}

		@Override
		public String getRecordTag() {
			return "register";
		}

	}

}
