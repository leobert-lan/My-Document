package com.lht.cloudjob.Event;

import com.lht.cloudjob.interfaces.ITriggerCompare;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.cloudjob.util.string.StringUtil;

/**
 * <p><b>Package</b> com.lht.cloudjob.Event
 * <p><b>Project</b> Chuangyiyun
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

        public UserInfoUpdatedEvent(BasicInfoResBean basicInfoResBean) {
            this.basicInfoResBean = basicInfoResBean;
        }

        public BasicInfoResBean getBasicInfoResBean() {
            return basicInfoResBean;
        }
    }

    public static class LogoutEvent {
    }

    /**
     * 设置标签成功事件
     */
    public static class LabelSetEvent {
    }

    /**
     * 绑定手机成功事件
     */
    public static class PhoneBindEvent {
    }

    /**
     * 重新企业认证
     */
    public static class ReEAuthEvent {
    }

    /**
     * 重新实名认证
     */
    public static class RePAuthEvent {
    }

    public static class LocationPickedEvent {
        private String province;
        private String city;
        private String area;

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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public boolean isEmpty() {
            return isEmpty(province) && isEmpty(city) && isEmpty(area);
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

    public static class SearchCloseEvent {
    }

    /**
     * 承接需求成功事件
     */
    public static class UnderTakeSuccessEvent {
    }

    /**
     * 完成协议签署事件
     */
    public static class ProtocolSignOnCompleteEvent {
        /**
         * 是否同意、预留
         */
        private final boolean isAgree;

        public ProtocolSignOnCompleteEvent() {
            this(false);
        }

        public ProtocolSignOnCompleteEvent(boolean isAgree) {
            this.isAgree = isAgree;
        }

        public boolean isAgree() {
            return isAgree;
        }
    }

    public static class ChooseCategoryEvent {
        private static CategoryResBean categoryResBean;

        public ChooseCategoryEvent(CategoryResBean categoryResBean) {
            this.categoryResBean = categoryResBean;
        }

        public static CategoryResBean getCategoryData(){
            return categoryResBean;
        }
    }
}
