package com.lht.creationspace.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.lht.creationspace.base.fragment.BaseFragment;

import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.adapter </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> FgViewPagerAdapter </p>
 * <p><b>Description:</b> 用于内容为fragment的viewpager适配器 </p>
 * Created by leobert on 2017/3/4.
 */

public class FgViewPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;

    public FgViewPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        // keep in memory,never destroy unless finish
    }

    @Override
    public int getCount() {
        if (fragments != null) {
            return fragments.size();
        }
        return 0;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

}