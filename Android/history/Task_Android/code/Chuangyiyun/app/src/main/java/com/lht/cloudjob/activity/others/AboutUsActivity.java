package com.lht.cloudjob.activity.others;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.innerweb.UserAgreementActivity;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.util.VersionUtil;

public class AboutUsActivity extends BaseActivity{

    private static final String PAGENAME = "AboutUsActivity";

    private TitleBar titleBar;

    private TextView txtProtocol, txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return AboutUsActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return AboutUsActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        txtProtocol = (TextView) findViewById(R.id.about_protocol);
        txtVersion = (TextView) findViewById(R.id.about_version);
    }

    @Override
    protected void initVariable() {
       protocolOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到蓝海创意云用户服务协议
                start(UserAgreementActivity.class);
            }
        };
    }

    private View.OnClickListener protocolOnClickListener;

    @Override
    protected void initEvent() {
        titleBar.setTitle(R.string.title_activity_aboutus);
        titleBar.setDefaultOnBackListener(getActivity());

        txtProtocol.setOnClickListener(protocolOnClickListener);

        txtVersion.setText(R.string.app_name);
        txtVersion.append(VersionUtil.getVersion(getActivity()));

    }

}
