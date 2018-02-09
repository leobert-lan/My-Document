package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.HotSearchModel;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> HotSearchResBean
 * <p><b>Description</b>: 热门搜索接口返回数据模型，注意数组。
 * {
 * "ret": 0,
 * "status": 1,
 * "message": "成功",
 * "data": [
 * {
 * "search_key": "服务",
 * "count": "115"
 * },
 * {},
 * ...
 * ]
 * }
 * <p/>
 * Created by leobert on 2016/8/16.
 * to see API at {@link IRestfulApi.HotSearchApi}
 * to see Model at {@link HotSearchModel}
 */
public class HotSearchResBean {
    private String search_key;
    private int count;

    public String getSearch_key() {
        return search_key;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
