package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.IndustryCategoryDBModel;
import com.lht.cloudjob.mvp.model.InfoModifyModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.viewinterface.IBindLabelActivity;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.litesuits.orm.LiteOrm;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BindLabelActivityPresenter
 * <p><b>Description</b>: 订阅-标签设置页面presenter
 * <p> Create by Leobert on 2016/8/30
 */
public class BindLabelActivityPresenter implements IApiRequestPresenter {
    private IBindLabelActivity iBindLabelActivity;

    public BindLabelActivityPresenter(IBindLabelActivity iBindLabelActivity) {
        this.iBindLabelActivity = iBindLabelActivity;
    }

    private LRTree lrTree;

    public void callGetCategoryData(CategoryResBean root) {
        if (lrTree == null) {
            iBindLabelActivity.showWaitView(true);
            DbCURDModel<IndustryCategoryDBModel> model = new DbCURDModel<>(new
                    CategoryModelDbCallback(root));
            model.doRequest();
        } else {
            iBindLabelActivity.setGridData(lrTree.querySon(root));
        }
    }

    /**
     * @param username username
     * @param pid1     旧版行业编号
     * @param labels   标签
     */
    public void doIndustryModify(String username, int pid1, int[] labels) {
        //未选取的、数量超过的校验
        int count = 0;
        if (labels != null) {
            count = labels.length;
        }
        if (count == 0) {
            iBindLabelActivity.showErrorMsg(iBindLabelActivity.getAppResource()
                    .getString(R.string.v1010_toast_bindlabel_errnull));
        } else if (count > 3) {
            iBindLabelActivity.showErrorMsg(iBindLabelActivity.getAppResource()
                    .getString(R.string.v1010_toast_bindlabel_maxcount));
        } else {
            iBindLabelActivity.showWaitView(true);
            InfoModifyModel infoModifyModel = new InfoModifyModel(username, new
                    ModifyInfoModelCallback());
            infoModifyModel.modifyIndustry(pid1);
            infoModifyModel.modifyLable(labels);
            infoModifyModel.doRequest(iBindLabelActivity.getActivity());
        }
    }

    private class ModifyInfoModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindLabelActivity.getActivity().finish();
            EventBus.getDefault().post(new AppEvent.LabelSetEvent());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iBindLabelActivity.cancelWaitView();
            iBindLabelActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iBindLabelActivity.cancelWaitView();

            // TODO: 2016/7/26
        }
    }

    private class CategoryModelDbCallback implements DbCURDModel.IDbCURD<IndustryCategoryDBModel> {

        private CategoryResBean root;

        public CategoryModelDbCallback(CategoryResBean root) {
            this.root = root;
        }

        @Override
        public IndustryCategoryDBModel doCURDRequest() {
            LiteOrm liteOrm = LiteOrm.newSingleInstance(iBindLabelActivity.getActivity(), DBConfig
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
                iBindLabelActivity.setGridData(lrTree.querySon(root));
            }
            iBindLabelActivity.cancelWaitView();
        }
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
}
