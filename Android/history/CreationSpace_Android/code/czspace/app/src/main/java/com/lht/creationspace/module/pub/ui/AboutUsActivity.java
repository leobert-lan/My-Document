package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.util.VersionUtil;

public class AboutUsActivity extends UMengActivity {

    private static final String PAGENAME = "AboutUsActivity";

    private ToolbarTheme1 titleBar;

    private TextView viewSchema1;

    private TextView viewSchema2;

    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return AboutUsActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return AboutUsActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        viewSchema1 = (TextView) findViewById(R.id.schema1);
        viewSchema2 = (TextView) findViewById(R.id.schema2);
        tvVersion = (TextView) findViewById(R.id.version);
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {
        String version = "v" + VersionUtil.getVersion(getActivity());
        tvVersion.setText(version);
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.v1000_default_setting_text_aboutus);

        setSupportActionBar(titleBar);

        viewSchema1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump2Schema1();
            }
        });

        viewSchema2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump2Schema2();
            }
        });
    }

    private void jump2Schema1() {
        UserAgreementActivity.getLauncher(getActivity())
                .injectData(transData(IPublicConst.SIMPLIFIED_AGREEMENT))
                .launch();
    }

    private UserAgreementActivity.UserAgreementActivityData transData(String simplifiedAgreement) {
        UserAgreementActivity.UserAgreementActivityData data = new UserAgreementActivity.UserAgreementActivityData();
        data.setTitle(getString(R.string.title_activity_user_agreement));
        data.setUserAgreementUrl(simplifiedAgreement);
        return data;
    }

    private void jump2Schema2() {
//        Intent intent = new Intent(getActivity(),UserAgreementActivity.class);
//        intent.putExtra(UserAgreementActivity.KEY_DATA, IPublicConst.SIMPLIFIED_AGREEMENT_JOININ);
//        intent.putExtra(UserAgreementActivity.KEY_TITLE,"入驻申请协议");
//        startActivity(intent);
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, AboutUsActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
