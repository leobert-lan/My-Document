package com.lht.creationspace.module.msg;

import android.content.Context;
import android.content.Intent;

import com.lht.creationspace.base.launcher.LoginIntentFactory;
import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.msg.ui.IMessageActivity;
import com.lht.creationspace.base.presenter.ABSVerifyNeedPresenter;
import com.lht.creationspace.util.internet.HttpUtil;

import java.io.Serializable;

/**
 * Created by chhyu on 2017/3/3.
 */

public class MessageActivityPresenter extends ABSVerifyNeedPresenter implements IApiRequestPresenter {
    private IMessageActivity iMessageActivity;

    public MessageActivityPresenter(IMessageActivity iMessageActivity) {
        this.iMessageActivity = iMessageActivity;
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    public void callCommentListLogin() {
        Intent intent = LoginIntentFactory.create(iMessageActivity.getActivity(), LoginTrigger.CommentList);
        iMessageActivity.getActivity().startActivity(intent);
    }

    public void callRemindLogin() {
        Intent intent = LoginIntentFactory.create(iMessageActivity.getActivity(), LoginTrigger.Remind);
        iMessageActivity.getActivity().startActivity(intent);
    }

    public void callNotificationLogin() {
        Intent intent = LoginIntentFactory.create(iMessageActivity.getActivity(), LoginTrigger.Notification);
        iMessageActivity.getActivity().startActivity(intent);
    }

    @Override
    public void identifyTrigger(ITriggerCompare trigger) {
        if (trigger.equals(LoginTrigger.CommentList)) {
            iMessageActivity.jump2CommentListActivity();
        } else if (trigger.equals(LoginTrigger.Remind)) {
            iMessageActivity.jump2RemindListActivity();
        } else if (trigger.equals(LoginTrigger.Notification)) {
            iMessageActivity.jump2NotificationListActivity();
        }
    }


    public enum LoginTrigger implements ITriggerCompare {
        CommentList(1), Remind(2), Notification(3);

        private final int tag;

        LoginTrigger(int i) {
            tag = i;
        }

        @Override
        public boolean equals(ITriggerCompare compare) {
            if (compare == null) {
                return false;
            }
            boolean b1 = compare.getClass().getName().equals(getClass().getName());
            boolean b2 = compare.getTag().equals(getTag());
            return b1 & b2;
        }

        @Override
        public Object getTag() {
            return tag;
        }

        @Override
        public Serializable getSerializable() {
            return this;
        }

    }
}
