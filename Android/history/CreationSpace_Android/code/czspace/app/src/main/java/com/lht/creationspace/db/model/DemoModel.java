package com.lht.creationspace.db.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <p><b>Package:</b> com.lht.creationspace.db.model </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> DemoModel </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */
@Table("demo")
public class DemoModel {
    @PrimaryKey(AssignType.BY_MYSELF)
    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
