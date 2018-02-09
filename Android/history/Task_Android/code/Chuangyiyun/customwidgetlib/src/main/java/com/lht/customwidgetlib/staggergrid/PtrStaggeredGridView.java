package com.lht.customwidgetlib.staggergrid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.lht.customwidgetlib.R;
import com.lht.customwidgetlib.staggergrid.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.library.staggregrid
 * <p><b>Project</b> StaggerGrid
 * <p><b>Classname</b> PtrStaggeredGridView
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/4/19.
 */
public class PtrStaggeredGridView extends StaggeredGridView {

    public PtrStaggeredGridView(Context context) {
        super(context);
        init(context);
    }

    public PtrStaggeredGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PtrStaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**  显示格式化日期模板   */
    private static final String DATE_FORMAT_STR = "yyyy年MM月dd日 HH:mm";

    /**  实际的padding的距离与界面上偏移距离的比例   */
    private static final int RATIO = 3;

    private static final int RELEASE_TO_REFRESH = 0;
    private static final int PULL_TO_REFRESH = 1;
    private static final int REFRESHING = 2;
    private static final int DONE = 3;
    private static final int LOADING = 4;

    /**  加载中   */
    private static final int ENDINT_LOADING = 1;
    /**  手动完成刷新   */
    private static final int ENDINT_MANUAL_LOAD_DONE = 2;
    /**  自动完成刷新   */
    private static final int ENDINT_AUTO_LOAD_DONE = 3;

    /**    0:RELEASE_TO_REFRESH;
     * <p> 1:PULL_To_REFRESH;
     * <p> 2:REFRESHING;
     * <p> 3:DONE;
     * <p> 4:LOADING */
    private int mHeadState;
    /**    0:完成/等待刷新 ;
     * <p> 1:加载中  */
    private int mEndState;

    // ================================= 功能设置Flag ================================

    /**  可以下拉刷新？   */
    private boolean mCanRefresh = false;
    /** 下拉刷新后是否显示第一条Item    */
    private boolean mIsMoveToFirstItemAfterRefresh = false;

    public boolean isCanRefresh() {
        return mCanRefresh;
    }

    public void setCanRefresh(boolean pCanRefresh) {
        mCanRefresh = pCanRefresh;
    }

    public boolean isMoveToFirstItemAfterRefresh() {
        return mIsMoveToFirstItemAfterRefresh;
    }

    public void setMoveToFirstItemAfterRefresh(
            boolean pIsMoveToFirstItemAfterRefresh) {
        mIsMoveToFirstItemAfterRefresh = pIsMoveToFirstItemAfterRefresh;
    }

    // ============================================================================

    private LayoutInflater mInflater;

    private LinearLayout mHeadView;
    private TextView mTipsTextView;
    private TextView mLastUpdatedTextView;
    private ImageView mArrowImageView;
    private ProgressBar mProgressBar;

    private View mEndRootView;
    private ProgressBar mEndLoadProgressBar;
    private TextView mEndLoadTipsTextView;

    /**  headView动画   */
    private RotateAnimation mArrowAnim;
    /**  headView反转动画   */
    private RotateAnimation mArrowReverseAnim;

    /** 用于保证startY的值在一个完整的touch事件中只被记录一次    */
    private boolean mIsRecored;

    private int mHeadViewWidth;
    private int mHeadViewHeight;

    private int mStartY;
    private boolean mIsBack;

    private int mFirstItemIndex;
    private int mLastItemIndex;
    private int mCount;
    private boolean mEnoughCount;//足够数量充满屏幕？

    private OnRefreshListener mRefreshListener;

    //////////////////////////////////////////////////////////////////////////
    //TODO
    //////////////////////////////////////////////////////////////////////////

    private void init(Context pContext) {
        setCacheColorHint(pContext.getResources().getColor(R.color.transparent));
        mInflater = LayoutInflater.from(pContext);
        addHeadView();
        initPullImageAnimation(0);
    }

