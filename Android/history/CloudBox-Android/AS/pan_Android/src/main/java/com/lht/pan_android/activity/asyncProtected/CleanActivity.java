// package com.lht.pan_android.activity.asyncProtected;
//
// import java.io.File;
// import java.util.ArrayList;
//
// import android.annotation.SuppressLint;
// import android.app.Activity;
// import android.content.SharedPreferences;
// import android.os.Bundle;
// import android.os.Handler;
// import android.os.Message;
// import android.text.TextUtils;
// import android.util.Log;
// import android.view.View;
// import android.view.View.OnClickListener;
// import android.view.Window;
// import android.widget.ImageView;
// import android.widget.LinearLayout;
// import android.widget.ProgressBar;
// import android.widget.TextView;
//
// import com.lht.pan_android.R;
// import com.lht.pan_android.Interface.IKeyManager;
// import com.lht.pan_android.Interface.IPreventPenetrate;
// import com.lht.pan_android.activity.BaseActivity;
// import com.lht.pan_android.activity.UMengActivity;
// import com.lht.pan_android.util.CloudBoxApplication;
// import com.lht.pan_android.util.DLog;
// import com.lht.pan_android.util.DLog.LogLocation;
// import com.lht.pan_android.util.file.FileUtil;
// import com.lht.pan_android.view.popupwins.CustomDialog;
// import
// com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
//
/// **
// * @ClassName: CleanActivity
// * @Description: TODO 此处我将业务逻辑写在里面了，需要mvp分层一下
// * @date 2016年4月5日 上午10:45:41
// *
// * @author leobert.lan
// * @version 1.0
// * @since JDK 1.7
// */
// public class CleanActivity extends AsyncProtectedActivity implements
// OnClickListener, IPreventPenetrate {
//
// private ImageView btnBack;
// private LinearLayout btnCleanCache;
//
// private LinearLayout btnCleanDownload;
//
// private static String PageName = "CleanActivity";
//
// private ProgressBar mProgressBar;
//
// private MyHandler mCustomHandler;
//
// private TextView txtCacheSize;
//
// private TextView txtDownloadSize;
//
// private String username;
//
// @Override
// protected void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// requestWindowFeature(Window.FEATURE_NO_TITLE);
// CloudBoxApplication.addActivity(this);
// setContentView(R.layout.activity_clean);
// mCustomHandler = new MyHandler();
// SharedPreferences sp = getSharedPreferences(IKeyManager.Token.SP_TOKEN,
// Activity.MODE_PRIVATE);
// username = sp.getString(IKeyManager.Token.KEY_USERNAME, "");
// initView();
// initEvent();
// }
//
// private void initView() {
// btnBack = (ImageView) findViewById(R.id.activity_mine_setting_back);
//
// btnCleanCache = (LinearLayout) findViewById(R.id.clean_ll_ccache);
//
// btnCleanDownload = (LinearLayout) findViewById(R.id.clean_ll_cdownload);
//
// mProgressBar = (ProgressBar) findViewById(R.id.clean_progress);
//
// txtCacheSize = (TextView) findViewById(R.id.clean_tv_cachesize);
//
// txtDownloadSize = (TextView) findViewById(R.id.clean_tv_downloadsize);
//
// downloadCleanAlert = new CustomDialog(this, this);
// downloadCleanAlert.setContent("是否清空已下载及正下载内容？");
// downloadCleanAlert.setPositiveButton("确定");
// downloadCleanAlert.setNegativeButton("取消");
// downloadCleanAlert
// .setPositiveClickListener(new OnPositiveClickListener() {
//
// @Override
// public void onPositiveClick() {
// cleanDownload();
// reportCountEvent(CALC_CB_CLEAN_DOWNLOAD);
// }
// });
//
// cacheCleanAlert = new CustomDialog(this, this);
// cacheCleanAlert.setContent("是否清空缓存？");
// cacheCleanAlert.setPositiveButton("确定");
// cacheCleanAlert.setNegativeButton("取消");
// cacheCleanAlert.setPositiveClickListener(new OnPositiveClickListener() {
//
// @Override
// public void onPositiveClick() {
// cleanCache();
// reportCountEvent(CALC_CB_CLEAN_CACHE);
// }
// });
// }
//
// private void initEvent() {
// btnBack.setOnClickListener(this);
// btnCleanCache.setOnClickListener(this);
// btnCleanDownload.setOnClickListener(this);
// }
//
// @Override
// public void onClick(View v) {
// switch (v.getId()) {
// case R.id.activity_mine_setting_back:
// CleanActivity.this.finish();
// break;
// case R.id.clean_ll_ccache:
// askCleanCache();
// break;
// case R.id.clean_ll_cdownload:
// askCleanDownload();
// break;
// default:
// break;
// }
// }
//
// @Override
// protected void onResume() {
// super.onResume();
// calcSize();
// }
//
// @Override
// public void onBackPressed() {
// Log.d(PageName, "back");
// super.onBackPressed();
// }
//
// private CustomDialog downloadCleanAlert = null;
//
// private CustomDialog cacheCleanAlert = null;
//
// private void askCleanDownload() {
// downloadCleanAlert.show();
// }
//
// private void askCleanCache() {
// cacheCleanAlert.show();
// }
//
// private void cleanDownload() {
// CleanAction action = new CleanAction();
// action.dbName = IKeyManager.DownLoadDataBaseKey.DOWNLOAD_DATABASE_NAME;
// action.tableName = IKeyManager.DownLoadDataBaseKey.DOWNLOAD_TABLE_NAME;
// action.key = username;
// ArrayList<File> files = new ArrayList<File>();
// files.add(getIndividualFolder());
// action.filesToClean = files;
// new CleanThread(mCustomHandler, action).start();
// }
//
// private void cleanCache() {
// CleanAction action = new CleanAction();
// ArrayList<File> files = new ArrayList<File>();
// File previews = new File(BaseActivity.getPreviewPath());
// File thumbnails = new File(BaseActivity.getThumbnailPath());
// File localThumbs = new File(BaseActivity.getLocalImageCachePath());
// files.add(previews);
// files.add(thumbnails);
// files.add(localThumbs);
// files.add(getCacheDir());
// action.filesToClean = files;
// new CleanThread(mCustomHandler, action).start();
// }
//
// @Override
// protected ProgressBar getProgressBar() {
// return mProgressBar;
// }
//
// @Override
// protected String getPageName() {
// return CleanActivity.PageName;
// }
//
// @Override
// protected UMengActivity getActivity() {
// return CleanActivity.this;
// }
//
// @Override
// public void preventPenetrate(Activity activity, boolean isProtectNeed) {
// if (activity instanceof CleanActivity)
// setActiveStateOfDispatchOnTouch(!isProtectNeed);
// else
// DLog.e(CleanActivity.class, new LogLocation(),
// "check the activity you used to initialize a"
// + " dialog,and the code in dialog while callback");
// }
//
// @SuppressLint("HandlerLeak")
// private final class MyHandler extends Handler {
// static final int WHAT_START = 1;
// static final int WHAT_ERROR = 2;
// static final int WHAT_DELETECOMPLETE = 3;
// static final int WHAT_CALCCOMPLETE = 4;
//
// static final String KEY_DOWNLOADSIZE = "down";
// static final String KEY_CACHESIZE = "cache";
//
// @Override
// public void handleMessage(Message msg) {
// super.handleMessage(msg);
// switch (msg.what) {
// case WHAT_START:
// showWaitView(true);
// break;
// case WHAT_ERROR:
// //TODO 暂时没有实际的error情况
// break;
// case WHAT_DELETECOMPLETE:
// cancelWaitView();
// calcSize();
// break;
// case WHAT_CALCCOMPLETE:
// Bundle b = msg.getData();
// long downloadSize = b.getLong(KEY_DOWNLOADSIZE, 0L);
// long cacheSize = b.getLong(KEY_CACHESIZE, 0L);
// showSize(downloadSize, cacheSize);
// cancelWaitView();
// break;
// default:
// break;
// }
// }
// }
//
// final class CleanAction {
// ArrayList<File> filesToClean = new ArrayList<File>();
// String dbName = "";
// String tableName = "";
// String key = "";
// }
//
// class CleanThread extends Thread {
// private final Handler mHandler;
// private final CleanAction mCleanAction;
//
// CleanThread(Handler hanlder, CleanAction action) {
// mHandler = hanlder;
// mCleanAction = action;
// }
//
// @Override
// public void run() {
// super.run();
// postStart();
// if (dbCleanNeed(mCleanAction)) {
// deleteDbRecord(mCleanAction);
// Log.d(PageName, "db clean complete");
// }
// if (!mCleanAction.filesToClean.isEmpty()) {
// deleteFiles(mCleanAction.filesToClean);
// }
// //补充被删除的基本文件夹
// createIndividualFolder();
// postComplete();
// }
//
// private void deleteDbRecord(CleanAction action) {
// getDownServiceBinder().doPauseAllInBackground();
// getDownLoadDataBaseUtils().delete(action.key);
// }
//
// private boolean dbCleanNeed(CleanAction action) {
// boolean ret = action.dbName
// .equals(IKeyManager.DownLoadDataBaseKey.DOWNLOAD_DATABASE_NAME)
// & action.tableName
// .equals(IKeyManager.DownLoadDataBaseKey.DOWNLOAD_TABLE_NAME)
// & !TextUtils.isEmpty(action.key);
// return ret;
// }
//
// private void deleteFiles(ArrayList<File> filesToClean) {
// for (File f : filesToClean) {
// delete(f);
// }
// }
//
// void postStart() {
// mHandler.sendEmptyMessage(MyHandler.WHAT_START);
// }
//
// void postError() {
// mHandler.sendEmptyMessage(MyHandler.WHAT_ERROR);
// }
//
// void postComplete() {
// mHandler.sendEmptyMessage(MyHandler.WHAT_DELETECOMPLETE);
// }
//
// private void delete(File file) {
// if (file.exists()) {
// if (file.isFile()) {
// file.delete();
// } else if (file.isDirectory()) {
// File files[] = file.listFiles();
// for (int i = 0; i < files.length; i++) {
// this.delete(files[i]);
// }
// }
// file.delete();
// } else {
// //TODO 没有任何文件的时候会执行，但不是error
// postError();
// }
// }
// }
//
// /**
// * @Title: initSize
// * @Description: 初始化文件大小
// * @author: leobert.lan
// */
// public void calcSize() {
//// Message msg = new Message();
//// msg.what = MyHandler.WHAT_START;
//// mCustomHandler.sendMessage(msg);
// new CalcThread(mCustomHandler).start();
// }
//
// public void showSize(long downloadSize, long cacheSize) {
// txtDownloadSize.setText(FileUtil.calcSize(downloadSize));
//
// txtCacheSize.setText(FileUtil.calcSize(cacheSize));
// }
//
// class CalcThread extends Thread {
// private final Handler mHandler;
//
// public CalcThread(Handler hanlder) {
// mHandler = hanlder;
// }
//
// @Override
// public void run() {
// super.run();
// mHandler.sendEmptyMessage(MyHandler.WHAT_START);
// File downFile = BaseActivity.getIndividualFolder();
// File thumbFile = new File(BaseActivity.getThumbnailPath());
// File previewFile = new File(BaseActivity.getPreviewPath());
// File localThumbFile = new File(
// BaseActivity.getLocalImageCachePath());
// File innerCache = getCacheDir();
// Bundle b = new Bundle();
// try {
// long downSize = getFileSize(downFile);
// long cacheSize = getFileSize(thumbFile)
// + getFileSize(previewFile)
// + getFileSize(localThumbFile) + getFileSize(innerCache);
// DLog.d(CleanActivity.class, "downsize:"+downSize+"cacheSize:"+cacheSize);
// b.putLong(MyHandler.KEY_DOWNLOADSIZE, downSize);
// b.putLong(MyHandler.KEY_CACHESIZE, cacheSize);
// } catch (Exception e) {
// mHandler.sendEmptyMessage(MyHandler.WHAT_ERROR);
// e.printStackTrace();
// }
// Message msg = new Message();
// msg.what = MyHandler.WHAT_CALCCOMPLETE;
// msg.setData(b);
// mHandler.sendMessage(msg);
// }
//
// private long getFileSize(File f) throws Exception {
// long size = 0;
// if (!f.exists())
// return size;
// File flist[] = f.listFiles();
// for (int i = 0; i < flist.length; i++) {
// if (flist[i].isDirectory()) {
// size = size + getFileSize(flist[i]);
// } else {
// size = size + flist[i].length();
// }
// }
// return size;
// }
// }
//
// }