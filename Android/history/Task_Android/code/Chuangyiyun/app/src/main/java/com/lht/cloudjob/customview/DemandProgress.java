package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.lht.cloudjob.R;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandProgress
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/22
 */
public class DemandProgress extends FrameLayout {

    public DemandProgress(Context context) {
        super(context);
        init();
    }

    public DemandProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemandProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View contentView;

    private LinearLayout llGroup;

    private void init() {
        contentView = inflate(getContext(), R.layout.view_demand_progress, this);
        llGroup = (LinearLayout) contentView.findViewById(R.id.ll_group);
    }

    public void setData(String[] steps, int step) {
        if (steps == null) {
            return;
        }
        int count = steps.length;
        llGroup.removeAllViews();
        for (int i = 0; i < count; i++) {
            DemandProgressUnitView unitView = new DemandProgressUnitView(getContext());
            unitView.setName(steps[i]);
            if (i < step) {
                unitView.setStatus(DemandProgressUnitView.Status.Before);
            } else if (i == step) {
                unitView.setStatus(DemandProgressUnitView.Status.Current);
            } else {
                unitView.setStatus(DemandProgressUnitView.Status.Future);
            }

            if (i == 0) {
                unitView.setType(DemandProgressUnitView.Type.Head);
            } else if (i == count - 1) {
                unitView.setType(DemandProgressUnitView.Type.End);
            } else {
                unitView.setType(DemandProgressUnitView.Type.Normal);
            }
//            unitView.measure(0,0);
//            float weight = unitView.getMeasuredWidth();
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
//                    .LayoutParams.WRAP_CONTENT, LinearLayout
//                    .LayoutParams.WRAP_CONTENT,weight);
//            unitView.setLayoutParams(params);
            llGroup.addView(unitView);
        }
    }
}
