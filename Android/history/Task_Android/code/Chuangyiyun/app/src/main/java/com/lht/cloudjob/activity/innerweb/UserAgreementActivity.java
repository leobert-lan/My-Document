package com.lht.cloudjob.activity.innerweb;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.interfaces.IPublicConst;

public class UserAgreementActivity extends InnerWebActivity {

    private static final String PAGENAME = "UserAgreementActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initVariable();
        initEvent();
        load();
    }

    @Override
    protected String getPageName() {
        return UserAgreementActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return UserAgreementActivity.this;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected WebView provideWebView() {
        return null;
    }

    @Override
    protected ProgressBar provideProgressBar() {
        return null;
    }

    @Override
    protected String getUrl() {
        return IPublicConst.SIMPLIFIED_AGREEMENT;
    }

    @Override
    protected int getMyTitle() {
        return R.string.title_activity_user_agreement;
    }
}
