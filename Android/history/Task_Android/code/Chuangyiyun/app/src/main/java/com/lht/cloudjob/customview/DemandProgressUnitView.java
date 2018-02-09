package com.lht.cloudjob.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandProgressUnitView
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/22
 */
public class DemandProgressUnitView extends FrameLayout {
    public DemandProgressUnitView(Context context) {
        super(context);
        init();
    }

    public DemandProgressUnitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemandProgressUnitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View contentView;

    private ImageView lineHead, lineNormal, lineEnd;

    private CheckBox cb;

    private TextView textView;

    private void init() {
        contentView = inflate(getContext(), R.layout.view_demand_progress_item, this);
        lineHead = (ImageView) contentView.findViewById(R.id.line_head);
        lineNormal = (ImageView) contentView.findViewById(R.id.line_normal);
        lineEnd = (ImageView) contentView.findViewById(R.id.line_end);

        cb = (CheckBox) contentView.findViewById(R.id.cb);
        textView = (TextView) contentView.findViewById(R.id.tv);
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public void setType(Type type) {
        float weight;

        switch (type) {
            case Head:
                lineHead.setVisibility(VISIBLE);
                lineNormal.setVisibility(GONE);
                lineEnd.setVisibility(GONE);
                weight = 0;
                break;
            case Normal:
                lineHead.setVisibility(GONE);
                lineNormal.setVisibility(VISIBLE);
                lineEnd.setVisibility(GONE);
                weight = 1;
                break;
            case End:
                lineHead.setVisibility(GONE);
                lineNormal.setVisibility(VISIBLE);
                lineEnd.setVisibility(VISIBLE);
                weight = 1;
                break;
            default:
                //as Normal
                lineHead.setVisibility(GONE);
                lineNormal.setVisibility(VISIBLE);
                lineEnd.setVisibility(GONE);
                weight = 1;
                break;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.WRAP_CONTENT,weight);
        setLayoutParams(params);
    }

    public void setStatus(Status status) {
        int color;
        switch (status) {
            case Before:
                lineHead.setImageResource(R.color.sub_strong_orange);
                lineNormal.setImageResource(R.color.sub_strong_orange);
                lineEnd.setImageResource(R.color.sub_strong_orange);
                cb.setChecked(true);
                color = Color.rgb(102, 102, 102);//h_666
                textView.setTextColor(color);
                break;
            case Current:
                lineHead.setImageResource(R.color.sub_strong_orange);
                lineNormal.setImageResource(R.color.sub_strong_orange);
                lineEnd.setImageResource(R.color.sub_strong_orange);
                cb.setChecked(true);
                color = Color.rgb(255, 102, 0);//ff6600
                textView.setTextColor(color);
                break;
            case Future:
                lineHead.setImageResource(R.color.h11_text_gray_ccc);
                lineNormal.setImageResource(R.color.h11_text_gray_ccc);
                lineEnd.setImageResource(R.color.h11_text_gray_ccc);
                cb.setChecked(false);
                color = Color.rgb(204, 204, 204);//H_ccc
                textView.setTextColor(color);
                break;
            default:
                //as current
                lineHead.setImageResource(R.color.sub_strong_orange);
                lineNormal.setImageResource(R.color.sub_strong_orange);
                lineEnd.setImageResource(R.color.sub_strong_orange);
                cb.setChecked(true);
                color = Color.rgb(255, 102, 0);
                textView.setTextColor(color);
                break;
        }
    }


    public enum Type {
        Head, Normal, End
    }

    public enum Status {
        Before, Current, Future
    }
}
