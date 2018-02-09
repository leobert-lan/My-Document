package com.lht.cloudjob.clazz;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.activity.others.ImagePreviewActivity;
import com.lht.cloudjob.mvp.model.pojo.ImagePreviewActivityData;
import com.lht.cloudjob.mvp.model.pojo.PreviewImage;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImagePreviewIntentFactory
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/8.
 */

public class ImagePreviewIntentFactory {
    public static Intent newImagePreviewIntent(Context context, ArrayList<PreviewImage> images, int index) {
        ImagePreviewActivityData data = new ImagePreviewActivityData();
        data.setCurrentIndex(index);
        data.setPreviewImages(images);
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.KEY_DATA, JSON.toJSONString(data));
        return intent;
    }
}
