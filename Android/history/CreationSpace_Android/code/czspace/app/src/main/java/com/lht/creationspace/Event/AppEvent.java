package com.lht.creationspace.Event;

import com.lht.creationspace.base.launcher.ITriggerCompare;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;
import com.lht.creationspace.module.proj.model.pojo.ProjectStateResBean;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.base.model.pojo.LoginInfo;
import com.lht.creationspace.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.vsocyy.Event
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> AppEvent
 * <p><b>Description</b>: 使用静态内部类管理所有的event事件
 * 本次项目中，eventbus仅用做回调的补充，在不便于使用回调的情况下使用eventbus
 * Created by leobert on 2016/5/5.
 */
public class AppEvent {

    public static class TriggerHolderEvent<T extends ITriggerCompare> {
        protected T trigger;

        public T getTrigger() {
            return trigger;
        }

        public void setTrigger(T trigger) {
            this.trigger = trigger;
        }
    }

    /**
     * 登录成功事件
     */
    public static class LoginSuccessEvent extends TriggerHolderEvent {
        private LoginInfo loginInfo;

        public LoginSuccessEvent(LoginInfo loginInfo) {
            this.loginInfo = loginInfo;
        }

        public LoginInfo getLoginInfo() {
            return loginInfo;
        }
    }

    public static class RegisterBackgroundLoginSuccessEvent {
        private LoginInfo loginInfo;

        public RegisterBackgroundLoginSuccessEvent(LoginInfo loginInfo) {
            this.loginInfo = loginInfo;
        }

        public LoginInfo getLoginInfo() {
            return loginInfo;
        }
    }

    /**
     * 三方快速绑定成功静默登录成功事件
     */
    public static class TpRegSilentLoginSuccessEvent {
        private LoginInfo loginInfo;

        public TpRegSilentLoginSuccessEvent(LoginInfo loginInfo) {
            this.loginInfo = loginInfo;
        }

        public LoginInfo getLoginInfo() {
            return loginInfo;
        }
    }

    /**
     * 手动关闭登录页事件
     */
    public static class LoginCancelEvent extends TriggerHolderEvent {
    }

    /**
     * 正常登录事件链被打断事件
     */
    public static class LoginInterruptedEvent {
    }

    /**
     * 用户信息更新事件
     * 用户信息编辑后查询成功发出该事件
     */
    public static class UserInfoUpdatedEvent {
        private final BasicInfoResBean basicInfoResBean;

        private boolean hasBriefSet = false;

        private String brief;

        public UserInfoUpdatedEvent(BasicInfoResBean basicInfoResBean) {
            this.basicInfoResBean = basicInfoResBean;
        }

        public BasicInfoResBean getBasicInfoResBean() {
            return basicInfoResBean;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            hasBriefSet = true;
            this.brief = brief;
        }

        public boolean isHasBriefSet() {
            return hasBriefSet;
        }
    }

    public static class LogoutEvent {
    }


    /**
     * 绑定手机成功事件
     */
    public static class PhoneBindEvent {
    }


    public static class LocationPickedEvent {
        private String province;
        private String city;
//        private String area;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

//        public String getArea() {
//            return area;
//        }
//
//        public void setArea(String area) {
//            this.area = area;
//        }

        public boolean isEmpty() {
            return isEmpty(province) && isEmpty(city);// && isEmpty(area);
        }

        private boolean isEmpty(String s) {
            return StringUtil.isEmpty(s);
        }
    }

    /**
     * 嵌套滑动布局的内容顶部到底事件
     * 可能需要扩展view来判断
     */
    public static class NestedContentScrollEvent {
        private final boolean isTopArrived;

        public NestedContentScrollEvent(boolean isTopArrived) {
            this.isTopArrived = isTopArrived;
        }

        public boolean isTopArrived() {
            return isTopArrived;
        }
    }

    public static class HomeTabDisplayEvent {
        private final boolean isShown;

        public HomeTabDisplayEvent(boolean isShown) {
            this.isShown = isShown;
        }

        public boolean isShown() {
            return isShown;
        }
    }

    public static class ImageGetEvent extends TriggerHolderEvent {
        private String path;

        private boolean isSuccess = false;

        public ImageGetEvent(String path, boolean isSuccess) {
            this.path = path;
            this.isSuccess = isSuccess;
        }

        public String getPath() {
            return path;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    public static class PwdResettedEvent {
    }


    /**
     * 三方流程中自动设置登录account的事件
     */
    public static class AuthSetAccountEvent {
        private final String account;

        public AuthSetAccountEvent(String account) {
            this.account = account;
        }

        public String getAccount() {
            return account;
        }
    }

//    /**
//     * 只返回父类型（子类型为null的时候）
//     */
//    public static class SetOnlyParentProjectTypeEvent {
//        private ProjectTypeResBean bean;
//
//        public ProjectTypeResBean getBean() {
//            return bean;
//        }
//
//        public void setBean(ProjectTypeResBean bean) {
//            this.bean = bean;
//        }
//    }

    /**
     * 返回父类型 + 子类型
     */
    public static class SetProjectTypeEvent {
        private ProjectTypeResBean bean;
        private ProjectTypeResBean.ProjectChildTypeResBean childTypeResBean;

        public ProjectTypeResBean getBean() {
            return bean;
        }

        public ProjectTypeResBean.ProjectChildTypeResBean getChildTypeResBean() {
            return childTypeResBean;
        }

        public void setBean(ProjectTypeResBean bean, ProjectTypeResBean.ProjectChildTypeResBean childTypeResBean) {
            this.bean = bean;
            this.childTypeResBean = childTypeResBean;
        }
    }

    /**
     * 只返回父类型（子类型为null的时候）
     */
    public static class SetProjectStateEvent {
        private ProjectStateResBean bean;

        public ProjectStateResBean getBean() {
            return bean;
        }

        public void setBean(ProjectStateResBean bean) {
            this.bean = bean;
        }
    }

    /**
     * 只返回父类型（子类型为null的时候）
     */
    public static class ModifyNicknameEvent {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class ArticlePublishSuccessEvent {
    }

    public static class BriefSetEvent {
        private final String brief;

        public BriefSetEvent(String brief) {
            this.brief = brief;
        }

        public String getBrief() {
            return brief;
        }
    }

}
