package com.lht.creationspace.module.home.ui.fg;

import android.view.View;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.Event.AppEvent;
import com.lht.creationspace.R;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.domain.interactors.AbsDbInteractor;
import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentActivity;
import com.lht.creationspace.base.fragment.hybrid.AbsHybridFragmentBase;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.customview.MaskView;
import com.lht.creationspace.customview.popup.PWForProjectTypeChoose;
import com.lht.creationspace.customview.popup.PopupPublishTypeChooseWin;
import com.lht.creationspace.customview.toolBar.msg.ToolbarTheme6;
import com.lht.creationspace.hybrid.IHybridPagesCollection;
import com.lht.creationspace.hybrid.native4js.impl.DownloadImpl;
import com.lht.creationspace.hybrid.web4native.WebBridgeCaller;
import com.lht.creationspace.hybrid.web4native.project.ProjectSearchFilter;
import com.lht.creationspace.module.home.ui.ac.HomeActivity;
import com.lht.creationspace.module.proj.interactors.ProjTypeInteractorFactory;
import com.lht.creationspace.module.proj.model.ProjTypeDbModel;
import com.lht.creationspace.module.proj.model.ProjectTypeModel;
import com.lht.creationspace.module.proj.model.pojo.ProjectTypeResBean;
import com.lht.creationspace.module.proj.repository.ProjTypeRepository;
import com.lht.creationspace.module.proj.repository.impl.ProjTypeRepositoryImpl;
import com.lht.lhtwebviewlib.BridgeWebView;
import com.lht.ptrlib.library.OnPtrWebRefreshListener;
import com.lht.ptrlib.library.PtrBridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by chhyu on 2017/3/2.
 */

public class FgHomeProject extends AbsHybridFragmentBase implements OnPtrWebRefreshListener.IUrlGetter {


    private static final String PAGENAME = "FgHomeProject";
    private ProgressBar progressBar;
    private MaskView maskView;
    private PtrBridgeWebView ptrBridgeWebView;
    private ToolbarTheme6 titleBar;
    private AbsHybridFragmentActivity parent;
    private PWForProjectTypeChoose pwForProjectTypeChoose;
    private PopupPublishTypeChooseWin typeChooseWin;

