package com.lht.cloudjob.clazz;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Description: 两次返回退出
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
