package com.lht.pan_android.activity.others;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lht.pan_android.R;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.LoginActivity;
import com.lht.pan_android.adapter.ViewPagerAdapter;
import com.lht.pan_android.util.CloudBoxApplication;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends UMengActivity implements OnPageChangeListener {

	private final static String mPageName = "guideActivity";

	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private List<View> views;

	// 小圆点图片
	private ImageView[] viewPagerDots;

	// 当前小圆点选中时的位置
	private int currentIndex;

	private Button btnAccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		CloudBoxApplication.addActivity(this);
		setContentView(R.layout.activity_guide);

		btnAccess = (Button) findViewById(R.id.guide_btn_access);
		btnAccess.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(GuideActivity.this, LoginActivity.class));
				// 改变 判断是否是第一次的值
				SharedPreferences sp = getSharedPreferences(SplashActivity.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
				sp.edit().putBoolean(SplashActivity.KEY_FIRSTIN, false).apply();
				GuideActivity.this.finishWithoutOverrideAnim();
			}
		});

		initViews();
		initDots();
	}

	@SuppressLint("InflateParams")
	/**
	 * @Title: initViews
	 * @Description: 初始化界面
	 * @author: zhangbin
	 */
	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();

		views.add(inflater.inflate(R.layout.activity_guide_viewpager_one, null));
		views.add(inflater.inflate(R.layout.activity_guide_viewpager_two, null));
		views.add(inflater.inflate(R.layout.activity_guide_viewpager_three, null));

		viewPagerAdapter = new ViewPagerAdapter(views, this);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOnPageChangeListener(this);
	}

	private void initDots() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.guide_ll);

		viewPagerDots = new ImageView[views.size()];

		// 循环取得小圆点图片和默认图片
		for (int i = 0; i < views.size(); i++) {
			viewPagerDots[i] = (ImageView) linearLayout.getChildAt(i);
			viewPagerDots[i].setEnabled(true);
		}

		currentIndex = 0;
		// 选中状态的颜色
		viewPagerDots[currentIndex].setEnabled(false);
	}

	private void setCurrentDot(int position) {
		if (position < 0 || position > views.size() - 1 || currentIndex == position) {
			return;
		}

		viewPagerDots[position].setEnabled(false);
		viewPagerDots[currentIndex].setEnabled(true);

		currentIndex = position;
	}

	// 滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		return;
	}

	// 当前页面滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		return;
	}

	// 当前页面选中时调用
	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 2) {
			for (int i = 0; i < viewPagerDots.length; i++) {
				viewPagerDots[i].setVisibility(View.GONE);
			}
			btnAccess.setVisibility(View.VISIBLE);
		} else {
			for (int i = 0; i < viewPagerDots.length; i++) {
				viewPagerDots[i].setVisibility(View.VISIBLE);
			}
			btnAccess.setVisibility(View.GONE);
		}
		setCurrentDot(arg0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// MobclickAgent.onPageStart(mPageName);
		// MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// MobclickAgent.onPageEnd(mPageName);
		// MobclickAgent.onPause(this);
	}

	@Override
	protected String getPageName() {
		return GuideActivity.mPageName;
	}

	@Override
	protected UMengActivity getActivity() {
		return GuideActivity.this;
	}

}
