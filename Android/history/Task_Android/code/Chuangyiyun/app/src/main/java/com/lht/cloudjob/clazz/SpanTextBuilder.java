package com.lht.cloudjob.clazz;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SpanTextBuilder
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/10.
 */
public class SpanTextBuilder {

    public static SpannableStringBuilder build(String text, String appendSpace, int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int temp = text.length() + appendSpace.length();
        for (int i = 0; i < count; i++) {
            builder.append(text).append(appendSpace);
            builder.setSpan(new ForegroundColorSpan(Color.RED), temp * i + 3, temp * i + 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return builder;
    }
}
