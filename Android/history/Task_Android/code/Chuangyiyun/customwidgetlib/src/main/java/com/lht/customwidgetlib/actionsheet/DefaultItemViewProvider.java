package com.lht.customwidgetlib.actionsheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.customwidgetlib.R;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.actionsheet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DefaultItemViewProvider
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/5.
 */

/*
 * packaged
 */
class DefaultItemViewProvider implements IActionSheetItemViewProvider<String> {

    private LayoutInflater inflater;

    private OnActionSheetItemClickListener itemClickListener;

    DefaultItemViewProvider(LayoutInflater inflater, OnActionSheetItemClickListener itemClickListener) {
        this.inflater = inflater;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public  View getView(final int position, String item, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.actionsheet_item_default, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.actionsheet_item_text);
            convertView.setTag(holder);
        }


        holder.text.setText((String) item);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onActionSheetItemClick(position);
                }
            }
        });


        return convertView;
    }
}
