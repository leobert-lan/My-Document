package com.lht.customwidgetlib.actionsheet;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.lht.customwidgetlib.DisplayUtils;
import com.lht.customwidgetlib.R;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.actionsheet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ActionSheet
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/5.
 */
public class ActionSheet extends PopupWindow {

    private Activity mActivity;

    private View convertView;

    private ListView listView;

    private View btnBroken;

    private LinearLayout llBrokenArea;

    private DefaultActionSheetAdapter defaultAdapter;

    public void transparent() {
        convertView.setBackgroundResource(R.color.transparent);
        this.setBackgroundDrawable(null);
    }

    public ActionSheet(Activity activity) {
        this.mActivity = activity;
        convertView = LayoutInflater.from(activity).inflate(R.layout.actionsheet, null, false);
        setContentView(convertView);
        doDefaultSetting();

        listView = (ListView) convertView.findViewById(R.id.actionsheet_list);
        btnBroken = convertView.findViewById(R.id.actionsheet_btn_broken);
        llBrokenArea = (LinearLayout) convertView.findViewById(R.id.actionsheet_ll_broken);
        defaultAdapter = new DefaultActionSheetAdapter(
                new DefaultItemViewProvider(mActivity.getLayoutInflater(),
                        new OnActionSheetItemClickListener() {
                            @Override
                            public void onActionSheetItemClick(int position) {
                                dismiss();
                                if (actionSheetItemClickListener != null) {
                                    actionSheetItemClickListener.onActionSheetItemClick(position);
                                }
                            }
                        }));

        listView.setAdapter(defaultAdapter);

        btnBroken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private OnActionSheetItemClickListener actionSheetItemClickListener;

    /**
     * Only used for defaultAdapter,if you provide a custom adapter extends AbsActionSheetAdapter,
     * you should use your OnActionSheetItemClickListener implementation as an argument of
     * the constructor
     *
     * @param listener the listener to listen click event on sheet items
     */
    public void setOnActionSheetItemClickListener(OnActionSheetItemClickListener listener) {
        this.actionSheetItemClickListener = listener;
    }

    public void setDatasForDefaultAdapter(String[] datas) {
        defaultAdapter.setDatas(datas);
    }

    public void setAdapter(AbsActionSheetAdapter adapter) {
//        listView.setBackgroundResource(R.drawable.lib_bgselector_conner5dp);
        if (adapter.getCount() > 4) {
            listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    DisplayUtils.convertDpToPx(mActivity, 180)));
        }
        listView.setAdapter(adapter);
    }

    public void enableBrokenButton() {
        llBrokenArea.setVisibility(View.VISIBLE);
    }


    protected void doDefaultSetting() {
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(mActivity.getWindowManager().getDefaultDisplay()
                .getWidth() * 9 / 10);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.iOSActionSheet);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ColorDrawable dw = new ColorDrawable(0x000000);//0xb0000000
        this.setBackgroundDrawable(dw);

        setyOffset(-20);
        setOutsideClickDismiss();
    }

    private int yOffset = 0;

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void show(View parent) {
        backgroundAlpha(0.6f);
        if (!this.isShowing())
            this.showAtLocation(parent, defaultGravity, 0, yOffset);
    }

    /**
     * @Title: setOutsideClickDismiss
     * @Description: 设置点击外部消失
     * @author: leobert.lan
     */
    public void setOutsideClickDismiss() {
        if (this.isShowing()) {
            Log.e(getClass().getSimpleName(), "该方法需要在显示前调用");
        } else {
            this.setFocusable(true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            this.setBackgroundDrawable(dw);
        }
    }

    protected void backgroundAlpha(float alpha) {

//        if (alpha<1) {
//            mActivity.getWindow().getDecorView().setAlpha(0.8f);
//        } else {
//            mActivity.getWindow().getDecorView().setAlpha(alpha);
//        }

        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        mActivity.getWindow().setAttributes(lp);
    }

//    public void show(View parent) {
//        backgroundAplha(0.4f);
//        if (!this.isShowing())
//            this.showAtLocation(parent, defaultGravity, 0, 0);
////        if (iPopupHolder != null) {
////            iPopupHolder.setPenetrable(mActivity, true);
////        }
//    }

    private int defaultGravity = Gravity.BOTTOM;

    public void setGravity(int gravity) {
        this.defaultGravity = gravity;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
//        iPopupHolder.setLatestPopupWindow(this);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
//        iPopupHolder.setLatestPopupWindow(null);
//
//        if (iPopupHolder != null) {
//            iPopupHolder.setPenetrable(mActivity, false);
//        }
        super.dismiss();
        backgroundAlpha(1f);
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                backgroundAplha(1f);
//            }
//        }, 300);
    }

    public void show() {
        show(mActivity.getWindow().getDecorView());
    }


}
