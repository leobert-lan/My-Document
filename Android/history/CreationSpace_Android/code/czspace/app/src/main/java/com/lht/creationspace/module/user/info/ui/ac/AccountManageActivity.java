package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.security.ui.BindPhoneActivity;
import com.lht.creationspace.module.user.security.ui.ChangePwdActivity;
import com.lht.creationspace.util.string.StringUtil;

/**
 * 账号管理
 */
public class AccountManageActivity extends UMengActivity
        implements View.OnClickListener {

    private static final String PAGENAME = "AccountManageActivity";
    private ToolbarTheme1 titleBar;
    private ProgressBar progressBar;
    private LinearLayout llLoginPwd;
    private LinearLayout llPhoneBind;
    private LinearLayout llThressAccount;
//    private LinearLayout llAuthentication;
    private TextView tvPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return AccountManageActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return AccountManageActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        tvPhoneNum = (TextView) findViewById(R.id.accountmanage_tv_phonenum);

        //登录密码
        llLoginPwd = (LinearLayout) findViewById(R.id.accountmanage_ll_loginpwd);

        //手机绑定
        llPhoneBind = (LinearLayout) findViewById(R.id.accountmanage_ll_phonebind);

        //三方账号
        llThressAccount = (LinearLayout) findViewById(R.id.accountmanage_ll_threeaccount);

        //实名认证
//        llAuthentication = (LinearLayout) findViewById(R.id.accountmanage_ll_authentication);

    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        titleBar.setTitle(getString(R.string.v1000_title_activity_accountmanage));
        titleBar.setDefaultOnBackListener(this);

        setSupportActionBar(titleBar);
        initMobile();


        llLoginPwd.setOnClickListener(this);
        llPhoneBind.setOnClickListener(this);
        llThressAccount.setOnClickListener(this);
    }

    private void initMobile() {
        String _m = IVerifyHolder.mLoginInfo.getLoginResBean().getMobile();
        if (StringUtil.isEmpty(_m)) {
            tvPhoneNum.setText(null);
        } else {
            tvPhoneNum.setText(_m);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accountmanage_ll_loginpwd:
                ChangePwdActivity.getLauncher(this).launch();
                break;
            case R.id.accountmanage_ll_phonebind:
                if (StringUtil.isEmpty(IVerifyHolder.mLoginInfo.getLoginResBean().getMobile())) {
                    //bind new
                    BindPhoneActivity.BindPhoneActivityData data = new BindPhoneActivity.BindPhoneActivityData();
                    data.setUpdate(false);
                    BindPhoneActivity.getLauncher(this)
                            .injectData(data)
                            .launch();
                } else {
                    ShowPhoneActivity.getLauncher(this)
                            .launch();
                }
                break;
            case R.id.accountmanage_ll_threeaccount:
                ThirdAccountActivity.getLauncher(this).launch();
                break;

            default:
                break;
        }
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, AccountManageActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }
}
