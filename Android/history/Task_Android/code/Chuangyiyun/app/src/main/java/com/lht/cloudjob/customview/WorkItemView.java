package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.file.PreviewUtils;
import com.lht.cloudjob.util.time.TimeUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> WorkItemView
 * <p><b>Description</b>: 稿件列表子视图
 * <p> Create by Leobert on 2016/8/26
 */
public class WorkItemView extends FrameLayout {

    private TextView tvHintAttachment;

    public enum Type {
        reward, openBid, hideBid, unset
    }

    private static final int MAX_LINE_IN_SUMMARYMODE = 3;

    private Type type = Type.unset;

    public void setType(Type type) {
        this.type = type;
    }


    public WorkItemView(Context context) {
        super(context);
        init();
    }

    public WorkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorkItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View contentView;

    /**
     * 投稿人头像
     */
    private RoundImageView imgAvatar;

    /**
     * 投稿人
     */
    private TextView tvName;

    /**
     * 投稿时间
     */
    private TextView tvTime;

    /**
     * 稿件内容
     */
    private LinearLayout llContent;

    /**
     * 隐藏稿件的封面
     */
    private LinearLayout llCoverHidden;

    /**
     * 稿件描述
     */
    private TextView tvDescription;

    /**
     * 工期等信息
     */
    private TextView tvInfo;

    /**
     * 稿件图片
     */
    public ImageView imgWork;

    /**
     * 展开键
     */
    private CheckBox cbMore;

    /**
     * 稿件状态
     */
    private ImageView imgStatus;

    /**
     * 稿件名称
     */
    private TextView tvFileName;

    /**
     * 稿件名称前面的link
     */
    private ImageView ivFileLink;

    /**
     * 稿件大小
     */
    private TextView tvFileSize;

    private RelativeLayout rl_files;

    private void init() {
        contentView = inflate(getContext(), R.layout.view_item_works, this);
        initView(contentView);
        initEvent();
    }

