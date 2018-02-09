package com.lht.cloudjob.customview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TypeSheet
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/12.
 */
public class SortSheet extends FrameLayout {


    private View contentView;

    public SortSheet(Context context) {
        super(context);
        init();
    }

    public SortSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SortSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View background;

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    private LinearLayout llRealContent;

    private ConflictGridView gridSort;

    private SelectableItemViewProviderImpl itemViewProvider;

    private ListAdapter<String> adapter;

    private ArrayList<String> datas;

    private void init() {
        contentView = inflate(getContext(), R.layout.view_sortsheet, this);
        background = contentView.findViewById(R.id.view_background);
        llRealContent = (LinearLayout) contentView.findViewById(R.id.sort_ll_content);

        gridSort = (ConflictGridView) contentView.findViewById(R.id.sort_grid_type);

        llRealContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //empty 防止击穿
            }
        });

        background.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setVisibility(GONE);
        Resources res = getResources();

        datas = ArrayToArrayList(res.getStringArray(R.array.v1010_search_sort_contents));

        initGrid();
    }


    private ArrayList<String> ArrayToArrayList(String[] array) {
        ArrayList<String> temp = new ArrayList<>();
        if (array == null || array.length == 0) {
            return temp;
        }
        for (String s : array) {
            if (s == null) {
                continue;
            }
            temp.add(s);
        }
        return temp;
    }

    private void initGrid() {
        itemViewProvider = new SelectableItemViewProviderImpl(LayoutInflater.from(getContext()),
                new ICustomizeListItem<SelectableItemViewProviderImpl.ViewHolder>() {
                    @Override
                    public void customize(final int position, final View convertView,
                                            SelectableItemViewProviderImpl.ViewHolder viewHolder) {
                        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    itemViewProvider.setSelectedIndex(position);
                                    if (onSelectedListener != null) {
                                        onSelectedListener.onSortRuleSelected(SortRules.getByIndex(position));
                                    }
                                    YoYo.with(Techniques.Pulse).duration(300).withListener(new AnimEndListener() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            dismiss();
                                        }
                                    }).playOn(convertView);

                                } else {
                                    // fTypeItemViewProvider.setSelectedIndex(0); //不取消
                                    YoYo.with(Techniques.Pulse).duration(300).playOn(convertView);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });


        adapter = new ListAdapter<>(datas, itemViewProvider);

        gridSort.setAdapter(adapter);
    }

    private boolean isShown = false;

    @Override
    public boolean isShown() {
        return isShown;
    }

    public void show() {
        if (isShown)
            return;
        isShown = true;

        setVisibility(VISIBLE);
        bringToFront();
        llRealContent.startAnimation(dropdownAnimation(500));
    }

    public void dismiss() {
        if (isShown) {
            setVisibility(GONE);
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        }
        isShown = false;
    }

    protected Animation dropdownAnimation(long animDuration) {
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setDuration(animDuration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        return anim;
    }

    public void reset() {
        itemViewProvider.setSelectedIndex(0);
        adapter.notifyDataSetChanged();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    private OnSelectedListener onSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSortRuleSelected(SortRules sortRules);
    }

    /**
     * create_time => 发布时间
     * sub_end_time => 交稿截止时间
     * total_bids => 投稿数
     * max_cash => 金额
     */
    public enum SortRules {
        deadline("sub_end_time", true), price("max_cash", false), contributions("total_bids", false),
        createTime("create_time", false);
        private final boolean isASC;

        private final String orderName;

        SortRules(String orderName, boolean isASC) {
            this.orderName = orderName;
            this.isASC = isASC;
        }

        public boolean isASC() {
            return isASC;
        }

        public String getOrderName() {
            return this.orderName;
        }

        public String getDirection() {
            if (isASC)
                return "ASC";
            else
                return "DESC";
        }

        public static SortRules getByIndex(int index) {
            switch (index) {
                case 0:
                    return deadline;
                case 1:
                    return price;
                case 2:
                    return contributions;
                case 3:
                    return createTime;
                default:
                    return deadline;
            }
        }
    }

    private abstract class AnimEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
