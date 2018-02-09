package com.lht.cloudjob.util.phonebasic;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.lht.cloudjob.test.UMengTestHelpler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * <p><b>Package</b> com.lht.cloudjob.util.phonebasic
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SettingUtil
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/22.
 */

public class SettingUtil {
    /**
     * 启动应用的设置页
     *
     * @param context not null
     */
    public static void startAppSettings(Context context) {
        final String PACKAGE_URL_SCHEME = "package:"; // 方案
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
        context.startActivity(intent);
    }

    public static String getDeviceId(Application application) {
        String device_id = null;
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) application
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (UMengTestHelpler.checkPermission(application, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }


            String mac = null;
            FileReader fstream;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                    //ignore
                    e.printStackTrace();
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(application.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            device_id = "android_unKnown";
        }
        return device_id;
    }
}
