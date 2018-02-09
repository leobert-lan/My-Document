package com.lht.cloudjob.mvp.model.pojo;

import java.util.ArrayList;

public class ChildEntity {

    private String groupName;

    private ArrayList<String> childNames;

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<String> getChildNames() {
        return childNames;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setChildNames(ArrayList<String> childNames) {
        this.childNames = childNames;
    }

}
