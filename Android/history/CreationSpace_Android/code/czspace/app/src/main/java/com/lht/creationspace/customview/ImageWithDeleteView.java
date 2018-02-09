package com.lht.creationspace.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lht.creationspace.R;

/**
 * <p><b>Package</b> com.lht.vsocyy.customview
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> ImageWithDeleteView
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/8.
 */
public class ImageWithDeleteView extends FrameLayout {
    private ImageView content;

    private ImageButton delete;

    public ImageWithDeleteView(Context context) {
        this(context, null);
    }

    public ImageWithDeleteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageWithDeleteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_image_with_delete, this);
        content = (ImageView) findViewById(R.id.image_content);
        delete = (ImageButton) findViewById(R.id.image_delete);

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageDeleteListener != null) {
                    onImageDeleteListener.onDelete(ImageWithDeleteView.this, getTag());
                }
            }
        });
    }


    public ImageView getActualImageView() {
        return content;
    }

    private OnImageDeleteListener onImageDeleteListener;

    public void setOnImageDeleteListener(OnImageDeleteListener listener) {
        onImageDeleteListener = listener;
    }

    public interface OnImageDeleteListener {
        /**
         * @param view
         * @param tag
         */
        void onDelete(ImageWithDeleteView view, Object tag);
    }
}
