package com.lht.cloudjob.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lht.cloudjob.R;
import com.lht.cloudjob.clazz.LRUCache;
import com.lht.cloudjob.interfaces.keys.DBConfig;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestModel;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.ApiModelCallback;
import com.lht.cloudjob.mvp.model.DbCURDModel;
import com.lht.cloudjob.mvp.model.HotSearchModel;
import com.lht.cloudjob.mvp.model.SearchHistoryModel;
import com.lht.cloudjob.mvp.model.bean.BaseBeanContainer;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.HotSearchResBean;
import com.lht.cloudjob.mvp.viewinterface.ISearchHistoryActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.internet.HttpUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.litesuits.orm.LiteOrm;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.presenter
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> SearchHistoryActivityPresenter
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/1.
 */
public class SearchHistoryActivityPresenter implements DbCURDModel.IDbCURD<SearchHistoryModel>,
        IApiRequestPresenter {

    private ISearchHistoryActivity iSearchHistoryActivity;

    private String username;

    private DbCURDModel<SearchHistoryModel> dbCURDModel;

    private LiteOrm liteOrm;

    private LRUCache<String, String> searchLruCache;

    public SearchHistoryActivityPresenter(ISearchHistoryActivity iSearchHistoryActivity) {
        this.iSearchHistoryActivity = iSearchHistoryActivity;

        username = getUsername();
        liteOrm = LiteOrm.newSingleInstance(iSearchHistoryActivity.getActivity(),
                DBConfig.SearchHistroyDb.DB_NAME);

        searchLruCache = new LRUCache<>(5);

        dbCURDModel = new DbCURDModel<>(this);
    }

    /**
     * 初始化搜索历史
     */
    public void initSearchHistory() {
        dbCURDModel.doRequest();
    }

    public void initHotSearch() {
        IApiRequestModel model = new HotSearchModel(new HotSearchModelCallback());
        model.doRequest(iSearchHistoryActivity.getActivity());
    }

    private void generateSearchHistoryData() {
        ArrayList<String> ret = new ArrayList<>();
        for (Map.Entry<String, String> e : searchLruCache.getAll()) {
            ret.add(e.getValue());
        }
        iSearchHistoryActivity.updateSearchHistory(ret);
    }

    private String getUsername() {
        SharedPreferences preferences = iSearchHistoryActivity.getActivity()
                .getSharedPreferences(SPConstants.Token.SP_TOKEN, Context.MODE_PRIVATE);
        return preferences.getString(SPConstants.Token.KEY_USERNAME, "default");
    }


    @Override
    public SearchHistoryModel doCURDRequest() {
        return liteOrm.queryById(username, SearchHistoryModel.class);
    }

    @Override
    public void onCURDFinish(SearchHistoryModel searchHistoryModel) {
        JSONArray jArray = null;
        if (searchHistoryModel == null || StringUtil.isEmpty(searchHistoryModel.getHistory())) {
            jArray = new JSONArray();
        } else {
            jArray = JSONArray.parseArray(searchHistoryModel.getHistory());
        }
        rebuildSearchLruHistory(jArray);
    }

    private void rebuildSearchLruHistory(JSONArray jArray) {
        int count = jArray.size();
        for (int i = 0; i < count; i++) {
            String temp = jArray.getString(i);//JSON.parseObject(jArray.getString(i), String.class);
            searchLruCache.put(temp, temp);
        }
        generateSearchHistoryData();
    }

    public void callSearch(String key) {
        // TODO: 2016/8/2 null check

        iSearchHistoryActivity.jumpSearchActivity(key);
        searchLruCache.put(key, key);
        generateSearchHistoryData();
    }

    public void callSaveHistory() {
        // TODO: 2016/8/2 异步存储
        DbCURDModel<SearchHistoryModel> model = new DbCURDModel<>(new DbCURDModel.IDbCURD<SearchHistoryModel>() {
            @Override
            public SearchHistoryModel doCURDRequest() {
                SearchHistoryModel date = new SearchHistoryModel();
                date.setUsername(username);
                date.setHistory(searchLruCache.getSortedValues());
                liteOrm.save(date);
                return null;
            }

            @Override
            public void onCURDFinish(SearchHistoryModel searchHistoryModel) {
                //null
            }
        });
        model.doRequest();
    }

    public void callCleanHistory() {
//        删除异步，但是内容直接通过清理cache立即重获
        searchLruCache = new LRUCache<>(5);
        generateSearchHistoryData();
        DbCURDModel<SearchHistoryModel> model = new DbCURDModel<>(new DbCURDModel.IDbCURD<SearchHistoryModel>() {
            @Override
            public SearchHistoryModel doCURDRequest() {
                SearchHistoryModel date = new SearchHistoryModel();
                date.setUsername(username);
//                date.setHistory(searchLruCache.getSortedValues());
//                liteOrm.save(date);
                liteOrm.delete(date);
                return null;
            }

            @Override
            public void onCURDFinish(SearchHistoryModel searchHistoryModel) {
                //null
            }
        });
        model.doRequest();

    }

    @Override
    public void cancelRequestOnFinish(Context context) {
        HttpUtil.getInstance().onActivityDestroy(context);
    }

    private class HotSearchModelCallback implements ApiModelCallback<ArrayList<HotSearchResBean>> {

        @Override
        public void onSuccess(BaseBeanContainer<ArrayList<HotSearchResBean>> beanContainer) {
            iSearchHistoryActivity.updateHotSearch(beanContainer.getData());
        }

        @Override
        public void onFailure(BaseBeanContainer<BaseVsoApiResBean> beanContainer) {

            iSearchHistoryActivity.showErrorMsg(iSearchHistoryActivity.getActivity().getString(R.string.v1010_default_searchh_gethotsearch_success));
        }

        @Override
        public void onHttpFailure(int httpStatus) {

            iSearchHistoryActivity.showErrorMsg(iSearchHistoryActivity.getActivity().getString(R.string.v1010_default_searchh_gethotsearch_success));
        }
    }
}
