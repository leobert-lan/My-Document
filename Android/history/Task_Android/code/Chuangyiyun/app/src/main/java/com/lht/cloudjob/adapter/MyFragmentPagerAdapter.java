package com.lht.cloudjob.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lht.cloudjob.activity.asyncprotected.HomeActivity;
import com.lht.cloudjob.fragment.FgDemand;
import com.lht.cloudjob.fragment.FgMessage;
import com.lht.cloudjob.fragment.FgIndex;

@Deprecated
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 3;
    private FgIndex myFragment1 = null;
    private FgDemand myFragment2 = null;
    private FgMessage myFragment3 = null;


    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        myFragment1 = new FgIndex();
        myFragment2 = new FgDemand();
        myFragment3 = new FgMessage();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case HomeActivity.PAGE_ONE:
                fragment = myFragment1;
                break;
            case HomeActivity.PAGE_TWO:
                fragment = myFragment2;
                break;
            case HomeActivity.PAGE_THREE:
                fragment = myFragment3;
                break;
//            case HomeActivity.PAGE_FOUR:
//                fragment = myFragment4;
//                break;
        }
        return fragment;
    }


}

