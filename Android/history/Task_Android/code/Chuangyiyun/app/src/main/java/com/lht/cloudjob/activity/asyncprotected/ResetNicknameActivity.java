package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.presenter.ResetNickActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IResetNicknameActivity;

public class ResetNicknameActivity extends AsyncProtectedActivity implements IResetNicknameActivity,
        View.OnClickListener {

    private EditText etNickname;

    private static final String PAGENAME = "ResetNicknameActivity";

    private TitleBar titleBar;

    private ImageView imgDelete;

    private Button btnSubmit;

    private ResetNickActivityPresenter presenter;
    private ProgressBar progressBar;

    public static final String KEY_DATA = "data_username";

    public static final String KEY_DATA2 = "data_nickname";

    private String username;

    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_nickname);
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
    public UMengActivity getActivity() {
        return ResetNicknameActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        etNickname = (EditText) findViewById(R.id.resetnick_et_nickname);
        imgDelete = (ImageView) findViewById(R.id.resetnick_img_delete);
        btnSubmit = (Button) findViewById(R.id.resetnick_btn_submit);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void initVariable() {
        presenter = new ResetNickActivityPresenter(this);
        username = getIntent().getStringExtra(KEY_DATA);
        nickname = getIntent().getStringExtra(KEY_DATA2);
    }

    private static final int MAX_NICK_LENGTH = 20;

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_resetnick);
        titleBar.setDefaultOnBackListener(getActivity());

        imgDelete.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        etNickname.setText(nickname);

        presenter.watchEditLength(etNickname,MAX_NICK_LENGTH);
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resetnick_btn_submit:
                presenter.callModifyNickname(username,etNickname.getText().toString());
                break;
            case R.id.resetnick_img_delete:
                clearInput();
                break;
            default:
                break;
        }
    }
}
