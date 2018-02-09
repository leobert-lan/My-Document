package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.RoundImageView;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.tab.TabManager;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.module.user.info.UserInfoCreateActivityPresenter;
import com.lht.creationspace.module.user.info.ui.IUserInfoCreateActivity;
import com.lht.creationspace.module.user.register.ui.ac.RoleChooseActivity;

/**
 * <p><b>Package</b> com.lht.vsocyy.activity.asyncprotected
 * <p><b>Project</b> vsocyy
 * <p><b>Classname</b> UserInfoCreateActivity
 * <p><b>Description</b>: 注册后完善信息的页面（maybe 登录流程也会加入）
 * <p>
 * Created by leobert on 2017/2/17.
 */
public class UserInfoCreateActivity extends AsyncProtectedActivity
        implements TabManager.OnTabSelectedListener,
        View.OnClickListener, IUserInfoCreateActivity {
    private static final String PAGENAME = "UserInfoCreateActivity";

    public static final int VALUE_SOURCE_LOGIN =
            IApiNewCollections.RoleCreateApi.VALUE_SOURCE_LOGIN;

    public static final int VALUE_SOURCE_REGISTER =
            IApiNewCollections.RoleCreateApi.VALUE_SOURCE_REGISTER;

    private ProgressBar progressBar;
    private RoundImageView rivAvatar;
    private EditText etNickname;
    private EditText etSign;
    private TextView tvSkip;
    private RadioButton rbMan;
    private RadioButton rbBaoMi;
    private RadioButton rbWoman;
    private Button btnNext;
    private ImageButton ibtnClearNickname;
    private UserInfoCreateActivityPresenter presenter;

    private boolean hasChooseRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_create);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Override
    protected String getPageName() {
        return UserInfoCreateActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return UserInfoCreateActivity.this;
    }

    @Override
    protected void initView() {
        rivAvatar = (RoundImageView) findViewById(R.id.riv_avatar);
        etNickname = (EditText) findViewById(R.id.et_create_nickname);
        etSign = (EditText) findViewById(R.id.et_create_sign);
        tvSkip = (TextView) findViewById(R.id.tv_skip);

        rbMan = (RadioButton) findViewById(R.id.rb_man);
        rbBaoMi = (RadioButton) findViewById(R.id.rb_baomi);
        rbWoman = (RadioButton) findViewById(R.id.rb_woman);

        ibtnClearNickname = (ImageButton) findViewById(R.id.ibtn_clearnickname);
        btnNext = (Button) findViewById(R.id.btn_next);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void initVariable() {
        presenter = new UserInfoCreateActivityPresenter(this);
        hasChooseRole = IVerifyHolder.mLoginInfo.isHasChooseRole();
        if (hasChooseRole) {
            btnNext.setText(R.string.v1000_default_chooserole_choose_complete);
        }
    }

    @Override
    protected void initEvent() {
        TabManager.init(this, rbMan, rbBaoMi, rbWoman);
        rbBaoMi.performClick();

        presenter.watchText(etNickname, 20);
        presenter.watchText(etSign, 30);

//        PressEffectUtils.bindDefaultPressEffect(rivAvatar);  ignore the press-feedback

        ibtnClearNickname.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        rivAvatar.setOnClickListener(this);
        etNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        etSign.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private int sex = UserInfoCreateActivityPresenter.SEX_UNSPECIFIED;

    @Override
    public void onTabSelect(CompoundButton selectedTab) {
        switch (selectedTab.getId()) {
            case R.id.rb_man:
                sex = 0;
                break;
            case R.id.rb_woman:
                sex = 1;
                break;
            case R.id.rb_baomi:
                sex = UserInfoCreateActivityPresenter.SEX_UNSPECIFIED;
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_clearnickname:
                etNickname.setText(null);
                break;
            case R.id.btn_next:
                presenter.callInfoCreate(sex, etNickname.getText().toString(),
                        etSign.getText().toString());
                break;
            case R.id.tv_skip:
                jump2RoleSelect();
                break;
            case R.id.riv_avatar:
                presenter.callSelectAvatar();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键
        CustomPopupWindow win = getLatestPopupWindow();
        if (win != null && win.isShowing())
            win.dismiss();
    }

    @Override
    public void jump2RoleSelect() {
        if (hasChooseRole) {
            finish();
            return;
        }
        RoleChooseActivity.getLauncher(this)
                .injectData(transData())
                .launch();
        finish();
    }

    private RoleChooseActivity.RoleChooseActivityData transData() {
        RoleChooseActivity.RoleChooseActivityData data = new RoleChooseActivity.RoleChooseActivityData();
        data.setSourceTag(parseSource());
        return data;
    }

    @Override
    public void showDialog(int contentResid, int positiveResid,
                           CustomPopupWindow.OnPositiveClickListener
                                   onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public void updateLocalAvatar(String url) {
//        int _e = DisplayUtils.convertDpToPx(getActivity(), 70f);
        Glide.with(getActivity()).load(url)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.v1000_drawable_avatar_default)
                .placeholder(R.drawable.v1000_drawable_avatar_default)
                .centerCrop()
                .into(rivAvatar);

    }

    private int parseSource() {
        int source = AbsActivityLauncher.parseData(getIntent(),
                UserInfoCreateActivityData.class).getSourceTag();
        boolean legal = (source == VALUE_SOURCE_LOGIN || source == VALUE_SOURCE_REGISTER);
        if (!legal) {
            source = VALUE_SOURCE_REGISTER;
        }
        return source;
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<UserInfoCreateActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, UserInfoCreateActivity.class);
        }

        @Override
        public AbsActivityLauncher<UserInfoCreateActivityData> injectData(UserInfoCreateActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class UserInfoCreateActivityData {

        private int sourceTag;

        public int getSourceTag() {
            return sourceTag;
        }

        public void setSourceTag(int sourceTag) {
            this.sourceTag = sourceTag;
        }
    }
}
