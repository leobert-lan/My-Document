package com.lht.creationspace.module.home.ui.fg;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.fragment.BaseFragment;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.vinterface.IAsyncProtectedFragment;
import com.lht.creationspace.customview.DrawableCenterTextView;
import com.lht.creationspace.customview.RoundImageView;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.customview.share.TPSPWCreator;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.customview.toolBar.msg.ToolbarTheme4;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.module.home.FgHomeMinePresenter;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;
import com.lht.creationspace.module.home.ui.IFgHomeMine;
import com.lht.creationspace.module.msg.ui.MessageActivity;
import com.lht.creationspace.module.setting.ui.SettingActivity;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.module.user.info.ui.ac.AccountManageActivity;
import com.lht.creationspace.module.user.info.ui.ac.HybridUCenterActivity;
import com.lht.creationspace.module.user.info.ui.ac.PersonalInfoActivity;
import com.lht.creationspace.module.user.social.HybridMyArticleActivity;
import com.lht.creationspace.module.user.social.HybridMyAttentionActivity;
import com.lht.creationspace.module.user.social.HybridMyCircleActivity;
import com.lht.creationspace.module.user.social.HybridMyCollectionActivity;
import com.lht.creationspace.module.user.social.HybridMyFansActivity;
import com.lht.creationspace.module.user.social.HybridMyProjectActivity;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.creationspace.util.ui.PressEffectUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import individual.leobert.uilib.numbadge.NumBadge;

import static com.lht.creationspace.base.IVerifyHolder.mLoginInfo;

/**
 * tab mine
 * Created by chhyu on 2017/2/14.
 */
