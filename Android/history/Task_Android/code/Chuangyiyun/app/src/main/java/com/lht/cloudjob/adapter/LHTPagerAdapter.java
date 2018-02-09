package com.lht.cloudjob.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lht.cloudjob.interfaces.adapter.IPagerItemViewProvider;
import com.lht.cloudjob.util.debug.DLog;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ListAdapter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/27.
 */
public class LHTPagerAdapter<D> extends PagerAdapter {
    private ArrayList<D> liData;

    private IPagerItemViewProvider<D> itemViewProvider;

    public LHTPagerAdapter(ArrayList<D> liData, IPagerItemViewProvider<D> itemViewProvider) {
        this.itemViewProvider = itemViewProvider;
        this.liData = liData;
    }

    @Override
    public int getCount() {
        return liData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public D getItem(int position) {
        return liData.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return itemViewProvider.getView(position, getItem(position),container);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void setLiData(ArrayList<D> liData) {
        this.liData = liData;
        notifyDataSetChanged();
    }

    public void addLiData(ArrayList<D> datas) {
        if (this.liData == null)
            this.liData = datas;
        else {
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
            DLog.d(LHTPagerAdapter.class, "list data null");
            return;
        }
        if (position >= getCount()) {
            DLog.d(LHTPagerAdapter.class, "position out of boundary");
            return;
        }
        liData.set(position, data);
    }

    public ArrayList<D> getAll() {
        return liData;
    }

    public interface ICustomizePagerItem<D, V> {
        /**
         * desc: 为item实现各种定制化需求，
         *
         * @param view view of page item
         */
        void customize(int position, D data, View view, V viewHolder);
    }

}
