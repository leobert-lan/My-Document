package com.lht.creationspace.module.user.register.ui.ac;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.api.IApiNewCollections;
import com.lht.creationspace.module.user.info.RoleChoosePresenter;
import com.lht.creationspace.module.user.register.ui.IRoleChooseActivity;

/**
 *
 */
public class RoleChooseActivity extends AsyncProtectedActivity implements IRoleChooseActivity {

    private static final String PAGENAME = "RoleChooseActivity";
    private Button btnChooseComplete;
    private CheckBox role3;
    private CheckBox role4;

    private RoleChoosePresenter presenter;

    private ProgressBar progressBar;

    public static final int VALUE_SOURCE_LOGIN =
            IApiNewCollections.RoleCreateApi.VALUE_SOURCE_LOGIN;

    public static final int VALUE_SOURCE_REGISTER =
            IApiNewCollections.RoleCreateApi.VALUE_SOURCE_REGISTER;


    /**
     * if get in from login or tplogin{@link IApiNewCollections.RoleCreateApi#VALUE_SOURCE_LOGIN}
     * <p>
     * if get in from register or fastbind{@link IApiNewCollections.RoleCreateApi#VALUE_SOURCE_LOGIN}
     */
    private int source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return RoleChooseActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return RoleChooseActivity.this;
    }

    @Override
    protected void initView() {
        btnChooseComplete = (Button) findViewById(R.id.btn_choose_complete);
        role3 = (CheckBox) findViewById(R.id.cb_role_3);
        role4 = (CheckBox) findViewById(R.id.cb_role_4);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    @Override
    protected void initVariable() {
        presenter = new RoleChoosePresenter(this);
//        source = getIntent().getIntExtra(KEY_DATA, VALUE_SOURCE_REGISTER);
        source = AbsActivityLauncher.parseData(getIntent(), RoleChooseActivityData.class).getSourceTag();
        boolean legal = (source == VALUE_SOURCE_LOGIN || source == VALUE_SOURCE_REGISTER);
        if (!legal) {
            source = VALUE_SOURCE_REGISTER;
        }
        role3.setTag(3);
        role4.setTag(4);
    }

    @Override
    protected void initEvent() {
//        PressEffectUtils.bindDefaultPressEffect(btnChooseComplete);
        btnChooseComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.callBindRole(source, IVerifyHolder.mLoginInfo.getUsername());
            }
        });
        presenter.bindRoleSelectEvent(role3, role4);
    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<RoleChooseActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, RoleChooseActivity.class);
        }

        @Override
        public AbsActivityLauncher<RoleChooseActivityData> injectData(RoleChooseActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class RoleChooseActivityData {

        private int sourceTag;

        public int getSourceTag() {
            return sourceTag;
        }

        public void setSourceTag(int sourceTag) {
            this.sourceTag = sourceTag;
        }
    }
}
