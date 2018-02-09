package com.lht.creationspace.hybrid.webclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.lht.creationspace.util.DisplayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * <p><b>Package:</b> com.lht.creationspace.webclient </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> WebImageScaleIMPL </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/13.
 */

public class WebImagePreview {
    private static final String JS_IMAGELOAD_FILE = "imageload.js";


//    // 注入js函数监听
//    public static void injectJsImageClickListener(WebView webView) {
//        // 这段js函数的功能就是，遍历所有的img几点，
//        // 并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
//        String imageloadJS = getJsImageloadCode(webView.getContext());
//        if (!TextUtils.isEmpty(imageloadJS)) {
//            webView.loadUrl(imageloadJS);
//        }
//    }

    private static String JS_IMAGELOAD_CODE = null;

    private static String getJsImageloadCode(Context context) {
        if (JS_IMAGELOAD_CODE == null) {
            JS_IMAGELOAD_CODE = getFromAssets(context, JS_IMAGELOAD_FILE);
        }
        return JS_IMAGELOAD_CODE;
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

//    public static class WebImagePreviewComponent {
//
//        public static final String JAVA_ONIMAGECLICK_LISTENER_NAME = "imagelistener";
//        public static final String JAVA_SCROLLIMPL_NAME = "javaScrollImpl";

//        private final OnWebImageClickListener imageClickListener;

//        private final OnWebScrollImpl webScrollImpl;

//        public WebImagePreviewComponent(OnWebImageClickListener imageClickListener,
//                                        OnWebScrollImpl webScrollImpl) {
//            this.imageClickListener = imageClickListener;
//            this.webScrollImpl = webScrollImpl;
//        }

//        public OnWebImageClickListener getImageClickListener() {
//            return imageClickListener;
//        }

//        public OnWebScrollImpl getWebScrollImpl() {
//            return webScrollImpl;
//        }
//
//        public int getPositionHolder() {
//            return imageClickListener.positionHolder;
//        }
//
//        public String[] getIdsHolder() {
//            return imageClickListener.idsHolder;
//        }
//    }

//    @Deprecated
//    public static abstract class OnWebImageClickListener {
//
//        private int positionHolder = 0;
//        private String[] idsHolder;
//
//        @JavascriptInterface
//        public final void openImage(String object, String ids, int position) {
//            DLog.d(WebImagePreview.class, "java interface called by web:openImage()," +
//                    "datas:\r\n" + object);
//
//            try {
//                String[] urls = object.split(",");
//                ArrayList<String> images = new ArrayList<>();
//                for (String s : urls)
//                    images.add(s);
//
//                String[] _ids = ids.split(",");
//
//                positionHolder = position;
//                idsHolder = _ids;
//
//                openImage(images, _ids, position);
//            } catch (JSONException e) {
//                DLog.d(WebImagePreview.class, "parse images error");
//                e.printStackTrace();
//            }
//        }
//
//        @JavascriptInterface
//        public abstract void openImage(List<String> images, String[] ids, int position);
//
//        public static class DefaultOnWebImageClickListenerIMPL extends WebImagePreview.OnWebImageClickListener {
//
//            private final WeakReference<Activity> activityRef;
//
//            private final int reqCode;
//
//            public DefaultOnWebImageClickListenerIMPL(Activity activity,int reqCode) {
//                activityRef = new WeakReference<>(activity);
//                this.reqCode = reqCode;
//            }
//
//            @Override
//            public void openImage(List<String> images, String[] ids, int position) {
//                Log.d("lmsg", "ids:" + JSON.toJSONString(ids));
//                Activity activity = activityRef.get();
//                if (activity == null)
//                    return;
//                Intent intent = ImagePreviewIntentFactory.newImagePreviewIntent(activity,
//                        images, position);
//                activity.startActivityForResult(intent, reqCode);
//            }
//        }
//    }


    public static class OnWebScrollImpl {
        private final WebView webView;

        public OnWebScrollImpl(WebView webView) {
            this.webView = webView;
        }

        @JavascriptInterface
        public final void smoothScrollToY(int yOffset) {
            final int androidYOffset =
                    DisplayUtils.convertDpToPx(webView.getContext(), yOffset);
            smoothScrollWebToY(webView, androidYOffset);
        }

        public void smoothScrollWebToY(WebView view, int yOffset) {
            Log.d("lmsg", "offset:" + yOffset);
            view.scrollTo(0, yOffset);
        }
    }

}
