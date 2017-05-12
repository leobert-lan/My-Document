Android 系统级别的Service有好多，可以通过
` getSystemService(@ServiceName @NonNull String name)
`
方法得到（通过@Service注解限定参数范围）。


系统Service主要有：

* WINDOW_SERVICE（android.view.WindowManager）
* LAYOUT_INFLATER_SERVICE（android.view.LayoutInflater）
* ACTIVITY_SERVICE（android.app.ActivityManager）
* POWER_SERVICE（android.os.PowerManager）
* ALARM_SERVICE（android.app.AlarmManager）
* NOTIFICATION_SERVICE（android.app.NotificationManager）
* KEYGUARD_SERVICE（android.app.KeyguardManager）
* LOCATION_SERVICE（android.location.LocationManager）
* SEARCH_SERVICE（android.app.SearchManager）
* SENSOR_SERVICE（android.hardware.SensorManager）
* STORAGE_SERVICE（android.os.storage.StorageManager）
* VIBRATOR_SERVICE（android.os.Vibrator）
* CONNECTIVITY_SERVICE（android.net.ConnectivityManager）
* WIFI_SERVICE（android.net.wifi.WifiManager）
* AUDIO_SERVICE（android.media.AudioManager）
* MEDIA_ROUTER_SERVICE（android.media.MediaRouter）
* TELEPHONY_SERVICE（android.telephony.TelephonyManager）
* TELEPHONY_SUBSCRIPTION_SERVICE（android.telephony.SubscriptionManager）
* CARRIER_CONFIG_SERVICE（android.telephony.CarrierConfigManager）
* INPUT_METHOD_SERVICE（android.view.inputmethod.InputMethodManager）
* UI_MODE_SERVICE（android.app.UiModeManager）
* DOWNLOAD_SERVICE（android.app.DownloadManager）
* BATTERY_SERVICE（android.os.BatteryManager）
* JOB_SCHEDULER_SERVICE（android.app.job.JobScheduler）
* NETWORK_STATS_SERVICE（android.app.usage.NetworkStatsManager）
* HARDWARE_PROPERTIES_SERVICE（android.os.HardwarePropertiesManager）


WindowManager

经常看直播的小伙伴们会发现现在退出直播页之后会有一个悬浮窗在直播列表上，方便用户在查看其它直播的时候仍然可以看到之前的直播内容，这个通过WindowManager就可以实现。WindowManager继承自ViewManager，ViewManager里边有添加删除更新View的方法

public void addView(View view, ViewGroup.LayoutParams params);


public void updateViewLayout(View view, ViewGroup.LayoutParams params);

public void removeView(View view);

其中LayoutParams中可以定义View的坐标，type（window类型），flag（显示特性），softInputMode，gravity，horizontalMargin，verticalMargin，format，windowAnimations（系统资源），alpha等属性。只要在调用这三个方法就可以了。

## LayoutInflater

LayoutInflater比较常用，尤其在Adapter中经常会用到

LayoutInflater.from(context).inflate(R.layout.item, parent, false)
这里注意一下后两个参数，parent一般不推荐传null，如果上边这种形式返回的是实例化出来的View，如果false改为true，返回的为第二个参数parent，这点可以从源码中得到答案。

// We are supposed to attach all the views we found (int temp)
                    // to root. Do that now.
                    if (root != null && attachToRoot) {
                        root.addView(temp, params);
                    }

                    // Decide whether to return the root that was passed in or the
                    // top view found in xml.
                    if (root == null || !attachToRoot) {
                        result = temp;
                    }
root为传进来的parent，result开始初始化为root，最后返回result。

## ActivityManager

ActivityManager的很多方法是通过ActivityManagerNative实现的，ActivityManagerNative继承了Binder并实现了IActivityManager接口。ActivityManager里常用方法：

//跟当前app有关系的任务列表
public List<ActivityManager.AppTask> getAppTasks()

//当前运行的Service列表
public List<RunningServiceInfo> getRunningServices(int maxNum) throws SecurityException

//得到内存信息
public void getMemoryInfo(MemoryInfo outInfo)

//设备配置信息
public ConfigurationInfo getDeviceConfigurationInfo()

