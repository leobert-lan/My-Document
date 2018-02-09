package com.lht.creationspace.base.activity.hybrid;

import android.widget.ProgressBar;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme1;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.PtrBridgeWebView;

/**
 * <p><b>Package:</b> com.lht.creationspace.activity.hybrid </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> HybridGeneralActivity </p>
 * <p><b>Description:</b> 通用的混合activity UI基类 </p>
 * Created by leobert on 2017/3/8.
 */

public abstract class HybridGeneralActivity extends AbsHybridActivityBase {

    private ProgressBar progressBar;
    private PtrBridgeWebView ptrBridgeWebView;
    private MaskView maskView;
    protected ToolbarTheme1 titleBar;

    protected String url;

    public static final String KEY_DATA = "_key_url";

    @Override
    protected final BridgeWebView getBridgeWebView() {
        return ptrBridgeWebView.getRefreshableView();
    }

    @Override
    protected PtrBridgeWebView getPTRBase() {
        return ptrBridgeWebView;
    }

    @Override
    protected final MaskView getWebMask() {
        return maskView;
    }

    @Override
    protected final ProgressBar getPageProtectPbar() {
        return progressBar;
    }

    @Override
    protected final int getContentLayoutRes() {
        return R.layout.activity_hybrid_;
    }

    @Override
    protected String getUrl() {
        return url;
    }


    @Override
    protected final void initView() {
        titleBar = (ToolbarTheme1) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        maskView = (MaskView) findViewById(R.id.mask);

        ptrBridgeWebView = (PtrBridgeWebView) findViewById(R.id.ptr_web_view);
    }

    @Override
    protected void initVariable() {
        url = getIntent().getStringExtra(KEY_DATA);
    }

}
