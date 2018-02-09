package com.lht.creationspace.util.internet;

import java.io.Serializable;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.internet
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> HttpAction
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/23.
 */

public enum HttpAction implements IHttpActionDebugger {
    UNSET("unset for debugger"),

    POST("post"),

    GET("get"),

    HEAD("head"),

    DELETE("delete"),

    PUT("put"),

    PATCH("patch");

    private final String action;

    HttpAction(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return "action is:"+action;
    }

    @Override
    public boolean equals(IHttpActionDebugger compare) {
        if (compare == null) {
            return false;
        }
        boolean b1 = compare.getClass().getName().equals(getClass().getName());
        boolean b2 = compare.getAction().equals(getAction());
        return b1 & b2;
    }

    @Override
    public Serializable getSerializable() {
        return this;
    }
}
interface IHttpActionDebugger {
    String getAction();

    /**
     * 对比是否是同一个,利用序列化
     * @param compare
     * @return
     */
    boolean equals(IHttpActionDebugger compare);

    Serializable getSerializable();
}
