package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.innerweb.UserAgreementActivity;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.TitleBarWithOp;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.RegisterResBean;
import com.lht.cloudjob.mvp.presenter.RegisterActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IRegisterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RegisterActivity extends AsyncProtectedActivity implements IRegisterActivity,
        View.OnClickListener {

    private static final String PAGENAME = "RegisterActivity";

    private TitleBarWithOp titleBarWithOp;

    private ProgressBar progressBar;

    private CheckBox cbProtocol;

    private EditText etPhone;

    private EditText etVerifyCode;

    private EditText etPwd;

    private TextView hrefProtocol;

    private Button btnVerifyGettter;

    private Button btnRegister;

    private RegisterActivityPresenter presenter;

//    @Deprecated //1.0.42
//    public static final String KEY_ISGUIDEIN = "isGuideIn";
//    @Deprecated //1.0.42
//    private boolean isGuideIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
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
    public UMengActivity getActivity() {
        return RegisterActivity.this;
    }

    @Override
    protected void initView() {
        titleBarWithOp = (TitleBarWithOp) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        cbProtocol = (CheckBox) findViewById(R.id.register_cb_protocal);

        etPhone = (EditText) findViewById(R.id.register_et_account);
        etVerifyCode = (EditText) findViewById(R.id.register_et_verifycode);
        etPwd = (EditText) findViewById(R.id.register_et_pwd);
        hrefProtocol = (TextView) findViewById(R.id.register_tv_protocol);

        btnVerifyGettter = (Button) findViewById(R.id.register_btn_getcode);
        btnRegister = (Button) findViewById(R.id.register_btn_register);
    }

    @Override
    protected void initVariable() {
        presenter = new RegisterActivityPresenter(this);
//        isGuideIn = getIntent().getBooleanExtra(KEY_ISGUIDEIN, false);
    }

    @Override
    protected void initEvent() {
        titleBarWithOp.setOpText(R.string.v1010_default_register_href_login);
        titleBarWithOp.setTitle(R.string.title_activity_register);

        presenter.watchInputLength(etPhone, 11);

        titleBarWithOp.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isGuideIn) {
//                    Intent intent = LoginIntentFactory.create(getActivity(), GuideActivity
//                            .LoginTrigger.GuideAccess);
//                    intent.putExtra(LoginActivity.KEY_START_HOME_ON_FINISH, true);
//                    startActivity(intent);
//                }
                finish();
            }
        });

        cbProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YoYo.with(Techniques.Pulse).duration(300).playOn(cbProtocol);
                presenter.setIsProtocolAgreed(isChecked);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        return etPwd.getText().toString();
    }

    @Override
    public String getPhone() {
        return etPhone.getText().toString();
    }

    @Override
    public void jump2Subscribe(RegisterResBean data) {
        Intent intent = new Intent(getActivity(), SubscribeActivity.class);
        intent.putExtra(SubscribeActivity.KEY_DATA, data.getUsername());
        intent.putExtra(SubscribeActivity.KEY_REGISTERIN, true);
        intent.putExtra(LoginActivity.TRIGGERKEY,
                getIntent().getSerializableExtra(LoginActivity.TRIGGERKEY));
        startActivity(intent);
        finish();
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
//                if (isGuideIn) {
//                    Intent intent = LoginIntentFactory.create(getActivity(), GuideActivity
//                            .LoginTrigger.GuideAccess);
//                    startActivity(intent);
//                }
                finish();
            }
        });

        loginGuideDialog.show();
    }

    public void notifyOverLength() {
        Toast.makeText(RegisterActivity.this,
                getString(R.string.v1010_default_feedback_enter_text_toolong), Toast.LENGTH_SHORT)
                .show();
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
                start(UserAgreementActivity.class);
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
//        if (isGuideIn) { //引导页进入的
//            start(HomeActivity.class);
//        }
    }
}
