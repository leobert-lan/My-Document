package com.lht.creationspace.module.user.info.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.UMengActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.creationspace.module.user.security.ui.VerifyOldPhoneActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 显示已绑定的手机页面
 */
public class ShowPhoneActivity extends UMengActivity {

    private static final String PAGENAME = "ShowPhoneActivity";
    private ToolbarTheme1 titleBar;
    private TextView tvPhoneBinded;
    private Button btnRebindPhone;
    private String bindedPhone;

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
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        tvPhoneBinded = (TextView) findViewById(R.id.showphone_tv_oldphone);
        btnRebindPhone = (Button) findViewById(R.id.showphone_btn_rebind);
    }

    @Override
    protected void initVariable() {
        try {
            bindedPhone = IVerifyHolder.mLoginInfo.getLoginResBean().getMobile();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(getString(R.string.v1010_default_bindphone_bindphone));

        setSupportActionBar(titleBar);

        tvPhoneBinded.setText(bindedPhone);

        btnRebindPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyOldPhoneActivity.getLauncher(getActivity())
                        .launch();
            }
        });
    }


    @Subscribe
    public void onEventMainThread(AppEvent.PhoneBindEvent event) {
        finishWithoutOverrideAnim();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
            return new LhtActivityLauncherIntent(context, ShowPhoneActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return this;
        }
    }

}
