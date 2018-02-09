package com.lht.pan_android.view;

import com.lht.pan_android.view.ShareSelectView.ISSV;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @ClassName: SSVListView
 * @Description: TODO
 * @date 2016年1月25日 上午10:48:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SSVListView extends ListView implements ISSV {

	private final Context mContext;

	public SSVListView(Context context) {
		this(context, null);
	}

	public SSVListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SSVListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	private SSVListAdapter ssvListAdapter;

	@Override
	public void setAdapter(SSVAdapter adapter) {
		if (adapter instanceof SSVListAdapter) {
			ssvListAdapter = (SSVListAdapter) adapter;
			setAdapter((BaseAdapter) ssvListAdapter);
			adapter.notifyDataSetChanged();
		} else {
			throw new IllegalArgumentException("give me a instance of SSVListAdapter");
		}
	}

	@Override
	public SSVAdapter getSSVAdapter() {
		return ssvListAdapter;
	}

}
