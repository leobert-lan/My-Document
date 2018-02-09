package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.info.ResetNickActivityPresenter;
import com.lht.creationspace.module.user.info.ui.IResetNicknameActivity;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.toast.ToastUtils;

public class ResetNicknameActivity extends AsyncProtectedActivity implements IResetNicknameActivity,
        View.OnClickListener {

    private EditText etNickname;

    private static final String PAGENAME = "ResetNicknameActivity";

    private ToolbarTheme1 titleBar;

    private ImageView imgDelete;

    private Button btnSubmit;

    private ResetNickActivityPresenter presenter;
    private ProgressBar progressBar;

    private String username;

    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_nickname);
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
        return ResetNicknameActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return ResetNicknameActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        etNickname = (EditText) findViewById(R.id.resetnick_et_nickname);
        imgDelete = (ImageView) findViewById(R.id.resetnick_img_delete);
        btnSubmit = (Button) findViewById(R.id.resetnick_btn_submit);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetNickActivityPresenter(this);
        username = IVerifyHolder.mLoginInfo.getUsername();
        nickname = IVerifyHolder.mLoginInfo.getNickname();
    }

    private static final int MAX_NICK_LENGTH = 20;

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_modifynickname);
        titleBar.setDefaultOnBackListener(getActivity());

        setSupportActionBar(titleBar);

        imgDelete.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etNickname.setText(StringUtil.replaceEnter(nickname));
        etNickname.setSelection(StringUtil.replaceEnter(nickname).length());
        presenter.watchEditLength(etNickname, MAX_NICK_LENGTH);

        etNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void clearInput() {
        etNickname.setText("");
    }

    @Override
    public void showErrorMsg(String msg) {
        ToastUtils.show(getActivity(), msg, ToastUtils.Duration.s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetnick_btn_submit:
                presenter.callModifyNickname(username, etNickname.getText().toString().trim());
                break;
            case R.id.resetnick_img_delete:
                clearInput();
                break;
            default:
                break;
        }
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ResetNicknameActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return this;
        }
    }
}
