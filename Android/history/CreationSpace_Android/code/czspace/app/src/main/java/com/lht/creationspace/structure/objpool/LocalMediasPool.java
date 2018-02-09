package com.lht.creationspace.structure.objpool;

import com.lht.creationspace.structure.objpool.ObjectPool;
import com.lht.creationspace.module.pub.model.pojo.MediaCenterUploadResBean;
import com.yalantis.ucrop.entity.LocalMedia;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz.objpool </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LocalMediasPool </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/11.
 */

public class LocalMediasPool extends
        ObjectPool<LocalMedia, String, MediaCenterUploadResBean> {
    @Override
    protected String generateKey(LocalMedia keyItem) {
        return keyItem.getPath();
    }
}
