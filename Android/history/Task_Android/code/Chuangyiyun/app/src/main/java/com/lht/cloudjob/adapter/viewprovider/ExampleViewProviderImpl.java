package com.lht.cloudjob.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.adapter.IListItemViewProvider;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;

/**
 * @package com.lht.cloudjob.adapter.viewprovider
 * @project AndroidBase
 * @classname ExampleViewProviderImpl
 * @description: 这里是一个例子，好一点的做法是让需要使用adapter的activity实现该接口
 * 更好的做法是再定义一个接口，抽象添加回调的行为
 * Created by leobert on 2016/4/1.
 */
public class ExampleViewProviderImpl implements IListItemViewProvider {

    private final LayoutInflater mInflater;

    private final ICustomizeListItem iCustomizeListItem;

    public ExampleViewProviderImpl(LayoutInflater inflater, ICustomizeListItem iSetCallback) {
        mInflater = inflater;
        iCustomizeListItem = iSetCallback;
    }

    @Override
    public View getView(final int position, Object item, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
            if (convertView != null && convertView.getTag() != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
            holder = new ViewHolder();
            //TODO just for example,change the layout resource
            convertView = mInflater.inflate(R.layout.listitem_example, null);
            //TODO bind with holder

            holder.tv = (TextView) convertView.findViewById(R.id.list_item_tv);

            convertView.setTag(holder);
        }
        //ATTENTION: 这里我直接cast String了，相应的我构造参数的时候也会直接给String，设计时，这个item对应的是Bean
        holder.tv.setText((String)item);

        //create a package to package the viewholder class,this will make your code less flexible but more standard
//        ViewHolderPkg viewHolderPkg = new ViewHolderPkg(holder);

        // do some works like setting via a callback interface
        // [I call it a callback interface because here is a callback method,and the interface is only called here]
        if (iCustomizeListItem != null)
            iCustomizeListItem.customize(position,convertView, holder);
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv;
    }
}
