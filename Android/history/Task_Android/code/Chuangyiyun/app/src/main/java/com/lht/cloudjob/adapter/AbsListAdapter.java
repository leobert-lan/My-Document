package com.lht.cloudjob.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.IListScanOperate;

/**
 * @package com.lht.cloudjob.adapter
 * @project AndroidBase
 * @classname AbsListAdapter
 * @description: listview适配器抽象父类，保持了getView的抽象
 * Created by leobert on 2016/4/1.
 */
public abstract class AbsListAdapter<D> extends BaseAdapter {

    private final IListItemViewProvider<D> itemViewProvider;

    protected int page = 1;

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

    /**
     * desc: TODO: 描述方法
     * <p/>
     * 这里我考虑过两种解耦方式：
     * 1高度复用adapter，使用接口完成getView回调。
     * 2定义click等相关接口
     * 当前，我选用第一种形式，不会让activity显得过于臃肿
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return itemViewProvider.getView(position, getItem(position), convertView, parent);
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
