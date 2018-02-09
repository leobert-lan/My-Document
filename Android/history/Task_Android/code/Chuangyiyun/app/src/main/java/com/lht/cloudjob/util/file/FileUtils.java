package com.lht.cloudjob.util.file;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.toast.ToastUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.file
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> FileUtils
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/5/12.
 */
public class FileUtils {
    public static void copyFile(File origin, File dest) throws IOException {
        File parentfolder = new File(dest.getParent());
        if (!parentfolder.exists()) {
            parentfolder.mkdirs();
        }
        if (!dest.exists()) {
            DLog.d(FileUtils.class, "check dest:" + dest.getAbsolutePath());
            dest.createNewFile();
            dest.setWritable(true);
        }

        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(origin));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(dest));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }

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

    public static String getFileName(String fileUri) {
        File file = new File(fileUri);
        String fileName = file.getName();
        if (!fileName.contains("."))
            return fileName;
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        return name;
    }

    public static String getFileType(String fileUri) {
        File file = new File(fileUri);
        String fileName = file.getName();
        if (!fileName.contains("."))
            return "";
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return fileType.toLowerCase(Locale.ENGLISH);
    }

    public static String queryImageByUri(Uri uri, ContentResolver contentResolver) {
        if (uri == null) {
            return null;
        }

        if (contentResolver == null) {
            return null;
        }

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, filePathColumn,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            return picturePath;
        } else {
            if (uri.getScheme().equals("file")) {
                return uri.getPath();
            } else {
                DLog.d(Lmsg.class, "UnSupportScheme in queryImageByUri,Uri is:" + uri);
                ToastUtils.show(MainApplication.getOurInstance(), R.string
                        .v1010_toast_error_album_private, ToastUtils.Duration.s);
            }

        }
        return null;
    }

}
