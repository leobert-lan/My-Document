package com.lht.cloudjob.activity.asyncprotected;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lht.cloudjob.Event.AppEvent;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl2;
import com.lht.cloudjob.adapter.viewprovider.SelectableItemViewProviderImpl3;
import com.lht.cloudjob.clazz.LRTree;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.adapter.ICustomizeListItem;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.presenter.ChooseCategoryActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.IChooseCategoryActivity;
import com.nineoldandroids.animation.Animator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ChooseCategoryActivity extends AsyncProtectedActivity implements IChooseCategoryActivity {

    public static final String PAGENAME = "ChooseCategoryActivity";
    private TitleBar titleBar;
    private ProgressBar progressBar;
    private ChooseCategoryActivityPresenter presenter;
    private ListView listCprimary;
    private ListView listSubcontent;
    private ListAdapter<CategoryResBean> pClassListAdapter;
    private SelectableItemViewProviderImpl2 pClassViewProviderImpl;
    private SelectableItemViewProviderImpl3 sClassViewProviderImpl;
    private ListAdapter<CategoryResBean> subListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        listCprimary = (ListView) findViewById(R.id.demand_publish_list_cprimary);
        listSubcontent = (ListView) findViewById(R.id.demand_publish_list_subcontent);

        sClassViewProviderImpl = new SelectableItemViewProviderImpl3(getLayoutInflater(), new SubListItemCallback());
        subListAdapter = new ListAdapter<>(generateSubs(null), sClassViewProviderImpl);

    }

    @Override
    protected void initVariable() {
        presenter = new ChooseCategoryActivityPresenter(this);

        pClassViewProviderImpl = new SelectableItemViewProviderImpl2(getLayoutInflater(), new PrimaryListItemCallback());
        pClassListAdapter = new ListAdapter<>(new ArrayList<CategoryResBean>(), pClassViewProviderImpl);
    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(getActivity());
        titleBar.setTitle(R.string.v1042_choosecategory_title_text_choose_category);

        listCprimary.setAdapter(pClassListAdapter);
        listSubcontent.setAdapter(subListAdapter);

        presenter.callGetCategoryData();
    }

    @Override
    protected String getPageName() {
        return ChooseCategoryActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return ChooseCategoryActivity.this;
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return presenter;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public void updateCategoryData(LRTree lrTree) {
        //更新UI
        this.lrTree = lrTree;
        initListDatas();
    }

    /*============================================================================================================*/

    private int pClickPositionHolder = 0;

    private int sClickPositionHolder = -1;

    private class PrimaryListItemCallback implements ICustomizeListItem<SelectableItemViewProviderImpl2.ViewHolder> {

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
//                    if (position == 0) {
//                        selectedItems.setsClassItem(null);
//                        pClassViewProviderImpl.setSelectedIndex(position);
//                    }
                }
            });
        }
    }

    private LRTree lrTree;

    private ArrayList<CategoryResBean> getPrimaryIndustryClass() {
        if (!checkDataExist())
            return new ArrayList<>();
        return lrTree.newQueryBuilder(0).level(0).query();
    }

    private boolean checkDataExist() {
        boolean b = (lrTree == null);
        if (b) {
            presenter.callGetCategoryData();
        }
        return !b;
    }


    private void initListDatas() {
        pClassViewProviderImpl.setCheckedIndex(0);
        pClassViewProviderImpl.setSelectedIndex(0);
        pClassListAdapter.setLiData(getPrimaryIndustryClass());
    }

    private SelectedItems selectedItems = new SelectedItems();

    public static class SelectedItems {
        private CategoryResBean pClassItem;
        private CategoryResBean sClassItem;

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

    class SubListItemCallback implements ICustomizeListItem<SelectableItemViewProviderImpl3.ViewHolder> {

        @Override
        public void customize(final int position, final View convertView, SelectableItemViewProviderImpl3.ViewHolder viewHolder) {
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
                        YoYo.with(Techniques.Pulse).duration(300).withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                AppEvent.ChooseCategoryEvent event =
                                        new AppEvent.ChooseCategoryEvent(subListAdapter.getItem(position));
                                EventBus.getDefault().post(event);
                                finish();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

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

    private ArrayList<CategoryResBean> generateSubs(CategoryResBean pClassBean) {
        ArrayList<CategoryResBean> ret = new ArrayList<>();
        if (pClassBean == null) {
            return ret;
        }
        return getSecondaryIndustryClass(pClassBean);
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

}
