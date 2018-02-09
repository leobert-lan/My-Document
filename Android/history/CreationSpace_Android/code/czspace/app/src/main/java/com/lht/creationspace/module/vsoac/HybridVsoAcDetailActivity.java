package com.lht.creationspace.module.vsoac;

import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.hybrid.AbsHybridActivityBase;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.share.TPSPWCreator;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme2;
import com.lht.creationspace.hybrid.native4js.expandreqbean.NF_TPShareReqBean;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.web4native.BaseWebResBean;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.global.QueryPageShareData;
import com.lht.creationspace.hybrid.webclient.LhtWebviewClient;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.lhtwebviewlib.base.Interface.CallBackFunction;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HybridVsoAcDetailActivity extends AbsHybridActivityBase {

    private static final String PAGENAME = "HybridVsoAcDetailActivity";
    private PtrBridgeWebView ptrBridgeWebView;
    private ProgressBar progressBar;
    private MaskView maskView;
    private String link;

    private ToolbarTheme2 titleBar;

    @Override
    protected BridgeWebView getBridgeWebView() {
        return ptrBridgeWebView.getRefreshableView();
    }

    @Override
    protected PtrBridgeWebView getPTRBase() {
        return ptrBridgeWebView;
    }

    @Override
    protected MaskView getWebMask() {
        return maskView;
    }

    @Override
    protected ProgressBar getPageProtectPbar() {
        return progressBar;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.activity_hybrid_vso_ac_detail;
    }

    @Override
    protected String getUrl() {
        return link;
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {
        if (title == null)
            return;
        if (title.equals(getUrl()))
            return;
        if (title.equals("about:blank"))
            return;
        titleBar.setTitle(title);
    }

    @Override
    protected void initView() {
        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);
        titleBar = (ToolbarTheme2) findViewById(R.id.titlebar);
    }

    @Override
    protected void initVariable() {
        link = AbsActivityLauncher.parseData(getIntent(), HybridVsoAcDetailActivityData.class).getUrl();
        initDefaultShareData();
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setRightImageResource(R.drawable.v1000_drawable_fenxh);
        titleBar.setiOperateCallback(new ICallback() {
            @Override
            public void onCallback() {
                callShare();
            }
        });

        setSupportActionBar(titleBar);

        lhtWebviewClient.addOnLoadFinishListener(new LhtWebviewClient.OnLoadFinishListener() {
            @Override
            public void onLoadFinish() {
                DLog.d(DLog.Lmsg.class, "onVsoAcLoadFinished");
                WebBridgeCaller.with(getBridgeWebView()).call(pageShareDataRequest);
            }
        });

    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return HybridVsoAcDetailActivity.PAGENAME;
    }

    @Override
    public BaseActivity getActivity() {
        return HybridVsoAcDetailActivity.this;
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<HybridVsoAcDetailActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, HybridVsoAcDetailActivity.class);
        }

        @Override
        public AbsActivityLauncher<HybridVsoAcDetailActivityData> injectData(HybridVsoAcDetailActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * Created by chhyu on 2017/5/9.
     */

    public static class HybridVsoAcDetailActivityData {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private void initDefaultShareData() {
        String url = getBridgeWebView().getUrl();
        url = StringUtil.isEmpty(url) ? getUrl() : url;

        shareData.setOpenUrl(url);
        String title = getBridgeWebView().getTitle();
        shareData.setShareSummary(StringUtil.isEmpty(title) ? "" : title);
        shareData.setShareTitle("【活动】Flag社又有新活动啦，赶快点开看看吧~");

    }


    private void callShare() {
        DLog.d(DLog.Lmsg.class,"[share data]:"+JSON.toJSONString(shareData));

        ThirdPartySharePopWins wins = TPSPWCreator.create(this, shareData);
        wins.show();
    }

    private ThirdPartySharePopWins.UrlShareData shareData = new ThirdPartySharePopWins.UrlShareData();

    private QueryPageShareData pageShareDataRequest = new QueryPageShareData(this) {
        @Override
        public CallBackFunction getOnWebRespNativeCallback() {
            return new CallBackFunction() {
                @Override
                public void onCallBack(String s) {
                    try {
                        BaseWebResBean bean = JSON.parseObject(s, BaseWebResBean.class);
                        String _data = bean.getData();
                        NF_TPShareReqBean data = JSON.parseObject(_data, NF_TPShareReqBean.class);

                        shareData.setOpenUrl(data.getUrl());
                        shareData.setShareSummary(data.getSummary());
                        shareData.setShareTitle(data.getTitle());

                    } catch (JSONException e) {
                        initDefaultShareData();
                        DLog.d(DLog.Lmsg.class, "cannot parse the share data,use default+\r\n"
                                + "error is:" + e.getMessage()
                                + "\r\ncheck default share data:" + JSON.toJSONString(shareData));
                    }
                }
            };
        }
    };
}
