package com.lht.cloudjob.activity.asyncprotected;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.adapter.ListAdapter2;
import com.lht.cloudjob.adapter.viewprovider.CommentConfigViewProvrderImpl;
import com.lht.cloudjob.customview.RoundImageView;
import com.lht.cloudjob.customview.TitleBar;
import com.lht.cloudjob.interfaces.keys.SPConstants;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.mvp.model.bean.CommentConfigResBean;
import com.lht.cloudjob.mvp.model.bean.DemandInfoResBean;
import com.lht.cloudjob.mvp.model.pojo.CommentActivityData;
import com.lht.cloudjob.mvp.presenter.CommentActivityPresenter;
import com.lht.cloudjob.mvp.viewinterface.ICommentActivity;
import com.lht.cloudjob.util.debug.DLog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentActivity extends AsyncProtectedActivity implements ICommentActivity, View
        .OnClickListener, ListAdapter2.ICustomizeListItem2<CommentConfigResBean,
        CommentConfigViewProvrderImpl.ViewHolder> {

    private final static String PAGENAME = "CommentActivity";
    //    public final static String KEY_TASK_BN = "task_bn";
    public final static String KEY_DATA = "_data_commentactivitydata";

    private TitleBar titleBar;
    private CommentActivityPresenter presenter;
    private ProgressBar progressBar;
    private RoundImageView ivAvatar;
    private Button btnSubmit;
    private SharedPreferences mTokenPreferences = null;
    private EditText etCommentContet;
    private ListView lvCommentItem;
    private CommentConfigViewProvrderImpl commentItemViewProvrder;
    private ListAdapter2<CommentConfigResBean> listAdapter2;
    HashMap<String, Integer> commentScoreMap = new HashMap<String, Integer>();
    private TextView tvCompanyName;
    private CommentActivityData commentActivityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        String s = getIntent().getStringExtra(KEY_DATA);
        commentActivityData = JSON.parseObject(s, CommentActivityData.class);
        initView();
        initVariable();
        initEvent();
    }

    @Override
    protected String getPageName() {
        return CommentActivity.PAGENAME;
    }

    @Override
    public UMengActivity getActivity() {
        return CommentActivity.this;
    }

    @Override
    protected void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        ivAvatar = (RoundImageView) findViewById(R.id.comment_iv_avatar);
        tvCompanyName = (TextView) findViewById(R.id.comment_tv_commentcompany);
        btnSubmit = (Button) findViewById(R.id.comment_btn_submit);
        etCommentContet = (EditText) findViewById(R.id.comment_et_commentcontent);
        lvCommentItem = (ListView) findViewById(R.id.comment_lv_commentitem);

    }

    @Override
    protected void initVariable() {
        presenter = new CommentActivityPresenter(this);

    }

    @Override
    protected void initEvent() {
        titleBar.setDefaultOnBackListener(this);
        titleBar.setTitle(getString(R.string.v1010_default_comment_commont));

        //获取评价配置项
        presenter.callInitPage(commentActivityData);

        updatePublisher(commentActivityData.getPublisher());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_btn_submit:
                presenter.callSubmit(commentActivityData.getTask_bn(), getCommentContent(), commentScoreMap);
                break;
            default:
                break;
        }
    }

    @Override
    public SharedPreferences getTokenPreferences() {
        if (mTokenPreferences == null) {
            mTokenPreferences = getSharedPreferences(SPConstants.Token.SP_TOKEN, Activity
                    .MODE_PRIVATE);
        }
        return mTokenPreferences;
    }

    @Override
    public void showErrorMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新评论配置项
     *
     * @param data ...
     */
    @Override
    public void updateCommentItemData(ArrayList<CommentConfigResBean> data) {
        for (int i = 0;i<data.size();i++) {
            commentScoreMap.put(data.get(i).getAid(),1);
        }

        commentItemViewProvrder = new CommentConfigViewProvrderImpl(getLayoutInflater(), this,
                commentScoreMap);
        listAdapter2 = new ListAdapter2<>(data, commentItemViewProvrder);
        lvCommentItem.setAdapter(listAdapter2);

        btnSubmit.setOnClickListener(this);
    }

    /**
     * 更新甲方信息，头像、昵称...
     *
     * @param publisher
     */
    @Override
    public void updatePublisher(DemandInfoResBean.Publisher publisher) {
        if (publisher == null) {
            publisher = new DemandInfoResBean.Publisher();
        }
        // TODO: 2016/9/26
        Picasso.with(getActivity()).load(publisher.getAvatar()).diskCache(BaseActivity
                .getLocalImageCache()).placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default).fit().into(ivAvatar);

        tvCompanyName.setText(publisher.getNickname());

    }

    /**
     * 获取评论内容
     */
    public String getCommentContent() {
        return etCommentContet.getText().toString().trim();
    }

    @Override
    public void customize(int position, CommentConfigResBean Data, View convertView,
                            CommentConfigViewProvrderImpl.ViewHolder viewHolder) {

    }
}
