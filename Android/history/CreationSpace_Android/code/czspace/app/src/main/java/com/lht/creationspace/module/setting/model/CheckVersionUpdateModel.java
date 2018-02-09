package com.lht.creationspace.module.setting.model;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.cfg.IPublicConst;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.util.VersionUtil;
import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chhyu on 2016/11/21.
 * check for last version
 */
public class CheckVersionUpdateModel implements IApiRequestModel {

    private final ApiModelCallback<VersionResBean> modelCallback;

    private final HttpUtil mHttpUtil;

    private RequestParams params = new RequestParams();

    private RequestHandle handle;
    private final String url = "http://soft.vsochina.com/admin.php?r=api/software/update";
    private final String KEY_SID = "sid";
    private final String KEY_SYS = "sys";
    private final String KEY_VER = "ver";

    public CheckVersionUpdateModel(String localVersion, ApiModelCallback<VersionResBean> callback) {
        this.modelCallback = callback;
        this.mHttpUtil = HttpUtil.getInstance();
        params.add(KEY_SID, IPublicConst.APP_SID);
        params.add(KEY_SYS, IPublicConst.APP_SYS);
        params.add(KEY_VER, localVersion);
    }

    @Override
    public void doRequest(final Context context) {
        AsyncResponseHandlerComposite composite = new AsyncResponseHandlerComposite(HttpAction.POST, url, params);
        composite.addHandler(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                final int NEED_UPDATE = 200;
                if (bytes == null || bytes.length < 1) {
                    return;
                }
                String res = new String(bytes);
                VersionResBean bean = JSON.parseObject(res, VersionResBean.class);
                Log.e("lmsg", "ret" + bean.getRet());
                if (bean.getRet() == NEED_UPDATE) {
                    String newVersion = bean.getVer();
                    String locationVersion = VersionUtil.getVersion(context);
                    if (isUpdateNeeded(newVersion, locationVersion)) {
                        modelCallback.onSuccess(new BaseBeanContainer<>(bean));
                    }
                } else {
                    modelCallback.onFailure(new BaseBeanContainer<BaseVsoApiResBean>());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                modelCallback.onHttpFailure(i);
            }
        });
        handle = mHttpUtil.postWithParams(context, url, params, composite);
    }

    /**
     * 检查是否需要更新
     *
     * @param remoteVer  网上获取的版本
     * @param currentVer 本地的版本
     * @return true if need update,false otherwise;
     */
    public static boolean isUpdateNeeded(String remoteVer, String currentVer) {
        String[] remote = remoteVer.split("\\.");
        String[] current = currentVer.split("\\.");

        if (remote.length == 0) {
            return false;
        }
        for (int i = 0; i < remote.length; i++) {
            if (current.length <= i) {
                return true;
            }

            try {
                if (Integer.parseInt(current[i]) < Integer.parseInt(remote[i])) {
                    return true;
                }

                if (i == remote.length - 1) {
                    if (remote.length < current.length) {
                        return false;
                    }
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void cancelRequestByContext(Context context) {
        if (handle != null) {
            handle.cancel(true);
        }
    }

    /**
     * Created by chhyu on 2016/11/21.
     */

    public static class VersionResBean {

        private int ret;
        private String ver;

        private VersionInfoData data;

        public VersionInfoData getData() {
            return data;
        }

        public void setData(VersionInfoData data) {
            this.data = data;
        }

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }

        /**
         * 版本信息
         */
        public static class VersionInfoData {
            private String info;
            private String md5;
            private String url;
            private String fileName;

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }

            public String getMd5() {
                return md5;
            }

            public void setMd5(String md5) {
                this.md5 = md5;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getFileName() {
                return fileName;
            }

            public void setFileName(String fileName) {
                this.fileName = fileName;
            }
        }
    }
}
