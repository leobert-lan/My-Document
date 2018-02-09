package com.lht.creationspace.module.proj.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.proj.model.ProjectTypeModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.module.proj.ui.IProjectTypeChooseActivity;
import com.lht.creationspace.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/7.
 */

public class ProjectTypeChooseActivityPresenter implements IApiRequestPresenter {
    private IProjectTypeChooseActivity iProjectTypeChooseActivity;

    public ProjectTypeChooseActivityPresenter(IProjectTypeChooseActivity iProjectTypeChooseActivity) {
        this.iProjectTypeChooseActivity = iProjectTypeChooseActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    /**
     * 获取项目类型
     */
    public void getParentType() {
        iProjectTypeChooseActivity.showWaitView(true);
        ProjectTypeModel model = new ProjectTypeModel(new ProjectTypeModelCallback());
        model.doRequest(iProjectTypeChooseActivity.getActivity());
    }

    class ProjectTypeModelCallback implements RestfulApiModelCallback<ArrayList<ProjectTypeResBean>> {

        @Override
        public void onSuccess(ArrayList<ProjectTypeResBean> data) {
            iProjectTypeChooseActivity.cancelWaitView();
            Log.e("lmsg", JSON.toJSONString(data));
            iProjectTypeChooseActivity.setParentData(data);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectTypeChooseActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectTypeChooseActivity.cancelWaitView();
        }
    }
}
