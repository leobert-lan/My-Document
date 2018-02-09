package com.lht.creationspace.module.proj.presenter;

import android.content.Context;

import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.proj.model.ProjectStateModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.module.proj.ui.IProjectStateChooseActivity;
import com.lht.creationspace.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/8.
 */

public class ProjectStateChooseActivityPresenter implements IApiRequestPresenter {
    private IProjectStateChooseActivity iProjectStateChooseActivity;

    public ProjectStateChooseActivityPresenter(IProjectStateChooseActivity iProjectStateChooseActivity) {
        this.iProjectStateChooseActivity = iProjectStateChooseActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callGetProjectState() {
        iProjectStateChooseActivity.showWaitView(true);
        ProjectStateModel model = new ProjectStateModel(new ProjectStateModelCallback());
        model.doRequest(iProjectStateChooseActivity.getActivity());

    }

    class ProjectStateModelCallback implements RestfulApiModelCallback<ArrayList<ProjectStateResBean>> {
        @Override
        public void onSuccess(ArrayList<ProjectStateResBean> bean) {
            iProjectStateChooseActivity.cancelWaitView();
            iProjectStateChooseActivity.updateProjectStateDate(bean);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectStateChooseActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectStateChooseActivity.cancelWaitView();
        }
    }
}
