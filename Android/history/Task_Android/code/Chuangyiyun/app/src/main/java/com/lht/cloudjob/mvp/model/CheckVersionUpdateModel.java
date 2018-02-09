package com.lht.cloudjob.mvp.model;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.interfaces.IPublicConst;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.VersionResBean;
import com.lht.cloudjob.util.VersionUtil;
import com.lht.cloudjob.util.internet.AsyncResponseHandlerComposite;
import com.lht.cloudjob.util.internet.HttpAction;
import com.lht.cloudjob.util.internet.HttpUtil;
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
}
