package com.lht.creationspace.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.domain.interactors.AbsDbInteractor;
import com.lht.creationspace.base.domain.interactors.DbInteractorFactory;
import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.customview.popup.PopupInputWin;
import com.lht.creationspace.customview.toast.HeadUpToast;
import com.lht.creationspace.db.model.DemoModel;
import com.lht.creationspace.db.repository.impl.DemoRepositoryImpl;
import com.lht.creationspace.hybrid.IHybridPageConfig;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_GeneralNavigateReqBean;
import com.lht.creationspace.hybrid.native4js.impl.VsoAcDetailNavigateImpl;
import com.lht.creationspace.module.article.ui.ArticlePublishActivity;
import com.lht.creationspace.module.article.ui.HybridArticleDetailActivity;
import com.lht.creationspace.module.msg.ui.MessageActivity;
import com.lht.creationspace.module.proj.ui.HybridProjectDetailActivity;
import com.lht.creationspace.module.proj.ui.ProjChapterPublishActivity;
import com.lht.creationspace.module.proj.ui.ProjectPublishActivity;
import com.lht.creationspace.module.pub.SplashActivityPresenter;
import com.lht.creationspace.module.pub.ui.SplashActivity;
import com.lht.creationspace.module.user.info.ui.ac.UserInfoCreateActivity;
import com.lht.creationspace.module.user.register.ui.ac.AccountCombineActivity;
import com.lht.creationspace.module.user.register.ui.ac.FastBindActivity;
import com.lht.creationspace.module.user.register.ui.ac.RoleChooseActivity;
import com.lht.creationspace.test.anim.TestAnimActivity;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.time.TimeUtil;
import com.lht.creationspace.util.toast.ToastUtils;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;


public class TestActivity extends AsyncProtectedActivity implements View.OnClickListener {

    private LinearLayout ll;

    private SharedPreferences sp;
    private TextView tvIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        sp = getSharedPreferences(IHybridPageConfig.DEV_SP, MODE_PRIVATE);

        tvIp = (TextView) findViewById(R.id.hybrid_ip_tv);
        String ip = sp.getString(IHybridPageConfig.SP_KEY_IP, IHybridPageConfig.DEFAULT_IP);
        updateIp(ip);

