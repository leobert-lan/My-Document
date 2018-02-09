package com.lht.customwidgetlib.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lht.customwidgetlib.R;
import com.lht.customwidgetlib.wheel.OnWheelChangedListener;
import com.lht.customwidgetlib.wheel.WheelView;
import com.lht.customwidgetlib.wheel.adapters.ArrayListWheelTextAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>Package</b> com.lht.customwidgetlib.picker
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LocationPicker
 * <p><b>Description</b>: TODO
 * <p> Create by Leobert on 2016/9/8
 */
public class LocationPicker extends FrameLayout implements OnWheelChangedListener {

    /**
     * 所有省
     */
    protected ArrayList<String> mProvinceDatas = new ArrayList<>();
    /**
     * key - 省 value - 市
     */
    protected Map<String, ArrayList<String>> mCitisDatasMap = new HashMap<>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, ArrayList<String>> mAreasDatasMap = new HashMap<>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentAreaName;


    public LocationPicker(Context context) {
        this(context, null);
    }

    public LocationPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(defStyleAttr);
    }

    private void init(int defStyleAttr) {
        inflate(getContext(), R.layout.view_picker_location, this);
    }

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initEvent();
    }

    private void initView() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    private void initEvent() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/9/8
                Toast.makeText(getContext(), "当前选中:" + mCurrentProviceName + "," +
                        mCurrentCityName + ","
                        + mCurrentAreaName, Toast.LENGTH_SHORT).show();
            }
        });

        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentAreaName = mAreasDatasMap.get(mCurrentCityName).get(newValue);
        }
    }

    public void setDatas(ArrayList<String> mProvinceDatas,
                         Map<String, ArrayList<String>> mCitisDatasMap,
                         Map<String, ArrayList<String>> mAreasDatasMap) {
        this.mProvinceDatas = mProvinceDatas;
        this.mCitisDatasMap = mCitisDatasMap;
        this.mAreasDatasMap = mAreasDatasMap;

        mViewProvince.setViewAdapter(new ArrayListWheelTextAdapter<>(getContext(), mProvinceDatas));
        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        ArrayList<String> areas = null;
        if (mCitisDatasMap.get(mCurrentProviceName) != null) {
            mCurrentCityName =mCitisDatasMap.get(mCurrentProviceName).get(pCurrent);
            areas = mAreasDatasMap.get(mCurrentCityName);
        }

        if (areas == null) {
            areas = new ArrayList<>();
            areas.add("");
        }
        mViewDistrict.setViewAdapter(new ArrayListWheelTextAdapter<>(getContext(), areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas.get(pCurrent);
        ArrayList<String> cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new ArrayList<>();
            cities.add("");
        }
        mViewCity.setViewAdapter(new ArrayListWheelTextAdapter<>(getContext(), cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }


}
