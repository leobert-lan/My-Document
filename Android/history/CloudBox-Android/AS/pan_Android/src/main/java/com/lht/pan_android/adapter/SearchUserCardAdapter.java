package com.lht.pan_android.adapter;

import java.util.ArrayList;

import com.lht.pan_android.R;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.view.SSVCardAdapter;

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
public class SearchUserCardAdapter extends SSVCardAdapter {

	private ArrayList<SearchUserItemBean> mData;

	private final Context mContext;

	public SearchUserCardAdapter(Context context, ArrayList<SearchUserItemBean> data) {
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

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.cardviewcell, parent);
			holder = new ViewHolder();
			holder.nickname = (TextView) convertView.findViewById(R.id.cardview_cell_name);
			holder.delete = (ImageView) convertView.findViewById(R.id.cardview_cell_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nickname.setText(mData.get(position).getNickname());
		holder.delete.setTag(getItem(position));
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchUserItemBean bean = (SearchUserItemBean) v.getTag();
				delete(0, bean);
			}
		});

		return convertView;
	}

	@Override
	public View getView(SearchUserItemBean bean) {
		final ViewHolder holder;

		View convertView = LayoutInflater.from(mContext).inflate(R.layout.cardviewcell, null);
		holder = new ViewHolder();
		holder.nickname = (TextView) convertView.findViewById(R.id.cardview_cell_name);
		holder.delete = (ImageView) convertView.findViewById(R.id.cardview_cell_delete);
		convertView.setTag(holder);

		holder.nickname.setText(bean.getNickname());
		holder.delete.setTag(bean);
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				SearchUserItemBean bean = (SearchUserItemBean) v.getTag();
				delete(0, bean);
			}
		});

		return convertView;

	}

	private class ViewHolder {
		TextView nickname;
		ImageView delete;
	}

	@Override
	protected void delete(int index, SearchUserItemBean bean) {
		deleteListener.onItemDelete(bean.getUsername());
	}

	@Override
	public void sync(ArrayList<SearchUserItemBean> dataSet, SearchUserItemBean bean) {
		mData = dataSet;
		super.sync(dataSet, bean);
	}

	public int getIndex(SearchUserItemBean bean) {
		if (mData.contains(bean))
			return mData.indexOf(bean);
		else
			return -1;
	}

}
