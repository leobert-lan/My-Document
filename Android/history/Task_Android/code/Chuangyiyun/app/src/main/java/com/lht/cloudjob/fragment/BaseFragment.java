package com.lht.cloudjob.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lht.cloudjob.R;
import com.lht.cloudjob.util.debug.DLog;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    protected abstract void initView(View contentView);

    protected abstract void initVariable();

    protected abstract void initEvent();

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public void onDestroy() {
        DLog.e(getClass(), "onDestroy:" + getClass().getSimpleName());
        super.onDestroy();
    }

    protected abstract String getPageName();


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
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
}
