package com.lht.pan_android.adapter;

import com.lht.pan_android.util.string.StringUtil;

import android.widget.BaseAdapter;

/**
 * @ClassName: AbsUserListAdapter
 * @Description: TODO
 * @date 2016年1月29日 上午10:01:07
 * 
 * @author leobert.lan
 * @version 1.0
 */
public abstract class AbsUserListAdapter extends BaseAdapter {

	protected String formateDescendantFiles(String descendantFiles) {
		if (StringUtil.isEmpty(descendantFiles))
			return null;
		else
			return "(" + descendantFiles + ")";
	}

}
