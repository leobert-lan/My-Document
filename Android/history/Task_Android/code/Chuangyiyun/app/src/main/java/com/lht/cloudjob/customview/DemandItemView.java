package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandItemView
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/22.
 */
public class DemandItemView extends FrameLayout {
    public DemandItemView(Context context) {
        this(context, null);
    }

    public DemandItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemandItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private DemandItemViewHolder viewHolder;

    public DemandItemViewHolder getViewHolder() {
        return viewHolder;
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_item_demand, this);

        viewHolder = new DemandItemViewHolder();
        bindView(view, viewHolder);
    }

    private void bindView(View view, DemandItemViewHolder viewHolder) {
        viewHolder.root = (RelativeLayout) view.findViewById(R.id.rl_root);
        viewHolder.cbSelected = (CheckBox) view.findViewById(R.id.demand_cb_select);
        viewHolder.tvPrice = (TextView) view.findViewById(R.id.demand_item_tv_price);
        viewHolder.tvTitle = (TextView) view.findViewById(R.id.demand_item_tv_title);
        viewHolder.tagJi = (TextView) view.findViewById(R.id.demand_item_tag_ji);
        viewHolder.tagDing = (TextView) view.findViewById(R.id.demand_item_tag_ding);
        viewHolder.tvParticipater = (TextView) view.findViewById(R.id.demand_item_tv_participater);
        viewHolder.tvType = (TextView) view.findViewById(R.id.demand_item_tv_type);
        viewHolder.tvRemainTime = (TextView) view.findViewById(R.id.demand_item_tv_remaintime);
        viewHolder.tvState = (TextView) view.findViewById(R.id.demand_item_tv_state);
        viewHolder.btnOp1 = (TextView) view.findViewById(R.id.demand_item_op1);
        viewHolder.btnOp2 = (TextView) view.findViewById(R.id.demand_item_op2);

        viewHolder.row1 = (LinearLayout) view.findViewById(R.id.row1);
        viewHolder.row2 = (RelativeLayout) view.findViewById(R.id.row2);
        viewHolder.row3 = (RelativeLayout) view.findViewById(R.id.row3);
        viewHolder.line1 = view.findViewById(R.id.line1);
        viewHolder.line2 = view.findViewById(R.id.line2);
        viewHolder.tag = (ImageView) view.findViewById(R.id.demand_item_img_tag);
    }

    public void setOnSelectChangedListener(CompoundButton.OnCheckedChangeListener listener) {
        viewHolder.cbSelected.setOnCheckedChangeListener(listener);
    }

    public IDemandItemTranser newDemandItemTranser(Type type) {
        switch (type) {
            case ORIGINAL:
                return new IDemandItemTranser.OriginalTranser(this);
            case SIMPLE:
                return new IDemandItemTranser.SimpleTranser(this);
            default:
                return new IDemandItemTranser.OriginalTranser(this);
        }
    }

    //实现变换

    /**
     * 回归原始状态
     */
    private void changeToOriginal() {
        newDemandItemTranser(Type.ORIGINAL).trans();
    }

    public void enableSelectedMode(boolean isEnabled) {
        if (isEnabled) {
            viewHolder.cbSelected.setVisibility(VISIBLE);
        } else {
            viewHolder.cbSelected.setVisibility(GONE);
        }
    }

    private DemandItemData data;

    public void setData(DemandItemData data) {
        this.data = data;
        refreshViewByData();
    }

    private void refreshViewByData() {
        if (data == null) {
            return;
        }
        viewHolder.cbSelected.setChecked(data.isSelected());

        //价格
        viewHolder.tvPrice.setText(data.getPriceString());

        //需求标题
        viewHolder.tvTitle.setText(data.getDemandName());

        //置顶、加急
        freshPriority(data.getPriority());

        //line2
        //参与人数
        viewHolder.tvParticipater.setText(data.getParticipaterString());

        //类型别名
        viewHolder.tvType.setText(data.getModelString());

        //结束时间
        viewHolder.tvRemainTime.setText(data.getRemainTimeString());

        if (data.isSelected()) {
            viewHolder.root.setBackgroundResource(R.color.h10_text_gray_title2_pressed);
        } else {
            viewHolder.root.setBackgroundResource(R.color.bg_white);
        }

    }

    private void freshPriority(int priority) {
        //我承接的是没有给字段的
        switch (priority) {
            case DemandItemData.PRIORITY_TOP_URGENT:
                viewHolder.tagJi.setVisibility(VISIBLE);
                viewHolder.tagDing.setVisibility(VISIBLE);
                break;
            case DemandItemData.PRIORITY_TOP: //优先级2：置顶
                viewHolder.tagJi.setVisibility(GONE);
                viewHolder.tagDing.setVisibility(VISIBLE);
                break;
            case DemandItemData.PRIORITY_URGENT: //优先级3：加急
                viewHolder.tagJi.setVisibility(VISIBLE);
                viewHolder.tagDing.setVisibility(GONE);
                break;
            case DemandItemData.PRIORITY_NORMAL:
                viewHolder.tagJi.setVisibility(GONE);
                viewHolder.tagDing.setVisibility(GONE);
                break;
            default:
                viewHolder.tagJi.setVisibility(GONE);
                viewHolder.tagDing.setVisibility(GONE);
                break;
        }
    }


    /**
     * 设置第一个按钮的点击回调
     *
     * @param listener 监听器
     */
    public void setOp1OnClickListener(OnClickListener listener) {
        viewHolder.btnOp1.setOnClickListener(listener);
    }

    /**
     * 设置第二个按钮的点击回调
     *
     * @param listener 监听器
     */
    public void setOp2OnClickListener(OnClickListener listener) {
        viewHolder.btnOp2.setOnClickListener(listener);
    }

    /**
     *
     */
    public static class DemandItemViewHolder {
        public RelativeLayout root;

        public CheckBox cbSelected;

        /**
         * 第一行 价格
         */
        public TextView tvPrice;
        /**
         * 第一行 标题
         */
        public TextView tvTitle;
        /**
         * 第一行 加急标签
         */
        public TextView tagJi;
        /**
         * 第一行 置顶标签
         */
        public TextView tagDing;

        /**
         * 第二行 参与人数
         */
        public TextView tvParticipater;
        /**
         * 第二行 任务类型
         */
        public TextView tvType;
        /**
         * 第二行 剩余时间（至截稿）
         */
        public TextView tvRemainTime;

        /**
         * 第三行 状态
         */
        public TextView tvState;
        /**
         * 第三行 按钮1
         */
        public TextView btnOp1;
        /**
         * 第三行 按钮2
         */
        public TextView btnOp2;

        public LinearLayout row1;
        public RelativeLayout row2, row3;

        public View line1, line2;

        /**
         * 中标、备选
         */
        public ImageView tag;
    }

    /**
     *
     */
    public enum Type {
        ORIGINAL(-1),

        SIMPLE(0);


//        //单人悬赏-投稿中
//        REWARD_CONTRIBUTE_CONTRIBUTING_DEFAULT(1),
//
//        //单人悬赏-投稿中-备选
//        REWARD_CONTRIBUTE_CONTRIBUTING_OPTIONAL(2),
//
//        //单人悬赏-选稿中
//        REWARD_CONTRIBUTE_SELECT_DEFAULT(3),
//
//        //单人悬赏-选稿中-备选
//        REWARD_CONTRIBUTE_SELECT_OPTIONAL(4),
//
//        //已中标
//        //单人悬赏-公示中
//        REWARD_BID_SPECTACLE_DEFAULT(5),
//        //单人悬赏-签署协议
//        REWARD_BID_AGREEMENT_DEFAULT(6),
//        //单人悬赏-签署协议-等待甲方
//        REWARD_BID_AGREEMENT_WAITJIA(7),
//
//        //交付中
//        //单人悬赏-交付-上传资源
//        REWARD_LEAD_UPLOADSRC(8),
//        //单人悬赏-交付-等待甲方确认
//        REWARD_LEAD_WAITJIA(9),
//        //单人悬赏-交付-被拒绝
//        REWARD_LEAD_DENY(10),
//
//        //完成
//        //单人悬赏-完成-等待评价
//        REWARD_COMPLETE_EVALUATE_NONE(11),
//        //单人悬赏-完成-完成评价
//        REWARD_COMPLETE_EVALUATE_DOWN(12),
//
//
//        //项目招标-投稿中
//        TENDER_CONTRIBUTE_CONTRIBUTING_DEFAULT(13),
//
//        //项目招标-投稿中-备选
//        TENDER_CONTRIBUTE_CONTRIBUTING_OPTIONAL(14),
//
//        //项目招标-选稿中
//        TENDER_CONTRIBUTE_SELECT_DEFAULT(15),
//
//        //项目招标-选稿中-备选
//        TENDER_CONTRIBUTE_SELECT_OPTIONAL(16),
//
//        //已中标
//        //项目招标-公示中
//        TENDER_BID_SPECTACLE_DEFAULT(17),
//        //项目招标-签署协议
//        TENDER_BID_AGREEMENT_DEFAULT(18),
//        //项目招标-签署协议-等待甲方
//        TENDER_BID_AGREEMENT_WAITJIA(19),
//        //项目招标-签署协议-乙方拒绝
//        TENDER_BID_AGREEMENT_DENYYI(20),
//
//        //交付中
//        //项目招标-交付-等待托管
//        TENDER_LEAD_WAITDELEGATE(21),
//        //项目招标-交付-上传资源
//        TENDER_LEAD_UPLOADSRC(22),
//        //项目招标-交付-等待甲方确认
//        TENDER_LEAD_WAITJIA(23),
//        //项目招标-交付-被拒绝
//        TENDER_LEAD_DENY(24),
//        //项目招标-交付-通过
//        TENDER_LEAD_CONFIRM(25),
//
//        //完成
//        //项目招标-完成-等待评价
//        TENDER_COMPLETE_EVALUATE_NONE(26),
//        //项目招标-完成-完成评价
//        TENDER_COMPLETE_EVALUATE_DOWN(27);


        private final int tag;

        Type(int tag) {
            this.tag = tag;
        }

        public int getTag() {
            return tag;
        }
    }
}