        ll = (LinearLayout) findViewById(R.id.test_ll);
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll_2);

        setOnClick2Child(ll);
        setOnClick2Child(ll2);
    }

    private void updateIp(String ip) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("是否使用本地环境:")
                .append(IHybridPageConfig.AbsHybridPageConfig.USE_DEV_ENV)
                .append("  当前本地服务IP：").append(ip);
        tvIp.setText(stringBuilder.toString());
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    protected String getPageName() {
        return "TestActivity";
    }

    @Override
    public BaseActivity getActivity() {
        return TestActivity.this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {

    }

    /**
     * @param viewGroup
     */
    private LinearLayout setOnClick2Child(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setOnClick2Child((ViewGroup) viewGroup.getChildAt(i));
            }
            //全部添加上
            viewGroup.getChildAt(i).setOnClickListener(this);
        }
        return null;
    }

    private void changeIp(String ip) {
        IHybridPageConfig.AbsHybridPageConfig.USE_DEV_ENV = true;
        updateIp(ip);
        ToastUtils.show(getActivity(), "修改为：" + ip, ToastUtils.Duration.s);
        SPUtil.modifyString(sp, IHybridPageConfig.SP_KEY_IP, ip);
    }

    private void showTestIpConfigPopup() {

        PopupInputWin inputWin = new PopupInputWin(this, new PopupInputWin.AbsInputCallback() {
            @Override
            public void onInputCancel() {

            }

            @Override
            public void onInputInterrupt() {

            }

            @Override
            public void onInputSubmit(String textInput) {
                changeIp(textInput);
            }
        });
        inputWin.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.test_headup_toast:
                testHeadUpToast();
                break;
            case R.id.hybrid_ip_submit:
                showTestIpConfigPopup();
                break;
            case R.id.hybrid_ip_67:
                changeIp("172.16.23.67");
                break;
            case R.id.hybrid_ip_68:
                changeIp("172.16.23.68");
                break;
            case R.id.hybrid_ip_77:
                changeIp("172.16.23.77");
                break;
            case R.id.hybrid_ip_215:
                changeIp("172.16.23.215");
                break;
            case R.id.test_guide:
                testGuide();
                break;
            case R.id.test_main:
                start(SplashActivity.class);
                break;
            case R.id.test_infocreate:
                start(UserInfoCreateActivity.class);
                break;
            case R.id.test_articledetail:
                start(HybridArticleDetailActivity.class);
                break;
            case R.id.test_vsoacdetail:
                VsoAcDetailNavigateImpl  navigate =new VsoAcDetailNavigateImpl(this);
                NF_GeneralNavigateReqBean bean = new NF_GeneralNavigateReqBean();
                bean.setUrl("http://172.16.23.77:8081/maker/module/actpage.html");
                bean.setTitle("写死的title");
                navigate.handler(JSON.toJSONString(bean), new CallBackFunction() {
                    @Override
                    public void onCallBack(String s) {

                    }
                });
                break;
            case R.id.test_projectdetail:
                start(HybridProjectDetailActivity.class);
                break;
            case R.id.test_login:
                intent = LoginIntentFactory.create(getActivity(), SplashActivityPresenter.LoginTrigger.BackgroundLogin);
                startActivity(intent);
                break;
            case R.id.test_contact_account:
                intent = new Intent(this, AccountCombineActivity.class);
                startActivity(intent);
                break;
            case R.id.test_publish_article:
                intent = new Intent(this, ArticlePublishActivity.class);
                startActivity(intent);
                break;
            case R.id.test_mime:
                intent = new Intent(this, RoleChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.test_message:
                intent = new Intent(this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.test_fast_bind:
                intent = new Intent(this, FastBindActivity.class);
                startActivity(intent);
                break;
            case R.id.test_project_publish:
                intent = new Intent(this, ProjectPublishActivity.class);
                startActivity(intent);
                break;
            case R.id.test_bwv:
                intent = new Intent(this, TestHybrid.class);
                startActivity(intent);
                break;
            case R.id.test_input_popup:
                PopupInputWin inputWin = new PopupInputWin(this, new PopupInputWin.AbsInputCallback() {
                    @Override
                    public void onInputCancel() {

                    }

                    @Override
                    public void onInputInterrupt() {

                    }

                    @Override
                    public void onInputSubmit(String textInput) {

                    }
                });
                inputWin.show();
                break;
//            case R.id.test_ptr:
//                break;

            case R.id.test_anim:
                start(TestAnimActivity.class);
                break;
            case R.id.test_db1:
                testDB1();
                break;
            case R.id.test_db2:
                testDB2();
                break;
            case R.id.test_ptr_web:
                start(TestPtrWebAc.class);
                break;
            case R.id.test_ptr_recycle:
//                start(TestPtrRecycle.class);
                break;
            case R.id.test_proj_update:
                start(ProjChapterPublishActivity.class);
                break;
            default:
                break;
        }
    }

    private void testGuide() {
        SharedPreferences mBasicPreferences = getSharedPreferences(SPConstants.Basic.SP_NAME, Activity
                .MODE_PRIVATE);
        SPUtil.modifyInt(mBasicPreferences, SPConstants.Basic.KEY_STARTCOUNT, 0);
        IVerifyHolder.mLoginInfo.copy(new LoginInfo());

        start(SplashActivity.class);
    }

    private BaseRepository.StringKeyDbRepository<DemoModel> demoRepository
            = new DemoRepositoryImpl();

    private DbInteractorFactory.StringKeyDbInteractorFactory<DemoModel>
            demoModelInteractorFactory
            = DbInteractorFactory.getStringKDIFInstance(demoRepository);

    private void testDB2() {
        DemoModel model = new DemoModel();
        model.setName("leo");
        model.setAge(25);
        demoModelInteractorFactory.newSaveOrUpdateInteractor(model,
                new BaseRepository.SimpleOnTaskFinishListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("lmsg", "saved leo");
                    }

                    @Override
                    public void onCanceledBeforeRun() {
                        Log.d("lmsg", "save leo:cancel before run");
                    }
                }).execute();
    }

    private AbsDbInteractor<DemoModel> queryInteractor;

    private void testDB1() {
        DemoModel model = new DemoModel();
        model.setName("leo");
        model.setAge(25);
//        model.delete(bean, new AbsDbModel.SimpleOnTaskFinishListener() {
//            @Override
//            public void onSuccess() {
//                Toast.makeText(getActivity(), "deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
       queryInteractor = demoModelInteractorFactory.newQueryByIdInteractor("leo",
                new BaseRepository.OnQueryTaskFinishListener<DemoModel>() {
                    @Override
                    public void onCanceledBeforeRun() {
                        Log.d("lmsg", "save leo:cancel before run");
                    }

                    @Override
            public void onNotExist() {
                Toast.makeText(getActivity(), "not exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExist(DemoModel result) {
                Toast.makeText(getActivity(), JSON.toJSONString(result),
                                        Toast.LENGTH_SHORT).show();
            }
        });
        queryInteractor.execute();
//        model.saveOrUpdate(bean, new AbsDbModel.SimpleOnTaskFinishListener() {
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
//                        model.queryById("leo", new AbsDbModel.OnQueryTaskFinishListener<TestDbModel.UserBean>() {
//                            @Override
//                            public void onNotExist() {
//                                Toast.makeText(getActivity(), "not exist", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onExist(TestDbModel.UserBean result) {
//                                Toast.makeText(getActivity(), JSON.toJSONString(result),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//        );
    }

    private void testDateSelect() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DLog.e(getClass(), "check date:" + String.format("%d-%d-%d", year, monthOfYear + 1,
                        dayOfMonth));
            }
        };

        DatePickerDialog dialog = TimeUtil.newDatePickerDialog(this, TimeUtil
                .getCurrentTimeInLong(), listener);
        dialog.show();
    }

    private void testHeadUpToast() {
        HeadUpToast headUpToast = new HeadUpToast(getActivity());
        headUpToast.setContent(0, "hehehehehe\r\nhehehe");
        headUpToast.show();
    }


    @Override
    public ProgressBar getProgressBar() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        if (queryInteractor != null)
            queryInteractor.cancel();
    }
}
