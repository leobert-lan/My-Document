package com.lht.creationspace.module.home.ui.fg;

import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.popup.PopupPublishTypeChooseWin;
import com.lht.creationspace.customview.toolBar.msg.ToolbarTheme4;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * <p><b>Package:</b> com.lht.creationspace.fragment.hybrid </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> FgHomeVsoActivity </p>
 * <p><b>Description:</b> home-活动 </p>
 * Created by leobert on 2017/3/9.
 */

public class FgHomeVsoActivity extends AbsHybridFragmentBase {
    private static final String PAGENAME = "FgHomeVsoActivity";
    private ProgressBar progressBar;
    private PtrBridgeWebView ptrBridgeWebView;
    private ToolbarTheme4 titleBar;
    private MaskView maskView;
    private PopupPublishTypeChooseWin typeChooseWin;

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
        return R.layout.fg_home_vsoactivity;
    }


    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridHomeVsoActivity().getPageUrl();
    }

    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    protected void initView(View contentView) {
        titleBar = (ToolbarTheme4) contentView.findViewById(R.id.titlebar);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        ptrBridgeWebView = (PtrBridgeWebView) contentView.findViewById(R.id.ptr_web_view);
        maskView = (MaskView) contentView.findViewById(R.id.mask);
        typeChooseWin = new PopupPublishTypeChooseWin(parent);
    }

    @Override
    protected void initVariable() {
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle(getString(R.string.v1000_title_fragment_vso_activities));
        titleBar.setBackgroundResource(R.color.cyy_h9);
        titleBar.setRightImageDrawable(R.drawable.v1000_drawable_jiah2);
        titleBar.setOnNavMessageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2MessageActivity();
            }
        });

        titleBar.setOnRightImageClickListener(new ICallback() {
            @Override
            public void onCallback() {
                showPublishCover();
            }
        });

        typeChooseWin.setOnProjectPublish(new PopupPublishTypeChooseWin.OnPublishClickListener() {
            @Override
            public void onProjectPublish() {
                typeChooseWin.dismiss();
                ((HomeActivity) parent).callPublishProject();
            }

            @Override
            public void onArticlePublish() {
                typeChooseWin.dismiss();
                ((HomeActivity) parent).callPublishArticle();
            }
        });
    }

    /**
     * 显示发布popup
     */
    private void showPublishCover() {
        typeChooseWin.show();
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return FgHomeVsoActivity.PAGENAME;
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }
}
