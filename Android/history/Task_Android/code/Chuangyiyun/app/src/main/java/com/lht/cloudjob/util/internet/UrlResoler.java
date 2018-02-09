package com.lht.cloudjob.util.internet;

import com.lht.cloudjob.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.internet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UrlResoler
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/29
 */
public class UrlResoler {
    private String url;

    private JSONObject querys;

    private static final String QUERY_SYMBOL = "?";

    private static final String AND_SYMBOL = "&";

    private static final String EQUAL_SYMBOL = "=";

    public UrlResoler(String url) {
        this.url = StringUtil.nullStrToEmpty(url);
        querys = parseQueryStringJson();
    }

    public String getQueryString() {
        if (url.contains(QUERY_SYMBOL)) {
            int start = url.indexOf(QUERY_SYMBOL);
            return url.substring(start + 1);
        } else {
            return null;
        }
    }

    public boolean containsQueryKey(String key) {
        return querys.has(key);
    }

    public Object getQueryValue(String key) throws JSONException {
        return querys.get(key);
    }

    public boolean isValueNull(String key) {
        try {
            Object value = getQueryValue(key);
            if (value instanceof String) {
                String v = (String) value;
                if (StringUtil.isEmpty(v))
                    return true;
                if (v.toLowerCase(Locale.ENGLISH).contains("null"))
                    return true;
                return false;
            } else {
                return true;
            }
        } catch (JSONException e) {
            return true;
        }
    }

    private JSONObject parseQueryStringJson() {
        JSONObject jsonObject = new JSONObject();
        String s = getQueryString();
        if (StringUtil.isEmpty(s)) {
            return jsonObject;
        } else {
            String[] items = s.split(AND_SYMBOL);
            for (String item : items) {
                if (!StringUtil.isEmpty(item)) {
                    try {
                        jsonObject.put(parseKey(item),parseValue(item));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }

            return jsonObject;
        }
    }

    private String parseKey(String s) {
        if (s.contains(EQUAL_SYMBOL)) {
            int index = s.indexOf(EQUAL_SYMBOL);
            return s.substring(0,index);
        }
        return null;
    }

    private String parseValue(String s) {
        if (s.contains(EQUAL_SYMBOL)) {
            int index = s.indexOf(EQUAL_SYMBOL);
            return s.substring(index + 1);
        }
        return null;
    }

}
