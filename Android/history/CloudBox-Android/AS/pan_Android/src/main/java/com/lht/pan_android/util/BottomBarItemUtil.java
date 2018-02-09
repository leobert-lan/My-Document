package com.lht.pan_android.util;

import java.util.ArrayList;

import com.lht.pan_android.Interface.OnSelectTab;
import com.lht.pan_android.Interface.OnTabItemClickListener;
import com.lht.pan_android.clazz.TabItem;
import com.lht.pan_android.view.TabView;

/**
 * @ClassName: BottomBarItemUtil
 * @Description: TODO
 * @date 2015年11月24日 上午9:02:25
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class BottomBarItemUtil implements OnSelectTab {

	private ArrayList<TabItem> mList = new ArrayList<TabItem>();

	private final OnTabItemClickListener mTabItemClickListener;

	public BottomBarItemUtil(ArrayList<TabItem> list, OnTabItemClickListener l) {
		this.mList = list;
		mTabItemClickListener = l;
	}

	public void create() {
		for (int i = 0; i < mList.size(); i++) {
			TabItem tab = mList.get(i);
			TabView v = tab.getTabView();
			v.setIndex(i);
			v.setSelecImgResid(tab.getSelecImgResid());
			v.setDefImgResid(tab.getDefImgResid());
			v.setLableResid(tab.getLabelResid());
			v.setSelcColorResid(tab.getSelcColorResid());
			v.setDefColorResid(tab.getDefColorResid());
			v.setOnTabItemClickListener(mTabItemClickListener);
		}
	}

	@Override
	public void onSelect(int index) {
		for (int i = 0; i < mList.size(); i++) {
			if (i == index)
				mList.get(i).getTabView().changeState(true);
			else
				mList.get(i).getTabView().changeState(false);
		}

	}

}
