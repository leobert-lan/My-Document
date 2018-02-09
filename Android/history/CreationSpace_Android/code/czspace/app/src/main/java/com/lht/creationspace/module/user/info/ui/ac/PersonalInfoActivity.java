package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.RoundImageView;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.module.user.info.PersonalInfoActivityPresenter;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.module.user.info.ui.IPersonalInfoActivity;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import individual.leobert.uilib.actionsheet.ActionSheet;
import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;

public class PersonalInfoActivity extends AsyncProtectedActivity
        implements IPersonalInfoActivity, View.OnClickListener {

    private static final String PAGENAME = "PersonalInfoActivity";
    private ToolbarTheme7 mTitleBar;
    private ProgressBar mProgressBar;
    private PersonalInfoActivityPresenter presenter;
    private LinearLayout llAvatar;
    private LinearLayout llNickname;
    private LinearLayout llSex;
    private EditText etIntroduce;
    private TextView tvCurrentCount;
    private TextView tvNickname;
    private TextView tvSex;
    private RoundImageView rivAvatar;
    /**
     * 头部的昵称
     */
    private TextView tvHeadNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        EventBus.getDefault().register(this);
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
        return PersonalInfoActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return PersonalInfoActivity.this;
    }

    @Override
    protected void initView() {
        mTitleBar = (ToolbarTheme7) findViewById(R.id.titlebar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        llAvatar = (LinearLayout) findViewById(R.id.personalinfo_ll_avatar);
        llNickname = (LinearLayout) findViewById(R.id.personalinfo_ll_nickname);
        llSex = (LinearLayout) findViewById(R.id.personalinfo_ll_sex);

        tvHeadNickname = (TextView) findViewById(R.id.personalinfo_tv_headnickname);
        tvNickname = (TextView) findViewById(R.id.personalinfo_tv_nickname);
        tvSex = (TextView) findViewById(R.id.personalinfo_tv_sex);

        etIntroduce = (EditText) findViewById(R.id.personalinfo_et_introduce);
        tvCurrentCount = (TextView) findViewById(R.id.tv_current_count);
        rivAvatar = (RoundImageView) findViewById(R.id.personalinfo_img_avatar);

    }

    @Override
    protected void initVariable() {
        presenter = new PersonalInfoActivityPresenter(this);

    }

    @Override
    protected void initEvent() {
        mTitleBar.setDefaultOnBackListener(getActivity());
        mTitleBar.setTitle(R.string.title_activity_personalinfo);

        setSupportActionBar(mTitleBar);

        presenter.watchText(etIntroduce, 30);
        tvNickname.setText(StringUtil.replaceEnter(IVerifyHolder.mLoginInfo.getNickname()));
        tvHeadNickname.setText(StringUtil.replaceEnter(IVerifyHolder.mLoginInfo.getNickname()));
//        tvSex.setText(IVerifyHolder.mLoginInfo.getLoginResBean());
        llAvatar.setOnClickListener(this);
        llNickname.setOnClickListener(this);
        llSex.setOnClickListener(this);
        mTitleBar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInputPanel();
                presenter.callModifyIntroduce(etIntroduce.getText().toString());
            }
        });

        presenter.init(IVerifyHolder.mLoginInfo.getUsername());
        etIntroduce.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
    }


    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personalinfo_ll_avatar:
                presenter.callModifyAvatar();
                break;
            case R.id.personalinfo_ll_nickname:
                presenter.callModifyNickname();
                break;
            case R.id.personalinfo_ll_sex:
                presenter.callModifySex();
                break;
        }
    }

    @Override
    public void jumpToModifyNickname() {
        presenter.jump2ModifyNiclname();
    }

    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            onActionSheetDismiss();
        }
    };

    @Override
    public void showSexSelectActionsheet(String[] data,
                                         OnActionSheetItemClickListener listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.transparent();
        actionSheet.setOnDismissListener(dismissListener);
        actionSheet.show();
    }

    @Override
    public void showDialog(int contentResid, int positiveResid, CustomPopupWindow
            .OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }


    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void updateCurrentnum(int edittextId, int currentCount, int remains) {
        tvCurrentCount.setText(String.valueOf(currentCount));
    }


    @Override
    public void updateUserInfo(BasicInfoResBean data) {
        if (PersonalInfoActivityPresenter.AvatarPlaceHolder.hasModified()) {
            //修改过之后的
            loadAvatar(data.getAvatar());
        }
        tvNickname.setText(StringUtil.replaceEnter(data.getNickname()));
        tvHeadNickname.setText(StringUtil.replaceEnter(data.getNickname()));
        if (data.getSex() == 1) {
            tvSex.setText("男");
        } else if (data.getSex() == 2) {
            tvSex.setText("女");
        } else {
            tvSex.setText("保密");
        }
    }

    @Override
    public void loadAvatar(String path) {
        Glide.with(getActivity()).load(path)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.v1000_drawable_avatar_default)
                .error(R.drawable.v1000_drawable_avatar_default)
                .into(rivAvatar);
    }


    @Override
    public void setIntroCompleteEnable(boolean isEnabled) {
        ToolbarTheme7.Config config = new ToolbarTheme7.Config();
        if (isEnabled) {
            config.setEnableText(true);
            config.setText("保存");
        } else {
            config.setEnableText(false);
        }
        mTitleBar.changeOpSurfaceByConfig(config);
    }

    @Override
    public void updateUserIntroduce(String introduce) {
        etIntroduce.setText(introduce);
        tvCurrentCount.setText(String.valueOf(introduce.length()));
        etIntroduce.setSelection(introduce.length());
    }

    @Override
    public String getIntro() {
        return etIntroduce.getText().toString();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ModifyNicknameEvent event) {
        IVerifyHolder.mLoginInfo.setNickname(event.getNickname());
        IVerifyHolder.mLoginInfo.getLoginResBean().setNickname(event.getNickname());

        tvNickname.setText(StringUtil.replaceEnter(event.getNickname()));
        tvHeadNickname.setText(StringUtil.replaceEnter(event.getNickname()));
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
            return new LhtActivityLauncherIntent(context, PersonalInfoActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

}
