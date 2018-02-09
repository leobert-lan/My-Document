package com.lht.cloudjob.customview;

import android.view.View;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IDemandItemTranser
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/8/17
 */
public interface IDemandItemTranser {
    void trans();

    void showPrice();

    void hidePrice();

    void showTitle();

    void hideTitle();

    void showTagUrgent();

    void hideTagUrgent();

    void showTagTop();

    void hideTagTop();

    void showParticipater();

    void hideParticipater();

    void showType();

    void hideType();

    void showRemainTime();

    void hideRemainTime();

    void showState();

    void hideState();

    void showButton1();

    void hideButton1();

    void showButton2();

    void hideButton2();

    void showRow1();

    void hideRow1();

    void showRow2();

    void hideRow2();

    void showRow3();

    void hideRow3();

    void showTagTender();

    void hideTagTender();

    abstract class AbsTranser implements IDemandItemTranser {
        private static final int VISIBLE = View.VISIBLE;
        private static final int GONE = View.GONE;
        protected final DemandItemView mDemandItemView;

        protected DemandItemView.DemandItemViewHolder viewHolder;

        public AbsTranser(DemandItemView demandItemView) {
            mDemandItemView = demandItemView;
            this.viewHolder = demandItemView.getViewHolder();
        }

        public void showPrice() {
            viewHolder.tvPrice.setVisibility(VISIBLE);
        }

        public void hidePrice() {
            viewHolder.tvPrice.setVisibility(GONE);
        }

        public void showTitle() {
            viewHolder.tvTitle.setVisibility(VISIBLE);
        }

        public void hideTitle() {
            viewHolder.tvTitle.setVisibility(GONE);
        }

        public void showTagUrgent() {

            viewHolder.tagJi.setVisibility(VISIBLE);
        }

        public void hideTagUrgent() {
            viewHolder.tagJi.setVisibility(GONE);
        }

        public void showTagTop() {
            viewHolder.tagDing.setVisibility(VISIBLE);
        }

        public void hideTagTop() {
            viewHolder.tagDing.setVisibility(GONE);
        }

        public void showParticipater() {
            viewHolder.tvParticipater.setVisibility(VISIBLE);
        }

        public void hideParticipater() {
            viewHolder.tvParticipater.setVisibility(GONE);
        }

        public void showType() {
            viewHolder.tvType.setVisibility(VISIBLE);
        }

        public void hideType() {
            viewHolder.tvType.setVisibility(GONE);
        }

        public void showRemainTime() {
            viewHolder.tvRemainTime.setVisibility(VISIBLE);
        }

        public void hideRemainTime() {
            viewHolder.tvRemainTime.setVisibility(GONE);
        }

        public void showState() {
            viewHolder.tvState.setVisibility(VISIBLE);
        }

        public void hideState() {
            viewHolder.tvState.setVisibility(GONE);
        }

        public void showButton1() {
            viewHolder.btnOp1.setVisibility(VISIBLE);
        }

        public void hideButton1() {
            viewHolder.btnOp1.setVisibility(GONE);
        }

        public void showButton2() {
            viewHolder.btnOp2.setVisibility(VISIBLE);
        }

        public void hideButton2() {
            viewHolder.btnOp2.setVisibility(GONE);
        }

        public void showRow1() {
            viewHolder.row1.setVisibility(VISIBLE);
        }

        public void hideRow1() {
            viewHolder.row1.setVisibility(GONE);
        }

        public void showRow2() {
            viewHolder.row2.setVisibility(VISIBLE);
        }

        public void hideRow2() {
            viewHolder.row2.setVisibility(GONE);
        }

        public void showRow3() {
            viewHolder.row3.setVisibility(VISIBLE);
            viewHolder.line1.setVisibility(VISIBLE);
            viewHolder.line2.setVisibility(VISIBLE);
        }

        public void hideRow3() {
            viewHolder.row3.setVisibility(GONE);
            viewHolder.line1.setVisibility(GONE);
            viewHolder.line2.setVisibility(GONE);
        }

        public void showTagTender() {
            viewHolder.tag.setVisibility(VISIBLE);
            viewHolder.tag.bringToFront();
        }

        public void hideTagTender() {
            viewHolder.tag.setVisibility(GONE);
        }

    }

    final class OriginalTranser extends AbsTranser {

        public OriginalTranser(DemandItemView demandItemView) {
            super(demandItemView);
        }

        @Override
        public void trans() {
            showButton1();
            showButton2();
            showParticipater();
            showPrice();
            showRemainTime();
            showRow1();
            showRow2();
            showRow3();
            showState();
            showTagTender();
            showTagTop();
            showTagUrgent();
            showTitle();
            showType();
            viewHolder.btnOp1.setOnClickListener(null);
            viewHolder.btnOp2.setOnClickListener(null);
        }
    }

    final class SimpleTranser extends AbsTranser {

        public SimpleTranser(DemandItemView demandItemView) {
            super(demandItemView);
        }

        @Override
        public void trans() {
            showRow1();
            showRow2();
            showPrice();
            showTitle();
            showTagUrgent();
            showTagTop();
            showParticipater();
            showType();
            showRemainTime();
            hideTagTender();
            hideRow3();
            viewHolder.btnOp1.setOnClickListener(null);
            viewHolder.btnOp2.setOnClickListener(null);
        }
    }
}
