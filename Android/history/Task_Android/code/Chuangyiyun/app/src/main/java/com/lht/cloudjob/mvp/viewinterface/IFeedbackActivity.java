package com.lht.cloudjob.mvp.viewinterface;

import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IFeedbackAcitivty
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/11.
 */
public interface IFeedbackActivity extends IActivityAsyncProtected{
    void notifyOverLength();
    void addFeedbackImage(String path);

    /**
     * 显示获得照片方式的actionsheet
     *
     * @param datas 需要显示的数据
     * @param listener 回掉接口实现类示例
     * @param showCancel    是否显示取消
     */
    void showImageGetActionSheet(String[] datas,OnActionSheetItemClickListener listener,boolean showCancel);


    void showDialog(int contentResid,int positiveResid,
                    CustomPopupWindow.OnPositiveClickListener positiveClickListener);
}
