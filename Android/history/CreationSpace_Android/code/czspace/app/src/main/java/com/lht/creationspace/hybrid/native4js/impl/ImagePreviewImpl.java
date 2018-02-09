package com.lht.creationspace.hybrid.native4js.impl;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.R;
import com.lht.creationspace.module.pub.ui.ImagePreviewActivity;
import com.lht.creationspace.base.model.pojo.PreviewImage;
import com.lht.creationspace.hybrid.native4js.Native4JsExpandAPI;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_ImgPreviewReqBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import java.util.ArrayList;

/**
 * Created by leobert on 2017/3/1.
 */

public class ImagePreviewImpl extends ABSApiImpl<NF_ImgPreviewReqBean>
        implements Native4JsExpandAPI.ImagePreviewHandler {

    /**
     * 启动预览的reqCode
     */
    private final int reqCode;

    public ImagePreviewImpl(Activity activity, int reqCode) {
        super(activity);
        this.reqCode = reqCode;
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {

        DLog.d(ImagePreviewImpl.class, "onReceive image preview, datas:" + s);

        try {
            NF_ImgPreviewReqBean bean =
                    JSON.parseObject(s, NF_ImgPreviewReqBean.class);
            boolean b = isBeanError(bean);

            if (b) {
                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
                DLog.d(getClass(), "跳转参数异常：" + s);
            } else {
//                Intent intent = createIntent(bean);
//                if (mContext instanceof Activity) {
//                    ((Activity) (mContext)).startActivityForResult(intent, reqCode);
//
//                    ((Activity) (mContext)).overridePendingTransition(R.anim.activity_fade_in, 0);
//                } else {
//                    mContext.startActivity(intent);
//                }
                ImagePreviewActivity.getLauncher(mContext)
                        .startAnim(R.anim.activity_fade_in, 0)
                        .injectData(transData(bean))
                        .launchForResult(reqCode);

                if (callBackFunction != null)
                    callBackFunction.onCallBack(JSON.toJSONString(newSuccessResBean()));
            }

        } catch (Exception e) {
            e.printStackTrace();

            if (callBackFunction != null)
                callBackFunction.onCallBack(JSON.toJSONString(newFailureResBean()));
        }
    }

//    private Intent createIntent(NF_ImgPreviewReqBean bean) {
//        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
//
//        ImagePreviewActivityData data = transData(bean);
//
//        intent.putExtra(ImagePreviewActivity.KEY_DATA, JSON.toJSONString(data));
//
//        return intent;
//    }

    //temp
    private ImagePreviewActivity.ImagePreviewActivityData transData(NF_ImgPreviewReqBean bean) {
        ArrayList<PreviewImage> previewImages = new ArrayList<>();
        ArrayList<NF_ImgPreviewReqBean.ImgEntity> imgs = bean.getImgs();
        for (NF_ImgPreviewReqBean.ImgEntity imgEntity : imgs) {
            PreviewImage image = new PreviewImage();
            image.setResUrl(imgEntity.getUrl_origin());
            image.setPreviewUrl(imgEntity.getUrl_preview());

            previewImages.add(image);
        }
        ImagePreviewActivity.ImagePreviewActivityData data = new ImagePreviewActivity.ImagePreviewActivityData();
        data.setCurrentIndex(bean.getIndex());
        data.setPreviewImages(previewImages);
        return data;
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newSuccessResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<String> newFailureResBean() {
        BaseResponseBean<String> bean = new BaseResponseBean<>();
        bean.setData(null);
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setMsg(BaseResponseBean.MSG_DEFAULT);
        return bean;
    }

    public static BridgeNativeFunction newInstance(Activity activity, int reqCode) {
        return new BridgeNativeFunction(API_NAME, new ImagePreviewImpl(activity, reqCode));
    }

    @Override
    protected boolean isBeanError(NF_ImgPreviewReqBean bean) {
        if (bean == null)
            return BEAN_IS_ERROR;
        if (bean.getImgs() == null)
            return BEAN_IS_ERROR;
        if (bean.getImgs().isEmpty())
            return BEAN_IS_ERROR;
        if (bean.getIndex() >= bean.getImgs().size())
            return BEAN_IS_ERROR;
        for (NF_ImgPreviewReqBean.ImgEntity entity : bean.getImgs()) {
            if (StringUtil.isEmpty(entity.getUrl_preview()))
                return BEAN_IS_ERROR;
        }
        return BEAN_IS_CORRECT;
    }
}
