package com.lht.cloudjob.customview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TypeSheet
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/12.
 */
public class FilterSheet extends FrameLayout {
    private View contentView;

    public FilterSheet(Context context) {
        super(context);
        init();
    }

    public FilterSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View background;

    private OnDismissListener onDismissListener;

    private Button btnReset;

    private Button btnComplete;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    private LinearLayout llRealContent;

    private ConflictGridView gridType, gridDelegate, gridAuth;

    private SelectableItemViewProviderImpl fTypeItemViewProvider;
    private SelectableItemViewProviderImpl fDelegateItemViewProvider;
    private SelectableItemViewProviderImpl fAuthItemViewProvider;

    private ListAdapter<String> typeAdapter, delegateAdapter, authAdapter;

    private ArrayList<String> typeDatas, delegateDatas, authDatas;

    private int fTypeIndexHolder = 0;
    private int fDelegateIndexHolder = 0;
    private int fAuthIndexHolder = 0;

    private void init() {
        contentView = inflate(getContext(), R.layout.view_filtersheet, this);
        background = contentView.findViewById(R.id.view_background);
        llRealContent = (LinearLayout) contentView.findViewById(R.id.filter_ll_content);

        gridType = (ConflictGridView) contentView.findViewById(R.id.filter_grid_type);
        gridDelegate = (ConflictGridView) contentView.findViewById(R.id.filter_grid_delegate);
        gridAuth = (ConflictGridView) contentView.findViewById(R.id.filter_grid_auth);

        btnReset = (Button) contentView.findViewById(R.id.searchfilter_btn_reset);
        btnComplete = (Button) contentView.findViewById(R.id.searchfilter_btn_complete);

        llRealContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //empty 防止击穿
            }
        });

        background.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               onResetClicked();
            }
        });

        btnComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteClicked();
            }
        });
        setVisibility(GONE);
        Resources res = getResources();

        typeDatas = ArrayToArrayList(res.getStringArray(R.array.v1010_search_ftype_contents));
        delegateDatas = ArrayToArrayList(res.getStringArray(R.array
                .v1010_search_fdelegate_contents));
        authDatas = ArrayToArrayList(res.getStringArray(R.array.v1010_search_fauth_contents));

        initGrid();
    }

    private void resetSelf() {
        fTypeItemViewProvider.setSelectedIndex(0);
        fDelegateItemViewProvider.setSelectedIndex(0);
        fAuthItemViewProvider.setSelectedIndex(0);
        fTypeIndexHolder = fTypeItemViewProvider.getSelectedIndex();
        fDelegateIndexHolder = fDelegateItemViewProvider.getSelectedIndex();
        fAuthIndexHolder = fAuthItemViewProvider.getSelectedIndex();
        typeAdapter.notifyDataSetChanged();
        delegateAdapter.notifyDataSetChanged();
        authAdapter.notifyDataSetChanged();
    }

    private void onResetClicked() {
        resetSelf();

        //perform a complete-click at last;
        onCompleteClicked();
    }

    private void onCompleteClicked() {
        fTypeIndexHolder = fTypeItemViewProvider.getSelectedIndex();
        fDelegateIndexHolder = fDelegateItemViewProvider.getSelectedIndex();
        fAuthIndexHolder = fAuthItemViewProvider.getSelectedIndex();

        if (onSelectedListener != null) {
            FilterSelectedItems items = new FilterSelectedItems();
            items.setfTypeIndex(fTypeItemViewProvider.getSelectedIndex());
            items.setfDelegateIndex(fDelegateItemViewProvider.getSelectedIndex());
            items.setfAuthIndex(fAuthItemViewProvider.getSelectedIndex());
            onSelectedListener.onFilterSelected(items);
        }
        dismiss();
    }

    private ArrayList<String> ArrayToArrayList(String[] array) {
        ArrayList<String> temp = new ArrayList<>();
        if (array == null || array.length == 0) {
            return temp;
        }
        for (String s : array) {
            if (s == null) {
                continue;
            }
            temp.add(s);
        }
        return temp;
    }

    private void initGrid() {
        fTypeItemViewProvider = new SelectableItemViewProviderImpl(LayoutInflater.from(getContext
                ()),
                new ICustomizeListItem<SelectableItemViewProviderImpl.ViewHolder>() {
                    @Override
                    public void customize(final int position, final View convertView,
                                            SelectableItemViewProviderImpl.ViewHolder viewHolder) {
                        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton
                                .OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean
                                    isChecked) {
                                if (isChecked) {
                                    fTypeItemViewProvider.setSelectedIndex(position);
                                } else {
                                    // fTypeItemViewProvider.setSelectedIndex(0); //不取消
                                    YoYo.with(Techniques.Pulse).duration(300).playOn(convertView);
                                }
                                typeAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        fDelegateItemViewProvider = new SelectableItemViewProviderImpl(LayoutInflater.from
                (getContext()),
                new ICustomizeListItem<SelectableItemViewProviderImpl.ViewHolder>() {
                    @Override
                    public void customize(final int position, final View convertView,
                                            SelectableItemViewProviderImpl.ViewHolder viewHolder) {
                        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton
                                .OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean
                                    isChecked) {
                                if (isChecked) {
                                    fDelegateItemViewProvider.setSelectedIndex(position);
                                } else {
                                    // fDelegateItemViewProvider.setSelectedIndex(0); //不取消
                                    YoYo.with(Techniques.Pulse).duration(300).playOn(convertView);
                                }
                                delegateAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        fAuthItemViewProvider = new SelectableItemViewProviderImpl(LayoutInflater.from(getContext
                ()),
                new ICustomizeListItem<SelectableItemViewProviderImpl.ViewHolder>() {
                    @Override
                    public void customize(final int position, final View convertView,
                                            SelectableItemViewProviderImpl.ViewHolder viewHolder) {
                        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton
                                .OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean
                                    isChecked) {
                                if (isChecked) {
                                    fAuthItemViewProvider.setSelectedIndex(position);
                                } else {
                                    //fAuthItemViewProvider.setSelectedIndex(0); //不取消
                                    YoYo.with(Techniques.Pulse).duration(300).playOn(convertView);
                                }
                                authAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });


        typeAdapter = new ListAdapter<>(typeDatas, fTypeItemViewProvider);
        delegateAdapter = new ListAdapter<>(delegateDatas, fDelegateItemViewProvider);
        authAdapter = new ListAdapter<>(authDatas, fAuthItemViewProvider);

        gridAuth.setAdapter(authAdapter);
        gridType.setAdapter(typeAdapter);
        gridDelegate.setAdapter(delegateAdapter);
    }

    private boolean isShown = false;

    @Override
    public boolean isShown() {
        return isShown;
    }

    public void show() {
        if (isShown)
            return;
        isShown = true;

        setVisibility(VISIBLE);
        bringToFront();
        llRealContent.startAnimation(dropdownAnimation(500));

        restore();
    }

    private void restore() {
        fTypeItemViewProvider.setSelectedIndex(fTypeIndexHolder);
        fDelegateItemViewProvider.setSelectedIndex(fDelegateIndexHolder);
        fAuthItemViewProvider.setSelectedIndex(fAuthIndexHolder);

        authAdapter.notifyDataSetChanged();
        delegateAdapter.notifyDataSetChanged();
        typeAdapter.notifyDataSetChanged();
    }

    public void dismiss() {
        if (isShown) {
            setVisibility(GONE);
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        }
        isShown = false;
    }

    protected Animation dropdownAnimation(long animDuration) {
        Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        anim.setDuration(animDuration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        return anim;
    }

    public void reset() {
        btnReset.performClick();
    }

    public void resetWithoutCallback() {
        resetSelf();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    private OnSelectedListener onSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onFilterSelected(FilterSelectedItems filterSelectedItems);
    }

    public static class FilterSelectedItems {
        private int fTypeIndex;
        private int fDelegateIndex;
        private int fAuthIndex;

        public int getfTypeIndex() {
            return fTypeIndex;
        }

        public void setfTypeIndex(int fTypeIndex) {
            this.fTypeIndex = fTypeIndex;
        }

        public int getfDelegateIndex() {
            return fDelegateIndex;
        }

        public void setfDelegateIndex(int fDelegateIndex) {
            this.fDelegateIndex = fDelegateIndex;
        }

        public int getfAuthIndex() {
            return fAuthIndex;
        }

        public void setfAuthIndex(int fAuthIndex) {
            this.fAuthIndex = fAuthIndex;
        }
    }
}
