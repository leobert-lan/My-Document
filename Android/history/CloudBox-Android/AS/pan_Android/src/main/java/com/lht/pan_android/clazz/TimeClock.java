package com.lht.pan_android.clazz;

import com.lht.pan_android.Interface.IKeyManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.CountDownTimer;

/**
 * @ClassName: TimeCount
 * @Description: TODO
 * @date 2016年1月11日 上午11:28:59
 * 
 * @author zhangbin
 * @version 1.0
 */
public class TimeClock implements IKeyManager.Timer {

	private Context mContext;
	private SharedPreferences sharedPreferences;
	private final int DEFAULT_PERIOD = 60000;

	private long period;

	private final String tag;

	public TimeClock(Context mContext, String tag) {
		super();
		this.mContext = mContext;
		this.tag = tag;
		sharedPreferences = mContext.getSharedPreferences(SP_TIMER, Context.MODE_PRIVATE);
		period = DEFAULT_PERIOD;
	}

	/**
	 * @Title: setPeriod
	 * @Description: 设置默认时间周期
	 * @author: zhangbin
	 * @param period
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	public void updateTimeStamp() {
		this.updateTimeStamp(getCurrentTimeStamp());
	}

	/**
	 * @Title: updateTimeStamp
	 * @Description: 缓存当前时间
	 * @author: zhangbin
	 * @param timeStamp
	 */
	public void updateTimeStamp(long timeStamp) {
		Editor editor = sharedPreferences.edit();
		editor.putLong(KEY_VERIFICATE + tag, timeStamp);
		editor.commit();
	}

	/**
	 * @Title: getTimeClock
	 * @Description: 执行时间线程
	 * @author: zhangbin
	 * @param countDownInterval
	 * @return
	 */
	public TimeCount getTimeClock(long countDownInterval) {
		Long timestamp = sharedPreferences.getLong(KEY_VERIFICATE + tag, 0);
		Long remianTime = period - (getCurrentTimeStamp() - timestamp);
		remianTime = remianTime > 0 ? remianTime : 0;
		return new TimeCount(remianTime, countDownInterval);
	}

	public class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			timeLapseListener.onFinish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timeLapseListener.onTick(millisUntilFinished);
		}
	}

	private OnTimeLapseListener timeLapseListener;

	public void setTimeLapseListener(OnTimeLapseListener onTimeLapseListener) {
		this.timeLapseListener = onTimeLapseListener;
	};

	public interface OnTimeLapseListener {
		void onFinish();

		void onTick(long millisUntilFinished);
	}

	/**
	 * @Title: getCurrentTimeStamp
	 * @Description: 系统当前时间戳
	 * @author: zhangbin
	 * @return
	 */
	private Long getCurrentTimeStamp() {
		return System.currentTimeMillis();
	}
}
