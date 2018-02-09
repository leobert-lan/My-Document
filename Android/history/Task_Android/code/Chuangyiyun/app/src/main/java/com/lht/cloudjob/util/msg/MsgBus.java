package com.lht.cloudjob.util.msg;//package com.lht.cloudjob.util.msg;
//
//import android.os.Bundle;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import com.lht.cloudjob.util.debug.DLog;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * @package com.lht.cloudjob.util.interact
// * @project AndroidBase
// * @classname MsgBus
// * @description: TODO
// * Created by leobert on 2016/4/7.
// */
//public class MsgBus extends android.os.Handler {
//    private static MsgBus ourInstance = null;
//
//    private static HashMap<String, ArrayList<IMsgBusCallBackHandle>> handles = new HashMap<String, ArrayList<IMsgBusCallBackHandle>>();
//
//    public static MsgBus getInstance() {
//        if (ourInstance == null)
//            ourInstance = new MsgBus();
//        if (handles == null)
//            handles = new HashMap<String, ArrayList<IMsgBusCallBackHandle>>();
//        return ourInstance;
//    }
//
//    /**
//     * single instance and make sure a main looper is used
//     */
//    private MsgBus() {
//        super(Looper.getMainLooper());
//    }
//
//    public void register(Object object) {
//        //TODO
//        HashMap<IMsgBusCallBackHandle, String[]> ret = MsgBusHelper.parseCallbacksWithKey(object);
//
//        Log.d("lmsg","发现数量："+ret.size());
//        for (IMsgBusCallBackHandle handler : ret.keySet()) {
//            String[] keys = ret.get(handler);
//            for (String key : keys) {
//                subscribe(key, handler);
//            }
//        }
//    }
//
//    public void subscribe(String key, IMsgBusCallBackHandle handle) {
//        int hash = key.hashCode();
//        String _key = String.valueOf(hash);
//        synchronized (handlersLock) {
//            if (handles.containsKey(_key)) {
//                handles.get(_key).add(handle);
//            } else {
//                ArrayList<IMsgBusCallBackHandle> temp = new ArrayList<>();
//                temp.add(handle);
//                handles.put(_key, temp);
//            }
//        }
//    }
//
//    /**
//     * 取消订阅
//     *
//     * @param key
//     * @param handle
//     */
//    public void deSubscribe(String key, IMsgBusCallBackHandle handle) {
//        int hash = key.hashCode();
//        String _key = String.valueOf(hash);
//        synchronized (handlersLock) {
//            if (handles.containsKey(_key)) {
//                Log.d("lmsg", "移除 存在key");
//                boolean b = handles.get(_key).remove(handle);
//                Log.d("lmsg", "移除是否成功？" + b);
//            } else {
//                Log.d("lmsg", "移除 不存在key");
//            }
//        }
//    }
//
//    public void deSubscribe(Object object) {
//        ArrayList<String> keys = MsgBusHelper.parseKeys(object);
//        ArrayList<IMsgBusCallBackHandle> callbacks = MsgBusHelper.parseCallbacks(object);
//
//        for (IMsgBusCallBackHandle handler : callbacks) {
//            for (String key : keys) {
//                this.deSubscribe(key, handler);
//            }
//        }
//    }
//
//    public synchronized void post(String key) {
//        this.post(key, new Bundle());
//    }
//
//    public synchronized void post(String key, Bundle data) {
//        Message msg = new Message();
//        msg.what = key.hashCode();
//        msg.obj = key;
//        msg.setData(data);
//        sendMessage(msg);
//    }
//
//    private static Object handlersLock = new Object();
//
//    @Override
//    public void handleMessage(Message msg) {
//        String _key = String.valueOf(msg.what);
//        synchronized (handlersLock) {
//            if (handles.containsKey(_key)) {
//                Bundle data = msg.getData();
//                String key = "*/*";
//                try {
//                    key = (String) msg.obj;
//                } catch (ClassCastException e) {
//                    e.printStackTrace();
//                }
//                ArrayList<IMsgBusCallBackHandle> temp = handles.get(_key);
//                DLog.d(getClass(), new DLog.LogLocation(), "存在注册,key:" + key + "，注册量：" + temp.size());
//                for (IMsgBusCallBackHandle handler : temp) {
//                    if (handler != null)
//                        handler.onCallback(key, data);
//                }
//            } else {
//                String key = "*/*";
//                try {
//                    key = (String) msg.obj;
//                } catch (ClassCastException e) {
//                    e.printStackTrace();
//                }
//                DLog.e(getClass(), new DLog.LogLocation(), "没有注册该类型的回调事件,key:" + key);
//            }
//        }
//        super.handleMessage(msg);
//    }
//
//    public static void destroy() {
//        synchronized (MsgBus.class) {
//            handles.clear();
//            handles = null;
//            handlersLock = null;
//            ourInstance = null;
//        }
//    }
//}
