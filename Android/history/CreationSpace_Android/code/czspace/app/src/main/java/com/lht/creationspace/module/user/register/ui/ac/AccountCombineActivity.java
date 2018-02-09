package com.lht.creationspace.module.user.register.ui.ac;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.register.AccountCombineActivityPresenter;
import com.lht.creationspace.module.user.register.ui.IAccountCombineActivity;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.ui.EditTextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AccountCombineActivity extends AsyncProtectedActivity
        implements IAccountCombineActivity {
    private static final String PAGENAME = "AccountCombineActivity";

    private ProgressBar progressBar;

    private ToolbarTheme1 titleBar;

    private EditText etAccount;

    private EditText etPwd;

    private CheckBox togglePwd;

    private View viewClearAccount;

    private TextView btnCombine;

    private TextView btnRegister;

    private AccountCombineActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_combine);
        initView();
        initVariable();
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Override
    protected String getPageName() {
        return AccountCombineActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return AccountCombineActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        etAccount = (EditText) findViewById(R.id.account_combine_et_account);
        etPwd = (EditText) findViewById(R.id.account_combine_et_pwd);

        togglePwd = (CheckBox) findViewById(R.id.cb_set_pwd_display);
        viewClearAccount = findViewById(R.id.account_combine_btn_clear_account);

        btnCombine = (TextView) findViewById(R.id.account_combine_btn_combine);
        btnRegister = (TextView) findViewById(R.id.account_combine_btn_register);

    }

    @Override
    protected void initVariable() {
        presenter = new AccountCombineActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_account_combine);
        setSupportActionBar(titleBar);

        viewClearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAccount.setText(null);
            }
        });

        togglePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditTextUtils.togglePwdDisplay(isChecked, etPwd);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callFastBind();
            }
        });

        btnCombine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callAccountCombine(etAccount.getText().toString(),
                        etPwd.getText().toString());
            }
        });

        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnCombine.performClick();
                    return true;
                }
                return false;
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

    /**
     * 三方注册后台登录成功事件回调，fastbind发出，自身也会发出，但不影响
     */
    @Subscribe
    public void onEventMainThread(AppEvent.TpRegSilentLoginSuccessEvent event) {
        finishWithoutOverrideAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private SharedPreferences sharedPreferences;

    @Override
    public SharedPreferences getTokenPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, MODE_PRIVATE);
        }
        return sharedPreferences;
    }


    public static final class Launcher extends AbsActivityLauncher<TPOauthUserBean> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, AccountCombineActivity.class);
        }

        @Override
        public AbsActivityLauncher<TPOauthUserBean> injectData(TPOauthUserBean data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }
}
