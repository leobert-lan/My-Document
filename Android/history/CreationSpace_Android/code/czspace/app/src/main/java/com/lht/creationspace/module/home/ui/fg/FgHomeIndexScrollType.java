package com.lht.creationspace.module.home.ui.fg;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.fragment.BaseFragment;
import com.lht.creationspace.adapter.FgViewPagerAdapter;
import com.lht.creationspace.customview.tab.TabManager;
import com.lht.creationspace.customview.popup.PopupPublishTypeChooseWin;
import com.lht.creationspace.customview.toolBar.msg.ToolbarTheme4;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.vinterface.IAsyncProtectedFragment;
import com.lht.creationspace.util.debug.DLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
@Deprecated
public class FgHomeIndexScrollType extends BaseFragment implements IAsyncProtectedFragment,
        TabManager.OnTabSelectedListener, IVerifyHolder ,ViewPager.OnPageChangeListener{

    private static final String PAGENAME = "FgHomeIndex";
    private ToolbarTheme4 titleBar;

    /**
     * 推荐
     */
    private RadioButton rbRecommend;
    /**
     * 关注
     */
    private RadioButton rbAttention;
    /**
     * 广场
     */
    private RadioButton rbSquare;
    /**
     * 招募
     */
//    private RadioButton rbRecruit;

    private View indicator1;
    private View indicator2;
    private View indicator3;
//    private View indicator4;

    private AbsHybridFragmentActivity parent;

    /**
     * 推荐
     */
    private AbsHybridFragmentBase fgRecommend;

    /**
     * 关注
     */
    private AbsHybridFragmentBase fgFavor;

    /**
     * 广场
     */
    private AbsHybridFragmentBase fgSquare;

    //    /**
//     * 招募（广告）
//     */
//    private AbsHybridFragmentBase fgAds;
    private PopupPublishTypeChooseWin typeChooseWin;

    private ViewPager viewPager;

    private FgViewPagerAdapter fgViewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_fg_home_index_scroll_type,
                container, false);
        parent = (AbsHybridFragmentActivity) getActivity();
        initView(contentView);
        initVariable();
        initEvent();
        return contentView;
    }

    HashMap<RadioButton, View> hashMap = new HashMap<>();

    @Override
    protected void initView(View contentView) {
        rbRecommend = (RadioButton) contentView.findViewById(R.id.fgindex_rb_recommend);
        rbAttention = (RadioButton) contentView.findViewById(R.id.fgindex_rb_attention);
        rbSquare = (RadioButton) contentView.findViewById(R.id.fgindex_rb_square);
//        rbRecruit = (RadioButton) contentView.findViewById(R.id.fgindex_rb_recruit);

        indicator1 = contentView.findViewById(R.id.fgindex_indicator1);
        indicator2 = contentView.findViewById(R.id.fgindex_indicator2);
        indicator3 = contentView.findViewById(R.id.fgindex_indicator3);
//        indicator4 = contentView.findViewById(R.id.fgindex_indicator4);

        titleBar = (ToolbarTheme4) contentView.findViewById(R.id.titlebar);

        fgRecommend = AbsHybridFragmentBase.newInstance(FgHomeIndexRecommend.class, parent);
        fgFavor = AbsHybridFragmentBase.newInstance(FgHomeIndexFavor.class, parent);
        fgSquare = AbsHybridFragmentBase.newInstance(FgHomeIndexSquare.class, parent);
//        fgAds = AbsHybridFragmentBase.newInstance(FgHomeIndexAds.class, parent);

        viewPager = (ViewPager) contentView.findViewById(R.id.fg_index_viewpager);
    }

    @Override
    protected void initVariable() {
        hashMap.put(rbRecommend, indicator1);
        hashMap.put(rbAttention, indicator2);
        hashMap.put(rbSquare, indicator3);
//        hashMap.put(rbRecruit, indicator4);

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(fgRecommend);
        fragments.add(fgFavor);
        fragments.add(fgSquare);

        fgViewPagerAdapter = new FgViewPagerAdapter(getChildFragmentManager(),fragments);
    }

    @Override
    protected void initEvent() {
        String strTitle = "创意空间";
        SpannableString spannableString = new SpannableString(strTitle);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(parent, R.color.main_green_dark)),
                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(parent, R.color.black)),
                2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleBar.setTitle(spannableString);

        viewPager.setAdapter(fgViewPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        TabManager.init(this, rbRecommend, rbAttention, rbSquare);//, rbRecruit);
        rbRecommend.performClick();
        typeChooseWin = new PopupPublishTypeChooseWin(parent);
        titleBar.setOnRightImageClickListener(new ICallback() {
            @Override
            public void onCallback() {
                showPublishCover();
            }
        });
        titleBar.setOnNavMessageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2MessageActivity();
            }
        });
        typeChooseWin.setOnProjectPublish(new PopupPublishTypeChooseWin.OnPublishClickListener() {
            @Override
            public void onProjectPublish() {
                typeChooseWin.dismiss();
                ((HomeActivity)parent).callPublishProject();
            }

            @Override
            public void onArticlePublish() {
                typeChooseWin.dismiss();
                ((HomeActivity)parent).callPublishArticle();
            }

        });
    }

