package com.lht.cloudjob.mvp.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.clazz.SelectableDataWrapper;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.QuerySubscribeModel;
import com.lht.cloudjob.mvp.model.SubscribeModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.bean.QuerySubscribeResBean;
import com.lht.cloudjob.mvp.model.pojo.IndustryWrapper;
import com.lht.cloudjob.mvp.viewinterface.ISubscribeActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.litesuits.orm.LiteOrm;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SubscribeActivityPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/19
 */
public class SubscribeActivityPresenter implements IApiRequestPresenter {

    private final ISubscribeActivity iSubscribeActivity;

    public SubscribeActivityPresenter(ISubscribeActivity iSubscribeActivity) {
        this.iSubscribeActivity = iSubscribeActivity;
    }

    /**
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private LRTree lrTree;

    /**
     * @param username username
     * @param need  是否需要获取，注册就不需要
     */
    public void callGetSubscribedData(String username, boolean need) {
        if (need) {
            iSubscribeActivity.showWaitView(true);
            IApiRequestModel model = new QuerySubscribeModel(username, new
                    QuerySubscribeModelCallback());
            model.doRequest(iSubscribeActivity.getActivity());
        } else {
            callGetCategoryData(null);
        }
    }

    private void callGetCategoryData(ArrayList<Integer> selected) {
        if (selected == null) {
            selected = new ArrayList<>();
        }
        if (lrTree == null) {
            iSubscribeActivity.showWaitView(true);
            DbCURDModel<IndustryCategoryDBModel> model = new DbCURDModel<>(new
                    CategoryModelDbCallback(selected));
            model.doRequest();
        } else {
            iSubscribeActivity.setListData(generateListData(selected));
        }
    }

    public void callUpdateSubscribe(String username, ArrayList selected) {
        if (selected == null || selected.isEmpty()) {

            iSubscribeActivity.showErrorMsg(iSubscribeActivity.getActivity().getString(R.string.v1010_default_subscribe_no_select_table));
            return;
        }
        iSubscribeActivity.showWaitView(true);
        IApiRequestModel model = new SubscribeModel(username, selected, new
                SubscribeModelCallback());
        model.doRequest(iSubscribeActivity.getActivity());
    }

    private ArrayList<IndustryWrapper> generateListData(ArrayList<Integer> selected) {
        ArrayList<IndustryWrapper> ret = new ArrayList<>();
        ArrayList<CategoryResBean> roots = lrTree.queryRoots();
        if (roots == null) {
            roots = new ArrayList<>();
        }
        for (CategoryResBean root : roots) {
            IndustryWrapper wrapper = new IndustryWrapper();
            wrapper.setField(root);

            //检验数据
            ArrayList<SelectableDataWrapper<CategoryResBean>> groups = new ArrayList<>();
            ArrayList<CategoryResBean> origins = lrTree.querySon(root);
            for (CategoryResBean origin : origins) {
                SelectableDataWrapper<CategoryResBean> dataWrapper = SelectableDataWrapper
                        .wrapObject(origin);
                if (selected.contains(origin.getId())) {
                    dataWrapper.setIsSelected(true);
                }
                groups.add(dataWrapper);
            }
//            wrapper.setLabels(SelectableDataWrapper.wrapList(lrTree.querySon(root)));
            wrapper.setLabels(groups);

            ret.add(wrapper);
        }

        return ret;
    }

    private class CategoryModelDbCallback implements DbCURDModel.IDbCURD<IndustryCategoryDBModel> {

        private final ArrayList<Integer> selected;

        public CategoryModelDbCallback(ArrayList<Integer> selected) {
            this.selected = selected;
        }

        @Override
        public IndustryCategoryDBModel doCURDRequest() {
            LiteOrm liteOrm = LiteOrm.newSingleInstance(iSubscribeActivity.getActivity(), DBConfig
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
                iSubscribeActivity.setListData(generateListData(selected));
            }
            iSubscribeActivity.cancelWaitView();
        }
    }

    private class QuerySubscribeModelCallback implements ApiModelCallback<QuerySubscribeResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<QuerySubscribeResBean> beanContainer) {
            iSubscribeActivity.cancelWaitView();
            callGetCategoryData(beanContainer.getData().getIndustryIdList());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSubscribeActivity.cancelWaitView();
            callGetCategoryData(null);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSubscribeActivity.cancelWaitView();
            // 查询失败直接展示全部数据
            callGetCategoryData(null);
        }
    }

    private class SubscribeModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSubscribeActivity.cancelWaitView();
            iSubscribeActivity.showMsg(iSubscribeActivity.getActivity().getString(R.string.v1010_default_subscribe_subscribe_success));

            //统计 订阅成功-计数
            iSubscribeActivity.reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_SUCCESS);

            iSubscribeActivity.finishActivity();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iSubscribeActivity.cancelWaitView();
            iSubscribeActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iSubscribeActivity.cancelWaitView();
        }
    }
}
