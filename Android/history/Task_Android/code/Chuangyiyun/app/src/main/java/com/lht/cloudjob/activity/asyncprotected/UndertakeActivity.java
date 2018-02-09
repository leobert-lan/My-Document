package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.ImageWithDeleteView;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.presenter.UndertakeActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IUndertakeActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * <p><b>Package</b> com.lht.cloudjob.activity.asyncprotected
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> UndertakeActivity
 * <p><b>Description</b>: the abstract super class of undertake type.
 * this activity-class describes most methods about image-take
 * <p> Create by Leobert on 2016/9/13
 */
public abstract class UndertakeActivity extends AsyncProtectedActivity implements
        IUndertakeActivity, CompoundButton.OnCheckedChangeListener {
    protected TextView tvHint;
    protected ImageWithDeleteView imgWork;
    protected ImageView imgAddWork;
    protected EditText etDescribe;

    protected RadioButton rbHideLetter;
    protected RadioButton rbNotHideLetter;

    /**
     * 是否显示稿件
     */
    protected boolean isShow = false;

    @Override
    public void addWorkPicture(String path) {
        imgWork.setVisibility(View.VISIBLE);
        imgAddWork.setVisibility(View.GONE);
//        tvHint.setVisibility(View.GONE);

        Picasso.with(this).load(new File(path)).error(R.drawable.v1011_drawable_work_default)
                .placeholder(R.drawable.v1011_drawable_work_default).fit()
                .into(imgWork.getActualImageView());

    }

    @Override
    public void notifyOverLength() {
        ToastUtils.show(getActivity(),
                getString(R.string.v1010_toast_warm_enter_text_overlength), ToastUtils.Duration.s);
    }

    @Override
    public void showDialog(int contentResid, int positiveResid,
                           CustomPopupWindow.OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    protected void initEvent() {
        //统计 打开投稿页面 -计数
        reportCountEvent(IUmengEventKey.KEY_TASK_SUBMIS_PAGE);

        imgAddWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UndertakeActivityPresenter) getApiRequestPresenter()).callAddPicture();
            }
        });

        imgWork.setOnImageDeleteListener(new ImageWithDeleteView.OnImageDeleteListener() {
            @Override
            public void onDelete(ImageWithDeleteView view, Object tag) {
                imgWork.setVisibility(View.GONE);
                ((UndertakeActivityPresenter) getApiRequestPresenter()).removePicture();
                imgAddWork.setVisibility(View.VISIBLE);
                tvHint.setVisibility(View.VISIBLE);
            }
        });

        ((UndertakeActivityPresenter) getApiRequestPresenter()).watchText(etDescribe,
                UndertakeActivityPresenter.MAX_DESC_LENGTH);


        rbHideLetter.setOnCheckedChangeListener(this);
        rbNotHideLetter.setOnCheckedChangeListener(this);
    }

    @Override
    public void showImageGetActionSheet(String[] data, OnActionSheetItemClickListener
            onActionSheetItemClickListener, boolean isBrokenable) {
        ActionSheet actionSheet = new ActionSheet(getActivity());

        if (isBrokenable) {
            actionSheet.enableBrokenButton();
        }

        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOutsideClickDismiss();
        actionSheet.transparent();
        actionSheet.setOnActionSheetItemClickListener(onActionSheetItemClickListener);
        actionSheet.show();
    }

    @Override
    public void onBackPressed() {
        //统计 未完成投稿 -计数
        reportCountEvent(IUmengEventKey.KEY_TASK_SUBMIS_INCOMPL);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UndertakeActivityPresenter.INTENT_CODE_ALBUM:
                if (resultCode == RESULT_OK) {
                    String path = FileUtils.queryImageByUri(data.getData(), getContentResolver());
                    if (!StringUtil.isEmpty(path)) {
                        ((UndertakeActivityPresenter) getApiRequestPresenter()).callTransAlbum
                                (path);
                    } else {
                        ToastUtils.show(getActivity(), R.string
                                .v1010_toast_error_album_private, ToastUtils.Duration.s);
                    }
                } else {
                    //相册 未获取图片
                    DLog.e(getClass(), "select: fail");
                }
                break;
            case UndertakeActivityPresenter.INTENT_CODE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    DLog.e(getClass(), "capture: trans");
                    //相机 获取图片并进一步处理
                    ((UndertakeActivityPresenter) getApiRequestPresenter()).callTransCapture();
                } else {
                    //相机 未获取图片
                    DLog.e(getClass(), "capture: fail");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == rbHideLetter.getId()) {
            isShow = !rbHideLetter.isChecked();
        }

        if (buttonView.getId() == rbNotHideLetter.getId()) {
            isShow = rbNotHideLetter.isChecked();
        }
    }
}
