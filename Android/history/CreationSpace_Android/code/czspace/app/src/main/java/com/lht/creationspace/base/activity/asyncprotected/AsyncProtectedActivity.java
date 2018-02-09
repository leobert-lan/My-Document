package com.lht.creationspace.base.activity.asyncprotected;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.toast.ToastUtils;

public abstract class AsyncProtectedActivity extends UMengActivity
        implements IActivityAsyncProtected, IPopupHolder {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean needDispatch = true;

    // 子类实现 public abstract ProgressBar getProgressBar();

    public void setActiveStateOfDispatchOnTouch(boolean b) {
        needDispatch = b;
    }

    public void showWaitView(boolean isProtectNeed) {
        if (getProgressBar() != null) {
            getProgressBar().setVisibility(View.VISIBLE);
            getProgressBar().bringToFront();
        } else {
            DLog.i(getClass(), "progressbar is null");
        }
        if (isProtectNeed)
            setActiveStateOfDispatchOnTouch(false);
    }

    /**
     * desc: TODO: 描述方法
     * 注意：如果仅progressBar显示需要拦截屏幕，可以这样做，否则需要进行判断或者设计多个flag共同作用
     */
    public void cancelWaitView() {
        if (getProgressBar() != null) {
            getProgressBar().setVisibility(View.GONE);
        } else {
            DLog.i(getClass(), "progressbar is null");
        }
        setActiveStateOfDispatchOnTouch(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (needDispatch)
            return super.dispatchTouchEvent(ev);
        else
            return false;
    }

    @Override
    public BaseActivity getHolderActivity() {
        return getActivity();
    }

    @Override
    public void setPenetrable(Activity activity, boolean isProtectNeed) {
        if (activity instanceof AsyncProtectedActivity)
            ((AsyncProtectedActivity) activity)
                    .setActiveStateOfDispatchOnTouch(!isProtectNeed);
    }

    @Override
    public Resources getAppResource() {
        return getActivity().getResources();
    }

    @Override
    protected void onDestroy() {
        IApiRequestPresenter presenter = getApiRequestPresenter();
        if (presenter != null) {
            presenter.cancelRequestOnFinish(getActivity());
        }
        super.onDestroy();
    }

    protected abstract IApiRequestPresenter getApiRequestPresenter();

    public void showMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void showHeadUpMsg(int type, String msg) {
        ToastUtils.showHeadUp(getActivity(),msg,type);
    }
}
