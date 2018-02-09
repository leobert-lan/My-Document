package com.lht.pan_android.util.string;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;

import org.apache.http.protocol.HTTP;

import com.lht.pan_android.R;
import com.lht.pan_android.Interface.IKeyManager;

import android.content.Context;
import android.widget.Toast;

/**
 * @ClassName: StringUtil
 * @Description: TODO
 * @date 2015年11月6日 上午9:33:13
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class StringUtil implements IKeyManager.Token {

	private static String[] FORBIDDEN_SET = { "/", "\\", ":", "*", "?", "\"", "<", ">", "|" };

	private final static String PATH_SEPARATOR = "/";

	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		if (s == "" || "".equals(s))
			return true;
		if (s.length() == 0)
			return true;
		return false;
	}

	public static boolean isCorrectFolderName(String foldername) {
		// TODO 确认命名规则，正则表达式检验，,这里简单验证非法字符
		for (int i = 0; i < FORBIDDEN_SET.length; i++) {
			if (foldername.contains(FORBIDDEN_SET[i]))
				return false;
		}
		return true;
	}

	public static String splitLastSubPath(String path) {
		if (!path.contains(PATH_SEPARATOR))
			throw new IllegalArgumentException("wrong path!do not make jokes,OK?");
		return path.substring(0, path.lastIndexOf(PATH_SEPARATOR));
	}

	public static String removeAuth(String url) {
		String[] frag = url.split("&");
		String ret = "";
		for (String s : frag) {
			if (s.startsWith(KEY_ACCESS_ID) || s.startsWith(KEY_ACCESS_TOKEN))
				continue;
			ret += s + "&";
		}
		return ret.substring(0, ret.length() - 1);
	}

	public static String UrlEncodePath(String string) {
		String[] frag = string.split("/");
		String ret = "";
		for (String s : frag) {
			try {
				s = URLEncoder.encode(s, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			ret += s + "/";
		}
		return ret.substring(0, ret.length() - 1);
	}

	/**
	 * 其实我前面有类似的代码 2.4.0没有判断，但是这样不行，要把交互去掉
	 */
	public static Boolean JudgeFileName(Context context, String string) {
		Matcher matcher = java.util.regex.Pattern.compile("[\\\\/:*?\"<>|]").matcher(string);
		Boolean isLegal = true;
		if (string.startsWith(".")) {
			Toast.makeText(context, "头字符不合法", Toast.LENGTH_SHORT).show();
			isLegal = false;
		} else if (string.equals("$RECYCLE.BIN")) {
			Toast.makeText(context, "命名非法", Toast.LENGTH_SHORT).show();
			isLegal = false;
		} else if (matcher.find()) {
			Toast.makeText(context, "含有非法字符", Toast.LENGTH_SHORT).show();
			isLegal = false;
		} else if (string.length() > 255) {
			Toast.makeText(context, "字符长度太长", Toast.LENGTH_SHORT).show();
			isLegal = false;
		} else if (StringUtil.isEmpty(string)) {
			Toast.makeText(context, R.string.string_folder_null, Toast.LENGTH_SHORT).show();
			isLegal = false;
		}
		return isLegal;
	}

}
