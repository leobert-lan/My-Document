package com.lht.cloudjob.activity.asyncprotected;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.others.ShowPhoneActivity;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.RoundImageView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.presenter.PersonalInfoActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IPersonalInfoActivity;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.string.StringUtil;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PersonalInfoActivity extends AsyncProtectedActivity implements IPersonalInfoActivity,
        View.OnClickListener {

    private static final String PAGENAME = "PersonalInfoActivity";

    private TitleBar mTitleBar;

    private ProgressBar mProgressBar;

    private LinearLayout llModifyAvatar, llNickname, llSex, llCellphone, llPwd;//

    /**
     * 完善行业信息入口
     */
    private LinearLayout llModifyField;

    private PersonalInfoActivityPresenter presenter;

    /**
     * the nickname in avatar section
     */
    private TextView tvNick;

    private TextView tvNickname;

    private TextView tvSex;

    private TextView tvPhone;

    private RoundImageView imgAvatar;
    private String username;

    public static final String KEY_DATA = "data_username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        username = getIntent().getStringExtra(KEY_DATA);
        EventBus.getDefault().register(this);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    protected String getPageName() {
        return PersonalInfoActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return PersonalInfoActivity.this;
    }

    @Override
    protected void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setDefaultOnBackListener(getActivity());
        mTitleBar.setTitle(R.string.title_activity_personalinfo);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        llModifyAvatar = (LinearLayout) findViewById(R.id.personalinfo_ll_avatar);
        llNickname = (LinearLayout) findViewById(R.id.personalinfo_ll_nickname);
        llSex = (LinearLayout) findViewById(R.id.personalinfo_ll_sex);
        llCellphone = (LinearLayout) findViewById(R.id.personalinfo_ll_cellphone);
        llPwd = (LinearLayout) findViewById(R.id.personalinfo_ll_loginpwd);
        llModifyField = (LinearLayout) findViewById(R.id.personalinfo_ll_modifyfield);

        tvNick = (TextView) findViewById(R.id.personalinfo_tv_nickname1);
        tvNickname = (TextView) findViewById(R.id.personalinfo_tv_nickname);
        tvPhone = (TextView) findViewById(R.id.personalinfo_tv_cellphone);
        tvSex = (TextView) findViewById(R.id.personalinfo_tv_sex);
        imgAvatar = (RoundImageView) findViewById(R.id.personalinfo_img_avatar);
    }

    @Override
    protected void initVariable() {
        presenter = new PersonalInfoActivityPresenter(this);
    }

    @Override
    protected void initEvent() {
        llModifyAvatar.setOnClickListener(this);
        llNickname.setOnClickListener(this);
        llSex.setOnClickListener(this);
        llCellphone.setOnClickListener(this);
        llPwd.setOnClickListener(this);
        llModifyField.setOnClickListener(this);
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ImageGetEvent event) {
        //处理照片获取并复制后的后续事件 压缩、显示...
        presenter.callResolveEvent(event);
    }

    @Override
    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personalinfo_ll_avatar:
                //统计 修改头像事件 - 计数
                reportCountEvent(IUmengEventKey.KEY_USER_UPDATE_AVATAR);
                presenter.callModifyAvatar();
                break;
            case R.id.personalinfo_ll_nickname:
                presenter.callModifyNickname();
                break;
            case R.id.personalinfo_ll_sex:
                presenter.callModifySex();
                break;
            case R.id.personalinfo_ll_modifyfield:
                jumpToFieldModify();
                break;
            case R.id.personalinfo_ll_cellphone:
                presenter.callBindPhone();
                break;
            case R.id.personalinfo_ll_loginpwd:
                presenter.callResetPwd();
                break;
        }
    }

    @Override
    public void showAvatarSelectActionsheet(String[] data, OnActionSheetItemClickListener
            listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.transparent();
        actionSheet.setOnDismissListener(dismissListener);
        actionSheet.show();
    }

    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            onActionSheetDismiss();
        }
    };

    @Override
    public void jumpToResetNickname() {
        Intent intent = new Intent(getActivity(), ResetNicknameActivity.class);
        intent.putExtra(ResetNicknameActivity.KEY_DATA, username);
        intent.putExtra(ResetNicknameActivity.KEY_DATA2, tvNickname.getText().toString());
        startActivity(intent);
    }

    @Override
    public void showSexSelectActionsheet(String[] data, OnActionSheetItemClickListener listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.transparent();
        actionSheet.show();
    }

    @Override
    public void showDialog(int contentResid, int positiveResid, CustomPopupWindow
            .OnPositiveClickListener onPositiveClickListener) {
        CustomDialog dialog = new CustomDialog(this);
        dialog.changeView2Single();
        dialog.setContent(contentResid);
        dialog.setPositiveButton(positiveResid);
        dialog.setPositiveClickListener(onPositiveClickListener);
        dialog.show();
    }

    @Override
    public void jumpToFieldModify() {
//        start(BindFieldActivity.class, BindFieldActivity.KEY_DATA, username); deprecated
        //统计 从个人资料进行订阅 -计数
        reportCountEvent(IUmengEventKey.KEY_SUBSCRIBE_ATMINE);

        Intent intent = new Intent(getActivity(), SubscribeActivity.class);
        intent.putExtra(SubscribeActivity.KEY_DATA, username);
        intent.putExtra(SubscribeActivity.KEY_PERSONALINFOIN, true);
        startActivity(intent);
    }


    @Override
    public void jumpToResetPwd() {
        start(ChangePwdActivity.class, ChangePwdActivity.KEY_DATA, username);
    }

    @Override
    public void updateView(BasicInfoResBean bean) {
        Picasso.with(getActivity()).load(bean.getAvatar())
                .placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default)
                .diskCache(BaseActivity.getLocalImageCache()).into(imgAvatar);
        tvNick.setText(bean.getNickname());
        tvNickname.setText(bean.getNickname());
        tvPhone.setText(bean.getMobile());
        int sexCode = bean.getSex();
        String sex;

        if (sexCode == 0) {
            sex = getString(R.string.v1010_default_personalinfo_privary);
        } else if (sexCode == 1) {
            sex = getString(R.string.v1010_default_personalinfo_man);
        } else {
            sex = getString(R.string.v1010_default_personalinfo_woman);
        }
        tvSex.setText(sex);
    }

    @Override
    public void showErrorMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 绑定手机页面
     *
     * @param basicInfoResBean 基础信息，可能用到
     */
    @Override
    public void jumpToBindPhone(BasicInfoResBean basicInfoResBean) {
        String _data = JSON.toJSONString(basicInfoResBean);
        start(BindPhoneActivity.class, BindPhoneActivity.KEY_DATA, _data);
    }


    /**
     * 显示页面
     *
     * @param basicInfoResBean 基础信息
     */
    @Override
    public void jumpToShowPhone(BasicInfoResBean basicInfoResBean) {
        String _data = JSON.toJSONString(basicInfoResBean);
        start(ShowPhoneActivity.class, ShowPhoneActivity.KEY_DATA, _data);
    }

    private boolean resumeOnActionSheetDismiss = false;

    @Override
    public void onActionSheetDismiss() {
        resumeOnActionSheetDismiss = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeOnActionSheetDismiss) {
            resumeOnActionSheetDismiss = false;
            return;
        }
//        Log.d("lmsg","personal info onresume");
        presenter.init(username);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PersonalInfoActivityPresenter.INTENT_CODE_ALBUM:
                if (resultCode == RESULT_OK) {
                    String path = FileUtils.queryImageByUri(data.getData(), getContentResolver());
                    if (!StringUtil.isEmpty(path)) {
                        presenter.callTransAlbum(path);
                    } else {
                        ToastUtils.show(getActivity(), R.string
                                .v1010_toast_error_album_private, ToastUtils.Duration.s);
                    }
                } else {
                    //相册 未获取图片
                }
                break;
            case PersonalInfoActivityPresenter.INTENT_CODE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    //相机 获取图片并进一步处理
                    presenter.callTransCapture();
                } else {
                    //相机 未获取图片
                }
                break;
            default:
                break;
        }
    }
}