public class FgHomeMine extends BaseFragment implements IAsyncProtectedFragment,
        View.OnClickListener, IFgHomeMine {

    private static final String PAGENAME = "FgHomeMine";
    /**
     * 我的项目
     */
    private DrawableCenterTextView viewMyProject;
    /**
     * 我的文章
     */
    private DrawableCenterTextView viewMyArticle;
    /**
     * 我的圈子
     */
    private DrawableCenterTextView viewMyCircle;
    /**
     * 账号管理
     */
    private DrawableCenterTextView viewAccountManage;
    /**
     * 设置
     */
    private DrawableCenterTextView viewSetting;
    /**
     * 邀请好友
     */
    private DrawableCenterTextView viewInviteFriend;
    private LinearLayout llAttention;
    private LinearLayout llFans;
    private LinearLayout llCollection;
    private FgHomeMinePresenter presenter;
    private RoundImageView rlvAvatar;
    private TextView tvUserNickname;
    private TextView tvUserIntroduction;
    private TextView tvAttentionNum;
    private TextView tvFansNum;
    private TextView tvCollectionNum;
    private RelativeLayout rlHeader;

    private AsyncProtectedActivity parent;

    private ToolbarTheme4 titleBar;
    private NumBadge unreadMsgBadge;

//    private ITabNotifyHandle iTabNotifyHandle;

//    public interface ITabNotifyHandle {
//        /**
//         * 显示小红点
//         */
//        void showDotNotify();
//
//        /**
//         * 显示数值类型提示
//         *
//         * @param num 提示数值
//         */
//        void showNumberNotify(int num);
//
//        /**
//         * 隐藏提示内容
//         */
//        void hideNotify();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        presenter.onFinish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fg_home_mine, container, false);
        initView(contentView);
        initVariable();
        initEvent();
            parent = (AsyncProtectedActivity) getActivity();
//        try {
//            iTabNotifyHandle = ((IHomeActivity) parent).getMineTabNotifyHandle();
//            if (iTabNotifyHandle == null) {
//                throw new IllegalStateException("the parent cannot return null at getMineTabNotifyHandle");
//            }
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//            DLog.e(getClass(), "the host activity must extends AsyncProtectedActivity ");
//        }
        return contentView;
    }


    @Override
    protected void initView(View contentView) {
        titleBar = (ToolbarTheme4) contentView.findViewById(R.id.titlebar);
        rlvAvatar = (RoundImageView) contentView.findViewById(R.id.rlv_avatar);
        tvUserNickname = (TextView) contentView.findViewById(R.id.tv_user_nickname);
        tvUserIntroduction = (TextView) contentView.findViewById(R.id.tv_user_introduction);
        tvAttentionNum = (TextView) contentView.findViewById(R.id.tv_attention_num);
        tvFansNum = (TextView) contentView.findViewById(R.id.tv_fans_num);
        tvCollectionNum = (TextView) contentView.findViewById(R.id.tv_collection_num);

        llAttention = (LinearLayout) contentView.findViewById(R.id.ll_attention);
        llFans = (LinearLayout) contentView.findViewById(R.id.ll_fans);
        llCollection = (LinearLayout) contentView.findViewById(R.id.ll_collection);

        viewMyProject = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_my_project);
        viewMyArticle = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_my_article);
        viewMyCircle = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_my_circle);
        viewAccountManage = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_account_manage);
        viewSetting = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_setting);
        viewInviteFriend = (DrawableCenterTextView) contentView.findViewById(R.id.fgmine_invite_friend);

        rlHeader = (RelativeLayout) contentView.findViewById(R.id.rl_header);
        unreadMsgBadge = (NumBadge) contentView.findViewById(R.id.unread_message_badge);

    }


    @Override
    protected void initVariable() {
        presenter = new FgHomeMinePresenter(this);
    }

    @Override
    protected void initEvent() {
        titleBar.setRightImageDrawable(R.drawable.v1000_drawable_grzybj);
        titleBar.hideTitleBottomDividerLine();

        PressEffectUtils.bindDefaultPressEffect(llAttention);
        PressEffectUtils.bindDefaultPressEffect(llFans);
        PressEffectUtils.bindDefaultPressEffect(llCollection);

        PressEffectUtils.bindDefaultPressEffect(viewMyProject);
        PressEffectUtils.bindDefaultPressEffect(viewMyArticle);
        PressEffectUtils.bindDefaultPressEffect(viewMyCircle);
        PressEffectUtils.bindDefaultPressEffect(viewAccountManage);
        PressEffectUtils.bindDefaultPressEffect(viewSetting);
        PressEffectUtils.bindDefaultPressEffect(viewInviteFriend);

        llAttention.setOnClickListener(this);
        llFans.setOnClickListener(this);
        llCollection.setOnClickListener(this);

        viewMyProject.setOnClickListener(this);
        viewMyArticle.setOnClickListener(this);
        viewMyCircle.setOnClickListener(this);
        viewAccountManage.setOnClickListener(this);
        viewSetting.setOnClickListener(this);
        viewInviteFriend.setOnClickListener(this);
        rlHeader.setOnClickListener(this);

        titleBar.setOnRightImageClickListener(new ICallback() {
            @Override
            public void onCallback() {
                //修改个人信息
                checkLoginState(PersonalInfoActivity.class, FgHomeMinePresenter.LoginTrigger.PersonalInfo);
            }
        });

        titleBar.setOnNavMessageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.getLauncher(getActivity()).launch();
            }
        });

        if (hasLogin()) {
            setUi2LoginState(mLoginInfo);
        } else {
            setUi2UnLoginState();
        }

        presenter.callGetBrief();
        presenter.callGetSocialInfo();
    }

    @Override
    public void onRestrictResume() {
        super.onRestrictResume();
//        Log.e("lmsg", getPageName() + " onRestrictResume");
//        if (hasLogin())
        presenter.callGetSocialInfo(); //包含未登录逻辑
    }

    @Override
    protected void setUi2LoginState(LoginInfo loginInfo) {
        super.setUi2LoginState(loginInfo);
//        Picasso.with(getActivity()).load(loginInfo.getAvatar())
//                .diskCache(getLocalThumbnailCacheDir())
//                .placeholder(R.drawable.v1000_drawable_avatar_default)
//                .error(R.drawable.v1000_drawable_avatar_default)
//                .into(rlvAvatar);
        Glide.with(getActivity()).load(loginInfo.getAvatar())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.v1000_drawable_avatar_default)
                .error(R.drawable.v1000_drawable_avatar_default)
                .into(rlvAvatar);
        String nickname = StringUtil.replaceEnter(loginInfo.getNickname());
        tvUserNickname.setText(nickname);
    }

    @Override
    protected void setUi2UnLoginState() {
        super.setUi2UnLoginState();
        rlvAvatar.setImageResource(R.drawable.v1000_drawable_avatar_default);
        tvUserNickname.setText("点击登录");
        tvUserIntroduction.setText(null);
        tvAttentionNum.setText("0");
        tvFansNum.setText("0");
        tvCollectionNum.setText("0");

    }


    @Override
    @Subscribe(sticky = true)
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        super.onEventMainThread(event);
        mLoginInfo.copy(event.getLoginInfo());
        presenter.identifyTrigger(event.getTrigger());
        presenter.callGetBrief();
        presenter.callGetSocialInfo();
    }

    @Subscribe
    public void onEventMainThread(AppEvent.UserInfoUpdatedEvent event) {
        BasicInfoResBean bean = event.getBasicInfoResBean();
        if (bean != null) {
//            Picasso.with(getActivity()).load(bean.getAvatar())
//                    .diskCache(getLocalThumbnailCacheDir())
//                    .placeholder(R.drawable.v1000_drawable_avatar_default)
//                    .error(R.drawable.v1000_drawable_avatar_default)
//                    .into(rlvAvatar);
            Glide.with(getActivity()).load(bean.getAvatar())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.v1000_drawable_avatar_default)
                    .error(R.drawable.v1000_drawable_avatar_default)
                    .into(rlvAvatar);
        }
        tvUserNickname.setText(bean.getNickname());

        if (event.isHasBriefSet())
            tvUserIntroduction.setText(StringUtil.replaceEnter(event.getBrief()));
    }

    @Subscribe
    public void onEventMainThread(AppEvent.BriefSetEvent event) {
        tvUserIntroduction.setText(StringUtil.replaceEnter(event.getBrief()));
    }

    @Subscribe
    public void onEventMainThread(AppEvent.ModifyNicknameEvent event) {
        tvUserNickname.setText(event.getNickname());
    }

    @Override
    protected String getPageName() {
        return FgHomeMine.PAGENAME;
    }

    @Override
    public void showWaitView(boolean isProtectNeed) {
        parent.showWaitView(isProtectNeed);
    }

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_attention:
                //关注
                checkLoginState(HybridMyAttentionActivity.class, FgHomeMinePresenter.LoginTrigger.Attention);
                break;
            case R.id.ll_fans:
                //粉丝
                checkLoginState(HybridMyFansActivity.class, FgHomeMinePresenter.LoginTrigger.Fans);
                break;
            case R.id.ll_collection:
                //收藏
                checkLoginState(HybridMyCollectionActivity.class, FgHomeMinePresenter.LoginTrigger.Collection);
                break;
            case R.id.fgmine_my_project:
                //我的项目
                checkLoginState(HybridMyProjectActivity.class, FgHomeMinePresenter.LoginTrigger.MyProject);
                break;
            case R.id.fgmine_my_article:
                //我的文章
                checkLoginState(HybridMyArticleActivity.class, FgHomeMinePresenter.LoginTrigger.MyArticle);
                break;
            case R.id.fgmine_my_circle:
                //我的圈子
                checkLoginState(HybridMyCircleActivity.class, FgHomeMinePresenter.LoginTrigger.MyTopic);
                break;
            case R.id.fgmine_account_manage:
                if (hasLogin()) {
                    AccountManageActivity.getLauncher(getActivity()).launch();
                } else {
                    presenter.callLogin(FgHomeMinePresenter.LoginTrigger.AccountManage);
                }
