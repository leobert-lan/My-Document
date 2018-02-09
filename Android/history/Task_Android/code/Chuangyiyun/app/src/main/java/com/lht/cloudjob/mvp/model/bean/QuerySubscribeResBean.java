package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.util.string.StringUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> QuerySubscribeResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/19
 */
public class QuerySubscribeResBean {
//    "indus_id": "2,8",
//            "indus_name": "宣传片/广告片/纪录片,音频制作"
    private String indus_id;

    private String indus_name;

    public String getIndus_id() {
        return indus_id;
    }

    public void setIndus_id(String indus_id) {
        this.indus_id = indus_id;
    }

    public String getIndus_name() {
        return indus_name;
    }

    public void setIndus_name(String indus_name) {
        this.indus_name = indus_name;
    }

    public ArrayList<Integer> getIndustryIdList() {
        ArrayList<Integer> ret = new ArrayList<>();
        if(StringUtil.isEmpty(getIndus_id())) {
            return ret;
        }

        String[] ids = getIndus_id().split(",");
        for (String id :ids) {
            try {
                ret.add(Integer.parseInt(id));
            } catch (Exception e) {
                continue;
            }
        }
        return ret;
    }
}
