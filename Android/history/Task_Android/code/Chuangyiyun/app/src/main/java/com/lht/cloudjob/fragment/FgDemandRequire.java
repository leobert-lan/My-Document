package com.lht.cloudjob.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.adapter.viewprovider.FilesAttachmentProviderImpl;
import com.lht.cloudjob.adapter.viewprovider.ImagesAttachmentProviderImpl;
import com.lht.cloudjob.customview.ConflictListView;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.CustomProgressView;
import com.lht.cloudjob.customview.RoundImageView;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.mvp.model.pojo.PreviewFileEntity;
import com.lht.cloudjob.mvp.presenter.DemandRequireFragmentPresenter;
import com.lht.cloudjob.mvp.viewinterface.IActivityAsyncProtected;
import com.lht.cloudjob.mvp.viewinterface.IDemandRequireFragment;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.file.PreviewUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.time.TimeUtil;
import com.lht.customwidgetlib.list.HorizontalListView;
import com.lht.customwidgetlib.nestedscroll.AttachUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FgDemandRequire extends BaseFragment implements IDemandRequireFragment, IVerifyHolder {

    private static final String PAGENAME = "FgDemandRequire";

    private DemandInfoResBean demandInfoResBean;

    private TextView tvRequirements;

    private TextView hintAttachment;

    private TextView tvName;

    private RoundImageView imgAvatar;

//    private TextView tvContact;

    private TextView tvRegisterTime;

    private CheckBox cbFollow;

    private ScrollView scrollView;

    private DemandRequireFragmentPresenter presenter;

    private IActivityAsyncProtected parent;
    private HorizontalListView hlvImages;
    private ConflictListView clvFiles;

    private ListAdapter2<DemandInfoResBean.AttachmentExt> imageAdapter;

    private ListAdapter2<DemandInfoResBean.AttachmentExt> fileAdapter;


    public FgDemandRequire() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fg_demand_require, container, false);
        initView(view);
        initVariable();
        initEvent();

        return view;
    }

    protected void initView(View view) {
        scrollView = (ScrollView) view.findViewById(R.id.fgdr_sv);
        tvRequirements = (TextView) view.findViewById(R.id.fgdr_tv_requirement);
        hintAttachment = (TextView) view.findViewById(R.id.hint_attachment);
        imgAvatar = (RoundImageView) view.findViewById(R.id.fgdr_img_publisher_avatar);
        tvName = (TextView) view.findViewById(R.id.fgdr_tv_publisher_name);

//        tvContact = (TextView) view.findViewById(R.id.fgdr_tv_publisher_contact);
        tvRegisterTime = (TextView) view.findViewById(R.id.fgdr_tv_publisher_registertime);
        cbFollow = (CheckBox) view.findViewById(R.id.fgdr_btn_follow);
        hlvImages = (HorizontalListView) view.findViewById(R.id.fgdr_attachments_imgs);
        clvFiles = (ConflictListView) view.findViewById(R.id.fgdr_attachments_files);

    }

    protected void initVariable() {
        parent = (IActivityAsyncProtected) getActivity();
        presenter = new DemandRequireFragmentPresenter(this);
        presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
        ImagesAttachmentProviderImpl imagesItemViewProvider =
                new ImagesAttachmentProviderImpl(getActivity().getLayoutInflater(), imgItemCustomor);
        imageAdapter =
                new ListAdapter2<>(new ArrayList<DemandInfoResBean.AttachmentExt>(), imagesItemViewProvider);

        FilesAttachmentProviderImpl filesItemViewProvider =
                new FilesAttachmentProviderImpl(getActivity().getLayoutInflater(), fileItemCustomor);
        fileAdapter =
                new ListAdapter2<>(new ArrayList<DemandInfoResBean.AttachmentExt>(), filesItemViewProvider);
    }

    protected void initEvent() {

        hlvImages.setAdapter(imageAdapter);
        clvFiles.setAdapter(fileAdapter);
        clvFiles.setDividerHeight(0);
        cbFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbFollow.isChecked()) {
                    //if has followed ,click will change the state_check to false,and we should
                    // do unfollow
                    presenter.callUnFollowPublisher(mLoginInfo.getUsername(), getPublisher());
                } else {
                    presenter.callFollowPublisher(mLoginInfo.getUsername(), getPublisher());
                }
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isTopArrived = AttachUtil.isScrollViewAttach(scrollView);
                EventBus.getDefault().post(new AppEvent.NestedContentScrollEvent(isTopArrived));
                return false;
            }
        });
    }

    private void initFollow() {
        if (!StringUtil.isEmpty(mLoginInfo.getUsername())) {
            if (mLoginInfo.getUsername().equals(getPublisher())) {
                cbFollow.setVisibility(View.GONE);
            } else {
                cbFollow.setVisibility(View.VISIBLE);
            }
        } else {
            cbFollow.setVisibility(View.VISIBLE);
        }
    }

    public DemandInfoResBean getDemandInfoResBean() {
        if (demandInfoResBean == null) {
            demandInfoResBean = new DemandInfoResBean();
        }
        return demandInfoResBean;
    }

    public void setDemandInfoResBean(DemandInfoResBean demandInfoResBean) {
        this.demandInfoResBean = demandInfoResBean;
        if (isResumed()) {
            updateView();
        }
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
    public void onResume() {
        super.onResume();
        updateView();
    }

    /**
     * 更新视图内容
     */
    private void updateView() {
        DemandInfoResBean bean = getDemandInfoResBean();

        tvRequirements.setText(bean.getDescription());

        initFollow();

        ArrayList<DemandInfoResBean.AttachmentExt> attachments = bean.getAttachment();
        updateAttachments(attachments);


        DemandInfoResBean.Publisher publisher = bean.getPublisher();
        if (publisher == null) {
            publisher = new DemandInfoResBean.Publisher();
        }
        Picasso.with(getActivity()).load(publisher.getAvatar()).diskCache(BaseActivity
                .getLocalImageCache()).placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default).fit().into(imgAvatar);

        tvName.setText(publisher.getNickname());

//        String _contact = new StringBuilder().append(getString(R.string.v1010_default_fgdemandrequire_contact)).append(publisher.getNickname()).toString();
//        tvContact.setText(_contact);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String _createTime = TimeUtil.getTime(bean.getCreate_time(), format);


        tvRegisterTime.setText(_createTime);

        DemandInfoResBean.Favored favored = bean.getFavored();
        if (favored == null) {
            favored = new DemandInfoResBean.Favored();
        }
        setPublisherFollowed(favored.isPublisher());
    }

    /**
     * @param attachments
     */
    private void updateAttachments(ArrayList<DemandInfoResBean.AttachmentExt> attachments) {
        if (attachments != null) {
            if (attachments.size() != 0) {
                hintAttachment.setVisibility(View.VISIBLE);
                ArrayList<DemandInfoResBean.AttachmentExt> images = PreviewUtils.filterImageTypedWithoutGif(attachments);
                ArrayList<DemandInfoResBean.AttachmentExt> files = PreviewUtils.filterFileTyped(attachments);
                if (images.size() != 0) {
                    imageAdapter.setLiData(images);
                } else {
                    hlvImages.setVisibility(View.GONE);
                }
                if (files.size() != 0) {
                    fileAdapter.setLiData(files);
                } else {
                    clvFiles.setVisibility(View.GONE);
                }
                // TODO: 2016/11/9 test
//                fileAdapter.setLiData(files);
            } else {
                hintAttachment.setVisibility(View.GONE);
                hlvImages.setVisibility(View.GONE);
                clvFiles.setVisibility(View.GONE);
            }
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
        presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
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
        presenter.identifyCanceledTrigger(event.getTrigger());
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
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
        parent.showMsg(msg);
    }


    @Override
    public String getPublisher() {
        return getDemandInfoResBean().getUsername();
    }

    @Override
    public void setPublisherFollowed(boolean isFollowed) {
        cbFollow.setChecked(isFollowed);
        if (!isFollowed) {
            cbFollow.setText(R.string.v1010_default_demandinfo_text_follow);
        } else {
            cbFollow.setText(R.string.v1010_default_demandinfo_text_unfollow);
        }
    }

    private CustomProgressView customProgressView;

    @Override
    public void showPreviewProgress(PreviewFileEntity entity) {
        customProgressView = new CustomProgressView(parent.getActivity());
        customProgressView.show();
        customProgressView.setProgress(0, 100);
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

    private ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt,
            ImagesAttachmentProviderImpl.ViewHolder> imgItemCustomor =
            new ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt, ImagesAttachmentProviderImpl.ViewHolder>() {
                public void customize(final int position, DemandInfoResBean.AttachmentExt data, View convertView, ImagesAttachmentProviderImpl.ViewHolder viewHolder) {

                    Picasso.with(getActivity()).load(data.getUrl_thumbnail()).diskCache(BaseActivity
                            .getLocalImageCache()).placeholder(R.drawable.v1011_drawable_work_default)
                            .error(R.drawable.v1011_drawable_work_error).fit().into(viewHolder.ivImageAttachment);
                    viewHolder.ivImageAttachment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.callPreviewImage(imageAdapter.getAll(), position);
                        }
                    });
                }
            };

    private ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt,
            FilesAttachmentProviderImpl.ViewHolder> fileItemCustomor =
            new ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt, FilesAttachmentProviderImpl.ViewHolder>() {
                @Override
                public void customize(final int position, final DemandInfoResBean.AttachmentExt data, View convertView, FilesAttachmentProviderImpl.ViewHolder viewHolder) {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.callPreviewFile(getDemandId(), data);
                        }
                    });
                }
            };

    private String getDemandId() {
        if (demandInfoResBean == null) {
            return null;
        }
        return demandInfoResBean.getTask_bn();
    }

    public void cancelPreview() {
        if (presenter != null) {
            presenter.doCancelDownload();
        }
    }
}