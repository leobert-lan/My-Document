package com.lht.cloudjob.share;

import com.lht.cloudjob.R;
import com.lht.cloudjob.util.debug.DLog;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.widget.Toast;

/**
 * @ClassName: DefaultShareIMPL
 * @Description: TODO
 * @date 2016年3月25日 下午3:04:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class DefaultShareIMPL implements IShare {

	private final Activity mActivity;

	public DefaultShareIMPL(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public void share2QQ(ShareBean bean) {
		if (bean instanceof QShareImageBean) {
			qqShare((QShareImageBean) bean);
		} else if (bean instanceof QShareImage7TextBean) {
			qqShare((QShareImage7TextBean) bean);
		} else {
			DLog.e(getClass(), new DLog.LogLocation(), "call qqshare without a qqshare bean");
		}
	}

	private void qqShare(QShareImageBean bean) {
		QQShareUtil.qShareImageOnly(bean, mActivity, new DefaultQShareListener());
	}

	private void qqShare(QShareImage7TextBean bean) {
		QQShareUtil.qShareImage7Text(bean, mActivity, new DefaultQShareListener());
	}

	@Override
	public void share2QZone(ShareBean bean) {
		this.share2QQ(bean);
		// 业务区分是通过bean内的flag的
	}

	@Override
	public void share2Wechat(ShareBean bean) {
		if (bean instanceof WeChatShareBean) {
			new WeChatShareUtil(mActivity).weChatShareWebPage((WeChatShareBean) bean);
		} else {
			DLog.e(getClass(), new DLog.LogLocation(), "call wechat share with a error bean");
		}
	}

	@Override
	public void share2WechatFC(ShareBean bean) {
		this.share2Wechat(bean);
	}

	@Override
	public void share2Sina(ShareBean bean) {
		if (bean instanceof SinaShareBean) {
			SinaShareUtil.shareByAPI(mActivity, (SinaShareBean) bean);
		} else {
			DLog.e(getClass(), new DLog.LogLocation(), "call sina share without a sina bean");
		}
	}

	/**
	 * @ClassName: DefaultQShareListener
	 * @Description: QQ相关分享回调接口实现类
	 * @date 2016年3月25日 下午3:07:10
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	class DefaultQShareListener implements IQShareListener {

		@Override
		public void onCancel() {
			Toast.makeText(mActivity,mActivity.getString(R.string.v1010_default_tp_share_exit), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(Object arg0) {
			Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_share_success), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(UiError arg0) {
			Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_share_failure), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onQQNotAvailable() {
			Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_QQnotinstalled), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onQZoneNotAvailable() {
			Toast.makeText(mActivity, mActivity.getString(R.string.v1010_default_tp_failure_qqzonenotinstalled), Toast.LENGTH_SHORT).show();
		}

	}

}
