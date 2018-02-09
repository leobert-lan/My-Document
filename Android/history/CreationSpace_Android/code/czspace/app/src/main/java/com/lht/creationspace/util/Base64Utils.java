package com.lht.creationspace.util;

import android.util.Base64;

/**
 * @author leobert base64工具类
 */
public class Base64Utils {

	// BASE64 encode
	public static String GetBASE64(String res) {
		byte[] encode = Base64.encode(res.getBytes(), Base64.DEFAULT);
		String enc = new String(encode);
		return enc;
	}

	// BASE64 decode
	public static String GetFromBASE64(String enc) {
		byte[] result = Base64.decode(enc, Base64.DEFAULT);
		String res = new String(result);
		return res;
	}

}
