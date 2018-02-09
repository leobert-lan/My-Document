package com.lht.pan_android.view;

import android.content.Context;
import android.view.View;

/**
 * @ClassName: ShareSelectView
 * @Description: TODO
 * @date 2016年1月25日 上午9:48:42
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ShareSelectView {

	private final Context mContext;

	private final static String tag = "shareselectview";

	public ShareSelectView(Context context, AutoArrangeCardLayout cardView, SSVListView ssvListView) {
		mContext = context;
		this.cardView = cardView;
		this.ssvListView = ssvListView;
		init();
	}

	/**
	 * @Title: init
	 * @Description: TODO
	 * @author: leobert.lan
	 */
	private void init() {
		initViewMode();
	}

	private ViewMode mode;

	private void initViewMode() {
		mode = ViewMode.simple;
		changeMode(mode);
	}

	public void changeMode(ViewMode viewMode) {
		switch (viewMode) {
		case simple:
			changeViewMode2Simple();
			break;
		case list:
			changeViewMode2List();
		default:
			break;
		}
	}

	/**
	 * @Title: changeViewMode2List
	 * @Description: 切换到list视图
	 * @author: leobert.lan
	 */
	private void changeViewMode2List() {
		ssvListView.setVisibility(View.VISIBLE);
		cardView.setVisibility(View.GONE);
		ssvListView.bringToFront();
		if (modeChangedLintener != null)
			modeChangedLintener.onModeChanged(ViewMode.list);
	}

	/**
	 * @Title: changeViewMode2Simple
	 * @Description: 切换到card视图
	 * @author: leobert.lan
	 */
	private void changeViewMode2Simple() {
		cardView.setVisibility(View.VISIBLE);
		ssvListView.setVisibility(View.GONE);
		cardView.bringToFront();
		if (modeChangedLintener != null)
			modeChangedLintener.onModeChanged(ViewMode.simple);
	}

	private final AutoArrangeCardLayout cardView;

	private final SSVListView ssvListView;

	private IDataSetChanged dataSetChangedlistener = new IDataSetChanged() {

		@Override
		public void onAdd(View v, String key) {
			if (mode == ViewMode.simple)
				cardView.callAddView(v, key);
		}

		@Override
		public void onRemove(View v, String key) {
			if (mode == ViewMode.simple) {
				cardView.callRemoveViewAt(key);
			}
		}

		@Override
		public void removeAll() {
			if (mode == ViewMode.simple) {
				cardView.callRemoveAll();
			}
		}
	};

	public void setSSVCardAdapter(SSVCardAdapter adapter) {
		adapter.setDataSetChangedListener(dataSetChangedlistener);
		cardView.setAdapter(adapter);
	}

	public void setSSVListAdapter(SSVListAdapter adapter) {
		adapter.setDataSetChangedListener(dataSetChangedlistener);
		ssvListView.setAdapter(adapter);
	}

	public SSVListAdapter getSSVListAdapter() {
		return (SSVListAdapter) ssvListView.getSSVAdapter();
	}

	public SSVCardAdapter getSSVCardAdapter() {
		return (SSVCardAdapter) cardView.getSSVAdapter();
	}

	private OnModeChangedLintener modeChangedLintener;

	public void setOnModeChangedLintener(OnModeChangedLintener l) {
		this.modeChangedLintener = l;
	}

	/**
	 * @ClassName: IDataSetChanged
	 * @Description: 通知：数据源发生了变动，重新绘制
	 * @date 2016年1月25日 上午9:50:51
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface IDataSetChanged {
		void onAdd(View v, String key);

		void onRemove(View v, String key);

		void removeAll();
	}

	public interface ISSV {
		void setAdapter(SSVAdapter adapter);

		SSVAdapter getSSVAdapter();
	}

	public interface OnModeChangedLintener {
		void onModeChanged(ViewMode modeToChange);
	}

	public enum ViewMode {
		simple, list;
	}
}
