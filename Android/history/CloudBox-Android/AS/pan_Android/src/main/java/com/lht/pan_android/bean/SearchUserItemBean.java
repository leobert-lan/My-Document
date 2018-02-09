package com.lht.pan_android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName: SearchUserItemBean
 * @Description: TODO
 * @date 2016年1月26日 下午3:32:33
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class SearchUserItemBean implements Parcelable {

	private String username;
	private String nickname;
	private String icon;

	private boolean isSelect;

	public SearchUserItemBean() {
	}

	public SearchUserItemBean(String username, String nickname, String icon, boolean isSelect) {
		super();
		this.username = username;
		this.nickname = nickname;
		this.icon = icon;
		this.isSelect = isSelect;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(icon);
		dest.writeString(nickname);
		dest.writeString(username);

		if (isSelect)
			dest.writeInt(1);
		else
			dest.writeInt(0);
	}

	public static final Parcelable.Creator<SearchUserItemBean> CREATOR = new Creator<SearchUserItemBean>() {
		@Override
		public SearchUserItemBean[] newArray(int size) {
			return new SearchUserItemBean[size];
		}

		@Override
		public SearchUserItemBean createFromParcel(Parcel in) {
			return new SearchUserItemBean(in);
		}
	};

	public SearchUserItemBean(Parcel in) {
		icon = in.readString();
		nickname = in.readString();
		username = in.readString();

		int i = in.readInt();
		if (i == 1)
			isSelect = true;
		else
			isSelect = false;
	}

}
