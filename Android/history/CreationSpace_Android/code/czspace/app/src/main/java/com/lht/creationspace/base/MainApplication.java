package com.lht.creationspace.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.BuildConfig;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.innerweb.MessageInfoActivity;
import com.lht.creationspace.base.domain.interactors.AbsDbInteractor;
import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.module.cache.CacheController;
import com.lht.creationspace.module.cache.ICacheController;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.proj.interactors.ProjTypeInteractorFactory;
import com.lht.creationspace.module.proj.model.ProjTypeDbModel;
import com.lht.creationspace.module.proj.model.ProjectTypeModel;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.module.proj.repository.ProjTypeRepository;
import com.lht.creationspace.module.proj.repository.impl.ProjTypeRepositoryImpl;
import com.lht.creationspace.social.oauth.QQConstants;
import com.lht.creationspace.social.oauth.WeChatConstants;
import com.lht.creationspace.structure.SingletonStack;
import com.lht.creationspace.util.AppPreference;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.time.TimeUtil;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

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

    private CacheController cacheController;

    public static Tencent getTencent() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQConstants.APP_ID, ourInstance); //你的id
        }
        return mTencent;
    }

    private static final SingletonStack<BaseActivity> mainActivityStack
            = new SingletonStack<>();

    public static final ArrayList<BaseActivity> activityList = new ArrayList<>();

    public static IWXAPI getWechat() {
        return mWechat;
    }

    /**
     * 获取系统相册的位置
     */
    public synchronized File getSystemImageDir() {
        return cacheController.getSystemImageDir();
    }

    public synchronized File getLocalDownloadCacheDir() {
        return cacheController.getLocalDownloadCacheDir();
    }

    public synchronized File getSystemDownloadDir() {
        return cacheController.getSystemDownloadDir();
    }

    public File getLocalThumbnailCacheDir() {
        return cacheController.getLocalThumbnailCacheDir();
    }

    public File getLocalPreviewCacheDir() {
        return cacheController.getLocalThumbnailCacheDir();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DLog.i(getClass(), "Debug-Mode:" + BuildConfig.DEBUG);
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

        initUserInfo();
        cacheController = new CacheController(getLocalStorageRoot());
        cacheController.registerCacheChangedListener(
                new ICacheController.OnCacheChangedListener() {
            @Override
            public void onCacheChanged(ICacheController cacheController) {
            }
        });
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


        BridgeWebView.setDebugMode(BuildConfig.DEBUG);
        BridgeWebView.setDialogTheme(android.R.style.Theme_Material_Light_Dialog_Alert);
        JSON.setArrayStrictMode(false);

        initWechat();

        getAllProjectType();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void initWechat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mWechat == null) {
                    mWechat = WXAPIFactory.createWXAPI(MainApplication.getOurInstance(), WeChatConstants.APP_ID, false);
                }
            }
        }).start();
    }

    private void activeLifecycleMonitor() {
        getOurInstance().registerActivityLifecycleCallbacks(GlobalLifecycleMonitor.getInstance());
    }


    public void initUserInfo() {
        SharedPreferences sp = getTokenSp();
        IVerifyHolder.mLoginInfo.setUsername(sp.getString(SPConstants.Token.KEY_USERNAME, ""));
        IVerifyHolder.mLoginInfo.setAccessToken(sp.getString(SPConstants.Token.KEY_ACCESS_TOKEN,
                ""));
    }

    public SharedPreferences getTokenSp() {
        return getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        cacheController.notifyUserChanged(event.getLoginInfo().getUsername());
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LogoutEvent event) {
        IVerifyHolder.mLoginInfo.copy(new LoginInfo());
        EventBus.getDefault().removeStickyEvent(AppEvent.LoginSuccessEvent.class);
        cacheController.notifyUserChanged(null);
        finishAll();
    }

    public void bindDevice() {
        // TODO: 2017/4/6 极光集成后使用
        DLog.i(getClass(),"处理完极光之后再使用");
//        String username = IVerifyHolder.mLoginInfo.getUsername();
//        String registrationId = JPushInterface.getRegistrationID(getOurInstance());
//        DeviceBindModel model = new DeviceBindModel(username, registrationId);
//        model.doRequest(getOurInstance());
    }

    public synchronized void finishAll() {
        DLog.d(getClass(), "finishAll");
        for (BaseActivity activity : activityList) {
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
        for (BaseActivity activity : activityList) {
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
//        intent.putExtra(HomeActivity.KEY_ISLOGIN, IVerifyHolder.mLoginInfo.isLogin());
//        intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(IVerifyHolder.mLoginInfo));
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


    public static void addActivity(BaseActivity activity) {
        activityList.add(activity);
        if (isMainStackActivity(activity)) {
            mainActivityStack.add(activity);
        }
    }

    public static void removeActivity(BaseActivity activity) {
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

    private Activity currentTopActivity;

    public void updateCurrentTopActivity(Activity activity) {
        this.currentTopActivity = activity;
    }

    public Activity getCurrentTopActivity() {
        return currentTopActivity;
    }


    public void getAllProjectType() {
        ProjectTypeModel model = new ProjectTypeModel(new ProjectTypeCallback());
        model.doRequest(getOurInstance());
    }

    public void reloadCache() {
        cacheController.reloadCache();
    }

    private class ProjectTypeCallback implements RestfulApiModelCallback<ArrayList<ProjectTypeResBean>> {
        @Override
        public void onSuccess(ArrayList<ProjectTypeResBean> datas) {
            //存DB
            if (datas != null && datas.size() > 0) {
                save2DB(datas);
            }
        }

        @Override
        public void onFailure(int restCode, String msg) {
        }

        @Override
        public void onHttpFailure(int httpStatus) {
        }
    }

    public void save2DB(ArrayList<ProjectTypeResBean> datas) {
        String data = JSON.toJSONString(datas);
        final ProjTypeDbModel model = new ProjTypeDbModel();
        model.setCreateTime(TimeUtil.getCurrentTimeInLong());
        model.setData(data);

        ProjTypeRepository repository = new ProjTypeRepositoryImpl();
        final ProjTypeInteractorFactory interactorFactory
                = ProjTypeInteractorFactory.getInstance(repository);
        AbsDbInteractor<Void> interactor =
                interactorFactory.newDeleteAllInteractor(new BaseRepository.SimpleOnTaskFinishListener() {
                    @Override
                    public void onSuccess() {
                        interactorFactory.newSaveOrUpdateInteractor(model).execute();
                    }

                    @Override
                    public void onCanceledBeforeRun() {

                    }
                });
        interactor.execute();

//        final ProjTypeDbModel dbModel = new ProjTypeDbModel();
//        dbModel.deleteAll(new AbsDbModel.SimpleOnTaskFinishListener() {
//            @Override
//            public void onSuccess() {
//                new ProjTypeDbModel().saveOrUpdate(model);
//            }
//        });
    }

    private static boolean isMainStackActivity(Activity activity) {
//        if (activity instanceof BannerInfoActivity)
//            return false;
        if (activity instanceof MessageInfoActivity)
            return false;
        return true;
    }

    private String getLocalStorageRoot() {
        return getCacheDir().getPath();
    }
}
