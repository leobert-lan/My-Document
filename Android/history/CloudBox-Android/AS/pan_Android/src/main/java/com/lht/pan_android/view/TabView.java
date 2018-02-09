package com.lht.pan_android.view;

import com.lht.pan_android.Interface.OnTabItemClickListener;
import com.lht.pan_android.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @ClassName: TabView
 * @Description: 主界面底部栏元素，实现
 *               <li>添加图片和文字</li>
 *               <li>控制默认大小和间距</li>
 *               <li>状态改变时的效果</li>
 * @date 2015年11月23日 下午2:44:34
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class TabView extends LinearLayout {
	private ImageView imgTabIcon;
	private TextView txtTabName;
	private int mIndex = -1;
	private Context mContext;

	public TabView(Context context) {
		this(context, null);
	}

	public TabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public void setWidth(int w) {
		getLayoutParams().width = w;
	}

	public void setIconSize(int edge) {

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imgTabIcon.getLayoutParams();
		linearParams.height = edge;
		linearParams.width = edge;
		imgTabIcon.setLayoutParams(linearParams);
		imgTabIcon.setScaleType(ScaleType.FIT_CENTER);
	}

	/**
	 * @Title: init
	 * @Description: TODO
	 * @author: leobert.lan
	 */
	private void init() {
		setOnClickListener(null);
		imgTabIcon = new ImageView(mContext);
		txtTabName = new TextView(mContext);
		txtTabName.setGravity(Gravity.CENTER_HORIZONTAL);
		// TODO 字号
		txtTabName.setTextSize(11);
		txtTabName.setBackgroundResource(android.R.color.transparent);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_HORIZONTAL);

		imgTabIcon.setBackgroundResource(android.R.color.transparent);
		addView(imgTabIcon);
		// 注意此处，需要先add再设置否则空指针
		imgTabIcon.setAdjustViewBounds(true);
		// 注意，文字在下
		addView(txtTabName);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) txtTabName.getLayoutParams();
		linearParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		linearParams.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		linearParams.setMargins(0, 0, 0, Dp2Px(mContext, 7));
		txtTabName.setLayoutParams(linearParams);
	}

	private int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private int mSelecImgResid;

	private int mDefImgResid;

	private int mLableResid;

	private int mSelcColorResid;

	private int mDefColorResid;

	private boolean isSelect;

	private OnTabItemClickListener mOnTabItemClickListener;

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public int getSelecImgResid() {
		return mSelecImgResid;
	}

	public void setSelecImgResid(int selecImgResid) {
		this.mSelecImgResid = selecImgResid;
	}

	public int getDefImgResid() {
		return mDefImgResid;
	}

	public void setDefImgResid(int defImgResid) {
		this.mDefImgResid = defImgResid;
		imgTabIcon.setImageResource(defImgResid);
	}

	public int getLableResid() {
		return mLableResid;
	}

	public void setLableResid(int lableResid) {
		this.mLableResid = lableResid;
		txtTabName.setText(mContext.getResources().getString(lableResid));
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

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public ImageView getImgTabIcon() {
		return imgTabIcon;
	}

	public TextView getTxtTabName() {
		return txtTabName;
	}

	public void setOnTabItemClickListener(OnTabItemClickListener l) {
		this.mOnTabItemClickListener = l;
	}

	/**
	 * 改变选中状态
	 * 
	 * @see android.view.View#performClick()
	 */
	@Override
	public boolean performClick() {
		mOnTabItemClickListener.onTabItemClick(mIndex);
		return super.performClick();
	}

	/**
	 * @Title: changeState
	 * @Description: 改变状态
	 * @author: leobert.lan
	 */
	public void changeState(boolean select) {
		isSelect = select;
		if (isSelect) {
			imgTabIcon.setImageResource(mSelecImgResid);
			txtTabName.setTextColor(mContext.getResources().getColor(mSelcColorResid));
		} else {
			imgTabIcon.setImageResource(mDefImgResid);
			txtTabName.setTextColor(mContext.getResources().getColor(mDefColorResid));
		}
	}
}