//获取系统运行进程
public List<RunningAppProcessInfo> getRunningAppProcesses()
AlarmManager

AlarmManager 可以在未来的一个时间点调起app，到了时间点是如果app没有启动，系统会广播之前设定的Intent，自动启动app。
AlarmManager常用方法

/** @param type {ELAPSED_REALTIME}, {ELAPSED_REALTIME_WAKEUP},
     *        {RTC},  {RTC_WAKEUP}中的一个，唤醒方式.
     * @param triggerAtMillis 设定的时间点，type方式不同，表示的时间不同
     * @param operation执行的Intent.
public void set(int type, long triggerAtMillis, PendingIntent operation)

//重复Alarm,intervallMillis表示两次的间隔
public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) 

//API 19开始上边两个方法设定的时间变得不准确，系统添加了下边两个方法保证严格传递。
public void setWindow(int type, long windowStartMillis, long windowLengthMillis, PendingIntent operation) 

public void setExact(int type, long triggerAtMillis, PendingIntent operation)
设置完之后要在工程内创建接受Intent的Activity、Broadcast或者Service处理逻辑。

## NotificationManager

NotificationManager比较常见，用来发送消息通知.

notifyManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
builder = new Notification.Builder(context);
builder.setContentTitle(context.getString(R.string.offline_package_download, mission.getName()))
                    .setContentText(context.getString(R.string.start_download))
                    .setSmallIcon(android.R.drawable.stat_sys_download).setProgress(100, 0, true)
                    .setTicker(context.getString(R.string.start_download)).setAutoCancel(true);
notifyManager.notify(missionId, builder.getNotification());
LocationManager

提供设备地理位置信息的Service。

LocationManager manager = (LocationManager)getSystemService(LOCATION_SERVICE);
//需要确认权限ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION一个粗精度，一个精确精度，至少一个
manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50.0f, new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
});
另外，LocationManager里边还可以获取并设置GPS、NetWork等提供获取位置的方式，这里不在赘述。

SearchManager

实际开发中，并不需要直接与SearchManager交互，因为Activity和Intent（ACTION_SEARCH） 中已经提供了搜索的方法。

SensorManager

使用SensorManager时要注意传感器的释放，避免耗电，锁屏时系统会自动释放传感器。

public class SensorActivity extends Activity implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;

    public SensorActivity() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }
}
Vibrator

需要权限：android.Manifest.permission#VIBRATE
Vibrator使用起来比较简单，两个常用方法：
//振动milliseconds毫秒
public void vibrate(long milliseconds)
//pattern控制振动的开关，数组内第一个元素表示等待多长时间开始振动，第二个元素表示振动多长时间，依次类推。repeat表示从数组内开始重复振动的索引，传－1表示不重复。
public void vibrate(long[] pattern, int repeat)
//停止振动
public abstract void cancel();

ConnectivityManager

ConnectivityManager提供网络连接状态查询，并在网络状态改变时通知App。
主要作用：

监听网络连接（Wi-fi，GPRS，UMTS，等）
网络连接改变时发送广播
网络丢失时试图故障转移到另一个网络
提供查询大致或精确可用网络状态的API
提供查询并选定数据传输网络的API
//网络是否可用
ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
return (networkInfo != null && networkInfo.isConnected());

//判断Wifi状态
ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
return manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

//注册广播，接受网络状态变化，
IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
registerReceiver(NetworkReceiver, filter);
WifiManager

WifiManager管理各种Wi-fi连接，提供打开、关闭Wi-fi，连接、断开连接、添加网络，浏览接入点，获取连接网络信息的API。通过Context.getSystemService(Context.WIFI_SERVICE)获取实例时应该使用application context，避免内存泄漏。

AudioManager

AudioManager管理音量和铃声模式。

//获取铃声模式AudioManager.RINGER_MODE_NORMAL、AudioManager.RINGER_MODE_SILENT、AudioManager.RINGER_MODE_VIBRATE
public int getRingerMode()

//设置铃声模式
public void setRingerMode(int ringerMode)

//获取流最大音量
public int getStreamMaxVolume(int streamType) 

//获取流最小音量
public int getStreamMinVolume(int streamType)

//获取流当前音量
public int getStreamVolume(int streamType)

//调整最相关的流的音量
//direction ADJUST_LOWER、ADJUST_RAISE、ADJUST_SAME、ADJUST_MUTE、ADJUST_UNMUTE
public void adjustVolume(int direction, int flags)

//设备音量是否锁定
public boolean isVolumeFixed()
TelephonyManager

通过TelephonyManager可以获取手机卡、运营商、当前网络、国家代码等信息。

TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
Log.e("getDeviceId",manager.getDeviceId());//deviceID
Log.e("getNetworkOperatorName",manager.getNetworkOperatorName());//运营商名称
Log.e("isNetworkRoaming",manager.isNetworkRoaming()+"");//是否漫游
Log.e("getNetworkCountryIso",manager.getNetworkCountryIso());//国家代码
Log.e("getNetworkType",manager.getNetworkType()+"");//当前网络类型，2G\3G\4G
Log.e("getSimState",manager.getSimState()+"");//默认SIM卡状态
Log.e("getSimOperator",manager.getSimOperator());//SIM卡经营者
Log.e("getSimOperatorName",manager.getSimOperatorName());//SIM卡经营者名称
Log.e("getSimCountryIso",manager.getSimCountryIso());//SIM卡经营者所在国家代码
Log.e("getSimSerialNumber",manager.getSimSerialNumber());//SIM卡序列号
Log.e("getLine1Number",manager.getLine1Number());//手机号
... ...
InputMethodManager

InputMethodManager用来管理软键盘。常用方法包括hide，show，toggle.

InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        switch (view.getId()) {
            case R.id.hide:
            //隐藏软键盘
                manager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case R.id.show:
               //显示选择输入法悬浮窗
                manager.showInputMethodPicker();
                //显示软键盘
                manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.toggle:
               //软键盘开关
                manager.toggleSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
UiModeManager

UiModeManager管理设备夜间模式和车载模式，常用方法：

UiModeManager manager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        switch (view.getId()){
            case R.id.car_mode_yes:
//                开启车载模式
                manager.enableCarMode(0);
                break;
            case R.id.car_mode_no:
//                关闭车载模式
                manager.disableCarMode(0);
                break;
            case R.id.night_mode_yes:
//                开启夜间模式，注意setNightMode只有在开启车载模式时才起作用
                manager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                Log.e(TAG, String.valueOf(manager.getNightMode()));
                break;
            case R.id.night_mode_no:
//                关闭夜间模式
                manager.setNightMode(UiModeManager.MODE_NIGHT_YES);
                Log.e(TAG, String.valueOf(manager.getNightMode()));
                break;
        }
        ((Button)view).setText(String.valueOf(manager.getCurrentModeType()));
DownloadManager

DownloadManager用于处理下载长时间下载操作。DownloadManager会在后台下载，监听HTTP连接并在下载失败或者系统重启或者连接变化是重新下载。请求下载是应该注册broadcast receiver处理Notification点击和下载完成后的操作。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                reference对应startDownload中manger.enqueue的返回值。
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.e(TAG, "onReceive: "+reference);
            }
        };
        registerReceiver(receiver, filter);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.download_start:
                startDownload();
                break;
        }
    }

    private void startDownload() {
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(PATH));
        down.setDescription("test  test");
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_MOVIES,"test.mp4");
        long l = manager.enqueue(down);
        Log.e(TAG, "startDownload: "+l);
    }
BatteryManager

BatteryManager包涵了ACTION_BATTERY_CHANGED这个Intent的常量和字符串，提供了查询电量和充电属性的API。

IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
Intent batteryStatus = context.registerReceiver(null, filter);
//你可以读到充电状态,如果在充电，可以读到是usb还是交流电

// 是否在充电
int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                     status == BatteryManager.BATTERY_STATUS_FULL;

// 怎么充
int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
另外除去上述常见Service之外还有PowerManager、KeyguardManager、StorageManager、MediaRouter、SubscriptionManager、CarrierConfigManager、JobScheduler、NetworkStatsManager、HardwarePropertiesManager后续补全。

