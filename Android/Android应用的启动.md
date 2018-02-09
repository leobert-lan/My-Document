# Android应用的启动

在Android系统中，应用程序由Launcher启动*（系统应用暂不讨论）*，**而Launcher本身也是一个应用程序**。

回顾一段代码：

```
<intent-filter>
	<action android:name="android.intent.action.MAIN" />
	<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
```
其中有两条，一个是标记application启动后打开的首页，一个是标记需要在Launcher应用中加一个启动shortcut

## 图

![在Launch发送startActivity请求](http://images.cnitblog.com/i/654262/201407/311952378217720.jpg)
在Launch发送startActivity请求

--

![AMS接收客户端startActivity请求](http://images.cnitblog.com/i/654262/201407/312111010087235.jpg)
AMS接收客户端startActivity请求

## 源码

拆分着看一下Launcher的源码：
### 类声明

```
public final class Launcher extends Activity
        implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
                   View.OnTouchListener 
```

Launcher是一个应用程序，而且有界面。


### OnClick回调部分：
```
/**
 * Launches the intent referred by the clicked shortcut.
 *
 * @param v The view representing the clicked shortcut.
 */
public void onClick(View v) {
    // Make sure that rogue clicks don't get through while allapps is launching, or after the
    // view has detached (it's possible for this to happen if the view is removed mid touch).
    if (v.getWindowToken() == null) {
        return;
    }

    if (!mWorkspace.isFinishedSwitchingState()) {
        return;
    }

    Object tag = v.getTag();
    if (tag instanceof ShortcutInfo) {
        // Open shortcut
        final Intent intent = ((ShortcutInfo) tag).intent;
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        intent.setSourceBounds(new Rect(pos[0], pos[1],
                pos[0] + v.getWidth(), pos[1] + v.getHeight()));

        boolean success = startActivitySafely(v, intent, tag);

        if (success && v instanceof BubbleTextView) {
            mWaitingForResume = (BubbleTextView) v;
            mWaitingForResume.setStayPressed(true);
        }
    } else if (tag instanceof FolderInfo) {
        if (v instanceof FolderIcon) {
            FolderIcon fi = (FolderIcon) v;
            handleFolderClick(fi);
        }
    } else if (v == mAllAppsButton) {
        if (isAllAppsVisible()) {
            showWorkspace(true);
        } else {
            onClickAllAppsButton(v);
        }
    }
}
```

如果：

* 是应用快捷方式，直接调用startActivitySafely方法，。
* 是Folder(应用文件夹)调用handleFolderClick方法
* 是全部应用按钮，调用onClickAllAppsButton(v)方法显示所有应用图标。


### startActivitySafely

```
boolean startActivitySafely(View v, Intent intent, Object tag) {
    boolean success = false;
    try {
        success = startActivity(v, intent, tag);
    } catch (ActivityNotFoundException e) {
        Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
    }
    return success;
}
```
Activity#startActivity:

```
 /**
 * Same as {@link #startActivity(Intent, Bundle)} with no options
 * specified.
 *
 * @param intent The intent to start.
 *
 * @throws android.content.ActivityNotFoundException
 *
 * @see {@link #startActivity(Intent, Bundle)}
 * @see #startActivityForResult
 */
@Override
public void startActivity(Intent intent) {
    this.startActivity(intent, null);
}


@Override
public void startActivity(Intent intent, @Nullable Bundle options) {
    if (options != null) {
        startActivityForResult(intent, -1, options);
    } else {
        // Note we want to go through this call for compatibility with
        // applications that may have overridden the method.
        startActivityForResult(intent, -1);
    }
}


public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
    if (mParent == null) {
        Instrumentation.ActivityResult ar =
            mInstrumentation.execStartActivity(
                this, mMainThread.getApplicationThread(), mToken, this,
                intent, requestCode, options);
        if (ar != null) {
            mMainThread.sendActivityResult(
                mToken, mEmbeddedID, requestCode, ar.getResultCode(),
                ar.getResultData());
        }
        if (requestCode >= 0) {
            // If this start is requesting a result, we can avoid making
            // the activity visible until the result is received.  Setting
            // this code during onCreate(Bundle savedInstanceState) or onResume() will keep the
            // activity hidden during this time, to avoid flickering.
            // This can only be done when a result is requested because
            // that guarantees we will get information back when the
            // activity is finished, no matter what happens to it.
            mStartedActivity = true;
        }

        final View decor = mWindow != null ? mWindow.peekDecorView() : null;
        if (decor != null) {
            decor.cancelPendingInputEvents();
        }
        // TODO Consider clearing/flushing other event sources and events for child windows.
    } else {
        if (options != null) {
            mParent.startActivityFromChild(this, intent, requestCode, options);
        } else {
            // Note we want to go through this method for compatibility with
            // existing applications that may have overridden it.
            mParent.startActivityFromChild(this, intent, requestCode);
        }
    }
    if (options != null && !isTopOfTask()) {
        mActivityTransitionState.startExitOutTransition(this, options);
    }
}
```
追溯到：`mParent!=null`调用的startActivityFromChild：

```
public void startActivityFromChild(@NonNull Activity child, Intent intent,
        int requestCode, @Nullable Bundle options) {
    Instrumentation.ActivityResult ar =
        mInstrumentation.execStartActivity(
            this, mMainThread.getApplicationThread(), mToken, child,
            intent, requestCode, options);
    if (ar != null) {
        mMainThread.sendActivityResult(
            mToken, child.mEmbeddedID, requestCode,
            ar.getResultCode(), ar.getResultData());
    }
}
```

也是调用了{Instrumentation#execStartActivity}

```
 public ActivityResult execStartActivity(
        Context who, IBinder contextThread, IBinder token, Activity target,
        Intent intent, int requestCode, Bundle options) {
    IApplicationThread whoThread = (IApplicationThread) contextThread;
    if (mActivityMonitors != null) {
        synchronized (mSync) {
            final int N = mActivityMonitors.size();
            for (int i=0; i<N; i++) {
                final ActivityMonitor am = mActivityMonitors.get(i);
                if (am.match(who, null, intent)) {
                    am.mHits++;
                    if (am.isBlocking()) {
                        return requestCode >= 0 ? am.getResult() : null;
                    }
                    break;
                }
            }
        }
    }
    try {
        intent.migrateExtraStreamToClipData();
        intent.prepareToLeaveProcess();
        int result = ActivityManagerNative.getDefault()
            .startActivity(whoThread, who.getBasePackageName(), intent,
                    intent.resolveTypeIfNeeded(who.getContentResolver()),
                    token, target != null ? target.mEmbeddedID : null,
                    requestCode, 0, null, options);
        checkStartActivityResult(result, intent);
    } catch (RemoteException e) {
    }
    return null;
}
```

最终又调用ActivityManagerNative.getDefault().startActivity方法，

ActivityManagerNative.getDefault()返回的是ActivityManagerService的代理类。

先看一下checkStartActivityResult，也就是对ActivityManagerService.startActivity返回结果的处理：

```
public static void checkStartActivityResult(int res, Object intent) {
    if (res >= ActivityManager.START_SUCCESS) {
        return;
    }

    switch (res) {
        case ActivityManager.START_INTENT_NOT_RESOLVED:
        case ActivityManager.START_CLASS_NOT_FOUND:
            if (intent instanceof Intent && ((Intent)intent).getComponent() != null)
                throw new ActivityNotFoundException(
                        "Unable to find explicit activity class "
                        + ((Intent)intent).getComponent().toShortString()
                        + "; have you declared this activity in your AndroidManifest.xml?");
            throw new ActivityNotFoundException(
                    "No Activity found to handle " + intent);
        case ActivityManager.START_PERMISSION_DENIED:
            throw new SecurityException("Not allowed to start activity "
                    + intent);
        case ActivityManager.START_FORWARD_AND_REQUEST_CONFLICT:
            throw new AndroidRuntimeException(
                    "FORWARD_RESULT_FLAG used while also requesting a result");
        case ActivityManager.START_NOT_ACTIVITY:
            throw new IllegalArgumentException(
                    "PendingIntent is not an activity");
        case ActivityManager.START_NOT_VOICE_COMPATIBLE:
            throw new SecurityException(
                    "Starting under voice control not allowed for: " + intent);
        default:
            throw new AndroidRuntimeException("Unknown error code "
                    + res + " when starting " + intent);
    }
}
```

抛出了一些比较low的错误，嗯，比较low。

```
@Override
public final int startActivity(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions) {
    return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, bOptions,
            UserHandle.getCallingUserId());
}

@Override
public final int startActivityAsUser(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
    enforceNotIsolatedCaller("startActivity");
    userId = mUserController.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(),
            userId, false, ALLOW_FULL_ONLY, "startActivity", null);
    // TODO: Switch to user app stacks here.
    return mActivityStarter.startActivityMayWait(caller, -1, callingPackage, intent,
            resolvedType, null, null, resultTo, resultWho, requestCode, startFlags,
            profilerInfo, null, null, bOptions, false, userId, null, null);
}
```

又调用了ActivityStarter的startActivityMayWait，他们最终和ActivityStackSupervisor类来来回回的调用，最终真正启动Activity的方法在ActivityStackSupervisor类中的realStartActivityLocked方法中，忽略其他代码，直接看启动的

```
//真正启动
final boolean realStartActivityLocked(ActivityRecord r, ProcessRecord app,
        boolean andResume, boolean checkConfig) throws RemoteException {
    。。。。

    //调用ActivityThread的的内部类ApplicationThread类
        app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
                System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
                new Configuration(task.mOverrideConfig), r.compat, r.launchedFromPackage,
                task.voiceInteractor, app.repProcState, r.icicle, r.persistentState, results,
                newIntents, !andResume, mService.isNextTransitionForward(), profilerInfo);

}
```

这里的会调用ActivityThread的的内部类ApplicationThread类中的scheduleLaunchActivity方法，ApplicationThread是IApplicationThread和子类，是一个Binder对象，ApplicationThread是我们应用和系统ActivityManagerService沟通的桥梁，我们在去看ActivityThread中的scheduleLaunchActivity方法

```
        public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident,
            ActivityInfo info, Configuration curConfig, CompatibilityInfo compatInfo,
            String referrer, IVoiceInteractor voiceInteractor, int procState, Bundle state,
            PersistableBundle persistentState, List<ResultInfo> pendingResults,
            List<ReferrerIntent> pendingNewIntents, boolean notResumed, boolean isForward,
            ProfilerInfo profilerInfo) {

        updateProcessState(procState, false);

        ActivityClientRecord r = new ActivityClientRecord();

        r.token = token;
        r.ident = ident;
        r.intent = intent;
        r.referrer = referrer;
        r.voiceInteractor = voiceInteractor;
        r.activityInfo = info;
        r.compatInfo = compatInfo;
        r.state = state;
        r.persistentState = persistentState;

        r.pendingResults = pendingResults;
        r.pendingIntents = pendingNewIntents;

        r.startsNotResumed = notResumed;
        r.isForward = isForward;

        r.profilerInfo = profilerInfo;

        updatePendingConfiguration(curConfig);

        sendMessage(H.LAUNCH_ACTIVITY, r);
    }
```
非常简单，最终就发送了一个消息到ActivityThread中的内部类H中，H继承于Handler，我们去看看

```
 public void handleMessage(Message msg) {
        if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
        switch (msg.what) {
            case LAUNCH_ACTIVITY: {
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
                final ActivityClientRecord r = (ActivityClientRecord) msg.obj;

                r.packageInfo = getPackageInfoNoCheck(
                        r.activityInfo.applicationInfo, r.compatInfo);
                handleLaunchActivity(r, null);
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
            } break;
        。。。

        }
```
    
又调用了handleLaunchActivity方法

```
private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    // If we are getting ready to gc after going to the background, well
    // we are back active so skip it.
    unscheduleGcIdler();
    mSomeActivitiesChanged = true;

    if (r.profilerInfo != null) {
        mProfiler.setProfiler(r.profilerInfo);
        mProfiler.startProfiling();
    }

    // Make sure we are running with the most recent config.
    handleConfigurationChanged(null, null);

    if (localLOGV) Slog.v(
        TAG, "Handling launch of " + r);

    // Initialize before creating the activity
    WindowManagerGlobal.initialize();

    Activity a = performLaunchActivity(r, customIntent);

    if (a != null) {
        r.createdConfig = new Configuration(mConfiguration);
        Bundle oldState = r.state;
        handleResumeActivity(r.token, false, r.isForward,
                !r.activity.mFinished && !r.startsNotResumed);

        if (!r.activity.mFinished && r.startsNotResumed) {
            // The activity manager actually wants this one to start out
            // paused, because it needs to be visible but isn't in the
            // foreground.  We accomplish this by going through the
            // normal startup (because activities expect to go through
            // onResume() the first time they run, before their window
            // is displayed), and then pausing it.  However, in this case
            // we do -not- need to do the full pause cycle (of freezing
            // and such) because the activity manager assumes it can just
            // retain the current state it has.
            try {
                r.activity.mCalled = false;
                mInstrumentation.callActivityOnPause(r.activity);
                // We need to keep around the original state, in case
                // we need to be created again.  But we only do this
                // for pre-Honeycomb apps, which always save their state
                // when pausing, so we can not have them save their state
                // when restarting from a paused state.  For HC and later,
                // we want to (and can) let the state be saved as the normal
                // part of stopping the activity.
                if (r.isPreHoneycomb()) {
                    r.state = oldState;
                }
                if (!r.activity.mCalled) {
                    throw new SuperNotCalledException(
                        "Activity " + r.intent.getComponent().toShortString() +
                        " did not call through to super.onPause()");
                }

            } catch (SuperNotCalledException e) {
                throw e;

            } catch (Exception e) {
                if (!mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException(
                            "Unable to pause activity "
                            + r.intent.getComponent().toShortString()
                            + ": " + e.toString(), e);
                }
            }
            r.paused = true;
        }
    } else {
        // If there was an error, for any reason, tell the activity
        // manager to stop us.
        try {
            ActivityManagerNative.getDefault()
                .finishActivity(r.token, Activity.RESULT_CANCELED, null, false);
        } catch (RemoteException ex) {
            // Ignore
        }
    }
}
```

上面的方法中通过performLaunchActivity方法创建了Activity，然后并回调Activity响应的生命周期方法，我们去看performLaunchActivity

```
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    // System.out.println("##### [" + System.currentTimeMillis() + "] ActivityThread.performLaunchActivity(" + r + ")");

    ActivityInfo aInfo = r.activityInfo;
    if (r.packageInfo == null) {
        r.packageInfo = getPackageInfo(aInfo.applicationInfo, r.compatInfo,
                Context.CONTEXT_INCLUDE_CODE);
    }

    ComponentName component = r.intent.getComponent();
    if (component == null) {
        component = r.intent.resolveActivity(
            mInitialApplication.getPackageManager());
        r.intent.setComponent(component);
    }

    if (r.activityInfo.targetActivity != null) {
        component = new ComponentName(r.activityInfo.packageName,
                r.activityInfo.targetActivity);
    }

    Activity activity = null;
    try {
        java.lang.ClassLoader cl = r.packageInfo.getClassLoader();
        activity = mInstrumentation.newActivity(
                cl, component.getClassName(), r.intent);
        StrictMode.incrementExpectedActivityCount(activity.getClass());
        r.intent.setExtrasClassLoader(cl);
        r.intent.prepareToEnterProcess();
        if (r.state != null) {
            r.state.setClassLoader(cl);
        }
    } catch (Exception e) {
        if (!mInstrumentation.onException(activity, e)) {
            throw new RuntimeException(
                "Unable to instantiate activity " + component
                + ": " + e.toString(), e);
        }
    }

    try {
        Application app = r.packageInfo.makeApplication(false, mInstrumentation);

        if (localLOGV) Slog.v(TAG, "Performing launch of " + r);
        if (localLOGV) Slog.v(
                TAG, r + ": app=" + app
                + ", appName=" + app.getPackageName()
                + ", pkg=" + r.packageInfo.getPackageName()
                + ", comp=" + r.intent.getComponent().toShortString()
                + ", dir=" + r.packageInfo.getAppDir());

        if (activity != null) {
            Context appContext = createBaseContextForActivity(r, activity);
            CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
            Configuration config = new Configuration(mCompatConfiguration);
            if (DEBUG_CONFIGURATION) Slog.v(TAG, "Launching activity "
                    + r.activityInfo.name + " with config " + config);
            activity.attach(appContext, this, getInstrumentation(), r.token,
                    r.ident, app, r.intent, r.activityInfo, title, r.parent,
                    r.embeddedID, r.lastNonConfigurationInstances, config,
                    r.referrer, r.voiceInteractor);

            if (customIntent != null) {
                activity.mIntent = customIntent;
            }
            r.lastNonConfigurationInstances = null;
            activity.mStartedActivity = false;
            int theme = r.activityInfo.getThemeResource();
            if (theme != 0) {
                activity.setTheme(theme);
            }

            activity.mCalled = false;
            if (r.isPersistable()) {
                mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
            } else {
                mInstrumentation.callActivityOnCreate(activity, r.state);
            }
            if (!activity.mCalled) {
                throw new SuperNotCalledException(
                    "Activity " + r.intent.getComponent().toShortString() +
                    " did not call through to super.onCreate()");
            }
            r.activity = activity;
            r.stopped = true;
            if (!r.activity.mFinished) {
                activity.performStart();
                r.stopped = false;
            }
            if (!r.activity.mFinished) {
                if (r.isPersistable()) {
                    if (r.state != null || r.persistentState != null) {
                        mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state,
                                r.persistentState);
                    }
                } else if (r.state != null) {
                    mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state);
                }
            }
            if (!r.activity.mFinished) {
                activity.mCalled = false;
                if (r.isPersistable()) {
                    mInstrumentation.callActivityOnPostCreate(activity, r.state,
                            r.persistentState);
                } else {
                    mInstrumentation.callActivityOnPostCreate(activity, r.state);
                }
                if (!activity.mCalled) {
                    throw new SuperNotCalledException(
                        "Activity " + r.intent.getComponent().toShortString() +
                        " did not call through to super.onPostCreate()");
                }
            }
        }
        r.paused = true;

        mActivities.put(r.token, r);

    } catch (SuperNotCalledException e) {
        throw e;

    } catch (Exception e) {
        if (!mInstrumentation.onException(activity, e)) {
            throw new RuntimeException(
                "Unable to start activity " + component
                + ": " + e.toString(), e);
        }
    }

    return activity;
}
```

1.上面方法的逻辑是通过Instrumentation.newActivity方法创建了Activity，

```
 public Activity newActivity(ClassLoader cl, String className,
        Intent intent)
        throws InstantiationException, IllegalAccessException,
        ClassNotFoundException {
    return (Activity)cl.loadClass(className).newInstance();
}
```

2.通过r.packageInfo.makeApplication方法判断Application是否已经创建，如果已经创建直接返回，如果没有创建则创建Application并回调onCreate方法，

```
public Application makeApplication(boolean forceDefaultAppClass,
        Instrumentation instrumentation) {
    if (mApplication != null) {
        return mApplication;
    }
    ...

    try {
        java.lang.ClassLoader cl = getClassLoader();
        if (!mPackageName.equals("android")) {
            initializeJavaContextClassLoader();
        }
        ContextImpl appContext = ContextImpl.createAppContext(mActivityThread, this);
        app = mActivityThread.mInstrumentation.newApplication(
                cl, appClass, appContext);
        appContext.setOuterContext(app);
    } catch (Exception e) {
        if (!mActivityThread.mInstrumentation.onException(app, e)) {
            throw new RuntimeException(
                "Unable to instantiate application " + appClass
                + ": " + e.toString(), e);
        }
    }
    mActivityThread.mAllApplications.add(app);
    mApplication = app;

    if (instrumentation != null) {
        try {
            instrumentation.callApplicationOnCreate(app);
        } catch (Exception e) {
            if (!instrumentation.onException(app, e)) {
                throw new RuntimeException(
                    "Unable to create application " + app.getClass().getName()
                    + ": " + e.toString(), e);
            }
        }
    }

  ...
    }

    return app;
}
```

3.创建Context对象，并回调Activity的attach，onCreate方法

```
if (activity != null) {
    Context appContext = createBaseContextForActivity(r, activity);
    CharSequence title = r.activityInfo.loadLabel(appContext.getPackageManager());
    Configuration config = new Configuration(mCompatConfiguration);
    if (DEBUG_CONFIGURATION) Slog.v(TAG, "Launching activity "
            + r.activityInfo.name + " with config " + config);
    activity.attach(appContext, this, getInstrumentation(), r.token,
            r.ident, app, r.intent, r.activityInfo, title, r.parent,
            r.embeddedID, r.lastNonConfigurationInstances, config,
            r.referrer, r.voiceInteractor);
    ...

    activity.mCalled = false;
    if (r.isPersistable()) {
        mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
    } else {
        mInstrumentation.callActivityOnCreate(activity, r.state);
    }
   ...
}
```

好了，到这里我们的应用就已经被启动起来了，我们总结一下，

1.首先我们要启动的Activity会去ActivityManagerService中去校检是否合法

2.通过回调ActivityThread中内部类ApplicationThread的scheduleLaunchActivity去发送一个消息到ActivityThread中的内部类H中，H继承于Handler

3.然后会通过反射创建Activity对象及Application对象，并回调响应生命周期方法

4.这里只是想告诉大家Activity是怎么运作的原理，至于其中省略了很多的代码，比如打开这个Activity的时候需要将当前Activity回调onPause等生命周期的方法，再比如，如果是通过Launcher来新打开一个应用程序的时候首先会创建一个新的进程等。

这里说一点ActivityManagerService和我们应用间沟通几乎都是ActivityThread，ApplicationThread，H这几个类之间来回调用，这里我们只说了onCreate的回调，其实其他生的生命周期也一样都是通过这种模式来的，而且不只是Activity，我们Android的四大组件几乎都用了这种模式，具体源码我们可以自己去看一看吧 
给出几个典型的

```

 case STOP_ACTIVITY_SHOW:
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStop");
                handleStopActivity((IBinder)msg.obj, true, msg.arg2);
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                break;

 case CREATE_SERVICE:
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceCreate");
                handleCreateService((CreateServiceData)msg.obj);
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                break;

  case BIND_SERVICE:
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceBind");
                handleBindService((BindServiceData)msg.obj);
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                break;

  case INSTALL_PROVIDER:
                handleInstallProvider((ProviderInfo) msg.obj);
                break;

  case RECEIVER:
                Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "broadcastReceiveComp");
                handleReceiver((ReceiverData)msg.obj);
                maybeSnapshot();
                Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                break;
```    

