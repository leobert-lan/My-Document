package com.lht.creationspace.module.pub.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.lht.creationspace.R;
import com.lht.creationspace.adapter.LHTPagerAdapter;
import com.lht.creationspace.adapter.viewprovider.ImagePagerItemViewProvider;
import com.lht.creationspace.base.activity.BaseActivity;
import com.lht.creationspace.base.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.creationspace.base.launcher.AbsActivityLauncher;
import com.lht.creationspace.listener.LhtPhotoViewDoubleTapListener;
import com.lht.creationspace.customview.PhotoViewPager;
import com.lht.creationspace.customview.toast.SaveImageStateToast;
import com.lht.creationspace.customview.share.TPSPWCreator;
import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.customview.toolBar.navigation.ToolbarTheme2;
import com.lht.creationspace.listener.ICallback;
import com.lht.creationspace.adapter.interfaces.IPagerItemViewProvider;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.module.pub.ImagePreviewActivityPresenter;
import com.lht.creationspace.base.model.pojo.PreviewImage;
import com.lht.creationspace.util.OpenGlHelper;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import individual.leobert.uilib.actionsheet.ActionSheet;
import individual.leobert.uilib.actionsheet.OnActionSheetItemClickListener;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends AsyncProtectedActivity implements IImagePreviewActivity,
        LHTPagerAdapter.ICustomizePagerItem<PreviewImage, ImagePagerItemViewProvider.ViewHolder> {

    private static final String PAGENAME = "ImagePreviewActivity";

    private PhotoViewPager photoViewPager;

    private LHTPagerAdapter<PreviewImage> pagerAdapter;

    private IPagerItemViewProvider<PreviewImage> itemViewProvider;

    private ToolbarTheme2 titleBar2;

    private ImagePreviewActivityPresenter presenter;

    private ProgressBar progressBar;

    private FrameLayout titleShade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏 显示状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    public BaseActivity getActivity() {
        return ImagePreviewActivity.this;
    }

    @Override
    protected void initView() {
        photoViewPager = (PhotoViewPager) findViewById(R.id.imgpv_pager);
        titleBar2 = (ToolbarTheme2) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        titleShade = (FrameLayout) findViewById(R.id.image_preview_title_shade);

    }

    @Override
    protected boolean needTransparentStatusBar() {
        return true;
    }

    @Override
    protected void initVariable() {
        ImagePreviewActivityData data =
                AbsActivityLauncher.parseData(getIntent(), ImagePreviewActivityData.class);
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
        titleShade.bringToFront();
        titleBar2.bringToFront();
        titleBar2.setDefaultOnBackListener(getActivity());
        titleBar2.hideIvBack();
        titleBar2.setBackground(R.color.transparent);
        titleBar2.setRightImageDrawable(getDrawable(R.drawable.layer_list_icon_more));
        titleBar2.hideTitleBottomDividerLine();
        titleBar2.setTitleTextColor(R.color.text_white);
        photoViewPager.setAdapter(pagerAdapter);

        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                freshIndicator(position);
            }

            @Override
            public void onPageSelected(int position) {
                freshIndicator(position);
                autoCheckOperateDisplay();
                EventBus.getDefault().post(ImagePagerItemViewProvider.restoreScaleEvent);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        photoViewPager.setCurrentItem(index, true);
        freshIndicator(index);

        titleBar2.setiOperateCallback(new ICallback() {
            @Override
            public void onCallback() {
                int index = photoViewPager.getCurrentItem();
                presenter.onMenuOpenCalled(previewImages.get(index));
            }
        });
    }

    private void freshIndicator(int position) {
        if (previewImages == null)
            return;
        if (previewImages.size() > 1) {
            final String _format = "%d/%d";
            titleBar2.setTitle(String.format(Locale.ENGLISH, _format,
                    position + 1, previewImages.size()));
        } else {
            titleBar2.setTitle(null);
        }
    }

    private void loadImage(final int position, PreviewImage data,
                           final ImagePagerItemViewProvider.ViewHolder viewHolder) {
        Glide.with(getActivity()).load(data.getPreviewUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageViewTarget<Bitmap>(viewHolder.getPhotoView()) {

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        viewHolder.getProgressBar().setVisibility(View.VISIBLE);
                        viewHolder.getProgressBar().bringToFront();
                        autoCheckOperateDisplay();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        if (e instanceof OpenGlHelper.TooLargeException) {
                            viewHolder.showErrorView("数据错误");
                            viewHolder.setRetryActions(null);
                        } else {
                            viewHolder.showErrorView();
                        }
                        pagerAdapter.getItem(position).setLoaded(false);
                        autoCheckOperateDisplay();
                    }

                    @Override
                    public void onResourceReady(Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        int maxTextureSize = OpenGlHelper.getOpenGlMaxSize();
                        if (resource.getWidth() > maxTextureSize || resource.getHeight() > maxTextureSize) {
                            onLoadFailed(new OpenGlHelper.TooLargeException(), null);
                            return;
                        }
                        super.onResourceReady(resource, glideAnimation);
                        viewHolder.getProgressBar().setVisibility(View.GONE);
                        viewHolder.hideErrorView();
                        pagerAdapter.getItem(position).setLoaded(true);
                        autoCheckOperateDisplay();

                        DLog.d(DLog.Lmsg.class, "index:" + position + ",checkOnReady: w=" + resource.getWidth() + "   ,h=" + resource.getHeight());
                    }

                    @Override
                    protected void setResource(Bitmap resource) {
                        ImageView target = getView();
                        int maxTextureSize = OpenGlHelper.getOpenGlMaxSize();
//                        Log.e("lmsg","maxsize:"+maxTextureSize);

                        if (resource.getWidth() > maxTextureSize || resource.getHeight() > maxTextureSize) {
                            onLoadFailed(new OpenGlHelper.TooLargeException(), null);
                            return;
                        }
                        if (target instanceof PhotoView) {
                            PhotoView photoView = (PhotoView) target;

                            Rect resourceRect = new Rect(0, 0, resource.getWidth(), resource.getHeight());
                            Rect containerRect = new Rect(0, 0, photoView.getWidth(), photoView.getHeight());

                            if (resourceRect.width() < (containerRect.width() / 2)
                                    && resourceRect.height() < (containerRect.height() / 2)) {
                                photoView.setScaleType(ImageView.ScaleType.CENTER);
                            } else {
                                photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            }

                            LhtPhotoViewDoubleTapListener doubleTapListener =
                                    new LhtPhotoViewDoubleTapListener(
                                            (PhotoViewAttacher) photoView.getIPhotoViewImplementation(),
                                            resourceRect, containerRect);


                            photoView.setOnDoubleTapListener(doubleTapListener);
                        } else {
                            DLog.e(ImagePreviewActivity.class, "target is not photoview,set double click fail");
                        }

                        this.getView().setImageBitmap(resource);
                    }
                });

    }

    private void autoCheckOperateDisplay() {
        int index = photoViewPager.getCurrentItem();
        boolean loaded = pagerAdapter.getItem(index).isLoaded();
        if (loaded)
            titleBar2.enableRightOperate();
        else
            titleBar2.disableRightOperate();
    }

    @Override
    public void customize(final int position, final PreviewImage data,
                          View view, final ImagePagerItemViewProvider.ViewHolder viewHolder) {
        PhotoView photoView = viewHolder.getPhotoView();
        photoView.setScaleLevels(1f, 2f, 3f);

        //set double click on image set

        loadImage(position, data, viewHolder);
        autoCheckOperateDisplay();

        viewHolder.setRetryActions(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.hideErrorView();
                loadImage(position, data, viewHolder);
            }
        });

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                finish();
            }

            @Override
            public void onOutsidePhotoTap() {
                finish();
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }


    @Override
    public void showMenuActionSheet(String[] data, OnActionSheetItemClickListener listener) {
        ActionSheet actionSheet = new ActionSheet(getActivity());
        actionSheet.transparent();
        actionSheet.setDatasForDefaultAdapter(data);
        actionSheet.setOnActionSheetItemClickListener(listener);
        actionSheet.enableBrokenButton();
        actionSheet.show();
    }

    @Override
    public void notifySaveSuccess() {
        SaveImageStateToast toast = new SaveImageStateToast(this);
        toast.setToastImage(R.drawable.v1011_drawable_success_toast);
        toast.setToastContent("保存成功");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void notifySaveFailure() {
        SaveImageStateToast toast = new SaveImageStateToast(this);
        toast.setToastImage(R.drawable.v1011_drawable_failure_toast);
        toast.setToastContent("保存失败");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void showSharePopwins(ThirdPartySharePopWins.ImageShareData imageShareData) {
        ThirdPartySharePopWins wins = TPSPWCreator.create(getActivity(), imageShareData);
        wins.removeItem(5);
        wins.show();
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
        ToastUtils.show(this, msg, ToastUtils.Duration.s);
    }

    public static final String RET_EXT_INDEX = "ret_ext_index";

    @Override
    protected void displayFinishAnim() {
        overridePendingTransition(0, R.anim.activity_fade_out);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RET_EXT_INDEX, photoViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public static Launcher getLauncher(Context context) {
        return new Launcher(context);
    }

    public static class Launcher extends AbsActivityLauncher<ImagePreviewActivityData> {

        public Launcher(Context context) {
            super(context);
        }

        @Override
        protected LhtActivityLauncherIntent newBaseIntent(Context context) {
            return new LhtActivityLauncherIntent(context, ImagePreviewActivity.class);
        }

        @Override
        public AbsActivityLauncher<ImagePreviewActivityData> injectData(ImagePreviewActivityData data) {
            launchIntent.putExtra(data.getClass().getSimpleName(), JSON.toJSONString(data));
            return this;
        }
    }

    /**
     * <p><b>Package</b> com.lht.vsocyy.mvp.model.pojo
     * <p><b>Project</b> VsoCyy
     * <p><b>Classname</b> ImagePreviewActivityData
     * <p><b>Description</b>: 图片预览页面元数据
     * <p>Created by leobert on 2016/11/4.
     */

    public static class ImagePreviewActivityData {
        private ArrayList<PreviewImage> previewImages;

        /**
         * start with zero
         */
        private int currentIndex;

        public ArrayList<PreviewImage> getPreviewImages() {
            return previewImages;
        }

        public void setPreviewImages(ArrayList<PreviewImage> previewImages) {
            this.previewImages = previewImages;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }
    }
}
