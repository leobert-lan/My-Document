package com.lht.creationspace.module.proj.presenter;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.customview.toast.HeadUpToast;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.proj.model.ProjectCollectModel;
import com.lht.creationspace.module.proj.model.ProjectDisCollectModel;
import com.lht.creationspace.module.proj.model.QueryProjectCollectStateModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.proj.ui.IProjectDetailActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.lht.creationspace.util.string.StringUtil;

import java.io.Serializable;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.presenter </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjectDetailActivityPresenter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/13.
 */

public class ProjectDetailActivityPresenter implements IApiRequestPresenter {

    private IProjectDetailActivity iProjectDetailActivity;

    public ProjectDetailActivityPresenter(IProjectDetailActivity iProjectDetailActivity) {
        this.iProjectDetailActivity = iProjectDetailActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void queryCollectState(String targetProject, boolean needDiscollect) {
        if (!IVerifyHolder.mLoginInfo.isLogin())
            return;
        if (needDiscollect)
            iProjectDetailActivity.showWaitView(true);
        QueryProjectCollectStateModel.QueryProjectCollectStateData data =
                new QueryProjectCollectStateModel.QueryProjectCollectStateData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetProject);
        QueryProjectCollectStateModel model =
                new QueryProjectCollectStateModel(data,
                        new QueryProjectCollectStateCallback(needDiscollect));
        model.doRequest(iProjectDetailActivity.getActivity());
    }


    private String operateId;


    private String targetProject;

    public void callCollect(String targetProject) {
        this.targetProject = targetProject;
        if (!IVerifyHolder.mLoginInfo.isLogin()) {
            Intent intent = LoginIntentFactory.create(iProjectDetailActivity.getActivity(),
                    LoginTrigger.CollectProject);
            iProjectDetailActivity.getActivity().startActivity(intent);
            return;
        }
        iProjectDetailActivity.showWaitView(true);
        ProjectCollectModel.ProjectCollectData data = new ProjectCollectModel.ProjectCollectData();
        data.setUser(IVerifyHolder.mLoginInfo.getUsername());
        data.setTarget(targetProject);
        ProjectCollectModel model = new ProjectCollectModel(data, new CollectProjectCallback());
        model.doRequest(iProjectDetailActivity.getActivity());
    }

    public void disCollect(String targetProject) {
        this.targetProject = targetProject;
        if (StringUtil.isEmpty(operateId)) {
            queryCollectState(targetProject, true);
            return;
        }
        iProjectDetailActivity.showWaitView(true);
        ProjectDisCollectModel model =
                new ProjectDisCollectModel(operateId, new DisCollectProjectCallback());
        model.doRequest(iProjectDetailActivity.getActivity());
    }

    public void handleLoginSuccessEvent(AppEvent.LoginSuccessEvent event) {
        if (LoginTrigger.CollectProject.equals(event.getTrigger())) {
            callCollect(targetProject);
        }
    }

    private class QueryProjectCollectStateCallback
            implements RestfulApiModelCallback<QueryProjectCollectStateModel.ModelResBean> {

        private final boolean needDiscollect;

        public QueryProjectCollectStateCallback(boolean needDiscollect) {
            this.needDiscollect = needDiscollect;
        }

        @Override
        public void onSuccess(QueryProjectCollectStateModel.ModelResBean bean) {
            iProjectDetailActivity.cancelWaitView();
            operateId = bean.getData().getId();
            if (needDiscollect) {
                disCollect(targetProject);
            } else {
                iProjectDetailActivity.manualSetCollectState(true);
            }
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectDetailActivity.cancelWaitView();
            if (!needDiscollect)
                iProjectDetailActivity.manualSetCollectState(false);
            else
                iProjectDetailActivity.manualSetCollectState(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectDetailActivity.cancelWaitView();
            if (!needDiscollect)
                iProjectDetailActivity.manualSetCollectState(false);
            else
                iProjectDetailActivity.manualSetCollectState(true);
        }
    }


    private class CollectProjectCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(true);

            iProjectDetailActivity.showHeadUpMsg(HeadUpToast.TYPE_SUCCESS, "成功收藏");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(false);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(false);

        }
    }


    private class DisCollectProjectCallback
            implements RestfulApiModelCallback<String> {

        @Override
        public void onSuccess(String string) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(false);
            operateId = null;

            iProjectDetailActivity.showHeadUpMsg(HeadUpToast.TYPE_FAILURE, "取消收藏");
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(true);
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectDetailActivity.cancelWaitView();
            iProjectDetailActivity.manualSetCollectState(true);

        }
    }

    public enum LoginTrigger implements ITriggerCompare {
        CollectProject(1);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            if (compare == null) {
                return false;
            }
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }
}
