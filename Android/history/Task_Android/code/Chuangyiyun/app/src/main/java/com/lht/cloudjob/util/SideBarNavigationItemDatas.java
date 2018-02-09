package com.lht.cloudjob.util;

import android.content.Context;

import com.lht.cloudjob.R;
import com.lht.cloudjob.mvp.model.pojo.NavigationItem;

import java.util.ArrayList;
import java.util.List;

public class SideBarNavigationItemDatas {
    private static SideBarNavigationItemDatas mDatas;
    private List<NavigationItem> mListUnLogin;

    private List<NavigationItem> mListLogined;

    private SideBarNavigationItemDatas(Context context) {
        mListUnLogin = new ArrayList<>();
        mListUnLogin.add(new NavigationItem("个人资料", context.getResources().getDrawable(R.drawable.navigationicon_selector_personalinfo), Style.DISBALE));
        mListUnLogin.add(new NavigationItem("我的关注", context.getResources().getDrawable(R.drawable.navigationicon_selector_myattention), Style.DISBALE));
//        mListUnLogin.add(new NavigationItem("实名认证", context.getResources().getDrawable(R.drawable.navigationicon_selector_peridentify), Style.DISBALE));
//        mListUnLogin.add(new NavigationItem("企业认证", context.getResources().getDrawable(R.drawable.navigationicon_selector_entidentify), Style.DISBALE));
        mListUnLogin.add(new NavigationItem("设置", context.getResources().getDrawable(R.drawable.navigationicon_selector_setup), Style.DEFAULT));
        mListUnLogin.add(new NavigationItem("推荐给好友", context.getResources().getDrawable(R.drawable.navigationicon_selector_recommend), Style.DEFAULT));


        mListLogined = new ArrayList<>();
        mListLogined.add(new NavigationItem("个人资料",
                context.getResources().getDrawable(R.drawable.navigationicon_selector_personalinfo),
                Style.DEFAULT));
        mListLogined.add(new NavigationItem("我的关注",
                context.getResources().getDrawable(R.drawable.navigationicon_selector_myattention),
                Style.DEFAULT));
        mListLogined.add(new NavigationItem("设置",
                context.getResources().getDrawable(R.drawable.navigationicon_selector_setup),
                Style.DEFAULT));
        mListLogined.add(new NavigationItem("推荐给好友",
                context.getResources().getDrawable(R.drawable.navigationicon_selector_recommend),
                Style.DEFAULT));
    }

    public static SideBarNavigationItemDatas getInstance(Context context) {
        if (mDatas == null) {
            synchronized (SideBarNavigationItemDatas.class) {
                if (mDatas == null) {
                    mDatas = new SideBarNavigationItemDatas(context);
                }
            }
        }
        return mDatas;

    }


    public List<NavigationItem> getMenuUnlogin() {
        return new ArrayList<>(mListUnLogin);
    }

    public List<NavigationItem> getMenuLogined() {
        return new ArrayList<>(mListLogined);
    }

    //滚动事件
    public enum ScrollDirection {
        UP,
        DOWN,
        SAME
    }

    public enum Style {
        DISBALE, DEFAULT, HASLINE, NO_ICON;
    }

}
