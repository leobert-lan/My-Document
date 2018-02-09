package com.lht.creationspace.base.model.apimodel;

import com.lht.creationspace.util.internet.AsyncResponseHandlerComposite;
import com.lht.creationspace.util.internet.HttpAction;
import com.lht.creationspace.util.internet.RestfulApiResponseDebugHandler;
import com.loopj.android.http.RequestParams;

/**
 * <p><b>Package:</b> com.lht.creationspace.mvp.model </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> AbsRestfulApiModel </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/7.
 */

public class AbsRestfulApiModel {

    protected final AsyncResponseHandlerComposite
    newAsyncResponseHandlerComposite(HttpAction action,
                                     String url,
                                     RequestParams params) {
        AsyncResponseHandlerComposite composite =
                new AsyncResponseHandlerComposite(action, url, params);

        RestfulApiResponseDebugHandler debugHandler =
                new RestfulApiResponseDebugHandler(url, params, true);
        debugHandler.setDebugger(action);
        composite.useCustomDebugHandler(debugHandler);
        return composite;
    }
}
