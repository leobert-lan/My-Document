package com.lht.cloudjob.adapter.viewprovider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;

/**
 * Created by chhyu on 2016/11/8.
 */
public class ImagesAttachmentProviderImpl implements IListItemViewProvider<DemandInfoResBean.AttachmentExt> {

    private LayoutInflater inflater;
    private ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt, ViewHolder>
            iCustomizeListItem;

    public ImagesAttachmentProviderImpl(LayoutInflater inflater,
                                        ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt,
                                                ViewHolder> iCustomizeListItem) {
        this.inflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    @Override
    public View getView(int position, DemandInfoResBean.AttachmentExt item, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.iamges_attachment_item, null);
            holder.ivImageAttachment = (ImageView) convertView.findViewById(R.id.iv_image_attachment);
            convertView.setTag(holder);
        }

        if (iCustomizeListItem != null) {
            iCustomizeListItem.customize(position, item, convertView, holder);
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView ivImageAttachment;
    }
}
