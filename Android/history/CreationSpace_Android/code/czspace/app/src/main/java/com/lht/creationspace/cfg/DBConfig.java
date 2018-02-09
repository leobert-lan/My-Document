package com.lht.creationspace.cfg;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.keys
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> DBConfig
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/16.
 */
public interface DBConfig {
    interface BasicDb {
        String DB_NAME = "basic.db";
    }

    interface AuthenticateDb {
        String DB_NAME = "authenticate.db";
    }

    interface SearchHistroyDb {
        /**
         * 保存搜索相关数据的数据库
         */
        String DB_NAME = "search.db";
    }

    interface BadgeNumberDb {
        /**
         * 保存数字角标的
         */
        String DB_NAME = "badge_number.db";
    }

}