    private void initEvent() {
        //展开键逻辑
        cbMore.setOnCheckedChangeListener(null);
        cbMore.setChecked(false);
        cbMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    cbMore.setText(R.string.v1010_default_demandinfo_cb_less);
                } else {
                    tvDescription.setMaxLines(3);
                    cbMore.setText(R.string.v1010_default_demandinfo_cb_more);
                }
            }
        });
    }

    private void initView(View contentView) {
        imgAvatar = (RoundImageView) contentView.findViewById(R.id.worksitem_img_avatar);
        tvName = (TextView) contentView.findViewById(R.id.worksitem_tv_name);
        tvTime = (TextView) contentView.findViewById(R.id.worksitem_tv_time);
        llContent = (LinearLayout) contentView.findViewById(R.id.worksitem_ll_content);
        llCoverHidden = (LinearLayout) contentView.findViewById(R.id.worksitem_cover_hidden);
        tvDescription = (TextView) contentView.findViewById(R.id.worksitem_tv_description);
        cbMore = (CheckBox) contentView.findViewById(R.id.worksitem_cb_more);
        tvInfo = (TextView) contentView.findViewById(R.id.worksitem_tv_info);
        imgStatus = (ImageView) contentView.findViewById(R.id.worksitem_img_status);
        imgWork = (ImageView) contentView.findViewById(R.id.worksitem_img_work);
        tvFileName = (TextView) contentView.findViewById(R.id.tv_worksitem_file_work);
        ivFileLink = (ImageView) contentView.findViewById(R.id.iv_worksitem_file_link);
        tvFileSize = (TextView) contentView.findViewById(R.id.tv_worksitem_file_size);
        rl_files = (RelativeLayout) contentView.findViewById(R.id.rl_files);
        tvHintAttachment = (TextView) contentView.findViewById(R.id.tv_worksitem_hintAttachment);
    }

    private ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            int lineCount = tvDescription.getLineCount();
            if (lineCount > MAX_LINE_IN_SUMMARYMODE) {
                cbMore.setVisibility(VISIBLE);
            } else {
                cbMore.setVisibility(GONE);
            }
        }
    };

    private DemandInfoResBean.Work workData;

    public void setWorkData(DemandInfoResBean.Work workData) {
        this.workData = workData;
        freshViewByData();
    }

    private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private void freshViewByData() {
        //是否隐藏
        if (workData.isHidden()) {
            llContent.setVisibility(GONE);
            llCoverHidden.setVisibility(VISIBLE);
        } else {
            llCoverHidden.setVisibility(GONE);
            llContent.setVisibility(VISIBLE);
        }

        loadAvatar();
        loadTextTypedInfo();
        loadAttachment();
        loadStatusTag();

        //刷新显示、隐藏 展开键
        tvDescription.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        tvDescription.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        int lineCount = tvDescription.getLineCount();
        if (lineCount > MAX_LINE_IN_SUMMARYMODE) {
            cbMore.setVisibility(VISIBLE);
        } else {
            cbMore.setVisibility(GONE);
        }

    }

    private void loadAvatar() {
        Picasso.with(getContext()).load(workData.getAvatar())
//                .diskCache(BaseActivity.getLocalImageCache())  escape disk-cache
                .placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default)
                .fit().into(imgAvatar);
    }

    private void loadStatusTag() {
        if (workData.isHittedWork()) {
            imgStatus.setImageResource(R.drawable.v1010_drawable_icon_success);
            imgStatus.setVisibility(VISIBLE);
            imgStatus.bringToFront();
        } else if (workData.isOptionalWork()) {
            imgStatus.setImageResource(R.drawable.v1011_drawable_icon_alternate);
            imgStatus.setVisibility(VISIBLE);
            imgStatus.bringToFront();
        } else {
            imgStatus.setImageResource(R.color.transparent);
            imgStatus.setVisibility(GONE);
        }
    }

    private void loadTextTypedInfo() {
        if (type == Type.reward) {
            tvInfo.setVisibility(GONE);
        } else if (type == Type.unset) {
            DLog.e(Lmsg.class,"work item view,type not set");
            tvInfo.setVisibility(GONE);
        } else {
            double price = workData.getPrice();
            if (price > 0) {
                tvInfo.setVisibility(VISIBLE);
                tvInfo.setText(workData.getInfoAlias(type));
            } else {
                tvInfo.setVisibility(GONE);
            }
        }

        tvName.setText(workData.getNickname());
        tvTime.setText(TimeUtil.getTime(workData.getCreate_time(), TIME_FORMAT));

        tvDescription.setMaxLines(MAX_LINE_IN_SUMMARYMODE);
        tvDescription.setText(workData.getDescription());
        cbMore.setText(R.string.v1010_default_demandinfo_cb_more);
    }

    private void loadAttachment() {
        ArrayList<DemandInfoResBean.AttachmentExt> attachments = workData.getWork_ext();
        if (attachments != null && !attachments.isEmpty()) {
            ArrayList<DemandInfoResBean.AttachmentExt> images = PreviewUtils.filterImageTypedWithoutGif(attachments);
            ArrayList<DemandInfoResBean.AttachmentExt> files = PreviewUtils.filterFileTyped(attachments);

            loadImageAttachment(images);
            loadFileAttachment(files);
        } else {
            rl_files.setVisibility(GONE);
            tvHintAttachment.setVisibility(GONE);
            imgWork.setVisibility(GONE);
//            ivFileLink.setVisibility(GONE);
//            tvFileName.setVisibility(GONE);
//            tvFileSize.setVisibility(GONE);
        }
    }

    private void loadImageAttachment(final ArrayList<DemandInfoResBean.AttachmentExt> images) {
        if (images.size() != 0) {
            imgWork.setVisibility(View.VISIBLE);
            final DemandInfoResBean.AttachmentExt imageExt = images.get(0);
            Picasso.with(getContext()).load(imageExt.getUrl_thumbnail())
                    .diskCache(BaseActivity.getLocalImageCache())
                    .placeholder(R.drawable.v1011_drawable_work_default)
                    .error(R.drawable.v1011_drawable_work_error)
                    .fit().into(imgWork);
            imgWork.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        attachmentClickListener.onImageAttachmentClick(imageExt, getWorkOwner());
                    }
                }
            });
        } else {
            imgWork.setOnClickListener(null);
            imgWork.setVisibility(View.GONE);
        }
    }

    private void loadFileAttachment(ArrayList<DemandInfoResBean.AttachmentExt> files) {
        if (files.size() != 0) {
//            ivFileLink.setVisibility(VISIBLE);
//            tvFileName.setVisibility(VISIBLE);
//            tvFileSize.setVisibility(VISIBLE);
            rl_files.setVisibility(VISIBLE);
            final DemandInfoResBean.AttachmentExt fileExt = files.get(0);
            tvFileName.setText(files.get(0).getFile_name());
            String fileSize = FileUtils.calcSize(fileExt.getFile_size());
            tvFileSize.setText("(" + fileSize + ")");
            rl_files.bringToFront();
            rl_files.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        attachmentClickListener.onFileAttachmentClick(fileExt, getWorkOwner());
                    }
                }
            });
        } else {
            rl_files.setOnClickListener(null);
            rl_files.setVisibility(GONE);
//            ivFileLink.setVisibility(GONE);
//            tvFileName.setVisibility(GONE);
//            tvFileSize.setVisibility(GONE);
        }
    }

    private String getWorkOwner() {
        if (workData == null) {
            return null;
        }
        return workData.getUsername();
    }

    private IAttachmentClickListener attachmentClickListener;

    public void setAttachmentClickListener(IAttachmentClickListener attachmentClickListener) {
        this.attachmentClickListener = attachmentClickListener;
    }

    public interface IAttachmentClickListener {

        void onImageAttachmentClick(DemandInfoResBean.AttachmentExt image, String workOwner);

        void onFileAttachmentClick(DemandInfoResBean.AttachmentExt file, String workOwner);
    }

}
