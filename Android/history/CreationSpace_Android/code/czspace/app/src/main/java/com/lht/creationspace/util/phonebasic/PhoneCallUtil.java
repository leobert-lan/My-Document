package com.lht.creationspace.util.phonebasic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.phonebasic
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> PhoneCallUtil
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/6
 */
public class PhoneCallUtil {
    public static void makePhoneCall(Context context, String targetPhone) {
        Uri uri = Uri.parse("tel:" + targetPhone);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
