package com.lht.cloudjob.adapter;

import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.customwidgetlib.actionsheet.AbsActionSheetAdapter;
import com.lht.customwidgetlib.actionsheet.IActionSheetItemViewProvider;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter
 * <p><b>Project</b> Chuangyiyun
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
