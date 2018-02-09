package com.lht.cloudjob.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.ThemeManager;
import com.lht.cloudjob.customview.zdepth.ZDepthShadowLayout;
import com.lht.cloudjob.util.DisplayUtils;

import java.lang.ref.WeakReference;


public class TitleBarSearch extends ZDepthShadowLayout
        implements ThemeManager.IThemeListener {

    private Context context;
    private RelativeLayout mTitleBar;
    private ImageView mLeftView;

    private ImageButton btnClose;

    private EditText etSearch;

    public TitleBarSearch(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TitleBarSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        mTitleBar = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_bar_search, null);
        mLeftView = (ImageView) mTitleBar.findViewById(R.id.toolbar_left);
        etSearch = (EditText) mTitleBar.findViewById(R.id.toolbar_et_search);
        btnClose = (ImageButton) mTitleBar.findViewById(R.id.toolbar_btn_close);

        float height = DisplayUtils.convertDpToPx(context, 56f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) height);
        addView(mTitleBar, params);

        mTitleBar.setBackgroundColor(ThemeManager.with(getContext()).getCurrentColor());

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (onSearchCalledListener != null) {
                        onSearchCalledListener.onSearchCalled(v.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

        ThemeManager.with(getContext()).registerListener(this);
        bringToFront();
    }

    public EditText getEtSearch() {
        return etSearch;
    }

    public void setOnBackListener(final ITitleBackListener listener) {
        mLeftView.setVisibility(View.VISIBLE);
        if (listener != null) {
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTitleBackClick();
                }
            });
        }
    }

    public void setDefaultOnBackListener(Activity activity) {
        setOnBackListener(new DefaultOnBackClickListener(activity));
    }

    public void setCloseVisible(boolean isVisible) {
        if (isVisible) {
            btnClose.setVisibility(VISIBLE);
        } else {
            btnClose.setVisibility(GONE);
        }
    }

    public void setOnCloseClickListener(OnClickListener onCloseClickListener) {
        btnClose.setOnClickListener(onCloseClickListener);
    }

    @Override
    public void onThemeChange(int color) {
        mTitleBar.setBackgroundColor(color);
    }

    public interface ITitleBackListener {
        void onTitleBackClick();
    }

    class DefaultOnBackClickListener implements ITitleBackListener {
        private WeakReference<Activity> mActivity;

        DefaultOnBackClickListener(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onTitleBackClick() {
            if (mActivity.get() != null) {
                mActivity.get().finish();
            }
        }
    }

    private OnSearchCalledListener onSearchCalledListener = null;

    public void setOnSearchCalledListener(OnSearchCalledListener onSearchCalledListener) {
        this.onSearchCalledListener = onSearchCalledListener;
    }

    public interface OnSearchCalledListener {
        void onSearchCalled(String text);
    }

    public void setSearchKey(String key) {
        if (etSearch != null)
            etSearch.setText(key);
    }

}
