package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.CategoryModel1;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CategoryResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/4.
 * to see Model at{@link CategoryModel1}
 * to see API at {@link IRestfulApi.CategoryApi1}
 */
public class CategoryResBean {
    private int id;//": "2",
    private int root;//": "1",
    private String name;//     "name": "宣传片/广告片/纪录片",
    private int lft;//            "lft": "2",
    private int rgt;//            "rgt": "15",
    private int lvl;//            "lvl": "1",
    private String icon;//            "icon": "",
    private int old_indus_id;//            "old_indus_id": "534",
    private int listorder;//            "listorder": "1"

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLft() {
        return lft;
    }

    public void setLft(int lft) {
        this.lft = lft;
    }

    public int getRgt() {
        return rgt;
    }

    public void setRgt(int rgt) {
        this.rgt = rgt;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getOld_indus_id() {
        return old_indus_id;
    }

    public void setOld_indus_id(int old_indus_id) {
        this.old_indus_id = old_indus_id;
    }

    public int getListorder() {
        return listorder;
    }

    public void setListorder(int listorder) {
        this.listorder = listorder;
    }
}
