package com.lht.cloudjob;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.activity.UMengActivity;
import com.lht.cloudjob.activity.asyncprotected.AsyncProtectedActivity;
import com.lht.cloudjob.activity.others.SplashActivity;
import com.lht.cloudjob.customview.CustomProgressView;
import com.lht.cloudjob.interfaces.net.IApiRequestPresenter;
import com.lht.cloudjob.test.TestVideoActivity;

import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.file.FileUtils;
import com.lht.cloudjob.util.time.TimeUtil;

import java.io.File;
import java.util.Locale;


public class TestActivity extends AsyncProtectedActivity implements View.OnClickListener {

    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ll = (LinearLayout) findViewById(R.id.test_ll);

        setOnClick2Child(ll);
    }

    @Override
    protected IApiRequestPresenter getApiRequestPresenter() {
        return null;
    }

    @Override
    protected String getPageName() {
        return "TestActivity";
    }

    @Override
    public UMengActivity getActivity() {
        return TestActivity.this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initEvent() {

    }

    /**
     * @param viewGroup
     */
    private void setOnClick2Child(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setOnClick2Child((ViewGroup) viewGroup.getChildAt(i));
            }
            //全部添加上
            viewGroup.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.test_main:
                start(SplashActivity.class);
                break;
            case R.id.test_image_preview:
                testInstall();
                break;
            case R.id.test_file_preview:
                break;
            case R.id.test_bwv:
//                start(TestBridgeWebviewActivity.class);
                break;
            case R.id.test_video:
                start(TestVideoActivity.class);
                break;
            case R.id.test_pb:
//                testDateSelect();
                testPB();
                break;
            case R.id.test_promote:
                break;
            default:
                break;
        }
    }

    private void testPB() {

        final String format = getString(R.string.v1020_dialog_preview_onmobile_toolarge);
        String s = String.format(Locale.ENGLISH, format, FileUtils.calcSize(2000));
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        CustomProgressView customProgressView = new CustomProgressView(this);
        customProgressView.setProgress(10, 100);
        customProgressView.show();
    }

    private void testDateSelect() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DLog.e(getClass(), "check date:" + String.format("%d-%d-%d", year, monthOfYear + 1,
                        dayOfMonth));
            }
        };

        DatePickerDialog dialog = TimeUtil.newDatePickerDialog(this, TimeUtil
                .getCurrentTimeInLong(), listener);
        dialog.show();
    }

    private void testInstall() {
        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/demo.apk");
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
        MainApplication.getOurInstance().finishAll();
    }


    @Override
    public ProgressBar getProgressBar() {
        return null;
    }
}
