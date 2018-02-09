package com.lht.creationspace.module.setting.ui;

import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.base.model.pojo.DownloadEntity;
import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.viewinterface
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ISettingActivity
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public interface ISettingActivity extends IActivityAsyncProtected {


    /**
     * 跳转到意见反馈
     */
    void jump2Feedback();

    /**
     * 显示缓存数量
     *
     * @param bytes 缓存大小
     */
    void showCacheSize(long bytes);

    /**
     * 显示清理缓存的询问框
     *
     * @param positiveClickListener 确认时的回掉
     */
    void showCacheCleanAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    /**
     * 显示登出的询问框
     *
     * @param positiveClickListener 确认时的回掉
     */
    void showLogoutAlert(CustomPopupWindow.OnPositiveClickListener positiveClickListener);

    /**
     * 创建个人文件夹
     */
    void createIndividualFolder();

    /**
     * 新版本提示
     *
     * @param versionCode             新版本号
     * @param onPositiveClickListener 点击继续的监听
     */
    void showNewVersionDialog(String versionCode, CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    /**
     * 新版本安装提示
     *
     * @param onPositiveClickListener
     */
    void showInstallDialog(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    /**
     * 使用流量时的提示
     *
     * @param onPositiveClickListener
     */
    void showOnMobileNet(CustomPopupWindow.OnPositiveClickListener onPositiveClickListener);

    /**
     * 更新进度条
     *
     * @param entity
     * @param current
     * @param total
     */
    void updateProgress(DownloadEntity entity, long current, long total);

    /**
     * 显示出下载的进度条
     *
     * @param entity
     */
    void showDownloadProgress(DownloadEntity entity);

    /**
     * 隐藏进度条
     */
    void hideDownloadProgress();

    void jump2AboutUs();
}