//    /**
//     * 跳转到消息页面
//     */
//    private void jump2MessageActivity() {
//        Intent intent = new Intent(getActivity(), MessageActivity.class);
//        startActivity(intent);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 显示发布popup
     */
    private void showPublishCover() {
        typeChooseWin.show();
    }

    @Override
    protected String getPageName() {
        return FgHomeIndexScrollType.PAGENAME;
    }

    @Override
    public void showWaitView(boolean isProtectNeed) {
        parent.showWaitView(isProtectNeed);
    }

    @Override
    public void cancelWaitView() {
        parent.cancelWaitView();
    }

    @Override
    public Resources getAppResource() {
        return getResources();
    }

    @Override
    public void showMsg(String msg) {
        parent.showMsg(msg);
    }

    @Override
    public void showErrorMsg(String msg) {
        parent.showMsg(msg);
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        mLoginInfo.copy(event.getLoginInfo());
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {

    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    private void refreshTab(int selectedId) {
        if (hashMap == null || hashMap.isEmpty()) {
            return;
        }
        Set<RadioButton> keys = hashMap.keySet();
        for (RadioButton radioButton : keys) {
            if (radioButton.getId() == selectedId) {
                //字体效果，线
                radioButton.getPaint().setFakeBoldText(true);
                hashMap.get(radioButton).setVisibility(View.VISIBLE);
            } else {
                //字体效果，线
                radioButton.getPaint().setFakeBoldText(false);
                hashMap.get(radioButton).setVisibility(View.INVISIBLE);
            }
        }
    }

//    private static final int ID_FRAGMENT = R.id.childfragment;

    @Override
    public void onTabSelect(CompoundButton selectedTab) {
        int id = selectedTab.getId();
        refreshTab(id);
        switch (selectedTab.getId()) {
            case R.id.fgindex_rb_recommend:
                viewPager.setCurrentItem(0);
//                switchFragment(ID_FRAGMENT, fgRecommend);
                break;
            case R.id.fgindex_rb_attention:
                viewPager.setCurrentItem(1);
//                switchFragment(ID_FRAGMENT, fgFavor);
                break;
            case R.id.fgindex_rb_square:
                viewPager.setCurrentItem(2);
//                switchFragment(ID_FRAGMENT, fgSquare);
                break;
//            case R.id.fgindex_rb_recruit:
//                switchFragment(ID_FRAGMENT, fgAds);
//                break;
            default:
                break;
        }
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //ignore
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rbRecommend.performClick();
                break;
            case 1:
                rbAttention.performClick();
                break;
            case 2:
                rbSquare.performClick();
                break;
            default:
                DLog.e(FgHomeIndexScrollType.class,"检查viewpager内容数量");
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //ignore
    }
}
