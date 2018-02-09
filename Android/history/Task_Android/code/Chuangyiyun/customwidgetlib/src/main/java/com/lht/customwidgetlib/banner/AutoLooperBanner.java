package com.lht.customwidgetlib.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

//import com.bumptech.glide.Glide;
import com.lht.customwidgetlib.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.banner
 * <p><b>Project</b> AndroidBase
 * <p><b>Classname</b> AutoLooperBanner
 * <p><b>Description</b>: 自动滚动的Banner
 * <p/>
 * <b> Attributes are declared as follows：</b>
 * <p/>
 * <b>something about indicator like size,color,shape and so on</b>
 * <li>selectedIndicatorColor" format="color|reference"
 * <li>unSelectedIndicatorColor" format="color|reference"
 * <li>indicatorShape" format="enum"
 * element"rect" value="0"
 * element"oval" value="1"
 * <li>selectedIndicatorHeight" format="dimension|reference"
 * <li>selectedIndicatorWidth" format="dimension|reference"
 * <li>unSelectedIndicatorHeight" format="dimension|reference"
 * <li>unSelectedIndicatorWidth" format="dimension|reference"
 * <li>indicatorPosition" format="enum
 * element"centerBottom" value="0"
 * element"rightBottom" value="1"
 * element"leftBottom" value="2"
 * element"centerTop" value="3"
 * element"rightTop" value="4"
 * element"leftTop" value="5"
 * <li>indicatorSpace" format="dimension|reference"
 * <li>indicatorMargin" format="dimension|reference"
 * <p/>
 * <li>autoPlayDuration" format="integer|reference" <I>ms formated</I>
 * <li>scrollDuration" format="integer|reference" <I>ms formated</I>
 * <li>isAutoPlay" format="boolean" <I>default is true</I>
 * <li>defaultImage" format="integer|reference"
 * <p/>
 * <p> Created by leobert on 2016/4/18.
 */
public class AutoLooperBanner extends RelativeLayout {

    private ViewPager pager;
    //指示器容器
    private LinearLayout indicatorContainer;

    private Drawable unSelectedDrawable;
    private Drawable selectedDrawable;

    private int WHAT_AUTO_PLAY = 1000;

    private boolean isAutoPlay = true;

    private int itemCount;

    private int selectedIndicatorColor = 0xffff0000;
    private int unSelectedIndicatorColor = 0x88888888;

    private Shape indicatorShape = Shape.oval;
    private int selectedIndicatorHeight = 6;
    private int selectedIndicatorWidth = 6;
    private int unSelectedIndicatorHeight = 6;
    private int unSelectedIndicatorWidth = 6;

    private Position indicatorPosition = Position.centerBottom;
    private int autoPlayDuration = 4000;
    private int scrollDuration = 900;

    private int indicatorSpace = 3;
    private int indicatorMargin = 10;

    private int defaultImage;

    /**
     * the shape of the indicator
     */
    private enum Shape {
        rect, oval
    }

    /**
     * the location of indicator
     */
    private enum Position {
        centerBottom,
        rightBottom,
        leftBottom,
        centerTop,
        rightTop,
        leftTop
    }

    private IBannerUpdate iBannerUpdate = null;

    public void setIBannerUpdate(IBannerUpdate iBannerUpdate) {
        this.iBannerUpdate = iBannerUpdate;
    }

    public IBannerUpdate getIBannerUpdate() {
        return iBannerUpdate;
    }

