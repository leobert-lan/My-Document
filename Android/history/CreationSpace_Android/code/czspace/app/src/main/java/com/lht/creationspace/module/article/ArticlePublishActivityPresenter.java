package com.lht.creationspace.module.article;

import android.content.Context;
import android.widget.EditText;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.module.topic.ui.ChooseTopicActivity;
import com.lht.creationspace.module.topic.ui.HybridCircleDetailActivity;
import com.lht.creationspace.checkable.AbsOnAllCheckLegalListener;
import com.lht.creationspace.checkable.CheckableJobs;
import com.lht.creationspace.checkable.jobs.BlankStringCheckJob;
import com.lht.creationspace.checkable.jobs.UnCompetedInputCheckJob;
import com.lht.creationspace.structure.objpool.LocalMediaComparator;
import com.lht.creationspace.structure.objpool.LocalMediasPool;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.article.model.ArticlePublishModel;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.base.model.TextWatcherModel;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.lht.creationspace.module.article.model.pojo.NewArticleInfoResBean;
import com.lht.creationspace.module.article.ui.IArticlePublishActivity;
import com.lht.creationspace.util.internet.HttpUtil;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import thirdparty.leobert.pvselectorlib.PVSelector;
import thirdparty.leobert.pvselectorlib.model.PictureConfig;

/**
 * 文章发布-内容填写
 * Created by chhyu on 2017/3/3.
 */
public class ArticlePublishActivityPresenter implements IApiRequestPresenter {
    private IArticlePublishActivity iArticlePublishActivity;
    private final TextWatcherModel textWatcherModel;
    private final ArticlePublishModel.ArticleData articleData;

    public ArticlePublishActivityPresenter(IArticlePublishActivity
                                                   iArticlePublishActivity) {
        this.iArticlePublishActivity = iArticlePublishActivity;
        textWatcherModel = new TextWatcherModel(new TextWatcherModelCallbackImpl());
        articleData = new ArticlePublishModel.ArticleData();
        articleData.setAuthor(IVerifyHolder.mLoginInfo.getUsername());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void watchText(EditText e, int maxLength) {
        textWatcherModel.doWatcher(e, maxLength);
    }


    private List<LocalMedia> selectedMedias = new ArrayList<>();

    public void callAddPhoto() {
        PVSelector.getPhotoSelector(iArticlePublishActivity.getActivity())
                .enableCamera().multiSelect(9).useSystemCompressOnCrop(true, false)
                .setCompleteText("选好了")
                .setSelectedMedia(selectedMedias)
                .launch(new PictureConfig.OnSelectResultCallback() {
                    @Override
                    public void onSelectSuccess(List<LocalMedia> resultList) {
                        selectedMedias = resultList;
                        iArticlePublishActivity.updateSelectedImages(selectedMedias);
                    }
                });
    }

    public void removeSelected(int position) {
        selectedMedias.remove(position);
    }

    /**
     * 下一步
     */
    public void callPrepareArticlePublish(String etTitle, String etContent) {
        articleData.setTitle(etTitle);
        articleData.setContent(etContent);
        CheckableJobs.getInstance()
                .next(new UnCompetedInputCheckJob(etTitle,
                        R.string.v1000_default_articlepublish_toast_article_title_isempty,
                        iArticlePublishActivity))
                .next(new BlankStringCheckJob(etTitle,
                        R.string.v1000_default_articlepublish_toast_error_blanktitle,
                        iArticlePublishActivity))
                .next(new UnCompetedInputCheckJob(etContent,
                        R.string.v1000_default_articlepublish_toast_article_content_isempty,
                        iArticlePublishActivity))
                .next(new BlankStringCheckJob(etContent,
                        R.string.v1000_default_articlepublish_toast_error_blankcontent,
                        iArticlePublishActivity))
                .onAllCheckLegal(new AbsOnAllCheckLegalListener<ArticlePublishModel
                        .ArticleData>(articleData) {
                    @Override
                    public void onAllCheckLegal() {
                        if (selectedMedias == null || selectedMedias.isEmpty()) {
                            autoJudge(getSavedParam());
                        } else {
                            solveUploadMediaPool();
                            doUploadImages(uploadMediaPool);
                        }
                    }
                })
                .start();
    }

    private void solveUploadMediaPool() {
        if (uploadMediaPool.isEmpty()) {
            uploadMediaPool.add(selectedMedias);
        } else {
            uploadMediaPool.prepare();
            uploadMediaPool.solveRemovedItems(selectedMedias,
                    LocalMediaComparator.getInstance());
            uploadMediaPool.solveAddedItems(selectedMedias,
                    LocalMediaComparator.getInstance());
        }
    }

    /**
     * 跳转到选择圈子页面
     */
    private void autoJudge(ArticlePublishModel.ArticleData articleData) {
        if (!iArticlePublishActivity.hasCircleSet()) {
            ChooseTopicActivity.getLauncher(iArticlePublishActivity.getActivity())
                    .injectData(articleData)
                    .launch();
        } else {
            articleData.setCircleId(iArticlePublishActivity.getCircleId());
            iArticlePublishActivity.showWaitView(true);
            ArticlePublishModel model = new ArticlePublishModel(articleData,
                    new ArticlePublishModelCallback());
            model.doRequest(iArticlePublishActivity.getActivity());
        }
    }

    LocalMediasPool uploadMediaPool = new LocalMediasPool();

    private void doUploadImages(LocalMediasPool uploadMediaPool) {
        if (uploadMediaPool.hasNext()) {
            LocalMedia localMedia = uploadMediaPool.next();
            doUpload(localMedia);
        } else {
            iArticlePublishActivity.cancelWaitView();
            ArrayList<MediaCenterUploadResBean> images
                    = uploadMediaPool.getSavedItems();
            ArrayList<String> imageIds = new ArrayList<>();
            for (MediaCenterUploadResBean bean : images) {
                imageIds.add(bean.getId());
            }

            articleData.setImages(imageIds);
            autoJudge(articleData);
        }
    }

    private void doUpload(LocalMedia localMedia) {
        iArticlePublishActivity.showWaitView(true);
        MediaCenterUploadModel.MediaCenterUploadData data = new MediaCenterUploadModel.MediaCenterUploadData();
        data.setFilePath(localMedia.getCompressPath());
        data.setType(MediaCenterUploadModel.TYPE_ARTICLE_ATTACHMENT);
        MediaCenterUploadModel model =
                new MediaCenterUploadModel(data, new UploadModelCallback(localMedia));
        model.doRequest(iArticlePublishActivity.getActivity());
    }

    public void callGC() {
        uploadMediaPool.clearAll();
        uploadMediaPool = null;
    }

    private class UploadModelCallback implements
            RestfulApiModelCallback<MediaCenterUploadResBean> {
        private final LocalMedia localMedia;

        UploadModelCallback(LocalMedia localMedia) {
            this.localMedia = localMedia;
        }

        @Override
        public void onSuccess(MediaCenterUploadResBean bean) {
            uploadMediaPool.save(localMedia, bean);
            doUploadImages(uploadMediaPool);
        }

        @Override
        public void onFailure(int restCode, String msg) {

            iArticlePublishActivity.cancelWaitView();
            iArticlePublishActivity.showMsg("文件上传失败");
            iArticlePublishActivity.showDialog(R.string.v1010_dialog_feedback_content_error_upload,
                    R.string.v1010_dialog_feedback_positive_reupload,
                    new CustomPopupWindow.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClick() {
                            doUpload(localMedia);
                        }
                    });
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            // TODO: 2016/9/13
            iArticlePublishActivity.cancelWaitView();

        }
    }


