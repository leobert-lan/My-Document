package com.lht.cloudjob.mvp.model.bean;

import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.SearchModel;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SearchResBean
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/29
 *
 * to see Model at{@link SearchModel}
 * to see API at{@link IRestfulApi.SearchTaskApi}
 */
public class SearchResBean extends TaskResBean {

    /**
     * 搜索以最小（最大也是一样）表示预设的task_cash
     */
    private double min_cash;

    public double getMin_cash() {
        return min_cash;
    }

    public void setMin_cash(double min_cash) {
        this.min_cash = min_cash;
    }

    @Override
    public void copyTo(DemandItemData data) {
        super.copyTo(data);
        data.setPrice(getMin_cash());
    }
}
