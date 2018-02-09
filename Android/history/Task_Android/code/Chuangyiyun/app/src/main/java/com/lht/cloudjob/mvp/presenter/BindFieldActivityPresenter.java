package com.lht.cloudjob.mvp.presenter;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.viewinterface.IBindFieldActivity;
import com.litesuits.orm.LiteOrm;

import java.util.ArrayList;


public class BindFieldActivityPresenter {

    private IBindFieldActivity iBindFieldActivity;

    public BindFieldActivityPresenter(IBindFieldActivity iBindFieldActivity) {
        this.iBindFieldActivity = iBindFieldActivity;
    }

    private LRTree lrTree;

    public void callGetCategoryData() {
        if (lrTree == null) {
            iBindFieldActivity.showWaitView(true);
            DbCURDModel<IndustryCategoryDBModel> model = new DbCURDModel<>(new
                    CategoryModelDbCallback());
            model.doRequest();
        } else {
            iBindFieldActivity.setListData(lrTree.queryRoots());
        }
    }

    private class CategoryModelDbCallback implements DbCURDModel.IDbCURD<IndustryCategoryDBModel> {

        @Override
        public IndustryCategoryDBModel doCURDRequest() {
            LiteOrm liteOrm = LiteOrm.newSingleInstance(iBindFieldActivity.getActivity(), DBConfig
                    .BasicDb.DB_NAME);
            ArrayList<IndustryCategoryDBModel> data = liteOrm.query(IndustryCategoryDBModel.class);
            int size = data.size();
            if (size == 0)
                return null;
            return data.get(size - 1);
        }

        @Override
        public void onCURDFinish(IndustryCategoryDBModel industryCategoryDBModel) {
            ArrayList<CategoryResBean> data =
                    (ArrayList<CategoryResBean>) JSON.parseArray(industryCategoryDBModel.getData(),
                            CategoryResBean.class);
            if (data != null && data.size() > 0) {
                lrTree = new LRTree(data);
                iBindFieldActivity.setListData(lrTree.queryRoots());
            }
            iBindFieldActivity.cancelWaitView();
        }
    }

}
