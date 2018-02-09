package com.lht.pan_android.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.greenrobot.eventbus.EventBus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.Interface.IUmengEventKey;
import com.lht.pan_android.Interface.IUrlManager;
import com.lht.pan_android.Interface.IUrlManager.MultiUserInfoApi;
import com.lht.pan_android.Interface.MainActivityIPreventPenetrate;
import com.lht.pan_android.Interface.ShareSubMenuClickListener;
import com.lht.pan_android.activity.BaseActivity;
import com.lht.pan_android.activity.UMengActivity;
import com.lht.pan_android.activity.ViewPagerItem.ShareActivity;
import com.lht.pan_android.activity.asyncProtected.ImagePreviewActivity;
import com.lht.pan_android.activity.asyncProtected.MainActivity;
import com.lht.pan_android.activity.asyncProtected.ShareToUserActivity;
import com.lht.pan_android.activity.innerWeb.WebVideoActivity;
import com.lht.pan_android.bean.MultiUserQueryItemBean;
import com.lht.pan_android.bean.PreviewBean;
import com.lht.pan_android.bean.PreviewImgInfosBean;
import com.lht.pan_android.bean.ReportBean;
import com.lht.pan_android.bean.SearchUserItemBean;
import com.lht.pan_android.bean.ShareItemBean;
import com.lht.pan_android.bean.Users;
import com.lht.pan_android.bean.VideoItem;
import com.lht.pan_android.clazz.Events.MultiUserQuery;
import com.lht.pan_android.clazz.Events.ShareReportEvent;
import com.lht.pan_android.util.TimeUtil;
import com.lht.pan_android.util.ToastUtil;
import com.lht.pan_android.util.ToastUtil.Duration;
import com.lht.pan_android.util.activity.ShareDeleteUtil;
import com.lht.pan_android.util.activity.ShareDeleteUtil.DeleteCallBack;
import com.lht.pan_android.util.activity.ShareResetPwdUtil;
import com.lht.pan_android.util.activity.ShareResetPwdUtil.ShareResetPwdCallBack;
import com.lht.pan_android.util.activity.ShareUtil.Operate;
import com.lht.pan_android.util.file.FileUtil;
import com.lht.pan_android.util.file.IPreviewFileImpl;
import com.lht.pan_android.util.file.IPreviewFileImpl.IPreviewCaller;
import com.lht.pan_android.util.string.StringUtil;
import com.lht.pan_android.view.popupwins.AlertDialogWithImageCreator;
import com.lht.pan_android.view.popupwins.CustomPopupWindow.OnPositiveClickListener;
import com.lht.pan_android.view.popupwins.PublicShareOperatePopWinsCreator;
import com.lht.pan_android.view.popupwins.SharePopUpModifyPwd;
import com.lht.pan_android.view.popupwins.SharePopUpModifyPwd.ModifyClickListener;
import com.lht.pan_android.view.popupwins.ShareUserInfoDialog;
import com.lht.pan_android.view.popupwins.TPSPWCreater;
import com.lht.pan_android.view.popupwins.ThirdPartySharePopWins;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @ClassName: TestAdapter
 * @Description: TODO
 * @date 2015年11月25日 下午5:41:42
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ShareListAdapter extends BaseAdapter
		implements IKeyManager.ShareInfo, IKeyManager.ContentType, IKeyManager.Token, IPreviewCaller, IUmengEventKey {

	private Context mContext;

	private ArrayList<ShareItemBean> mLiData;

	private String username;

	private final String auth;
	SharePopUpModifyPwd sharePopUpModifyPwd;

	// final CustomDialog publicShareWins;

	private HashMap<String, String> va = new HashMap<String, String>();

	public ShareListAdapter(ArrayList<ShareItemBean> mData, Context mContext) {
		this.mLiData = mData;
		this.mContext = mContext;
		SharedPreferences sp = mContext.getSharedPreferences(SP_TOKEN, Context.MODE_PRIVATE);
		String accessId;
		String accessToken;
		username = sp.getString(KEY_USERNAME, "");
		accessId = sp.getString(KEY_ACCESS_ID, "");
		accessToken = sp.getString(KEY_ACCESS_TOKEN, "");
		auth = "&access_id=" + accessId + "&access_token=" + accessToken;

		va.put(IKeyManager.Token.KEY_ACCESS_TOKEN, accessToken);
		va.put(IKeyManager.Token.KEY_ACCESS_ID, accessId);

	}

	private class ViewHolder {
		CheckBox mToggle;
		CheckBox mSelect;
		ImageView mIcon;
		ImageView mType;
		TextView mTitle;
		TextView mCount;
		TextView mTime;
		TextView mSize;
		RelativeLayout mClickSection;
		LinearLayout subSection;
		LinearLayout download;
		LinearLayout move;
		LinearLayout ignore;
		LinearLayout report;
	}

	@Override
	public int getCount() {
		return mLiData.size();
	}

	@Override
	public ShareItemBean getItem(int position) {
		return mLiData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		if (getCount() == 0) {
			((ShareActivity) mContext).change2EmptyView();
		} else {
			((ShareActivity) mContext).change2NormalView();
		}
		super.notifyDataSetChanged();
	}

	private void toggleClick(final int position) {
		final ShareItemBean sItemBeans = mLiData.get(position);
		if (((ShareActivity) mContext).URLFLAG == ShareActivity.FLAG_MYSHARE) {
			PublicShareOperatePopWinsCreator cancelPublicShareOpWins = new PublicShareOperatePopWinsCreator(
					((ShareActivity) mContext).getParent(), new MainActivityIPreventPenetrate());

			cancelPublicShareOpWins.setContent(R.string.share_dialog_cancle_share);
			cancelPublicShareOpWins.setNegativeButton(R.string.string_cancel);
			cancelPublicShareOpWins.setPositiveButton(R.string.string_sure);
			cancelPublicShareOpWins.setPositiveClickListener(new OnPositiveClickListener() {

				@Override
				public void onPositiveClick() {
					getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_IGNORESEND);
					ShareDeleteUtil shareDeleteUtil = new ShareDeleteUtil(mContext);
					shareDeleteUtil.setCallBack(deleteCallBack, position);
					shareDeleteUtil.delete(sItemBeans.getShareId());
				}
			});
			cancelPublicShareOpWins.create().show();
		}
	}

	ShareResetPwdUtil shareResetPwdUtil;

	/**
	 * @Title: click
	 * @Description: 文件夹进入和文件预览，TODO 以后少写这样的代码，难以阅读维护
	 * @author: zhangbin
	 * @param position
	 */
	protected void click(int position) {
		if (!BaseActivity.isConnected()) {
			ToastUtil.show(mContext, R.string.no_internet, Duration.s);
			return;
		}
		final ShareItemBean shareItemBean = mLiData.get(position);
		if (((ShareActivity) mContext).URLFLAG == ShareActivity.FLAG_SHARETOME) {
			// 分享给我的
			clickItemOfShareToMe(shareItemBean, position);
		} else {
			// 我的分享
			clickItemOfMyShare(shareItemBean, position);
		}

	}

	private void clickItemOfMyShare(final ShareItemBean shareItemBean, int position) {
		if (shareItemBean.getShareType().equals(IKeyManager.ShareInfo.SHARE_PRIVATE)) {
			// 显示私有分享信息
			showMyPrivateShareInfo(shareItemBean);
		} else if (shareItemBean.getShareType().equals(IKeyManager.ShareInfo.SHARE_PUBLIC)) {
			// 显示公共分享信息
			showMyPublicShareInfo(shareItemBean);
		} else if (shareItemBean.getShareType().equals(IKeyManager.ShareInfo.SHARE_FRIEND)) {
			// 显示平台内用户分享信息
			showMyPlatformShareInfo(shareItemBean);
		}
	}

	private void showMyPlatformShareInfo(ShareItemBean shareItemBean) {
		final ShareUserInfoDialog dialog = new ShareUserInfoDialog(((ShareActivity) mContext).getParent(),
				new MainActivityIPreventPenetrate());

		dialog.setShareId(shareItemBean.getShareId());
		JSONArray jArray = JSON.parseArray(shareItemBean.getShareTo());
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < jArray.size(); i++) {
			temp.add(jArray.getString(i));
		}
		IUrlManager.MultiUserInfoApi api = new IUrlManager.MultiUserInfoApi() {
		};
		String url = MultiUserInfoApi.DOMAIN + MultiUserInfoApi.ADDRESS + username + "/" + MultiUserInfoApi.FUNCTION;
		String querys = "";
		for (String users : temp) {
			querys = querys + MultiUserInfoApi.KEY_USER + "=" + users + "&";
		}

		url = url + "?" + querys + auth;
		url = url.replaceAll("&&", "&");

		MultiUserQuery event = new MultiUserQuery();
		event.apiRequest = url;
		final Intent intent = new Intent(mContext, ShareToUserActivity.class);
		final OnPositiveClickListener modifyListener = new OnPositiveClickListener() {

			@Override
			public void onPositiveClick() {
				intent.putExtra(ShareToUserActivity.INTENT_TYPE, ShareToUserActivity.INTENT_TYPE_MODIFY);
				intent.putExtra(ShareToUserActivity.INTENT_KEY_SHAREID, dialog.getShareId());
				mContext.startActivity(intent);
			}
		};
		event.handler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String ret = new String(arg2);
				Users users = JSON.parseObject(ret, Users.class);
				ArrayList<MultiUserQueryItemBean> userList = (ArrayList<MultiUserQueryItemBean>) JSON
						.parseArray(users.getUsers(), MultiUserQueryItemBean.class);
				final ArrayList<SearchUserItemBean> shareUsers = new ArrayList<SearchUserItemBean>();
				
				if (userList == null) {
					userList = new ArrayList<MultiUserQueryItemBean>();
				}
				
				for (MultiUserQueryItemBean bean : userList) {
					shareUsers
							.add(new SearchUserItemBean(bean.getUsername(), bean.getNickname(), bean.getIcon(), true));
				}
				dialog.setData(userList);

				intent.putParcelableArrayListExtra("_data", shareUsers);
				dialog.setPositiveClickListener(modifyListener);
				dialog.setPositiviveEnable(true);
				dialog.show();

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				intent.putParcelableArrayListExtra("_data", null);
				dialog.setPositiveClickListener(modifyListener);
				dialog.setPositiviveEnable(true);
				dialog.show();
			}
		};
		if (temp.size() > 0)
			EventBus.getDefault().post(event);
		else {
			intent.putParcelableArrayListExtra("_data", null);
			dialog.setPositiveClickListener(modifyListener);
			dialog.setPositiviveEnable(true);
			dialog.setData(new ArrayList<MultiUserQueryItemBean>());
			dialog.show();
		}

	}

	private void showMyPublicShareInfo(final ShareItemBean shareItemBean) {
		ThirdPartySharePopWins wins = TPSPWCreater.create(((ShareActivity) mContext).getParent());
		wins.setShareContent(shareItemBean.getLink());
		wins.setShareSummary(shareItemBean.getName());
		wins.show();
	}

	private void showMyPrivateShareInfo(final ShareItemBean shareItemBean) {
		sharePopUpModifyPwd = new SharePopUpModifyPwd(((ShareActivity) mContext).getParent(),
				new MainActivityIPreventPenetrate());
		sharePopUpModifyPwd.showButton();
		sharePopUpModifyPwd.setModifyPwd(R.string.share_reset_pwd);
		sharePopUpModifyPwd.setPositiveButton(R.string.share_tpshare);
		sharePopUpModifyPwd.setNegativeButton(R.string.string_cancel);

		sharePopUpModifyPwd.setTitle(shareItemBean.getLink());
		sharePopUpModifyPwd.setContent(shareItemBean.getPassword());
		sharePopUpModifyPwd.setModifyClickListener(new ModifyClickListener() {

			@Override
			public void onModifyClick() {
				getActivity().reportCountEvent(IUmengEventKey.CALC_CB_SHARE_PWDMODIFY);
				if (sharePopUpModifyPwd.getContent().equals("")) {
					AlertDialogWithImageCreator pwdNullAlertCreator = new AlertDialogWithImageCreator(
							((ShareActivity) mContext).getParent(), new MainActivityIPreventPenetrate());

					pwdNullAlertCreator.setContentRes(R.string.share_pwd_null);
					pwdNullAlertCreator.setPositiveButton(R.string.string_sure);
					pwdNullAlertCreator.setPositiveClickListener(new OnPositiveClickListener() {
						@Override
						public void onPositiveClick() {
							sharePopUpModifyPwd.setModifyPwd(R.string.share_reset_pwd);
							sharePopUpModifyPwd.show();
						}
					});
					pwdNullAlertCreator.create().show();

				} else {
					shareResetPwdUtil = new ShareResetPwdUtil(mContext);
					shareResetPwdUtil.shareResetPwd(shareItemBean.getShareId(),
							getShareItemEntity(sharePopUpModifyPwd.getContent()));
					shareResetPwdUtil.setCallBack(shareResetPwdCallBack);
				}
			}
		});
		sharePopUpModifyPwd.setPositiveClickListener(new IOn3rdShareClickImpl(shareItemBean));

		sharePopUpModifyPwd.show();
	}

	private void clickItemOfShareToMe(ShareItemBean shareItemBean, int position) {
		if (shareItemBean.getIsDir().equals(IKeyManager.ShareInfo.TYPE_FOLDER)) {
			// access folder, close the submenu
			if (openHolder != null)
				openHolder.setChecked(false);

			LinearLayout linearLayout = ((ShareActivity) mContext).getParentBack();
			linearLayout.setVisibility(View.VISIBLE);
			((ShareActivity) mContext).mShareUtil.performOperate(Operate.click_to_access,
					shareItemBean.getPath() + "/" + shareItemBean.getShareId() + "/" + shareItemBean.getOwner());
			TextView txtCurrent = ((ShareActivity) mContext).getCurrentTitle();
			txtCurrent.setText(shareItemBean.getName());
			if (shareItemBean.getPath().lastIndexOf("/") > 0) {
				String temp = shareItemBean.getPath().substring(0, shareItemBean.getPath().lastIndexOf("/"));
				((ShareActivity) mContext).getParentTitle().setText(temp.substring(temp.lastIndexOf("/") + 1));
			} else {
				((ShareActivity) mContext).getParentTitle().setText(R.string.string_sharetome);
			}
			notifyDataSetChanged();
		} else {
			String type = FileUtil.getMIMETypeByName(shareItemBean.getName());
			if (shareItemBean.getContentType().startsWith(IMAGE)
					&& FileUtil.isSupportedImage(shareItemBean.getName())) {
				Intent i = new Intent();
				i.setClass(mContext, ImagePreviewActivity.class);
				i.putExtra(ImagePreviewActivity.INTENT_FLAG, ImagePreviewActivity.FLAG_SHARE);
				i.putExtra(ImagePreviewActivity.INTENT_DATA, getPreviewImagesInfo(position));
				mContext.startActivity(i);
			} else if (type.startsWith(FileUtil.VIDOETYPE)) {
				startVideoPreview(shareItemBean);
			} else {
				// 第三方预览逻辑

				if (type.equals(FileUtil.UNSUPPORTTYPE)) {
					// 排除一下
					ToastUtil.show(getActivity(), R.string.preview_unsupported_file, Duration.s);
					return;
				}
				// TODO
				PreviewBean bean = new PreviewBean();
				bean.setContentType(shareItemBean.getContentType());
				bean.setModifyTime(shareItemBean.getShareTime());
				bean.setName(shareItemBean.getName());
				bean.setOwner(shareItemBean.getOwner());
				bean.setPath(shareItemBean.getPath());
				bean.setShareId(shareItemBean.getShareId());
				bean.setSize(shareItemBean.getSize());
				bean.setType(2);

				new Thread(new IPreviewFileImpl(this, bean)).start();
				((UMengActivity) mContext).reportCountEvent(CALC_CB_OPEN_DOCUMENT);
			}
		}

	}

	private void startVideoPreview(ShareItemBean shareItemBean) {
		Intent intent = new Intent(mContext, WebVideoActivity.class);
		/*
		 * shareType=1 shareId path username queryName access_token , access_id
		 */

		StringBuilder builder = new StringBuilder();
		builder.append("https://").append(IUrlManager.VideoPreview.HOST).append("/")
				.append(IUrlManager.VideoPreview.PATH).append("?").append("shareId=").append(shareItemBean.getShareId())
				.append("&path=").append(shareItemBean.getPath()).append(auth).append("&shareType=1")
				.append("&username=").append(shareItemBean.getOwner()).append("&queryName=").append(username);

		VideoItem item = new VideoItem();
		item.setUrl(builder.toString());
		item.setName(shareItemBean.getName());

		intent.putExtra(WebVideoActivity.KEY_DATA, JSON.toJSONString(item));

		mContext.startActivity(intent);
	}

	ShareResetPwdCallBack shareResetPwdCallBack = new ShareResetPwdCallBack() {

		@Override
		public void onSuccess() {
			ToastUtil.show(mContext, R.string.share_modify_pwd_success, Duration.s);
		}
	};

	protected StringEntity getShareItemEntity(String pwd) {
		JSONObject jObj = new JSONObject();
		StringEntity ret = null;
		try {
			jObj.put("password", pwd);
			ret = new StringEntity(jObj.toString(), HTTP.UTF_8);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	// ImageDownloaderTest mDownloader;

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.share_list_item, parent, false);
			holder = new ViewHolder();
			holder.mClickSection = (RelativeLayout) convertView.findViewById(R.id.share_list_item_clicksection);
			holder.mType = (ImageView) convertView.findViewById(R.id.share_type);
			holder.mIcon = (ImageView) convertView.findViewById(R.id.share_list_item_icon);
			holder.mTitle = (TextView) convertView.findViewById(R.id.share_list_item_title);
			holder.mCount = (TextView) convertView.findViewById(R.id.share_list_item_count);
			holder.mTime = (TextView) convertView.findViewById(R.id.share_list_item_time);
			holder.mSize = (TextView) convertView.findViewById(R.id.share_list_item_size);
			holder.mToggle = (CheckBox) convertView.findViewById(R.id.share_list_item_toggle);
			holder.mSelect = (CheckBox) convertView.findViewById(R.id.share_list_item_select);
			holder.subSection = (LinearLayout) convertView.findViewById(R.id.share_list_child_sub);

			bindSubView(convertView, holder);
			setSubView(holder, position);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// ============code section :set========================
		setSubView(holder, position);
		holder.mType.bringToFront();
		if (getItem(position).getIsDir().equals(TYPE_FOLDER))
			holder.download.setVisibility(View.GONE);
		else
			holder.download.setVisibility(View.VISIBLE);

		// judgeLogic(holder, mLiData.get(position));

		holder.mSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			}
		});
		holder.mSelect.setChecked(isAllSelected);

		// ***********填充数据*********
		judgeLogic(holder, mLiData.get(position));

		holder.mClickSection.setTag(position);
		holder.mClickSection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				click((Integer) v.getTag());
			}
		});

		holder.mToggle.setTag(position);
		holder.mToggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleClick((Integer) v.getTag());
				openHolder = (CheckBox) v;
				if (((ShareActivity) mContext).URLFLAG == ShareActivity.FLAG_MYSHARE)
					openHolder.setChecked(false);
			}
		});
		// if (mDownloader == null)
		// mDownloader = new ImageDownloaderTest();
		holder.mIcon.setImageResource(R.drawable.unknow108);
		String icon_url = getItem(position).getIcon();

		icon_url = StringUtil.removeAuth(icon_url);
		String auth = getAuth(icon_url);
		holder.mIcon.setTag(icon_url);

		if (StringUtil.isEmpty(auth)) {
			Picasso.with(mContext).load(icon_url).placeholder(R.drawable.unknow108).error(R.drawable.unknow108).fit()
					.diskCache().tag(mContext).into(holder.mIcon);
		} else {
			Picasso.with(mContext).load(icon_url, va).placeholder(R.drawable.unknow108).error(R.drawable.unknow108)
					.diskCache().fit().tag(mContext).into(holder.mIcon);
		}

		return convertView;
	}

	private void bindSubView(View convertView, ViewHolder holder) {
		holder.download = (LinearLayout) convertView.findViewById(R.id.share_list_child_download);
		holder.move = (LinearLayout) convertView.findViewById(R.id.share_list_child_move);
		holder.ignore = (LinearLayout) convertView.findViewById(R.id.share_list_child_ignore);
		holder.report = (LinearLayout) convertView.findViewById(R.id.share_list_child_report);
	}

	private void setSubView(ViewHolder holder, int position) {
		holder.download.setTag(position);
		holder.move.setTag(position);
		holder.ignore.setTag(position);
		holder.report.setTag(position);

		holder.report.setVisibility(View.VISIBLE);

		holder.download.setOnClickListener(shareSubMenuClickListener);
		holder.move.setOnClickListener(shareSubMenuClickListener);
		holder.ignore.setOnClickListener(shareSubMenuClickListener);
		holder.report.setOnClickListener(shareSubMenuClickListener);

		boolean canIgnored = canBeIgnored(mLiData.get(position).getPath());

		if (canIgnored) {
			holder.ignore.setEnabled(true);
			holder.ignore.setAlpha(1f);
		} else {
			holder.ignore.setEnabled(false);
			holder.ignore.setAlpha(0.6f);
		}
	}

	private boolean canBeIgnored(String path) {
		String temp = path.substring(1);
		return !temp.contains("/");
	}

	/**
	 * @Title: judgeLogic
	 * @Description: 两个界面的逻辑判断
	 * @author: zhangbin
	 * @param holder
	 * @param shareItemBean
	 */
	private void judgeLogic(final ViewHolder holder, ShareItemBean shareItemBean) {

		if (((ShareActivity) mContext).URLFLAG == ShareActivity.FLAG_MYSHARE) {
			holder.subSection.setVisibility(View.GONE);
			holder.mToggle.setButtonDrawable(R.drawable.shancmm);
			holder.mType.setVisibility(View.VISIBLE);
			holder.mSize.setText("");
			holder.mTime.setText(shareItemBean.getPath());

			if (shareItemBean.getShareType().equals(SHARE_PUBLIC)) {
				holder.mType.setImageResource(R.drawable.gongkljsj);
			} else if (shareItemBean.getShareType().equals(SHARE_PRIVATE)) {
				holder.mType.setImageResource(R.drawable.simljsj);
			} else if (shareItemBean.getShareType().equals(SHARE_FRIEND)) {
				holder.mType.setImageResource(R.drawable.yonghfxsj);
			}
		} else {
			// 分享给我的
			holder.mType.setVisibility(View.GONE);
			holder.mToggle.setButtonDrawable(R.drawable.cloudbox_list_toggle_check);
//			final String isFolder = "1";
			holder.mTime.setText(TimeUtil.getFormatedTime(shareItemBean.getShareTime()));
			holder.mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						holder.subSection.setVisibility(View.VISIBLE);
						if (null != openHolder && openHolder != holder.mToggle && openHolder.isChecked())
							openHolder.setChecked(false);
					} else {
						holder.subSection.setVisibility(View.GONE);
					}
				}
			});
		}
		if (shareItemBean.getIsDir().equals(TYPE_FOLDER)) {
			holder.mIcon.setImageResource(R.drawable.wenjj);
		} else {
			holder.mIcon.setImageResource(R.drawable.word);
		}
		holder.mTitle.setText(shareItemBean.getName());
	}

	private CheckBox openHolder = null;

	private ShareSubMenuClickListener mShareSubMenuClickListener = null;

	public void setShareSubMenuClickListener(ShareSubMenuClickListener shareSubMenuClickListener) {
		this.mShareSubMenuClickListener = shareSubMenuClickListener;
	}

	private OnClickListener shareSubMenuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ShareItemBean item = getItem((Integer) v.getTag());
			switch (v.getId()) {
			case R.id.share_list_child_download:
				mShareSubMenuClickListener.onDownLoadClick(item);
				break;
			case R.id.share_list_child_move:
				mShareSubMenuClickListener.onMoveClick(item);
				break;
			case R.id.share_list_child_ignore:
				mShareSubMenuClickListener.onIgnoreClick(item, (Integer) v.getTag());
				if (null != openHolder && openHolder.isChecked())
					openHolder.setChecked(false);
				break;
			case R.id.share_list_child_report:
				ReportBean bean = new ReportBean(item);
				bean.setUsername(username);
				ShareReportEvent event = new ShareReportEvent(bean);
				EventBus.getDefault().post(event);
				break;
			default:
				break;
			}
		}
	};

	public boolean DeleteItem(final int position) {
		if (mLiData == null)
			return false;
		if (position >= getCount())
			return false;
		mLiData.remove(position);
		notifyDataSetChanged();
		return true;
	}

	public void Clear() {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		mLiData.clear();
		notifyDataSetChanged();
	}

	public void AddAll(ArrayList<ShareItemBean> datas) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public void ReplaceAll(ArrayList<ShareItemBean> datas) {
		mLiData = new ArrayList<ShareItemBean>();
		for (int i = 0; i < datas.size(); i++) {
			mLiData.add(datas.get(i));
		}
		notifyDataSetChanged();
	}

	public boolean AddItem(ShareItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		mLiData.add(data);
		notifyDataSetChanged();
		return true;
	}

	public boolean AddItem(int position, ShareItemBean data) {
		if (mLiData == null)
			mLiData = new ArrayList<ShareItemBean>();
		if (position > getCount())
			return false;
		else
			mLiData.add(position, data);
		notifyDataSetChanged();
		return true;
	}

	/**
	 * @Title: getPreviewImagesInfo
	 * @Description: 获取预览图片信息
	 * @author: zhangbin
	 * @param position
	 * @return
	 */
	public String getPreviewImagesInfo(int position) {

		ArrayList<String> dirItem = new ArrayList<String>();
		int index = 0;

		for (int i = 0; i < mLiData.size(); i++) {
			if (mLiData.get(i).getContentType() != null) {
				if (mLiData.get(i).getContentType().startsWith(IMAGE)) {
					dirItem.add(JSON.toJSONString(mLiData.get(i)));
					if (i < position)
						index++;
				}
			}
		}
		String[] dirBeans = new String[dirItem.size()];
		for (int j = 0; j < dirItem.size(); j++) {
			dirBeans[j] = dirItem.get(j);
		}
		PreviewImgInfosBean bean = new PreviewImgInfosBean();
		bean.setDirBeans(dirBeans);
		bean.setCposition(index);
		return JSON.toJSONString(bean);
	}

	/**
	 * @Title: getAuth
	 * @Description: 如果是icon，就没有auth...
	 * @author: leobert.lan
	 * @param url
	 * @return
	 */
	private String getAuth(String url) {
		return (url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3)
				|| url.startsWith(IUrlManager.FileIcon.DOMAIN + IUrlManager.FileIcon.FUNCTION_ICON_V3SUB)) ? "" : auth;
	}

	public void toogleMultView() {
		if (null != openHolder)
			openHolder.setChecked(false);
		notifyDataSetChanged();
	}

	private boolean isAllSelected;

	/**
	 * deleteCallBack:删除成功后的回调：删除视图
	 */
	private DeleteCallBack deleteCallBack = new DeleteCallBack() {

		@Override
		public void onSuccess(final int position) {
			DeleteItem(position);
		}
	};

	public UMengActivity getActivity() {
		return (ShareActivity) mContext;

	}

	@Override
	public Activity getCallerActivity() {
		return ((Activity) mContext).getParent();
	}

	@Override
	public void onOpenStart() {
		// 跟合适的做法是使用接口隔离减少耦合
		((MainActivity) ((Activity) mContext).getParent()).showWaitView(true);
	}

	@Override
	public void onOpenFinish() {
		((MainActivity) ((Activity) mContext).getParent()).cancelWaitView();
	}

	private final class IOn3rdShareClickImpl implements OnPositiveClickListener {

		private final ShareItemBean shareItemBean;

		public IOn3rdShareClickImpl(ShareItemBean bean) {
			shareItemBean = bean;
		}

		@Override
		public void onPositiveClick() {
			ThirdPartySharePopWins wins = TPSPWCreater.create(((ShareActivity) mContext).getParent());
			wins.setShareContent(shareItemBean.getLink());
			wins.setShareSummary(shareItemBean.getName());
			wins.show();
		}
	}

}