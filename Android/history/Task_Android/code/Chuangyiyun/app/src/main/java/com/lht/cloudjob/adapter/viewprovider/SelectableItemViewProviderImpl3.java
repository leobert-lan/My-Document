package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SelectableItemViewProviderImpl
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/12.
 */
public class SelectableItemViewProviderImpl3 implements IListItemViewProvider<CategoryResBean> {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem<ViewHolder> iCustomizeListItem;

    public SelectableItemViewProviderImpl3(
            LayoutInflater inflater,
            ICustomizeListItem<ViewHolder> iCustomizeListItem) {

        this.mInflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    //默认不选中
    private int selectedIndex = -1;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public View getView(int position, CategoryResBean item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_selectabletype3, null);
            //TODO bind holder
//            holder.fullLineDown = convertView.findViewById(R.id.full_line_down);
//            holder.fullLineUp = convertView.findViewById(R.id.full_line_up);
            holder.line = (ImageView) convertView.findViewById(R.id.half_line_down);
//            holder.halfLineUp = convertView.findViewById(R.id.half_line_up);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        }
        holder.cb.setText(item.getName());
        holder.cb.setOnCheckedChangeListener(null); //clear to init
        holder.cb.setChecked(position == getSelectedIndex());
        holder.cb.setSelected(position == getSelectedIndex());
        holder.autoChange(position, getSelectedIndex());
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, convertView, holder);
        return convertView;
    }

    public class ViewHolder {
        public CheckBox cb;

//        public View fullLineUp;
//
//        public View fullLineDown;
//
//        public View halfLineUp;
//
//        public View halfLineDown;

        public ImageView line;

        public void changeToSelected() {
//            fullLineDown.setVisibility(View.VISIBLE);
//            fullLineUp.setVisibility(View.VISIBLE);
//            halfLineDown.setVisibility(View.GONE);
//            halfLineUp.setVisibility(View.GONE);
            line.setImageResource(R.color.sub_strong_red);
        }

        public void changeToNormal() {
            line.setImageResource(R.color.h6_divider);
//            fullLineDown.setVisibility(View.GONE);
//            fullLineUp.setVisibility(View.GONE);
//            halfLineDown.setVisibility(View.VISIBLE);
//            halfLineUp.setVisibility(View.VISIBLE);
        }

        @Deprecated
        public void changeToUponSelected() {
//            fullLineDown.setVisibility(View.VISIBLE);
//            fullLineUp.setVisibility(View.GONE);
//            halfLineDown.setVisibility(View.GONE);
//            halfLineUp.setVisibility(View.VISIBLE);
        }

        @Deprecated
        public void changeToBelowSelected() {
//            fullLineDown.setVisibility(View.GONE);
//            fullLineUp.setVisibility(View.VISIBLE);
//            halfLineDown.setVisibility(View.VISIBLE);
//            halfLineUp.setVisibility(View.GONE);
        }

        public void autoChange(int current, int selected) {
            if (current == selected) {
                changeToSelected();
            } else {
                changeToNormal();
            }

//            else if (current + 1 == selected) {
//                changeToUponSelected();
//            } else if (current - 1 == selected) {
//                changeToBelowSelected();
//            }
        }

    }
}
