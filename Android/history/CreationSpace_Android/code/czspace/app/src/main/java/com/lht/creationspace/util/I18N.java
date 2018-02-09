package com.lht.creationspace.util;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.lht.creationspace.util.debug.DLog;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.vsocyy.util
 * <p><b>Project</b> AndroidBase
 * <p><b>Classname</b> I18N
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/4/19.
 */
public class I18N {
    public static final String KEY_LANGUAGE = "language-prop";

    public static void changeLanguage(Activity activity, Language language) {
        DLog.e(I18N.class, "changeLanguage1");
        if (language == null)
            return;
        DLog.e(I18N.class,"changeLanguage2");
        AppPreference appPreference = AppPreference.getInstance(activity.getApplication());
        Resources resources = activity.getResources();//获得res资源对象
        Configuration config = resources.getConfiguration();//获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
        config = setLanguageProp(language, config);
        appPreference.commitInt(KEY_LANGUAGE, language.getCode());
        resources.updateConfiguration(config, dm);
    }


    public static enum Language {
        EN(1), ZH_CN(2), ZH_TW(3);
        private final int code;

        Language(int i) {
            code = i;
        }

        public int getCode() {
            return this.code;
        }
//        //机制问题 以下不再支持
////        /**
////         * 香港地区中文
////         */
////        ZH_HK,
////        /**
////         * 澳门地区中文
////         */
////        ZH_MO,
    }

    private static Configuration setLanguageProp(Language language, Configuration config) {
        switch (language) {
            case EN:
                config.locale = Locale.ENGLISH;
                break;
            case ZH_CN:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
//            case ZH_HK:
//                config.locale = Locale.H
//                break;
//            case ZH_MO:
//                break;
            case ZH_TW:
                config.locale = Locale.TRADITIONAL_CHINESE;
                break;
            default:
                break;
        }
        return config;
    }
}

