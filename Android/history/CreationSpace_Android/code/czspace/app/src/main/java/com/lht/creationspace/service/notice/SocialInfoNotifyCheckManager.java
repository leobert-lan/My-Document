package com.lht.creationspace.service.notice;

import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.model.apimodel.RestfulApiModelCallback;
import com.lht.creationspace.module.home.model.QuerySocialInfoModel;

/**
 * <p><b>Package:</b> com.lht.creationspace.service.notice </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> UnreadCollectionNotifyCheckManager </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/8.
 */

public class SocialInfoNotifyCheckManager
        extends NotifyCheckManager<QuerySocialInfoModel.ModelResBean> {
    private SocialInfoNotifyCheckManager() {
        super(new Checker(new Callback()));
    }

    private static SocialInfoNotifyCheckManager instance;

    public static SocialInfoNotifyCheckManager getInstance() {
        if (instance == null) {
            instance = new SocialInfoNotifyCheckManager();
        }
        return instance;
    }

    private static class Callback
            implements RestfulApiModelCallback<QuerySocialInfoModel.ModelResBean> {

        @Override
        public void onSuccess(QuerySocialInfoModel.ModelResBean bean) {
            handle(bean);
        }

        @Override
        public void onFailure(int restCode, String msg) {
            handle(new QuerySocialInfoModel.ModelResBean());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            handle(new QuerySocialInfoModel.ModelResBean());
        }

        private void handle(QuerySocialInfoModel.ModelResBean b) {
            SocialInfoNotifyCheckManager.getInstance().onCheckFinish();
            SocialInfoNotifyCheckManager.getInstance().notifyListener(b);
        }
    }




    private static class Checker implements UnreadNotifyChecker {
        private RestfulApiModelCallback<QuerySocialInfoModel.ModelResBean> callback;

        public Checker(RestfulApiModelCallback<QuerySocialInfoModel.ModelResBean> callback) {
            this.callback = callback;
        }

        @Override
        public void check() {
            if (IVerifyHolder.mLoginInfo.isLogin()) {
                QuerySocialInfoModel model =
                        new QuerySocialInfoModel(IVerifyHolder.mLoginInfo.getUsername(),
                                callback);
                model.doRequest(MainApplication.getOurInstance());
            } else {
                callback.onSuccess(new QuerySocialInfoModel.ModelResBean());
            }
        }
    }
}
