package com.lht.creationspace.util.internet;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.lht.creationspace.BuildConfig;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.R;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.util.SPUtil;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.toast.ToastUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.vsocyy.util.internet
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> RestfulApiResponseDebugHandler
 * <p><b>Description</b>: restful风格的接口的debug处理器
 * Created by leobert on 2016/6/20.
 */
public class RestfulApiResponseDebugHandler extends AsyncHttpResponseHandler {

    private boolean needToast = true;

    private final String url;

    private final RequestParams params;

    private IHttpActionDebugger debugger;


    public RestfulApiResponseDebugHandler(String url, RequestParams params) {
        this(url, params, false);
    }


    public RestfulApiResponseDebugHandler(String url, RequestParams params, boolean needToast) {
        this.url = url;
        this.params = params;
        this.needToast = needToast;
        updateCreateTime();
    }

    private long createTimeStamp;

    private void updateCreateTime() {
        createTimeStamp = System.currentTimeMillis();
    }

    private boolean isTimeOut() {
        boolean b = (System.currentTimeMillis() - createTimeStamp) >= (long) HttpUtil.TIME_OUT;
        return b;
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
    }

    private void debug(int i, Header[] headers, byte[] bytes) {
        if (BuildConfig.DEBUG) {
            DLog.i(RestfulApiResponseDebugHandler.class, "api request success,info for debug:\r\n"
                    + "action is -------  " + getDebugger().getAction() + "\r\n"
                    + url + "\r\n status:" + i
                    + "\r\ncheck params:\r\n" + getReqParamsString());

            DLog.v(RestfulApiResponseDebugHandler.class,
                    "\r\n\r\ncheck headers:\r\n\r\n" + debugHeaders(getRequestHeaders())
                            + "\r\n\r\n check response header:\r\n\r\n" + debugHeaders(headers));
            if (bytes != null && bytes.length > 0) {
                DLog.d(RestfulApiResponseDebugHandler.class, "check response:\r\n" + new String(bytes));
            }
        }
    }

    private void handleUnAuth() {
        SharedPreferences sp = MainApplication.getOurInstance().getTokenSp();
        SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_ID, "");
        SPUtil.modifyString(sp, SPConstants.Token.KEY_ACCESS_TOKEN, "");
        SPUtil.modifyString(sp, SPConstants.Token.KEY_USERNAME, "");

        Activity current = MainApplication.getOurInstance().getCurrentTopActivity();
        if (current == null) {
            Intent intent = new Intent();
            intent.setClass(MainApplication.getOurInstance(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            EventBus.getDefault().post(new AppEvent.LogoutEvent());
            MainApplication.getOurInstance().startActivity(intent);
        } else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(current, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setCancelable(false);
            builder.setMessage("登录已过期请重新登录");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setClass(MainApplication.getOurInstance(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    EventBus.getDefault().post(new AppEvent.LogoutEvent());
                    MainApplication.getOurInstance().startActivity(intent);
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        DLog.e(RestfulApiResponseDebugHandler.class, "api request failure:\r\n"
                + url + "\r\n status:" + i
                + "\r\ncheck params:\r\n" + getReqParamsString()
                + "\r\ncheck headers:\r\n" + debugHeaders(getRequestHeaders())
                + "\r\n check response header:\r\n" + debugHeaders(headers));
        if (bytes != null && bytes.length > 0) {
            DLog.e(RestfulApiResponseDebugHandler.class, "check response:\r\n" + new String(bytes));
        }

        if (i == 401)
            handleUnAuth();
        else if (isNeedToast()) { //401默认不处理toast
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

    public void setDebugger(IHttpActionDebugger debugger) {
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
                .Duration.s);
    }

    private int getFailure(int requestCode) {
        // better modify a set of failure code named by its means instead of int
        int rid = R.string.v1000_toast_net_exception;
        switch (requestCode) {
            case 0:
                if (!isTimeOut())
                    rid = R.string.no_internet;
                break;
            case 401:
                rid = R.string.v1000_toast_net_401;
                break;
            case 404:
//			rid = R.string.not_found;
                break;
            default:
                rid = R.string.v1000_toast_server_error;
                break;
        }
        return rid;

    }

}
