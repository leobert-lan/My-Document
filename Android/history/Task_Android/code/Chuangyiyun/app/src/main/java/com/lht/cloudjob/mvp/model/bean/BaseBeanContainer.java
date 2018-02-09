package com.lht.cloudjob.mvp.model.bean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BaseBean
 * <p><b>Description</b>: 本意是想po，vo，bean按层使用的，但鉴于人力，使用bean通用吧，如果vo有特殊需求，再
 * 创建并对象转换
 * Created by leobert on 2016/5/4.
 */
public class BaseBeanContainer<T> {
    private T data;

    public BaseBeanContainer() {}

    public BaseBeanContainer(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
