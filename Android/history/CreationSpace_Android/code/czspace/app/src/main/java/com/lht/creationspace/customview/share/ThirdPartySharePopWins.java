package com.lht.creationspace.customview.share;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lht.creationspace.R;
import com.lht.creationspace.customview.popup.CustomPopupWindow;
import com.lht.creationspace.customview.popup.IPopupHolder;
import com.lht.creationspace.util.debug.DLog;

import java.util.ArrayList;

/**
 * <p><b>Package</b>
 * <p><b>Project</b>
 * <p><b>Classname</b> ThirdPartySharePopWins
 * <p><b>Description</b>: 分享到第三方平台操作表
 * <p>
 * Created by leobert on 2017/3/8.
 */
public class ThirdPartySharePopWins extends CustomPopupWindow {

    private Adapter mAdapter;

    private View contentView;

    private GridView gridView;

    private ImageButton btnCancel;

    private LinearLayout animContent;


    private OnThirdPartyShareItemClickListener itemClickListener;

    private final ShareData shareData;

    public ThirdPartySharePopWins(IPopupHolder iPopupHolder, ShareData shareData) {
        super(iPopupHolder);
        this.shareData = shareData;
    }

    public ShareData getShareData() {
        return shareData;
    }

    @Override
    protected void init() {
        super.doDefaultSetting();
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        contentView = inflater.inflate(R.layout.popwindow_share_to_plateform, null);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setContentView(contentView);

        animContent = (LinearLayout) contentView.findViewById(R.id.ll_anim_content);
        gridView = (GridView) contentView.findViewById(R.id.share_to_plateform_gridView);
        btnCancel = (ImageButton) contentView.findViewById(R.id.cancel_action);

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onClick(v);
                }
            }
        });
    }

    @Override
    protected int getCustomBackgroundDrawableColor() {
        return 0x000000;
    }

    @Override
    protected int getMyAnim() {
        return R.style.CustomIn_SlideOutBottom;
    }

    @Override
    protected void onShow() {
        super.onShow();
        animContent.startAnimation(slideUpAnimation(300));
    }

    private OnClickListener onCancelListener = null;

    public void setOnCancelListener(OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    void setItems(ArrayList<ThirdPartyShareViewItem> items) {
        mAdapter = new Adapter(items);
        gridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnThirdPartyShareItemClickListener(OnThirdPartyShareItemClickListener l) {
        this.itemClickListener = l;
    }


    private final class Adapter extends BaseAdapter {
        private final ArrayList<ThirdPartyShareViewItem> mItems;

        Adapter(ArrayList<ThirdPartyShareViewItem> items) {
            this.mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.share_to_plateform_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.gridview_text);
                holder.image = (ImageView) convertView.findViewById(R.id.gridview_img);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mItems.get(position).name);
            holder.image.setImageResource(mItems.get(position).drawableRes);

            convertView.setOnClickListener(new ViewClickListenerImpl(position));

            return convertView;
        }

        public void remove(int index) {
            if (index >= getCount()) {
                DLog.e(getClass(), "移除item使用的index越界");
                return;
            }
            mItems.remove(index);
            notifyDataSetChanged();
        }

    }

    private final class ViewClickListenerImpl implements OnClickListener {
        final int mIndex;

        ViewClickListenerImpl(int index) {
            mIndex = index;
        }

        @Override
        public void onClick(View v) {
            if (null != itemClickListener) {
                itemClickListener.onClick(ThirdPartySharePopWins.this, mIndex, v);
            } else {
                DLog.e(getClass(), new DLog.LogLocation(), "先给一个item被点击的回调");
            }
        }
    }

    interface OnThirdPartyShareItemClickListener {
        void onClick(ThirdPartySharePopWins popWins, int itemIndex, View item);
    }

    public void removeItem(int index) {
        if (mAdapter == null) {
            DLog.e(getClass(), "setItems at first");
            return;
        }
        mAdapter.remove(index);
    }

    public int getItemCount() {
        if (mAdapter == null)
            return 0;
        return mAdapter.getCount();
    }


    class ViewHolder {
        ImageView image;

        TextView title;
    }

    public static final class ImageShareData extends ShareData {
        private String localImagePath;

        public String getLocalImagePath() {
            return localImagePath;
        }

        public void setLocalImagePath(String localImagePath) {
            this.localImagePath = localImagePath;
        }
    }

    public static final class UrlShareData extends ShareData {
        private String shareTitle;

        private String shareSummary;

        private String openUrl;

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareSummary() {
            return shareSummary;
        }

        public void setShareSummary(String shareSummary) {
            this.shareSummary = shareSummary;
        }

        public String getOpenUrl() {
            return openUrl;
        }

        public void setOpenUrl(String openUrl) {
            this.openUrl = openUrl;
        }
    }

    public static class ShareData {
    }

}
