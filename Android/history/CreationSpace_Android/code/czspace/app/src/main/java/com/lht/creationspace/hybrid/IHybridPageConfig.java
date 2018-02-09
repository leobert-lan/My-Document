package com.lht.creationspace.hybrid;

import android.content.Context;
import android.content.SharedPreferences;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.net
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IHybridPageConfig
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/16.
 */

public interface IHybridPageConfig {

    String getPageUrl();

    String getPageUrlWithQueryString(Object... values);

    String DEV_SP = "hybrid_config";

    String SP_KEY_IP = "_ip";

    String DEFAULT_IP = "172.16.23.67";

    interface IHybridPageUrlFormat {
        String format(String path);
    }

    abstract class AbsHybridPageConfig implements IHybridPageConfig {
        final IHybridPageUrlFormat urlFormat;

        /**
         * 是否使用开发环境，注意发布一定要改为FALSE
         */
        public static boolean USE_DEV_ENV = false;

        AbsHybridPageConfig() {
            if (USE_DEV_ENV) {
                urlFormat = new DevHybridPageUrlFormat();
            } else {
                urlFormat = new HybridPageUrlFormat();
            }
        }

        protected abstract String getPagePath();

        @Override
        public String getPageUrl() {
            return urlFormat.format(getPagePath());
        }

        protected String getQueryStringFormat() {
            return null;
        }

        @Override
        public String getPageUrlWithQueryString(Object... values) {
            String _url = getPageUrl();
            if (StringUtil.isEmpty(getQueryStringFormat()))
                return _url;
            String _query = String.format(Locale.ENGLISH, getQueryStringFormat(), values);
            return _url + "?" + _query;
        }
    }

    final class DevHybridPageUrlFormat implements IHybridPageUrlFormat {
        private String URL_MODEL_DEV = "http://%s:8081/%s";

        @Override
        public String format(String path) {
            DLog.wtf(DLog.Tmsg.class, new DLog.LogLocation(), "[warning] hybrid dev is using");
            if (path.startsWith("/")) {
                throw new IllegalArgumentException("don't let the path start with /");
            }
            return String.format(Locale.ENGLISH, URL_MODEL_DEV, getIp(), path);
        }

        private String getIp() {
            SharedPreferences sp = MainApplication.getOurInstance()
                    .getSharedPreferences(DEV_SP, Context.MODE_PRIVATE);
            return sp.getString(SP_KEY_IP, DEFAULT_IP);
        }
    }

    final class HybridPageUrlFormat implements IHybridPageUrlFormat {

        String URL_MODEL = "https://m.vsochina.com/%s";

        @Override
        public String format(String path) {
            if (path.startsWith("/")) {
                throw new IllegalArgumentException("don't let the path start with /");
            }
            return String.format(Locale.ENGLISH, URL_MODEL, path);
        }
    }
}
