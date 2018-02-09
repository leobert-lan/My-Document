package com.lht.cloudjob.util;

import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.util.md5.Md5Util;

/**
 * <p><b>Package</b> com.lht.cloudjob.util
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> VsoUtil
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/7
 */
public class VsoUtil {
    public static String createVsoSessionCode(String username, String userid) {
        String _s = IPublicConst.VSO_SESSION_PREFIX + username + userid;
        return Md5Util.getStringMd5(_s);
    }
}
