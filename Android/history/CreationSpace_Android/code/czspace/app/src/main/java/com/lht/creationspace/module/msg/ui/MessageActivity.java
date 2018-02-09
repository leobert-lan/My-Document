package com.lht.creationspace.module.msg.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme7;
import com.lht.creationspace.module.msg.MessageActivityPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import individual.leobert.uilib.numbadge.NumBadge;


/**
 * 消息总列表页，暂未使用,todo title
 */
public class MessageActivity extends AsyncProtectedActivity implements View.OnClickListener, IMessageActivity, IVerifyHolder {
    private static final String PAGENAME = "MsgActivity";
    private ProgressBar progressBar;
    private ToolbarTheme7 titleBar;
    private NumBadge nbCommentCount;
    private NumBadge nbNotifyCount;
    private RelativeLayout rlComment;
    private RelativeLayout rlRemind;
    private RelativeLayout rlNotify;
    private MessageActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_msg);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return MessageActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return MessageActivity.this;
    }

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        titleBar = (ToolbarTheme7) findViewById(R.id.titlebar);
        nbCommentCount = (NumBadge) findViewById(R.id.numbadge_comment_count);
        nbNotifyCount = (NumBadge) findViewById(R.id.numbadge_notify_count);
        rlComment = (RelativeLayout) findViewById(R.id.rl_comment);
        rlRemind = (RelativeLayout) findViewById(R.id.rl_remind);
        rlNotify = (RelativeLayout) findViewById(R.id.rl_notify);
    }

    @Override
    protected void initVariable() {
        presenter = new MessageActivityPresenter(this);
//        presenter.setLoginStatus(!StringUtil.isEmpty(mLoginInfo.getUsername()));
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(this);
        titleBar.setOpTextColor(R.color.main_green_dark);
        titleBar.setTitle(R.string.v1000_title_activity_message);
        titleBar.setOpText(R.string.v1000_default_message_text_send_message);
        titleBar.setOpOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg("发消息");
            }
        });

        setSupportActionBar(titleBar);

        nbCommentCount.updateWithFriendlyMode(123, 99);
        nbNotifyCount.updateWithFriendlyMode(123, 99);

        rlComment.setOnClickListener(this);
        rlRemind.setOnClickListener(this);
        rlNotify.setOnClickListener(this);
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_comment:
                if (hasLogin()) {
                    jump2CommentListActivity();
                } else {
                    presenter.callCommentListLogin();
                }
                break;
            case R.id.rl_remind:
                if (hasLogin()) {
                    jump2RemindListActivity();
                } else {
                    presenter.callRemindLogin();
                }
                break;
            case R.id.rl_notify:
                if (hasLogin()) {
                    jump2NotificationListActivity();
                } else {
                    presenter.callNotificationLogin();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void jump2CommentListActivity() {
        HybridMentionMeListActivity.getLauncher(this).launch();
    }

    @Override
    public void jump2RemindListActivity() {
        HybridRemindListActivity.getLauncher(this).launch();
    }

    @Override
    public void jump2NotificationListActivity() {
        HybridNotificationListActivity.getLauncher(this).launch();
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {

    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        mLoginInfo.copy(event.getLoginInfo());
//        presenter.setLoginStatus(true);
        presenter.identifyTrigger(event.getTrigger());
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static final class Launcher extends AbsActivityLauncher<Void> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, MessageActivity.class);
        }

        @Override
        public AbsActivityLauncher<Void> injectData(Void data) {
            return Launcher.this;
        }
    }
}
