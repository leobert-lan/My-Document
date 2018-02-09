package com.lht.pan_android.util;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @ClassName: Exit
 * @Description: 两次返回退出
 * @date 2015年11月23日 下午2:53:35
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Exit {

	private boolean isExit = false;
	private Runnable task = new Runnable() {
		@Override
		public void run() {
			isExit = false;
		}
	};

	public void doExitInTwoSecond() {
		isExit = true;
		HandlerThread thread = new HandlerThread("doTask");
		thread.start();
		new Handler(thread.getLooper()).postDelayed(task, 2000);
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

}