    private OnBannerItemClickListener onBannerItemClickListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_AUTO_PLAY) {
                if (pager != null) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
                }
            }
            return false;
        }
    });

    public AutoLooperBanner(Context context) {
        super(context);
        init(null, 0);
    }

    public AutoLooperBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AutoLooperBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AutoLooperBanner, defStyle, 0);
        selectedIndicatorColor = array.getColor(R.styleable.AutoLooperBanner_selectedIndicatorColor, selectedIndicatorColor);
        unSelectedIndicatorColor = array.getColor(R.styleable.AutoLooperBanner_unSelectedIndicatorColor, unSelectedIndicatorColor);

        int shape = array.getInt(R.styleable.AutoLooperBanner_indicatorShape, Shape.oval.ordinal());
        for (Shape shape1 : Shape.values()) {
            if (shape1.ordinal() == shape) {
                indicatorShape = shape1;
                break;
            }
        }
        selectedIndicatorHeight = (int) array.getDimension(R.styleable.AutoLooperBanner_selectedIndicatorHeight, selectedIndicatorHeight);
        selectedIndicatorWidth = (int) array.getDimension(R.styleable.AutoLooperBanner_selectedIndicatorWidth, selectedIndicatorWidth);
        unSelectedIndicatorHeight = (int) array.getDimension(R.styleable.AutoLooperBanner_unSelectedIndicatorHeight, unSelectedIndicatorHeight);
        unSelectedIndicatorWidth = (int) array.getDimension(R.styleable.AutoLooperBanner_unSelectedIndicatorWidth, unSelectedIndicatorWidth);

        int position = array.getInt(R.styleable.AutoLooperBanner_indicatorPosition, Position.centerBottom.ordinal());
        for (Position position1 : Position.values()) {
            if (position == position1.ordinal()) {
                indicatorPosition = position1;
            }
        }
        indicatorSpace = (int) array.getDimension(R.styleable.AutoLooperBanner_indicatorSpace, indicatorSpace);
        indicatorMargin = (int) array.getDimension(R.styleable.AutoLooperBanner_indicatorMargin, indicatorMargin);
        autoPlayDuration = array.getInt(R.styleable.AutoLooperBanner_autoPlayDuration, autoPlayDuration);
        scrollDuration = array.getInt(R.styleable.AutoLooperBanner_scrollDuration, scrollDuration);
        isAutoPlay = array.getBoolean(R.styleable.AutoLooperBanner_isAutoPlay, isAutoPlay);
        defaultImage = array.getResourceId(R.styleable.AutoLooperBanner_defaultImage, defaultImage);
        array.recycle();

        //绘制未选中状态图形
        LayerDrawable unSelectedLayerDrawable;
        LayerDrawable selectedLayerDrawable;
        GradientDrawable unSelectedGradientDrawable;
        unSelectedGradientDrawable = new GradientDrawable();

        //绘制选中状态图形
        GradientDrawable selectedGradientDrawable;
        selectedGradientDrawable = new GradientDrawable();
        switch (indicatorShape) {
            case rect:
                unSelectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                selectedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case oval:
                unSelectedGradientDrawable.setShape(GradientDrawable.OVAL);
                selectedGradientDrawable.setShape(GradientDrawable.OVAL);
                break;
        }
        unSelectedGradientDrawable.setColor(unSelectedIndicatorColor);
        unSelectedGradientDrawable.setSize(unSelectedIndicatorWidth, unSelectedIndicatorHeight);
        unSelectedLayerDrawable = new LayerDrawable(new Drawable[]{unSelectedGradientDrawable});
        unSelectedDrawable = unSelectedLayerDrawable;

        selectedGradientDrawable.setColor(selectedIndicatorColor);
        selectedGradientDrawable.setSize(selectedIndicatorWidth, selectedIndicatorHeight);
        selectedLayerDrawable = new LayerDrawable(new Drawable[]{selectedGradientDrawable});
        selectedDrawable = selectedLayerDrawable;

    }

    //添加本地图片路径
    public void setViewRes(List<Integer> viewRes) {
        List<View> views = new ArrayList<>();
        itemCount = viewRes.size();
        //主要是解决当item为小于3个的时候滑动有问题，这里将其拼凑成3个以上
        if (itemCount < 1) {//当item个数0
            throw new IllegalStateException("item count not equal zero");
        } else if (itemCount < 2) {//当item个数为1
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(0), 0));
        } else if (itemCount < 3) {//当item个数为2
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(1), 1));
            views.add(getImageView(viewRes.get(0), 0));
            views.add(getImageView(viewRes.get(1), 1));
        } else {
            for (int i = 0; i < viewRes.size(); i++) {
                views.add(getImageView(viewRes.get(i), i));
            }
        }
        setViews(views);
    }

    @NonNull
    private ImageView getImageView(Integer res, final int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(position);
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (iBannerUpdate != null) {
            //local 情况不使用默认图
            ImgRes<Integer> imgRes = new ImgRes<>(res);
            iBannerUpdate.UpdateImage(imgRes, imageView);
        } else {
            Log.w(getClass().getSimpleName(), "you should provide a interface-impl to update View," +
                    "use setIBannerUpdate(IBannerUpdate iBannerUpdate) ");
        }
//        Glide.with(getContext()).load(res).centerCrop().into(imageView);
        return imageView;
    }

    //添加网络图片路径
    public void setViewUrls(List<String> urls) {
        List<View> views = new ArrayList<>();
        itemCount = urls.size();
        //主要是解决当item为小于3个的时候滑动有问题，这里将其拼凑成3个以上
        if (itemCount < 1) {//当item个数0
            throw new IllegalStateException("item count not equal zero");
        } else if (itemCount < 2) { //当item个数为1
            views.add(getImageView(urls.get(0), 0));
            views.add(getImageView(urls.get(0), 0));
            views.add(getImageView(urls.get(0), 0));
        } else if (itemCount < 3) {//当item个数为2
            views.add(getImageView(urls.get(0), 0));
            views.add(getImageView(urls.get(1), 1));
            views.add(getImageView(urls.get(0), 0));
            views.add(getImageView(urls.get(1), 1));
        } else {
            for (int i = 0; i < urls.size(); i++) {
                views.add(getImageView(urls.get(i), i));
            }
        }
        setViews(views);
    }

    @NonNull
    private ImageView getImageView(String url, final int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(position);
                }
            }
        });
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (iBannerUpdate != null) {
            //net 情况使用默认图,需要判断是否存在
            ImgRes<String> imgRes = new ImgRes<>(url);
            if (defaultImage != 0) {
                imgRes.setDefaultUrlRes(defaultImage);
            }
            iBannerUpdate.UpdateImage(imgRes, imageView);
        } else {
            Log.w(getClass().getSimpleName(), "you should provide a interface-impl to update View," +
                    "use setIBannerUpdate(IBannerUpdate iBannerUpdate) ");
        }
