package com.lht.cloudjob.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.IListScanOperate;
import com.lht.cloudjob.util.debug.DLog;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ListAdapter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public class ListAdapter2<D> extends BaseAdapter {
    private ArrayList<D> liData;

    private IListItemViewProvider<D> itemViewProvider;

    protected int page = 1;

    public ListAdapter2(ArrayList<D> liData, IListItemViewProvider<D> itemViewProvider) {
        this.itemViewProvider = itemViewProvider;
        this.liData = liData;
        page = 1;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return itemViewProvider.getView(position, getItem(position), convertView, parent);
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
            DLog.d(ListAdapter2.class, "list data null");
            return;
        }
        if (position >= getCount()) {
            DLog.d(ListAdapter2.class, "position out of boundary");
            return;
        }
        liData.set(position, data);
    }

    public ArrayList<D> getAll() {
        return liData;
    }

    public interface ICustomizeListItem2<D, V> {
        /**
         * desc: 为item设置各种回调，
         *
         * @param convertView
         */
        void customize(int position, D data, View convertView, V viewHolder);
    }

    /**
     * 重置page
     */
    protected void resetPage() {
        page = 1;
    }

    /**
     * 增加一页
     */
    protected void addPage() {
        page++;
    }

    /**
     * @return
     */
    public int getDefaultPagedOffset() {
        return page * 20;
    }

    public int getPagedOffset(int pageSize) {
        return page * pageSize;
    }

    public void scan(IListScanOperate<D> scanOperate) {
        if (scanOperate == null)
            return;
        for (int position = 0; position < getCount(); position++) {
            scanOperate.onScan(position, getItem(position));
        }
    }
}