    /**
     * 添加下拉刷新的HeadView
     * @date 2013-11-11 下午9:48:26
     * @change JohnWatson
     * @version 1.0
     */
    private void addHeadView() {
        mHeadView = (LinearLayout) mInflater.inflate(R.layout.staggeredgrid_ptr_head, null);

        mArrowImageView = (ImageView) mHeadView
                .findViewById(R.id.head_arrowImageView);
        mArrowImageView.setMinimumWidth(70);
        mArrowImageView.setMinimumHeight(50);
        mProgressBar = (ProgressBar) mHeadView
                .findViewById(R.id.head_progressBar);
        mTipsTextView = (TextView) mHeadView.findViewById(
                R.id.head_tipsTextView);
        mLastUpdatedTextView = (TextView) mHeadView
                .findViewById(R.id.head_lastUpdatedTextView);

        measureView(mHeadView);
        mHeadViewHeight = mHeadView.getMeasuredHeight();
        mHeadViewWidth = mHeadView.getMeasuredWidth();

        mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
        mHeadView.invalidate();

        Log.v("size", "width:" + mHeadViewWidth + " height:"
                + mHeadViewHeight);

        addHeaderView(mHeadView, null, false);

        mHeadState = DONE;
    }

    /**
     * 实例化下拉刷新的箭头的动画效果
     * @param pAnimDuration 动画运行时长
     * @date 2013-11-20 上午11:53:22
     * @change JohnWatson
     * @version 1.0
     */
    private void initPullImageAnimation(final int pAnimDuration) {

        int _Duration;

        if(pAnimDuration > 0){
            _Duration = pAnimDuration;
        }else{
            _Duration = 250;
        }
        Interpolator _Interpolator = new LinearInterpolator();

        mArrowAnim = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowAnim.setInterpolator(_Interpolator);
        mArrowAnim.setDuration(_Duration);
        mArrowAnim.setFillAfter(true);

        mArrowReverseAnim = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mArrowReverseAnim.setInterpolator(_Interpolator);
        mArrowReverseAnim.setDuration(_Duration);
        mArrowReverseAnim.setFillAfter(true);
    }

    /**
     * 测量HeadView宽高(注意：此方法仅适用于LinearLayout，请读者自己测试验证。)
     * @param pChild
     * @date 2013-11-20 下午4:12:07
     * @change JohnWatson
     * @version 1.0
     */
    private void measureView(View pChild) {
        ViewGroup.LayoutParams p = pChild.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;

        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        pChild.measure(childWidthSpec, childHeightSpec);
    }


