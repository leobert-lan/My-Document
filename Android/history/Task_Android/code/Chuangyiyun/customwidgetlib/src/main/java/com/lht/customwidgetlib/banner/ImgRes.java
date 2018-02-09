package com.lht.customwidgetlib.banner;

/**
 * @package com.lht.customwidgetlib.banner
 * @project AndroidBase
 * @classname ImgRes
 * @description: 资源泛型类，可以是本地资源，也可以是url资源
 * Created by leobert on 2016/4/18.
 */
public class ImgRes<T> {
    private T res;

    private int defaultUrlRes;

    private boolean defaultExist = false;

    /*package*/  ImgRes(T res) {
        this.res = res;
    }

    public T getRes() {
        return res;
    }

    /*package*/  void setRes(T res) {
        this.res = res;
    }

    public int getDefaultUrlRes() {
        return defaultUrlRes;
    }

    /*package*/ void setDefaultUrlRes(int defaultUrlRes) {
        this.defaultUrlRes = defaultUrlRes;
        this.defaultExist = true;
    }

    public boolean isDefaultExist() {
        return defaultExist;
    }

}
