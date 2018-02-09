package com.lht.cloudjob.native4js.impl;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.clazz.Lmsg;
import com.lht.cloudjob.mvp.model.pojo.DownloadEntity;
import com.lht.cloudjob.native4js.Native4JsExpandAPI;
import com.lht.cloudjob.native4js.expandresbean.NF_DownloadReqBean;
import com.lht.cloudjob.native4js.expandresbean.NF_DownloadResBean;
import com.lht.cloudjob.service.DownloadFileService;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.lhtwebviewlib.base.model.BridgeNativeFunction;
import com.lht.lhtwebviewlib.business.bean.BaseResponseBean;
import com.lht.lhtwebviewlib.business.impl.ABSApiImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by chhyu on 2016/12/6.
 * 桥接下载
 */

public class DownloadImpl extends ABSApiImpl implements Native4JsExpandAPI.DownloadHandler {

    private final Context mContext;
    private CallBackFunction mFunction;
    private static String MSG_ERROR;

    public DownloadImpl(Context ctx) {
        this.mContext = ctx;
        EventBus.getDefault().register(this);
    }

    @Override
    public void handler(String s, CallBackFunction callBackFunction) {
        this.mFunction = callBackFunction;
        NF_DownloadReqBean downloadBean = JSON.parseObject(s, NF_DownloadReqBean.class);
        boolean bool = isBeanError(downloadBean);

        if (!bool) {
            //启动service 去下载
            Intent intentService = new Intent(mContext, DownloadFileService.class);
            String data = JSON.toJSONString(downloadBean);
            intentService.putExtra(DownloadFileService.DOWNLOAD_INFO, data);
            mContext.startService(intentService);

        } else {
            BaseResponseBean<NF_DownloadResBean> bean = newFailureResBean(0, MSG_ERROR);
            bean.setData(newDownloadResBean(downloadBean));
            mFunction.onCallBack(JSON.toJSONString(bean));
        }
    }

    private NF_DownloadResBean newDownloadResBean(NF_DownloadReqBean reqBean) {
        NF_DownloadResBean resBean = new NF_DownloadResBean();
        if (reqBean == null) {
            return resBean;
        }
        resBean.setFile_ext(reqBean.getFile_ext());
        resBean.setFile_name(reqBean.getFile_name());
        resBean.setFile_path(null);
        resBean.setFile_size(reqBean.getFile_size());
        resBean.setMime(reqBean.getMime());
        resBean.setUrl_download(reqBean.getUrl_download());
        return resBean;
    }

    @Subscribe
    public void onEventMainThread(VsoBridgeDownloadEvent event) {
        int status = event.getStatus();
        switch (status) {
            case VsoBridgeDownloadEvent.STATUS_SUCCESS:
                onDownloadSuccess(event);
                break;
            case VsoBridgeDownloadEvent.STATUS_ERROR:
                onDownloadError(event);
                break;
            case VsoBridgeDownloadEvent.STATUS_CANCEL:
                onDownloadError(event);
                break;
            default:
                break;
        }
    }

    private void onDownloadSuccess(VsoBridgeDownloadEvent event) {
        NF_DownloadResBean bean = new NF_DownloadResBean();
        DownloadEntity entity = event.getDownloadEntity();
        bean.setFile_name(entity.getFileName());
        bean.setFile_size(entity.getFileSize());
        bean.setUrl_download(entity.getFileUrl());
        bean.setFile_path(event.getFile().getAbsolutePath());
        BaseResponseBean<NF_DownloadResBean> nf_downloadResBeanBaseResponseBean = newSuccessResBean(bean);
        mFunction.onCallBack(JSON.toJSONString(nf_downloadResBeanBaseResponseBean));
    }

    private void onDownloadError(VsoBridgeDownloadEvent event) {
        String msg = event.getMsg();

        NF_DownloadResBean bean = new NF_DownloadResBean();
        DownloadEntity entity = event.getDownloadEntity();
        bean.setFile_name(entity.getFileName());
        bean.setFile_size(entity.getFileSize());
        bean.setUrl_download(entity.getFileUrl());

        BaseResponseBean<NF_DownloadResBean> resBean = newFailureResBean(0, msg);
        resBean.setData(bean);

        mFunction.onCallBack(JSON.toJSONString(resBean));
    }

    /*for test*/
//    public
    private BaseResponseBean<NF_DownloadResBean> newSuccessResBean(NF_DownloadResBean data) {
        BaseResponseBean<NF_DownloadResBean> bean = new BaseResponseBean<>();
        bean.setData(data);
        bean.setStatus(BaseResponseBean.STATUS_SUCCESS);
        bean.setMsg("");
        return bean;
    }

    /*for test*/
//    public
    private BaseResponseBean<NF_DownloadResBean> newFailureResBean(int ret, String msg) {
        BaseResponseBean<NF_DownloadResBean> bean = new BaseResponseBean<>();
        bean.setStatus(BaseResponseBean.STATUS_FAILURE);
        bean.setRet(ret);
        bean.setMsg(msg);
        return bean;
    }

    @Override
    protected boolean isBeanError(Object o) {
        if (o instanceof NF_DownloadReqBean) {
            NF_DownloadReqBean bean = (NF_DownloadReqBean) o;
            if (TextUtils.isEmpty(bean.getFile_name())) {
                MSG_ERROR = "您要下载的文件名为空";
                return BEAN_IS_ERROR;
            }
            if (TextUtils.isEmpty(bean.getUrl_download())) {
                MSG_ERROR = "您要下载的文件地址为空";
                return BEAN_IS_ERROR;
            }

            return BEAN_IS_CORRECT;
        } else {
            DLog.wtf(Lmsg.class, "check you code,bean not match because your error");
            return BEAN_IS_ERROR;
        }
    }
    public static BridgeNativeFunction newInstance(Context context) {
        return new BridgeNativeFunction(API_NAME, new DownloadImpl(context));
    }

    @Override
    protected void finalize() throws Throwable {
        EventBus.getDefault().unregister(this);
        super.finalize();
    }

    public static final class VsoBridgeDownloadCancelEvent {
        private final String tag;

        public VsoBridgeDownloadCancelEvent(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    public static final class VsoBridgeDownloadEvent {

        public static final int STATUS_DEFAULT = 0;

        public static final int STATUS_ONSTART = 1;

        public static final int STATUS_DOWNLOADING = 2;

        public static final int STATUS_SUCCESS = 3;

        public static final int STATUS_CANCEL = 4;

        public static final int STATUS_ERROR = 5;


        private int status;
        private String uniqueTag;
        private DownloadEntity downloadEntity;
        private long currentSize;
        private long totalSize;
        private String msg;
        private File file;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUniqueTag() {
            return uniqueTag;
        }

        public void setUniqueTag(String uniqueTag) {
            this.uniqueTag = uniqueTag;
        }

        public DownloadEntity getDownloadEntity() {
            return downloadEntity;
        }

        public void setDownloadEntity(DownloadEntity downloadEntity) {
            this.downloadEntity = downloadEntity;
        }

        public long getCurrentSize() {
            return currentSize;
        }

        public void setCurrentSize(long currentSize) {
            this.currentSize = currentSize;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
