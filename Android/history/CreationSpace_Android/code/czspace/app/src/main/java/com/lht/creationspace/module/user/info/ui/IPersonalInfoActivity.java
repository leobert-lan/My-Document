package com.lht.creationspace.module.user.info.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.module.user.info.model.pojo.BasicInfoResBean;

import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IPersonalInfoActivity
 * <p><b>Description</b>: 个人信息页面接口
 * Created by leobert on 2016/7/11.
 */
public interface IPersonalInfoActivity extends IActivityAsyncProtected {

    /**
     * 跳转到修改昵称页面
     */
    void jumpToModifyNickname();

    /**
     * 显示性别选择操作表
     *
     * @param data
     * @param listener
     */
    void showSexSelectActionsheet(String[] data, OnActionSheetItemClickListener listener);

    void showDialog(int contentResid, int positiveResid, CustomPopupWindow.OnPositiveClickListener
            onPositiveClickListener);

    /**
     * 显示错误信息
     *
     * @param msg 错误信息
     */
    void showErrorMsg(String msg);

    /**
     * 更新当前字数
     *
     * @param edittextId
     * @param currentCount
     * @param remains
     */
    void updateCurrentnum(int edittextId, int currentCount, int remains);

    /**
     * 更新用户信息
     *
     * @param data
     */
    void updateUserInfo(BasicInfoResBean data);

    void loadAvatar(String path);

    /**
     * @param isEnabled true display complete,else hide
     */
    void setIntroCompleteEnable(boolean isEnabled);

    void updateUserIntroduce(String introduce);

    String getIntro();
}
