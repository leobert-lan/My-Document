package com.lht.creationspace.listener;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;


import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> LhtPhotoViewDoubleTapListener
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/4.
 */

public class LhtPhotoViewDoubleTapListener implements GestureDetector.OnDoubleTapListener {
    private PhotoViewAttacher photoViewAttacher;
    private final Rect resourceRect;
    private final Rect containerRect;

    public LhtPhotoViewDoubleTapListener(PhotoViewAttacher photoViewAttacher,
                                         Rect resourceRect,
                                         Rect containerRect) {
        if (resourceRect.isEmpty()) {
            throw new IllegalArgumentException("resourceRect is empty");
        }

        if (containerRect.isEmpty()) {
            throw new IllegalArgumentException("containerRect is empty");
        }


        this.photoViewAttacher = photoViewAttacher;
        this.resourceRect = resourceRect;
        this.containerRect = containerRect;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (this.photoViewAttacher == null) {
            return false;
        } else {
            ImageView imageView = this.photoViewAttacher.getImageView();
            if (null != this.photoViewAttacher.getOnPhotoTapListener()) {
                RectF displayRect = this.photoViewAttacher.getDisplayRect();
                if (null != displayRect) {
                    float x = e.getX();
                    float y = e.getY();
                    if (displayRect.contains(x, y)) {
                        float xResult = (x - displayRect.left) / displayRect.width();
                        float yResult = (y - displayRect.top) / displayRect.height();
                        this.photoViewAttacher.getOnPhotoTapListener().onPhotoTap(imageView, xResult, yResult);
                        return true;
                    }

                    this.photoViewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
                }
            }

            if (null != this.photoViewAttacher.getOnViewTapListener()) {
                this.photoViewAttacher.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
            }

            return false;
        }
    }


    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        if (this.photoViewAttacher == null) {
            return false;
        } else {
            try {
                float scale = this.photoViewAttacher.getScale();
                float x = ev.getX();
                float y = ev.getY();
                //最小情况下放大到最大，否则回到最小
                if (scale > this.photoViewAttacher.getMinimumScale()) {
                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
                } else {
                    //放大逻辑
                    RectF minDisplayRect = photoViewAttacher.getDisplayRect();

                    //资源图
//                    float heightScale = (float) containerRect.height() / (float) resourceRect.height();
//                    float widthScale = (float) containerRect.width() / (float) resourceRect.width();

                    //初始显示图
                    float heightScale = (float) containerRect.height() / minDisplayRect.height();
                    float widthScale = (float) containerRect.width() / minDisplayRect.width();

                    float preCropScale = Math.max(heightScale, widthScale);
                    float displayScale = Math.min(preCropScale, photoViewAttacher.getMaximumScale());
//                    Log.d("lmsg", "preCropScale:" + preCropScale + "\r\ndisplay scale:" + displayScale);
                    this.photoViewAttacher.setScale(displayScale, x, y, true);
                }

//                if (scale < this.photoViewAttacher.getMaximumScale()) {
//                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMaximumScale(), x, y, true);
//                } else {
//                    this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
//                }
            } catch (ArrayIndexOutOfBoundsException var5) {
            }

            return true;
        }
    }
}
