package com.lht.creationspace.adapter;


import java.util.ArrayList;

import individual.leobert.uilib.actionsheet.AbsActionSheetAdapter;
import individual.leobert.uilib.actionsheet.IActionSheetItemViewProvider;

/**
 * <p><b>Package</b> com.lht.vsocyy.adapter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> MActionSheetAdapter
 * <p><b>Description</b>: 操作表泛型适配器
 * Created by leobert on 2016/7/27.
 */
public class MActionSheetAdapter<T> extends AbsActionSheetAdapter {
    private ArrayList<T> liData;

    public MActionSheetAdapter(ArrayList<T> liData, IActionSheetItemViewProvider itemViewProvider) {
        super(itemViewProvider);
        this.liData = liData;
    }

    @Override
    public int getCount() {
        return liData.size();
    }

    @Override
    public Object getItem(int position) {
        return liData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
