package com.lht.creationspace.hybrid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> WebViewJsNaverHelper </p>
 * <p><b>Description:</b> TODO </p>"javascript:
 * Created by leobert on 2017/4/13.
 */
@Deprecated
public class WebViewJsOffsetHelper {

    public static void scrollToElement(WebView webView, String id) {
        String naverJS = getJsNaverCode(webView.getContext());
        if (!TextUtils.isEmpty(naverJS)) {
            webView.loadUrl("javascript:"+naverJS+"smoothScrollToElement("+id+");");
        }
    }

    private static final String JS_NAVER_FILE = "webScroll.js";

    private static String JS_NAVER_CODE = null;

    private static String getJsNaverCode(Context context) {
        if (JS_NAVER_CODE == null) {
            JS_NAVER_CODE = getFromAssets(context, JS_NAVER_FILE);
        }
        return JS_NAVER_CODE;
    }


    // 读取assets中的文件
    @NonNull
    private static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(
                    context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = bufReader.readLine()) != null)
                result.append(line);

            bufReader.close();
            inputReader.close();

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
