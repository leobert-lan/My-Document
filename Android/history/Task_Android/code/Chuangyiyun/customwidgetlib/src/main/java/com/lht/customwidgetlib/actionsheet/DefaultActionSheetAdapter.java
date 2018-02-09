package com.lht.customwidgetlib.actionsheet;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.actionsheet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DefaultActionSheetAdapter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/5.
 */
public class DefaultActionSheetAdapter extends AbsActionSheetAdapter {
    private String[] datas;

    public DefaultActionSheetAdapter(IActionSheetItemViewProvider itemViewProvider) {
        super(itemViewProvider);
        this.datas = new String[] {};
    }

    public DefaultActionSheetAdapter(String[] datas, IActionSheetItemViewProvider itemViewProvider) {
        super(itemViewProvider);
        this.datas = datas;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
