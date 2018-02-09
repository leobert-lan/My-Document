package com.lht.creationspace.hybrid.web4native.global;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.customview.share.TPSPWCreator;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_TPShareReqBean;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.hybrid.web4native.AbsSimpleWebRequest;
import com.lht.creationspace.hybrid.web4native.BaseWebResBean;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;

import java.lang.ref.WeakReference;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native.global </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> QueryPageShareData </p>
 * <p><b>Description:</b> 查询页面分享的数据 </p>
 * Created by leobert on 2017/3/13.
 */

public class QueryPageShareData extends AbsSimpleWebRequest
        implements IJSFuncCollection.IGetPageShareData {

    private final WeakReference<IPopupHolder> iPopupHolderRef;

    public QueryPageShareData(IPopupHolder iPopupHolder) {
        iPopupHolderRef = new WeakReference<>(iPopupHolder);
    }

    @Override
    public String getReqWebHandlerName() {
        return API_NAME;
    }

    @Override
    public CallBackFunction getOnWebRespNativeCallback() {
        return new CallBackFunction() {
            @Override
            public void onCallBack(String s) {
                try {
                    BaseWebResBean bean = JSON.parseObject(s, BaseWebResBean.class);
                    String _data = bean.getData();
                    NF_TPShareReqBean data = JSON.parseObject(_data, NF_TPShareReqBean.class);

                    showSharePopupWins(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    protected void showSharePopupWins(NF_TPShareReqBean data) {
        IPopupHolder iPopupHolder = iPopupHolderRef.get();
        if (iPopupHolder == null) {
            DLog.d(getClass(), "popupholder weak ref has bean gc");
            return;
        }
        ThirdPartySharePopWins.UrlShareData shareData =
                new ThirdPartySharePopWins.UrlShareData();
        shareData.setOpenUrl(data.getUrl());
        shareData.setShareSummary(data.getSummary());
        shareData.setShareTitle(data.getTitle());

        ThirdPartySharePopWins wins = TPSPWCreator.create(iPopupHolder, shareData);
        wins.show();
    }
}
