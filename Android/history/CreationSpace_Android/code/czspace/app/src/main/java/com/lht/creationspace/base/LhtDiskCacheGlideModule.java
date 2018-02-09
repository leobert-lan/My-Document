package com.lht.creationspace.base;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;
import com.lht.creationspace.base.MainApplication;

import java.io.File;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LhtDiskCacheGlideModule </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/4/20.
 */

public class LhtDiskCacheGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                final int MAX_SIZE_100M = 100 * 1000 * 1000;
                File cacheFolder = MainApplication.getOurInstance().getLocalThumbnailCacheDir();

                return DiskLruCacheWrapper.get(cacheFolder, MAX_SIZE_100M);
            }
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
