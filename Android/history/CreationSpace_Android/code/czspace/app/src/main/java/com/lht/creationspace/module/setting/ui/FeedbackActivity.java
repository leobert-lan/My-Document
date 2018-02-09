package com.lht.creationspace.module.setting.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.GridImageAdapter;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.layoutmanager.FullyGridLayoutManager;
import com.lht.creationspace.module.setting.FeedbackActivityPresenter;
import com.lht.creationspace.util.toast.ToastUtils;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

public class FeedbackActivity extends AsyncProtectedActivity
        implements IFeedbackActivity {

    private static final String PAGENAME = "FeedbackActivity";

    private EditText etMobile, etContent;

    private FeedbackActivityPresenter presenter;

    private ToolbarTheme1 titleBar;

    private ProgressBar pb;

    private Button btnSubmit;

    private RecyclerView rvImages;
    private GridImageAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        initVariable();
        initEvent();
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
    public BaseActivity getActivity() {
        return FeedbackActivity.this;
    }

    @Override
    protected void initView() {
        etContent = (EditText) findViewById(R.id.feedback_et_content);
        etMobile = (EditText) findViewById(R.id.feedback_et_mobile);
        pb = (ProgressBar) findViewById(R.id.progressbar);

        rvImages = (RecyclerView) findViewById(R.id.feedback_rv_images);


        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(),
                5, GridLayoutManager.VERTICAL, false);
        rvImages.setLayoutManager(manager);

        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        btnSubmit = (Button) findViewById(R.id.feedback_btn_submit);
    }

    @Override
    protected void initVariable() {
        presenter = new FeedbackActivityPresenter(this);

        rvAdapter = new GridImageAdapter(getActivity(), new GridImageAdapter.onPicClickListener() {
            @Override
            public void onAddPicClick(int type, int position) {
                if (type == GridImageAdapter.TYPE_ADD)
                    presenter.callAddPicture(rvAdapter.getItemCount());
            }

            @Override
            public void onDelete(int position) {
                presenter.removeSelected(position);
                rvAdapter.notifyDataSetChanged();
            }
        }) {
            @Override
            protected int getViewHolderLayoutResource() {
                return R.layout.view_image_with_delete;
            }
        };

        rvAdapter.setSelectMax(presenter.MAX_IMAGE_COUNT);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_feedback);
        presenter.watchInputLength(etMobile, 11);

        setSupportActionBar(titleBar);

        rvImages.setAdapter(rvAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                String contact = etMobile.getText().toString();
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
        ToastUtils.show(FeedbackActivity.this,
                getString(R.string.v1000_default_feedback_enter_text_toolong), ToastUtils.Duration.s);
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

    @Override
    public void updateSelectedMedias(List<LocalMedia> selectedMedias) {
        rvAdapter.setList(selectedMedias);
        rvAdapter.notifyDataSetChanged();
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, FeedbackActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
