package com.lht.cloudjob.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.cloudjob.R;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl2;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl3;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.customview
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TypeSheet
 * <p><b>Description</b>: industry type select panel
 * Created by leobert on 2016/8/12.
 */
public class TypeSheet extends FrameLayout {

    private View contentView;

    private LRTree lrTree;

    public TypeSheet(Context context) {
        super(context);
        init();
    }

    public TypeSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypeSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLrTree(LRTree lrTree) {
        this.lrTree = lrTree;
        hideProgressBar();
        initListDatas();
    }

    private void initListDatas() {
        pClassListAdapter.setLiData(generatePriData());
    }

    /**
     * 一级类目适配器
     */
    private ListAdapter<CategoryResBean> pClassListAdapter;

    /**
     * 二级类目适配器
     */
    private ListAdapter<CategoryResBean> subListAdapter;

    /**
     * 一级类目视图提供者
     */
    private SelectableItemViewProviderImpl2 pClassViewProviderImpl;

    /**
     * 二级类目视图提供者
     */
    private SelectableItemViewProviderImpl3 sClassViewProviderImpl;

    private void initContent() {
        pClassViewProviderImpl = new SelectableItemViewProviderImpl2(LayoutInflater.from
                (getContext()),
                new PrimaryListItemCallback());
        pClassListAdapter = new ListAdapter<>(generatePriData(), pClassViewProviderImpl);
        listCPrimary.setAdapter(pClassListAdapter);

        sClassViewProviderImpl = new SelectableItemViewProviderImpl3(LayoutInflater.from
                (getContext()),
                new SubListItemCallback());
        subListAdapter = new ListAdapter<>(generateSubs(null), sClassViewProviderImpl);
        listSub.setAdapter(subListAdapter);
    }

    private ArrayList<CategoryResBean> generatePriData() {
        ArrayList<CategoryResBean> ret = new ArrayList<>();
        CategoryResBean item = new CategoryResBean();
        item.setId(-1);
        item.setName("全部");
        item.setRoot(Integer.MAX_VALUE);
        item.setLvl(0);
        item.setLft(0);
        item.setRgt(1);
        ret.add(item);
        ret.addAll(getPrimaryIndustryClass());
        return ret;
    }

    private View background;

    private LinearLayout llRealContent;

    private OnDismissListener onDismissListener;

    private ListView listCPrimary;

    private ListView listSub;

    private ProgressBar progressBar;

    private IDataProvider iDataProvider;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    void init() {
        contentView = inflate(getContext(), R.layout.view_typesheet, this);
        background = contentView.findViewById(R.id.view_background);
        listCPrimary = (ListView) contentView.findViewById(R.id.typesheet_list_cprimary);
        listSub = (ListView) contentView.findViewById(R.id.typesheet_list_subcontent);


        progressBar = (ProgressBar) contentView.findViewById(R.id.progressbar);
        if (getContext() instanceof IDataProvider) {
            iDataProvider = (IDataProvider) getContext();
        } else {
            throw new IllegalArgumentException("the activity must implements IDataProvider to use" +
                    " typesheet");
        }

        llRealContent = (LinearLayout) contentView.findViewById(R.id.content);
        background.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setVisibility(GONE);

        initContent();
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
        checkDataExist();
        restore();
    }

