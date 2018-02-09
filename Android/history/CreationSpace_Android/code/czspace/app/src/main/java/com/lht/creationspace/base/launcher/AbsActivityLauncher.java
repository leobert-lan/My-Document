package com.lht.creationspace.base.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.AnimRes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AbsActivityLauncher </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/5/8.
 */

public abstract class AbsActivityLauncher<D> {

    public static final class LhtActivityLauncherIntent extends Intent {
        public LhtActivityLauncherIntent() {
        }

        public LhtActivityLauncherIntent(Intent o) {
            super(o);
        }

        public LhtActivityLauncherIntent(String action) {
            super(action);
        }

        public LhtActivityLauncherIntent(String action, Uri uri) {
            super(action, uri);
        }

        public LhtActivityLauncherIntent(Context packageContext, Class<?> cls) {
            super(packageContext, cls);
        }

        public LhtActivityLauncherIntent(String action, Uri uri, Context packageContext, Class<?> cls) {
            super(action, uri, packageContext, cls);
        }
    }

    protected LhtActivityLauncherIntent launchIntent;

    private Context context;

    private boolean shouldOverridePeddingTransition = false;

    private int enterPendingTransitionAnim = 0;

    private int exitPendingTransitionAnim = 0;

    public AbsActivityLauncher(Context context) {
        this.context = context;
        launchIntent = newBaseIntent(context);
    }

    protected abstract LhtActivityLauncherIntent newBaseIntent(Context context);

    public abstract AbsActivityLauncher<D> injectData(D data);

    /**
     * @param enter enter anim res
     * @param exit  exit anim res
     * @return
     */
    public final AbsActivityLauncher<D> startAnim(@AnimRes int enter, @AnimRes int exit) {
        if (enter <= 0 && exit <= 0)
            return this;
        shouldOverridePeddingTransition = true;
        enterPendingTransitionAnim = enter;
        exitPendingTransitionAnim = exit;
        return this;
    }

    private void useAnimIfNecessary() {
        if (context instanceof Activity && shouldOverridePeddingTransition)
            ((Activity) context).overridePendingTransition(enterPendingTransitionAnim,
                    exitPendingTransitionAnim);
    }

    public final void launch() {
        context.startActivity(launchIntent);
        useAnimIfNecessary();
    }

    public final void launchForResult(int reqCode) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(launchIntent, reqCode);
            useAnimIfNecessary();
        } else {
            DLog.w(getClass(), "context is not activity,will launch as launch()");
            launch();
        }
    }

    protected final void doInject(D d) {
        launchIntent.putExtra(d.getClass().getSimpleName(), JSON.toJSONString(d));
    }

    public static <M> M parseData(Intent intent, Class<M> dataClazz) {
        return parseData(intent, dataClazz.getSimpleName(), dataClazz);
    }

    public static <M> M parseData(Intent intent, String key, Class<M> dataClazz) {
        if (null == intent) {
            DLog.e(AbsActivityLauncher.class, "intent is null");
            try {
                return dataClazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (StringUtil.isEmpty(key)) {
            DLog.e(AbsActivityLauncher.class, "key is empty or null");
            try {
                return dataClazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                return null;
            }
        }

        String _data = intent.getStringExtra(key);
        M data = null;
        try {
            data = JSON.parseObject(_data, dataClazz);
        } catch (JSONException e) {
            DLog.e(AbsActivityLauncher.class, "json parse error");
            e.printStackTrace();
        }
        return data;
    }
}
