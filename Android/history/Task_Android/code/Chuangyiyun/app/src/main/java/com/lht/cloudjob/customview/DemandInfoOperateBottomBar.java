package com.lht.cloudjob.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.customwidgetlib.text.DrawableCenterTextView;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandInfoOperateBottomBar
 * <p><b>Description</b>: 需求详情底部操作栏
 * <p> Create by Leobert on 2016/9/6
 */
public class DemandInfoOperateBottomBar extends FrameLayout {

    public DemandInfoOperateBottomBar(Context context) {
        this(context, null);
    }

    public DemandInfoOperateBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemandInfoOperateBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(defStyleAttr);
    }

    private void init(int defStyleAttr) {
        inflate(getContext(), R.layout.view_demandinfo_bar_bottom, this);
    }

    private CheckBox cbCollect;

    private CheckBox btnShare;

    private TextView tvHint;

    private DrawableCenterTextView btnOp1;

    private DrawableCenterTextView btnOp2;

    public interface OnOperateListener {
        void onOperate(int operateCode);
    }

    private OnOperateListener onOperateListener;

    public OnOperateListener getOnOperateListener() {
        return onOperateListener;
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        cbCollect = (CheckBox) findViewById(R.id.cb_collect);
        btnShare = (CheckBox) findViewById(R.id.btn_share);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        btnOp1 = (DrawableCenterTextView) findViewById(R.id.btn_op1);
        btnOp2 = (DrawableCenterTextView) findViewById(R.id.btn_op2);

        btnOp1.setOnClickListener(onClickListener);
        btnOp2.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onOperateListener != null) {
                int code = (int) v.getTag();
                onOperateListener.onOperate(code);
            }
        }
    };

    public void setTaskCollected(boolean isCollected) {
        cbCollect.setChecked(isCollected);
    }

    public boolean isCollectChecked() {
        return cbCollect.isChecked();
    }

    public void setOnCollectClickListener(OnClickListener onCollectClickListener) {
        cbCollect.setOnClickListener(onCollectClickListener);
    }

    public void setOnShareClickListener(OnClickListener onShareClickListener) {
        btnShare.setOnClickListener(onShareClickListener);
    }

    public void setOperateAndRefresh(ArrayList<Integer> operates) {
        if (operates == null ||operates.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        if (operates.size()>2) {
            DLog.e(Lmsg.class,"operate数量超标："+operates);
        }
        setVisibility(VISIBLE);
        if (operates.contains(OPERATE_CODE_EVALUATE)) { // 12 单btn 评价
            trans2Type5(OPERATE_CODE_EVALUATE);
        } else if (operates.contains(OPERATE_CODE_SIGN)) { //含4 签署 |call btn|sign btn|
            trans2Type4(OPERATE_CODE_CALL,OPERATE_CODE_SIGN);
        } else if (operates.contains(OPERATE_CODE_CONTRIBUTE)) { //含1的投稿
            trans2Type1(OPERATE_CODE_CONTRIBUTE);
        } else if (operates.contains(OPERATE_CODE_CONTRIBUTE2)) { // 含13的投稿
            trans2Type1(OPERATE_CODE_CONTRIBUTE2);
        }
//        else if (operates.contains(OPERATE_CODE_FINDSAME)) {
//            trans2Type2(OPERATE_CODE_CALL);
//        }
        else { //显示单文本通知
            int code = 0;
            if (operates.size()>0) {
                code = operates.get(0);
                if (code == OPERATE_CODE_CALL) {
                    if (operates.size()>1) {
                        code = operates.get(1);
                    }
                }
            }
            trans2Type3(code, OPERATE_CODE_CALL);
        }

    }

    private void setAllGone() {
        btnShare.setVisibility(GONE);
        cbCollect.setVisibility(GONE);
        tvHint.setVisibility(GONE);
        btnOp1.setVisibility(GONE);
        btnOp2.setVisibility(GONE);
    }

    /**
     * Operate:[1]或者[13]
     */
    private void trans2Type1(int tagOp2) {
        setAllGone();
        btnShare.setVisibility(VISIBLE);
        cbCollect.setVisibility(VISIBLE);
        btnOp2.setVisibility(VISIBLE);
        btnOp2.setTag(tagOp2);
        Drawable leftDrawable = getDrawableRes(R.drawable
                .v1010_drawable_icon_contribute_default);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        btnOp2.setCompoundDrawables(leftDrawable, null, null, null);
        btnOp2.setText(R.string.v1010_default_demandinfo_btn_contribute);
    }


    /**
     * operate:[2,3]
     */
    private void trans2Type2(int tagOp2) {
        setAllGone();
        btnOp2.setVisibility(VISIBLE);
        btnOp2.setTag(tagOp2);
        Drawable leftDrawable = getDrawableRes(R.drawable
                .selector_drawable_contact2);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        btnOp2.setCompoundDrawables(leftDrawable, null, null, null);
        btnOp2.setText(R.string.v1010_default_demandinfo_btn_call);
    }

    private int getStringResByCode(int code) {
        int ret = R.string.v1010_default_demandinfo_text_ophint_bug;
        switch (code) {
            case HINT_CODE_2:
                ret = R.string.v1010_default_demandinfo_text_ophint2;
                break;
            case HINT_CODE_5:
                ret = R.string.v1010_default_demandinfo_text_ophint5;
                break;
            case HINT_CODE_6:
                ret = R.string.v1010_default_demandinfo_text_ophint6;
                break;
            case HINT_CODE_7:
                ret = R.string.v1010_default_demandinfo_text_ophint7;
                break;
            case HINT_CODE_8:
                ret = R.string.v1010_default_demandinfo_text_ophint8;
                break;
            case HINT_CODE_9:
                ret = R.string.v1010_default_demandinfo_text_ophint9;
                break;
            case HINT_CODE_10:
                ret = R.string.v1010_default_demandinfo_text_ophint10;
                break;
            case HINT_CODE_11:
                ret = R.string.v1010_default_demandinfo_text_ophint11;
                break;
            default:
                break;
        }
        return ret;
    }

    /**
     * operate:[5,3]or[6,3]or[7,3]or[8,3]or[9,3]or[10,3]or[11,3]
     */
    private void trans2Type3(int hintCode,int tagOp2) {
        setAllGone();
        tvHint.setVisibility(VISIBLE);
        tvHint.setText(getStringResByCode(hintCode));

        btnOp2.setVisibility(VISIBLE);
        btnOp2.setTag(tagOp2);
        Drawable leftDrawable = getDrawableRes(R.drawable
                .selector_drawable_contact2);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        btnOp2.setCompoundDrawables(leftDrawable, null, null, null);
        btnOp2.setText(R.string.v1010_default_demandinfo_btn_call);
    }

    /**
     * operate:[3,4]
     */
    private void trans2Type4(int tagOp1, int tagOp2) {
        setAllGone();
        btnOp1.setVisibility(VISIBLE);
        btnOp1.setTag(tagOp1);
        Drawable leftDrawable = getDrawableRes(R.drawable
                .selector_drawable_contact);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        btnOp1.setCompoundDrawables(leftDrawable, null, null, null);
        btnOp1.setText(R.string.v1010_default_demandinfo_btn_call);
        btnOp1.requestLayout();

        btnOp2.setVisibility(VISIBLE);
        btnOp2.setTag(tagOp2);
        Drawable leftDrawable2 = getDrawableRes(R.drawable
                .v1010_drawable_icon_sign_default);
        leftDrawable2.setBounds(0, 0, leftDrawable2.getMinimumWidth(), leftDrawable2.getMinimumHeight());
        btnOp2.setCompoundDrawables(leftDrawable2, null, null, null);
        btnOp2.setText(R.string.v1010_default_demandinfo_btn_sign);
        btnOp2.requestLayout();
    }

    /**
     * operate:[12]
     */
    private void trans2Type5(int tagOp2) {
        setAllGone();
        btnOp2.setVisibility(VISIBLE);
        btnOp2.setTag(tagOp2);
        Drawable leftDrawable2 = getDrawableRes(R.drawable
                .v1010_drawable_icon_evaluation_default);
        leftDrawable2.setBounds(0, 0, leftDrawable2.getMinimumWidth(), leftDrawable2.getMinimumHeight());
        btnOp2.setCompoundDrawables(leftDrawable2, null, null, null);
        btnOp2.setText(R.string.v1010_default_demandinfo_btn_evaluate);
    }

    private Drawable getDrawableRes(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawableResApi21(resid);
        } else {
            return getResources().getDrawable(resid);
        }
    }

    @TargetApi(21)
    private Drawable getDrawableResApi21(int resid) {
        return getResources().getDrawable(resid, null);
    }


    public static final int OPERATE_CODE_CONTRIBUTE2 = 13;

    public static final int OPERATE_CODE_CONTRIBUTE = 1;

    public static final int OPERATE_CODE_EVALUATE = 12;

    public static final int OPERATE_CODE_CALL = 3;

    public static final int OPERATE_CODE_SIGN = 4;

    public static final int HINT_CODE_2 = 2;
    public static final int HINT_CODE_5 = 5;
    public static final int HINT_CODE_6 = 6;
    public static final int HINT_CODE_7 = 7;
    public static final int HINT_CODE_8 = 8;
    public static final int HINT_CODE_9 = 9;
    public static final int HINT_CODE_10 = 10;
    public static final int HINT_CODE_11 = 11;

}
