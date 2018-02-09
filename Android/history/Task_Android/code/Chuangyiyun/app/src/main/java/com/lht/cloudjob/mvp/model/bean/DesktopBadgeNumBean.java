package com.lht.cloudjob.mvp.model.bean;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DesktopBadgeNumBean
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/7.
 */

@Table("DesktopBadgeNum")
public class DesktopBadgeNumBean {
    @PrimaryKey(AssignType.BY_MYSELF)
    private String userName;

    private int numSystemNotify;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNumSystemNotify() {
        return numSystemNotify;
    }

    public void setNumSystemNotify(int numSystemNotify) {
        this.numSystemNotify = numSystemNotify;
    }
}