    public boolean onTouchEvent(MotionEvent event) {

        if (mCanRefresh) {
            //TODO
            if(mEndState == ENDINT_LOADING){
                // 如果存在加载更多功能，并且当前正在加载更多，默认不允许下拉刷新，必须加载完毕后才能使用。
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if (mFirstItemIndex == 0 && !mIsRecored) {
                        mIsRecored = true;
                        mStartY = (int) event.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP:

                    if (mHeadState != REFRESHING && mHeadState != LOADING) {
                        if (mHeadState == DONE) {

                        }
                        if (mHeadState == PULL_TO_REFRESH) {
                            mHeadState = DONE;
                            changeHeaderViewByState();
                        }
                        if (mHeadState == RELEASE_TO_REFRESH) {
                            mHeadState = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                        }
                    }

                    mIsRecored = false;
                    mIsBack = false;

                    break;

                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();

                    if (!mIsRecored && mFirstItemIndex == 0) {
                        mIsRecored = true;
                        mStartY = tempY;
                    }

                    if (mHeadState != REFRESHING && mIsRecored && mHeadState != LOADING) {

                        // 保证在设置padding的过程中，当前的位置一直是在head，
                        // 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了
                        if (mHeadState == RELEASE_TO_REFRESH) {

                            setSelection(0);

                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - mStartY) / RATIO < mHeadViewHeight)
                                    && (tempY - mStartY) > 0) {
                                mHeadState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                            // 一下子推到顶了
                            else if (tempY - mStartY <= 0) {
                                mHeadState = DONE;
                                changeHeaderViewByState();
                            }
                            // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                        }
                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (mHeadState == PULL_TO_REFRESH) {

                            setSelection(0);

                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - mStartY) / RATIO >= mHeadViewHeight) {
                                mHeadState = RELEASE_TO_REFRESH;
                                mIsBack = true;
                                changeHeaderViewByState();
                            } else if (tempY - mStartY <= 0) {
                                mHeadState = DONE;
                                changeHeaderViewByState();
                            }
                        }

                        if (mHeadState == DONE) {
                            if (tempY - mStartY > 0) {
                                mHeadState = PULL_TO_REFRESH;
                                changeHeaderViewByState();
                            }
                        }

                        if (mHeadState == PULL_TO_REFRESH) {
                            mHeadView.setPadding(0, -1 * mHeadViewHeight
                                    + (tempY - mStartY) / RATIO, 0, 0);

                        }

                        if (mHeadState == RELEASE_TO_REFRESH) {
                            mHeadView.setPadding(0, (tempY - mStartY) / RATIO
                                    - mHeadViewHeight, 0, 0);
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当HeadView状态改变时候，调用该方法，以更新界面
     * @date 2013-11-20 下午4:29:44
     * @change JohnWatson
     * @version 1.0
     */
    private void changeHeaderViewByState() {
        switch (mHeadState) {
            case RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mTipsTextView.setVisibility(View.VISIBLE);
                mLastUpdatedTextView.setVisibility(View.VISIBLE);

                mArrowImageView.clearAnimation();
                mArrowImageView.startAnimation(mArrowAnim);
                // 松开刷新
                mTipsTextView.setText(R.string.p2refresh_release_refresh);

                break;
            case PULL_TO_REFRESH:
                mProgressBar.setVisibility(View.GONE);
                mTipsTextView.setVisibility(View.VISIBLE);
                mLastUpdatedTextView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状态转变来的
                if (mIsBack) {
                    mIsBack = false;
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mArrowReverseAnim);
                    // 下拉刷新
                    mTipsTextView.setText(R.string.p2refresh_pull_to_refresh);
                } else {
                    // 下拉刷新
                    mTipsTextView.setText(R.string.p2refresh_pull_to_refresh);
                }
                break;

            case REFRESHING:
                mHeadView.setPadding(0, 0, 0, 0);

                // 华生的建议： 实际上这个的setPadding可以用动画来代替。我没有试，但是我见过。其实有的人也用Scroller可以实现这个效果，
                // 我没时间研究了，后期再扩展，这个工作交给小伙伴你们啦~ 如果改进了记得发到我邮箱噢~
                // 本人邮箱： xxzhaofeng5412@gmail.com

                mProgressBar.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.GONE);
                // 正在刷新...
                mTipsTextView.setText(R.string.p2refresh_doing_head_refresh);
                mLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
            case DONE:
                mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

                // 此处可以改进，同上所述。

                mProgressBar.setVisibility(View.GONE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.drawable.arrow);
                // 下拉刷新
                mTipsTextView.setText(R.string.p2refresh_pull_to_refresh);
                mLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener pRefreshListener) {
        if(pRefreshListener != null){
            mRefreshListener = pRefreshListener;
            mCanRefresh = true;
        }
    }

    /**
     * 正在下拉刷新
     * @date 2013-11-20 下午4:45:47
     * @change JohnWatson
     * @version 1.0
     */
    private void onRefresh() {
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        }
    }

    /**
     * 下拉刷新完成
     * @date 2013-11-20 下午4:44:12
     * @change JohnWatson
     * @version 1.0
     */
    public void onRefreshComplete() {
        // 下拉刷新后是否显示第一条Item
        if(mIsMoveToFirstItemAfterRefresh)setSelection(0);

        mHeadState = DONE;
        // 最近更新: Time
        mLastUpdatedTextView.setText(
                getResources().getString(R.string.p2refresh_refresh_lasttime) +
                        new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA).format(new Date()));
        changeHeaderViewByState();
    }

    public void setAdapter(BaseAdapter adapter) {
        // 最近更新: Time
        mLastUpdatedTextView.setText(
                getResources().getString(R.string.p2refresh_refresh_lasttime) +
                        new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA).format(new Date()));
        super.setAdapter(adapter);
    }
}
