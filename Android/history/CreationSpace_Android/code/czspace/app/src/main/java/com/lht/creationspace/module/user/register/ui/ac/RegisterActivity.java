package com.lht.creationspace.module.user.register.ui.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.PwdEditTextViewGroup;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.dialog.CustomDialog;
import com.lht.creationspace.module.user.info.ui.ac.UserInfoCreateActivity;
import com.lht.creationspace.module.user.register.RegisterActivityPresenter;
import com.lht.creationspace.module.user.register.ui.IRegisterActivity;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RegisterActivity extends AsyncProtectedActivity implements IRegisterActivity,
        View.OnClickListener {

    private static final String PAGENAME = "RegisterActivity";

    private ProgressBar progressBar;

    private CheckBox cbProtocol;

    private EditText etPhone;

    private EditText etVerifyCode;

    private TextView hrefProtocol;

    private Button btnVerifyGettter;

    private TextView btnRegister;

    private RegisterActivityPresenter presenter;

    private ImageButton btnBack;

    private PwdEditTextViewGroup pwdEditTextViewGroup;
    private ImageButton ibtnClearAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LoginInterruptedEvent event) {
        finish();
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
        return RegisterActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return RegisterActivity.this;
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        cbProtocol = (CheckBox) findViewById(R.id.register_cb_protocal);
        btnBack = (ImageButton) findViewById(R.id.regist_iv_back);

        etPhone = (EditText) findViewById(R.id.register_et_account);
        etVerifyCode = (EditText) findViewById(R.id.register_et_verifycode);
        hrefProtocol = (TextView) findViewById(R.id.register_tv_protocol);

        btnVerifyGettter = (Button) findViewById(R.id.register_btn_getcode);
        btnRegister = (TextView) findViewById(R.id.register_btn_register);

        pwdEditTextViewGroup = (PwdEditTextViewGroup) findViewById(R.id.register_rl_pwd);
        ibtnClearAccount = (ImageButton) findViewById(R.id.register_ibtn_clearaccount);
    }

    @Override
    protected void initVariable() {
        presenter = new RegisterActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        presenter.watchInputLength(etPhone, 11);

        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YoYo.with(Techniques.Pulse).duration(300).playOn(cbProtocol);
                presenter.setIsProtocolAgreed(isChecked);
            }
        });
        ibtnClearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etPhone.setText(null);
            }
        });

        btnVerifyGettter.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        hrefProtocol.setOnClickListener(this);
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void showErrorMsg(String message) {
        ToastUtils.show(this, message, ToastUtils.Duration.s);
    }

    @Override
    public void setVCInputActiveStatus(boolean isEnable) {
        etVerifyCode.setEnabled(isEnable);
    }

    @Override
    public void setVCGetterActiveStatus(boolean isEnable) {
        btnVerifyGettter.setClickable(isEnable);
    }

    @Override
    public void showCDRemaining(String formatTime) {
        btnVerifyGettter.setText(formatTime);
    }

    @Override
    public void initVCGetter(int textResId) {
        btnVerifyGettter.setText(textResId);
        btnVerifyGettter.setClickable(true);
    }

    @Override
    public String getVerifyCode() {
        return etVerifyCode.getText().toString();
    }

    @Override
    public String getPwd() {
        return pwdEditTextViewGroup.getInput();
    }

    @Override
    public String getPhone() {
        return etPhone.getText().toString();
    }

    @Override
    public void jump2UserInfoCreate() {
//        Intent intent = new Intent(getActivity(), UserInfoCreateActivity.class);
//        intent.putExtra(UserInfoCreateActivity.KEY_DATA, UserInfoCreateActivity.VALUE_SOURCE_REGISTER);
//        startActivity(intent);
        presenter.jump2RoleChooseActivty(UserInfoCreateActivity.VALUE_SOURCE_REGISTER);

    }

    @Override
    public void showLoginGuideDialog() {
        CustomDialog loginGuideDialog = new CustomDialog(this);
        loginGuideDialog.setContent(R.string.v1010_dialog_register_exist);
        loginGuideDialog.setNegativeButton(R.string.v1010_dialog_register_negative_reinput);
        loginGuideDialog.setNegativeClickListener(new CustomPopupWindow.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                etPhone.setText("");
            }
        });

        loginGuideDialog.setPositiveButton(R.string.v1010_dialog_register_positive_login);
        loginGuideDialog.setPositiveClickListener(new CustomPopupWindow.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                finish();
            }
        });

        loginGuideDialog.show();
    }

    public void notifyOverLength() {
        ToastUtils.show(getActivity(),
                getString(R.string.v1000_default_feedback_enter_text_toolong)
                , ToastUtils.Duration.s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_getcode:
                presenter.callSendSmsVerifyCode(etPhone.getText().toString());
                break;
            case R.id.register_btn_register:
                presenter.callRegister();
                break;
            case R.id.register_tv_protocol:
                presenter.jump2UserAgreement();
                break;
            default:
                break;
        }
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
}
