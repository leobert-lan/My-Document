package com.lht.pan_android.adapter;

import java.util.ArrayList;

import com.lht.pan_android.R;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.view.SSVListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: SearchUserCardAdapter
 * @Description: TODO
 * @date 2016年1月26日 下午4:25:45
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SearchUserSSVListAdapter extends SSVListAdapter {

	private ArrayList<SearchUserItemBean> mData;

	private final Context mContext;

	public SearchUserSSVListAdapter(Context context, ArrayList<SearchUserItemBean> data) {
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < getCount())
			return mData.get(position);
		else
			return new SearchUserItemBean();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView != null && convertView.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.ssvlist_cell, null);
			holder = new ViewHolder();
			holder.nickname = (TextView) convertView.findViewById(R.id.ssvlist_cell_name);
			holder.delete = (ImageView) convertView.findViewById(R.id.ssvlist_cell_delete);
			convertView.setTag(holder);
		}

		holder.nickname.setText(mData.get(position).getNickname());
		holder.delete.setTag(getItem(position));
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchUserItemBean bean = (SearchUserItemBean) v.getTag();
				int index = getIndex(bean);
				delete(index, null);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		TextView nickname;
		ImageView delete;
	}

	@Override
	public void sync(ArrayList<SearchUserItemBean> dataSet, SearchUserItemBean bean) {
		mData.clear();
		for (SearchUserItemBean bean2 : dataSet) {
			if (bean2.isSelect())
				mData.add(bean2);
		}
		notifyDataSetChanged();
	}

	@Override
	public void delete(int index, SearchUserItemBean bean) {
		mData.get(index).setSelect(false);
		notifyDataSetChanged();
		if (deleteListener != null)
			deleteListener.onItemDelete(mData.get(index).getUsername());
	}

	public int getIndex(SearchUserItemBean bean) {
		if (mData.contains(bean))
			return mData.indexOf(bean);
		else
			return -1;
	}

	@Override
	protected View getView(SearchUserItemBean bean) {
		return null;
	}

}
