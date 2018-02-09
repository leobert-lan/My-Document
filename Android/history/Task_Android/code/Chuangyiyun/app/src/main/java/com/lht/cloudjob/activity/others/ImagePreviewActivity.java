package com.lht.cloudjob.activity.others;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.cloudjob.adapter.LHTPagerAdapter;
import com.lht.cloudjob.adapter.viewprovider.ImagePagerItemViewProvider;
import com.lht.cloudjob.clazz.LhtPhotoViewDoubleTapListener;
import com.lht.cloudjob.customview.CustomDialog;
import com.lht.cloudjob.customview.CustomPopupWindow;
import com.lht.cloudjob.customview.TitleBar2;
import com.lht.cloudjob.interfaces.adapter.IPagerItemViewProvider;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.pojo.ImagePreviewActivityData;
import com.lht.cloudjob.mvp.model.pojo.PreviewImage;
import com.lht.cloudjob.mvp.presenter.ImagePreviewActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IImagePreviewActivity;
import com.lht.customwidgetlib.actionsheet.ActionSheet;
import com.lht.customwidgetlib.actionsheet.OnActionSheetItemClickListener;
import com.lht.customwidgetlib.viewpager.PhotoViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends AsyncProtectedActivity implements IImagePreviewActivity,
        LHTPagerAdapter.ICustomizePagerItem<PreviewImage, ImagePagerItemViewProvider.ViewHolder> {
    private static final String PAGENAME = "ImagePreviewActivity";

    public static final String KEY_DATA = "_data_ImagePreviewActivityData";

    private PhotoViewPager photoViewPager;

    private LHTPagerAdapter<PreviewImage> pagerAdapter;

    private IPagerItemViewProvider<PreviewImage> itemViewProvider;

    private TitleBar2 titleBar2;

    private ImagePreviewActivityPresenter presenter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return ImagePreviewActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return ImagePreviewActivity.this;
    }

    @Override
    protected void initView() {
        photoViewPager = (PhotoViewPager) findViewById(R.id.imgpv_pager);
        titleBar2 = (TitleBar2) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

    }

    @Override
    protected void initVariable() {
        ImagePreviewActivityData data =
                JSON.parseObject(getIntent().getStringExtra(KEY_DATA), ImagePreviewActivityData.class);
        if (data != null) {
            previewImages = data.getPreviewImages();
            if (previewImages == null) {
                previewImages = new ArrayList<>();
            }
            index = data.getCurrentIndex();
        }
        presenter = new ImagePreviewActivityPresenter(this);
        itemViewProvider = new ImagePagerItemViewProvider(getLayoutInflater(), this);
        pagerAdapter = new LHTPagerAdapter<>(previewImages, itemViewProvider);
    }

    private ArrayList<PreviewImage> previewImages;
    private int index = 0;


    @Override
    protected void initEvent() {
        titleBar2.bringToFront();
        titleBar2.setDefaultOnBackListener(getActivity());
        photoViewPager.setAdapter(pagerAdapter);

        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                titleBar2.setTitle(pagerAdapter.getItem(position).getName());
            }

            @Override
            public void onPageSelected(int position) {
                titleBar2.setTitle(pagerAdapter.getItem(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        photoViewPager.setCurrentItem(index, true);
    }

    private void loadImage(PreviewImage data, final ImagePagerItemViewProvider.ViewHolder viewHolder) {
        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
        viewHolder.getProgressBar().bringToFront();
        Picasso.with(getActivity()).load(data.getPreviewUrl()).diskCache(BaseActivity.getPreviewDirCache())
                .into(viewHolder.getPhotoView(), new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.hideErrorView();
                    }

                    @Override
                    public void onError() {
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.showErrorView();
                    }
                });
    }

    @Override
    public void customize(int position, final PreviewImage data,
                          View view, final ImagePagerItemViewProvider.ViewHolder viewHolder) {
        PhotoView photoView = viewHolder.getPhotoView();
        photoView.setScaleLevels(1f, 2f, 3f);

        loadImage(data, viewHolder);

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                presenter.callDowmloadImage(data);
                return true;
            }
        });


        viewHolder.setRetryActions(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.hideErrorView();
                loadImage(data, viewHolder);
            }
        });

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                toggleTitleBar();
            }

            @Override
            public void onOutsidePhotoTap() {
                toggleTitleBar();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTitleBar();
            }
        });

        photoView.setOnDoubleTapListener(new LhtPhotoViewDoubleTapListener(
                (PhotoViewAttacher) photoView.getIPhotoViewImplementation()));
    }

    private void toggleTitleBar() {
        if (titleBar2.getVisibility() == View.GONE) {
            titleBar2.setVisibility(View.VISIBLE);
            titleBar2.bringToFront();
        } else {
            titleBar2.setVisibility(View.GONE);
        }
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public void showDownloadAlertActionsheet(String[] data, OnActionSheetItemClickListener listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.transparent();
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.show();
    }

    @Override
    public void showMobileDownloadAlert(CustomPopupWindow.OnPositiveClickListener listener) {
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.setContent(R.string.v1020_dialog_download_onmobile);
        dialog.setNegativeButton(R.string.v1020_dialog_nbtn_onmobile);
        dialog.setPositiveButton(R.string.v1020_dialog_pbtn_onmobile);
        dialog.setPositiveClickListener(listener);
        dialog.show();
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public Resources getAppResource() {
        return getResources();
    }

    @Override
    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (presenter.cancelDownload()) {
            return;
        }
        super.onBackPressed();
    }
}