//                checkLoginState(AccountManageActivity.class, FgHomeMinePresenter.LoginTrigger.AccountManage);
                break;
            case R.id.fgmine_setting:
                SettingActivity.getLauncher(getActivity()).launch();
                break;
            case R.id.fgmine_invite_friend:
                //邀请好友
                presenter.callShare();
                break;
            case R.id.rl_header:
                if (!hasLogin())
                    presenter.callLogin(FgHomeMinePresenter.LoginTrigger.Login);
                else {
                    jump2UCenter(IVerifyHolder.mLoginInfo.getUsername());
                }
                break;
            default:
                break;
        }
    }

    private void jump2UCenter(String username) {
        HybridUCenterActivity.getLauncher(getActivity())
                .injectData(transData(new IHybridPagesCollection.HybridUserSpace().getPageUrlWithQueryString(username), username))
                .launch();
    }

    private HybridUCenterActivity.HybridUCenterActivityData transData(String pageUrlWithQueryString, String username) {
        HybridUCenterActivity.HybridUCenterActivityData data = new HybridUCenterActivity.HybridUCenterActivityData();
        data.setUrl(pageUrlWithQueryString);
        data.setTargetUser(username);
        return data;
    }

    private void checkLoginState(Class c, FgHomeMinePresenter.LoginTrigger loginTrigger) {
        if (hasLogin()) {
            Intent intent = new Intent(getActivity(), c);
            startActivity(intent);
        } else {
            presenter.callLogin(loginTrigger);
        }
    }

    @Override
    public void showSharePopwins(ThirdPartySharePopWins.ShareData shareData) {
        ThirdPartySharePopWins wins =
                TPSPWCreator.create((IPopupHolder) getActivity(), shareData);
        wins.show();
    }

    @Override
    public void updateSocialInfo(QuerySocialInfoModel.ModelResBean bean) {
        tvAttentionNum.setText(bean.getSubscribe_count());
        tvFansNum.setText(bean.getFans_count());
        tvCollectionNum.setText(bean.getFavorite_count());

        if (bean.isHas_unread_favorite()) {
//            iTabNotifyHandle.showDotNotify();
            unreadMsgBadge.updateWithPointMode();
        } else {
            unreadMsgBadge.clear();
//            iTabNotifyHandle.hideNotify();
        }
    }
}
