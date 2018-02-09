package com.lht.cloudjob.activity.asyncprotected;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.customview.ImageWithDeleteView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.presenter.UndertakeActivityPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class UndertakeRewardActivity extends UndertakeActivity implements View.OnClickListener {

    private static final String PAGENAME = "UndertakeRewardActivity";
    public static final String TASK_BN = "task_bn";
    private TitleBar titleBar;
    private UndertakeActivityPresenter presenter;
    private SharedPreferences mTokenPreferences = null;
    private ProgressBar progressBar;
    private String task_bn;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undertake_reward);
        task_bn = getIntent().getStringExtra(TASK_BN);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }


    @Override
    protected String getPageName() {
        return UndertakeRewardActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return UndertakeRewardActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etDescribe = (EditText) findViewById(R.id.undertake_et_letter_describe);
        btnSubmit = (Button) findViewById(R.id.undertake_btn_submit);

        rbHideLetter = (RadioButton) findViewById(R.id.undertake_rb_hideletter);
        rbNotHideLetter = (RadioButton) findViewById(R.id.undertake_rb_nothideletter);

        tvHint = (TextView) findViewById(R.id.undertake_tv_hint);
        imgWork = (ImageWithDeleteView) findViewById(R.id.undertake_img_work);
        imgAddWork = (ImageView) findViewById(R.id.iv_add_accessory);
    }

    @Override
    protected void initVariable() {
        presenter = new UndertakeActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        titleBar.setOnBackListener(new TitleBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                //统计 未完成投稿 -计数
                reportCountEvent(IUmengEventKey.KEY_TASK_SUBMIS_INCOMPL);
                finish();
            }
        });
        titleBar.setTitle(getString(R.string.v1010_default_undertake_undertake));

        btnSubmit.setOnClickListener(this);
//        rbHideLetter.setOnCheckedChangeListener(this);
//        rbNotHideLetter.setOnCheckedChangeListener(this);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ImageGetEvent event) {
        //处理照片获取并复制后的后续事件 压缩、显示...
        presenter.callResolveEvent(event);
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
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public String getLetterDescribe() {
        return etDescribe.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undertake_btn_submit:
                presenter.doLetterSubmit(task_bn, getLetterDescribe(), isShow, false);
                break;
            default:
                break;
        }
    }

    @Override
    public SharedPreferences getTokenPreferences() {
        if (mTokenPreferences == null) {
            mTokenPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, Activity
                    .MODE_PRIVATE);
        }
        return mTokenPreferences;

    }

    @Override
    public void finishActivity() {
        finish();
    }

}
