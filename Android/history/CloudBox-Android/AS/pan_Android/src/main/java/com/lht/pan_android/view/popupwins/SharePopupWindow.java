package com.lht.pan_android.view.popupwins;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IPreventPenetrate;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.asyncProtected.ShareToUserActivity;
import com.lht.pan_android.util.activity.SharePublicAndPrivateUtil;
import com.lht.pan_android.util.activity.SharePublicAndPrivateUtil.SharePublicCallBack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * @ClassName: SharePopupWindow
 * @Description: 分享操作表
 * @date 2016年4月7日 上午11:34:41
 * 
 * @author leobert.lan
 * @version 1.0
 * @since JDK 1.7
 */
public class SharePopupWindow extends CustomPopupWindow {

	private SharePublicAndPrivateUtil sharePublicUtil;
	private LinearLayout sharePublic;
	private LinearLayout sharePrivate;
	private LinearLayout shareCustomer;
	private View contentView;
	private SharePopUpModifyPwd sharePopUpModifyPwd;
	private String url = null;
	private String path;

	private final String fileName;

	public SharePopupWindow(Activity context, String path, IPreventPenetrate ippl) {
		super(context, ippl);

		this.path = path;
		sharePublicUtil = new SharePublicAndPrivateUtil(mActivity);

		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.popwindow_share_dialog, null);
		sharePublic = (LinearLayout) contentView.findViewById(R.id.linear_share_public);
		sharePrivate = (LinearLayout) contentView.findViewById(R.id.linear_share_private);
		shareCustomer = (LinearLayout) contentView.findViewById(R.id.linear_share_customer);

		sharePublic.setOnClickListener(itemsOnClick);
		sharePrivate.setOnClickListener(itemsOnClick);
		shareCustomer.setOnClickListener(itemsOnClick);

		this.setContentView(contentView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);

		this.setAnimationStyle(R.style.AnimationPreview);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

		// TODO 写到show中
		backgroundAplha(0.4f);
		if (path.contains("/")) {
			fileName = path.substring(path.lastIndexOf("/") + 1);
		} else {
			fileName = "来自创意云盘的分享";
		}
	}

	protected StringEntity getShareItemEntity(String pwd) {
		JSONObject jObj = new JSONObject();
		StringEntity ret = null;
		try {
			jObj.put("path", path);
			jObj.put("permission", "all");
			jObj.put("password", pwd);
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.linear_share_public:
				doPublicShare();
				break;
			case R.id.linear_share_private:
				doPrivateShare();
				break;
			case R.id.linear_share_customer:
				doCloudBoxUserShare();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void dismiss() {
		backgroundAplha(1.0f);
		super.dismiss();
	}

	/**
	 * @Title: doCloudBoxUserShare
	 * @Description: 平台内用户分享，启动新的activity
	 * @author: leobert.lan
	 */
	protected void doCloudBoxUserShare() {
		dismiss();
		getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_USERS);
		Intent intent = new Intent(mActivity, ShareToUserActivity.class);
		intent.putExtra(ShareToUserActivity.INTENT_KEY_PATH, path);
		mActivity.startActivity(intent);
	}

	/**
	 * @Title: doPrivateShare
	 * @Description: 私有分享 同步耗时
	 * @author: leobert.lan
	 */
	protected void doPrivateShare() {
		dismiss();
		getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_PRIVATE);
		sharePopUpModifyPwd = new SharePopUpModifyPwd(mActivity, iPreventPenetrate);
		sharePopUpModifyPwd.setTitle(R.string.share_setting_pwd);
		sharePopUpModifyPwd.setPositiveButton(R.string.string_sure);
		sharePopUpModifyPwd.setNegativeButton(R.string.string_cancel);
		sharePopUpModifyPwd.setPositiveClickListener(pwdModifySubmitListener);
		sharePopUpModifyPwd.show();
	}

	/**
	 * @Title: doPublicShare
	 * @Description: 公开分享 同步耗时，回调
	 * @author: leobert.lan
	 */
	protected void doPublicShare() {
		dismiss();
		getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_PUBLIC);
		url = IUrlManager.SharePublicAndPrivateUrl.PUBLICSHAREFUNCTION;
		sharePublicUtil.setCallBack(sharePublicCallBack);
		sharePublicUtil.sharePublic(url, getShareItemEntity(null), fileName);
	}

	OnPositiveClickListener pwdModifySubmitListener = new OnPositiveClickListener() {
		@Override
		public void onPositiveClick() {
			url = IUrlManager.SharePublicAndPrivateUrl.PRIVATESHAREFUNCTION;
			if (sharePopUpModifyPwd.getContent().equals("")) {

				AlertDialogWithImageCreator pwdNullAlertCreator = new AlertDialogWithImageCreator(mActivity,
						iPreventPenetrate);

				pwdNullAlertCreator.setContentRes(R.string.share_pwd_null);
				pwdNullAlertCreator.setPositiveButton(R.string.string_sure);

				pwdNullAlertCreator.setPositiveClickListener(new OnPositiveClickListener() {

					@Override
					public void onPositiveClick() {
						sharePopUpModifyPwd.show();
					}
				});
				pwdNullAlertCreator.create().show();
			} else {
				sharePopUpModifyPwd.dismiss();
				sharePublicUtil.sharePublic(url, getShareItemEntity(sharePopUpModifyPwd.getContent()), fileName);
				sharePublicUtil.setCallBack(sharePublicCallBack);
			}
		}
	};

	/**
	 * sharePublicCallBack:公开分享成功的回调
	 */
	SharePublicCallBack sharePublicCallBack = new SharePublicCallBack() {

		@Override
		public void onSuccess(String link, String fileName) {

			// TODO 在这里改变逻辑
			ShowPopWindow(link, fileName);
		}

		@Override
		public void onFailure() {
			AlertDialogWithImageCreator shareFailureAlertCreator = new AlertDialogWithImageCreator(mActivity,
					iPreventPenetrate);

			shareFailureAlertCreator.setContentRes(R.string.share_file_null);
			shareFailureAlertCreator.setImgRes(R.drawable.shib);
			shareFailureAlertCreator.setPositiveButton(R.string.string_sure);
			shareFailureAlertCreator.create().show();
		}
	};

	public UMengActivity getActivity() {
		return (UMengActivity) mActivity;
	}

	private void ShowPopWindow(String link, String name) {
		ThirdPartySharePopWins wins = TPSPWCreater.create(mActivity);
		wins.setShareContent(link);
		wins.setShareSummary(name);
		wins.show();
	}

	@Override
	void init() {
		// TODO Auto-generated method stub

	}
}
