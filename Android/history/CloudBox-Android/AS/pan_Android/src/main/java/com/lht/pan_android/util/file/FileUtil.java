package com.lht.pan_android.util.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.lht.pan_android.Interface.Mime;
import com.lht.pan_android.util.string.StringUtil;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * @ClassName: FileUtil
 * @Description: 文件操作类
 * @date 2015年12月1日 下午2:29:22
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class FileUtil implements Mime {
	public FileUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * InputStream to byte
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public byte[] readInputStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}

		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();

		return data;
	}

	/**
	 * Byte to bitmap
	 * 
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null) {
			if (opts != null) {
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			} else {
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
		}

		return null;
	}

	private static int base = 1024;

	public static String calcSize(long size) {
		// 不涉及什么复杂的算法了，直接根据进制上限算
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		if (size < base) {
			// byte
			return String.valueOf(size) + "B";
		} else if (size < base * base) {
			// kb
			double k = ((double) size / base);
			return String.valueOf(df.format(k)) + "K";
		} else if (size < base * base * base) {
			// mb
			double m = ((double) size / (base * base));
			return String.valueOf(df.format(m)) + "M";
		} else if (size < base * base * base * base) {
			double g = ((double) size / (base * base * base));
			return String.valueOf(df.format(g)) + "G";
		} else {
			double t = ((double) size / (base * base * base * base));
			return String.valueOf(df.format(t) + "T");
		}
	}

	/**
	 * @Title: createLocalFile
	 * @Description: 创建本地文件
	 * @author: leobert.lan
	 * @param path
	 * @param length
	 */
	public static void createLocalFile(String path, long length) {
		File file = null;
		if (path != null) {
			file = new File(path);
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(length);
				accessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void delete(final File file) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				FileUtil.deleteFile(file);
			}
		}).start();
	}

	private static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	/**
	 * @Title: deleteFilesInFolder
	 * @Description: 删除folder中的
	 * @author: leobert.lan
	 * @param path
	 */
	public static void deleteFilesInFolder(String path) {
		final File f = new File(Environment.getExternalStorageDirectory() + path);
		Log.d("lmsg", "call delete,check:" + path + "\r\n:" + f.getPath());
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (f.exists()) {
					if (f.isDirectory()) {
						File[] filesin = f.listFiles();
						deleteFiles(filesin);
					} else {
						Log.d("lmsg", "not dir");
					}
				} else {
					Log.d("lmsg", "nil");
				}
			}
		}).start();
	}

	private static void deleteFiles(File[] files) {
		Log.d("lmsg", "count:" + files.length);
		for (File f : files) {
			if (f.exists())
				deleteFile(f);
			else
				Log.d("lmsg", "nil nil:\r\n" + f.getPath());

		}
	}

	public static final String UNSUPPORTTYPE = "*/*";
	
	public static final String VIDOETYPE = "video";

	@SuppressLint("DefaultLocale")
	public static String getMIMETypeBySuffix(String suffix) {
		String type = UNSUPPORTTYPE;
		String end = suffix.toLowerCase();
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	public static String getMIMETypeByName(String fName) {
		String type = UNSUPPORTTYPE;
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0)
			return type;
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (StringUtil.isEmpty(end))
			return type;
		return getMIMETypeBySuffix(end);
	}

	public static boolean isSupportedImage(String name) {
		if (StringUtil.isEmpty(name))
			return false;
		int dotIndex = name.lastIndexOf(".");
		if (dotIndex < 0)
			return false;
		String end = name.substring(dotIndex, name.length()).toLowerCase();
		for (int i = 0; i < ImageTable.length; i++) {
			if (end.equals(ImageTable[i][0]))
				return true;
		}
		return false;
	}

}
