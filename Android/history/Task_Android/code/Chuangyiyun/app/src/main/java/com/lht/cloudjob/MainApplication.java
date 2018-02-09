package com.lht.cloudjob;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.BannerInfoActivity;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.activity.innerweb.MessageInfoActivity;
import com.lht.cloudjob.clazz.BadgeNumberManager;
import com.lht.cloudjob.clazz.GlobalLifecycleMonitor;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.clazz.SingletonStack;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CategoryModel1;
import com.lht.cloudjob.mvp.model.DeviceBindModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.pojo.CaptureUriCompat;
import com.lht.cloudjob.tplogin.QQConstants;
import com.lht.cloudjob.tplogin.WeChatConstants;
import com.lht.cloudjob.util.AppPreference;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.time.TimeUtil;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.yalantis.ucrop.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;


/**
 * MainApplication
 * Created by leobert on 2016/4/7.
 */
public class MainApplication extends Application {
    private static MainApplication ourInstance;

    private static Tencent mTencent;

    private static IWXAPI mWechat;

    public static Tencent getTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQConstants.APP_ID, ourInstance); //你的id
        }
        return mTencent;
    }

    private static final SingletonStack<UMengActivity> mainActivityStack
            = new SingletonStack<>();

    public static final ArrayList<UMengActivity> activityList = new ArrayList<>();

    public static IWXAPI getWechat() {
        return mWechat;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        DLog.i(getClass(), "[CloudJob] Debug-Mode:" + BuildConfig.DEBUG);
        if (ourInstance == null) {
            ourInstance = this;
        }
        activeLifecycleMonitor();
        //角标模块激活
        BadgeNumberManager.initOnApplicationStart();

        //debug模式检测内存泄漏
//        if (BuildConfig.DEBUG) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }

        Picasso.with(getOurInstance()).setLoggingEnabled(true);
        initUserInfo();
        //jpush
        {
            JPushInterface.setDebugMode(BuildConfig.DEBUG);
            JPushInterface.init(this);
            String s = JPushInterface.getRegistrationID(getOurInstance());
            DLog.i(getClass(), "r_id:" + s);
        }

        //umeng
        {
            MobclickAgent.setDebugMode(true);
//            DLog.d(getClass(), "device info:" + UMengTestHelpler.getDeviceInfo(getOurInstance()));
        }

        EventBus.getDefault().register(this);
        Picasso.setExtendDiskCacheFile(getDefaultPicassoCacheDir());


        BridgeWebView.setDebugMode(BuildConfig.DEBUG);
        JSON.setArrayStrictMode(false);

        if (mWechat == null) {
            mWechat = WXAPIFactory.createWXAPI(this, WeChatConstants.APP_ID, false);
        }

        CategoryModel1 model = new CategoryModel1(new CategoryModelCallback());
        model.doRequest(getOurInstance());
    }

    private void activeLifecycleMonitor() {
        getOurInstance().registerActivityLifecycleCallbacks(GlobalLifecycleMonitor.getInstance());
    }

    private File getDefaultPicassoCacheDir() {
        String username = IVerifyHolder.mLoginInfo.getUsername();
        if (StringUtil.isEmpty(username)) {
            username = "default";
        }
        String tmp = Environment.getExternalStorageDirectory() + "/Vso/CloudJob/" + username
                + "/localImageCache";
        File file = new File(tmp);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private void initUserInfo() {
        SharedPreferences sp = getTokenSp();
        IVerifyHolder.mLoginInfo.setUsername(sp.getString(SPConstants.Token.KEY_USERNAME, ""));
        IVerifyHolder.mLoginInfo.setAccessToken(sp.getString(SPConstants.Token.KEY_ACCESS_TOKEN,
                ""));
    }

    public SharedPreferences getTokenSp() {
        return getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LogoutEvent event) {
        finishAll();
    }

    public void bindDevice() {
        String username = IVerifyHolder.mLoginInfo.getUsername();
        String registrationId = JPushInterface.getRegistrationID(getOurInstance());
        DeviceBindModel model = new DeviceBindModel(username, registrationId);
        model.doRequest(getOurInstance());
    }

    public synchronized void finishAll() {
        DLog.d(getClass(), "finishAll");
        for (UMengActivity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
//            activityList.remove(activity);
        }
        mainActivityStack.clear();
        activityList.clear();
    }

    /**
     * 某些特殊情况下，页面栈被打乱，难以按照原定的逻辑走，如“打补丁”会很臃肿，
     * 在必要的情况下打开主页，避免页面被全部关闭即可
     */
    public synchronized void startHomeIfNecessary() {
        for (UMengActivity activity : activityList) {
            if (activity instanceof HomeActivity) {
                return;
            }
        }

        //不存在HomeActivity实例，需要打开
        startHome();
    }

    private void startHome() {
        Intent intent = new Intent(getOurInstance(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(HomeActivity.KEY_ISLOGIN, IVerifyHolder.mLoginInfo.isLogin());
        intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(IVerifyHolder.mLoginInfo));
        startActivity(intent);
    }

    @Override
    public void onTerminate() {
        //never called on device,only simulator works
//        EventBus.getDefault().unregister(this);
        super.onTerminate();
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    public AppPreference getAppPreference() {
        return AppPreference.getInstance(this);
    }

    public static MainApplication getOurInstance() {
        return ourInstance;
    }


    public static void addActivity(UMengActivity activity) {
        activityList.add(activity);
        if (isMainStackActivity(activity)) {
            mainActivityStack.add(activity);
        }
    }

    public static void removeActivity(UMengActivity activity) {
        activityList.remove(activity);
        if (isMainStackActivity(activity)) {
            mainActivityStack.remove(activity);
        }
    }

    public String getMainStackTopActivityPath() {
        if (mainActivityStack.isEmpty()) {
            return HomeActivity.class.getName();
        } else {
            return mainActivityStack.lastElement().getClass().getName();
        }
    }

    private class CategoryModelCallback implements ApiModelCallback<ArrayList<CategoryResBean>> {
        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<CategoryResBean>> beanContainer) {
            ArrayList<CategoryResBean> temp = beanContainer.getData();
            if (temp != null && temp.size() > 0) {
                String s = JSON.toJSONString(temp);
                final IndustryCategoryDBModel model = new IndustryCategoryDBModel();
                model.setCreateTime(TimeUtil.getCurrentTimeInLong());
                model.setData(s);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LiteOrm liteOrm =
                                LiteOrm.newSingleInstance(getOurInstance(), DBConfig.BasicDb
                                        .DB_NAME);
                        //删除老数据
                        liteOrm.delete(new WhereBuilder(IndustryCategoryDBModel.class)
                                .lessThan("createTime", TimeUtil.getCurrentTimeInLong()));
                        //插入新数据
                        liteOrm.save(model);
                    }
                }).start();
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            //empty
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            //empty
        }
    }

    private static boolean isMainStackActivity(Activity activity) {
        if (activity instanceof BannerInfoActivity)
            return false;
        if (activity instanceof MessageInfoActivity)
            return false;
        return true;
    }

    public CaptureUriCompat newCaptureIntentUri() {
        Uri destFileUri;
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String authority = getPackageName() + ".provider";
            File cameraFile = FileUtils.createCameraFile(this, 1);
            destFileUri = FileProvider.getUriForFile(ourInstance, authority, cameraFile);//通过FileProvider创建一个content类型的Uri
            path = cameraFile.getAbsolutePath();
            DLog.i(Lmsg.class, "newCaptureIntentUri:" + destFileUri + "\r\n" + cameraFile.getAbsolutePath());
        } else {
            File dest = new File(BaseActivity.getLocalImageCachePath(), "temp.jpg");
            destFileUri = Uri.fromFile(dest);
            path = dest.getAbsolutePath();
        }
        return new CaptureUriCompat(destFileUri,path);
    }
}
