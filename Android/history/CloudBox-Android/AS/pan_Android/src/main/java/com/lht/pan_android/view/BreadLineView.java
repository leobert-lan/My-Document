package com.lht.pan_android.view;

import java.util.ArrayList;

import com.lht.pan_android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: BreadLineView
 * @Description: 自定义面包线控件
 * @date 2016年1月5日 上午11:04:20
 * 
 * @author zhangbin
 * @version 1.0
 */
@SuppressLint("ResourceAsColor")
public class BreadLineView extends LinearLayout {

	private Context mContext;

	private ArrayList<String> paths;

	private TextView tv;

	public BreadLineView(Context context) {
		super(context);
		this.mContext = context;
		paths = new ArrayList<String>();
	}

	/**
	 * @Title: updateView
	 * @Description: 更新导航
	 * @author: zhangbin
	 */
	public void updateView(String path) {

		if (paths == null) {
			throw new NullPointerException("the paths you give is null");
		}
		removeAllViews();

		String[] temp = path.split("/");
		// Log.d("amsg", "size:" + temp.length);
		paths.clear();
		paths.add(getResources().getString(R.string.title_activity_cloud_box));
		for (int i = 1; i < temp.length; i++) {
			paths.add(temp[i]);
		}

		for (int i = 0; i < paths.size(); i++) {
			tv = new TextView(mContext);
			tv.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setTextColor(getResources().getColor(R.color.gray_999));
			tv.setPadding(10, 5, 10, 5);
			tv.setGravity(Gravity.CENTER);
			tv.setText(paths.get(i));
			tv.setBackgroundResource(R.color.gray_ed);
			// 设置位置标签
			tv.setTag(i);
			addView(tv);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String path = getPath((Integer) v.getTag());
					updateView(path);
					tv.setTextColor(getResources().getColor(R.color.gray_333));
					if (breadItemClickListener == null)
						throw new NullPointerException("set onbreak...lisener at first");
					breadItemClickListener.postPath(path);
				}
			});
			if (i == paths.size() - 1) {
				tv.setBackgroundResource(R.color.gray_dc);
				tv.setTextColor(getResources().getColor(R.color.gray_333));
				break;
			}
			ImageView img = new ImageView(mContext);
			img.setBackgroundColor(mContext.getResources().getColor(R.color.gray_ed));
			if (i == paths.size() - 2)
				img.setBackgroundColor(mContext.getResources().getColor(R.color.gray_dc));
			img.setImageResource(R.drawable.mianbx1);
			addView(img);
		}
		requestLayout();

		if (onUpdatedListener != null)
			onUpdatedListener.onUpdated();
	}

	protected String getPath(Integer position) {
		String path = "";
		for (int i = 0; i <= position; i++) {
			path += paths.get(i) + "/";
		}
		if (position == 0) {
			path = path + "/";
		}

		return path.substring(path.indexOf("/"), path.length() - 1);
	}

	public BreadLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BreadLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private OnBreadItemClickListener breadItemClickListener = null;

	public void setBreadItemClickListener(OnBreadItemClickListener breadItemClickListener) {
		this.breadItemClickListener = breadItemClickListener;
	}

	/**
	 * @ClassName: OnBreadItemClickListener
	 * @Description: TODO
	 * @date 2016年1月6日 下午3:50:25
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	public interface OnBreadItemClickListener {
		void postPath(String path);
	}

	@Override
	public void addView(View child) {
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in2);
		child.startAnimation(animation);
		super.addView(child);
	}

	@Override
	public void removeView(View view) {
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out2);
		view.startAnimation(animation);
		super.removeView(view);
	}

	private OnUpdatedListener onUpdatedListener;

	public void setOnUpdatedListener(OnUpdatedListener l) {
		this.onUpdatedListener = l;
	}

	public interface OnUpdatedListener {
		void onUpdated();
	}
}
