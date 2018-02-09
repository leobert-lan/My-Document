package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.util.file.FileUtils;

/**
 * Created by chhyu on 2016/11/8.
 */

public class FilesAttachmentProviderImpl implements IListItemViewProvider<DemandInfoResBean.AttachmentExt> {

    private LayoutInflater inflater;

    private ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt, ViewHolder> iCustomizeListItem;

    public FilesAttachmentProviderImpl(LayoutInflater inflater,
                                       ListAdapter2.ICustomizeListItem2<DemandInfoResBean.AttachmentExt,
                                               ViewHolder> iCustomizeListItem) {
        this.inflater = inflater;
        this.iCustomizeListItem = iCustomizeListItem;
    }

    @Override
    public View getView(int position, DemandInfoResBean.AttachmentExt item, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.file_attachment_item, null);
            holder.ivFileLink = (ImageView) convertView.findViewById(R.id.iv_file_link);
            holder.tvFileAttachment = (TextView) convertView.findViewById(R.id.tv_file_attachment);
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            convertView.setTag(holder);
        }
        holder.tvFileAttachment.setText(item.getFile_name());
        String fileSize = FileUtils.calcSize(item.getFile_size());
        holder.tvFileSize.setText("(" + fileSize + ")");

        if (iCustomizeListItem != null) {
            iCustomizeListItem.customize(position, item, convertView, holder);
        }
        return convertView;
    }

   public class ViewHolder {
        private ImageView ivFileLink;
        private TextView tvFileAttachment;
        private TextView tvFileSize;
    }
}
