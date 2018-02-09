package com.lht.creationspace.cfg;

import android.os.Build;

import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.util.VersionUtil;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces
 * <p><b>Project</b> VsoC
 * yy
 * <p><b>Classname</b> IPublicConst
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/6
 */
public interface IPublicConst {
    String TEL = "400-164-7979";

    String VSO_SESSION_PREFIX = "v-s-o-c*h*i*n*a";

    /**
     * 蓝海创意云用户协议
     */
    String SIMPLIFIED_AGREEMENT = "https://m.vsochina.com/maker/module/protocol.html";


    /**
     * 入驻申请协议 useless
     */
    String SIMPLIFIED_AGREEMENT_JOININ = "http://m.vsochina.com/protocol/user-agreement";

    String SHARE_APP_LINK = "https://m.vsochina.com/maker/module/loadpage.html";

    String MSGINFO_URL_FORMAT = "http://m.vsochina.com/message/detail/%s?auth_token=%s&auth_username=%s";

    class MsgInfoUrlHelpler {
       public static String formatUrl(String msgId) {
           return  String.format(MSGINFO_URL_FORMAT, msgId, IVerifyHolder.mLoginInfo.getAccessToken(),
                   IVerifyHolder.mLoginInfo.getUsername());
       }
    }

    String APP_SID = "FLAG";

    String APP_SYS = "android";


    String USERAGENT_MODEL = "VSO-CreationSpace/%s Android/%s Language/%s";

    String USER_AGENT = String.format(USERAGENT_MODEL,
            VersionUtil.getVersion(MainApplication.getOurInstance()), Build.VERSION.SDK_INT,
            Locale.getDefault().getLanguage());
}
