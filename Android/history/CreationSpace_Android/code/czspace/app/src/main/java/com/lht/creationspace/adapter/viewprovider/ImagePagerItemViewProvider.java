package com.lht.creationspace.adapter.viewprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.adapter.LHTPagerAdapter;
import com.lht.creationspace.adapter.interfaces.IPagerItemViewProvider;
import com.lht.creationspace.base.model.pojo.PreviewImage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.senab.photoview.PhotoView;

/**
 * <p><b>Package</b> com.lht.cloudjob.adapter.viewprovider
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> ImagePagerItemViewProvider
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2016/11/3.
 */

public class ImagePagerItemViewProvider implements IPagerItemViewProvider<PreviewImage> {
    public static class RestoreScaleEvent {
    }

    public static RestoreScaleEvent restoreScaleEvent = new RestoreScaleEvent();

    private final LayoutInflater mInflater;

    private final LHTPagerAdapter.ICustomizePagerItem<PreviewImage, ViewHolder> iCustomizePagerItem;

    public ImagePagerItemViewProvider(LayoutInflater inflater,
                                      LHTPagerAdapter.ICustomizePagerItem<PreviewImage, ViewHolder> iCustomizePagerItem) {
        this.mInflater = inflater;
        this.iCustomizePagerItem = iCustomizePagerItem;
    }

    @Override
    public View getView(int position, PreviewImage item, ViewGroup container) {
        View view = mInflater.inflate(
                R.layout.item_pager_imgpreview, null);
        ViewHolder holder = new ViewHolder();
        holder.photoView = (PhotoView) view.findViewById(R.id.previewpager_item_photo);
        holder.progressBar = (ProgressBar) view.findViewById(R.id.previewpager_item_pb);
        holder.tvError = (TextView) view.findViewById(R.id.previewpager_item_tv_error);
        holder.imgError = (ImageView) view.findViewById(R.id.previewpager_item_img_error);

        holder.hideErrorView();

        if (iCustomizePagerItem != null) {
            iCustomizePagerItem.customize(position, item, view, holder);
        }
        container.addView(view);
        return view;
    }

    public class ViewHolder {
        PhotoView photoView;

        ProgressBar progressBar;

        ImageView imgError;

        TextView tvError;

        public ViewHolder() {
            try {
                EventBus.getDefault().register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public PhotoView getPhotoView() {
            return photoView;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public void setRetryActions(View.OnClickListener retryActions) {
            imgError.setOnClickListener(retryActions);
            tvError.setOnClickListener(retryActions);
        }

        @Subscribe
        public void onEventMainThread(RestoreScaleEvent event) {
            if (photoView != null)
                photoView.setScale(1, true);
        }

        public void showErrorView() {
            imgError.setVisibility(View.VISIBLE);
            imgError.bringToFront();
            tvError.setVisibility(View.VISIBLE);
            tvError.bringToFront();
        }

        public void showErrorView(String msg) {
            showErrorView();
            tvError.setText(msg);
        }

        public void hideErrorView() {
            imgError.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }

        @Override
        protected void finalize() throws Throwable {
            try {
                EventBus.getDefault().unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.finalize();
        }
    }

}
