package com.lht.pan_android.adapter;

import java.util.ArrayList;

import com.lht.pan_android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @ClassName: SearchHistroyAdapter
 * @Description: TODO
 * @date 2016年4月22日 下午1:37:10
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SearchHistroyAdapter extends BaseAdapter {

	private ArrayList<String> mLiData;
	private final OnClickListener itemClickListener;
	private final Context mContext;

	public SearchHistroyAdapter(Context context, ArrayList<String> lidata, OnClickListener listener) {
		mLiData = lidata;
		mContext = context;
		itemClickListener = listener;
	}

	@Override
	public int getCount() {
		return mLiData.size();
	}

	@Override
	public Object getItem(int position) {
		return mLiData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_search_histroy, parent, false);
			holder.txtSearchKey = (TextView) convertView.findViewById(R.id.search_histroy_searchkey);
			convertView.setTag(holder);
		}
		holder.txtSearchKey.setText(mLiData.get(position));
		holder.txtSearchKey.setOnClickListener(itemClickListener);
		return convertView;
	}

	public void ReplaceAll(ArrayList<String> ret, boolean b) {
		mLiData = new ArrayList<String>();
		// for (String s : ret) {
		// mLiData.add(s);
		// }
		for (int i = ret.size() - 1; i > -1; i--) {
			mLiData.add(ret.get(i));
		}
		notifyDataSetChanged();
	}

	private final class ViewHolder {
		TextView txtSearchKey;
	}

}
