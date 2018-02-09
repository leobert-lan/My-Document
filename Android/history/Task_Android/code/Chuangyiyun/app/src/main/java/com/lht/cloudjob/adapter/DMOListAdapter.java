package com.lht.cloudjob.adapter;

import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.util.debug.DLog;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DMOListAdapter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/22.
 */
public class DMOListAdapter extends AbsListAdapter {

    private ArrayList<DemandItemData> liData;

    public DMOListAdapter(ArrayList<DemandItemData> liData, IListItemViewProvider<DemandItemData>
            itemViewProvider) {
        super(itemViewProvider);
        this.liData = liData;
    }

    @Override
    public int getCount() {
        return liData.size();
    }

    @Override
    public DemandItemData getItem(int position) {
        return liData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setLiDatas(ArrayList<DemandItemData> liDatas) {
        this.liData = liDatas;
        resetPage();
        notifyDataSetChanged();
    }

    public void addLiDatas(ArrayList<DemandItemData> datas) {
        if (this.liData == null)
            this.liData = datas;
        else {
            addPage();
            for (int i = 0; i < datas.size(); i++) {
                this.liData.add(datas.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public void replace(int position, DemandItemData data) {
        if (liData == null) {
            DLog.d(getClass(), "list data null");
            return;
        }
        if (position >= getCount()) {
            DLog.d(getClass(), "position out of boundary");
            return;
        }
        liData.set(position, data);
//        notifyDataSetChanged(); 如有必要，手动回调
    }

    public ArrayList<DemandItemData> getAll() {
        return liData;
    }
}
