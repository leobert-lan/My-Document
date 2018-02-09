package com.lht.cloudjob.interfaces;

import android.os.Build;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.util.VersionUtil;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IPublicConst
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/6
 */
public interface IPublicConst {
    String TEL = "400-164-7979";

    String VSO_SESSION_PREFIX = "v-s-o-c*h*i*n*a";

    /**
     * 简体中文版用户守则
     */
    String SIMPLIFIED_AGREEMENT = "http://m.vsochina.com/protocol/user-agreement";

    /**
     * APP分享h5页面
     */
    String SHARE_APP_LINK = "http://m.vsochina.com/protocol/downloadApp";

    /**
     * m站交付协议
     */
    String AGREEMENT_MODEL_URL = "http://m.vsochina.com/protocol?taskbn=%s&auth_token=%s&auth_username=%s";

    /**
     * m站消息详情
     */
    String MSGINFO_URL_FORMAT = "http://m.vsochina.com/message/detail/%s?auth_token=%s&auth_username=%s";

    class MsgInfoUrlHelpler {
       public static String formatUrl(String msgId) {
           return  String.format(MSGINFO_URL_FORMAT, msgId, IVerifyHolder.mLoginInfo.getAccessToken(),
                   IVerifyHolder.mLoginInfo.getUsername());
       }
    }

    String APP_SID = "757D6429-6ED8-4869-A904-8905B8045B3D";

    String APP_SYS = "android";


    String USERAGENT_MODEL = "VSO-CloudJob/%s Android/%s Language/%s";

    String USER_AGENT = String.format(USERAGENT_MODEL,
            VersionUtil.getVersion(MainApplication.getOurInstance()), Build.VERSION.SDK_INT,
            Locale.getDefault().getLanguage());
}
