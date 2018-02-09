package com.lht.lhtwebviewlib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.Interface.IMediaTrans;
import com.lht.lhtwebviewlib.base.Interface.WebViewJavascriptBridge;
import com.lht.lhtwebviewlib.base.NotFoundHandler;
import com.lht.lhtwebviewlib.base.model.BridgeUtil;
import com.lht.lhtwebviewlib.base.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge {

    private static boolean DEBUG_MODE = false;

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static void setDebugMode(boolean debugMode) {
        DEBUG_MODE = debugMode;
    }

    private final String TAG = "BridgeWebView-leobert";

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<>();
    BridgeHandler defaultHandler = new DefaultHandler();

    /**
     * startupMessage:发送给JS的消息
     */
    private List<Message> startupMessage = new ArrayList<>();

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    BridgeWebChromeClient bridgeWebChromeClient;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    /**
     * @param handler default handler,handle messages send by js without assigned
     *                handler name, if js message has handler name, it will be
     *                handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);

        //added by leobert
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setUseWideViewPort(true);

        //自适应屏幕
        this.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);


        // 如果满足调试条件 （system version >= 19 ,named KITKAT）开启调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        this.setWebViewClient(generateBridgeWebViewClient());

        bridgeWebChromeClient = new BridgeWebChromeClient();
        this.setWebChromeClient(bridgeWebChromeClient);
    }

    protected BridgeWebViewClient generateBridgeWebViewClient() {
        return new BridgeWebViewClient(this);
    }

    public void setIMediaTrans(IMediaTrans iMediaTrans) {
        this.bridgeWebChromeClient.setIMediaTrans(iMediaTrans);
    }

    /**
     * @param url
     * @Title: handlerReturnData
     * @Description: 呼叫webview处理返回数据
     * @author: leobert.lan
     */
    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        Log.d(TAG, "check data:" + data);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        } else {
            Log.wtf(TAG, "底层消息框架出错");
        }
    }

    /**
     * 供js间接调用的 send方法，发送数据
     *
     * @see WebViewJavascriptBridge#send(String)
     */
    @Override
    public void send(String data) {
        send(data, null);
    }

    /**
     * 供js间接调用的 send方法，包含response接口
     *
     * @see WebViewJavascriptBridge#send(String,
     * CallBackFunction)
     */
    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * @param handlerName      句柄名称
     * @param data             数据
     * @param responseCallback 添加到 map {responseCallBacks}的java回调方法
     * @Title: doSend
     * @Description: 向JS 发送数据
     * @author: leobert.lan
     */
    private void doSend(String handlerName, String data,
                        CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(
                    BridgeUtil.CALLBACK_ID_FORMAT,
                    ++uniqueId
                            + (BridgeUtil.UNDERLINE_STR + SystemClock
                            .currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    /**
     * @param m
     * @Title: queueMessage
     * @Description: 添加到消息队列并呼叫分发
     * @author: leobert.lan
     */
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    /**
     * @param m
     * @Title: dispatchMessage
     * @Description: 分发消息
     * @author: leobert.lan
     */
    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        // escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");

        // 格式化 JS
        String javascriptCommand = String.format(
                BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);

        // 注意，主线程才能操作UI
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            // 注入JS 并执行
            this.loadUrl(javascriptCommand);
        }
    }

    /**
     * @Title: flushMessageQueue
     * @Description: 目的在于获取js中的消息队列
     * @author: leobert.lan
     */
    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {

            // 注入方法（如果DOM中不存在）：获取native消息队列内容 并调用
            // 并向map {responseCallback} 中添加回调

            // this is the important code
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA,
                    generateCallBackFunction());
        } else {
            // 不是主线程,无法操作任何事情
            Log.wtf(TAG, "caller is not the main Thread,check!");
        }

    }

    /**
     * @param jsUrl
     * @param returnCallback
     * @Title: loadUrl
     * @Description: 注入 JS并执行,并向map {responseCallback} 中添加回调
     * @author: leobert.lan
     */
    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl),
                returnCallback);
    }

    /**
     * register handler,so that javascript can call it 注册handler，js即可调用
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data,
                            CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }

    /**
     * @return CallBackFunction 接口对象 ： handler 中的回调方法
     * @Title: generateCallBackFunction
     * @Description: 生成接口对象 responseCallBack方法
     * @author: leobert.lan
     */
    private CallBackFunction generateCallBackFunction() {
        return new CallBackFunction() {

            @Override
            public void onCallBack(String data) {

                Log.d(TAG, "check the data form callbackFunction,"
                        + "this will add to responseCallBack\r\n " + "data is:"
                        + data);

                // deserializeMessage
                List<Message> list = null;
                try {
                    list = Message.toArrayList(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (list == null || list.size() == 0) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    Message m = list.get(i);
                    String responseId = m.getResponseId();
                    // 是否是response
                    if (!TextUtils.isEmpty(responseId)) {
                        // 是response
                        // 根据responseId 获取已注册的对应的方法，执行数据回传，注销数据回传方法
                        Log.d(TAG, "this is response");

                        CallBackFunction function = responseCallbacks
                                .get(responseId);
                        String responseData = m.getResponseData();
                        function.onCallBack(responseData);
                        responseCallbacks.remove(responseId);
                    } else {
                        // 不是response

                        Log.d(TAG, "this is not response");

                        CallBackFunction responseFunction = null;
                        // if had callbackId
                        final String callbackId = m.getCallbackId();
                        if (!TextUtils.isEmpty(callbackId)) {
                            // js 需要response,实现回调方法：
                            // js callback的id , response 的data
                            // 添加到消息队列并分发

                            responseFunction = new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                    // 实现数据回传到js

                                    Message responseMsg = new Message();
                                    responseMsg.setResponseId(callbackId);
                                    responseMsg.setResponseData(data);
                                    queueMessage(responseMsg);
                                }
                            };
                        } else {
                            responseFunction = new CallBackFunction() {
                                @Override
                                public void onCallBack(String data) {
                                    // do nothing
                                    Log.i(TAG,
                                            "this j2n caller do not have a response_id");
                                }
                            };
                        }

                        // “实例化”句柄 important
                        BridgeHandler handler;
                        if (!TextUtils.isEmpty(m.getHandlerName())) {
                            // js 指定了句柄名称 寻找对应的句柄
                            handler = messageHandlers.get(m.getHandlerName());
                        } else {
                            // js 没有指定句柄名称,使用默认句柄
                            handler = defaultHandler;
                        }

                        if (handler != null) {
                            Log.i(TAG, "on response data");
                            handler.handler(m.getData(), responseFunction);
                        } else {
// TODO: 2016/12/8    添加无目标方法的回调响应
                            handler = new NotFoundHandler();
                            handler.handler(m.getHandlerName(), responseFunction);
                            Log.wtf(TAG,
                                    "404. function not found,check functionName:"
                                            + m.getHandlerName());
                        }
                    }
                }
            }
        };
    }
}
