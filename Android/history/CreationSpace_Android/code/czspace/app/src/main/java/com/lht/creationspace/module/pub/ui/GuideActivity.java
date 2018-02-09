package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.ViewPagerAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends UMengActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String mPageName = "guideActivity";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> views;

    // 小圆点图片
    private ImageView[] viewPagerDots;

    // 当前小圆点选中时的位置
    private int currentIndex;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initVariable();
        initEvent();
    }

    /**
     * desc: 实例化View
     */
    @Override
    protected void initView() {
        LayoutInflater inflater = getLayoutInflater();

        views = new ArrayList<>();
        views.add(inflater.inflate(R.layout.view_guide_one, null, false));
        views.add(inflater.inflate(R.layout.view_guide_two, null, false));
        views.add(inflater.inflate(R.layout.view_guide_three, null, false));
        View page4 = inflater.inflate(R.layout.view_guide_four, null, false);
        views.add(page4);

        btnStart = (Button) page4.findViewById(R.id.guide_btn_start);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        initDots();
    }

    /**
     * desc: 实例化必要的参数，以防止initEvent需要的参数空指针
     */
    @Override
    protected void initVariable() {
        viewPagerAdapter = new ViewPagerAdapter(views);
    }

    /**
     * desc: 监听器设置、adapter设置等
     */
    @Override
    protected void initEvent() {
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPagerDots[0].setEnabled(true);

        btnStart.setOnClickListener(this);
    }

    private LinearLayout llIndicator;

    private void initDots() {
        llIndicator = (LinearLayout) findViewById(R.id.guide_ll);

        viewPagerDots = new ImageView[views.size()];

        // 循环取得小圆点图片和默认图片
        for (int i = 0; i < views.size(); i++) {
            if (llIndicator.getChildAt(i) != null) {
                viewPagerDots[i] = (ImageView) llIndicator.getChildAt(i);
                viewPagerDots[i].setImageResource(R.drawable.dian1);
            }
        }
        currentIndex = 0;
        // 选中状态的颜色
        viewPagerDots[currentIndex].setImageResource(R.drawable.dian2);
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        viewPagerDots[position].setImageResource(R.drawable.dian2);
        viewPagerDots[currentIndex].setImageResource(R.drawable.dian1);

        currentIndex = position;
    }

    // 当前页面滑动时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 当前页面选中时调用
    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position);
    }

    // 滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    protected String getPageName() {
        return GuideActivity.mPageName;
    }

    @Override
    public BaseActivity getActivity() {
        return GuideActivity.this;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guide_btn_start:
                Intent intent = new Intent(GuideActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed(); shield back key press
    }

    public static  Launcher getLauncher(Context context) {
        return new  Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, GuideActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
