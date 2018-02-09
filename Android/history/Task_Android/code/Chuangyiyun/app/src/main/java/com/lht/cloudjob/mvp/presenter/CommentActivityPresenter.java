package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.interfaces.umeng.IUmengEventKey;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.CommentConfigModel;
import com.lht.cloudjob.mvp.model.CommentModel;
import com.lht.cloudjob.mvp.model.DemandInfoModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CommentConfigResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.CommentActivityData;
import com.lht.cloudjob.mvp.viewinterface.ICommentActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.string.StringUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CommentActivityPresenter
 * <p><b>Description</b>: TODO
 * <p>Created by Administrator on 2016/9/8.
 */

public class CommentActivityPresenter implements IApiRequestPresenter {

    private final ICommentActivity iCommentActivity;
    private final String username;
    private ArrayList<CommentConfigResBean> dataList;

    public CommentActivityPresenter(ICommentActivity iCommentActivity) {
        this.iCommentActivity = iCommentActivity;
        SharedPreferences tokens = iCommentActivity.getTokenPreferences();
        username = tokens.getString(SPConstants.Token.KEY_USERNAME, "");

    }


    @Override
    public void cancelRequestOnFinish(Context context) {

    }

    /**
     * 提交
     */
    public void callSubmit(String task_bn, String content, HashMap<String, Integer> map) {
        if (map.size() < dataList.size()) {
            iCommentActivity.showMsg(iCommentActivity.getActivity().getString(R.string.v1010_default_comment_star_score));
            return;
        }
        isComplete(task_bn, R.string.v1010_default_undertake_task_bn_isempty);
        isComplete(content, R.string.v1010_default_comment_content_isempty);

        //统计 提交评价-计数
        iCommentActivity.reportCountEvent(IUmengEventKey.KEY_TASK_COMMENT);
        CommentModel model = new CommentModel(task_bn, username, content, map, new CommentModelCallback());
        model.doRequest(iCommentActivity.getActivity());
    }

    private final class CommentModelCallback implements ApiModelCallback<BaseVsoApiResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCommentActivity.cancelWaitView();
            iCommentActivity.getActivity().finish();
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCommentActivity.cancelWaitView();
            iCommentActivity.showErrorMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCommentActivity.cancelWaitView();
        }
    }

//    /**
//     * 遍历结集合
//     *
//     * @param map 要遍历的集合
//     */
//    private void ergodicMap(HashMap<String, Integer> map) {
//        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            String key = (String) entry.getKey();
//            Integer value = (Integer) entry.getValue();
//            DLog.e(getClass(), key + "====" + value);
//        }
//    }


    /**
     * 获取评价配置项
     *
     * @param task_bn  需求编号
     * @param username 用户名
     */
    private void doGetCommentConfig(String task_bn, String username) {
        if (StringUtil.isEmpty(task_bn)) {
            iCommentActivity.showErrorMsg(iCommentActivity.getActivity().getString(R.string.v1010_default_comment_task_bn_isEmpty));
            return;
        }
        if (StringUtil.isEmpty(username)) {
            iCommentActivity.showErrorMsg(iCommentActivity.getActivity().getString(R.string.v1010_default_comment_username_isEmpty));
            return;
        }
        CommentConfigModel model = new CommentConfigModel(task_bn, username, new CommentConfigModelCallback());
        model.doRequest(iCommentActivity.getActivity());
    }

    private final class CommentConfigModelCallback implements ApiModelCallback<ArrayList<CommentConfigResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<CommentConfigResBean>> beanContainer) {
            dataList = beanContainer.getData();
            if (dataList == null) {
                iCommentActivity.updateCommentItemData(new ArrayList<CommentConfigResBean>());

            } else {
                iCommentActivity.updateCommentItemData(dataList);
            }
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCommentActivity.cancelWaitView();
            iCommentActivity.showMsg(beanContainer.getData().getMessage());
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCommentActivity.cancelWaitView();
            // TODO: 2016/9/23
        }
    }

    /**
     * 判空
     *
     * @param s         数据
     * @param toastText toast 内容
     * @return
     */
    private boolean isComplete(String s, int toastText) {
        if (StringUtil.isEmpty(s)) {
            iCommentActivity.showMsg(iCommentActivity.getActivity().getString(toastText));
            return false;
        }
        return true;
    }

    public void callInitPage(CommentActivityData data) {
        if (data.isNeedQueryInfo()) {
            //请求demandInfo 信息

            IApiRequestModel model = new DemandInfoModel(data.getUsername(), data.getTask_bn(), new GetDemandInfoModelCallback());
            model.doRequest(iCommentActivity.getActivity());
        } else {

            doGetCommentConfig(data.getTask_bn(), data.getUsername());
        }
    }

    /**
     * 承接需求列表获取回调实现类
     */
    private class GetDemandInfoModelCallback implements ApiModelCallback<DemandInfoResBean> {

        @Override
        public void onSuccess(BaseBeanContainer<DemandInfoResBean> beanContainer) {
            iCommentActivity.cancelWaitView();
            DemandInfoResBean data = beanContainer.getData();
            iCommentActivity.updatePublisher(data.getPublisher());
            doGetCommentConfig(data.getTask_bn(), username);
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {
            iCommentActivity.showErrorMsg(beanContainer.getData().getMessage());
            iCommentActivity.cancelWaitView();
        }

        @Override
        public void onHttpFailure(int httpStatus) {
            iCommentActivity.cancelWaitView();
        }
    }
}
