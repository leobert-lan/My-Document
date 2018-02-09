package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.adapter.viewprovider.SubscribeGroupViewProviderImpl;
import com.lht.cloudjob.adapter.viewprovider.SubscribeItemViewProviderImpl;
import com.lht.cloudjob.clazz.SelectableDataWrapper;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.pojo.IndustryWrapper;
import com.lht.cloudjob.mvp.presenter.SubscribeActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ISubscribeActivity;

import java.util.ArrayList;

/**
 * <p><b>Description</b>: 新版订阅
 * <p/>
 * Created by leobert
 */
public class SubscribeActivity extends AsyncProtectedActivity implements ISubscribeActivity,
        ListAdapter2.ICustomizeListItem2<IndustryWrapper, SubscribeGroupViewProviderImpl
                .ViewHolder> {

    private TitleBar titleBar;
    private static final String PAGENAME = "SubscribeActivity";
    private ListAdapter2<IndustryWrapper> mAdapter;
    private ListView listField;

    //    private TextView tvSkip;
    private Button btnSubscribe;

    private ProgressBar progressBar;

    public static final String KEY_DATA = "_data_username";

    private SubscribeActivityPresenter presenter;

    public static final String KEY_REGISTERIN = "isRegisterIn";

    public static final String KEY_PERSONALINFOIN = "isPersonalInfoIn";

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        username = getIntent().getStringExtra(KEY_DATA);
        initView();
        initVariable();
        initEvent();
    }

    /**
     * desc: 获取页面名称
     *
     * @return String
     */
    @Override
    protected String getPageName() {
        return SubscribeActivity.PAGENAME;
    }

    /**
     * desc: 获取activity
     */
    @Override
    public UMengActivity getActivity() {
        return SubscribeActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        listField = (ListView) findViewById(R.id.lv_vocation_type);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnSubscribe = (Button) findViewById(R.id.subscribe_btn_submit);
    }

    @Override
    protected void initVariable() {
        presenter = new SubscribeActivityPresenter(this);
        mAdapter = new ListAdapter2<>(new ArrayList<IndustryWrapper>(), new
                SubscribeGroupViewProviderImpl
                (getLayoutInflater(), this));
        listField.setAdapter(mAdapter);

    }

    private boolean isRegisterIn;

    private boolean isPersonalInfoIn() {
        return getIntent().getBooleanExtra(KEY_PERSONALINFOIN, false);
    }

    @Override
    protected void initEvent() {
        isRegisterIn = getIntent().getBooleanExtra(KEY_REGISTERIN, false);

        titleBar.setOnBackListener(new TitleBar.ITitleBackListener() {
            @Override
            public void onTitleBackClick() {
                //统计 订阅未完成-计数
                reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_SKIP);
                if (isRegisterIn) {
                    finishActivity();
                } else {
                    finish();
                }
            }
        });
        titleBar.setTitle(R.string.title_activity_subscribe);
        presenter.callGetSubscribedData(username, !isRegisterIn);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callUpdateSubscribe(username, getSelectedIndestryId());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isRegisterIn) {
            start(HomeActivity.class, HomeActivity.KEY_DATA, JSON.toJSONString(IVerifyHolder.mLoginInfo));
        }

        //统计 订阅未完成-计数
        reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_SKIP);

        super.onBackPressed();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    /**
     * desc: 提供等待窗
     *
     * @return 一个progressBar实例
     */
    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void setListData(ArrayList<IndustryWrapper> data) {
        mAdapter.setLiData(data);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void finishActivity() {

//        个人中心进入-finish
//        index进入-finish
//        guide页-register-利用trigger - home
//        其他页-register-利用trigger - finish
        if (isPersonalInfoIn()) {
            finish();
        }
        //deprecated 1.0.42
//        else if (GuideActivity.LoginTrigger.GuideAccess.equals(getLoginTrigger())) {
//            Intent intent = new Intent(getActivity(), HomeActivity.class);
//            intent.putExtra(HomeActivity.KEY_ISLOGIN, true);
//            intent.putExtra(HomeActivity.KEY_DATA, JSON.toJSONString(IVerifyHolder.mLoginInfo));
//            startActivity(intent);
//            finish();
//        }
        else {
            finish();
        }
    }

    private Object getLoginTrigger() {
        return getIntent().getSerializableExtra(LoginActivity.TRIGGERKEY);
    }

    /**
     * desc: 为item设置各种回调，
     */
    @Override
    public void customize(int position, IndustryWrapper data, View convertView,
                          SubscribeGroupViewProviderImpl.ViewHolder viewHolder) {
        SubscribeItemViewProviderImpl groupItemViewProvider =
                new SubscribeItemViewProviderImpl(getLayoutInflater(), itemViewOnGetListener);
        ListAdapter2<SelectableDataWrapper<CategoryResBean>> groupAdapter =
                new ListAdapter2<>(data.getLabels(), groupItemViewProvider);
        viewHolder.gvLabels.setAdapter(groupAdapter);
    }

    private ListAdapter2.ICustomizeListItem2<SelectableDataWrapper<CategoryResBean>,
            SubscribeItemViewProviderImpl.ViewHolder> itemViewOnGetListener =
            new ListAdapter2.ICustomizeListItem2<SelectableDataWrapper<CategoryResBean>,
                    SubscribeItemViewProviderImpl.ViewHolder>() {


                @Override
                public void customize(int position, final
                SelectableDataWrapper<CategoryResBean> data,
                                      View convertView, SubscribeItemViewProviderImpl
                                              .ViewHolder viewHolder) {
                    viewHolder.cb.setOnCheckedChangeListener(new CompoundButton
                            .OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            data.setIsSelected(isChecked);
                        }
                    });
                }
            };

    private ArrayList<Integer> getSelectedIndestryId() {
        ArrayList<Integer> ret = new ArrayList<>();
        ArrayList<IndustryWrapper> data = mAdapter.getAll();
        for (IndustryWrapper wrapper : data) {
            ArrayList<SelectableDataWrapper<CategoryResBean>> groupData = wrapper.getLabels();

            //in group
            for (SelectableDataWrapper<CategoryResBean> item : groupData) {
                if (item.isSelected()) {
                    ret.add(item.getData().getId());
                }
            }
        }
        return ret;
    }
}
