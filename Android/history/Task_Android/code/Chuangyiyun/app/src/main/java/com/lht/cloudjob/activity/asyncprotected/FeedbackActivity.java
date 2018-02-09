package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.AutoArrangeCardLayout;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.ImageWithDeleteView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.FeedbackActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IFeedbackActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

public class FeedbackActivity extends AsyncProtectedActivity implements IFeedbackActivity {

    private static final String PAGENAME = "FeedbackActivity";

    private EditText etContact, etContent;

    private FeedbackActivityPresenter presenter;

    private TitleBar titleBar;

    private ImageButton addImage;

    private ProgressBar pb;

    private AutoArrangeCardLayout autoArrangeCardLayout;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return FeedbackActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return FeedbackActivity.this;
    }

    @Override
    protected void initView() {
        etContact = (EditText) findViewById(R.id.feedback_et_contact);
        etContent = (EditText) findViewById(R.id.feedback_et_content);
        pb = (ProgressBar) findViewById(R.id.progressbar);

        autoArrangeCardLayout = (AutoArrangeCardLayout) findViewById(R.id.feedback_images);
        addImage = (ImageButton) getLayoutInflater().inflate(R.layout.view_add_image, null);

        titleBar = (TitleBar) findViewById(R.id.titlebar);
        btnSubmit = (Button) findViewById(R.id.feedback_btn_submit);
    }

    @Override
    protected void initVariable() {
        presenter = new FeedbackActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_feedback);
        presenter.watchInputLength(etContact, 11);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callAddPicture(autoArrangeCardLayout.getChildCount());
            }
        });
        autoArrangeCardLayout.addAtLast(addImage);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                String contact = etContact.getText().toString();
                presenter.callSubmit(content, contact);
            }
        });
    }

    @Override
    public ProgressBar getProgressBar() {
        return pb;
    }

    @Override
    public void notifyOverLength() {
        Toast.makeText(FeedbackActivity.this,
                getString(R.string.v1010_default_feedback_enter_text_toolong), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void addFeedbackImage(String path) {
        DLog.d(getClass(), "add:path" + path);
        ImageWithDeleteView imageWithDeleteView = new ImageWithDeleteView(this);
        imageWithDeleteView.setTag(path);

        imageWithDeleteView.setOnImageDeleteListener(new ImageWithDeleteView
                .OnImageDeleteListener() {
            @Override
            public void onDelete(ImageWithDeleteView view, Object tag) {
                autoArrangeCardLayout.removeView(view);
                if (autoArrangeCardLayout.getChildrenCountWithoutAdd() < FeedbackActivityPresenter.MAX_IMAGE_COUNT) {
                    autoArrangeCardLayout.onNotFull();
                }
                presenter.callRemovePicture((String) tag);
            }
        });

        autoArrangeCardLayout.addBeforeLast(imageWithDeleteView);

        if (autoArrangeCardLayout.getChildrenCountWithoutAdd() >= FeedbackActivityPresenter.MAX_IMAGE_COUNT) {
            autoArrangeCardLayout.onFull();
        }

        Picasso.with(this).load(new File(path)).error(R.drawable.v1011_drawable_work_error)
                .placeholder(R.drawable.v1011_drawable_work_default).fit()
                .into(imageWithDeleteView.getActualImageView());


    }

    @Override
    public void showImageGetActionSheet(String[] datas, OnActionSheetItemClickListener listener,
                                        boolean showCancel) {
        ActionSheet actionSheet = new ActionSheet(getActivity());

        if (showCancel) {
            actionSheet.enableBrokenButton();
        }

        actionSheet.setDatasForDefaultAdapter(datas);
        actionSheet.setOutsideClickDismiss();
        actionSheet.transparent();
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.show();
    }

    @Override
    public void showDialog(int contentResid, int positiveResid, CustomPopupWindow
            .OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(positiveClickListener);
        dialog.show();
    }


    @Subscribe
    public void onEventMainThread(AppEvent.ImageGetEvent event) {
        //处理照片获取并复制后的后续事件 压缩、显示...
        presenter.callResolveEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FeedbackActivityPresenter.INTENT_CODE_ALBUM:
                if (resultCode == RESULT_OK) {
                    String path = FileUtils.queryImageByUri(data.getData(), getContentResolver());
                    if (!StringUtil.isEmpty(path)) {
                        presenter.callTransAlbum(path);
                    } else {
                        ToastUtils.show(getActivity(), R.string
                                .v1010_toast_error_album_private, ToastUtils.Duration.s);
                    }
                } else {
                    //相册 未获取图片
                    DLog.e(getClass(), "select: fail");
                }
                break;
            case FeedbackActivityPresenter.INTENT_CODE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    DLog.e(getClass(), "capture: trans");
                    //相机 获取图片并进一步处理
                    presenter.callTransCapture();
                } else {
                    //相机 未获取图片
                    DLog.e(getClass(), "capture: fail");
                }
                break;
            default:
                break;
        }
    }

}
