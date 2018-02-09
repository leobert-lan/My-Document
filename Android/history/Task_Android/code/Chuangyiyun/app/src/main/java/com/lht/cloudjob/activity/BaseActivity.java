package com.lht.cloudjob.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;

import com.alibaba.fastjson.JSON;
import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.toast.ToastUtils;

import java.io.File;

public abstract class BaseActivity extends UMengActivity {

    public static boolean onWifi = false;

    public static boolean isConnected = false;

//    public static String thumbnailPath;

    public static String previewDir;

    public static boolean isOnlyOnWifi = true;

    private Toolbar toolbar;

    private static File downloadDir = null;

    private static File publicDownloadDir = null;


    public Toolbar getToolbar() {
        return toolbar;
    }

    private boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.addActivity(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        toolbar = (Toolbar) inflater.inflate(R.layout.toolbar_default, null);
        setSupportActionBar(toolbar);
//        doJobsAboutPermissions();

    }

//    private static boolean hasTryToDoJobsAboutPermissions = false;

    protected synchronized void doJobsAboutPermissions() {
//        if (hasTryToDoJobsAboutPermissions) {
//            return;
//        }
//        hasTryToDoJobsAboutPermissions = true;
        grantPermissions();
        writePermission = checkWritePermission();
        createIndividualFolder();
    }

    private boolean checkWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PermissionsManager.getInstance().hasPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

    public void grantCameraPermission(PermissionsResultAction action) {
        DLog.i(Lmsg.class, "call grant camera permission");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //程序可以认为已经异常，保护安全做deny
            DLog.e(Lmsg.class, "error call grant camera permission below Android M");
            action.onDenied(Manifest.permission.CAMERA);
            return;
        }
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{Manifest.permission.CAMERA}, action);
        DLog.i(Lmsg.class, "do grant camera permission");
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
        DLog.d(Lmsg.class, "self call grantPermissions ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(getActivity(), new
                PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        DLog.i(getClass(), "All permission granted");
                        writePermission = true;
                        createIndividualFolder();
                    }

                    @Override
                    public void onDenied(String permission) {
                        DLog.e(getClass(), "permission denied:" + permission);
                        grant(permission);
                    }
                });
    }

    protected void grant(final String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(getActivity(),
                new String[]{permission},
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            createIndividualFolder();
                        }
                    }

                    @Override
                    public void onDenied(String permission) {
//                        grant(permission);
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onResume() {
        createIndividualFolder();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        toolbar = null;//avoid memory leak
        MainApplication.removeActivity(getActivity());
        super.onDestroy();
    }

    private String getStorageRoot() {
        if (writePermission) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return getCacheDir().getPath();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DLog.i(Lmsg.class, "requestPermission:\r\n" + JSON.toJSONString(permissions)
                + "\r\nresult:\r\n" + JSON.toJSONString(grantResults));
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    static String localImageCache;

    private SharedPreferences spToken = null;

    private String getUsername() {
        if (spToken == null) {
            spToken = getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
        }

        return spToken.getString(SPConstants.Token.KEY_USERNAME, "default");
    }

    /**
     * @Title: createIndividualFolder
     * @Description: 创建个人文件夹
     * @author: leobert.lan
     */
    public void createIndividualFolder() {
        if (!isSdCardMounted()) {
            ToastUtils.show(this, R.string.sdcard_not_mounted, ToastUtils.Duration.l);
//            return;
        }

        downloadDir = new File(getStorageRoot() + "/Vso/CloudJob/" + getUsername() + "/download");
        previewDir = getStorageRoot() + "/Vso/CloudJob/" + getUsername() + "/preview";
        localImageCache = getStorageRoot() + "/Vso/CloudJob/" + getUsername() + "/localImageCache";
        publicDownloadDir = new File(getStorageRoot() + "/Download");
        mkdirs(downloadDir);
        mkdirs(new File(previewDir));
        mkdirs(new File(localImageCache));
        mkdirs(publicDownloadDir);
    }

    private void mkdirs(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 获取下载目录
     *
     * @return
     */
    public static File getDownloadDir() {
        return downloadDir;
    }

    /**
     * 公共下载目录（系统下载目录）
     *
     * @return
     */
    public static File getPublicDownloadDir() {
        return publicDownloadDir;
    }

    /**
     * 检测外部存储是否加载
     */
    private boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    public static boolean isConnected() {
        return isConnected;
    }

    public static void setConnected(boolean isConnected) {
        BaseActivity.isConnected = isConnected;
    }

    public static boolean isOnWifi() {
        return onWifi;
    }

    public static void setOnWifi(boolean onWifi) {
        BaseActivity.onWifi = onWifi;
    }

//    public static String getThumbnailPath() {
//        return thumbnailPath;
//    }

    public static String getLocalImageCachePath() {
        return localImageCache;
    }

    public static File getLocalImageCache() {
        return new File(getLocalImageCachePath());
    }

    public static String getPreviewDir() {
        return previewDir;
    }

    public static File getPreviewDirCache() {
        return new File(previewDir);
    }

    public static boolean isOnlyOnWifi() {
        return isOnlyOnWifi;
    }

    /**
     * desc: TODO: 描述方法
     *
     * @param isOnlyOnWifi
     */
    public static void setOnlyOnWifi(boolean isOnlyOnWifi) {
        BaseActivity.isOnlyOnWifi = isOnlyOnWifi;
    }

    /**
     * 获取系统相册的位置
     *
     * @return
     */
    private static File systemImageDir = null;

    public static File getSystemImageDir() {
        if (systemImageDir == null) {
            systemImageDir = new File(Environment.getExternalStorageDirectory().getPath() +
                    "/DCIM/Camera");
        }
        return systemImageDir;
    }
}
