package com.lht.customwidgetlib.text;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.text
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> INumBudge
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/12.
 */

public interface INumBadge {
    void clear();

    void updateWithPointMode();

    void updateWithActualMode(int num);

    void updateWithFriendlyMode(int num,int max);

    void setBackgroundShape(Shape shape);

    /**
     * 背景形状
     */
    enum Shape {
        rectAngle, oval
    }
}
