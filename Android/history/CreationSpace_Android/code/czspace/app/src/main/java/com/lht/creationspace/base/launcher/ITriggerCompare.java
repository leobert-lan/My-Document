package com.lht.creationspace.base.launcher;

import java.io.Serializable;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ITriggerCompare
 * <p><b>Description</b>: 事件触发器对比接口，事件使用枚举定义。
 * 定义该接口是因为对象的序列化和反序列化导致无法直接使用 == operate无法得到预想的结果
 * Created by leobert on 2016/5/6.
 */
public interface ITriggerCompare {
    /**
     * 对比是否是同一个,利用序列化
     * @param compare
     * @return
     */
    boolean equals(ITriggerCompare compare);

    Object getTag();

    Serializable getSerializable();
}
