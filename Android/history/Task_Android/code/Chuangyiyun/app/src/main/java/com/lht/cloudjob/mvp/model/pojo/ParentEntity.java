package com.lht.cloudjob.mvp.model.pojo;

import java.util.ArrayList;

public class ParentEntity {

	private String groupName;

	private ArrayList<ChildEntity> childs;


	public String getGroupName() {
		return groupName;
	}

	public ArrayList<ChildEntity> getChilds() {
		return childs;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setChilds(ArrayList<ChildEntity> childs) {
		this.childs = childs;
	}

}
