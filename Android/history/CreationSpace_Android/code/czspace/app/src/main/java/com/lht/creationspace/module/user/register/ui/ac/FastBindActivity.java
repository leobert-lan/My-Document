package com.lht.creationspace.module.user.register.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.pub.ui.UserAgreementActivity;
import com.lht.creationspace.module.user.register.FastBindActivityPresenter;
import com.lht.creationspace.module.user.register.ui.IFastBandActivity;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.ui.EditTextUtils;

/**
 * 三方授权->不存在 快速绑定注册
 */
public class FastBindActivity extends AsyncProtectedActivity
        implements View.OnClickListener, IFastBandActivity {

    private static final String PAGENAME = "FastBindActivity";
    private EditText etPhone;
    private Button btnGetVerifyCode;
    private ProgressBar progressBar;
    private FastBindActivityPresenter presenter;
    private EditText etPwd;
    private Button btnBind;
    private EditText etVerifycode;
    private ToolbarTheme1 titleBar;
    private ImageButton ibtnClear;

    private CheckBox cbProtocol;

    private TextView hrefProtocol;

    private TPOauthUserBean oauthBean;

    private TextView btnSkip;

    private TextView tvAttention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_bind);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return FastBindActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return FastBindActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etPhone = (EditText) findViewById(R.id.fastbind_et_phone);
        btnGetVerifyCode = (Button) findViewById(R.id.fastbind_btn_getverifycode);
        etVerifycode = (EditText) findViewById(R.id.fastbind_et_verifycode);
        ibtnClear = (ImageButton) findViewById(R.id.fastbind_ibtn_clearaccount);

        etPwd = (EditText) findViewById(R.id.fastbind_et_pwd);
        btnBind = (Button) findViewById(R.id.fastbind_btn_bind);

        cbProtocol = (CheckBox) findViewById(R.id.register_cb_protocal);
        hrefProtocol = (TextView) findViewById(R.id.register_tv_protocol);
        btnSkip = (TextView) findViewById(R.id.fastbind_btn_skip);
        tvAttention = (TextView) findViewById(R.id.fastbind_tv_attention);

        btnGetVerifyCode.setOnClickListener(this);
        btnBind.setOnClickListener(this);
        ibtnClear.setOnClickListener(this);
        ((CheckBox) findViewById(R.id.cb_set_pwd_display)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EditTextUtils.togglePwdDisplay(b, etPwd);
            }
        });
    }

    @Override
    protected void initVariable() {
        presenter = new FastBindActivityPresenter(this);
//        String _data = getIntent().getStringExtra(KEY_DATA);
//        oauthBean = JSON.parseObject(_data, TPOauthUserBean.class);
        oauthBean = AbsActivityLauncher.parseData(getIntent(), TPOauthUserBean.class);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(this);
        titleBar.setTitle(R.string.v1000_title_activity_fast_bind);
        presenter.watchText(etPhone, 11);

        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YoYo.with(Techniques.Pulse).duration(300).playOn(cbProtocol);
                presenter.setIsProtocolAgreed(isChecked);
            }
        });

        hrefProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAgreementActivity.getLauncher(getActivity())
                        .injectData(transData(IPublicConst.SIMPLIFIED_AGREEMENT))
                        .launch();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callSingleRegister(oauthBean);
            }
        });

        String _attention;

        switch (oauthBean.getType()) {
            case TPOauthUserBean.TYPE_QQ:
                _attention = "注册后，您的QQ账号和手机都可以登录";
                break;

            case TPOauthUserBean.TYPE_WECHAT:
                _attention = "注册后，您的微信账号和手机都可以登录";
                break;
            case TPOauthUserBean.TYPE_SINA:
                _attention = "注册后，您的微博账号和手机都可以登录";
                break;
            default:
                _attention = "注册后，您的第三方账号和手机都可以登录";
                break;
        }
        tvAttention.setText(_attention);
    }

    private UserAgreementActivity.UserAgreementActivityData transData(String simplifiedAgreement) {
        UserAgreementActivity.UserAgreementActivityData data = new UserAgreementActivity.UserAgreementActivityData();
        data.setTitle(getString(R.string.title_activity_user_agreement));
        data.setUserAgreementUrl(simplifiedAgreement);
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resumeTimer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.cancelTimer();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fastbind_ibtn_clearaccount:
                etPhone.setText(null);
                break;
            case R.id.fastbind_btn_getverifycode:
                presenter.callGetVerifyCode(etPhone.getText().toString().trim());
                break;
            case R.id.fastbind_btn_bind:
                presenter.callFastBind(etPhone.getText().toString().trim(),
                        etPwd.getText().toString(),
                        etVerifycode.getText().toString().trim(),
                        oauthBean);
                break;
            default:
                break;
        }
    }

    @Override
    public void initVCGetter(int id) {
        btnGetVerifyCode.setText(id);
        btnGetVerifyCode.setClickable(true);
    }

    @Override
    public void showCDRemaining(String formatTime) {
        btnGetVerifyCode.setText(formatTime);
    }

    @Override
    public void setVCGetterActiveStatus(boolean isEnable) {
        btnGetVerifyCode.setClickable(isEnable);
    }

    @Override
    public void showPhoneConflictDialog(String message, String pbtn,
                                        CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setContent(message);
        dialog.setPositiveButton(pbtn);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.setNegativeButton("换号码");
        dialog.setNegativeClickListener(new CustomPopupWindow.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                etPhone.setText(null);
            }
        });
        dialog.show();
    }

    @Override
    public void jump2RoleChoose() {
        RoleChooseActivity.getLauncher(this)
                .injectData(transData(RoleChooseActivity.VALUE_SOURCE_REGISTER))
                .launch();
    }

    private RoleChooseActivity.RoleChooseActivityData transData(int valueSourceRegister) {
        RoleChooseActivity.RoleChooseActivityData data = new RoleChooseActivity.RoleChooseActivityData();
        data.setSourceTag(valueSourceRegister);
        return data;
    }


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<TPOauthUserBean> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, FastBindActivity.class);
        }

        @Override
        public AbsActivityLauncher<TPOauthUserBean> injectData(TPOauthUserBean data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }
}
