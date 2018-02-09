package com.lht.pan_android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lht.pan_android.Interface.IKeyManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.CursorLoader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

/**
 * @ClassName: VerifyAutoGetUtil
 * @Description: TODO
 * @date 2016年1月12日 上午11:19:35
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class VerifyAutoGetUtil implements IKeyManager.SMSProps {
	private final Activity mActivity;

	private final OnVerifyCodeGetListener verifyCodeGetListener;

	private final String mPhone;

	public VerifyAutoGetUtil(final Activity ctx, OnVerifyCodeGetListener l, String phone) {
		this.mActivity = ctx;
		this.verifyCodeGetListener = l;
		mPhone = phone;
	}

	private boolean isStarted = false;

	public void startWork() {
		if (isStarted)
			return;
		isStarted = true;
		if (mPhone == null)
			return;
		mActivity.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, contentObserver);
	}

	@SuppressLint("HandlerLeak")
	Handler han = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			String codestr = null;
			try {
				codestr = getSmsVerifyCode(mActivity, mPhone);
				verifyCodeGetListener.OnVerifyCodeGet(codestr);
			} catch (Exception e) {
			}
		};
	};

	private ContentObserver contentObserver = new ContentObserver(han) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			han.sendEmptyMessage(0);
		}
	};

	private String getSmsVerifyCode(Activity c, String phone) {

		Uri uri = Uri.parse("content://sms/inbox");
		String[] projection = new String[] { "address", "person", "body" };
		String selection = " address like'" + phone + "%' ";
		String[] selectionArgs = new String[] {};
		String sortOrder = "date desc";
		CursorLoader loader = new CursorLoader(c, uri, projection, selection, selectionArgs, sortOrder);
		Cursor cur = loader.loadInBackground();
		if (cur != null && cur.getCount() > 0) {
			cur.moveToFirst();
			String body = cur.getString(cur.getColumnIndex("body")).replaceAll("\n", " ");
			cur.close();
			return getCode(body, CODELENGTH);
		}
		cur.close();
		return null;
	}

	/**
	 * 从短信字符窜提取验证码
	 * 
	 * @param body
	 *            短信内容
	 * @param YZMLENGTH
	 *            验证码的长度 一般6位或者4位
	 * @return 接取出来的验证码
	 */
	private String getCode(String body, int YZMLENGTH) {
		// 首先([a-zA-Z0-9]{YZMLENGTH})是得到一个连续的六位数字字母组合
		// (?<![a-zA-Z0-9])负向断言([0-9]{YZMLENGTH})前面不能有数字
		// (?![a-zA-Z0-9])断言([0-9]{YZMLENGTH})后面不能有数字出现
		Pattern p = Pattern.compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + YZMLENGTH + "})(?![a-zA-Z0-9])");
		Matcher m = p.matcher(body);
		if (m.find()) {
			return m.group(0);
		}
		return null;
	}

	public interface OnVerifyCodeGetListener {
		void OnVerifyCodeGet(String verifyCode);
	}

}
