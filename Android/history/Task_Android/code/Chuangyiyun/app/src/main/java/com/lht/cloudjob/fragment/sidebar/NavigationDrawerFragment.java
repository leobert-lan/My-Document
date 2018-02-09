package com.lht.cloudjob.fragment.sidebar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.adapter.NavigationDrawerAdapter;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.ScrimInsetsFrameLayout;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.bars.OnNavigationDrawerItemSelectedListener;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.pojo.LoginType;
import com.lht.cloudjob.mvp.model.pojo.NavigationItem;
import com.lht.cloudjob.mvp.model.pojo.NavigationUserInfo;
import com.lht.cloudjob.mvp.presenter.HomeActivityPresenter;
import com.lht.cloudjob.util.SideBarNavigationItemDatas;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.phonebasic.PhoneCallUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * <p><b>Package</b> com.lht.cloudjob.fragment.sidebar
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NavigationDrawerFragment
 * <p><b>Description</b>: 侧边栏
 * Created by leobert on 2016/7/1.
 */
public class NavigationDrawerFragment extends Fragment implements
        OnNavigationDrawerItemSelectedListener {

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREFERENCES_FILE = "my_app_settings";
    private OnNavigationDrawerItemSelectedListener mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;

    /**
     * 用户昵称
     */
    private TextView txtNickname;

    /**
     * 用户id
     */
    private TextView txtUserId;

    private TextView txtCallUs;

    /**
     * 勋章显示
     */
    private ImageView imgVipMadel;

    /**
     * 头像显示
     */
    private ImageView imgAvatar;

    /**
     * 认证奖章
     */
    private ImageView imgIdentify;

    private TextView btnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_google, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);


        imgAvatar = (ImageView) view.findViewById(R.id.sidebar_img_avatar);
        imgVipMadel = (ImageView) view.findViewById(R.id.sidebar_img_madel);
        imgIdentify = (ImageView) view.findViewById(R.id.sidebar_img_identify);

        txtNickname = (TextView) view.findViewById(R.id.sidebar_tv_nickname);
        txtUserId = (TextView) view.findViewById(R.id.sidebar_tv_userid);
        txtCallUs = (TextView) view.findViewById(R.id.sidebar_tv_callus);
        btnLogin = (TextView) view.findViewById(R.id.sidebar_login);

        //初始化状态
        updateType(LoginType.Unlogin);

        //未登录情况下点击头像触发登录
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == LoginType.Unlogin) {
                    Intent i = LoginIntentFactory.create(getActivity(), HomeActivityPresenter
                            .LoginTrigger.SidebarBtnLogin);
                    getActivity().startActivity(i);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = LoginIntentFactory.create(getActivity(), HomeActivityPresenter
                        .LoginTrigger.SidebarBtnLogin);
                getActivity().startActivity(i);
            }
        });
        txtCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneCallUtil.makePhoneCall(getActivity(), IPublicConst.TEL);
            }
        });

        view.findViewById(R.id.googleDrawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防止击穿
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(getActivity(),
                PREF_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (OnNavigationDrawerItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        if (mFragmentContainerView.getParent() instanceof ScrimInsetsFrameLayout) {
            mFragmentContainerView = (View) mFragmentContainerView.getParent();
        }
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.myPrimaryDarkColor));
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveSharedSetting(getActivity(), PREF_USER_LEARNED_DRAWER, "true");
                }

                getActivity().invalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            openDrawer();
        }
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void openDrawer() {
        MobclickAgent.onEvent(getActivity(), IUmengEventKey.KEY_OPEN_SLIDING_MENU);
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    /**
     * Changes the icon of the drawer to back
     */
    public void showBackButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled
                    (true);
        }
    }

    /**
     * Changes the icon of the drawer to menu
     */
    public void showDrawerButton() {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled
                    (false);
        }
        mActionBarDrawerToggle.syncState();
    }

    //设置返回的标题
    void selectItem(int position) {
        mCurrentSelectedPosition = position;
        boolean needClose = true; //默认需要关闭
        if (mCallbacks != null) {
            needClose = mCallbacks.onNavigationDrawerItemSelected(type, position);
        }

        if (mDrawerLayout != null && needClose) {
            closeDrawer();
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public boolean onNavigationDrawerItemSelected(LoginType type, int position) {
        //TODO
        selectItem(position);
        //不需要处理返回值，selectItem已消费
        return false;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context
                .MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }


    public void updateUserInfo(NavigationUserInfo info) {
        //TODO
        Picasso.with(getActivity()).load(info.getAvatarUrl())
                .diskCache(BaseActivity.getLocalImageCache())
                .placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default)
                .fit().into(imgAvatar);

        if (info.getVipImgResId() > 0)
            Picasso.with(getActivity()).load(info.getVipImgResId()).fit().into(imgVipMadel);

        txtNickname.setText(info.getNickname());

        if (!StringUtil.isEmpty(info.getUsername())) {
            String _s = String.format("ID:%s", info.getUsername());
            txtUserId.setText(_s);
        }
    }

    private LoginType type;

    public void updateType(LoginType type) {
        if (type == null) {
            DLog.d(getClass(), new DLog.LogLocation(), "logintype is null");
            type = LoginType.Unlogin;
        }
        this.type = type;

        switch (type) {
            case Unlogin:
                changeTypeToUnLogin();
                break;
            case UnVerified:
                changeTypeToUnVerified();
                break;
            case PersonalVerified:
                changeTypeToPersonalVerified();
                break;
            case EnterpriseVerified:
                changeTypeToEnterpriseVerified();
                break;
            default:
                break;
        }
    }

    private void changeTypeToEnterpriseVerified() {
        final List<NavigationItem> navigationItems = SideBarNavigationItemDatas.getInstance(getActivity())
                .getMenuLogined();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);

        //设置选取item事件监听器
        adapter.setItemSelectedListener(this);

        btnLogin.setVisibility(View.GONE);
        txtUserId.setVisibility(View.VISIBLE);
        txtNickname.setVisibility(View.VISIBLE);
        imgVipMadel.setVisibility(View.VISIBLE);
        imgIdentify.setVisibility(View.VISIBLE);
        imgIdentify.setImageResource(R.drawable.v1010_drawable_icon_entidentified_medal);
        mDrawerList.setAdapter(adapter);
    }

    private void changeTypeToPersonalVerified() {
        final List<NavigationItem> navigationItems = SideBarNavigationItemDatas.getInstance(getActivity())
                .getMenuLogined();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);

        //设置选取item事件监听器
        adapter.setItemSelectedListener(this);

        btnLogin.setVisibility(View.GONE);
        txtUserId.setVisibility(View.VISIBLE);
        txtNickname.setVisibility(View.VISIBLE);
        imgVipMadel.setVisibility(View.VISIBLE);
        imgIdentify.setVisibility(View.VISIBLE);
        imgIdentify.setImageResource(R.drawable.v1010_drawable_icon_peridentified_medal);
        mDrawerList.setAdapter(adapter);

    }

    private void changeTypeToUnVerified() {
        final List<NavigationItem> navigationItems = SideBarNavigationItemDatas.getInstance(getActivity())
                .getMenuLogined();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);

        //设置选取item事件监听器
        adapter.setItemSelectedListener(this);

        btnLogin.setVisibility(View.GONE);

        txtUserId.setVisibility(View.VISIBLE);
        txtNickname.setVisibility(View.VISIBLE);
        imgVipMadel.setVisibility(View.VISIBLE);
        imgIdentify.setVisibility(View.GONE);
        mDrawerList.setAdapter(adapter);
    }

    private void changeTypeToUnLogin() {
        final List<NavigationItem> navigationItems = SideBarNavigationItemDatas.getInstance(getActivity())
                .getMenuUnlogin();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems);

        //设置选取item事件监听器
        adapter.setItemSelectedListener(this);
//TODO
        btnLogin.setVisibility(View.VISIBLE);
        txtUserId.setVisibility(View.GONE);
        txtNickname.setVisibility(View.GONE);
        imgVipMadel.setVisibility(View.GONE);
        imgIdentify.setVisibility(View.GONE);
        mDrawerList.setAdapter(adapter);
    }

}
