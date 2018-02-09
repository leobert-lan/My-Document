package com.lht.cloudjob.interfaces.keys;

/**
 * @author
 * @version 1.0
 * @date 2016/4/25
 */
public interface SPConstants {

    /**
     * app 基础sharedpreferences配置
     */
    interface Basic {
        /**
         * 命名
         */
        String SP_NAME = "base";


        /**
         * 属性1：启动次数统计
         */
        String KEY_STARTCOUNT = "start_count";

        String KEY_THEME_COLOR = "cur_theme_color";
    }

    /**
     * @author leobert.lan
     * @version 1.0
     * @ClassName: Token
     * @Description: 管理保存登录验证后核心数据的sp 的名称 和key
     * @date 2015年11月25日 上午11:26:00
     */
    interface Token {

        String SP_TOKEN = "sp_token";

        String KEY_ACCESS_ID = "access_id";

        String KEY_ACCESS_TOKEN = "access_token";

        String KEY_USERNAME = "username";

        String KEY_ACCOUNT = "account";

    }

    interface Timer {
        String SP_TIMER = "sp_timer";

        String KEY_VERIFICATE = "timestamp_";
    }

    /**
     * @author leobert.lan
     * @version 1.0
     * @ClassName: VersionSp
     * @Description: 缓存服务器版本等信息
     * @date 2016年2月29日 上午9:45:43
     */
    interface VersionSp {
        String SP_VERSION = "version_info";

        /**
         * KEY_CHECKEDSAVE:保存
         */
        String KEY_CHECKEDSAVE = "checked_save";

        /**
         * KEY_IGNORED:该版本是否已经忽略
         */
        String KEY_IGNORED = "ignored";
    }
}
