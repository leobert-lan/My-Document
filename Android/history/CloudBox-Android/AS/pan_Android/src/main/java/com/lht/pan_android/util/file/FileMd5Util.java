package com.lht.pan_android.util.file;

import java.io.FileInputStream;
import java.security.MessageDigest;

import android.text.TextUtils;

/**
 * @ClassName: FileMd5Util
 * @Description: 计算文件md5
 * @date 2015年11月19日 下午4:20:00
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class FileMd5Util {
	private static int bufferSize = 1024 * 1024;

	public static String getFileMd5String(String fileName) {
		byte[] buf = new byte[bufferSize];
		MessageDigest md;
		boolean fileIsNull = true;

		try {
			FileInputStream fis = new FileInputStream(fileName);
			int len = 0;
			md = MessageDigest.getInstance("MD5");

			len = fis.read(buf);
			if (len > 0) {
				fileIsNull = false;
				while (len > 0) {
					md.update(buf, 0, len);
					len = fis.read(buf);
				}
			}
		} catch (Exception e) {
			return null;
		}

		if (fileIsNull)
			return null;
		else
			return bufferToHex(md.digest());
	}

	public static String getStringMd5(String res) {
		MessageDigest md;
		if (TextUtils.isEmpty(res))
			return null;

		try {
			md = MessageDigest.getInstance("MD5");
			byte[] buf = res.getBytes();
			md.update(buf);
		} catch (Exception e) {
			return null;
		}

		return bufferToHex(md.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
}
