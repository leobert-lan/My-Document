package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.innerweb.MessageInfoActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.adapter.viewprovider.MessageItemViewProviderImpl;
import com.lht.cloudjob.clazz.BadgeNumberManager;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.MaskView;
import com.lht.cloudjob.customview.TitleBarModify;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.net.IRestfulApi;
import com.lht.cloudjob.mvp.model.bean.MessageListItemResBean;
import com.lht.cloudjob.mvp.presenter.MessageListActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IMessageListActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.ptrlib.library.PullToRefreshBase;
import com.lht.ptrlib.library.PullToRefreshListView;
import com.lht.ptrlib.library.PullToRefreshUtil;

import java.util.ArrayList;

public class SysMsgListActivity extends AsyncProtectedActivity implements IMessageListActivity,
        ListAdapter2.ICustomizeListItem2<MessageListItemResBean, MessageItemViewProviderImpl
                .ViewHolder>, View.OnClickListener {

    private static final String PAGENAME = "SysMsgListActivity";

    public static final String KEY_DATA = "_data_username";

    private ProgressBar progressBar;

    private TitleBarModify titleBar;

    private MessageListActivityPresenter presenter;

    private PullToRefreshListView pullToRefreshListView;

    private ListAdapter2<MessageListItemResBean> listAdapter;

    private MessageItemViewProviderImpl itemViewProvider;

    private LinearLayout llBottomBar;

    private Button btnMarkRead;

    private Button btnDelete;

    private MaskView maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sysmsg_list);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return SysMsgListActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return SysMsgListActivity.this;
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        titleBar = (TitleBarModify) findViewById(R.id.titlebar);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.message_list);
        llBottomBar = (LinearLayout) findViewById(R.id.buttom_bar);

        btnMarkRead = (Button) findViewById(R.id.message_btn_markbread);
        btnDelete = (Button) findViewById(R.id.message_btn_delete);

        maskView = (MaskView) findViewById(R.id.mask);
    }

    @Override
    protected void initVariable() {
        String username = getIntent().getStringExtra(KEY_DATA);
        presenter = new MessageListActivityPresenter(username, this);
        itemViewProvider = new MessageItemViewProviderImpl(getLayoutInflater(), this);
        listAdapter = new ListAdapter2<>(new ArrayList<MessageListItemResBean>(), itemViewProvider);
        pullToRefreshListView.getRefreshableView().setAdapter(listAdapter);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.title_activity_demandnotifymessage);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(refreshListener);

        titleBar.setRightOnToggleListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //模式切换
                itemViewProvider.setModifyModeEnable(isChecked);
                listAdapter.notifyDataSetChanged();
                if (isChecked) {
                    llBottomBar.setVisibility(View.VISIBLE);
                } else {
                    llBottomBar.setVisibility(View.GONE);
                    deselectAll();
                }
            }
        });

        titleBar.setLeftOnToggleListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //全选
                ArrayList<MessageListItemResBean> data = listAdapter.getAll();
                if (data == null) {
                    data = new ArrayList<>();
                }
                for (MessageListItemResBean bean : data) {
                    bean.setIsSelected(true);
                }
                listAdapter.setLiData(data);
            }
        });

        btnDelete.setOnClickListener(this);
        btnMarkRead.setOnClickListener(this);

        presenter.callRefreshMessages(IRestfulApi.MessageListApi.MessageType.SYSTEM);

    }

    private PullToRefreshBase.OnRefreshListener2<ListView> refreshListener = new
            PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callRefreshMessages(IRestfulApi.MessageListApi.MessageType.SYSTEM);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    PullToRefreshUtil.updateLastFreshTime(getActivity(), refreshView);
                    presenter.callAddMessages(listAdapter.getCount(),
                            IRestfulApi.MessageListApi.MessageType.SYSTEM);
                }
            };

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void setMessageListData(ArrayList<MessageListItemResBean> data) {
        maskView.hide();
        listAdapter.setLiData(data);
    }

    @Override
    public void addMessageListData(ArrayList<MessageListItemResBean> data) {
        maskView.hide();
        listAdapter.addLiData(data);
    }

    @Override
    public void showErrorMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void showEmptyView() {
        titleBar.setHideRightToggle();
        maskView.showAsEmpty(getString(R.string.v1010_mask_empty_msg));
    }

    @Override
    public void finishModify() {
        titleBar.finishModify();
        deselectAll();
    }

    @Override
    public void showRetryView(View.OnClickListener listener) {
        maskView.showAsNetErrorRetry(listener);
    }

    @Override
    public IRestfulApi.MessageListApi.MessageType askMessageType() {
        return IRestfulApi.MessageListApi.MessageType.SYSTEM;
    }

    @Override
    public void showDeleteAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setContent(R.string.v1010_dialog_message_content_delete);
        dialog.setNegativeButton(R.string.cancel);
        dialog.setPositiveButton(R.string.permit);
        dialog.setPositiveClickListener(positiveClickListener);
        dialog.show();
    }

    private void deselectAll() {
        ArrayList<MessageListItemResBean> data = listAdapter.getAll();
        for (MessageListItemResBean bean : data) {
            bean.setIsSelected(false);
        }
        listAdapter.setLiData(data);
    }

    @Override
    public void cancelWaitView() {
        super.cancelWaitView();
        pullToRefreshListView.onRefreshComplete();
    }


    /**
     * desc: 为item设置各种回调，
     *
     * @param position    位置
     * @param convertView 视图
     * @param viewHolder  holder
     */
    @Override
    public void customize(final int position, final MessageListItemResBean data, View convertView,
                          final MessageItemViewProviderImpl.ViewHolder viewHolder) {
        viewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemViewProvider.isModifyMode()) {
                    data.setView_status(MessageListItemResBean.STATUS_READ);
                    listAdapter.notifyDataSetChanged();

                    BadgeNumberManager.getInstance().removeSystemNotify(1);//读取减一

                    String url = IPublicConst.MsgInfoUrlHelpler.formatUrl(data.getMsg_id());
                    Intent intent = new Intent(getActivity(), MessageInfoActivity.class);
                    intent.putExtra(MessageInfoActivity.KEY_DATA, url);
                    intent.putExtra(MessageInfoActivity.KEY_HAS_MARKREADED, true);
                    getActivity().startActivity(intent);
                }
            }
        });

        viewHolder.cbSelect.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        data.setIsSelected(isChecked);
                        listAdapter.replaceData(position, data);

                        if (isChecked) {
                            viewHolder.rlRoot.setBackgroundResource(R.color.h6_divider);
                            viewHolder.line.setVisibility(View.INVISIBLE);
                        } else {
                            viewHolder.rlRoot.setBackgroundResource(R.color.bg_white);
                            viewHolder.line.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_btn_delete:
                callDelete();
                break;
            case R.id.message_btn_markbread:
                callMarkRead();
                break;
            default:
                break;
        }
    }

    private void callMarkRead() {
        ArrayList<MessageListItemResBean> data = listAdapter.getAll();
        ArrayList<String> ids = new ArrayList<>();
        int unReadCount = 0;
        for (MessageListItemResBean bean : data) {
            if (bean.isSelected()) {
                ids.add(bean.getMsg_id());
                if (bean.getView_status() != MessageListItemResBean.STATUS_READ)
                    unReadCount++;
            }
        }
        if (unReadCount > 0) {
            BadgeNumberManager.getInstance().removeSystemNotify(unReadCount);
        }
        presenter.callMarkMsgRead(ids);
    }

    private void callDelete() {
        ArrayList<MessageListItemResBean> data = listAdapter.getAll();
        ArrayList<String> ids = new ArrayList<>();
        int unReadCount = 0;
        for (MessageListItemResBean bean : data) {
            if (bean.isSelected()) {
                ids.add(bean.getMsg_id());
                if (bean.getView_status() != MessageListItemResBean.STATUS_READ)
                    unReadCount++;
            }
        }
        if (unReadCount > 0) {
            BadgeNumberManager.getInstance().removeSystemNotify(unReadCount);
        }

        presenter.callDeleteMsg(ids);


    }

    //结束刷新、加载
    @Override
    public void finishRefresh() {
        PullToRefreshUtil.updateLastFreshTime(this, pullToRefreshListView);
        pullToRefreshListView.onRefreshComplete();
    }

    /**
     * 显示右边的编辑按钮
     */
    @Override
    public void showCompiletoggle() {
        titleBar.setShowRightToggle();
    }
}