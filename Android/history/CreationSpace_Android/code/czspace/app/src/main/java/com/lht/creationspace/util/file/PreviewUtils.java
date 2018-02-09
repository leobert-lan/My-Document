package com.lht.creationspace.util.file;

import com.lht.creationspace.util.string.StringUtil;

import java.util.HashSet;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.file
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PreviewUtils
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/8.
 */

public class PreviewUtils {

    private static HashSet<String> mimes = new HashSet<>();

    static {
        mimes.add("application/vnd.ms-excel");
        mimes.add("application/pdf");
        mimes.add("application/msword");
        mimes.add("text/plain");
        mimes.add("application/vnd.ms-powerpoint");
//        excel  application/vnd.ms-excel
//        pdf application/pdf
//        word application/msword
//        ppt application/vnd.ms-powerpoint
//        txt text/plain
    }

    public static boolean isSupportByVsoPreviewRules(String mime) {
        if (mimes.contains(mime)) {
            return true;
        }

        if (isImageButGif(mime)) {
            return true;
        }
        return false;
    }

//    public static ArrayList<AttachmentExt> filterImageTypedWithoutGif(ArrayList<AttachmentExt> exts) {
//        if (exts == null || exts.isEmpty()) {
//            return new ArrayList<>();
//        }
//        ArrayList<AttachmentExt> rets = new ArrayList<>();
//
//        for (AttachmentExt ext : exts) {
//            if (isImageButGif(ext.getMime())) {
//                rets.add(ext);
//            }
//        }
//
//        return rets;
//    }
//
//    public static ArrayList<AttachmentExt> filterFileTyped(ArrayList<AttachmentExt> exts) {
//        if (exts == null || exts.isEmpty()) {
//            return new ArrayList<>();
//        }
//        ArrayList<AttachmentExt> rets = new ArrayList<>();
//
//        for (AttachmentExt ext : exts) {
//            if (!isImageButGif(ext.getMime())) {
//                rets.add(ext);
//            }
//        }
//
//        return rets;
//    }

    private static boolean isImageButGif(String mime) {
        String _mime = StringUtil.nullStrToEmpty(mime).toLowerCase();
        final String IMAGE = "image";
        final String GIF = "gif";
        if (!_mime.contains(IMAGE)) {
            return false;
        } else if (_mime.contains(GIF)) {
            return false;
        }
        return true;
    }
}
