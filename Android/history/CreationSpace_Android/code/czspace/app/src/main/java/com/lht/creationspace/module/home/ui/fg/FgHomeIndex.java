package com.lht.creationspace.module.home.ui.fg;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.fragment.BaseFragment;
import com.lht.creationspace.customview.tab.TabManager;
import com.lht.creationspace.customview.popup.PopupPublishTypeChooseWin;
import com.lht.creationspace.customview.toolBar.msg.ToolbarTheme8;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.vinterface.IAsyncProtectedFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by chhyu on 2017/2/16.
 * home-首页 大容器
 */

public class FgHomeIndex extends BaseFragment implements IAsyncProtectedFragment,
        TabManager.OnTabSelectedListener, IVerifyHolder {

    private static final String PAGENAME = "FgHomeIndex";
    private ToolbarTheme8 titleBar;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fg_home_index, container, false);
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

        titleBar = (ToolbarTheme8) contentView.findViewById(R.id.titlebar);


        fgRecommend = AbsHybridFragmentBase.newInstance(FgHomeIndexRecommend.class, parent);
        fgFavor = AbsHybridFragmentBase.newInstance(FgHomeIndexFavor.class, parent);
        fgSquare = AbsHybridFragmentBase.newInstance(FgHomeIndexSquare.class, parent);
//        fgAds = AbsHybridFragmentBase.newInstance(FgHomeIndexAds.class, parent);
    }

    @Override
    protected void initVariable() {
        hashMap.put(rbRecommend, indicator1);
        hashMap.put(rbAttention, indicator2);
        hashMap.put(rbSquare, indicator3);
//        hashMap.put(rbRecruit, indicator4);

    }

    @Override
    protected void initEvent() {
//        String strTitle = "创意空间";
//        SpannableString spannableString = new SpannableString(strTitle);
//        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(parent, R.color.main_green_dark)),
//                0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(parent, R.color.black)),
//                2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////        titleBar.setTitle(spannableString);
        titleBar.setBackgroundResource(R.color.cyy_h9);

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
                ((HomeActivity) parent).callPublishProject();
            }

            @Override
            public void onArticlePublish() {
                typeChooseWin.dismiss();
                ((HomeActivity) parent).callPublishArticle();
            }
        });
        titleBar.hideTitleBottomDividerLine();
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
        return FgHomeIndex.PAGENAME;
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
        return parent.getAppResource();
    }

    @Override
    public void showMsg(String msg) {
        parent.showMsg(msg);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
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

    private static final int ID_FRAGMENT = R.id.childfragment;

    @Override
    public void onTabSelect(CompoundButton selectedTab) {
        int id = selectedTab.getId();
        refreshTab(id);
        switch (selectedTab.getId()) {
            case R.id.fgindex_rb_recommend:
                switchFragment(ID_FRAGMENT, fgRecommend);
                break;
            case R.id.fgindex_rb_attention:
                switchFragment(ID_FRAGMENT, fgFavor);
                break;
            case R.id.fgindex_rb_square:
                switchFragment(ID_FRAGMENT, fgSquare);
                break;
//            case R.id.fgindex_rb_recruit:
//                switchFragment(ID_FRAGMENT, fgAds);
//                break;
            default:
                break;
        }
    }

}
