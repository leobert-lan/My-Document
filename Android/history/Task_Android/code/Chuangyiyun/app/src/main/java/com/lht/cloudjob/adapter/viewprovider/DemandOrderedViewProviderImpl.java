package com.lht.cloudjob.adapter.viewprovider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.lht.cloudjob.R;
import com.lht.cloudjob.customview.DemandItemView;
import com.lht.cloudjob.customview.IDemandItemTranser;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> DemandOrederdViewProviderImpl
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/20.
 */
public class DemandOrderedViewProviderImpl implements IListItemViewProvider<DemandItemData> {
    private final Context context;

    public DemandOrderedViewProviderImpl(Context context) {
        this.context = context;
    }



    @Override
    public View getView(int position, DemandItemData item, View convertView, ViewGroup parent) {

        DemandItemView temp;
        if (convertView != null && convertView instanceof DemandItemView) {
            temp = (DemandItemView) convertView;
            convertView = temp;
        } else {
            temp = new DemandItemView(context);
            convertView = temp;
        }

        IDemandItemTranser transer = temp.newDemandItemTranser(DemandItemView.Type.ORIGINAL);
        transer.trans();
        temp.setData(item);

        DemandItemView.DemandItemViewHolder viewHolder = temp.getViewHolder();
        if (item.isWorkSelected()) {
            viewHolder.tag.setVisibility(View.VISIBLE);
            viewHolder.tag.setImageResource(R.drawable.v1010_drawable_icon_success);
        } else if (item.isWorkOptional()) {
            viewHolder.tag.setVisibility(View.VISIBLE);
            viewHolder.tag.setImageResource(R.drawable.v1011_drawable_icon_alternate);
        } else {
            viewHolder.tag.setVisibility(View.GONE);
        }

        if (iSetCallbacksAndTrans != null) {
            iSetCallbacksAndTrans.onCallback(temp,item);
        }

        return convertView;
    }

//    private void fillLine3(DemandItemView.DemandItemViewHolder viewHolder,DemandItemData item) {
//        if
//        ArrayList<Integer> operate = item.get
//
//    }

    private ISetCallbacksAndTrans iSetCallbacksAndTrans;

    public void setiSetCallbacksAndTrans(ISetCallbacksAndTrans iSetCallbacksAndTrans) {
        this.iSetCallbacksAndTrans = iSetCallbacksAndTrans;
    }

    public interface ISetCallbacksAndTrans {
        void onCallback(DemandItemView demandItemView,DemandItemData item);
    }
}
