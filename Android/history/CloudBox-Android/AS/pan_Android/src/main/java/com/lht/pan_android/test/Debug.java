package com.lht.pan_android.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.lht.pan_android.util.TimeUtil;

import android.os.Environment;

/**
 * @ClassName: Debug
 * @Description: TODO
 * @date 2016年1月21日 下午4:30:05
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Debug {

	public static void Log(String s) {
		return;
//		String path = getPath();
//		s = TimeUtil.getCurrentTimeStamp() + ": error:\r\n============\r\n" + s;
//		write(s, path);
	}

	private static String getPath() {
		return Environment.getExternalStorageDirectory().toString() + "/vso_pan_log.txt";
	}

	private static void write(String s, String filepath) {
		try {
			File file = new File(filepath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			@SuppressWarnings("resource")
			FileWriter fw = new FileWriter(filepath, true);
			fw.write(s, 0, s.length());
			fw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
