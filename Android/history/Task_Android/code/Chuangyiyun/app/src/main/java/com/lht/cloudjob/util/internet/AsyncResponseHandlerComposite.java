package com.lht.cloudjob.util.internet;

import com.lht.cloudjob.umeng.customerror.BaseUmengError;
import com.lht.cloudjob.umeng.customerror.WebApiMappingError;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.internet
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AsyncResponseHandlerComposite
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/6/20.
 */
public class AsyncResponseHandlerComposite extends AsyncHttpResponseHandler {

    private ArrayList<AsyncHttpResponseHandler> handlers = new ArrayList<>();

    private BasicApiResponseHandler basicApiResponseHandler;


    public AsyncResponseHandlerComposite(String url, RequestParams params) {
        this(HttpAction.UNSET, url, params);
    }

    public AsyncResponseHandlerComposite(IHttpActionDebugger debugger, String url, RequestParams params) {
        basicApiResponseHandler = new BasicApiResponseHandler(url, params);
        basicApiResponseHandler.setDebugger(debugger);
        addHandler(basicApiResponseHandler);
        setBasicToastable(true);
    }

    public void setBasicToastable(boolean toastable) {
        basicApiResponseHandler.setNeedToast(toastable);
    }

    public boolean addHandler(AsyncHttpResponseHandler handler) {
        return handlers.add(handler);
    }


    public boolean removeHandler(AsyncHttpResponseHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        for (AsyncHttpResponseHandler handler : handlers) {
            if (handler == null)
                continue;
            try {
                handler.onSuccess(i, headers, bytes);
            } catch (Exception e) {
                handler.onFailure(i, headers, bytes, e);

                BaseUmengError error = new WebApiMappingError(e,bytes);
                error.report();

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        for (AsyncHttpResponseHandler handler : handlers) {
            if (handler == null)
                continue;
            handler.onFailure(i, headers, bytes, throwable);
        }
    }
}
