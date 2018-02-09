package com.lht.creationspace.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.AbsListAdapter;
import com.lht.creationspace.adapter.interfaces.IListItemViewProvider;

public class ExampleViewProviderImpl implements IListItemViewProvider<String> {

    private final LayoutInflater mInflater;

    private final AbsListAdapter.ICustomizeListItem<String, ViewHolder> iCustomizeListItem;

    public ExampleViewProviderImpl(LayoutInflater inflater,
                                   AbsListAdapter.ICustomizeListItem<String, ViewHolder> iSetCallback) {
        mInflater = inflater;
        iCustomizeListItem = iSetCallback;
    }

    @Override
    public View getView(final int position, String item, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_example, parent,false);
            holder.tv = (TextView) convertView.findViewById(R.id.list_item_tv);

            convertView.setTag(holder);
        }
        //ATTENTION: 这里我直接cast String了，相应的我构造参数的时候也会直接给String，设计时，这个item对应的是Bean
        holder.tv.setText(item);

        //create a package to package the viewholder class,this will make your code less flexible but more standard
//        ViewHolderPkg viewHolderPkg = new ViewHolderPkg(holder);

        // do some works like setting via a callback interface
        // [I call it a callback interface because here is a callback method,and the interface is only called here]
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position, item, convertView, holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv;
    }
}
