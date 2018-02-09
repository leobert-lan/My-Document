package com.lht.creationspace.adapter;

import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;
import com.lht.creationspace.util.debug.DLog;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.adapter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ListAdapter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public class ListAdapter<D> extends AbsListAdapter<D> {
    private ArrayList<D> liData;

    public ListAdapter(ArrayList<D> liData, IListItemViewProvider<D> itemViewProvider) {
        super(itemViewProvider);
        this.liData = liData;
    }

    @Override
    public int getCount() {
        return liData.size();
    }

    @Override
    public D getItem(int position) {
        return liData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setLiData(ArrayList<D> liData) {
        this.liData = liData;
        resetPage();
        notifyDataSetChanged();
    }

    public void addLiData(ArrayList<D> datas) {
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

    public boolean removeItem(int index) {
        if (liData == null || index >= getCount()) {
            return false;
        }
        liData.remove(index);
        notifyDataSetChanged();
        return true;
    }

    public void replaceData(int position, D data) {
        if (liData == null) {
            DLog.d(ListAdapter.class, "list data null");
            return;
        }
        if (position >= getCount()) {
            DLog.d(ListAdapter.class, "position out of boundary");
            return;
        }
        liData.set(position, data);
    }

    public ArrayList<D> getAll() {
        return liData;
    }

}
