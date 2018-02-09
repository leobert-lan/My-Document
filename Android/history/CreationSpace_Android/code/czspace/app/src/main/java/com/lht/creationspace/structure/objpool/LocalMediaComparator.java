package com.lht.creationspace.structure.objpool;

import com.yalantis.ucrop.entity.LocalMedia;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LocalMediaComparator </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/11.
 */

public class LocalMediaComparator implements EqualsComparator<LocalMedia> {
    private static LocalMediaComparator comparator;

    private LocalMediaComparator() {

    }

    public static LocalMediaComparator getInstance() {
        if (comparator == null) {
            comparator = new LocalMediaComparator();
        }
        return comparator;
    }

    @Override
    public int compare(LocalMedia item1, LocalMedia item2) {
        if (item1 == null || item2 == null)
            return RET_NOT_EQUAL;
        if (item1 == item2)
            return RET_EQUAL;
        if (item1.equals(item2))
            return RET_EQUAL;
        if (item1.getPath().equals(item2.getPath()))
            return RET_EQUAL;
        return RET_NOT_EQUAL;
    }
}
