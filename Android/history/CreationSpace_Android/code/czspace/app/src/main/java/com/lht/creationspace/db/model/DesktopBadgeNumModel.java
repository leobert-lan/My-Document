package com.lht.creationspace.db.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package:</b> com.lht.creationspace.db.model </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> DesktopBadgeNumModel </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

@Table("DesktopBadgeNum")
public class DesktopBadgeNumModel {
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
