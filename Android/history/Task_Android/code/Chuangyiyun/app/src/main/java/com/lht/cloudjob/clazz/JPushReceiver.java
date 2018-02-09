package com.lht.cloudjob.clazz;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.BannerInfoActivity;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.activity.innerweb.MessageInfoActivity;
import com.lht.cloudjob.clazz.customerror.PushAccessError;
import com.lht.cloudjob.clazz.customerror.PushContentError;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.mvp.model.bean.JpushMessage;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.notification.NotificationUtil;
import com.lht.cloudjob.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    private static boolean shouldRestrict = false; // 必须静态，收到广播可能（很可能）系统维护了一个新的广播接收器实例

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        DLog.d(getClass(), "[MyReceiver] onReceive - " + intent.getAction() + ", extras:\r\n " +
                getSerializedBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            DLog.d(getClass(), "[MyReceiver] 接收Registration Id : " + regId);
            //第一次注册成功后本地sdk会发出registration id，
            // 但是根据创意云业务没什么用，只有用户实际登录、登出才会维护
            //send the Registration Id to your server...// ignore

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            DLog.d(getClass(), "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

            processCustomMessageReceive(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            DLog.d(getClass(), "[MyReceiver] 接收到推送下来的通知");
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            DLog.d(getClass(), "[MyReceiver] 接收到推送下来的通知的ID: " + notificationId);

            //错误入口的通知，可能是测试时发送的
            processErrorNotification(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            DLog.d(getClass(), "[MyReceiver] 用户点击打开了通知");

            //错误入口的通知的打开，直接引导至主页
            processOpenErrorNotification(context);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            DLog.d(getClass(), "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            // ignore rich type;

            PushContentError error = new PushContentError("open a rich push");
            error.report(context);

            processOpenErrorNotification(context);

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            DLog.w(getClass(), "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            //ignore
        } else if (GlobalLifecycleMonitor.BROADCAST_ACTION_APPSLEEP.equals(intent.getAction())) {
            //应用处于后台或关闭了，需要重定向
            synchronized (this) {
                DLog.d(getClass(), "调整标记，进行重定向");
                shouldRestrict = true;
            }
        } else if (GlobalLifecycleMonitor.BROADCAST_ACTION_APPACTIVE.equals(intent.getAction())) {
            //应用处于前台，不需要重定向
            synchronized (this) {
                DLog.d(getClass(), "调整标记，不进行重定向");
                shouldRestrict = false;
            }
        } else {
            DLog.d(getClass(), "[MyReceiver] Unhandled intent - " + intent.getAction());
            //ignore
        }
    }

    /**
     * @param bundle 消息体实例
     * @return 序列化的内容
     */
    private static String getSerializedBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        if (bundle == null) {
            return "bundle is null";
        }

        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if (extra == null || extra.isEmpty()) {
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:").append(key).append(", value: [")
                                .append(myKey).append(" - ")
                                .append(json.optString(myKey)).append("]");
                    }
                } catch (JSONException e) {
                    DLog.e(Lmsg.class, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
            }
        }
        return sb.toString();
    }


    /**
     * 处理角标
     */
    private void processCustomMessageReceive(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        //按照message处理
        if (message == null || message.isEmpty()) {
            // 无法处理的内容，汇报错误
            PushContentError error = new PushContentError(getSerializedBundle(bundle));
            error.report(context);
            return;
        }

        // 不接受extras  String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        // Log.i(TAG,"message:"+message);

        JpushMessage jpushMessage;

        try {
            jpushMessage = JSON.parseObject(message, JpushMessage.class);
        } catch (Exception e) {
            PushContentError error = new PushContentError(getSerializedBundle(bundle));
            error.report(context);
            return;
        }

        JpushMessage.AndroidMessage androidMessage = jpushMessage.getAndroid();
        if (androidMessage != null) {

            JpushMessage.MessageExtra messageExtra = androidMessage.getExtras();
            if (messageExtra != null) {
                int objType = messageExtra.getObj_type();

                switch (objType) {
                    case JpushMessage.MessageExtra.OBJ_TYPE_VSOACTIVITY:
                        processActivityTypeMessage(context, androidMessage);
                        break;
                    case JpushMessage.MessageExtra.OBJ_TYPE_VSONOTIFY:
                        processSysNotifyTypeMessage(context, androidMessage);
                        break;
                    default:
                        DLog.e(Lmsg.class, "jpush 收到的 message,extra业务有误:obj_type=" + objType);
                        PushContentError error = new PushContentError(getSerializedBundle(bundle));
                        error.report(context);
                        break;
                }

            } else {
                DLog.e(Lmsg.class, "jpush 收到的 message没有extra");
                PushContentError error = new PushContentError(getSerializedBundle(bundle));
                error.report(context);
            }
        } else {
            DLog.e(Lmsg.class, "jpush 收到的 message没有指定androidMessage");
            PushContentError error = new PushContentError(getSerializedBundle(bundle));
            error.report(context);
        }

    }

    /**
     * 活动类的消息
     */
    private void processActivityTypeMessage(Context context, JpushMessage.AndroidMessage message) {
        JpushMessage.MessageExtra extra = message.getExtras();
        if (extra == null) {
            PushContentError error = new PushContentError(JSON.toJSONString(message));
            error.report(context);
            return;
        }

        if (StringUtil.isEmpty(extra.getActivity_url())) {
            PushContentError error = new PushContentError(JSON.toJSONString(message));
            error.report(context);
            return;
        }

        BadgeNumberManager.getInstance().addVsoActivityNotify(extra);

        Intent openIntent = newActivityIntent(context, BannerInfoActivity.class);
        openIntent.putExtra(BannerInfoActivity.KEY_DATA, extra.getActivity_url());
        openIntent.putExtra(BannerInfoActivity.KEY_HASEXCUTEBADGE, false);//需要自己处理角标

        synchronized (this) {
            if (shouldRestrict) {
                DLog.d(getClass(), "进行重定向");
                String s = MainApplication.getOurInstance().getMainStackTopActivityPath();
                openIntent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH,s);
            } else {
                DLog.d(getClass(), "不重定向");
            }
        }

        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode,
                openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String summary = message.getAlert();

        if (StringUtil.isEmpty(summary)) {//可能写到extra中了
            summary = extra.getContent();
        }

        if (StringUtil.isEmpty(summary)) {
            summary = context.getString(R.string.v1030_notify_ticker);
        }

        makeNotify(context, pendingIntent, context.getString(R.string.app_name), summary);
    }

    private void makeNotify(Context context, PendingIntent pendingIntent, String title, String content) {
        NotificationUtil util = new NotificationUtil(context);

        util.notifyMoreLineType(pendingIntent, R.drawable.icon_small,
                context.getString(R.string.v1030_notify_ticker), title, content,
                true, true, true);
    }

    /**
     * VSO系统类的消息
     */
    private void processSysNotifyTypeMessage(Context context, JpushMessage.AndroidMessage message) {

        JpushMessage.MessageExtra extra = message.getExtras();
        if (extra == null) {
            PushContentError error = new PushContentError(JSON.toJSONString(message));
            error.report(context);
            return;
        }

        if (StringUtil.isEmpty(extra.getMsg_id())) {
            PushContentError error = new PushContentError(JSON.toJSONString(message));
            error.report(context);
            return;
        }

        BadgeNumberManager.getInstance().addSystemNotify(1);

        Intent openIntent = newActivityIntent(context, MessageInfoActivity.class);
        String url = IPublicConst.MsgInfoUrlHelpler.formatUrl(extra.getMsg_id());
        openIntent.putExtra(MessageInfoActivity.KEY_DATA, url);
        synchronized (this) {
            if (shouldRestrict) {
                DLog.d(getClass(), "重定向");
                String s = MainApplication.getOurInstance().getMainStackTopActivityPath();
                openIntent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH,s);
            } else {
                DLog.d(getClass(), "不重定向");
            }
        }

        int requestCode = (int) SystemClock.uptimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode,
                openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String summary = message.getAlert();

        if (StringUtil.isEmpty(summary)) {//可能写到extra中了
            summary = extra.getContent();
        }

        if (StringUtil.isEmpty(summary)) {
            summary = context.getString(R.string.v1030_notify_ticker);
        }
        makeNotify(context, pendingIntent, context.getString(R.string.app_name), summary);
    }


    private Intent newActivityIntent(Context context, Class<? extends Activity> activity) {
        Intent i = new Intent(context, activity);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    /**
     * 收到通知，android不接受该业务，汇报错误
     */
    private void processErrorNotification(Context context, Bundle bundle) {
        PushAccessError error = new PushAccessError(getSerializedBundle(bundle));
        error.report(context);
    }

    /**
     * 打开收到通知，android不接受该业务，打开主页
     */
    private void processOpenErrorNotification(Context context) {
        //打开主页
        Intent i = newActivityIntent(context, HomeActivity.class);
        context.startActivity(i);
    }


}
