package com.lht.creationspace.base.domain.repository;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.repository </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> Repository </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public interface Repository<Model> {
    Void saveOrUpdate(final Model model);

    List<Model> queryAll();

    Void deleteAll();

    Void delete(@NonNull final Model model);

}
