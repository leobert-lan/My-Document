package com.lht.cloudjob.util.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;

//import android.widget.Toast;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.notification
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> NotificationUtil
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/1.
 */
@SuppressLint("NewApi")
public class NotificationUtil {

    private static final int FLAG = Notification.FLAG_INSISTENT;
    int requestCode = (int) SystemClock.uptimeMillis();
    private int notificationId = 0;
    private NotificationManager nm;
    private Notification notification;
    private NotificationCompat.Builder cBuilder;
    private Notification.Builder nBuilder;
    private Context mContext;

    private static class NotificationIdCounter {
        private static int NOTIFICATION_ID = 0;

        static synchronized int getId() {
            NOTIFICATION_ID++;
            return NOTIFICATION_ID;
        }
    }


    public NotificationUtil(Context context) {
        this.notificationId = NotificationIdCounter.getId();
        mContext = context;
        // 获取系统服务来初始化对象
        nm = (NotificationManager) mContext
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(mContext);
    }

    /**
     * 设置在顶部通知栏中的各种信息
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    private void setCompatBuilder(PendingIntent pendingIntent, int smallIcon, String ticker,
                                  String title, String content, boolean sound, boolean vibrate, boolean lights) {

        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
        cBuilder.setTicker(ticker);// 在顶部状态栏中的提示信息

        cBuilder.setContentTitle(title);// 设置通知中心的标题
        cBuilder.setContentText(content);// 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());

		/*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
		 * 不设置的话点击消息后也不清除，但可以滑动删除
		 */
        cBuilder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
		 * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
		 */
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
		 * Notification.DEFAULT_SOUND：系统默认铃声。
		 * Notification.DEFAULT_VIBRATE：系统默认震动。
		 * Notification.DEFAULT_LIGHTS：系统默认闪光。
		 * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
		 */
        int defaults = 0;

        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }

        cBuilder.setDefaults(defaults);
    }

    /**
     * 设置builder的信息，在用大文本时会用到这个
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    private void setBuilder(PendingIntent pendingIntent, int smallIcon, String ticker, boolean sound, boolean vibrate, boolean lights) {
        nBuilder = new Notification.Builder(mContext);
        nBuilder.setContentIntent(pendingIntent);

        nBuilder.setSmallIcon(smallIcon);

        nBuilder.setTicker(ticker);
        nBuilder.setWhen(System.currentTimeMillis());
        nBuilder.setPriority(Notification.PRIORITY_MAX);
        nBuilder.setAutoCancel(true);

        int defaults = 0;

        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }

        nBuilder.setDefaults(defaults);
    }

    /**
     * 普通的通知
     * <p/>
     * 1. 侧滑即消失，下拉通知菜单则在通知菜单显示
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    public void notifySingleLineType(PendingIntent pendingIntent, int smallIcon,
                                     String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        sent();
    }

    /**
     * 进行多项设置的通知(在小米上似乎不能设置大图标，系统默认大图标为应用图标)
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    public void notifyWechatType(PendingIntent pendingIntent, int smallIcon, int largeIcon, ArrayList<String> messageList,
                               String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);

        // 将Ongoing设为true 那么notification将不能滑动删除
        //cBuilder.setOngoing(true);

        /**
         // 删除时
         Intent deleteIntent = new Intent(mContext, DeleteService.class);
         int deleteCode = (int) SystemClock.uptimeMillis();
         // 删除时开启一个服务
         PendingIntent deletePendingIntent = PendingIntent.getService(mContext,
         deleteCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         cBuilder.setDeleteIntent(deletePendingIntent);

         **/

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), largeIcon);
        cBuilder.setLargeIcon(bitmap);

        cBuilder.setDefaults(Notification.DEFAULT_ALL);// 设置使用默认的声音
        //cBuilder.setVibrate(new long[]{0, 100, 200, 300});// 设置自定义的振动
        cBuilder.setAutoCancel(true);
        // builder.setSound(Uri.parse("file:///sdcard/click.mp3"));

        // 设置通知样式为收件箱样式,在通知中心中两指往外拉动，就能出线更多内容，但是很少见
        //cBuilder.setNumber(messageList.size());
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String msg : messageList) {
            inboxStyle.addLine(msg);
        }
        inboxStyle.setSummaryText("[" + messageList.size() + "条]" + title);
        cBuilder.setStyle(inboxStyle);
        sent();
    }

    /**
     * 自定义视图的通知
     *
     * @param remoteViews
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     */
    public void notify_customview(RemoteViews remoteViews, PendingIntent pendingIntent,
                                  int smallIcon, String ticker, boolean sound, boolean vibrate, boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, null, null, sound, vibrate, lights);

        notification = cBuilder.build();
        notification.contentView = remoteViews;
        // 发送该通知
        nm.notify(getNotificationId(), notification);
    }

    /**
     * 可以容纳多行提示文本的通知信息 (因为在高版本的系统中才支持，所以要进行判断)
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    public void notifyMoreLineType(PendingIntent pendingIntent, int smallIcon, String ticker,
                                   String title, String content, boolean sound, boolean vibrate,
                                   boolean lights) {

        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            notifySingleLineType(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
//            Toast.makeText(mContext, "您的手机低于Android 4.1.2，不支持多行通知显示！！", Toast.LENGTH_SHORT).show();
        } else {
            setBuilder(pendingIntent, smallIcon, ticker, true, true, false);
            nBuilder.setContentTitle(title);
            nBuilder.setContentText(content);
            nBuilder.setPriority(Notification.PRIORITY_HIGH);
            notification = new Notification.BigTextStyle(nBuilder).bigText(content).build();
            // 发送该通知
            nm.notify(getNotificationId(), notification);
        }
    }

    /**
     * 有进度条的通知，可以设置为模糊进度或者精确进度
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param content
     */
    public void notify_progress(PendingIntent pendingIntent, int smallIcon,
                                String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        /*
         * 因为进度条要实时更新通知栏也就说要不断的发送新的提示，所以这里不建议开启通知声音。
		 * 这里是作为范例，给大家讲解下原理。所以发送通知后会听到多次的通知声音。
		 */

        new Thread(new Runnable() {
            @Override
            public void run() {
                int incr;
                for (incr = 0; incr <= 100; incr += 10) {
                    // 参数：1.最大进度， 2.当前进度， 3.是否有准确的进度显示
                    cBuilder.setProgress(100, incr, false);
                    // cBuilder.setProgress(0, 0, true);
                    sent();
                    try {
                        Thread.sleep(1 * 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 进度满了后，设置提示信息
                cBuilder.setContentText("下载完成").setProgress(0, 0, false);
                sent();
            }
        }).start();
    }

    /**
     * 容纳大图片的通知
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param title
     * @param bigPic
     */
    public void notify_bigPic(PendingIntent pendingIntent, int smallIcon, String ticker,
                              String title, String content, int bigPic, boolean sound, boolean vibrate,
                              boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, null, sound, vibrate, lights);
        NotificationCompat.BigPictureStyle picStyle = new NotificationCompat.BigPictureStyle();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                bigPic, options);
        picStyle.bigPicture(bitmap);
        picStyle.bigLargeIcon(bitmap);
        cBuilder.setContentText(content);
        cBuilder.setStyle(picStyle);
        sent();
    }

    /**
     * 里面有两个按钮的通知
     */
    public void notifyWithButton(int smallIcon, int leftBtnIcon, String leftText, PendingIntent leftPendIntent,
                                 int rightBtnIcon, String rightText, PendingIntent rightPendIntent, String ticker,
                                 String title, String content, boolean sound, boolean vibrate, boolean lights) {

        requestCode = (int) SystemClock.uptimeMillis();
        setCompatBuilder(rightPendIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        cBuilder.addAction(leftBtnIcon,
                leftText, leftPendIntent);
        cBuilder.addAction(rightBtnIcon,
                rightText, rightPendIntent);
        sent();
    }

    public void notifyHeadUpType(PendingIntent pendingIntent, int smallIcon, int largeIcon,
                                 String ticker, String title, String content, int leftbtnicon,
                                 String lefttext, PendingIntent leftPendingIntent,
                                 int rightbtnicon, String righttext,
                                 PendingIntent rightPendingIntent,
                                 boolean sound, boolean vibrate, boolean lights) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        cBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), largeIcon));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cBuilder.addAction(leftbtnicon,
                    lefttext, leftPendingIntent);
            cBuilder.addAction(rightbtnicon,
                    righttext, rightPendingIntent);
            sent();
        } else {
//            Toast.makeText(mContext,mContext.getString(R.string.v1010_default_notification), Toast.LENGTH_SHORT).show();
            notifyMoreLineType(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        }
    }


    /**
     * 发送通知
     */
    private void sent() {
        notification = cBuilder.build();
        // 发送该通知
        nm.notify(notificationId, notification);
    }

    public int getNotificationId() {
        return notificationId;
    }

    /**
     * 根据id清除通知
     */
    public void clear() {
        // 取消通知
        nm.cancelAll();
    }
}
