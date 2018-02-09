package com.lht.creationspace.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.module.msg.ui.MessageActivity;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.util.debug.DLog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        MobclickAgent.onPageStart(getPageName());
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    protected abstract void initView(View contentView);

    protected abstract void initVariable();

    protected abstract void initEvent();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        Log.e("lmsg", getPageName() + " onHiddenchange");
        if (hidden) {
            onRestrictPause();
        } else {
            onRestrictResume();
        }
    }

    public void onRestrictPause() {
        MobclickAgent.onPageEnd(getPageName());

//        Log.e("lmsg", getPageName() + " onRestrictPause");
        if (getCurrentChildFragment() != null) {
            if (getCurrentChildFragment() instanceof BaseFragment) {
                BaseFragment currentChild = (BaseFragment) getCurrentChildFragment();
                currentChild.onRestrictPause();
            }
        }
    }

    public void onRestrictResume() {
        MobclickAgent.onPageStart(getPageName());

//        Log.e("lmsg", getPageName() + " onRestrictResume");
        if (getCurrentChildFragment() != null) {
            if (getCurrentChildFragment() instanceof BaseFragment) {
                BaseFragment currentChild = (BaseFragment) getCurrentChildFragment();
                currentChild.onRestrictResume();
            }
        }
    }


    protected boolean hasLogin() {
        return IVerifyHolder.mLoginInfo.isLogin();
    }

    protected void setUi2UnLoginState() {
        //stub
    }

    protected void setUi2LoginState(LoginInfo loginInfo) {
        //stub
    }

    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        setUi2LoginState(event.getLoginInfo());
    }

    @Override
    public void onDestroy() {
        DLog.d(getClass(), "onDestroy:" + getClass().getSimpleName());
        super.onDestroy();
    }

    protected abstract String getPageName();


    public final void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getPageName());
    }

    public final void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getPageName());
    }

    private Fragment saveFg;

    protected void switchFragment(int rid, Fragment to) {
        if (to == null) {
            // may throw you a IllegalArgumentException be better
            return;
        }
        if (saveFg != to) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                if (saveFg != null) {
                    ft.hide(saveFg);
                }
                ft.add(rid, to).show(to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                if (saveFg != null) {
                    ft.hide(saveFg);
                }
                ft.show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            saveFg = to;
        }
    }

    protected Fragment getCurrentChildFragment() {
        return saveFg;
    }

    /**
     * 获取系统相册的位置
     */
    public synchronized File getSystemImageDir() {
        return getMainApplication().getSystemImageDir();
    }

    protected MainApplication getMainApplication() {
        return MainApplication.getOurInstance();
    }

    public synchronized File getLocalDownloadCacheDir() {
        return getMainApplication().getLocalDownloadCacheDir();
    }

    public synchronized File getSystemDownloadDir() {
        return getMainApplication().getSystemDownloadDir();
    }

    public File getLocalThumbnailCacheDir() {
        return getMainApplication().getLocalThumbnailCacheDir();
    }

    public File getLocalPreviewCacheDir() {
        return getMainApplication().getLocalPreviewCacheDir();

    }

    /**
     * 跳转到消息页面
     */
    protected void jump2MessageActivity() {
//        Intent intent = new Intent(getActivity(), MessageActivity.class);
//        startActivity(intent);
        MessageActivity.getLauncher(getActivity()).launch();
    }
}
