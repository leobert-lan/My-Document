package com.lht.creationspace.module.proj.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.lht.creationspace.R;
import com.lht.creationspace.module.proj.ui.HybridProjPubProgressActivity;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.proj.model.ProjectPublishModel;
import com.lht.creationspace.module.proj.model.pojo.ProjectPublishResBean;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.module.proj.ui.IProjectPublishActivity;
import com.lht.creationspace.util.internet.HttpUtil;

/**
 * Created by chhyu on 2017/2/20.
 */

public class ProjectPublishActivityPresenter implements IApiRequestPresenter {

    private IProjectPublishActivity iProjectPublishActivity;
    private final TextWatcherModel textWatcherModel;

    public ProjectPublishActivityPresenter(IProjectPublishActivity iProjectPublishActivity) {
        this.iProjectPublishActivity = iProjectPublishActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void watchInputLength(EditText etProjectName, int projectNameMaxLength) {
        textWatcherModel.doWatcher(etProjectName, projectNameMaxLength);
    }


    private class TextWatcherModelCallbackImpl
            implements TextWatcherModel.TextWatcherModelCallback {
        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iProjectPublishActivity.notifyProjectNameOverLength();
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {
            iProjectPublishActivity.updateCurrentLength(edittextId, currentCount, remains);
        }
    }


    public void callPublish(ProjectPublishModel.ProjectData projectData) {
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(projectData.getProjectName(),
                        R.string.v1000_default_projectpublish_toast_projectname_is_empty,
                        iProjectPublishActivity))
                .next(new UnCompetedInputCheckJob(projectData.getProvince(),
                        R.string.v1000_default_projectpublish_toast_address_is_empty,
                        iProjectPublishActivity))
                .next(new UnCompetedInputCheckJob(projectData.getQq(),
                        R.string.v1000_default_projectpublish_toast_qq_is_empty,
                        iProjectPublishActivity))
                .next(new UnCompetedInputCheckJob(projectData.getMobile(),
                        R.string.v1000_default_projectpublish_toast_mobile_is_empty,
                        iProjectPublishActivity))
                .next(new UnCompetedInputCheckJob(projectData.getPrimaryProType(), -1,
                        R.string.v1000_default_projectpublish_toast_projecttype_is_empty,
                        iProjectPublishActivity))
                .next(new UnCompetedInputCheckJob(projectData.getProjectState(),
                        R.string.v1000_default_projectpublish_toast_projectstate_is_empty,
                        iProjectPublishActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<ProjectPublishModel
                        .ProjectData>(projectData) {
                    @Override
                    public void onAllCheckLegal() {
                        doPublish(getSavedParam());
                    }
                })
                .start();

    }

    /**
     * 发布
     */
    private void doPublish(ProjectPublishModel.ProjectData data) {
        iProjectPublishActivity.showWaitView(true);
        ProjectPublishModel model = new ProjectPublishModel(data,
                new ProjectPublishModelCallback());
        model.doRequest(iProjectPublishActivity.getActivity());
    }

    private class ProjectPublishModelCallback
            implements RestfulApiModelCallback<ProjectPublishResBean> {
        @Override
        public void onSuccess(ProjectPublishResBean bean) {
            iProjectPublishActivity.cancelWaitView();
            iProjectPublishActivity.showMsg("发布项目成功");
            Intent intent = new Intent(iProjectPublishActivity.getActivity(),HybridProjPubProgressActivity.class);
            intent.putExtra(HybridProjPubProgressActivity.KEY_DATA, bean.getDetail_url());
            iProjectPublishActivity.getActivity().startActivity(intent);

            iProjectPublishActivity.getActivity().finishWithoutOverrideAnim();
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iProjectPublishActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iProjectPublishActivity.cancelWaitView();
        }
    }
}
