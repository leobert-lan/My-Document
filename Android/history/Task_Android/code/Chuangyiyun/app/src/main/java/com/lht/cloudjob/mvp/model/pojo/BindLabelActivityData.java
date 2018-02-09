package com.lht.cloudjob.mvp.model.pojo;

import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.pojo
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BindLabelActivityData
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/30
 */
public class BindLabelActivityData {

    private String username;

    private CategoryResBean industry;

    private boolean isRegisterIn;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CategoryResBean getIndustry() {
        return industry;
    }

    public void setIndustry(CategoryResBean industry) {
        this.industry = industry;
    }

    public boolean isRegisterIn() {
        return isRegisterIn;
    }

    public void setIsRegisterIn(boolean isRegisterIn) {
        this.isRegisterIn = isRegisterIn;
    }
}