    public int projectPrimaryTypeId = -1;


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
        return R.layout.fg_home_project;
    }

    @Override
    protected String getUrl() {
        return new IHybridPagesCollection.HybridHomeProject().getPageUrlWithQueryString(projectPrimaryTypeId);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onWebViewReceivedTitle(WebView view, String title) {

    }

    @Override
    protected void initView(View contentView) {
        parent = (AbsHybridFragmentActivity) getActivity();
        titleBar = (ToolbarTheme6) contentView.findViewById(R.id.titlebar);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        maskView = (MaskView) contentView.findViewById(R.id.mask);
        ptrBridgeWebView = (PtrBridgeWebView) contentView.findViewById(R.id.ptr_web_view);

        pwForProjectTypeChoose = new PWForProjectTypeChoose(parent);
    }

    @Override
    protected void initVariable() {
    }

    @Override
    protected void initEvent() {
        EventBus.getDefault().register(this);
        titleBar.setTitle(getString(R.string.v1000_title_activity_all_project));
        titleBar.setRightImageDrawable(R.drawable.v1000_drawable_jiah2);
        titleBar.setRightOpClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPublishCover();
            }
        });
        pwForProjectTypeChoose.addOnDissmissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                titleBar.refreshUiOnManualHideFilter();
            }
        });
        typeChooseWin = new PopupPublishTypeChooseWin(parent);
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

        pwForProjectTypeChoose.setOnListItemClickListener(onListItemClickListener);

        titleBar.setiFilterDisplay(new ToolbarTheme6.IFilterDisplay() {

            @Override
            public void showFilter() {
                pwForProjectTypeChoose.showAsDropDown(titleBar);
//                pwForProjectTypeChoose.showBelow(titleBar);
                if (isTypeDateReady)
                    pwForProjectTypeChoose.updateData(typeDatas);
                else
                    getAllProjectTypesInDb();
            }

            @Override
            public void hideFilter() {
                titleBar.refreshUiOnManualHideFilter();
                titleBar.bringToFront();
                if (pwForProjectTypeChoose != null)
                    pwForProjectTypeChoose.dismiss();
            }
        });
        titleBar.setOnNavMessageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2MessageActivity();
            }
        });

        getPTRBase().setOnRefreshListener(new OnPtrWebRefreshListener(getPTRBase(), this));
    }

    /**
     * 显示发布popup
     */
    private void showPublishCover() {
        typeChooseWin.show();
    }


    private PWForProjectTypeChoose.OnListItemClickListener
            onListItemClickListener
            = new PWForProjectTypeChoose.OnListItemClickListener() {
        @Override
        public void onListItemClick(int position, ProjectTypeResBean data) {
            projectPrimaryTypeId = data.getId();
            if (lhtWebviewClient != null)
                lhtWebviewClient.setOriginUrl(getUrl());
            titleBar.setTitle(data.getName());
            WebBridgeCaller.with(getBridgeWebView())
                    .call(new ProjectSearchFilter(data.getId()));
            pwForProjectTypeChoose.dismiss();
            titleBar.refreshUiOnManualHideFilter();

        }
    };

    //////////////////////////////////////////////////////////////////////////////
    //下面就别看了
    //////////////////////////////////////////////////////////////////////////////

    private ArrayList<ProjectTypeResBean> typeDatas;
    private boolean isTypeDateReady = false;

    private void getAllProjectTypesInDb() {
        parent.showWaitView(true);
        ProjTypeRepository repository = new ProjTypeRepositoryImpl();
        ProjTypeInteractorFactory interactorFactory
                = ProjTypeInteractorFactory.getInstance(repository);
        AbsDbInteractor<ProjTypeDbModel> interactor =
                interactorFactory.newQueryLastInteractor(new ProjTypeQueryedListener());
        interactor.execute();
    }

    @Override
    public String getPageUrl() {
        return getUrl();
    }

    private class ProjTypeQueryedListener extends
            BaseRepository.OnQueryTaskFinishListener<ProjTypeDbModel> {
        @Override
        public void onNotExist() {
            //未从db取到数据，从 网络取
            getAllProjectTypesOnServer();
        }

        @Override
        public void onExist(ProjTypeDbModel result) {
            String data = result.getData();
            ArrayList<ProjectTypeResBean> beans = (ArrayList<ProjectTypeResBean>) JSON.parseArray(data, ProjectTypeResBean.class);
            if (beans != null && beans.size() > 0) {
                //从DB取到了数据
                parent.cancelWaitView();
                setProjectType(beans);
            } else {
                //未从db取到数据，从 网络取
                getAllProjectTypesOnServer();
            }
        }

        @Override
        public void onCanceledBeforeRun() {
            getAllProjectTypesOnServer();
        }
    }

    private void setProjectType(ArrayList<ProjectTypeResBean> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        ProjectTypeResBean dummyAll = new ProjectTypeResBean();
        dummyAll.setId(-1);
        dummyAll.setName("全部项目");
        datas.add(0, dummyAll);
        typeDatas = datas;
        isTypeDateReady = true;
        pwForProjectTypeChoose.updateData(datas);
    }

    @Subscribe
    @Override
    public void onEventMainThread(AppEvent.LoginSuccessEvent event) {
        invokeOnEventMainThread(event);
    }

    @Override
    protected String getPageName() {
        return FgHomeProject.PAGENAME;
    }

    @Subscribe
    @Override
    public void onEventMainThread(DownloadImpl.VsoBridgeDownloadEvent event) {
        invokeOnEventMainThread(event);
    }


    public void getAllProjectTypesOnServer() {
        ProjectTypeModel model = new ProjectTypeModel(new ProjectTypeCallback());
        model.doRequest(MainApplication.getOurInstance());
    }

    private class ProjectTypeCallback implements RestfulApiModelCallback<ArrayList<ProjectTypeResBean>> {
        @Override
        public void onSuccess(ArrayList<ProjectTypeResBean> datas) {
            parent.cancelWaitView();
            //存DB
            if (datas != null && datas.size() > 0) {
                MainApplication.getOurInstance().save2DB(datas);
            }
            isTypeDateReady = true;
            setProjectType(datas);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            parent.cancelWaitView();
            isTypeDateReady = true;
            setProjectType(new ArrayList<ProjectTypeResBean>());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            parent.cancelWaitView();
            isTypeDateReady = true;
            setProjectType(new ArrayList<ProjectTypeResBean>());
        }
    }
}
