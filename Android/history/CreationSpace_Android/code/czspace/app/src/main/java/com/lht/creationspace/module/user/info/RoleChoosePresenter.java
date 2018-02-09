package com.lht.creationspace.module.user.info;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lht.creationspace.R;
import com.lht.creationspace.base.model.apimodel.IApiRequestModel;
import com.lht.creationspace.base.presenter.IApiRequestPresenter;
import com.lht.creationspace.base.model.apimodel.ApiModelCallback;
import com.lht.creationspace.module.user.register.model.RoleChooseModel;
import com.lht.creationspace.base.model.apimodel.BaseBeanContainer;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.user.register.ui.IRoleChooseActivity;
import com.lht.creationspace.util.internet.HttpUtil;

import java.util.HashSet;

/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.presenter
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> RoleChoosePresenter
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/17.
 */

public class RoleChoosePresenter implements IApiRequestPresenter {
    private final IRoleChooseActivity iRoleChooseActivity;
    private final HashSet<Integer> roles = new HashSet<>();

    public RoleChoosePresenter(IRoleChooseActivity iRoleChooseActivity) {
        this.iRoleChooseActivity = iRoleChooseActivity;
    }

    public void bindRoleSelectEvent(CheckBox... checkBoxes) {
        for (CheckBox cb : checkBoxes) {
            cb.setOnCheckedChangeListener(roleChangeListener);
        }
    }

    public void callBindRole(int source, String user) {
        if (roles.isEmpty()) {
            iRoleChooseActivity.showMsg(iRoleChooseActivity.getAppResource()
                    .getString(R.string.v1000_default_rolechoose_null_role));
            return;
        }
        RoleChooseModel.RoleChooseData data = new RoleChooseModel.RoleChooseData();
        data.setUsr(user);
        data.setSource(source);
        data.setRoles(roles);
        iRoleChooseActivity.showWaitView(true);
        IApiRequestModel model = new RoleChooseModel(data, new RolesChooseCallback());
        model.doRequest(iRoleChooseActivity.getActivity());
    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private CompoundButton.OnCheckedChangeListener roleChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                Integer tag = (Integer) buttonView.getTag();
                if (isChecked) {
                    roles.add(tag);
                } else {
                    roles.remove(tag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private final class RolesChooseCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRoleChooseActivity.cancelWaitView();
            iRoleChooseActivity.showMsg("设置身份成功");
            iRoleChooseActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iRoleChooseActivity.cancelWaitView();
            iRoleChooseActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iRoleChooseActivity.cancelWaitView();
        }
    }
}
