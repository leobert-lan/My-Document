package com.lht.cloudjob.adapter;

import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;

import java.util.ArrayList;

/**
 * @package com.lht.cloudjob.adapter
 * @project AndroidBase
 * @classname ExampleListAdapter
 * @description: 只是个范例，不要使用
 * Created by leobert on 2016/4/1.
 */
@Deprecated
public class ExampleListAdapter extends AbsListAdapter {
    private ArrayList<? extends Object> liData;
    public ExampleListAdapter(ArrayList<? extends Object> liData,IListItemViewProvider itemViewProvider) {
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

    public void setLiData(ArrayList<? extends Object> liData) {
        this.liData = liData;
        notifyDataSetChanged();
    }
}
