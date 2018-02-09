package com.lht.cloudjob.mvp.model;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SearchHistoryModel
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
@Table("search_history")
public class SearchHistoryModel {

    @PrimaryKey(AssignType.BY_MYSELF)
    private String username;

    private String history;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
