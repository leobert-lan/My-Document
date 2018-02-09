package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.mvp.model.bean.CommentConfigResBean;
import com.lht.customwidgetlib.rating.RatingStar;

import java.util.HashMap;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CommentItemViewProvrderImpl
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/9.
 */

public class CommentConfigViewProvrderImpl implements IListItemViewProvider<CommentConfigResBean> {

    private final LayoutInflater mInflater;
    private final ListAdapter2.ICustomizeListItem2<CommentConfigResBean, ViewHolder> iSetCallbackForListItem;
    private HashMap<String, Integer> map;

    public CommentConfigViewProvrderImpl(LayoutInflater inflater,
                                         ListAdapter2.ICustomizeListItem2<CommentConfigResBean, ViewHolder>
                                                 iSetCallbackForListItem, HashMap<String, Integer> map) {
        this.mInflater = inflater;
        this.iSetCallbackForListItem = iSetCallbackForListItem;
        this.map = map;
    }

    @Override
    public View getView(int position, final CommentConfigResBean item,
                        View convertView, ViewGroup parent) {
        // comment_comment_item

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comment_comment_item, null);
            holder.commentItem = (TextView) convertView.findViewById(R.id.commentitem_tv_description);
            holder.commentRatingBar = (RatingStar) convertView.findViewById(R.id.commentitem_ratingBar_score);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.commentItem.setText(item.getName() + ":");
        holder.commentRatingBar.setOnRatingChangedListener(new RatingStar.OnRatingChangedListener() {
            @Override
            public void onRatingLevel(int level) {
                map.put(item.getAid(), level);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private TextView commentItem;
        private com.lht.customwidgetlib.rating.RatingStar commentRatingBar;
    }


}
