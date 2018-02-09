package com.lht.creationspace.structure.objpool;

import java.util.Comparator;

/**
 * <p><b>Package:</b> com.lht.creationspace.interfaces </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> EqualsComparator </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/11.
 */

public interface EqualsComparator<T> extends Comparator<T> {
    int RET_EQUAL = 1;

    int RET_NOT_EQUAL = 0;
}
