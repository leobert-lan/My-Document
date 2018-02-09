package com.lht.customwidgetlib.wheel.adapters;

import android.content.Context;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.wheel.adapters
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ArrayListWheelTextAdapter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/8
 */
public class ArrayListWheelTextAdapter <T> extends AbstractWheelTextAdapter {

    // items
    private ArrayList<T> items;

    /**
     * Constructor
     * @param context the current context
     * @param items the items
     */
    public ArrayListWheelTextAdapter(Context context, ArrayList<T> items) {
        super(context);

        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = items;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < items.size()) {
            T item = items.get(index);
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }
}
