package com.lht.pan_android;

import com.lht.pan_android.util.CloudBoxApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * @ClassName: BackgroundActivity
 * @Description: TODO
 * @date 2016年4月1日 上午9:26:57
 * 
 * @author leobert.lan
 * @version 1.0
 * @since JDK 1.7
 */
public class BackgroundActivity extends Activity {

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = new View(this);
		v.setBackgroundColor(R.color.alpha_60);
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		CloudBoxApplication.addBackgroundActivity(this);
	}

	@Override
	protected void onDestroy() {
		CloudBoxApplication.backgroundActivity = null;
		super.onDestroy();
	}
}
