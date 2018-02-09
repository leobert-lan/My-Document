package com.lht.cloudjob.clazz;

import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.clazz.customerror.BadgeExceptionError;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.bean.DesktopBadgeNumBean;
import com.lht.cloudjob.mvp.model.bean.JpushMessage;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;
import com.litesuits.orm.LiteOrm;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BadgeNumberManager
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/12/7.
 */

public class BadgeNumberManager implements IVerifyHolder, DbCURDModel.IDbCURD<DesktopBadgeNumBean> {
    private static int numSystemNotify;
    private static int numVsoActivityNotify;
    private static BadgeNumChangeType badgeNumChangeType = BadgeNumChangeType.Unknown;

    private LiteOrm liteOrm;

    private DbCURDModel<DesktopBadgeNumBean> dbCURDModel;

    public enum BadgeNumChangeType {
        Unknown,

        /**
         * 需要请求接口同步数据
         */
        VsoSysNum,

        /**
         * 需要根据通知内容更新界面
         * 不需要请求接口
         */
        VsoAcNum,

        /**
         * 请求接口同步数据时发出
         * 不需要再次请求接口，会死循环的
         */
        VsoSysNumReset,

        /**
         * 备用
         */
        VsoAcNumReset;

        private JpushMessage.MessageExtra extra;

        public JpushMessage.MessageExtra getExtra() {
            return extra;
        }

        public void setExtra(JpushMessage.MessageExtra extra) {
            this.extra = extra;
        }
    }

    public static class BadgeNumberChangedEvent {
        private final int numSystemNotify;
        private final int numVsoActivityNotify;

        private final BadgeNumChangeType changeType;

        public BadgeNumberChangedEvent(int numSystemNotify, int numVsoActivityNotify, BadgeNumChangeType type) {
            this.numSystemNotify = numSystemNotify;
            this.numVsoActivityNotify = numVsoActivityNotify;
            this.changeType = type;
        }

        public int getNumSystemNotify() {
            return numSystemNotify;
        }

        public int getNumVsoActivityNotify() {
            return numVsoActivityNotify;
        }

        public BadgeNumChangeType getChangeType() {
            return changeType;
        }

        public int getDesktopBadgeNumber() {
            if (numSystemNotify > 0) {
                return numSystemNotify;
            } else if (numVsoActivityNotify > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private static final String TAG = "BadgeNumberManager";

    private BadgeNumberManager() {
        liteOrm = LiteOrm.newSingleInstance(MainApplication.getOurInstance(),
                DBConfig.BadgeNumberDb.DB_NAME);
        dbCURDModel = new DbCURDModel<>(this);
        EventBus.getDefault().register(this);
    }

    /**
     * init in start
     */
    private static BadgeNumberManager instance;

    public static void initOnApplicationStart() {
        instance = new BadgeNumberManager();
    }

    public static BadgeNumberManager getInstance() {
        if (instance == null) {
            instance = new BadgeNumberManager();
        }
        return instance;
    }

    /**
     * 收到系统消息类通知时调用，
     *
     * @param i 推送通知一般单条抵达
     */
    public synchronized void addSystemNotify(int i) {
        synchronized (this) {
            numSystemNotify += i;
            badgeNumChangeType = BadgeNumChangeType.VsoSysNum;
            notifyChanges();
        }
    }

    /**
     * 重新拉取系统消息后调用，同步数据
     *
     * @param count
     */
    public synchronized void resetSystemNotifyCount(int count) {
        synchronized (this) {
            numSystemNotify = count;
            badgeNumChangeType = BadgeNumChangeType.VsoSysNumReset;
            notifyChanges();
        }
    }

    /**
     * 阅读了系统通知调用，点击具体的未读的系统通知条目 或 批量操作时，注意批量操作时甄别已读未读
     *
     * @param i
     */
    public synchronized void removeSystemNotify(int i) {
        synchronized (this) {
            numSystemNotify -= i;
            badgeNumChangeType = BadgeNumChangeType.VsoSysNum;
            notifyChanges();
        }
    }

    /**
     * 收到vso活动通知调用
     */
    public synchronized void addVsoActivityNotify(JpushMessage.MessageExtra extra) {
        synchronized (this) {
            numVsoActivityNotify += 1;
            badgeNumChangeType = BadgeNumChangeType.VsoAcNum;
            badgeNumChangeType.setExtra(extra);
            notifyChangesToAll();//no need to save db
        }
    }

    /**
     * 一旦阅读通知活动即可调用
     */
    public synchronized void resetVsoActivityNotify() {
        synchronized (this) {
            numVsoActivityNotify = 0;
            badgeNumChangeType = BadgeNumChangeType.VsoAcNumReset;
            notifyChangesToAll();//no need to save db
        }
    }

    /**
     * save changes to db and notifyChangesToAll
     */
    private void notifyChanges() {
        saveChanges2db();
        notifyChangesToAll();
    }

    private void saveChanges2db() {
        DesktopBadgeNumBean bean = new DesktopBadgeNumBean();
        bean.setUserName(getUsername());
        bean.setNumSystemNotify(numSystemNotify);
        liteOrm.save(bean);
    }

    public synchronized void notifyChangesToAll() {
        BadgeNumberChangedEvent event =
                new BadgeNumberChangedEvent(numSystemNotify, numVsoActivityNotify, badgeNumChangeType);
        EventBus.getDefault().postSticky(event);

        DLog.i(Lmsg.class, "call update desktop:" + event.getDesktopBadgeNumber());

        //修改桌面角标
        try {
            int desktopBadgeNum = event.getDesktopBadgeNumber();
            if (desktopBadgeNum > 0) {
                ShortcutBadger.applyCount(MainApplication.getOurInstance(), desktopBadgeNum);
            } else {
                ShortcutBadger.removeCount(MainApplication.getOurInstance());
            }
        } catch (Exception e) {
            BadgeExceptionError error = new BadgeExceptionError(e);
            error.report();
        }
    }


    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        if (dbCURDModel != null) {
            dbCURDModel.doRequest();
        }
    }

    @Override
    @Subscribe
    public void onEventMainThread(AppEvent.LoginCancelEvent event) {
        //ignore
    }

    @Subscribe
    public void onEventMainThread(AppEvent.LogoutEvent event) {
        //全部清除即可，有新通知由新通知触发事件维护
        synchronized (this) {
            numVsoActivityNotify = 0;
            numSystemNotify = 0;
            badgeNumChangeType = BadgeNumChangeType.Unknown;
            notifyChangesToAll();
        }
    }

    @Override
    public LoginInfo getLoginInfo() {
        return mLoginInfo;
    }

    private String getUsername() {
        String username = getLoginInfo().getUsername();
        if (StringUtil.isEmpty(username)) {
            username = "default";
        }
        return username;
    }

    @Override
    public DesktopBadgeNumBean doCURDRequest() {
        return liteOrm.queryById(getUsername(), DesktopBadgeNumBean.class);
    }

    @Override
    public void onCURDFinish(DesktopBadgeNumBean desktopBadgeNumBean) {
        synchronized (this) {
            if (desktopBadgeNumBean == null) {
                desktopBadgeNumBean = new DesktopBadgeNumBean();
            }
            numSystemNotify = desktopBadgeNumBean.getNumSystemNotify();
            badgeNumChangeType = BadgeNumChangeType.VsoSysNumReset;
            notifyChangesToAll();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        DLog.d(Lmsg.class, "badge manager finalize");
        super.finalize();
    }
}
