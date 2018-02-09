package com.lht.cloudjob.adapter.viewprovider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.IDemandItemTranser;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SimpleTaskItemViewProviderImpl
 * <p><b>Description</b>: TODO 暂时不用
 * <p> Create by Leobert on 2016/8/17
 */
public class SimpleTaskItemViewProviderImpl implements IListItemViewProvider<DemandItemData> {

    private final Context context;

    private final ISetCallbackForDemandItem iSetCallback;

    public SimpleTaskItemViewProviderImpl(Context context, ISetCallbackForDemandItem iSetCallback) {
        this.context = context;
        this.iSetCallback = iSetCallback;
    }

    @Override
    public View getView(int position, DemandItemData item, View convertView, ViewGroup parent) {
        DemandItemView temp;
        if (convertView != null && convertView instanceof DemandItemView) {
            temp = (DemandItemView) convertView;
            //... do
            // TODO: 2016/7/22 do transform
            convertView = temp;
        } else {
            temp = new DemandItemView(context);
            convertView = temp;
        }
        temp.setOnSelectChangedListener(null);

        IDemandItemTranser transer = temp.newDemandItemTranser(DemandItemView.Type.SIMPLE);
        transer.trans();
        temp.setData(item);

        temp.enableSelectedMode(isSelectedMode);

        if (iSetCallback!= null) {
            iSetCallback.setCallBack(position,item,temp);
        }

        return convertView;
    }

    private boolean isSelectedMode;

    public boolean isSelectedMode() {
        return isSelectedMode;
    }

    public void setSelectedModeEnabled(boolean enabled) {
        this.isSelectedMode = enabled;
    }


    public interface ISetCallbackForDemandItem {
        /**
         * desc: 为item设置各种回调，
         */
        void setCallBack(int position, DemandItemData data, DemandItemView view);
    }

}
