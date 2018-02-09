package com.lht.cloudjob.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.WorksItemViewProviderImpl;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.CustomProgressView;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.customview.WorkItemView;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;
import com.lht.cloudjob.mvp.presenter.DemandWorksFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.IActivityAsyncProtected;
import com.lht.cloudjob.mvp.viewinterface.IDemandInfoActivity;
import com.lht.cloudjob.mvp.viewinterface.IDemandWorksFragment;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.customwidgetlib.nestedscroll.AttachUtil;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * 需求详情-稿件列表
 */
public class FgDemandWorks extends BaseFragment implements IDemandWorksFragment, IVerifyHolder {

    private static final String PAGENAME = "FgDemandWorks";

    private MaskView maskView;
    private DemandInfoResBean demandInfoResBean;

    private PullToRefreshListView pullToRefreshListView;

    private ListAdapter<DemandInfoResBean.Work> adapter;

    private WorksItemViewProviderImpl itemViewProvider;

    private DemandWorksFragmentPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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

    private View contentView;

    private IActivityAsyncProtected parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fg_demand_works, container, false);
        initView(contentView);

        initVariable();
        initEvent();
        return contentView;
    }


    protected void initView(View view) {
        maskView = (MaskView) view.findViewById(R.id.fgdw_mask);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.fgdw_list_works);
    }

    protected void initVariable() {
        presenter = new DemandWorksFragmentPresenter(this);
        itemViewProvider = new WorksItemViewProviderImpl(getActivity().getLayoutInflater(), workItemCustomor);
        adapter = new ListAdapter<>(new ArrayList<DemandInfoResBean.Work>(), itemViewProvider);
        parent = (IActivityAsyncProtected) getActivity();
    }


    protected void initEvent() {
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnRefreshListener(refreshListener);
        pullToRefreshListView.setOnScrollListener(new AbsListView
                .OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                boolean isTopArrived = AttachUtil.isAdapterViewAttach(view);
                EventBus.getDefault().post(new AppEvent.NestedContentScrollEvent(isTopArrived));
            }
        });
        updateView();
    }


    public DemandInfoResBean getDemandInfoResBean() {
        if (demandInfoResBean == null) {
            demandInfoResBean = new DemandInfoResBean();
        }
        return demandInfoResBean;
    }

    public void setDemandInfoResBean(DemandInfoResBean demandInfoResBean) {
        this.demandInfoResBean = demandInfoResBean;
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

    @Subscribe
    public void onEventMainThread(AppEvent.UnderTakeSuccessEvent event) {
        presenter.callGetWorksList(getUsername());
    }

    private WorkItemView.Type type = WorkItemView.Type.unset;

    private void updateView() {
        if (getDemandInfoResBean().getModel() == 2) {
            if (getDemandInfoResBean().getIs_mark() == 1) {
                type = WorkItemView.Type.openBid;
            } else {
                type = WorkItemView.Type.hideBid;
            }
        } else {
            type = WorkItemView.Type.reward;
        }

        itemViewProvider.setType(type);

        checkEmpty(getDemandInfoResBean().getWorks());
    }

    private String getPublisher() {
        return getDemandInfoResBean().getUsername();
    }

    private String getUsername() {
        return IVerifyHolder.mLoginInfo.getUsername();
    }


    private void checkEmpty(ArrayList<DemandInfoResBean.Work> works) {
        if (works == null || works.isEmpty()) {
            maskView.showAsEmpty(getResources().getString(R.string
                    .v1010_default_demandinfo_hint_empty));
            //隐藏后无法进行刷新
            //pullToRefreshListView.setVisibility(View.GONE);
            //清空内容
            adapter.setLiData(new ArrayList<DemandInfoResBean.Work>());
        } else {
            maskView.hide();
            adapter.setLiData(works);
            pullToRefreshListView.setVisibility(View.VISIBLE);
            pullToRefreshListView.bringToFront();
        }
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> refreshListener =
            new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callGetWorksList(mLoginInfo.getUsername());
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callAddWorksList(mLoginInfo.getUsername(), adapter.getCount());
                }
            };

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
        parent.showMsg(msg);
    }

    @Override
    public String getTaskBn() {
        return getDemandInfoResBean().getTask_bn();
    }

    @Override
    public void setListData(ArrayList<DemandInfoResBean.Work> data) {
        adapter.setLiData(data);
        checkEmpty(data);
    }

    @Override
    public void addListData(ArrayList<DemandInfoResBean.Work> data) {
        adapter.addLiData(data);
        checkEmpty(adapter.getAll());
    }

    @Override
    public void finishRefresh() {
        pullToRefreshListView.onRefreshComplete();
    }

    @Override
    public void updateCount(int count) {
        if (parent instanceof IDemandInfoActivity) {
            ((IDemandInfoActivity) parent).updateCount(count);
        }
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
        updateView();
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

    private ICustomizeListItem<WorksItemViewProviderImpl.ViewHolder> workItemCustomor =
            new ICustomizeListItem<WorksItemViewProviderImpl.ViewHolder>() {

                @Override
                public void customize(int position, View convertView, WorksItemViewProviderImpl.ViewHolder viewHolder) {
                    WorkItemView workItemView = viewHolder.workItemView;

                    workItemView.setAttachmentClickListener(new WorkItemView.IAttachmentClickListener() {
                        @Override
                        public void onImageAttachmentClick(DemandInfoResBean.AttachmentExt image, String workOwner) {
                            ArrayList<DemandInfoResBean.AttachmentExt> attachmentExts = new ArrayList<>();
                            attachmentExts.add(image);
                            presenter.callPreviewImage(attachmentExts, getDemandOwner(), workOwner);
                        }

                        @Override
                        public void onFileAttachmentClick(DemandInfoResBean.AttachmentExt file, String workOwner) {
                            presenter.callPreviewFile(getDemandId(), file, getDemandOwner(), workOwner);
                        }

                        private String getDemandOwner() {
                            if (demandInfoResBean == null) {
                                return null;
                            }
                            return demandInfoResBean.getUsername();
                        }

                        private String getDemandId() {
                            if (demandInfoResBean == null) {
                                return null;
                            }
                            return demandInfoResBean.getTask_bn();
                        }
                    });
                }
            };

    private CustomProgressView customProgressView;

    @Override
    public void UpdateProgress(PreviewFileEntity entity, long current, long total) {
        if (customProgressView == null) {
            // TODO: 2016/11/7  log
            return;
        }
        if (!customProgressView.isShowing()) {
            customProgressView.show();
        }

        customProgressView.setProgress(current, total);
    }

    @Override
    public void hidePreviewProgress() {
        if (customProgressView == null) {
            //todo log
            return;
        }
        customProgressView.dismiss();
        customProgressView = null;
    }

    @Override
    public void showPreviewProgress(PreviewFileEntity entity) {
        customProgressView = new CustomProgressView(parent.getActivity());
        customProgressView.show();
        customProgressView.setProgress(0, 100);
    }

    @Override
    public void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(parent.getActivity());
        dialog.setContent(R.string.v1020_dialog_download_onmobile);
        dialog.setNegativeButton(R.string.v1020_dialog_nbtn_onmobile);
        dialog.setPositiveButton(R.string.v1020_dialog_pbtn_onmobile);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public void showLargeSizeDownloadAlert(long fileSize, CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(parent.getActivity());
        final String format = getString(R.string.v1020_dialog_preview_onmobile_toolarge);
        dialog.setContent(String.format(Locale.ENGLISH, format, FileUtils.calcSize(fileSize)));
        dialog.setNegativeButton(R.string.v1020_dialog_nbtn_onmobile);
        dialog.setPositiveButton(R.string.v1020_dialog_pbtn_onmobile);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    public void cancelPreview() {
        if (presenter != null) {
            presenter.doCancelDownload();
        }
    }
}
