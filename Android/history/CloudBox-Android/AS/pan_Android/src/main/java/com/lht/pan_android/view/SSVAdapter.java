package com.lht.pan_android.view;

import java.util.ArrayList;

import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.view.ShareSelectView.IDataSetChanged;

import android.view.View;
import android.widget.BaseAdapter;

/**
 * @ClassName: SSVadapter
 * @Description: TODO
 * @date 2016年1月25日 上午9:46:42
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class SSVAdapter extends BaseAdapter {

	private IDataSetChanged dataSetChangedlistener;

	public void setDataSetChangedListener(IDataSetChanged listener) {
		dataSetChangedlistener = listener;
	}

	public void sync(ArrayList<SearchUserItemBean> dataSet, SearchUserItemBean bean) {
		if (bean == null)
			return;
		if (bean.isSelect()) {
			add(bean);
		} else {
			remove(bean);
		}
	}

	private void add(SearchUserItemBean bean) {
		notifyDataSetChanged();
		if (dataSetChangedlistener == null)
			throw new NullPointerException("dev debug:set datasetchangedlinsener in your code");
		dataSetChangedlistener.onAdd(getView(bean), bean.getUsername());
	}

	private void remove(SearchUserItemBean bean) {
		if (dataSetChangedlistener == null)
			throw new NullPointerException("dev debug:set datasetchangedlinsener in your code");
		dataSetChangedlistener.onRemove(getView(bean), bean.getUsername());
	}

	protected abstract View getView(SearchUserItemBean bean);

	/**
	 * @Title: delete
	 * @Description: 自己删除使用，不供外部调用
	 * @author: leobert.lan
	 * @param index
	 */
	protected abstract void delete(int index, SearchUserItemBean bean);

	protected OnItemDeleteListener deleteListener;

	public void setOnItemDeleteListener(OnItemDeleteListener l) {
		deleteListener = l;
	}

	public interface OnItemDeleteListener {
		void onItemDelete(String username);
	}
}
