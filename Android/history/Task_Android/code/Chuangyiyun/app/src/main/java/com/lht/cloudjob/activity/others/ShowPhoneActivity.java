package com.lht.cloudjob.activity.others;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.VerifyPhoneActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 显示已绑定的手机页面
 */
public class ShowPhoneActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_DATA = "_data";
    private static final String PAGENAME = "ShowPhoneActivity";
    private TitleBar titleBar;
    private TextView tvPhoneBinded;
    private Button btnRebindPhone;
    private String _data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_phone);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ShowPhoneActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return ShowPhoneActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        tvPhoneBinded = (TextView) findViewById(R.id.showphone_tv_oldphone);
        btnRebindPhone = (Button) findViewById(R.id.showphone_btn_rebind);
    }

    @Override
    protected void initVariable() {
        _data = getIntent().getStringExtra(KEY_DATA);
        BasicInfoResBean basicInfoResBean = JSON.parseObject(_data, BasicInfoResBean.class);
        tvPhoneBinded.setText(basicInfoResBean.getMobile());
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(getString(R.string.v1010_default_bindphone_bindphone));

        btnRebindPhone.setOnClickListener(this);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.PhoneBindEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onClick(View v) {
        start(VerifyPhoneActivity.class, VerifyPhoneActivity.KEY_DATA, _data);
    }
}
