package com.lht.creationspace.module.home.ui;

import android.app.Activity;

import com.lht.creationspace.customview.share.ThirdPartySharePopWins;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;

/**
 * Created by chhyu on 2017/2/23.
 */

public interface IFgHomeMine{
    Activity getActivity();

    void showSharePopwins(ThirdPartySharePopWins.ShareData shareData);

    void updateSocialInfo(QuerySocialInfoModel.ModelResBean bean);
}
