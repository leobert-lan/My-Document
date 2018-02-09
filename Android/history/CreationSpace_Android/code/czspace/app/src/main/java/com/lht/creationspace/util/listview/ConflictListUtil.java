package com.lht.creationspace.util.listview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lht.creationspace.adapter.AbsListAdapter;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.listview
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ConflictListUtil
 * <p><b>Description</b>: 用来处理嵌套的list
 * Created by leobert on 2016/7/28.
 */
public class ConflictListUtil {

    /**
     * set the height of the listView dynamically,make sure the adapter will be set to the listView
     *
     * @param listView the listView to be adjusted
     * @param adapter  the adapter you will set ,to work for the listView
     */
    public static void setDynamicHeight(ListView listView, AbsListAdapter adapter) {
        if (adapter == null) {
            return;
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
//        +listView.getPaddingTop() + listView.getPaddingBottom();
        listView.setLayoutParams(params);
    }
}
