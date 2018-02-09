package com.lht.pan_android.clazz;

import java.util.HashMap;

import com.lht.pan_android.Interface.IKeyManager;

/**
 * @ClassName: Auth
 * @date 2016年5月17日 上午9:28:21
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Auth implements IKeyManager.Token {
	private final String token;

	private final String id;

	public Auth(String token, String id) {
		this.token = token;
		this.id = id;
	}

	public Auth(String strAuth) {
		if (!isLegel(strAuth))
			throw new IllegalArgumentException("格式非法：" + strAuth);
		else {
			token = obtainToken(strAuth);
			id = obtainId(strAuth);
		}

	}

	private boolean isLegel(String strAuth) {
		if (!strAuth.contains("&"))
			return false;
		if (!strAuth.contains("="))
			return false;
		if (!strAuth.contains(KEY_ACCESS_TOKEN + "="))
			return false;
		if (!strAuth.contains(KEY_ACCESS_ID + "="))
			return false;
		return true;
	}

	private String obtainToken(String s) {
		if (s.startsWith("&"))
			s = s.substring(1);
		if (!s.contains("&"))
			throw new IllegalArgumentException("token+id中间拼接出错");
		String[] temp = s.split("&");
		for (String str : temp) {
			if (str.contains(KEY_ACCESS_TOKEN))
				return str.substring(str.indexOf("=") + 1);
		}
		return "";
	}

	private String obtainId(String s) {
		if (s.startsWith("&"))
			s = s.substring(1);
		if (!s.contains("&"))
			throw new IllegalArgumentException("token+id中间拼接出错");
		String[] temp = s.split("&");
		for (String str : temp) {
			if (str.contains(KEY_ACCESS_ID))
				return str.substring(str.indexOf("=") + 1);
		}
		return "";
	}

	public String getToken() {
		return token;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "{" + KEY_ACCESS_TOKEN + ":" + getToken() + "," + KEY_ACCESS_ID + ":" + getId() + "}";
	}

	public String getStrAuth() {
		return "&" + KEY_ACCESS_TOKEN + "=" + getToken() + "&" + KEY_ACCESS_ID + "=" + getId();
	}

	public HashMap<String, String> getAuthMap() {
		HashMap<String, String> va = new HashMap<String, String>();
		va.put(KEY_ACCESS_ID, id);
		va.put(KEY_ACCESS_TOKEN, token);
		return va;
	}

}
