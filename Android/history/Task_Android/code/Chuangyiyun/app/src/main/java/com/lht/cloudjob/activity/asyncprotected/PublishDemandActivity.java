package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.customview.AutoArrangeCardLayout;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.ImageWithDeleteView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.presenter.FeedbackActivityPresenter;
import com.lht.cloudjob.mvp.presenter.PublishDemandActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IPublishDemandActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PublishDemandActivity extends AsyncProtectedActivity implements IPublishDemandActivity, View.OnClickListener {

    private static final String PAGENAME = "PublishDemandActivity";
    private static final int DEMAND_MAX_DESCRIBE_LENGTH = 100;
    private ProgressBar progressBar;
    private TitleBar titleBar;
    private PublishDemandActivityPresenter presenter;

    /**
     * 需求描述
     */
    private EditText etDemandDescribe;


    /**
     * 需求预算
     */
    private EditText etDemandBudget;

    private EditText etContact;

    private BasicInfoResBean basicInfoResBean;

    private TextView tvSelectPicCount;

    /**
     * 提交
     */
    private Button btnPulishSubmit;
    private TextView tvCurrentLength;
    private AutoArrangeCardLayout autoArrangeCardLayout;

    private ImageButton addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_demand);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        etDemandDescribe = (EditText) findViewById(R.id.et_demand_describe);
        tvCurrentLength = (TextView) findViewById(R.id.tv_describe_current);
        etDemandBudget = (EditText) findViewById(R.id.et_demand_budget);
        etContact = (EditText) findViewById(R.id.et_contact_number);
        btnPulishSubmit = (Button) findViewById(R.id.btn_publish_submit);
        autoArrangeCardLayout = (AutoArrangeCardLayout) findViewById(R.id.demandpublish_choose_images);

        View temp = getLayoutInflater().inflate(R.layout.view_addpic_withcount, null);

        addImage = (ImageButton) temp.findViewById(R.id.ibtn_add_attachment);
        tvSelectPicCount = (TextView) temp.findViewById(R.id.tv_attachment_count);

        autoArrangeCardLayout.addAtLast(temp);

        //提交
        btnPulishSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demandSubmit();
            }
        });
    }

    @Override
    protected void initVariable() {
        presenter = new PublishDemandActivityPresenter(this);
        basicInfoResBean = IVerifyHolder.mLoginInfo.getBasicUserInfo();
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.v1042_publishdemand_title_text_publish_demand);
        updateCurrentPicCount(0, 5);

        if (!StringUtil.isEmpty(basicInfoResBean.getMobile())) {
            etContact.setText(basicInfoResBean.getMobile());
        }

        //字数限制
        presenter.watchText(etDemandDescribe, PublishDemandActivity.DEMAND_MAX_DESCRIBE_LENGTH);

        addImage.setOnClickListener(this);
    }

    /**
     * 发布需求-- 提交
     */
    private void demandSubmit() {
        presenter.callPublish(etDemandDescribe.getText().toString(),
                etDemandBudget.getText().toString(),
                etContact.getText().toString());
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected String getPageName() {
        return PublishDemandActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return PublishDemandActivity.this;
    }

    @Override
    public void notifyOverLength() {
        ToastUtils.show(getActivity(),
                getString(R.string.v1010_toast_warm_enter_text_overlength), ToastUtils.Duration.s);
    }

    @Override
    public void updateCurrentLength(int currentCount) {
        tvCurrentLength.setText(String.format(Locale.ENGLISH, "%d/%d", currentCount, DEMAND_MAX_DESCRIBE_LENGTH));
    }

    @Override
    public void showPicSelectActionsheet(String[] data, OnActionSheetItemClickListener listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.transparent();
        actionSheet.show();
    }

    @Override
    public void updateCurrentPicCount(int current, int max) {
        if (current < max)
            autoArrangeCardLayout.onNotFull();
        else
            autoArrangeCardLayout.onFull();
        tvSelectPicCount.setText(String.format(Locale.ENGLISH, "%d/%d", current, max));
    }

    @Override
    public void addFeedbackImage(String path) {
        DLog.d(Lmsg.class, new DLog.LogLocation(), "add:path" + path);
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

        autoArrangeCardLayout.addBeforeLastIfNecessary(imageWithDeleteView);

        Picasso.with(this).load(new File(path)).error(R.drawable.v1011_drawable_work_error)
                .placeholder(R.drawable.v1011_drawable_work_default).fit()
                .into(imageWithDeleteView.getActualImageView());


    }

    @Override
    public void updateSelectedPic(List<LocalMedia> media, Set<LocalMedia> removed) {
        for (LocalMedia localMedia : media) {
            addFeedbackImage(localMedia.getPath());
        }

        for (LocalMedia localMedia2 : removed) {
            autoArrangeCardLayout.removeViewByTag(localMedia2.getPath());
        }
    }

    @Override
    public void showDialog(int contentResid, int positiveResid, CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(positiveClickListener);
        dialog.show();
    }

    @Override
    public void showDialog(int titleResId, int contentResid, int positiveResid,
                           CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setTitle(titleResId);
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(positiveClickListener);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_add_attachment:
                //选择图片
                presenter.doGetPic();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ImageGetEvent event) {
        //处理照片获取并复制后的后续事件 压缩、显示...
        Log.e("lmsg", "check copy event:" + JSON.toJSONString(event));
        presenter.callResolveEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        DLog.i(getClass(), "req code:" + requestCode + "  result code:" + resultCode);

        switch (requestCode) {
            case PublishDemandActivityPresenter.INTENT_CODE_ALBUM:
                //ignore
                break;
            case PublishDemandActivityPresenter.INTENT_CODE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    DLog.d(getClass(), "capture: trans");
                    //相机 获取图片并进一步处理
                    presenter.callTransCapture();
                } else {
                    //相机 未获取图片
                    DLog.d(getClass(), "capture: fail");
                }
                break;
            default:
                break;
        }
    }
}
