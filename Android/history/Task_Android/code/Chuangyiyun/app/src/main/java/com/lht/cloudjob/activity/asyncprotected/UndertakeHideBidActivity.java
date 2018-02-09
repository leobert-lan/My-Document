package com.lht.cloudjob.activity.asyncprotected;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

public class UndertakeHideBidActivity extends UndertakeActivity implements View.OnClickListener {

    private static final String PAGENAME = "UndertakeOpenBidActivity";
    public static final String TASK_BN = "task_bn";
    private TitleBar titleBar;
    private ProgressBar progressBar;
    private EditText etPrice;
    private EditText etTimeCycle;
    private UndertakeActivityPresenter presenter;

    private Button btnSubmit;
    private SharedPreferences mTokenPreferences = null;
    private String task_bn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undertake_hidebid);
        task_bn = getIntent().getStringExtra(TASK_BN);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return UndertakeHideBidActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return UndertakeHideBidActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        etPrice = (EditText) findViewById(R.id.hidebid_et_price);
        etTimeCycle = (EditText) findViewById(R.id.hidebid_et_timecycle);
        etDescribe = (EditText) findViewById(R.id.hidebid_et_letter_describe);

        btnSubmit = (Button) findViewById(R.id.hidebid_btn_submit);
        rbHideLetter = (RadioButton) findViewById(R.id.undertake_rb_hideletter);
        rbNotHideLetter = (RadioButton) findViewById(R.id.undertake_rb_nothideletter);
        tvHint = (TextView) findViewById(R.id.undertake_tv_hint);
        imgWork = (ImageWithDeleteView) findViewById(R.id.undertake_img_work);
        imgAddWork = (ImageView) findViewById(R.id.iv_add_accessory);
    }

    @Override
    protected void initVariable() {
        presenter = new UndertakeActivityPresenter(this);

        //乙方报价输入的价格限制
        presenter.watchTextWithSimpleWatcher(etPrice, UndertakeActivityPresenter.MAX_PRICE_LENGTH);
        //周期限制
        presenter.watchTextWithSimpleWatcher(etTimeCycle, UndertakeActivityPresenter.MAX_CYCLE_LENGTH);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hidebid_btn_submit:
                presenter.doLetterSubmit(task_bn, getLetterDescribe(), getDays(), getPrice(), isShow, false);
                break;
            default:
                break;
        }
    }

    private String getDays() {
        return etTimeCycle.getText().toString().trim();
    }

    private String getPrice() {
        return etPrice.getText().toString().trim();
    }

    public String getLetterDescribe() {
        return etDescribe.getText().toString().trim();
    }

}
