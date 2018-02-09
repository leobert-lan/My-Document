package com.lht.creationspace.base.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.base.keyback.IKeyBackHandlerChain;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.util.AppPreference;
import com.lht.creationspace.util.I18N;
import com.lht.creationspace.util.PlatformUtils;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

import java.io.File;

public abstract class BaseActivity extends AppCompatActivity implements IPopupHolder {//implement at de undercase

    private Toolbar toolbar;

    private static boolean languageLoaded = false;

    public static final String KEY_ORIGINCLASS_SHOULDREDIRECTONFINISH = "_key_origin";

    public static final String KEY_IS_RESTRICT_INTENT = "_key_is_restrict_intent";

    private String originClassShouldRedirectOnfinish = null;

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (PlatformUtils.isXiaoMi()) {
                restoreEnterAnimForXiaoMi();
            }
        }
        if (needTransparentStatusBar()) {
            transparentStatusBar();
        } else if (getCustomStatusBarColor() != 0) {
            setStatusBarColor(getCustomStatusBarColor());
        }
        parseOriginClassShouldRestrictOnfinish(getIntent());
        autoLoadLanguage();

        MainApplication.addActivity(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        toolbar = (Toolbar) inflater.inflate(R.layout.toolbar_default, null);
        setSupportActionBar(toolbar);
        equipKeyBackHandlerChain();
        restoreScale();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getOurInstance().updateCurrentTopActivity(getActivity());
    }

    private void restoreScale() {
        Resources resource = getResources();
        Configuration configuration = resource.getConfiguration();
        configuration.fontScale = 1.0f;//设置字体的缩放比例
        resource.updateConfiguration(configuration, resource.getDisplayMetrics());
    }

    /**
     * @return 默认使用主背景色，如果是0，不设置自定义颜色
     */
    protected int getCustomStatusBarColor() {
        return R.color.primary_background;
    }

    private void setStatusBarColor(int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getActivity(), colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected boolean needTransparentStatusBar() {
        return false;
    }

    private void transparentStatusBar() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); //底部导航栏相关
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  //底部导航栏相关
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);  //底部导航栏相关
        }
    }

    protected void equipKeyBackHandlerChain() {
//        getIKeyBackHandlerChain().next()
    }

    /**
     * desc: 获取activity
     */
    public abstract BaseActivity getActivity();

    private IKeyBackHandlerChain keyBackHandlerChain;

    protected final IKeyBackHandlerChain getIKeyBackHandlerChain() {
        if (keyBackHandlerChain == null) {
            keyBackHandlerChain = IKeyBackHandlerChain.KeyBackHandlerChainImpl.newInstance();
        }
        return keyBackHandlerChain;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
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
        if (intent == null)
            return;
        originClassShouldRedirectOnfinish =
                intent.getStringExtra(KEY_ORIGINCLASS_SHOULDREDIRECTONFINISH);
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

//    private static boolean hasTryToDoJobsAboutPermissions = false;

    protected synchronized void doJobsAboutPermissions() {
        grantPermissions();
    }

    private boolean checkWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PermissionsManager.getInstance().hasPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

    public void grantCameraPermission(PermissionsResultAction action) {
//        Log.i("lmsg", "call grant camera permission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //程序可以认为已经异常，保护安全做deny
//            Log.e("lmsg", "error call grant camera permission below Android M");
            action.onDenied(Manifest.permission.CAMERA);
            return;
        }
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{Manifest.permission.CAMERA}, action);
//        Log.i("lmsg", "do grant camera permission");
    }

    /**
     * @return the grant state of camera permission,true if granted
     */
    public boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean b = PermissionsManager.getInstance().hasPermission(getActivity(),
                    Manifest.permission.CAMERA);
            if (!b) {
                ToastUtils.show(getActivity(), "缺少相机权限，无法调用相机", ToastUtils.Duration.s);
            }
            return b;
        }
        return true;
    }


    protected void grantPermissions() {
//        Log.d("lmsg", "self call grantPermissions ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(getActivity(), new
                PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        DLog.i(getClass(), "All permission granted");
                    }

                    @Override
                    public void onDenied(String permission) {
                        DLog.e(getClass(), "permission denied:" + permission);
//                        grant(permission);
                    }
                });
    }

    protected void grant(final String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{permission}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        toolbar = null;//avoid memory leak
        MainApplication.removeActivity(getActivity());
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DLog.i(DLog.Lmsg.class, "requestPermission:\r\n" + JSON.toJSONString(permissions)
                + "\r\nresult:\r\n" + JSON.toJSONString(grantResults));
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    /**
     * 检测外部存储是否加载
     */
    protected boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    protected final void start(Class<? extends Activity> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

//    @Deprecated
//    protected final void start(Class<? extends Activity> clazz, String key, String stringValue) {
//        Intent intent = new Intent(getActivity(), clazz);
//        intent.putExtra(key, stringValue);
//        startActivity(intent);
//    }
//
//    @Deprecated
//    protected final void start(Class<? extends Activity> clazz, String key, boolean booleanValue) {
//        Intent intent = new Intent(getActivity(), clazz);
//        intent.putExtra(key, booleanValue);
//        startActivity(intent);
//    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (intent instanceof LoginIntentFactory.LoginIntent) {
            overridePendingTransition(R.anim.slide_bottom_in, 0);
        } else {
            overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
        }
    }

    @Override
    public void finish() {
        super.finish();
        displayFinishAnim();
    }

    public void finishWithoutOverrideAnim() {
        super.finish();
    }

    protected void displayFinishAnim() {
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    public boolean hideSoftInputPanel() {
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
    public void onBackPressed() {
        boolean hasHandled = getIKeyBackHandlerChain().onBackPressed();
        if (!hasHandled) {
            if (hideSoftInputPanel()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 100);
            } else {
                onLastCallInKeyBackChain();
                super.onBackPressed();
            }
        }
    }

    protected void onLastCallInKeyBackChain() {
        restrict2DestOnFinish();
    }

    private void restrict2DestOnFinish() {
        try {
            if (StringUtil.isEmpty(originClassShouldRedirectOnfinish)) {
                DLog.i(UMengActivity.class, "开始时重定向页面未指定,重新判断是否中途重定向");
                parseOriginClassShouldRestrictOnfinish(getIntent());
                if (StringUtil.isEmpty(originClassShouldRedirectOnfinish)) {
                    DLog.i(UMengActivity.class, "始终未进行页面重定向");
                    return;
                } else {
                    DLog.d(UMengActivity.class, "中途重定向：" + originClassShouldRedirectOnfinish);
                }
            }

            Class dest = Class.forName(originClassShouldRedirectOnfinish);
            Intent intent = new Intent(getActivity(), dest);
            intent.putExtra(KEY_IS_RESTRICT_INTENT, true);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
}
