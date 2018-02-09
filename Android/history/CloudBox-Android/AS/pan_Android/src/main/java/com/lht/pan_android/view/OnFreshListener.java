package com.lht.pan_android.view;

/**
 * @author leobert.lan
 * @date 2015年11月3日 如果不扩展的话，没必要设计两个接口继承之，一个接口类型，两个接口对象就行了
 */
public interface OnFreshListener {
	/**
	 * @Title: onPullStart
	 * @Description: 开始拉动时执行的回调接口方法
	 * @author: leobert.lan
	 */
	public void onPullStart();

	/**
	 * @Title: onFresh
	 * @Description: 刷新中执行的回调接口方法
	 * @author: leobert.lan
	 */
	public void onFresh();

	/**
	 * @Title: onEnd
	 * @Description: 关闭时执行的回调接口方法
	 * @author: leobert.lan
	 */
	public void onEnd();
}
