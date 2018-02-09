package com.lht.cloudjob.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.anthonycr.grant.PermissionsResultAction;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.interfaces.custompopupwins.IPopupHolder;
import com.lht.cloudjob.interfaces.umeng.IUmengReport;
import com.lht.cloudjob.util.AppPreference;
import com.lht.cloudjob.util.I18N;
import com.lht.cloudjob.util.PlatformUtils;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


/**
 *
 */
public abstract class UMengActivity extends AppCompatActivity implements IUmengReport,
        IPopupHolder {

    protected CustomPopupWindow pw = null;

    public static final String tag = "umeng";

    private static boolean languageLoaded = false;

    public static final String KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH = "_key_origin";

    public static final String KEY_IS_RESTRICT_INTENT = "_key_is_restrict_intent";

    private String originClassShouldRestrictOnfinish = null;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PlatformUtils.isXiaoMi()) {
                restoreEnterAnimForXiaoMi();
            }
        }
        parseOriginClassShouldRestrictOnfinish(getIntent());
        autoLoadLanguage();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void restoreEnterAnimForXiaoMi() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_left);
        getWindow().setEnterTransition(slide);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseOriginClassShouldRestrictOnfinish(intent);
    }

    private void parseOriginClassShouldRestrictOnfinish(Intent intent) {
        Log.e("lmsg","parseOriginClassShouldRestrictOnfinish");
        if (intent == null)
            return;
        originClassShouldRestrictOnfinish =
                intent.getStringExtra(KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH);
    }

    protected void autoLoadLanguage() {
        if (languageLoaded)
            return;
        languageLoaded = true;
        AppPreference appPreference = ((MainApplication) getApplication()).getAppPreference();
        int languageCode = appPreference.getInt(I18N.KEY_LANGUAGE, 0);
        I18N.Language language = null;
        switch (languageCode) {
            case 1:
                language = I18N.Language.EN;
                break;
            case 2:
                language = I18N.Language.ZH_CN;
                break;
            case 3:
                language = I18N.Language.ZH_TW;
                break;
            default:
                //跟随系统，不做设置? Locale.getDefault().getLanguage()
                break;
        }
        I18N.changeLanguage(getActivity(), language);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 友盟统计 页面统计-页面打开
        reportPageStart(getPageName());
        MobclickAgent.onResume(getActivity());
        DLog.d(MobclickAgent.class, new DLog.LogLocation(), "onresume pagename:" + getPageName() + "; check:"
                + getActivity().getPageName());
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 友盟统计 页面统计-页面关闭
        reportPageEnd(getPageName());
        MobclickAgent.onPause(getActivity());
        DLog.d(MobclickAgent.class, new DLog.LogLocation(), "onpause pagename:" + getPageName() + "; check:"
                + getActivity().getPageName());
    }

    public abstract boolean checkCameraPermission();

    public abstract void grantCameraPermission(PermissionsResultAction action);

    /**
     * 报告页面打开
     *
     * @param pageName the page name
     */
    public void reportPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }

    /**
     * desc: 报告页面关闭
     *
     * @param pageName the page name
     */
    public void reportPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);
    }

    /**
     * desc: 获取页面名称
     *
     * @return String
     */
    protected abstract String getPageName();

    /**
     * desc: 获取activity
     */
    @Override
    public abstract UMengActivity getActivity();

    /**
     * desc: 友盟统计-报告计数事件
     *
     * @param eventKey 事件key
     */
    public void reportCountEvent(String eventKey) {
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    public void reportCountEvent(String eventKey,
                                 HashMap<String, String> attrMap) {
        MobclickAgent.onEvent(getActivity(), eventKey, attrMap);
    }

    @Override
    public void reportCalcEvent(String eventKey,
                                HashMap<String, String> attrMap, int du) {
        MobclickAgent.onEventValue(getActivity(), eventKey, attrMap, du);
    }

    protected boolean hideSoftInputPanel() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return imm.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;

    }

    protected void showSoftInputPanel(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, 0);
    }

    @Override
    public void onActionSheetDismiss() {

    }

    /**
     * desc: 实例化View
     */
    protected abstract void initView();

    /**
     * desc: 实例化必要的参数，以防止initEvent需要的参数空指针
     */
    protected abstract void initVariable();

    /**
     * desc: 监听器设置、adapter设置等
     */
    protected abstract void initEvent();


    public UMengActivity getHolderActivity() {
        return this.getActivity();
    }


    protected void startActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    @Override
    public void setLatestPopupWindow(CustomPopupWindow latestPopupWindow) {
        this.pw = latestPopupWindow;
    }

    protected final void start(Class<? extends Activity> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    protected final void start(Class<? extends Activity> clazz, String key, String stringValue) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(key, stringValue);
        startActivity(intent);
    }

    protected final void start(Class<? extends Activity> clazz, String key, boolean booleanValue) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtra(key, booleanValue);
        startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PlatformUtils.isXiaoMi()) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                return;
            }
        }
//        if (!PlatformUtils.isXiaoMi()) {
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
//        }
    }

    @Override
    public void finish() {
        super.finish();
//        if (!PlatformUtils.isXiaoMi()) {
        restoreFinishAnim();
//        }
    }

    protected void restoreFinishAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void onBackPressed() {
        if (pw != null) {
            closePopupWindow();
        } else if (hideSoftInputPanel()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 100);
        } else {
            onLastChainInBackPressedWillCall();
            super.onBackPressed();
        }
    }

    protected void onLastChainInBackPressedWillCall() {
        restrict2DestOnFinish();
    }

    private void restrict2DestOnFinish() {
        try {
            if (StringUtil.isEmpty(originClassShouldRestrictOnfinish)) {
                DLog.i(UMengActivity.class, "开始时重定向页面未指定,重新判断是否中途重定向");
                parseOriginClassShouldRestrictOnfinish(getIntent());
                if (StringUtil.isEmpty(originClassShouldRestrictOnfinish)) {
                    DLog.i(UMengActivity.class, "始终未进行页面重定向");
                    return;
                } else {
                    DLog.d(UMengActivity.class, "中途重定向：" + originClassShouldRestrictOnfinish);
                }
            }

            Class dest = Class.forName(originClassShouldRestrictOnfinish);
            Intent intent = new Intent(getActivity(), dest);
            intent.putExtra(KEY_IS_RESTRICT_INTENT, true);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    protected void closePopupWindow() {
        if (pw != null) {
            pw.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
//        hideSoftInputPanel();
        super.onDestroy();
    }

    protected void focus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.requestFocusFromTouch();
    }

    @Override
    public void setPenetrable(Activity activity, boolean isProtectNeed) {
        if (activity instanceof AsyncProtectedActivity) {
            ((AsyncProtectedActivity) activity).setActiveStateOfDispatchOnTouch(!isProtectNeed);
        }
    }
}
