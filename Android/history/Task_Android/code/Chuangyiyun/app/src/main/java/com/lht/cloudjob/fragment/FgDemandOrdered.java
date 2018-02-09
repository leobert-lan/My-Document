package com.lht.cloudjob.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.CommentActivity;
import com.lht.cloudjob.activity.asyncprotected.DemandInfoActivity;
import com.lht.cloudjob.activity.asyncprotected.UndertakeHideBidActivity;
import com.lht.cloudjob.activity.asyncprotected.UndertakeOpenBidActivity;
import com.lht.cloudjob.activity.asyncprotected.UndertakeRewardActivity;
import com.lht.cloudjob.activity.innerweb.SignAgreementActivity;
import com.lht.cloudjob.adapter.DMOListAdapter;
import com.lht.cloudjob.adapter.viewprovider.DemandOrderedViewProviderImpl;
import com.lht.cloudjob.adapter.viewprovider.SimpleTaskItemViewProviderImpl;
import com.lht.cloudjob.clazz.OnCheckedChangeListenerImpl;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.pojo.CommentActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.OrderedTaskFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.IHomeActivity;
import com.lht.cloudjob.mvp.viewinterface.IOrderedTaskFragment;
import com.lht.cloudjob.util.phonebasic.PhoneCallUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FgDemandOrdered extends BaseFragment implements IVerifyHolder, IOrderedTaskFragment,
        DemandOrderedViewProviderImpl.ISetCallbacksAndTrans,
        SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "FgDemandOrdered";

    private ImageView indicator1, indicator2, indicator3, indicator4, indicator5;

    private PullToRefreshListView listView;

    private MaskView maskView;

    private IHomeActivity parent;

    private OrderedTaskFragmentPresenter presenter;

    private PullToRefreshListView listRecommend;

    private View emptyHeader;

    private ImageView imgEmpty;

    private DMOListAdapter recommendAdapter;

    public FgDemandOrdered() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fg_demand_ordered, container, false);
        initView(view);
        initVariable();
        initEvent();

        return view;
    }

    /**
     * 初始化tab
     *
     * @param temp 数据源
     */
    private void init(ArrayList<RadioButton> temp) {
        HashMap<RadioButton, CompoundButton.OnCheckedChangeListener> map = new HashMap<>();
        for (RadioButton rb : temp) {
            map.put(rb, l);
        }
        View.OnClickListener listener = new OnCheckedChangeListenerImpl(map);
        for (RadioButton rb : temp) {
            rb.setOnClickListener(listener);
        }
    }

    CompoundButton.OnCheckedChangeListener l = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.dmordered_tab1:
                    freshIndicator(indicator1, isChecked);
                    getData(isChecked, IRestfulApi.OrderedTaskListApi.Type.All);
                    break;
                case R.id.dmordered_tab2:
                    freshIndicator(indicator2, isChecked);
                    getData(isChecked, IRestfulApi.OrderedTaskListApi.Type.Bidding);
                    break;
                case R.id.dmordered_tab3:
                    freshIndicator(indicator3, isChecked);
                    getData(isChecked, IRestfulApi.OrderedTaskListApi.Type.Selected);
                    break;
                case R.id.dmordered_tab4:
                    freshIndicator(indicator4, isChecked);
                    getData(isChecked, IRestfulApi.OrderedTaskListApi.Type.Deliver);
                    break;
                case R.id.dmordered_tab5:
                    freshIndicator(indicator5, isChecked);
                    getData(isChecked, IRestfulApi.OrderedTaskListApi.Type.Unevaluated);
                    break;
                default:
                    break;
            }
        }

        private void getData(boolean isChecked, IRestfulApi.OrderedTaskListApi.Type type) {
            if (isChecked) {
                //统计 通过切换tab查看相关数据
                parent.reportCountEvent(IUmengEventKey.KEY_TABSWITCH_TASKMANAGER);

                presenter.callRefreshListData(mLoginInfo.getUsername(), type);
                savedType = type;
            }
        }
    };

    /**
     * 暂存type，初始化为all
     * 下拉刷新、上拉加载的时候使用
     */
    private IRestfulApi.OrderedTaskListApi.Type savedType = IRestfulApi.OrderedTaskListApi.Type.All;

    private void freshIndicator(View v, boolean b) {
        if (b) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private RadioButton tabAll;

    @Override
    protected void initView(View view) {
        maskView = (MaskView) view.findViewById(R.id.fgdo_mask);
        tabAll = (RadioButton) view.findViewById(R.id.dmordered_tab1);
        RadioButton view2 = (RadioButton) view.findViewById(R.id.dmordered_tab2);
        RadioButton view3 = (RadioButton) view.findViewById(R.id.dmordered_tab3);
        RadioButton view4 = (RadioButton) view.findViewById(R.id.dmordered_tab4);
        RadioButton view5 = (RadioButton) view.findViewById(R.id.dmordered_tab5);

        indicator1 = (ImageView) view.findViewById(R.id.dmo_indicator1);
        indicator2 = (ImageView) view.findViewById(R.id.dmo_indicator2);
        indicator3 = (ImageView) view.findViewById(R.id.dmo_indicator3);
        indicator4 = (ImageView) view.findViewById(R.id.dmo_indicator4);
        indicator5 = (ImageView) view.findViewById(R.id.dmo_indicator5);

        ArrayList<RadioButton> temp = new ArrayList<>();
        temp.add(tabAll);
        temp.add(view2);
        temp.add(view3);
        temp.add(view4);
        temp.add(view5);

        init(temp);

        listView = (PullToRefreshListView) view.findViewById(R.id.dmo_list);

        listRecommend = (PullToRefreshListView) view.findViewById(R.id
                .emptyr_list_recommend);

        emptyHeader = getActivity().getLayoutInflater().inflate(R.layout.view_empty_listhead, null);

        imgEmpty = (ImageView) emptyHeader.findViewById(R.id.empty_icon);
        imgEmpty.setImageResource(R.drawable.v1011_drawable_mask_empty);
    }

    @Override
    protected void initVariable() {
        parent = (IHomeActivity) getActivity();
        presenter = new OrderedTaskFragmentPresenter(this);
        DemandOrderedViewProviderImpl viewProvider = new DemandOrderedViewProviderImpl
                (getActivity());
        viewProvider.setiSetCallbacksAndTrans(this);
        orderedAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(), viewProvider);

        IListItemViewProvider itemViewProvider2 = new SimpleTaskItemViewProviderImpl(getActivity
                (), this);

        recommendAdapter = new DMOListAdapter(new ArrayList<DemandItemData>(), itemViewProvider2);
    }

    private DMOListAdapter orderedAdapter;

    @Override
    protected void initEvent() {
        listView.setAdapter(orderedAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                presenter.callRefreshListData(mLoginInfo.getUsername(), savedType);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                presenter.callAddListData(mLoginInfo.getUsername(), savedType, orderedAdapter
                        .getCount());
            }
        });
        listView.setMode(PullToRefreshBase.Mode.BOTH);

        listRecommend.setMode(PullToRefreshBase.Mode.DISABLED);
        listRecommend.setOnRefreshListener(refreshListener2);

        ListView listView2 = listRecommend.getRefreshableView();
        listView2.addHeaderView(emptyHeader);
        listRecommend.getRefreshableView().setAdapter(recommendAdapter);

        tabAll.performClick();
    }

    public void updateLoginInfo(LoginInfo loginInfo) {
        mLoginInfo.copy(loginInfo);
        if (presenter != null) {
            presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
        }
        checkLoginStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        String username = mLoginInfo.getUsername();
        if (StringUtil.isEmpty(username)) {
            showAsUnlogin();
        } else {
            showAsNormal();
        }
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener2 = new PullToRefreshBase
            .OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            //先获取当前的类别，如不存在会再获取推荐
            presenter.callRefreshListData(mLoginInfo.getUsername(), savedType);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callAddRecommendTaskList(mLoginInfo.getUsername(), recommendAdapter
                    .getDefaultPagedOffset());
        }
    };

    /**
     * 显示为登录视图，未登录、登出情况下
     */
    private void showAsUnlogin() {
        if (maskView == null) {
            //视图还未创建
            return;
        }
        maskView.showAsUnlogin(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callLogin(OrderedTaskFragmentPresenter.LoginTrigger.Mask);
            }
        });
    }

    /**
     * 显示正常视图，登录状态下
     */
    private void showAsNormal() {
        if (maskView == null) {
            //视图还未创建
            return;
        }
        maskView.hide();
    }

    /**
     * desc: 主线程回调登录成功
     * 需要处理：成员对象的更新、界面的更新
     *
     * @param event 登录成功事件，包含信息
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        presenter.setLoginStatus(true);
        mLoginInfo.copy(event.getLoginInfo());
        presenter.identifyTrigger(event.getTrigger());
    }

    /**
     * desc: 未进行登录的事件订阅
     *
     * @param event 手动关闭登录页事件
     */
    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //empty
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ProtocolSignOnCompleteEvent event) {
        //we won't try data refreshing onResume, so consume this event

        //ignore 改动问题颇多，不改了。
//        presenter.callRefreshListData(getLoginInfo().getUsername(), getCurrentType());
//        orderedAdapter.scan(new IListScanOperate<DemandItemData>() {
//            @Override
//            public void onScan(int position, DemandItemData data) {
//
//            }
//        });
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    @Override
    public void finishRefresh2() {
        PullToRefreshUtil.updateLastFreshTime(getActivity(), listView);
        listRecommend.onRefreshComplete();
    }

    @Override
    public void setRecommendListItems(ArrayList<DemandItemData> liData) {
        recommendAdapter.setLiDatas(liData);
    }

    @Override
    public void addRecommendListItems(ArrayList<DemandItemData> liData) {
        recommendAdapter.addLiDatas(liData);
    }

    @Override
    public IRestfulApi.OrderedTaskListApi.Type getCurrentType() {
        return savedType;
    }

    /**
     * 重新投稿
     *
     * @param item 元数据
     */
    @Override
    public void reContribute(DemandItemData item) {
        final int MODEL_REWARD = 1; // 悬赏类型 招标为2
        Intent intent;
        String task_bn = item.getUniqueId();
        if (item.getModel() == MODEL_REWARD) { //悬赏类
            intent = new Intent(getActivity(), UndertakeRewardActivity.class);
            intent.putExtra(UndertakeRewardActivity.TASK_BN, task_bn);
        } else {
            if (item.isHideTender()) {  //暗标类
                intent = new Intent(getActivity(), UndertakeHideBidActivity.class);
                intent.putExtra(UndertakeHideBidActivity.TASK_BN, task_bn);
            } else { //明标类 需要提供初始甲方报价
                intent = new Intent(getActivity(), UndertakeOpenBidActivity.class);
                intent.putExtra(UndertakeOpenBidActivity.TASK_BN, task_bn);
                final long desiredPrice = (long) item.getPrice();//列表呈现价格即甲方报价
                intent.putExtra(UndertakeOpenBidActivity.KEY_DESIRED_PRICE, desiredPrice);
            }
        }
        startActivity(intent);
    }

    /**
     * 联系ta
     *
     * @param item 元数据
     */
    @Override
    public void callPublisher(DemandItemData item) {
        PhoneCallUtil.makePhoneCall(getActivity(), item.getPublisherMobile());
    }

    /**
     * 签署协议
     *
     * @param item 元数据
     */
    @Override
    public void signAgreement(DemandItemData item) {
        Intent intent = new Intent(getActivity(), SignAgreementActivity.class);
        intent.putExtra(SignAgreementActivity.KEY_TASK_BN, item.getUniqueId());
        intent.putExtra(SignAgreementActivity.KEY_CANREFUSE, item.getModel() == 2);
        int type;
        if (item.isReward()) {
            //悬赏
            type = SignAgreementActivity.TYPE_REWARD;
        } else {
            if (item.isHideTender()) {
                //暗标
                type = SignAgreementActivity.TYPE_HIDEBID;
            } else {
                //明标
                type = SignAgreementActivity.TYPE_OPENBID;
            }
        }
        intent.putExtra(SignAgreementActivity.KEY_TYPE, type);
        startActivity(intent);
    }

    /**
     * 跳转到评价
     *
     * @param item 元数据
     */
    @Override
    public void jump2Evaluate(DemandItemData item) {
        Intent intent = new Intent(getActivity(), CommentActivity.class);
        CommentActivityData data = new CommentActivityData();
        data.setNeedQueryInfo(true);
        data.setTask_bn(item.getUniqueId());
        data.setUsername(mLoginInfo.getUsername());
        intent.putExtra(CommentActivity.KEY_DATA, JSON.toJSONString(data));
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected String getPageName() {
        return PAGENAME;
    }


    @Override
    public void setOrderedListItems(ArrayList<DemandItemData> datas) {
        orderedAdapter.setLiDatas(datas);
        listView.onRefreshComplete();
    }

    @Override
    public void addOrderedListItems(ArrayList<DemandItemData> datas) {
        orderedAdapter.addLiDatas(datas);
        listView.onRefreshComplete();
    }

    @Override
    public void showEmptyView() {
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setVisibility(View.GONE);
        listRecommend.setVisibility(View.VISIBLE);
        listRecommend.bringToFront();
        listRecommend.setMode(PullToRefreshBase.Mode.BOTH);

        presenter.callGetRecommendTaskList(mLoginInfo.getUsername());
    }

    @Override
    public void hideEmptyView() {
        listRecommend.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.bringToFront();
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listRecommend.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    public void finishRefresh() {
        listView.onRefreshComplete();
        PullToRefreshUtil.updateLastFreshTime(getActivity(), listView);
    }

    /**
     * desc: 显示等待窗
     *
     * @param isProtectNeed 是否需要屏幕防击穿保护
     */
    @Override
    public void showWaitView(boolean isProtectNeed) {
        parent.showWaitView(isProtectNeed);
    }

    /**
     * desc: 取消等待窗
     */
    @Override
    public void cancelWaitView() {
        parent.cancelWaitView();
    }

    @Override
    public Resources getAppResource() {
        return getResources();
    }

    @Override
    public void showMsg(String msg) {
        parent.showMsg(msg);
    }

    @Override
    public void showErrorMsg(String msg) {
        this.showMsg(msg);
    }

    @Override
    public void onCallback(DemandItemView demandItemView, final DemandItemData item) {
        DemandItemView.DemandItemViewHolder viewHolder = demandItemView.getViewHolder();
        ArrayList<Integer> operates = item.getOperates();
        if (operates == null || operates.isEmpty()) {
            viewHolder.row3.setVisibility(View.GONE);
            viewHolder.line1.setVisibility(View.GONE);
            viewHolder.line2.setVisibility(View.GONE);
        } else {
            viewHolder.row3.setVisibility(View.VISIBLE);
            viewHolder.line1.setVisibility(View.VISIBLE);
            viewHolder.line2.setVisibility(View.VISIBLE);
            viewHolder.btnOp1.setVisibility(View.GONE);
            //init to gone
            viewHolder.tvState.setVisibility(View.GONE);
            viewHolder.btnOp2.setVisibility(View.GONE);

            for (Integer operate : operates) {
                translateOperate(viewHolder.tvState, viewHolder.btnOp2, operate);
            }
        }

        demandItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemandInfoActivityData _d = new DemandInfoActivityData();
                _d.setDemandId(item.getUniqueId());
                _d.setLoginInfo(mLoginInfo);
                Intent intent = new Intent(getActivity(), DemandInfoActivity.class);
                intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(_d));
                getActivity().startActivity(intent);
            }
        });

        viewHolder.btnOp2.setOnClickListener(presenter.getOnOperateListener(item));
    }

    private void translateOperate(TextView tvHint, TextView tvOp2, Integer operate) {
        switch (operate) {
            case 1: { // btn
                tvOp2.setTag(operate);
                tvOp2.setVisibility(View.VISIBLE);
                tvOp2.setText(getString(R.string.v1010_default_undertake_again));
            }
            break;
            case 2: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_undertake_wait_select_letter));
            }
            break;
            case 3: { //btn
                tvOp2.setTag(operate);
                tvOp2.setVisibility(View.VISIBLE);
                tvOp2.setText(getString(R.string.v1010_default_demandinfo_btn_call));
            }
            break;
            case 4://签署协议 btn
            {
                tvOp2.setTag(operate);
                tvOp2.setVisibility(View.VISIBLE);
                tvOp2.setText(getString(R.string.v1010_default_demandinfo_btn_sign));
            }
            break;
            case 5: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_sign_agreement_wait_other_sign));
            }
            break;
            case 6://上传源文件
            {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_upload_resouce_file));
            }
            break;
            case 7: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_wait_other_sure));
            }
            break;
            case 8://重新上传
            {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_reupload));
            }
            break;
            case 9: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_demandinfo_text_ophint9));
            }
            break;
            case 10: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_wait_other_trusteeship_money));
            }
            break;
            case 11:// 等待托管下期资金
            {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_wait_trusteeship_next_money));
            }
            break;
            case 12: { //btn
                tvOp2.setTag(operate);
                tvOp2.setVisibility(View.VISIBLE);
                tvOp2.setText(getString(R.string.v1010_default_comment_to_comment));
            }
            break;
            /////以下应该微站数据
            case 13://投稿
            {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_undertake_undertake));
            }
            break;
            case 14: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_select_bidded_letter));
            }
            break;
            case 15: {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_check_accept_letter));
            }
            break;
            case 16://去支付
            {
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText(getString(R.string.v1010_default_text_to_pay));
            }
            break;
            default:
                break;
        }

    }

    /**
     * desc: 为item设置各种回调，
     *
     * @param position
     * @param data
     * @param view
     */
    @Override
    public void setCallBack(int position, final DemandItemData data, DemandItemView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemandInfoActivityData _d = new DemandInfoActivityData();
                _d.setDemandId(data.getUniqueId());
                _d.setLoginInfo(mLoginInfo);
                Intent intent = new Intent(getActivity(), DemandInfoActivity.class);
                intent.putExtra(DemandInfoActivity.KEY_DATA, JSON.toJSONString(_d));
                getActivity().startActivity(intent);
            }
        });
    }

}
