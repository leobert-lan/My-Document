package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.mvp.model.bean.BasicInfoResBean;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IPersonalInfoActivity
 * <p><b>Description</b>: 个人信息页面接口
 * Created by leobert on 2016/7/11.
 */
public interface IPersonalInfoActivity extends IActivityAsyncProtected {
    /**
     * 头像选取方式操作表
     *
     * @param data
     * @param listener
     */
    void showAvatarSelectActionsheet(String[] data, OnActionSheetItemClickListener listener);

    /**
     * 跳转到重设昵称页面
     */
    void jumpToResetNickname();

    /**
     * 显示性别选择操作表
     *
     * @param data
     * @param listener
     */
    void showSexSelectActionsheet(String[] data, OnActionSheetItemClickListener listener);

//    /**
//     * 显示行业选择操作表,该交互形式已经修改，TODO 修改代码
//     *
//     * @param data
//     * @param listener
//     */
//    @Deprecated
//    void showIndustrySelectActionSheet(ArrayList<IndustryApiResBean> data, int industry, OnActionSheetItemClickListener listener);

    void showDialog(int contentResid, int positiveResid, CustomPopupWindow.OnPositiveClickListener
            onPositiveClickListener);

    /**
     * 跳转到擅长行业、领域选择页面
     */
    void jumpToFieldModify();

    /**
     * 跳转到重设密码界面
     */
    void jumpToResetPwd();

    /**
     * 更新界面
     *
     * @param bean 暂不定义po，使用bean
     */
    void updateView(BasicInfoResBean bean);

    /**
     * 显示错误信息
     *
     * @param msg 错误信息
     */
    void showErrorMsg(String msg);

    /**
     * 绑定手机页面
     * @param basicInfoResBean 基础信息，可能用到
     */
    void jumpToBindPhone(BasicInfoResBean basicInfoResBean);

    /**
     * 显示页面
     * @param basicInfoResBean 基础信息
     */
    void jumpToShowPhone(BasicInfoResBean basicInfoResBean);

//    void enableFieldModify();

//    void disableFieldModify();
}