    private void restore() {
        pClickPositionHolder = pClassViewProviderImpl.getSelectedIndex();
        pClassViewProviderImpl.setCheckedIndex(pClickPositionHolder);
        pClassListAdapter.notifyDataSetChanged();
        sClassViewProviderImpl.setSelectedIndex(sClickPositionHolder);
        subListAdapter.setLiData(generateSubs(pClassListAdapter.getItem(pClickPositionHolder)));
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
        pClassViewProviderImpl.setSelectedIndex(0);
        pClassListAdapter.notifyDataSetChanged();
        subListAdapter.setLiData(generateSubs(pClassListAdapter.getItem(0)));
        sClassViewProviderImpl.setSelectedIndex(-1);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    /**
     * 获取第一类数据
     */
    private ArrayList<CategoryResBean> getPrimaryIndustryClass() {
        if (!checkDataExist())
            return new ArrayList<>();
        return lrTree.newQueryBuilder(0).level(0).query();
    }

    private ArrayList<CategoryResBean> generateSubs(CategoryResBean pClassBean) {
        ArrayList<CategoryResBean> ret = new ArrayList<>();
        if (pClassBean == null)
            return ret;

        if (!pClassBean.getName().equals("全部")) {
            CategoryResBean bean = new CategoryResBean();
            bean.setName("全部");
            bean.setOld_indus_id(pClassBean.getOld_indus_id());
            bean.setId(pClassBean.getId());
            ret.add(bean);
            ret.addAll(getSecondaryIndustryClass(pClassBean));
            return ret;
        } else {
            return getSecondaryIndustryClass(pClassBean);
        }
    }

    /**
     * 获取第二类数据，根据第一类节点
     *
     * @param pClassBean
     */
    private ArrayList<CategoryResBean> getSecondaryIndustryClass(CategoryResBean pClassBean) {
        if (!checkDataExist())
            return new ArrayList<>();
        return lrTree.querySon(pClassBean);
    }

    private boolean checkDataExist() {
        boolean b = (lrTree != null);
        if (!b) {
            showProgressBar();
            iDataProvider.callProvideDataAsync();
        }
        return b;
    }

    private void showProgressBar() {
        progressBar.setVisibility(VISIBLE);
        progressBar.bringToFront();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(GONE);
    }

    public interface IDataProvider {
        void callProvideDataAsync();
    }

    private int pClickPositionHolder = 0;

    private int sClickPositionHolder = -1;


    private class PrimaryListItemCallback implements
            ICustomizeListItem<SelectableItemViewProviderImpl2.ViewHolder> {

        @Override
        public void customize(final int position, View convertView,
                                final SelectableItemViewProviderImpl2.ViewHolder viewHolder) {
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        if (position == pClassViewProviderImpl.getSelectedIndex()) {
                            sClassViewProviderImpl.setSelectedIndex(sClickPositionHolder);
                        } else {
                            sClassViewProviderImpl.setSelectedIndex(-1);
                        }
                        pClickPositionHolder = position;
                        subListAdapter.setLiData(generateSubs(pClassListAdapter.getItem(position)));
                        pClassViewProviderImpl.setCheckedIndex(position);
                        pClassListAdapter.notifyDataSetChanged();
                        selectedItems.setpClassItem(pClassListAdapter.getItem(position));
                    } else {
                        viewHolder.cb.setChecked(true);
                    }
                    if (position == 0) {
                        selectedItems.setsClassItem(null);
                        pClassViewProviderImpl.setSelectedIndex(position);
                        if (onSelectedListener != null) {
                            onSelectedListener.onTypeSelected(selectedItems);
                        }
                        dismiss();
                    }
                }
            });
        }
    }

    private OnSelectedListener onSelectedListener;

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    private class SubListItemCallback implements
            ICustomizeListItem<SelectableItemViewProviderImpl3.ViewHolder> {

        /**
         * desc: 为item设置各种回调，
         *
         * @param position    位置
         * @param convertView 视图
         * @param viewHolder  holder
         */
        @Override
        public void customize(final int position, final View convertView,
                                SelectableItemViewProviderImpl3
                                        .ViewHolder viewHolder) {
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        sClickPositionHolder = position;
                        pClassViewProviderImpl.setSelectedIndex(pClickPositionHolder);
                        pClassListAdapter.notifyDataSetChanged();

                        sClassViewProviderImpl.setSelectedIndex(position);
                        subListAdapter.notifyDataSetChanged();
                        selectedItems.setsClassItem(subListAdapter.getItem(position));
                        if (onSelectedListener != null) {
                            onSelectedListener.onTypeSelected(selectedItems);
                        }
                        YoYo.with(Techniques.Pulse).duration(300).withListener(new AnimEndListener() {

                            @Override
                            public void
                            onAnimationEnd(Animator animation) {
                                dismiss();
                            }
                        }).playOn(convertView);
                    } else {
                        buttonView.setChecked(true);
                        YoYo.with(Techniques.Pulse).duration(300).playOn(convertView);
                    }
                }
            });
        }

    }

    private SelectedItems selectedItems = new SelectedItems();

    public class SelectedItems {
        private CategoryResBean pClassItem;
        private CategoryResBean sClassItem;

        /**
         * 暂不要使用该方法，不允许一级查询
         * @return
         */
        public CategoryResBean getpClassItem() {
            return pClassItem;
        }

        public void setpClassItem(CategoryResBean pClassItem) {
            this.pClassItem = pClassItem;
        }

        public CategoryResBean getsClassItem() {
            return sClassItem;
        }

        public void setsClassItem(CategoryResBean sClassItem) {
            this.sClassItem = sClassItem;
        }

    }


    public interface OnSelectedListener {
        void onTypeSelected(SelectedItems selectedItems);
    }

    private abstract class AnimEndListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
