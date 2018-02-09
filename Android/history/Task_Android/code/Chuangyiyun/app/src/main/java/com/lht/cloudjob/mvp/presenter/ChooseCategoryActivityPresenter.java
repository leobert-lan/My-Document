package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.customview.TypeSheet;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.viewinterface.IChooseCategoryActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.litesuits.orm.LiteOrm;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/2/8.
 */

public class ChooseCategoryActivityPresenter implements IApiRequestPresenter, TypeSheet.OnSelectedListener {

    private IChooseCategoryActivity iChooseCategoryActivity;
    private TypeSheet.SelectedItems selectedItems;
    private LRTree lrTree;

    public ChooseCategoryActivityPresenter(IChooseCategoryActivity iChooseCategoryActivity) {
        this.iChooseCategoryActivity = iChooseCategoryActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    @Override
    public void onTypeSelected(TypeSheet.SelectedItems selectedItems) {
        this.selectedItems = selectedItems;
    }

    /**
     * 获取行业分类信息
     */
    public void callGetCategoryData() {
        DbCURDModel<IndustryCategoryDBModel> model = new DbCURDModel<>(new CategoryModelDbCallback());
        model.doRequest();
    }

    private class CategoryModelDbCallback implements DbCURDModel.IDbCURD<IndustryCategoryDBModel> {

        @Override
        public IndustryCategoryDBModel doCURDRequest() {
            LiteOrm liteOrm = LiteOrm.newSingleInstance(iChooseCategoryActivity.getActivity(), DBConfig
                    .BasicDb.DB_NAME);
            ArrayList<IndustryCategoryDBModel> data = liteOrm.query(IndustryCategoryDBModel.class);
            int size = data.size();
            Log.d("lmsg", "data size:"+size+"\r\n数据：" + JSON.toJSONString(data));
            if (size == 0)
                return null;
            return data.get(size - 1);
        }

        @Override
        public void onCURDFinish(IndustryCategoryDBModel industryCategoryDBModel) {
            ArrayList<CategoryResBean> data =
                    (ArrayList<CategoryResBean>) JSON.parseArray(industryCategoryDBModel.getData(),
                            CategoryResBean.class);
            Log.e("lmsg", "数据2：" + JSON.toJSONString(data));
            if (data != null && data.size() > 0) {
                lrTree = new LRTree(data);
                iChooseCategoryActivity.updateCategoryData(lrTree);
            }
        }
    }
}