    private class TextWatcherModelCallbackImpl implements TextWatcherModel.TextWatcherModelCallback {

        @Override
        public void onOverLength(int edittextId, int maxLength) {
            iArticlePublishActivity.showMsg(iArticlePublishActivity.getActivity().getString(R.string.v1000_default_articlepublish_toast_title_too_long));
        }

        @Override
        public void onChanged(int edittextId, int currentCount, int remains) {
            iArticlePublishActivity.updateCurrentTitleLength(currentCount);
        }
    }

    private class ArticlePublishModelCallback
            implements RestfulApiModelCallback<NewArticleInfoResBean> {

        @Override
        public void onSuccess(NewArticleInfoResBean bean) {
            iArticlePublishActivity.cancelWaitView();
            iArticlePublishActivity.showMsg("文章发布成功");
            EventBus.getDefault().post(new AppEvent.ArticlePublishSuccessEvent());

            HybridCircleDetailActivity.getLauncher(iArticlePublishActivity.getActivity())
                    .injectData(transData(bean))
                    .launch();
        }

        @Override
        public void onFailure(int restCode, String msg) {
            iArticlePublishActivity.cancelWaitView();
            iArticlePublishActivity.showMsg("文章发布失败");
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iArticlePublishActivity.cancelWaitView();
        }
    }

    private HybridCircleDetailActivity.CircleDetailActivityData transData(NewArticleInfoResBean bean) {
        HybridCircleDetailActivity.CircleDetailActivityData data = new HybridCircleDetailActivity.CircleDetailActivityData();
        data.setUrl(bean.getCircle_detail_url());
        data.setOid(bean.getCircle_id());
        return data;
    }
}
