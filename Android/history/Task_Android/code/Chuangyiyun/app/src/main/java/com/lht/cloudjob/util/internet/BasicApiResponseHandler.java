package com.lht.cloudjob.util.internet;

import android.content.Intent;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.BuildConfig;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.clazz.customerror.ApiError;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.util.SPUtil;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.toast.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.internet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> BasicApiResponseHandler
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/6/20.
 */
public class BasicApiResponseHandler extends AsyncHttpResponseHandler {

    private boolean needToast = true;

    private final String url;

    private final RequestParams params;

    private IHttpActionDebugger debugger;


    public BasicApiResponseHandler(String url, RequestParams params) {
        this(url, params, false);
    }


    public BasicApiResponseHandler(String url, RequestParams params, boolean needToast) {
        this.url = url;
        this.params = params;
        this.needToast = needToast;
    }

    public void setNeedToast(boolean needToast) {
        this.needToast = needToast;
    }

    public boolean isNeedToast() {
        return needToast;
    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        debug(i, headers, bytes);

        //auth校验
        tryToCatchAuthError(bytes);
    }

    private void debug(int i, Header[] headers, byte[] bytes) {
        if (BuildConfig.DEBUG) {
            DLog.i(BasicApiResponseHandler.class, "api request success,info for debug:\r\n"
                    + "action is -------  " + getDebugger().getAction() + "\r\n"
                    + url + "\r\n status:" + i
                    + "\r\ncheck params:\r\n" + getReqParamsString());

            DLog.v(BasicApiResponseHandler.class,
                    "\r\n\r\ncheck headers:\r\n\r\n" + debugHeaders(getRequestHeaders())
                            + "\r\n\r\n check response header:\r\n\r\n" + debugHeaders(headers));
            if (bytes != null && bytes.length > 0) {
                DLog.d(BasicApiResponseHandler.class, "check response:\r\n" + new String(bytes));
            }
        }
    }

    private void tryToCatchAuthError(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return;
        String res = new String(bytes);

        try {
            BaseVsoApiResBean bean = JSON.parseObject(res, BaseVsoApiResBean.class);
            if (!bean.isAuthPermission()) {
                ToastUtils.show(MainApplication.getOurInstance(), bean.getMessage(), ToastUtils
                        .Duration.l);
                SharedPreferences sp = MainApplication.getOurInstance().getTokenSp();
                SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, "");
                SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, "");
                SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, "");

                Intent intent = new Intent();
                intent.setClass(MainApplication.getOurInstance(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                EventBus.getDefault().post(new AppEvent.LogoutEvent());
                MainApplication.getOurInstance().startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiError error = new ApiError(res);
            error.report();
        }

    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        DLog.e(BasicApiResponseHandler.class, "api request failure:\r\n"
                + url + "\r\n status:" + i
                + "\r\ncheck params:\r\n" + getReqParamsString()
                + "\r\ncheck headers:\r\n" + debugHeaders(getRequestHeaders())
                + "\r\n check response header:\r\n" + debugHeaders(headers));
        if (bytes != null && bytes.length > 0) {
            DLog.e(BasicApiResponseHandler.class, "check response:\r\n" + new String(bytes));
        }

        if (isNeedToast()) {
            toastFailureByCode(i);
        }
    }

    private String getReqParamsString() {
        if (params == null)
            return "params is null";
        String _params = params.toString();
        if (HttpAction.GET.equals(getDebugger()) || HttpAction.UNSET.equals(getDebugger())) {
            return _params;
        } else {
            _params = _params.replaceAll("=", " : ").replaceAll("&", "\n");
            return "[\n" + _params + "\n]";
        }
    }

    private IHttpActionDebugger getDebugger() {
        if (debugger == null) {
            debugger = HttpAction.UNSET;
        }
        return debugger;
    }

    void setDebugger(IHttpActionDebugger debugger) {
        this.debugger = debugger;
    }


    /**
     * @param headers
     * @return
     */
    private String debugHeaders(Header[] headers) {
        if (headers != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("[++++++++++\r\n");
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                builder.append(_h);
                builder.append("\n");
            }
            builder.append("++++++++++]\r\n");
            return builder.toString();
        }
        return "null header";
    }

    private void toastFailureByCode(int requestCode) {
        ToastUtils.show(MainApplication.getOurInstance(), getFailure(requestCode), ToastUtils
                .Duration.l);
    }

    private int getFailure(int requestCode) {
        // better modify a set of failure code named by its means instead of int
        int rid = R.string.v1010_toast_net_exception;
        switch (requestCode) {
            case 0:
//			rid = R.string.no_internet;
                break;
            case 401:
//			rid = R.string.token_overtime;
                break;
            case 404:
//			rid = R.string.not_found;
                break;
            default:
                rid = R.string.v1010_toast_net_exception;
                break;
        }
        return rid;

    }

}
