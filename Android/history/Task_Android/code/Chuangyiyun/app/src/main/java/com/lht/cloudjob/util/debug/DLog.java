package com.lht.cloudjob.util.debug;

import android.util.Log;

import com.lht.cloudjob.BuildConfig;

/**
 *  <p><b>Package</b> com.lht.cloudjob.util.debug
 * <p><b>Project</b> 
 * <p><b>Classname</b> DLog
 * <p><b>Description</b>: debug工具类
 * 
 * Created by leobert on 2016/7/10.
 */
public class DLog {

	/**
	 * forceDebug:强制打印，设置为true则打包后也打印log
	 */
	public static final boolean forceDebug = false;

	public static void v(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.v);
	}

	public static void v(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.v);
	}

	public static void d(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.d);
	}

	public static void d(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.d);
	}

	public static void i(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.i);
	}

	public static void i(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.i);
	}

	public static void w(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.w);
	}

	public static void w(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.w);
	}

	public static void e(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.e);
	}

	public static void e(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.e);
	}

	public static void wtf(Class<?> clazz, String msg) {
		log(clazz, msg, LogLevel.wtf);
	}

	public static void wtf(Class<?> clazz, LogLocation location, String msg) {
		log(clazz, msg + location.toString(), LogLevel.wtf);
	}

	private static void log(Class<?> clazz, String msg, LogLevel level) {

		if (BuildConfig.DEBUG | forceDebug) {
			switch (level) {
			case v:
				Log.v(clazz.getSimpleName(), msg);
				break;
			case d:
				Log.d(clazz.getSimpleName(), msg);
				break;
			case i:
				Log.i(clazz.getSimpleName(), msg);
				break;
			case w:
				Log.w(clazz.getSimpleName(), msg);
				break;
			case e:
				Log.e(clazz.getSimpleName(), msg);
				break;
			case wtf:
				Log.wtf(clazz.getSimpleName(), msg);
				break;

			default:
				break;
			}
		}

	}

	enum LogLevel {
		v, d, i, w, e, wtf;
	}

	public static class LogLocation extends Exception {
		/**
		 * serialVersionUID:序列化id
		 */
		private static final long serialVersionUID = 1L;

		public int line() {
			StackTraceElement[] trace = getStackTrace();
			if (trace == null || trace.length == 0) {
				return -1;
			}
			return trace[0].getLineNumber();
		}

		public String fun() {
			StackTraceElement[] trace = getStackTrace();
			if (trace == null || trace.length == 0) {
				return "";
			}
			return trace[0].getMethodName();
		}

		public LogLocation() {
			super();
		}

		@Override
		public String toString() {
			return "(logged at:" + line() + "  function:" + fun()
					+ ")";
		}
	}
}
