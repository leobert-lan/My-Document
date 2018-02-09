package com.lht.pan_android.mpv.model;

import com.lht.pan_android.Interface.IKeyManager;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * <p>
 * <b>Package</b> com.lht.chuangyiyun.mvp.model
 * <p>
 * <b>Project</b> Chuangyiyun
 * <p>
 * <b>Classname</b> TimerClockModel
 * <p>
 * <b>Description</b>: 时钟任务业务类,获取timerclock开始倒计时，自己计算剩余时间，注意使用时先更新起始点时间戳 Created
 * by leobert on 2016/5/5.
 */
public class TimerClockModel implements IKeyManager.Timer {

	private SharedPreferences sharedPreferences;
	private final int DEFAULT_PERIOD = 60000;

	private long period;

	private final String tag;

	public TimerClockModel(ISharedPreferenceProvider iSharedPreferenceProvider,
			OnTimeLapseListener onTimeLapseListener) {
		this.tag = iSharedPreferenceProvider.getRecordTag();
		sharedPreferences = iSharedPreferenceProvider.getSharedPreferences();
		period = DEFAULT_PERIOD;

		this.timeLapseListener = onTimeLapseListener;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * 更新计时起始点
	 */
	public void updateTimeStamp() {
		this.updateTimeStamp(getCurrentTimeStamp());
	}

	/**
	 * 更新计时起始点
	 * 
	 * @param timeStamp
	 *            当前的时间戳
	 */
	public void updateTimeStamp(long timeStamp) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(KEY_VERIFICATE + tag, timeStamp);
		editor.commit();
	}

	/**
	 * desc: 获取倒计时对象，注意获取之后立即start，中间的时间不会被考虑
	 *
	 * @param countDownInterval
	 * @return
	 */
	public TimeCount getTimeClock(long countDownInterval) {
		Long beginTimeStamp = sharedPreferences.getLong(KEY_VERIFICATE + tag, 0);
		Long remainTime = period - (getCurrentTimeStamp() - beginTimeStamp);
		remainTime = remainTime > 0 ? remainTime : 0;
		Log.e("lmsg", "timer remain:" + remainTime);
		return new TimeCount(remainTime, countDownInterval);
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

	private final OnTimeLapseListener timeLapseListener;

	/**
	 * 计时器回调接口
	 */
	public interface OnTimeLapseListener {
		/**
		 * 计时完成
		 */
		void onFinish();

		/**
		 * 每一个触发
		 *
		 * @param millisUntilFinished
		 *            距离结束的时间
		 */
		void onTick(long millisUntilFinished);
	}

	/**
	 * 获取当前系统时间
	 *
	 * @return 当前系统时间戳
	 */
	private Long getCurrentTimeStamp() {
		return System.currentTimeMillis();
	}

	public interface ISharedPreferenceProvider {
		/**
		 * @return 记录文件
		 */
		SharedPreferences getSharedPreferences();

		/**
		 * @return 记录的tag
		 */
		String getRecordTag();
	}
}
