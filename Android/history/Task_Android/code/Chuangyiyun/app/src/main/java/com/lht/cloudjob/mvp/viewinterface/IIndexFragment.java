package com.lht.cloudjob.mvp.viewinterface;

import android.app.Activity;
import android.text.SpannableString;

import com.lht.cloudjob.mvp.model.bean.BannerResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandItemData;
import com.lht.cloudjob.mvp.model.pojo.LoginInfo;
import com.lht.customwidgetlib.banner.AutoLooperBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.viewinterface
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IIndexFragment
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
public interface IIndexFragment extends IAsyncProtectedFragent{

    void showBanner(List<String> bannerImgUrls,
                    AutoLooperBanner.OnBannerItemClickListener onBannerItemClickListener);

    Activity getActivity();

    LoginInfo getLoginInfo();

    void finishRefresh();

    void setRecommendListItems(ArrayList<DemandItemData> liData);

    void addRecommendListItems(ArrayList<DemandItemData> liData);

    void updateGlobalNotify(CharSequence charSequence);

    void jump2RecomInfoActivity(BannerResBean bannerResBean);

    void jump2Subscribe();

    void jump2PublishDemand();
}
