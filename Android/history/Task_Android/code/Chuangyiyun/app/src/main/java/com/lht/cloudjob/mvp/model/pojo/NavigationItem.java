package com.lht.cloudjob.mvp.model.pojo;


import android.graphics.drawable.Drawable;

import com.lht.cloudjob.util.SideBarNavigationItemDatas;

public class NavigationItem {
    private String mText;
    private String mSub;
    private Drawable mDrawable;
    private SideBarNavigationItemDatas.Style style;
	public NavigationItem(String text, Drawable drawable, SideBarNavigationItemDatas.Style style) {
        mText = text;
        mDrawable = drawable;
        this.style=style;
    }

    public NavigationItem(String mText, String mSub, Drawable mDrawable, SideBarNavigationItemDatas.Style style) {
        this.mText = mText;
        this.mSub = mSub;
        this.mDrawable = mDrawable;
        this.style = style;
    }

    public SideBarNavigationItemDatas.Style getStyle() {
		return style;
	}

	public void setStyle(SideBarNavigationItemDatas.Style style) {
		this.style = style;
	}
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }


    public String getSub() {
        return mSub;
    }

    public void setSub(String mSub) {
        this.mSub = mSub;
    }

}
