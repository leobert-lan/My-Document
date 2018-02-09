package com.lht.cloudjob.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.BannerInfoActivity;
import com.lht.cloudjob.activity.asyncprotected.DemandInfoActivity;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.activity.asyncprotected.HotTaskActivity;
import com.lht.cloudjob.activity.asyncprotected.PublishDemandActivity;
import com.lht.cloudjob.activity.asyncprotected.RichTaskActivity;
import com.lht.cloudjob.activity.asyncprotected.SubscribeActivity;
import com.lht.cloudjob.activity.asyncprotected.TaskCenterActivity;
import com.lht.cloudjob.adapter.DMOListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SimpleTaskItemViewProviderImpl;
import com.lht.cloudjob.clazz.LoginIntentFactory;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.FgIndexTitleBar;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.bean.BannerResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandInfoActivityData;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.presenter.IndexFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.IHomeActivity;
import com.lht.cloudjob.mvp.viewinterface.IIndexFragment;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.customwidgetlib.banner.AutoLooperBanner;
import com.lht.customwidgetlib.banner.IBannerUpdate;
import com.lht.customwidgetlib.banner.ImgRes;
import com.lht.customwidgetlib.text.MarqueeLayout;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FgIndex extends AvatarBarFragment implements IBannerUpdate, IIndexFragment, View
        .OnClickListener, IVerifyHolder, SimpleTaskItemViewProviderImpl.ISetCallbackForDemandItem {

    private static final String PAGENAME = "FgIndex";

    private FgIndexTitleBar titleBar;

    private PullToRefreshListView pullToRefreshListView;

    private AutoLooperBanner banner;

    private MarqueeLayout notice;
    private ImageView ivDemandPublish;

    public FgIndex() {
    }

    private View contentView;

    private View headerView;

    private TextView tvHotTask;
    private TextView tvRichTask;
    private TextView tvTaskCenter;
    private TextView tvSubscribe;

    private IHomeActivity parent;

    private IndexFragmentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        contentView = inflater.inflate(R.layout.fg_index, container, false);
        headerView = inflater.inflate(R.layout.view_index_header, null);
        initHeaderView(headerView);
        initView(contentView);
        initVariable();
        initEvent();
        return contentView;
    }

    private void initHeaderView(View headerView) {
        banner = (AutoLooperBanner) headerView.findViewById(R.id.index_banner);
        notice = (MarqueeLayout) headerView.findViewById(R.id.index_notice);

        tvHotTask = (TextView) headerView.findViewById(R.id.index_hottask);
        tvRichTask = (TextView) headerView.findViewById(R.id.index_richtask);
        tvTaskCenter = (TextView) headerView.findViewById(R.id.index_taskcenter);

        tvSubscribe = (TextView) headerView.findViewById(R.id.index_subscribe);

        tvSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //统计 从主页进行订阅 -计数
                parent.reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_ATHOME);

                presenter.callSubscribe();
            }
        });

        //一件发需求
        ivDemandPublish = (ImageView) headerView.findViewById(R.id.iv_demand_publish);
        ivDemandPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin()) {
                    jump2PublishDemand();
                } else {
                    Intent intent = LoginIntentFactory.create(getActivity(),
                            IndexFragmentPresenter.LoginTrigger.PublishDemand);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    public void jump2Subscribe() {
        Intent intent = new Intent(getActivity(), SubscribeActivity.class);
        intent.putExtra(SubscribeActivity.KEY_DATA, mLoginInfo.getUsername());
        getActivity().startActivity(intent);
    }

    /**
     * 跳转到发布需求
     */
    @Override
    public void jump2PublishDemand() {
        Intent intent = new Intent(getActivity(), PublishDemandActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    protected ImageView getAvatarView() {
        if (titleBar == null)
            return null;
        return titleBar.getAvatarView();
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.startAutoPlay();
        notice.start();
    }

    @Override
    protected void initView(View contentView) {
        titleBar = (FgIndexTitleBar) contentView.findViewById(R.id.titlebar);
        pullToRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id
                .index_ptr_list);
    }

    private DMOListAdapter adapter;

    @Override
    protected void initVariable() {
        parent = (IHomeActivity) getActivity();
        presenter = new IndexFragmentPresenter(this);

        pullToRefreshListView.getRefreshableView().addHeaderView(headerView);

        adapter = new DMOListAdapter(new ArrayList<DemandItemData>(),
                new SimpleTaskItemViewProviderImpl(getActivity(), this));
        pullToRefreshListView.getRefreshableView().setAdapter(adapter);

        presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
    }

    @Override
    protected void initEvent() {
        tvHotTask.setOnClickListener(this);
        tvTaskCenter.setOnClickListener(this);
        tvRichTask.setOnClickListener(this);
        titleBar.setOnToggleListener(this);

        presenter.callGetRecommendTaskList(mLoginInfo.getUsername());

        String path = getFragmentInteractionListener().onPreAvatarLoad();
        loadAvatar(path);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(refreshListener);
        pullToRefreshListView.getRefreshableView().setHeaderDividersEnabled(false);

        banner.setIBannerUpdate(this);

        titleBar.setOnSearchClickListerner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //统计 点击搜索 -计数
                parent.reportCountEvent(IUmengEventKey.KEY_SEARCH_ATHOME);

                getFragmentInteractionListener().onSearchClick();
            }
        });
        presenter.callGetBannerData();
        presenter.callGetGlobalNotify();

    }

    private boolean isLogin() {
        return !StringUtil.isEmpty(mLoginInfo.getUsername());
    }

    private PullToRefreshBase.OnRefreshListener2 refreshListener = new PullToRefreshBase
            .OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);

            presenter.callGetRecommendTaskList(mLoginInfo.getUsername());
            presenter.callGetBannerData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
            presenter.callAddRecommendTaskList(mLoginInfo.getUsername(), adapter.getDefaultPagedOffset());
        }

    };


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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stopAutoPlay();
        notice.stop();
    }

    @Override
    public void UpdateImage(ImgRes<?> res, ImageView imageView) {
        if (res.isDefaultExist()) {
            Picasso.with(imageView.getContext())
                    .load((String) res.getRes())
                    .diskCache(BaseActivity.getLocalImageCache())
                    .placeholder(res.getDefaultUrlRes())
                    .fit()
                    .into(imageView);
        } else {
            Picasso.with(imageView.getContext())
                    .load((String) res.getRes())
                    .diskCache(BaseActivity.getLocalImageCache())
                    .fit()
                    .into(imageView);
        }
    }


    @Override
    public void onOpenMore() {
        super.onOpenMore();
    }

    @Override
    public void onCloseMore() {
        super.onCloseMore();
    }

    @Override
    public void showBanner(List<String> bannerImgUrls,
                           AutoLooperBanner.OnBannerItemClickListener onBannerItemClickListener) {
        if (bannerImgUrls == null || bannerImgUrls.isEmpty()) {
            return;
        }
        banner.setViewUrls(bannerImgUrls);
        //添加监听事件
        banner.setOnBannerItemClickListener(onBannerItemClickListener);
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
        mLoginInfo.copy(event.getLoginInfo());
        presenter.setLoginStatus(true);
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

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    @Override
    public void finishRefresh() {
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void setRecommendListItems(ArrayList<DemandItemData> liData) {
        adapter.setLiDatas(liData);
    }

    @Override
    public void addRecommendListItems(ArrayList<DemandItemData> liData) {
        adapter.addLiDatas(liData);
    }

    @Override
    public void updateGlobalNotify(CharSequence charSequence) {
        notice.setMarqueeText(charSequence);
        notice.start();
    }

    @Override
    public void jump2RecomInfoActivity(BannerResBean bannerResBean) {
        //修改呈现页面 2016-12-7 15:47:36
        Intent intent = new Intent(getActivity(), BannerInfoActivity.class);
        intent.putExtra(BannerInfoActivity.KEY_DATA, bannerResBean.getLink());
        intent.putExtra(UMengActivity.KEY_ORIGINCLASS_SHOULDRESTRICTONFINISH, HomeActivity.class.getName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.index_hottask:
                //统计 进入最火 - 计数
                parent.reportCountEvent(IUmengEventKey.KEY_VIEW_TASK_HOT);

                startActivity(new Intent(getActivity(), HotTaskActivity.class));
                break;
            case R.id.index_richtask:
                //统计 进入最壕 - 计数
                parent.reportCountEvent(IUmengEventKey.KEY_VIEW_TASK_RICH);

                startActivity(new Intent(getActivity(), RichTaskActivity.class));
                break;
            case R.id.index_taskcenter:
                //统计 进入需求大厅 - 计数
                parent.reportCountEvent(IUmengEventKey.KEY_TASKCENTER_ATHOME);

                startActivity(new Intent(getActivity(), TaskCenterActivity.class));
                break;

            default:
                break;
        }
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

    /**
     * desc: 为item设置各种回调，
     *
     * @param position
     * @param data
     * @param view
     */
    @Override
    public void setCallBack(final int position, final DemandItemData data, final DemandItemView
            view) {

        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DemandInfoActivityData _d = new DemandInfoActivityData();

                                        _d.setDemandId(data.getUniqueId());
                                        _d.setLoginInfo(mLoginInfo);
                                        Intent intent = new Intent(getActivity(),
                                                DemandInfoActivity.class);

                                        intent.putExtra(DemandInfoActivity.KEY_DATA, JSON
                                                .toJSONString(_d));
                                        getActivity().startActivity(intent);

                                    }
                                }
        );
    }
}
