package com.lht.creationspace.service.notice;

import java.util.Observable;
import java.util.Observer;

/**
 * <p><b>Package:</b> com.lht.creationspace.service.notice </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> OnNoticeCheckedListener </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/8.
 */

public abstract class OnNoticeCheckedListener<T>  implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        onNoticeChecked((T) arg);
    }

    public abstract void onNoticeChecked(T arg);
}