//        if (defaultImage != 0) {
//            Glide.with(getContext()).load(url).placeholder(defaultImage).centerCrop().into(imageView);
//        } else {
//            Glide.with(getContext()).load(url).centerCrop().into(imageView);
//        }
        return imageView;
    }

    //添加任意View视图
    private void setViews(final List<View> views) {
        removeView(pager);
        //初始化pager
        pager = new ViewPager(getContext());
        //添加viewpager到SliderLayout
        addView(pager);
        setSliderTransformDuration(scrollDuration);
        //初始化indicatorContainer
        indicatorContainer = new LinearLayout(getContext());
        indicatorContainer.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (indicatorPosition) {
            case centerBottom:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case centerTop:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case leftBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case leftTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case rightBottom:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case rightTop:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }
        //设置margin
        params.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
        //添加指示器容器布局到SliderLayout
        addView(indicatorContainer, params);

        //初始化指示器，并添加到指示器容器布局
        for (int i = 0; i < itemCount; i++) {
            ImageView indicator = new ImageView(getContext());
            indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            indicator.setPadding(indicatorSpace, indicatorSpace, indicatorSpace, indicatorSpace);
            indicator.setImageDrawable(unSelectedDrawable);
            indicatorContainer.addView(indicator);
        }
        LoopPagerAdapter pagerAdapter = new LoopPagerAdapter(views);
        pager.setAdapter(pagerAdapter);
        //设置当前item到Integer.MAX_VALUE中间的一个值，看起来像无论是往前滑还是往后滑都是ok的
        //如果不设置，用户往左边滑动的时候已经划不动了
        int targetItemPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % itemCount;
        pager.setCurrentItem(targetItemPosition);
        switchIndicator(targetItemPosition % itemCount);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switchIndicator(position % itemCount);
            }
        });
//        startAutoPlay(); 请手动调用

    }

    public void setSliderTransformDuration(int duration) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), null, duration);
            mScroller.set(pager, scroller);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 开始自动轮播
     */
    public void startAutoPlay() {
        stopAutoPlay(); // 避免重复消息
        if (isAutoPlay) {
            handler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, autoPlayDuration);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else {
            stopAutoPlay();
        }
    }


    /**
     * 停止自动轮播
     */
    public void stopAutoPlay() {
        if (isAutoPlay) {
            handler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopAutoPlay();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoPlay();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 切换指示器状态
     *
     * @param currentPosition 当前位置
     */
    private void switchIndicator(int currentPosition) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ((ImageView) indicatorContainer.getChildAt(i)).setImageDrawable(i == currentPosition ? selectedDrawable : unSelectedDrawable);
        }
    }


    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    public interface OnBannerItemClickListener {
        void onItemClick(int position);
    }

    public class LoopPagerAdapter extends PagerAdapter {
        private List<View> views;

        public LoopPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            //Integer.MAX_VALUE = 2147483647
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (views.size() > 0) {
                //position % view.size()是指虚拟的position会在[0，view.size()）之间循环
                View view = views.get(position % views.size());
                if (container.equals(view.getParent())) {
                    container.removeView(view);
                }
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            this(context, interpolator);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}