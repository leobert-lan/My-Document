package com.lht.creationspace.module.proj.model.pojo;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ProjectTypeResBean {
    private int id;
    private String name;
    private ArrayList<ProjectChildTypeResBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ProjectChildTypeResBean> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ProjectChildTypeResBean> children) {
        this.children = children;
    }

    public class ProjectChildTypeResBean {
        private int id;
        private String name;
        private String parent_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }
    }

    public void copy(ArrayList<ProjectTypeResBean> datas) {
        if (datas == null) {
            datas = new ArrayList<ProjectTypeResBean>();
        }
        for (int i = 0; i < datas.size(); i++) {
            ProjectTypeResBean bean = datas.get(i);
            this.setName(bean.getName());
            this.setId(bean.getId());
            this.setChildren(bean.getChildren());
        }
    }
}
