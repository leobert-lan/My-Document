package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.LocationsModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.LocationsResBean;
import com.lht.cloudjob.mvp.model.pojo.ChildEntity;
import com.lht.cloudjob.mvp.model.pojo.ParentEntity;
import com.lht.cloudjob.mvp.viewinterface.ILocationPickerActivity;
import com.lht.cloudjob.util.internet.HttpUtil;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LocationPickerActivityPresenter
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/9
 */
public class LocationPickerActivityPresenter implements IApiRequestPresenter {

    private ILocationPickerActivity iLocationPickerActivity;

    public LocationPickerActivityPresenter(ILocationPickerActivity iLocationPickerActivity) {
        this.iLocationPickerActivity = iLocationPickerActivity;
    }

    public void callGetLocationData() {
        iLocationPickerActivity.showWaitView(true);
        IApiRequestModel model = new LocationsModel(new LocationsModelCallback());
        model.doRequest(iLocationPickerActivity.getActivity());
    }

    /**
     * 页面结束时取消所有相关的回调
     *
     * @param context
     */
    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private class LocationsModelCallback implements ApiModelCallback<ArrayList<LocationsResBean
            .Province>> {


        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<LocationsResBean.Province>>
                                      beanContainer) {
            ArrayList<LocationsResBean.Province> beans = beanContainer.getData();


            ArrayList<ParentEntity> parents = new ArrayList<>();

            for (LocationsResBean.Province province : beans) {
                ParentEntity parent = new ParentEntity();
                parent.setGroupName(province.getP());

                ArrayList<ChildEntity> children = new ArrayList<>();

                ArrayList<LocationsResBean.City> cities = province.getC();
                if (cities != null) {

                    for (LocationsResBean.City city : cities) {
                        ChildEntity child = new ChildEntity();
                        child.setGroupName(city.getN());
                        ArrayList<String> childNames = new ArrayList<>();

                        ArrayList<LocationsResBean.Area> areas = city.getA();

                        if (areas != null) {
                            for (LocationsResBean.Area area : areas) {
                                childNames.add(area.getS());
                            }
                        }
                        child.setChildNames(childNames);
                        children.add(child);
                    }
                }
                parent.setChilds(children);
                parents.add(parent);
            }
            iLocationPickerActivity.updateList(parents);
            iLocationPickerActivity.cancelWaitView();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iLocationPickerActivity.showMsg(beanContainer.getData().getMessage());
            iLocationPickerActivity.cancelWaitView();

        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iLocationPickerActivity.cancelWaitView();
        }
    }
}
