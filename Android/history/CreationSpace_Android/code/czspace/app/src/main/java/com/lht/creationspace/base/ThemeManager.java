package com.lht.creationspace.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.lht.creationspace.cfg.SPConstants;
import com.lht.creationspace.util.SPUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;


public class ThemeManager {

    private static ThemeManager self;
    private WeakReference<Context> mContextWeakReference;
    private ArrayList<WeakReference<IThemeListener>> iThemeListeners;

    public static int BACKGROUNDS[] = {
            Color.rgb(255,255,255)//,//默认白色
//            Color.rgb(1, 199, 123)//主色
    };

    private ThemeManager(Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    public static ThemeManager with(Context ctx) {
        if (self == null) {
            self = new ThemeManager(ctx);
        } else {
            self.mContextWeakReference = new WeakReference<>(ctx);
        }
        return self;
    }

    private SharedPreferences sp;

    public int getCurrentColor() {
        if (sp == null) {
            Context context = mContextWeakReference.get();
            if (context != null) {
                sp = context.getSharedPreferences(SPConstants.Basic.SP_NAME, Context.MODE_PRIVATE);
            } else {
                return BACKGROUNDS[0];
            }
        }
        return sp.getInt(SPConstants.Basic.KEY_THEME_COLOR, BACKGROUNDS[0]);
    }

    public void saveColor(int index) {
        if (sp == null) {
            Context context = mContextWeakReference.get();
            if (context != null) {
                sp = context.getSharedPreferences(SPConstants.Basic.SP_NAME, Context.MODE_PRIVATE);
            } else {
                notifyThemeChange();
                return; // ignore save
            }
        }
        SPUtil.modifyInt(sp, SPConstants.Basic.KEY_THEME_COLOR, BACKGROUNDS[index]);
        notifyThemeChange();
    }

    public void registerListener(IThemeListener listener) {
        if (iThemeListeners == null) {
            iThemeListeners = new ArrayList<>();
        }
        WeakReference<IThemeListener> listenerWeakReference = new WeakReference<>(listener);
        iThemeListeners.add(listenerWeakReference);
    }

    public void notifyThemeChange() {
        if (iThemeListeners == null) return;
        int curColor = getCurrentColor();
        Iterator<WeakReference<IThemeListener>> iterator = iThemeListeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<IThemeListener> next = iterator.next();
            if (next == null) {
                iterator.remove();
            } else {
                IThemeListener listener = next.get();
                if (listener == null) {
                    iterator.remove();
                } else {
                    listener.onThemeChange(curColor);
                }
            }
        }
    }

    public interface IThemeListener {
        void onThemeChange(int color);
    }

}
