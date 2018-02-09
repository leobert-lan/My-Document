package com.lht.pan_android.clazz;

import com.lht.pan_android.view.TabView;

/**
 * @ClassName: TabItem
 * @Description:
 * @date 2015年11月24日 上午8:51:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TabItem {

	private TabView mTabView;

	private int mSelecImgResid;

	private int mDefImgResid;

	private int mLabelResid;

	private int mSelcColorResid;

	private int mDefColorResid;

	public TabItem(TabView tab, int simgid, int dimgid, int lablerid, int scolorid, int dcolorid) {
		this.mTabView = tab;
		this.mSelecImgResid = simgid;
		this.mDefImgResid = dimgid;
		this.mLabelResid = lablerid;
		this.mSelcColorResid = scolorid;
		this.mDefColorResid = dcolorid;
	}

	public TabView getTabView() {
		return mTabView;
	}

	public void setTabView(TabView mTabView) {
		this.mTabView = mTabView;
	}

	public int getSelecImgResid() {
		return mSelecImgResid;
	}

	public void setSelecImgResid(int mSelecImgResid) {
		this.mSelecImgResid = mSelecImgResid;
	}

	public int getDefImgResid() {
		return mDefImgResid;
	}

	public void setDefImgResid(int mDefImgResid) {
		this.mDefImgResid = mDefImgResid;
	}

	public int getLabelResid() {
		return mLabelResid;
	}

	public void setLabelResid(int mLabelResid) {
		this.mLabelResid = mLabelResid;
	}

	public int getSelcColorResid() {
		return mSelcColorResid;
	}

	public void setSelcColorResid(int mSelcColorResid) {
		this.mSelcColorResid = mSelcColorResid;
	}

	public int getDefColorResid() {
		return mDefColorResid;
	}

	public void setDefColorResid(int mDefColorResid) {
		this.mDefColorResid = mDefColorResid;
	}

}
