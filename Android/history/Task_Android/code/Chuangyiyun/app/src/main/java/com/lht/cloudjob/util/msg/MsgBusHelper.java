package com.lht.cloudjob.util.msg;//package com.lht.cloudjob.util.msg;
//
//import android.os.Parcelable;
//import android.util.Log;
//
//import com.lht.cloudjob.util.debug.DLog;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * @package com.lht.cloudjob.util.msg
// * @project AndroidBase
// * @classname MsgBusHelper
// * @description: 助手类
// * Created by leobert on 2016/4/8.
// */
//public class MsgBusHelper {
//
//    public static ArrayList<String> parseKeys(final Object object) {
//        ArrayList<String> keys = new ArrayList<>();
//        final Class<?> clazz = object.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(MsgBusHint.class)) {
//                MsgBusHint inject = field.getAnnotation(MsgBusHint.class);
//                if (inject.isKey()) {
//                    field.setAccessible(true);
//                    try {
//                        String key = (String) field.get(object);
//                        keys.add(key == null?"":key);
//                        Log.d("lmsg", "存在 key,key:"+key);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        continue;
//                    }
//                }
//            }
//        }
//        return keys;
//    }
//
//    public static ArrayList<IMsgBusCallBackHandle> parseCallbacks(final Object object) {
//        ArrayList<IMsgBusCallBackHandle> callbacks = new ArrayList<>();
//        final Class<?> clazz = object.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(MsgBusHint.class)) {
//                MsgBusHint inject = field.getAnnotation(MsgBusHint.class);
//                if (!inject.isKey()) {
//                    field.setAccessible(true);
//                    Log.d("lmsg", "存在 callback");
//                    try {
//                        IMsgBusCallBackHandle temp = (IMsgBusCallBackHandle) field.get(object);
//                        if (temp == null)
//                            continue;
//                        callbacks.add(temp);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        continue;
//                    }
//                }
//            }
//        }
//        return callbacks;
//    }
//
//    public static HashMap<IMsgBusCallBackHandle,String[]> parseCallbacksWithKey(final Object object) {
//        HashMap<IMsgBusCallBackHandle,String[]> ret = new HashMap<>();
//        final Class<?> clazz = object.getClass();
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (field.isAnnotationPresent(MsgBusHint.class)) {
//                MsgBusHint inject = field.getAnnotation(MsgBusHint.class);
//                if (!inject.isKey()) {
//                    String[] keys = inject.bindKey();
//                    field.setAccessible(true);
//                    Log.d("lmsg","存在 callback" );
//                    try {
//                        IMsgBusCallBackHandle temp = (IMsgBusCallBackHandle) field.get(object);
//                        if (temp == null)
//                            continue;
//                        ret.put(temp,keys);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        continue;
//                    }
//                }
//            }
//        }
//        return ret;
//    }
//}