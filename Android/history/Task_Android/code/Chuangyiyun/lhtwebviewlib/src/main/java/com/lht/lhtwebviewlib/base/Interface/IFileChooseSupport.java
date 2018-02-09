package com.lht.lhtwebviewlib.base.Interface;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * <p><b>Package</b> com.lht.lhtwebviewlib.base.Interface
 * <p><b>Project</b> Jsbridge_lib
 * <p><b>Classname</b> IFileChooseSupport
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/6.
 */

public interface IFileChooseSupport {


    // For 3.0+ Devices (Start)
    // onActivityResult attached before constructor
    void openFileChooser(ValueCallback uploadMsg, String acceptType);


    // For Lollipop 5.0+ Devices
    boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback,
                              WebChromeClient.FileChooserParams fileChooserParams);

    //For Android 4.1 only
    void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture);

    void openFileChooser(ValueCallback<Uri> uploadMsg);

    class DefaultFileChooseSupportImpl implements IFileChooseSupport {

        //    static WebView mWebView;
        public ValueCallback<Uri> mUploadMessage;
        public ValueCallback<Uri[]> uploadMessage;
        public static final int REQUEST_SELECT_FILE = 100;
        public static final int FILECHOOSER_RESULTCODE = 1;

        private final Activity activity;

        /**
         * you must call {{@link DefaultFileChooseSupportImpl#onActivityResult} in you activity
         * @param activity
         */
        public DefaultFileChooseSupportImpl(Activity activity) {
            this.activity = activity;
        }

        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB) // 11
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
        }


        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)//21
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        //For Android 4.1 only
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }


        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (requestCode == REQUEST_SELECT_FILE) {
                    if (uploadMessage == null)
                        return;
                    uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                    uploadMessage = null;
                }
            } else if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == mUploadMessage)
                    return;
                // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
                // Use RESULT_OK only if you're implementing WebView inside an Activity
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            } else {
                Toast.makeText(activity, "Failed to Upload Image", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
