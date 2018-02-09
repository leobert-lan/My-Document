package com.lht.creationspace.customview.tab;

import android.view.View;
import android.widget.CompoundButton;

import com.lht.creationspace.util.debug.DLog;

import java.util.HashMap;

/**
 * <p><b>Package</b> com.lht.vsocyy.clazz
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> TabManager
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/9.
 */

public class TabManager {

    public static void init(OnTabSelectedListener onTabSelectedListener, CompoundButton... compoundButtons) {
        HashMap<CompoundButton, CompoundButton.OnCheckedChangeListener> map = new HashMap<>();

        Listener listener = new Listener(onTabSelectedListener);

        for (CompoundButton rb : compoundButtons) {
            map.put(rb, listener);
        }
        View.OnClickListener listener2 = new OnCheckedChangeListenerImpl(map);
        for (CompoundButton rb : compoundButtons) {
            rb.setOnClickListener(listener2);
        }
    }

    private static final class Listener implements CompoundButton.OnCheckedChangeListener {

        private final OnTabSelectedListener onTabSelectedListener;

        Listener(OnTabSelectedListener onTabSelectedListener) {
            this.onTabSelectedListener = onTabSelectedListener;
            if (onTabSelectedListener == null)
                DLog.e(TabManager.class,"onTabSelectedListener is null");
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                return;
            }
            if (onTabSelectedListener == null) {
                DLog.e(TabManager.class,"onTabSelectedListener is null");
                return;
            }
            onTabSelectedListener.onTabSelect(buttonView);
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelect(CompoundButton selectedTab);
    }
}
