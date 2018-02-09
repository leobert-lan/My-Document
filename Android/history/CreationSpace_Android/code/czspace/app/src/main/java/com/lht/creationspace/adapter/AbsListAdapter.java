package com.lht.creationspace.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;


/**
 *  <p><b>Package</b> com.lht.vsocyy.adapter
 * <p><b>Project</b> AndroidBase
 * <p><b>Classname</b> AbsListAdapter
 * <p><b>Description</b>: listview适配器抽象父类
 *
 * Created by leobert on 2017/1/17.
 */
public abstract class AbsListAdapter<D> extends BaseAdapter {

    public interface IListScanOperate<D> {
        void onScan(int position, D data);
    }

    public interface ICustomizeListItem<D, V> {
        /**
         * desc: 为item设置各种回调，实现定制化
         *
         * @param convertView
         */
        void customize(int position, D data, View convertView, V viewHolder);
    }

    private final IListItemViewProvider<D> itemViewProvider;

    private int page = 1;

    public AbsListAdapter(IListItemViewProvider<D> itemViewProvider) {
        this.itemViewProvider = itemViewProvider;
        page = 1;
    }

    @Override
    public abstract int getCount();

    @Override
    public abstract D getItem(int position);

    @Override
    public abstract long getItemId(int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return itemViewProvider.getView(position, getItem(position), convertView, parent);
    }

    /**
     * 重置page
     */
    void resetPage() {
        page = 1;
    }

    /**
     * 增加一页
     */
    void addPage() {
        page++;
    }

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

    public IListItemViewProvider<D> getItemViewProvider() {
        return itemViewProvider;
    }
}
