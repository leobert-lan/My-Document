package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.TpRegisterActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ITpRegisterActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

public class TpRegisterActivity extends AsyncProtectedActivity implements ITpRegisterActivity {

    public static final String VERIFYINFOKEY = "verify_info";

    public static final String TRIGGERKEY = "trigger";

    private static final String PAGENAME = "TpRegisterActivity";

    /*
    * 临时性注释：
    * 该页面的唯一入口是三方登录后发现是新用户自动进入进行信息修改,
    * 但是不适合回到登陆页面了，所以需要将必要的信息都传递过来，并
    * 能够延续login trigger逻辑
    * */

    private LoginInfo verifyInfo;

    private ITriggerCompare trigger;

    private TpRegisterActivityPresenter presenter;

    private EditText etPwd;

    private EditText etCheck;

    private EditText tvUsername;

    private Button btnSubmit;

    private TitleBar titleBar;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tp_register);
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
        return TpRegisterActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return TpRegisterActivity.this;
    }

    @Override
    protected void initView() {
        tvUsername = (EditText) findViewById(R.id.tpregister_et_account);
        etPwd = (EditText) findViewById(R.id.tpregister_et_pwd);
        etCheck = (EditText) findViewById(R.id.tpregister_et_pwd2);
        btnSubmit = (Button) findViewById(R.id.tpregister_btn_register);

        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

    }

    @Override
    protected void initVariable() {
        presenter = new TpRegisterActivityPresenter(this);
        Intent intent = getIntent();
        verifyInfo = JSON.parseObject(intent.getStringExtra(VERIFYINFOKEY), LoginInfo.class);
        trigger = (ITriggerCompare) intent.getSerializableExtra(TRIGGERKEY);
    }

    @Override
    protected void initEvent() {
        tvUsername.setText(getString(R.string.v1010_default_tpregister_text_id) + verifyInfo.getUsername());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callSetPwd(verifyInfo, trigger, etPwd.getText().toString(), etCheck
                        .getText().toString());
            }
        });

        titleBar.setTitle(R.string.title_activity_tpregister);
        titleBar.setOnBackListener(new TitleBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                cancelLoginByHand();
                finish();
            }
        });
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private void cancelLoginByHand() {
        AppEvent.LoginCancelEvent event = new AppEvent.LoginCancelEvent();
        event.setTrigger(trigger);
        EventBus.getDefault().post(event);
        //deprecated 1.0.42
//        if (trigger.equals(GuideActivity.LoginTrigger.GuideAccess)) {
//            Intent intent = LoginIntentFactory.create(getActivity(),trigger);
//            intent.putExtra(LoginActivity.KEY_START_HOME_ON_FINISH,true);
//            startActivity(intent);
//        }
    }

    @Override
    public void onBackPressed() {
        cancelLoginByHand();
        super.onBackPressed();
    }

    @Override
    public Object getLoginTrigger() {
        return getIntent().getSerializableExtra(TRIGGERKEY);
    }

//    @Override
//    public void jump2MainActivity(LoginInfo info) {
//        Intent intent = new Intent(getActivity(), HomeActivity.class);
//        intent.putExtra(HomeActivity.KEY_ISLOGIN, true);
//        intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(info));
//        startActivity(intent);
//    }

    @Override
    public void jump2SubscribeActivity(LoginInfo verifyInfo) {
        IVerifyHolder.mLoginInfo.copy(verifyInfo);
        Intent intent = new Intent(getActivity(), SubscribeActivity.class);
        intent.putExtra(SubscribeActivity.KEY_REGISTERIN, true);
        intent.putExtra(SubscribeActivity.KEY_DATA, verifyInfo.getUsername());
        intent.putExtra(LoginActivity.TRIGGERKEY, (Serializable) getLoginTrigger());
        startActivity(intent);
    }
}
